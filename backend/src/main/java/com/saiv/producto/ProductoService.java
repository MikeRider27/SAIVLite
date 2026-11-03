package com.saiv.producto;

import com.saiv.common.ApiException;
import com.saiv.common.PageResponse;
import com.saiv.producto.ProductoDtos.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final CategoriaRepository categorias;
    private final MovimientoStockRepository movimientos;

    public ProductoService(ProductoRepository repo, CategoriaRepository categorias, MovimientoStockRepository movimientos) {
        this.repo = repo;
        this.categorias = categorias;
        this.movimientos = movimientos;
    }

    private static final Map<String, String> ORDEN = Map.of(
            "codigo", "codigo", "descripcion", "descripcion", "existencias", "existencias",
            "precioVenta", "precioVenta", "actualizadoEn", "actualizadoEn");

    @Transactional(readOnly = true)
    public PageResponse<ProductoDto> buscar(String q, Long categoriaId, boolean soloActivos, boolean bajoStock,
                                            int page, int size, String sort, boolean desc) {
        String like = (q == null || q.isBlank()) ? "" : "%" + q.trim().toLowerCase() + "%";
        String campo = ORDEN.getOrDefault(sort, "descripcion");
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200),
                desc ? Sort.by(campo).descending() : Sort.by(campo).ascending());
        return PageResponse.of(repo.buscar(like, categoriaId == null ? 0L : categoriaId, soloActivos, bajoStock, pageable),
                ProductoDto::of);
    }

    @Transactional(readOnly = true)
    public ProductoDto porId(Long id) {
        return ProductoDto.of(get(id));
    }

    @Transactional(readOnly = true)
    public ProductoDto porCodigo(String codigo) {
        return repo.findByCodigoIgnoreCase(codigo.trim())
                .map(ProductoDto::of)
                .orElseThrow(() -> ApiException.notFound("Producto con código " + codigo));
    }

    Producto get(Long id) {
        return repo.findById(id).orElseThrow(() -> ApiException.notFound("Producto"));
    }

    @Transactional
    public ProductoDto crear(ProductoRequest req, Long usuarioId) {
        if (repo.existsByCodigoIgnoreCase(req.codigo().trim())) {
            throw ApiException.conflict("Ya existe un producto con el código " + req.codigo());
        }
        Producto p = new Producto();
        aplicar(p, req);
        BigDecimal inicial = req.existencias() == null ? BigDecimal.ZERO : req.existencias();
        p.setExistencias(inicial);
        p = repo.save(p);
        if (inicial.signum() != 0) {
            registrarMovimiento(p, MovimientoStock.Tipo.ENTRADA, inicial, "Stock inicial", usuarioId);
        }
        return ProductoDto.of(p);
    }

    @Transactional
    public ProductoDto actualizar(Long id, ProductoRequest req) {
        Producto p = get(id);
        repo.findByCodigoIgnoreCase(req.codigo().trim())
                .filter(o -> !o.getId().equals(id))
                .ifPresent(o -> { throw ApiException.conflict("Ya existe un producto con el código " + req.codigo()); });
        aplicar(p, req); // las existencias solo cambian con ajustes, ventas o devoluciones
        return ProductoDto.of(p);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto p = get(id);
        repo.delete(p); // los ítems de venta conservan código/descripción (FK SET NULL)
    }

    @Transactional
    public ProductoDto ajustar(Long id, AjusteStockRequest req, Long usuarioId) {
        Producto p = repo.bloquear(id).orElseThrow(() -> ApiException.notFound("Producto"));
        BigDecimal delta = switch (req.tipo()) {
            case ENTRADA -> req.cantidad();
            case SALIDA -> req.cantidad().negate();
            case FIJAR -> req.cantidad().subtract(p.getExistencias());
        };
        if (p.getExistencias().add(delta).signum() < 0) {
            throw ApiException.badRequest("El stock no puede quedar negativo");
        }
        p.setExistencias(p.getExistencias().add(delta));
        MovimientoStock.Tipo tipo = switch (req.tipo()) {
            case ENTRADA -> MovimientoStock.Tipo.ENTRADA;
            case SALIDA -> MovimientoStock.Tipo.SALIDA;
            case FIJAR -> MovimientoStock.Tipo.AJUSTE;
        };
        registrarMovimiento(p, tipo, delta, req.motivo().trim(), usuarioId);
        return ProductoDto.of(p);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> movimientos(Long id) {
        get(id);
        return movimientos.findTop50ByProductoIdOrderByFechaHoraDescIdDesc(id).stream().map(MovimientoDto::of).toList();
    }

    public void registrarMovimiento(Producto p, MovimientoStock.Tipo tipo, BigDecimal delta, String motivo, Long usuarioId) {
        MovimientoStock m = new MovimientoStock();
        m.setProductoId(p.getId());
        m.setTipo(tipo);
        m.setCantidad(delta);
        m.setSaldo(p.getExistencias());
        m.setMotivo(motivo);
        m.setUsuarioId(usuarioId);
        movimientos.save(m);
    }

    private void aplicar(Producto p, ProductoRequest req) {
        p.setCodigo(req.codigo().trim());
        p.setDescripcion(req.descripcion().trim());
        p.setCategoria(req.categoriaId() == null ? null
                : categorias.findById(req.categoriaId()).orElseThrow(() -> ApiException.notFound("Categoría")));
        p.setPrecioCompra(req.precioCompra());
        p.setPrecioVenta(req.precioVenta());
        p.setStockMinimo(req.stockMinimo());
        p.setUnidad(req.unidad());
        p.setUbicacion(req.ubicacion() == null || req.ubicacion().isBlank() ? null : req.ubicacion().trim());
        p.setControlaInventario(req.controlaInventario());
        p.setActivo(req.activo());
    }

    // ---------------------------------------------------------------- CSV

    static final String CSV_HEADER = "codigo;descripcion;categoria;precio_compra;precio_venta;existencias;stock_minimo;unidad;ubicacion;controla_inventario;activo";

    @Transactional(readOnly = true)
    public String exportarCsv() {
        StringBuilder sb = new StringBuilder("﻿").append(CSV_HEADER).append('\n');
        for (Producto p : repo.todosConCategoria()) {
            sb.append(String.join(";",
                    csv(p.getCodigo()), csv(p.getDescripcion()),
                    csv(p.getCategoria() == null ? "" : p.getCategoria().getNombre()),
                    p.getPrecioCompra().toPlainString(), p.getPrecioVenta().toPlainString(),
                    p.getExistencias().stripTrailingZeros().toPlainString(),
                    p.getStockMinimo().stripTrailingZeros().toPlainString(),
                    p.getUnidad().name(), csv(p.getUbicacion() == null ? "" : p.getUbicacion()),
                    p.isControlaInventario() ? "si" : "no", p.isActivo() ? "si" : "no")).append('\n');
        }
        return sb.toString();
    }

    private static String csv(String v) {
        if (v.contains(";") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    /** Importa productos desde CSV (separador ';'). Crea o actualiza por código. */
    @Transactional
    public ImportResult importarCsv(InputStream in, Long usuarioId) throws IOException {
        int creados = 0, actualizados = 0;
        List<String> errores = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String linea = r.readLine();
            if (linea == null) throw ApiException.badRequest("El archivo está vacío");
            int n = 1;
            while ((linea = r.readLine()) != null) {
                n++;
                if (linea.isBlank()) continue;
                try {
                    List<String> c = parseCsv(linea);
                    if (c.size() < 5) throw new IllegalArgumentException("se esperaban al menos 5 columnas");
                    String codigo = c.get(0).trim();
                    if (codigo.isEmpty()) throw new IllegalArgumentException("código vacío");
                    var existente = repo.findByCodigoIgnoreCase(codigo);
                    Producto p = existente.orElseGet(Producto::new);
                    p.setCodigo(codigo);
                    p.setDescripcion(req(c.get(1), "descripción"));
                    String cat = c.get(2).trim();
                    p.setCategoria(cat.isEmpty() ? null : categorias.findByNombreIgnoreCase(cat).orElseGet(() -> {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(cat);
                        return categorias.save(nueva);
                    }));
                    p.setPrecioCompra(num(c.get(3)));
                    p.setPrecioVenta(num(c.get(4)));
                    BigDecimal stock = c.size() > 5 && !c.get(5).isBlank() ? num(c.get(5)) : null;
                    if (c.size() > 6 && !c.get(6).isBlank()) p.setStockMinimo(num(c.get(6)));
                    if (c.size() > 7 && !c.get(7).isBlank()) p.setUnidad(Unidad.valueOf(c.get(7).trim().toUpperCase()));
                    if (c.size() > 8) p.setUbicacion(c.get(8).isBlank() ? null : c.get(8).trim());
                    if (c.size() > 9 && !c.get(9).isBlank()) p.setControlaInventario(bool(c.get(9)));
                    if (c.size() > 10 && !c.get(10).isBlank()) p.setActivo(bool(c.get(10)));
                    p = repo.save(p);
                    if (stock != null && stock.compareTo(p.getExistencias()) != 0) {
                        BigDecimal delta = stock.subtract(p.getExistencias());
                        p.setExistencias(stock);
                        registrarMovimiento(p, MovimientoStock.Tipo.AJUSTE, delta, "Importación CSV", usuarioId);
                    }
                    if (existente.isPresent()) actualizados++; else creados++;
                } catch (Exception e) {
                    errores.add("Línea " + n + ": " + e.getMessage());
                }
            }
        }
        return new ImportResult(creados, actualizados, errores);
    }

    private static String req(String v, String campo) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(campo + " vacía");
        return v.trim();
    }

    private static BigDecimal num(String v) {
        BigDecimal b = new BigDecimal(v.trim().replace(",", "."));
        if (b.signum() < 0) throw new IllegalArgumentException("valor negativo: " + v);
        return b;
    }

    private static boolean bool(String v) {
        String s = v.trim().toLowerCase();
        return s.equals("si") || s.equals("sí") || s.equals("true") || s.equals("1") || s.equals("s");
    }

    static List<String> parseCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        String l = line.startsWith("﻿") ? line.substring(1) : line;
        for (int i = 0; i < l.length(); i++) {
            char ch = l.charAt(i);
            if (quoted) {
                if (ch == '"' && i + 1 < l.length() && l.charAt(i + 1) == '"') { cur.append('"'); i++; }
                else if (ch == '"') quoted = false;
                else cur.append(ch);
            } else if (ch == '"') quoted = true;
            else if (ch == ';') { out.add(cur.toString()); cur.setLength(0); }
            else cur.append(ch);
        }
        out.add(cur.toString());
        return out;
    }
}
