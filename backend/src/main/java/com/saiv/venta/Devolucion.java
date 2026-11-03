package com.saiv.venta;

import com.saiv.caja.SesionCaja;
import com.saiv.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
@Getter
@Setter
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_item_id")
    private VentaItem item;

    private BigDecimal cantidad;
    private BigDecimal importe;
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_caja_id")
    private SesionCaja sesionCaja;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora = LocalDateTime.now();
}
