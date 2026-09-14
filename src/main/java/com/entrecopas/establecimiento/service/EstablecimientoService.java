package com.entrecopas.establecimiento.service;

import com.entrecopas.establecimiento.dto.EstablecimientoDTO;
import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.establecimiento.repository.EstablecimientoRepository;
import com.entrecopas.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio para la gestión de locales de hostelería.
 */
@Service
public class EstablecimientoService {

    private final EstablecimientoRepository establecimientoRepository;

    public EstablecimientoService(EstablecimientoRepository establecimientoRepository) {
        this.establecimientoRepository = establecimientoRepository;
    }

    @Transactional(readOnly = true)
    public Establecimiento obtenerPorUsuario(Long usuarioId) {
        return establecimientoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no configurado para este usuario."));
    }

    @Transactional(readOnly = true)
    public EstablecimientoDTO obtenerPorId(Long id) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado con ID: " + id));
        return EstablecimientoDTO.fromEntity(est);
    }

    @Transactional(readOnly = true)
    public List<EstablecimientoDTO> listarActivos() {
        return establecimientoRepository.findByActivoTrue()
                .stream()
                .map(EstablecimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
