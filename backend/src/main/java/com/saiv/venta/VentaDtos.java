package com.saiv.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class VentaDtos {
    private VentaDtos() {}

    public record ItemRequest(@NotNull Long productoId, @NotNull @Positive BigDecimal cantidad) {}

    public record VentaRequest(@NotEmpty @Valid List<ItemRequest> items,
                               @NotNull Venta.MetodoPago metodoPago,
                               @NotNull @PositiveOrZero BigDecimal pagado) {}

    public record ItemDto(Long id, Long productoId, String codigo, String descripcion, BigDecimal cantidad,
                          BigDecimal cantidadDevuelta, BigDecimal precioUnitario, BigDecimal importe) {
        static ItemDto of(VentaItem i) {
            return new ItemDto(i.getId(), i.getProductoId(), i.getCodigo(), i.getDescripcion(), i.getCantidad(),
                    i.getCantidadDevuelta(), i.getPrecioUnitario(), i.getImporte());
        }
    }

    public record DevolucionDto(Long id, Long ventaId, Long itemId, String codigo, String descripcion,
                                BigDecimal cantidad, BigDecimal importe, String motivo, String usuario,
                                LocalDateTime fechaHora) {
        static DevolucionDto of(Devolucion d) {
            return new DevolucionDto(d.getId(), d.getItem().getVenta().getId(), d.getItem().getId(),
                    d.getItem().getCodigo(), d.getItem().getDescripcion(), d.getCantidad(), d.getImporte(),
                    d.getMotivo(), d.getUsuario().getNombre(), d.getFechaHora());
        }
    }

    public record VentaResumenDto(Long id, LocalDateTime fechaHora, String cajero, Venta.MetodoPago metodoPago,
                                  BigDecimal total) {
        static VentaResumenDto of(Venta v) {
            return new VentaResumenDto(v.getId(), v.getFechaHora(), v.getUsuario().getNombre(), v.getMetodoPago(), v.getTotal());
        }
    }

    public record VentaDto(Long id, LocalDateTime fechaHora, Long cajeroId, String cajero, Venta.MetodoPago metodoPago,
                           BigDecimal total, BigDecimal pagado, BigDecimal cambio, BigDecimal totalDevuelto,
                           List<ItemDto> items, List<DevolucionDto> devoluciones) {}

    public record DevolucionRequest(@NotNull Long itemId, @NotNull @Positive BigDecimal cantidad,
                                    @NotBlank @Size(max = 300) String motivo) {}
}
