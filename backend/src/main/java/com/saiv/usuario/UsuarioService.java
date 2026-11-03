package com.saiv.usuario;

import com.saiv.common.ApiException;
import com.saiv.security.JwtService;
import com.saiv.security.Permiso;
import com.saiv.usuario.UsuarioDtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest req) {
        Usuario u = repo.findByUsernameIgnoreCase(req.username().trim())
                .filter(Usuario::isActivo)
                .filter(x -> encoder.matches(req.password(), x.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));
        return new LoginResponse(jwt.generar(u.toPrincipal()), UsuarioDto.of(u));
    }

    public Usuario get(Long id) {
        return repo.findById(id).orElseThrow(() -> ApiException.notFound("Usuario"));
    }

    public List<UsuarioDto> listar() {
        return repo.findAllByOrderByNombreAsc().stream().map(UsuarioDto::of).toList();
    }

    @Transactional
    public UsuarioDto crear(UsuarioRequest req) {
        if (req.password() == null || req.password().isBlank()) {
            throw ApiException.badRequest("La contraseña es obligatoria");
        }
        if (repo.existsByUsernameIgnoreCase(req.username())) {
            throw ApiException.conflict("Ya existe un usuario con ese nombre de usuario");
        }
        Usuario u = new Usuario();
        aplicar(u, req);
        u.setPasswordHash(encoder.encode(req.password()));
        return UsuarioDto.of(repo.save(u));
    }

    @Transactional
    public UsuarioDto actualizar(Long id, UsuarioRequest req, Long actorId) {
        Usuario u = get(id);
        repo.findByUsernameIgnoreCase(req.username())
                .filter(o -> !o.getId().equals(id))
                .ifPresent(o -> { throw ApiException.conflict("Ya existe un usuario con ese nombre de usuario"); });
        boolean eraAdminActivo = u.getRol() == Rol.ADMIN && u.isActivo();
        boolean seguiraAdminActivo = req.rol() == Rol.ADMIN && req.activo();
        if (eraAdminActivo && !seguiraAdminActivo && repo.countByRolAndActivoTrue(Rol.ADMIN) <= 1) {
            throw ApiException.conflict("Debe existir al menos un administrador activo");
        }
        if (id.equals(actorId) && !req.activo()) {
            throw ApiException.conflict("No puede desactivar su propio usuario");
        }
        aplicar(u, req);
        if (req.password() != null && !req.password().isBlank()) {
            u.setPasswordHash(encoder.encode(req.password()));
        }
        return UsuarioDto.of(u);
    }

    @Transactional
    public void eliminar(Long id, Long actorId) {
        Usuario u = get(id);
        if (id.equals(actorId)) throw ApiException.conflict("No puede eliminar su propio usuario");
        if (u.getRol() == Rol.ADMIN && u.isActivo() && repo.countByRolAndActivoTrue(Rol.ADMIN) <= 1) {
            throw ApiException.conflict("Debe existir al menos un administrador activo");
        }
        try {
            repo.delete(u);
            repo.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw ApiException.conflict("El usuario tiene ventas registradas; desactívelo en lugar de eliminarlo");
        }
    }

    @Transactional
    public void cambiarPassword(Long id, CambioPasswordRequest req) {
        Usuario u = get(id);
        if (!encoder.matches(req.actual(), u.getPasswordHash())) {
            throw ApiException.badRequest("La contraseña actual no es correcta");
        }
        u.setPasswordHash(encoder.encode(req.nueva()));
    }

    @Transactional
    public void resetPassword(Long id, String nueva) {
        get(id).setPasswordHash(encoder.encode(nueva));
    }

    private void aplicar(Usuario u, UsuarioRequest req) {
        u.setNombre(req.nombre().trim());
        u.setUsername(req.username().trim().toLowerCase());
        u.setRol(req.rol());
        u.setActivo(req.activo());
        u.getPermisos().clear();
        if (req.rol() == Rol.CAJERO && req.permisos() != null) {
            u.getPermisos().addAll(req.permisos().isEmpty() ? EnumSet.noneOf(Permiso.class) : EnumSet.copyOf(req.permisos()));
        }
    }
}
