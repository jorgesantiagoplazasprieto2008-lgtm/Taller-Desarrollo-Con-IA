package com.entrecopas.productor.repository;

import com.entrecopas.productor.model.Productor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de persistencia para Productor.
 */
@Repository
public interface ProductorRepository extends JpaRepository<Productor, Long> {

    Optional<Productor> findByUsuarioId(Long usuarioId);

    Optional<Productor> findByUsuarioEmail(String email);
}
