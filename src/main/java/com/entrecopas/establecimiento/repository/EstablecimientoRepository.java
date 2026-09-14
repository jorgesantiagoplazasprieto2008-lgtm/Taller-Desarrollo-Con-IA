package com.entrecopas.establecimiento.repository;

import com.entrecopas.establecimiento.model.Establecimiento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de persistencia para Establecimiento.
 */
@Repository
public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {

    Optional<Establecimiento> findByUsuarioId(Long usuarioId);

    List<Establecimiento> findByCiudadIgnoreCase(String ciudad);

    List<Establecimiento> findByActivoTrue();
}
