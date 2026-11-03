package com.saiv.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
    List<Categoria> findAllByOrderByNombreAsc();

    @Query("select c.id, count(p) from Categoria c left join Producto p on p.categoria = c group by c.id")
    List<Object[]> contarProductos();
}
