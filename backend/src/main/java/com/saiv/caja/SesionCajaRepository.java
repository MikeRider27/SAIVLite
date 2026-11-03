package com.saiv.caja;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface SesionCajaRepository extends JpaRepository<SesionCaja, Long> {

    @EntityGraph(attributePaths = "usuario")
    Optional<SesionCaja> findByUsuarioIdAndEstado(Long usuarioId, SesionCaja.Estado estado);

    @EntityGraph(attributePaths = "usuario")
    Page<SesionCaja> findAllByOrderByAperturaDesc(Pageable pageable);

    @Query("select coalesce(sum(v.total), 0) from Venta v where v.sesionCaja.id = :id")
    BigDecimal totalVentas(@Param("id") Long sesionId);

    @Query("select count(v) from Venta v where v.sesionCaja.id = :id")
    long cantidadVentas(@Param("id") Long sesionId);

    @Query("select coalesce(sum(d.importe), 0) from Devolucion d where d.sesionCaja.id = :id")
    BigDecimal totalDevoluciones(@Param("id") Long sesionId);
}
