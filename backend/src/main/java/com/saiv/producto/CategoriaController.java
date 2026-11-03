package com.saiv.producto;

import com.saiv.common.ApiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    public record CategoriaDto(Long id, String nombre, long productos) {}
    public record CategoriaRequest(@NotBlank @Size(max = 80) String nombre) {}

    private final CategoriaRepository repo;

    public CategoriaController(CategoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<CategoriaDto> listar() {
        Map<Long, Long> conteo = new HashMap<>();
        repo.contarProductos().forEach(r -> conteo.put((Long) r[0], (Long) r[1]));
        return repo.findAllByOrderByNombreAsc().stream()
                .map(c -> new CategoriaDto(c.getId(), c.getNombre(), conteo.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PERM_CATEGORIAS')")
    @Transactional
    public CategoriaDto crear(@Valid @RequestBody CategoriaRequest req) {
        String nombre = req.nombre().trim();
        repo.findByNombreIgnoreCase(nombre).ifPresent(c -> { throw ApiException.conflict("La categoría ya existe"); });
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c = repo.save(c);
        return new CategoriaDto(c.getId(), c.getNombre(), 0);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_CATEGORIAS')")
    @Transactional
    public CategoriaDto actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest req) {
        String nombre = req.nombre().trim();
        Categoria c = repo.findById(id).orElseThrow(() -> ApiException.notFound("Categoría"));
        repo.findByNombreIgnoreCase(nombre).filter(o -> !o.getId().equals(id))
                .ifPresent(o -> { throw ApiException.conflict("La categoría ya existe"); });
        c.setNombre(nombre);
        return new CategoriaDto(c.getId(), c.getNombre(), 0);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_CATEGORIAS')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) throw ApiException.notFound("Categoría");
        repo.deleteById(id); // los productos quedan sin categoría (ON DELETE SET NULL)
        return ResponseEntity.noContent().build();
    }
}
