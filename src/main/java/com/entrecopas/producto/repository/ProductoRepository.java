package com.entrecopas.producto.repository;

import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de persistencia para Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstado(EstadoProducto estado);

    List<Producto> findByEstadoAndTipoBebidaIgnoreCase(EstadoProducto estado, String tipoBebida);

    List<Producto> findByProductorId(Long productorId);

    List<Producto> findByProductorIdOrderByCreatedAtDesc(Long productorId);

    Optional<Producto> findByIdAndProductorId(Long id, Long productorId);

    @Query("SELECT p FROM Producto p WHERE p.estado = :estado "
            + "AND (:tipo IS NULL OR LOWER(p.tipoBebida) = LOWER(:tipo)) "
            + "AND (:q IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "     OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Producto> buscarActivos(
            @Param("estado") EstadoProducto estado,
            @Param("tipo") String tipo,
            @Param("q") String q
    );
}
