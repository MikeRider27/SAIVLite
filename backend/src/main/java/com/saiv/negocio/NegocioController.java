package com.saiv.negocio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/negocio")
public class NegocioController {

    public record NegocioDto(String nombre, String direccion, String telefono, String ruc, String moneda,
                             short decimales, String lineaExtra, String mensajeGracias, String mensajeFinal) {
        static NegocioDto of(Negocio n) {
            return new NegocioDto(n.getNombre(), n.getDireccion(), n.getTelefono(), n.getRuc(), n.getMoneda(),
                    n.getDecimales() == null ? 0 : n.getDecimales(), n.getLineaExtra(), n.getMensajeGracias(), n.getMensajeFinal());
        }
    }

    public record NegocioRequest(@NotBlank @Size(max = 120) String nombre, @Size(max = 200) String direccion,
                                 @Size(max = 60) String telefono, @Size(max = 40) String ruc,
                                 @NotBlank @Size(max = 10) String moneda, @Min(0) @Max(3) short decimales,
                                 @Size(max = 200) String lineaExtra, @Size(max = 200) String mensajeGracias,
                                 @Size(max = 200) String mensajeFinal) {}

    private final NegocioRepository repo;

    public NegocioController(NegocioRepository repo) {
        this.repo = repo;
    }

    private Negocio get() {
        return repo.findById((short) 1).orElseGet(() -> {
            Negocio n = new Negocio();
            n.setNombre("Mi Negocio");
            n.setMoneda("Gs.");
            n.setDecimales((short) 0);
            return repo.save(n);
        });
    }

    @GetMapping
    @Transactional
    public NegocioDto ver() {
        return NegocioDto.of(get());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('PERM_CONFIGURACION')")
    @Transactional
    public NegocioDto guardar(@Valid @RequestBody NegocioRequest r) {
        Negocio n = get();
        n.setNombre(r.nombre().trim());
        n.setDireccion(r.direccion());
        n.setTelefono(r.telefono());
        n.setRuc(r.ruc());
        n.setMoneda(r.moneda().trim());
        n.setDecimales(r.decimales());
        n.setLineaExtra(r.lineaExtra());
        n.setMensajeGracias(r.mensajeGracias());
        n.setMensajeFinal(r.mensajeFinal());
        return NegocioDto.of(n);
    }
}
