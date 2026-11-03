package com.saiv.venta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = {"usuario", "items"})
    @Query("select v from Venta v where v.id = :id")
    Optional<Venta> detalle(@Param("id") Long id);

    @EntityGraph(attributePaths = "usuario")
    @Query("""
        select v from Venta v
        where v.fechaHora >= :desde and v.fechaHora < :hasta
          and (:usuarioId = 0 or v.usuario.id = :usuarioId)
        """)
    Page<Venta> buscar(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
                       @Param("usuarioId") Long usuarioId, Pageable pageable);
}
