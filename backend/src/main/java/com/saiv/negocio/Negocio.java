package com.saiv.negocio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "negocio")
@Getter
@Setter
public class Negocio {
    @Id
    private Short id = 1;
    private String nombre;
    private String direccion;
    private String telefono;
    private String ruc;
    private String moneda;
    private Short decimales;
    @Column(name = "linea_extra")
    private String lineaExtra;
    @Column(name = "mensaje_gracias")
    private String mensajeGracias;
    @Column(name = "mensaje_final")
    private String mensajeFinal;
}
