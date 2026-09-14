package com.entrecopas.financiero.controller;

import com.entrecopas.financiero.dto.DashboardFinancieroDTO;
import com.entrecopas.financiero.service.FinancieroService;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import com.entrecopas.usuario.service.UsuarioService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador Spring MVC para el renderizado web del dashboard financiero (JSP).
 */
@Controller
public class FinancieroViewController {

    private final FinancieroService financieroService;
    private final ProductorRepository productorRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    public FinancieroViewController(FinancieroService financieroService,
                                   ProductorRepository productorRepository,
                                   ProductoRepository productoRepository,
                                   UsuarioService usuarioService) {
        this.financieroService = financieroService;
        this.productorRepository = productorRepository;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Renderiza el dashboard financiero de producción con filtros dinámicos (JSP: financiero/dashboard.jsp).
     */
    @GetMapping({"/panel/financiero", "/panel/productor/financiero"})
    public String verDashboardFinanciero(
            @RequestParam(value = "productoId", required = false) Long productoId,
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "fechaDesde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(value = "fechaHasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);

        Productor productor = productorRepository.findByUsuarioId(usuarioId).orElse(null);
        List<Producto> misProductos = productor != null
                ? productoRepository.findByProductorId(productor.getId())
                : Collections.emptyList();

        LocalDate desde = fechaDesde;
        LocalDate hasta = fechaHasta;
        if (periodo != null && !periodo.trim().isEmpty()) {
            LocalDate hoy = LocalDate.now();
            switch (periodo.toLowerCase()) {
                case "1m":
                    desde = hoy.minusMonths(1);
                    hasta = hoy;
                    break;
                case "3m":
                    desde = hoy.minusMonths(3);
                    hasta = hoy;
                    break;
                case "1y":
                    desde = hoy.minusYears(1);
                    hasta = hoy;
                    break;
                default:
                    break;
            }
        }

        DashboardFinancieroDTO dashboard = financieroService.obtenerDashboard(usuarioId, productoId, desde, hasta);

        model.addAttribute("productor", productor);
        model.addAttribute("productos", misProductos);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("selectedProductoId", productoId);
        model.addAttribute("selectedPeriodo", periodo);
        model.addAttribute("selectedFechaDesde", fechaDesde);
        model.addAttribute("selectedFechaHasta", fechaHasta);
        model.addAttribute("paginaActiva", "financiero");

        return "financiero/dashboard";
    }
}
