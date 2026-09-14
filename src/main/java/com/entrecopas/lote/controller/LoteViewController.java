package com.entrecopas.lote.controller;

import com.entrecopas.lote.dto.CreateLoteRequest;
import com.entrecopas.lote.dto.LoteDTO;
import com.entrecopas.lote.dto.TrazabilidadPublicaDTO;
import com.entrecopas.lote.service.LoteService;
import com.entrecopas.producto.dto.ProductoDTO;
import com.entrecopas.producto.service.ProductoService;
import com.entrecopas.usuario.service.UsuarioService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Spring MVC para vistas JSP de trazabilidad y gestión de lotes.
 */
@Controller
public class LoteViewController {

    private final LoteService loteService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public LoteViewController(LoteService loteService,
                              ProductoService productoService,
                              UsuarioService usuarioService) {
        this.loteService = loteService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Ficha oficial pública de trazabilidad de un lote por código (JSP: trazabilidad.jsp).
     */
    @GetMapping("/trazabilidad/{codigo}")
    public String verTrazabilidad(@PathVariable String codigo, Model model) {
        try {
            TrazabilidadPublicaDTO trazabilidad = loteService.obtenerTrazabilidadPublica(codigo);
            model.addAttribute("trazabilidad", trazabilidad);
            model.addAttribute("codigoBuscado", codigo);
        } catch (Exception ex) {
            model.addAttribute("mensajeError", "No se encontró ningún lote con el código especificado: " + codigo);
        }
        model.addAttribute("paginaActiva", "trazabilidad");
        return "trazabilidad";
    }

    /**
     * Consulta rápida de trazabilidad con buscador (JSP: trazabilidad.jsp).
     */
    @GetMapping("/trazabilidad")
    public String consultarTrazabilidad(
            @RequestParam(value = "codigo", required = false) String codigo,
            Model model) {
        if (codigo != null && !codigo.trim().isEmpty()) {
            return "redirect:/trazabilidad/" + codigo.trim().toUpperCase();
        }
        model.addAttribute("paginaActiva", "trazabilidad");
        return "trazabilidad";
    }

    /**
     * Panel privado del productor: Historial de lotes producidos (JSP: productor/mis-lotes.jsp).
     */
    @GetMapping("/panel/productor/lotes")
    public String verMisLotes(Model model) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);
        List<LoteDTO> lotes = loteService.listarLotesDelProductor(usuarioId);
        model.addAttribute("lotes", lotes);
        model.addAttribute("paginaActiva", "mis-lotes");
        return "productor/mis-lotes";
    }

    /**
     * Formulario de alta para un nuevo lote productivo (JSP: productor/nuevo-lote.jsp).
     */
    @GetMapping("/panel/productor/lotes/nuevo")
    public String mostrarFormularioNuevoLote(Model model) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);
        List<ProductoDTO> misProductos = productoService.listarProductosDelProductor(usuarioId);

        model.addAttribute("productos", misProductos);
        model.addAttribute("loteForm", new CreateLoteRequest());
        model.addAttribute("paginaActiva", "nuevo-lote");
        return "productor/nuevo-lote";
    }

    /**
     * Procesa la creación de un nuevo lote desde el formulario JSP.
     */
    @PostMapping("/panel/productor/lotes/nuevo")
    public String procesarNuevoLote(
            @ModelAttribute("loteForm") CreateLoteRequest form,
            RedirectAttributes redirectAttributes) {
        try {
            Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);
            LoteDTO creado = loteService.crearLote(usuarioId, form);
            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "¡Lote registrado exitosamente! Código generado: " + creado.getCodigoTrazabilidad()
            );
            return "redirect:/panel/productor/lotes";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/panel/productor/lotes/nuevo";
        }
    }
}
