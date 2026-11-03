package com.saiv.usuario;

import com.saiv.security.CurrentUser;
import com.saiv.usuario.UsuarioDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @GetMapping("/me")
    public UsuarioDto me() {
        return UsuarioDto.of(service.get(CurrentUser.id()));
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambioPasswordRequest req) {
        service.cambiarPassword(CurrentUser.id(), req);
        return ResponseEntity.noContent().build();
    }
}
