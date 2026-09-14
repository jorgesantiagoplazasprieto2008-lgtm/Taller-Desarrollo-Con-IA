package com.entrecopas.disponibilidad.controller;

import com.entrecopas.disponibilidad.dto.DisponibilidadProductoDTO;
import com.entrecopas.disponibilidad.service.DisponibilidadService;
import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.establecimiento.service.EstablecimientoService;
import com.entrecopas.usuario.service.UsuarioService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Spring MVC para el panel de control de hostelería y disponibilidad en barra.
 */
@Controller
public class HosteleriaViewController {

    private final DisponibilidadService disponibilidadService;
    private final EstablecimientoService establecimientoService;
    private final UsuarioService usuarioService;

    public HosteleriaViewController(DisponibilidadService disponibilidadService,
                                   EstablecimientoService establecimientoService,
                                   UsuarioService usuarioService) {
        this.disponibilidadService = disponibilidadService;
        this.establecimientoService = establecimientoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Tablero de control de disponibilidad local para el hostelero (JSP: hosteleria/disponibilidad.jsp).
     */
    @GetMapping({"/panel/hosteleria", "/panel/hosteleria/disponibilidad"})
    public String verPanelHosteleria(Model model) {
        Long usuarioId = usuarioService.obtenerUsuarioHosteleriaAutenticadoId().orElse(2L);
        try {
            Establecimiento est = establecimientoService.obtenerPorUsuario(usuarioId);
            model.addAttribute("establecimiento", est);
            List<DisponibilidadProductoDTO> productos = disponibilidadService.listarProductosParaHosteleria(usuarioId);
            model.addAttribute("productos", productos);
        } catch (Exception ex) {
            model.addAttribute("advertencia", "Configure su local en el perfil antes de operar.");
            model.addAttribute("productos", java.util.Collections.emptyList());
        }

        model.addAttribute("paginaActiva", "hosteleria");
        return "hosteleria/disponibilidad";
    }

    /**
     * Procesa la actualización del interruptor de disponibilidad vía formulario estándar.
     */
    @PostMapping("/panel/hosteleria/disponibilidad/toggle")
    public String procesarToggleDisponibilidad(
            @RequestParam("productoId") Long productoId,
            @RequestParam(value = "disponible", defaultValue = "false") boolean disponible,
            RedirectAttributes redirectAttributes) {
        try {
            Long usuarioId = usuarioService.obtenerUsuarioHosteleriaAutenticadoId().orElse(2L);
            disponibilidadService.alternarDisponibilidad(usuarioId, productoId, disponible);
            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Disponibilidad actualizada exitosamente: " + (disponible ? "Disponible en Barra" : "Agotado / No disponible")
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/hosteleria/disponibilidad";
    }
}
