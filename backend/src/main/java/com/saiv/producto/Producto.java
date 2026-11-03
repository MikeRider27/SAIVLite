package com.saiv.producto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "precio_compra")
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @Column(name = "precio_venta")
    private BigDecimal precioVenta = BigDecimal.ZERO;

    private BigDecimal existencias = BigDecimal.ZERO;

    @Column(name = "stock_minimo")
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Unidad unidad = Unidad.UNIDAD;

    private String ubicacion;

    @Column(name = "controla_inventario")
    private boolean controlaInventario = true;

    private boolean activo = true;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    @PreUpdate
    void touch() {
        actualizadoEn = LocalDateTime.now();
    }

    public boolean isBajoStock() {
        return controlaInventario && existencias.compareTo(stockMinimo) <= 0;
    }
}
