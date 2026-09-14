package com.entrecopas.lote.controller;

import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.lote.dto.CreateLoteRequest;
import com.entrecopas.lote.dto.LoteDTO;
import com.entrecopas.lote.dto.TrazabilidadPublicaDTO;
import com.entrecopas.lote.service.LoteService;
import com.entrecopas.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para trazabilidad pública certificada y gestión de lotes productivos.
 */
@RestController
@RequestMapping("/api/v1")
public class LoteRestController {

    private final LoteService loteService;
    private final UsuarioService usuarioService;

    public LoteRestController(LoteService loteService, UsuarioService usuarioService) {
        this.loteService = loteService;
        this.usuarioService = usuarioService;
    }

    /**
     * Consulta pública de trazabilidad de lote por código (Sin autenticación).
     * Oculta de forma estricta costos y mermas.
     */
    @GetMapping("/trazabilidad/{codigo}")
    public ResponseEntity<TrazabilidadPublicaDTO> obtenerTrazabilidadPublica(@PathVariable String codigo) {
        TrazabilidadPublicaDTO dto = loteService.obtenerTrazabilidadPublica(codigo);
        return ResponseEntity.ok(dto);
    }

    /**
     * Registro de un nuevo lote productivo por el productor autenticado.
     */
    @PostMapping("/productor/lotes")
    public ResponseEntity<LoteDTO> crearLote(@Valid @RequestBody CreateLoteRequest request) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId()
                .orElseThrow(() -> new UnauthorizedDomainException("Debe iniciar sesión como productor."));
        LoteDTO creado = loteService.crearLote(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Listado privado de lotes del productor autenticado.
     */
    @GetMapping("/productor/lotes")
    public ResponseEntity<List<LoteDTO>> listarMisLotes() {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId()
                .orElseThrow(() -> new UnauthorizedDomainException("Debe iniciar sesión como productor."));
        List<LoteDTO> lotes = loteService.listarLotesDelProductor(usuarioId);
        return ResponseEntity.ok(lotes);
    }
}
