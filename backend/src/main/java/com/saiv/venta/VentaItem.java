package com.saiv.venta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "venta_items")
@Getter
@Setter
public class VentaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @Column(name = "producto_id")
    private Long productoId;

    private String codigo;
    private String descripcion;
    private String categoria;
    private BigDecimal cantidad;

    @Column(name = "cantidad_devuelta")
    private BigDecimal cantidadDevuelta = BigDecimal.ZERO;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name = "costo_unitario")
    private BigDecimal costoUnitario;

    private BigDecimal importe;

    public BigDecimal getDisponibleDevolver() {
        return cantidad.subtract(cantidadDevuelta);
    }
}
