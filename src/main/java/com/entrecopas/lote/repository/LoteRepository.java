package com.entrecopas.lote.repository;

import com.entrecopas.lote.model.Lote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de persistencia para Lote.
 */
@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByCodigoTrazabilidad(String codigoTrazabilidad);

    boolean existsByCodigoTrazabilidad(String codigoTrazabilidad);

    List<Lote> findByProductoIdOrderByFechaProduccionDesc(Long productoId);

    List<Lote> findByProductoProductorIdOrderByFechaProduccionDesc(Long productorId);

    long countByProductoProductorId(Long productorId);
}
