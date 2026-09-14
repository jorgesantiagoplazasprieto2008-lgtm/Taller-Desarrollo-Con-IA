package com.entrecopas.disponibilidad.repository;

import com.entrecopas.disponibilidad.model.Disponibilidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad Disponibilidad.
 */
@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByEstablecimientoId(Long establecimientoId);

    @Query("SELECT d FROM Disponibilidad d JOIN FETCH d.establecimiento e "
            + "WHERE d.producto.id = :productoId AND d.disponible = true AND e.activo = true")
    List<Disponibilidad> findByProductoIdAndDisponibleTrue(@Param("productoId") Long productoId);

    Optional<Disponibilidad> findByProductoIdAndEstablecimientoId(Long productoId, Long establecimientoId);
}
