package com.saiv.producto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ProductoDtos {
    private ProductoDtos() {}

    public record ProductoDto(Long id, String codigo, String descripcion, Long categoriaId, String categoria,
                              BigDecimal precioCompra, BigDecimal precioVenta, BigDecimal existencias,
                              BigDecimal stockMinimo, Unidad unidad, String ubicacion,
                              boolean controlaInventario, boolean activo, boolean bajoStock,
                              LocalDateTime actualizadoEn) {
        public static ProductoDto of(Producto p) {
            var c = p.getCategoria();
            return new ProductoDto(p.getId(), p.getCodigo(), p.getDescripcion(),
                    c == null ? null : c.getId(), c == null ? null : c.getNombre(),
                    p.getPrecioCompra(), p.getPrecioVenta(), p.getExistencias(), p.getStockMinimo(),
                    p.getUnidad(), p.getUbicacion(), p.isControlaInventario(), p.isActivo(), p.isBajoStock(),
                    p.getActualizadoEn());
        }
    }

    public record ProductoRequest(
            @NotBlank @Size(max = 60) String codigo,
            @NotBlank @Size(max = 200) String descripcion,
            Long categoriaId,
            @NotNull @PositiveOrZero BigDecimal precioCompra,
            @NotNull @PositiveOrZero BigDecimal precioVenta,
            @PositiveOrZero BigDecimal existencias,
            @NotNull @PositiveOrZero BigDecimal stockMinimo,
            @NotNull Unidad unidad,
            @Size(max = 80) String ubicacion,
            boolean controlaInventario,
            boolean activo) {}

    public enum TipoAjuste { ENTRADA, SALIDA, FIJAR }

    public record AjusteStockRequest(@NotNull TipoAjuste tipo,
                                     @NotNull @PositiveOrZero BigDecimal cantidad,
                                     @NotBlank @Size(max = 200) String motivo) {}

    public record MovimientoDto(Long id, MovimientoStock.Tipo tipo, BigDecimal cantidad, BigDecimal saldo,
                                String motivo, LocalDateTime fechaHora) {
        public static MovimientoDto of(MovimientoStock m) {
            return new MovimientoDto(m.getId(), m.getTipo(), m.getCantidad(), m.getSaldo(), m.getMotivo(), m.getFechaHora());
        }
    }

    public record ImportResult(int creados, int actualizados, java.util.List<String> errores) {}
}
