package com.saiv.venta;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {

    @EntityGraph(attributePaths = {"item", "item.venta", "usuario"})
    @Query("select d from Devolucion d where d.fechaHora >= :desde and d.fechaHora < :hasta order by d.fechaHora desc")
    List<Devolucion> entre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @EntityGraph(attributePaths = {"item", "usuario"})
    @Query("select d from Devolucion d where d.item.venta.id = :ventaId order by d.fechaHora")
    List<Devolucion> deVenta(@Param("ventaId") Long ventaId);
}
