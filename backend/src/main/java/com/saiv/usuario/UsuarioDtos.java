package com.saiv.usuario;

import com.saiv.security.Permiso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class UsuarioDtos {
    private UsuarioDtos() {}

    public record UsuarioDto(Long id, String nombre, String username, Rol rol, boolean activo,
                             List<String> permisos, LocalDateTime creadoEn) {
        public static UsuarioDto of(Usuario u) {
            return new UsuarioDto(u.getId(), u.getNombre(), u.getUsername(), u.getRol(), u.isActivo(),
                    u.permisosEfectivos(), u.getCreadoEn());
        }
    }

    public record UsuarioRequest(
            @NotBlank @Size(max = 120) String nombre,
            @NotBlank @Size(min = 3, max = 60) @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "solo letras, números, punto, guion") String username,
            @Size(min = 6, max = 100) String password,
            @NotNull Rol rol,
            boolean activo,
            Set<Permiso> permisos) {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record LoginResponse(String token, UsuarioDto usuario) {}

    public record CambioPasswordRequest(@NotBlank String actual, @NotBlank @Size(min = 6, max = 100) String nueva) {}

    public record ResetPasswordRequest(@NotBlank @Size(min = 6, max = 100) String nueva) {}
}
