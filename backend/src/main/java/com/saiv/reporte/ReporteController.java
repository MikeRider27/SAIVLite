package com.saiv.reporte;

import com.saiv.common.Fechas;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAuthority('PERM_REPORTES')")
public class ReporteController {

    public record PorDia(LocalDate fecha, long cantidad, BigDecimal total) {}
    public record TopProducto(String codigo, String descripcion, BigDecimal cantidad, BigDecimal total) {}
    public record Grupo(String nombre, long cantidad, BigDecimal total) {}

    public record Resumen(LocalDate desde, LocalDate hasta, long cantidadVentas, BigDecimal totalBruto,
                          BigDecimal totalDevoluciones, BigDecimal totalNeto, BigDecimal ticketPromedio,
                          BigDecimal costo, BigDecimal ganancia, List<PorDia> porDia, List<TopProducto> topProductos,
                          List<Grupo> porCajero, List<Grupo> porCategoria, List<Grupo> porMetodo) {}

    public record Inventario(long productos, long activos, long bajoStock, long sinStock, BigDecimal unidades,
                             BigDecimal valorCosto, BigDecimal valorVenta, List<Grupo> porCategoria) {}

    private final NamedParameterJdbcTemplate jdbc;

    public ReporteController(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static BigDecimal bd(Object o) {
        return o == null ? BigDecimal.ZERO : (BigDecimal) o;
    }

    @GetMapping("/resumen")
    public Resumen resumen(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        LocalDate d = desde == null ? LocalDate.now() : desde;
        LocalDate h = hasta == null ? d : hasta;
        var p = new MapSqlParameterSource().addValue("d", Fechas.inicio(d)).addValue("h", Fechas.fin(h));

        var tot = jdbc.queryForMap("""
            select count(*) as n, coalesce(sum(total), 0) as total
            from ventas where fecha_hora >= :d and fecha_hora < :h
            """, p);
        long n = ((Number) tot.get("n")).longValue();
        BigDecimal bruto = bd(tot.get("total"));

        BigDecimal devol = bd(jdbc.queryForObject("""
            select coalesce(sum(importe), 0) from devoluciones where fecha_hora >= :d and fecha_hora < :h
            """, p, BigDecimal.class));

        var margen = jdbc.queryForMap("""
            select coalesce(sum(i.costo_unitario * (i.cantidad - i.cantidad_devuelta)), 0) as costo,
                   coalesce(sum(i.precio_unitario * (i.cantidad - i.cantidad_devuelta)), 0) as venta
            from venta_items i join ventas v on v.id = i.venta_id
            where v.fecha_hora >= :d and v.fecha_hora < :h
            """, p);
        BigDecimal costo = bd(margen.get("costo")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ganancia = bd(margen.get("venta")).subtract(bd(margen.get("costo"))).setScale(2, RoundingMode.HALF_UP);

        List<PorDia> porDia = jdbc.query("""
            select cast(fecha_hora as date) as dia, count(*) as n, sum(total) as total
            from ventas where fecha_hora >= :d and fecha_hora < :h
            group by 1 order by 1
            """, p, (rs, i) -> new PorDia(rs.getDate("dia").toLocalDate(), rs.getLong("n"), rs.getBigDecimal("total")));

        List<TopProducto> top = jdbc.query("""
            select i.codigo, max(i.descripcion) as descripcion,
                   sum(i.cantidad - i.cantidad_devuelta) as cant,
                   sum(i.precio_unitario * (i.cantidad - i.cantidad_devuelta)) as total
            from venta_items i join ventas v on v.id = i.venta_id
            where v.fecha_hora >= :d and v.fecha_hora < :h
            group by i.codigo
            having sum(i.cantidad - i.cantidad_devuelta) > 0
            order by total desc limit 10
            """, p, (rs, i) -> new TopProducto(rs.getString("codigo"), rs.getString("descripcion"),
                rs.getBigDecimal("cant"), rs.getBigDecimal("total")));

        List<Grupo> porCajero = jdbc.query("""
            select u.nombre, count(*) as n, sum(v.total) as total
            from ventas v join usuarios u on u.id = v.usuario_id
            where v.fecha_hora >= :d and v.fecha_hora < :h
            group by u.nombre order by total desc
            """, p, (rs, i) -> new Grupo(rs.getString("nombre"), rs.getLong("n"), rs.getBigDecimal("total")));

        List<Grupo> porCategoria = jdbc.query("""
            select coalesce(i.categoria, 'Sin categoría') as nombre, count(distinct v.id) as n,
                   sum(i.precio_unitario * (i.cantidad - i.cantidad_devuelta)) as total
            from venta_items i join ventas v on v.id = i.venta_id
            where v.fecha_hora >= :d and v.fecha_hora < :h
            group by 1 order by total desc
            """, p, (rs, i) -> new Grupo(rs.getString("nombre"), rs.getLong("n"), rs.getBigDecimal("total")));

        List<Grupo> porMetodo = jdbc.query("""
            select metodo_pago as nombre, count(*) as n, sum(total) as total
            from ventas where fecha_hora >= :d and fecha_hora < :h
            group by 1 order by total desc
            """, p, (rs, i) -> new Grupo(rs.getString("nombre"), rs.getLong("n"), rs.getBigDecimal("total")));

        BigDecimal neto = bruto.subtract(devol);
        BigDecimal promedio = n == 0 ? BigDecimal.ZERO : bruto.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        return new Resumen(d, h, n, bruto, devol, neto, promedio, costo, ganancia, porDia, top, porCajero, porCategoria, porMetodo);
    }

    @GetMapping("/inventario")
    public Inventario inventario() {
        var t = jdbc.queryForMap("""
            select count(*) as productos,
                   count(*) filter (where activo) as activos,
                   count(*) filter (where activo and controla_inventario and existencias <= stock_minimo) as bajo,
                   count(*) filter (where activo and controla_inventario and existencias <= 0) as sin,
                   coalesce(sum(existencias) filter (where controla_inventario), 0) as unidades,
                   coalesce(sum(existencias * precio_compra) filter (where controla_inventario), 0) as costo,
                   coalesce(sum(existencias * precio_venta) filter (where controla_inventario), 0) as venta
            from productos
            """, new MapSqlParameterSource());
        List<Grupo> porCat = jdbc.query("""
            select coalesce(c.nombre, 'Sin categoría') as nombre, count(*) as n,
                   coalesce(sum(p.existencias * p.precio_compra) filter (where p.controla_inventario), 0) as total
            from productos p left join categorias c on c.id = p.categoria_id
            group by 1 order by total desc
            """, (rs, i) -> new Grupo(rs.getString("nombre"), rs.getLong("n"), rs.getBigDecimal("total")));
        return new Inventario(((Number) t.get("productos")).longValue(), ((Number) t.get("activos")).longValue(),
                ((Number) t.get("bajo")).longValue(), ((Number) t.get("sin")).longValue(),
                bd(t.get("unidades")), bd(t.get("costo")).setScale(2, RoundingMode.HALF_UP),
                bd(t.get("venta")).setScale(2, RoundingMode.HALF_UP), porCat);
    }
}
