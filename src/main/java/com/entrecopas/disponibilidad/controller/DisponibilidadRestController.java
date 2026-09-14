package com.entrecopas.disponibilidad.controller;

import com.entrecopas.disponibilidad.dto.DisponibilidadProductoDTO;
import com.entrecopas.disponibilidad.dto.EstablecimientoLocalDTO;
import com.entrecopas.disponibilidad.dto.ToggleDisponibilidadRequest;
import com.entrecopas.disponibilidad.model.Disponibilidad;
import com.entrecopas.disponibilidad.service.DisponibilidadService;
import com.entrecopas.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de disponibilidad en hostelería y consulta pública.
 */
@RestController
@RequestMapping("/api/v1")
public class DisponibilidadRestController {

    private final DisponibilidadService disponibilidadService;
    private final UsuarioService usuarioService;

    public DisponibilidadRestController(DisponibilidadService disponibilidadService,
                                        UsuarioService usuarioService) {
        this.disponibilidadService = disponibilidadService;
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para alternar la disponibilidad de un producto en barra (AJAX/Fetch).
     */
    @PostMapping("/hosteleria/disponibilidad/toggle")
    public ResponseEntity<Map<String, Object>> toggleDisponibilidad(
            @Valid @RequestBody ToggleDisponibilidadRequest request) {
        Long usuarioId = usuarioService.obtenerUsuarioHosteleriaAutenticadoId().orElse(2L);
        Disponibilidad disp = disponibilidadService.alternarDisponibilidad(
                usuarioId, request.getProductoId(), request.getDisponible());

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "productoId", disp.getProducto().getId(),
                "disponible", disp.getDisponible(),
                "updatedAt", disp.getUpdatedAt().toString()
        ));
    }

    /**
     * Lista todos los productos y su estado en el local de hostelería actual.
     */
    @GetMapping("/hosteleria/mis-productos")
    public ResponseEntity<List<DisponibilidadProductoDTO>> listarProductosHosteleria() {
        Long usuarioId = usuarioService.obtenerUsuarioHosteleriaAutenticadoId().orElse(2L);
        return ResponseEntity.ok(disponibilidadService.listarProductosParaHosteleria(usuarioId));
    }

    /**
     * Consulta pública de locales donde un producto específico está disponible.
     */
    @GetMapping("/productos/{id}/disponibilidad")
    public ResponseEntity<List<EstablecimientoLocalDTO>> consultarDisponibilidadPublica(
            @PathVariable Long id) {
        return ResponseEntity.ok(disponibilidadService.obtenerLocalesConDisponibilidad(id));
    }
}
