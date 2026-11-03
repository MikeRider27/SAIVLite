package com.saiv.producto;

import com.saiv.common.PageResponse;
import com.saiv.producto.ProductoDtos.*;
import com.saiv.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<ProductoDto> buscar(@RequestParam(required = false) String q,
                                            @RequestParam(required = false) Long categoriaId,
                                            @RequestParam(defaultValue = "false") boolean soloActivos,
                                            @RequestParam(defaultValue = "false") boolean bajoStock,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "25") int size,
                                            @RequestParam(defaultValue = "descripcion") String sort,
                                            @RequestParam(defaultValue = "false") boolean desc) {
        return service.buscar(q, categoriaId, soloActivos, bajoStock, page, size, sort, desc);
    }

    @GetMapping("/{id}")
    public ProductoDto porId(@PathVariable Long id) {
        return service.porId(id);
    }

    @GetMapping("/codigo/{codigo}")
    public ProductoDto porCodigo(@PathVariable String codigo) {
        return service.porCodigo(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PERM_PRODUCTOS')")
    public ProductoDto crear(@Valid @RequestBody ProductoRequest req) {
        return service.crear(req, CurrentUser.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PRODUCTOS')")
    public ProductoDto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PRODUCTOS')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ajustes")
    @PreAuthorize("hasAuthority('PERM_AJUSTAR_STOCK')")
    public ProductoDto ajustar(@PathVariable Long id, @Valid @RequestBody AjusteStockRequest req) {
        return service.ajustar(id, req, CurrentUser.id());
    }

    @GetMapping("/{id}/movimientos")
    public List<MovimientoDto> movimientos(@PathVariable Long id) {
        return service.movimientos(id);
    }

    @GetMapping(value = "/exportar", produces = "text/csv")
    @PreAuthorize("hasAuthority('PERM_PRODUCTOS') or hasAuthority('PERM_REPORTES')")
    public ResponseEntity<byte[]> exportar() {
        byte[] body = service.exportarCsv().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos-" + LocalDate.now() + ".csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(body);
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PERM_PRODUCTOS')")
    public ImportResult importar(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        return service.importarCsv(archivo.getInputStream(), CurrentUser.id());
    }
}
