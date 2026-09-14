package com.entrecopas.disponibilidad.service;

import com.entrecopas.disponibilidad.dto.DisponibilidadProductoDTO;
import com.entrecopas.disponibilidad.dto.EstablecimientoLocalDTO;
import com.entrecopas.disponibilidad.model.Disponibilidad;
import com.entrecopas.disponibilidad.repository.DisponibilidadRepository;
import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.establecimiento.repository.EstablecimientoRepository;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio para la gestión de disponibilidad y presencia local en hostelería.
 */
@Service
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final ProductoRepository productoRepository;

    public DisponibilidadService(DisponibilidadRepository disponibilidadRepository,
                                 EstablecimientoRepository establecimientoRepository,
                                 ProductoRepository productoRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Lista todos los productos artesanales activos junto con el estado de disponibilidad
     * en el local perteneciente al usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<DisponibilidadProductoDTO> listarProductosParaHosteleria(Long usuarioId) {
        Establecimiento establecimiento = establecimientoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un establecimiento gastronómico asociado a este usuario."));

        List<Producto> productosActivos = productoRepository.findByEstado(EstadoProducto.ACTIVO);
        List<Disponibilidad> disponibilidades = disponibilidadRepository.findByEstablecimientoId(
                establecimiento.getId());

        Map<Long, Disponibilidad> dispMap = disponibilidades.stream()
                .collect(Collectors.toMap(d -> d.getProducto().getId(), d -> d, (existing, replacement) -> existing));

        return productosActivos.stream().map(prod -> {
            Disponibilidad disp = dispMap.get(prod.getId());
            boolean estaDisponible = disp != null && Boolean.TRUE.equals(disp.getDisponible());
            return new DisponibilidadProductoDTO(
                    prod.getId(),
                    prod.getNombre(),
                    prod.getTipoBebida(),
                    prod.getPresentacion(),
                    prod.getProductor().getNombreComercial(),
                    prod.getPrecio(),
                    estaDisponible,
                    disp != null ? disp.getUpdatedAt() : null
            );
        }).collect(Collectors.toList());
    }

    /**
     * Alterna o establece el estado de disponibilidad de una bebida en el local del usuario autenticado.
     * Aplica barrera multi-tenant impidiendo modificar locales ajenos.
     */
    @Transactional
    public Disponibilidad alternarDisponibilidad(Long usuarioId, Long productoId, boolean disponible) {
        Establecimiento establecimiento = establecimientoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un establecimiento gastronómico asociado a este usuario."));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        Disponibilidad disp = disponibilidadRepository
                .findByProductoIdAndEstablecimientoId(productoId, establecimiento.getId())
                .orElseGet(() -> {
                    Disponibilidad nueva = new Disponibilidad();
                    nueva.setProducto(producto);
                    nueva.setEstablecimiento(establecimiento);
                    return nueva;
                });

        disp.setDisponible(disponible);
        return disponibilidadRepository.save(disp);
    }

    /**
     * Consulta pública: obtiene la lista de locales de hostelería donde un producto
     * se encuentra actualmente disponible en barra.
     */
    @Transactional(readOnly = true)
    public List<EstablecimientoLocalDTO> obtenerLocalesConDisponibilidad(Long productoId) {
        return disponibilidadRepository.findByProductoIdAndDisponibleTrue(productoId)
                .stream()
                .map(EstablecimientoLocalDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
