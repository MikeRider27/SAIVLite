package com.saiv.producto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_stock")
@Getter
@Setter
public class MovimientoStock {

    public enum Tipo { ENTRADA, SALIDA, AJUSTE, VENTA, DEVOLUCION }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    /** Cantidad con signo: positiva suma stock, negativa resta. */
    private BigDecimal cantidad;
    private BigDecimal saldo;
    private String motivo;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora = LocalDateTime.now();
}
