package com.saiv.venta;

import com.saiv.common.PageResponse;
import com.saiv.security.CurrentUser;
import com.saiv.security.UsuarioPrincipal;
import com.saiv.common.ApiException;
import com.saiv.venta.VentaDtos.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VentaController {

    private final VentaService service;

    public VentaController(VentaService service) {
        this.service = service;
    }

    @PostMapping("/ventas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PERM_VENDER')")
    public VentaDto registrar(@Valid @RequestBody VentaRequest req) {
        return service.registrar(req, CurrentUser.id());
    }

    /** Quien no tiene permiso de reportes solo ve sus propias ventas. */
    @GetMapping("/ventas")
    public PageResponse<VentaResumenDto> buscar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        UsuarioPrincipal u = CurrentUser.get();
        Long filtro = u.permisos().contains("REPORTES") ? usuarioId : u.id();
        return service.buscar(desde, hasta, filtro, page, size);
    }

    @GetMapping("/ventas/{id}")
    public VentaDto detalle(@PathVariable Long id) {
        VentaDto v = service.detalle(id);
        UsuarioPrincipal u = CurrentUser.get();
        if (!u.permisos().contains("REPORTES") && !u.permisos().contains("DEVOLUCIONES") && !v.cajeroId().equals(u.id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No tiene permiso para ver esta venta");
        }
        return v;
    }

    @PostMapping("/ventas/{id}/devoluciones")
    @PreAuthorize("hasAuthority('PERM_DEVOLUCIONES')")
    public VentaDto devolver(@PathVariable Long id, @Valid @RequestBody DevolucionRequest req) {
        return service.devolver(id, req, CurrentUser.id());
    }

    @GetMapping("/devoluciones")
    @PreAuthorize("hasAuthority('PERM_DEVOLUCIONES') or hasAuthority('PERM_REPORTES')")
    public List<DevolucionDto> devoluciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.devoluciones(desde, hasta);
    }
}
