package com.saiv.usuario;

import com.saiv.security.Permiso;
import com.saiv.security.UsuarioPrincipal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.CAJERO;

    private boolean activo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_permisos", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "permiso")
    @Enumerated(EnumType.STRING)
    private Set<Permiso> permisos = EnumSet.noneOf(Permiso.class);

    public List<String> permisosEfectivos() {
        var set = rol == Rol.ADMIN ? Arrays.asList(Permiso.values()) : permisos;
        return set.stream().map(Enum::name).sorted().toList();
    }

    public UsuarioPrincipal toPrincipal() {
        return new UsuarioPrincipal(id, username, rol.name(), permisosEfectivos());
    }
}
