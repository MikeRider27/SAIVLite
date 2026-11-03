package com.saiv.caja;

import com.saiv.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_caja")
@Getter
@Setter
public class SesionCaja {

    public enum Estado { ABIERTA, CERRADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime apertura = LocalDateTime.now();

    @Column(name = "monto_inicial")
    private BigDecimal montoInicial = BigDecimal.ZERO;

    private LocalDateTime cierre;

    @Column(name = "monto_esperado")
    private BigDecimal montoEsperado;

    @Column(name = "monto_declarado")
    private BigDecimal montoDeclarado;

    private String observacion;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ABIERTA;
}
