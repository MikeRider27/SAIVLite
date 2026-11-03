package com.saiv.venta;

import com.saiv.caja.SesionCaja;
import com.saiv.caja.SesionCajaRepository;
import com.saiv.common.ApiException;
import com.saiv.common.Fechas;
import com.saiv.common.PageResponse;
import com.saiv.producto.MovimientoStock;
import com.saiv.producto.Producto;
import com.saiv.producto.ProductoRepository;
import com.saiv.producto.ProductoService;
import com.saiv.producto.Unidad;
import com.saiv.usuario.UsuarioRepository;
import com.saiv.venta.VentaDtos.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private final VentaRepository ventas;
    private final DevolucionRepository devoluciones;
    private final ProductoRepository productos;
    private final ProductoService productoService;
    private final SesionCajaRepository sesiones;
    private final UsuarioRepository usuarios;

    public VentaService(VentaRepository ventas, DevolucionRepository devoluciones, ProductoRepository productos,
                        ProductoService productoService, SesionCajaRepository sesiones, UsuarioRepository usuarios) {
        this.ventas = ventas;
        this.devoluciones = devoluciones;
        this.productos = productos;
        this.productoService = productoService;
        this.sesiones = sesiones;
        this.usuarios = usuarios;
    }

    @Transactional
    public VentaDto registrar(VentaRequest req, Long usuarioId) {
        SesionCaja sesion = sesiones.findByUsuarioIdAndEstado(usuarioId, SesionCaja.Estado.ABIERTA)
                .orElseThrow(() -> ApiException.conflict("Debe abrir caja antes de vender"));

        // Agrupa líneas repetidas del mismo producto
        Map<Long, BigDecimal> cantidades = new LinkedHashMap<>();
        req.items().forEach(i -> cantidades.merge(i.productoId(), i.cantidad(), BigDecimal::add));

        Map<Long, Producto> porId = productos.bloquear(cantidades.keySet()).stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));

        Venta v = new Venta();
        v.setUsuario(usuarios.getReferenceById(usuarioId));
        v.setSesionCaja(sesion);
        v.setMetodoPago(req.metodoPago());
        BigDecimal total = BigDecimal.ZERO;

        for (var e : cantidades.entrySet()) {
            Producto p = porId.get(e.getKey());
            if (p == null) throw ApiException.notFound("Producto #" + e.getKey());
            if (!p.isActivo()) throw ApiException.conflict("El producto " + p.getDescripcion() + " está inactivo");
            BigDecimal cant = e.getValue();
            if (p.getUnidad() == Unidad.UNIDAD && cant.stripTrailingZeros().scale() > 0) {
                throw ApiException.badRequest("El producto " + p.getDescripcion() + " se vende por unidad entera");
            }
            if (p.isControlaInventario()) {
                if (p.getExistencias().compareTo(cant) < 0) {
                    throw ApiException.conflict("Stock insuficiente de " + p.getDescripcion()
                            + " (disponible: " + p.getExistencias().stripTrailingZeros().toPlainString() + ")");
                }
                p.setExistencias(p.getExistencias().subtract(cant));
            }
            VentaItem it = new VentaItem();
            it.setVenta(v);
            it.setProductoId(p.getId());
            it.setCodigo(p.getCodigo());
            it.setDescripcion(p.getDescripcion());
            it.setCategoria(p.getCategoria() == null ? null : p.getCategoria().getNombre());
            it.setCantidad(cant);
            it.setPrecioUnitario(p.getPrecioVenta());
            it.setCostoUnitario(p.getPrecioCompra());
            it.setImporte(p.getPrecioVenta().multiply(cant).setScale(2, RoundingMode.HALF_UP));
            v.getItems().add(it);
            total = total.add(it.getImporte());
        }

        if (req.metodoPago() == Venta.MetodoPago.EFECTIVO && req.pagado().compareTo(total) < 0) {
            throw ApiException.badRequest("El monto recibido es menor al total");
        }
        BigDecimal pagado = req.metodoPago() == Venta.MetodoPago.EFECTIVO ? req.pagado() : total;
        v.setTotal(total);
        v.setPagado(pagado);
        v.setCambio(pagado.subtract(total));
        v = ventas.save(v);

        for (VentaItem it : v.getItems()) {
            Producto p = porId.get(it.getProductoId());
            if (p.isControlaInventario()) {
                productoService.registrarMovimiento(p, MovimientoStock.Tipo.VENTA, it.getCantidad().negate(),
                        "Venta #" + v.getId(), usuarioId);
            }
        }
        return detalle(v.getId());
    }

    @Transactional(readOnly = true)
    public VentaDto detalle(Long id) {
        Venta v = ventas.detalle(id).orElseThrow(() -> ApiException.notFound("Venta"));
        List<DevolucionDto> devs = devoluciones.deVenta(id).stream().map(DevolucionDto::of).toList();
        BigDecimal devuelto = devs.stream().map(DevolucionDto::importe).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new VentaDto(v.getId(), v.getFechaHora(), v.getUsuario().getId(), v.getUsuario().getNombre(),
                v.getMetodoPago(), v.getTotal(), v.getPagado(), v.getCambio(), devuelto,
                v.getItems().stream().map(ItemDto::of).toList(), devs);
    }

    @Transactional(readOnly = true)
    public PageResponse<VentaResumenDto> buscar(LocalDate desde, LocalDate hasta, Long usuarioId, int page, int size) {
        LocalDateTime d = Fechas.inicio(desde);
        LocalDateTime h = Fechas.fin(hasta);
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200), Sort.by("fechaHora").descending());
        return PageResponse.of(ventas.buscar(d, h, usuarioId == null ? 0L : usuarioId, pageable), VentaResumenDto::of);
    }

    @Transactional
    public VentaDto devolver(Long ventaId, DevolucionRequest req, Long usuarioId) {
        Venta v = ventas.detalle(ventaId).orElseThrow(() -> ApiException.notFound("Venta"));
        VentaItem it = v.getItems().stream().filter(i -> i.getId().equals(req.itemId())).findFirst()
                .orElseThrow(() -> ApiException.notFound("Ítem de la venta"));
        if (req.cantidad().compareTo(it.getDisponibleDevolver()) > 0) {
            throw ApiException.badRequest("Solo se pueden devolver "
                    + it.getDisponibleDevolver().stripTrailingZeros().toPlainString() + " unidades de este ítem");
        }
        it.setCantidadDevuelta(it.getCantidadDevuelta().add(req.cantidad()));

        Devolucion d = new Devolucion();
        d.setItem(it);
        d.setCantidad(req.cantidad());
        d.setImporte(it.getPrecioUnitario().multiply(req.cantidad()).setScale(2, RoundingMode.HALF_UP));
        d.setMotivo(req.motivo().trim());
        d.setUsuario(usuarios.getReferenceById(usuarioId));
        d.setSesionCaja(sesiones.findByUsuarioIdAndEstado(usuarioId, SesionCaja.Estado.ABIERTA).orElse(null));
        devoluciones.save(d);

        if (it.getProductoId() != null) {
            productos.bloquear(it.getProductoId()).filter(Producto::isControlaInventario).ifPresent(p -> {
                p.setExistencias(p.getExistencias().add(req.cantidad()));
                productoService.registrarMovimiento(p, MovimientoStock.Tipo.DEVOLUCION, req.cantidad(),
                        "Devolución venta #" + ventaId + ": " + d.getMotivo(), usuarioId);
            });
        }
        devoluciones.flush();
        return detalle(ventaId);
    }

    @Transactional(readOnly = true)
    public List<DevolucionDto> devoluciones(LocalDate desde, LocalDate hasta) {
        return devoluciones.entre(Fechas.inicio(desde), Fechas.fin(hasta)).stream().map(DevolucionDto::of).toList();
    }
}
