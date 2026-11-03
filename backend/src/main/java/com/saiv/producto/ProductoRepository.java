package com.saiv.producto;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    @EntityGraph(attributePaths = "categoria")
    @Query("""
        select p from Producto p
        where (:q = '' or lower(p.codigo) like :q or lower(p.descripcion) like :q)
          and (:categoriaId = 0 or p.categoria.id = :categoriaId)
          and (:soloActivos = false or p.activo = true)
          and (:bajoStock = false or (p.controlaInventario = true and p.existencias <= p.stockMinimo))
        """)
    Page<Producto> buscar(@Param("q") String q, @Param("categoriaId") Long categoriaId,
                          @Param("soloActivos") boolean soloActivos, @Param("bajoStock") boolean bajoStock,
                          Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id in :ids")
    List<Producto> bloquear(@Param("ids") Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> bloquear(@Param("id") Long id);

    @Query("select p from Producto p left join fetch p.categoria order by p.descripcion")
    List<Producto> todosConCategoria();
}
