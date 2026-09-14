package com.entrecopas.financiero.controller;

import com.entrecopas.financiero.dto.DashboardFinancieroDTO;
import com.entrecopas.financiero.service.FinancieroService;
import com.entrecopas.usuario.service.UsuarioService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para consultar las métricas financieras agregadas del productor autenticado.
 */
@RestController
@RequestMapping("/api/v1/financiero")
public class FinancieroRestController {

    private final FinancieroService financieroService;
    private final UsuarioService usuarioService;

    public FinancieroRestController(FinancieroService financieroService, UsuarioService usuarioService) {
        this.financieroService = financieroService;
        this.usuarioService = usuarioService;
    }

    /**
     * Devuelve los KPIs agregados, desglose por lote y desglose por producto en formato JSON con filtros opcionales.
     */
    @GetMapping({"/resumen", "/metricas"})
    public ResponseEntity<DashboardFinancieroDTO> obtenerResumenFinanciero(
            @RequestParam(value = "productoId", required = false) Long productoId,
            @RequestParam(value = "fechaDesde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(value = "fechaHasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);
        DashboardFinancieroDTO dashboard = financieroService.obtenerDashboard(usuarioId, productoId, fechaDesde, fechaHasta);
        return ResponseEntity.ok(dashboard);
    }
}
