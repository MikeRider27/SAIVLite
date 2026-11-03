package com.saiv.caja;

import com.saiv.common.ApiException;
import com.saiv.common.PageResponse;
import com.saiv.security.CurrentUser;
import com.saiv.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    public record SesionDto(Long id, Long usuarioId, String usuario, LocalDateTime apertura, BigDecimal montoInicial,
                            LocalDateTime cierre, long cantidadVentas, BigDecimal totalVentas,
                            BigDecimal totalDevoluciones, BigDecimal montoEsperado, BigDecimal montoDeclarado,
                            BigDecimal diferencia, String observacion, SesionCaja.Estado estado) {}

    public record AbrirRequest(@NotNull @PositiveOrZero BigDecimal montoInicial) {}

    public record CerrarRequest(@NotNull @PositiveOrZero BigDecimal montoDeclarado, @Size(max = 300) String observacion) {}

    private final SesionCajaRepository repo;
    private final UsuarioRepository usuarios;

    public CajaController(SesionCajaRepository repo, UsuarioRepository usuarios) {
        this.repo = repo;
        this.usuarios = usuarios;
    }

    private SesionDto dto(SesionCaja s) {
        BigDecimal ventas = repo.totalVentas(s.getId());
        BigDecimal devol = repo.totalDevoluciones(s.getId());
        BigDecimal esperado = s.getMontoEsperado() != null ? s.getMontoEsperado()
                : s.getMontoInicial().add(ventas).subtract(devol);
        BigDecimal dif = s.getMontoDeclarado() == null ? null : s.getMontoDeclarado().subtract(esperado);
        return new SesionDto(s.getId(), s.getUsuario().getId(), s.getUsuario().getNombre(), s.getApertura(),
                s.getMontoInicial(), s.getCierre(), repo.cantidadVentas(s.getId()), ventas, devol, esperado,
                s.getMontoDeclarado(), dif, s.getObservacion(), s.getEstado());
    }

    /** Sesión abierta del usuario actual, o 204 si no tiene. */
    @GetMapping("/actual")
    @Transactional(readOnly = true)
    public ResponseEntity<SesionDto> actual() {
        return repo.findByUsuarioIdAndEstado(CurrentUser.id(), SesionCaja.Estado.ABIERTA)
                .map(s -> ResponseEntity.ok(dto(s)))
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/abrir")
    @PreAuthorize("hasAuthority('PERM_VENDER')")
    @Transactional
    public SesionDto abrir(@Valid @RequestBody AbrirRequest req) {
        Long uid = CurrentUser.id();
        if (repo.findByUsuarioIdAndEstado(uid, SesionCaja.Estado.ABIERTA).isPresent()) {
            throw ApiException.conflict("Ya tiene una caja abierta");
        }
        SesionCaja s = new SesionCaja();
        s.setUsuario(usuarios.getReferenceById(uid));
        s.setMontoInicial(req.montoInicial());
        s = repo.saveAndFlush(s);
        s.setUsuario(usuarios.findById(uid).orElseThrow());
        return dto(s);
    }

    @PostMapping("/cerrar")
    @Transactional
    public SesionDto cerrar(@Valid @RequestBody CerrarRequest req) {
        SesionCaja s = repo.findByUsuarioIdAndEstado(CurrentUser.id(), SesionCaja.Estado.ABIERTA)
                .orElseThrow(() -> ApiException.conflict("No tiene una caja abierta"));
        s.setMontoEsperado(s.getMontoInicial().add(repo.totalVentas(s.getId())).subtract(repo.totalDevoluciones(s.getId())));
        s.setMontoDeclarado(req.montoDeclarado());
        s.setObservacion(req.observacion());
        s.setCierre(LocalDateTime.now());
        s.setEstado(SesionCaja.Estado.CERRADA);
        return dto(s);
    }

    @GetMapping("/sesiones")
    @PreAuthorize("hasAuthority('PERM_REPORTES')")
    @Transactional(readOnly = true)
    public PageResponse<SesionDto> sesiones(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.of(repo.findAllByOrderByAperturaDesc(PageRequest.of(page, Math.min(size, 100))), this::dto);
    }
}
