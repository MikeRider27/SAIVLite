package com.saiv.usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Crea el administrador inicial si la base de datos no tiene usuarios. */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final String username;
    private final String password;

    public AdminSeeder(UsuarioRepository repo, PasswordEncoder encoder,
                       @Value("${saiv.admin.username}") String username,
                       @Value("${saiv.admin.password}") String password) {
        this.repo = repo;
        this.encoder = encoder;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) return;
        Usuario u = new Usuario();
        u.setNombre("Administrador");
        u.setUsername(username.toLowerCase());
        u.setPasswordHash(encoder.encode(password));
        u.setRol(Rol.ADMIN);
        repo.save(u);
        log.warn("Usuario administrador inicial creado: '{}'. Cambie la contraseña tras el primer ingreso.", username);
    }
}
