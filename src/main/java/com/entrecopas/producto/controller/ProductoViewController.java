package com.entrecopas.producto.controller;

import com.entrecopas.disponibilidad.service.DisponibilidadService;
import com.entrecopas.producto.dto.CreateProductoRequest;
import com.entrecopas.producto.dto.ProductoDTO;
import com.entrecopas.producto.model.EstadoProducto;
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
 * Controlador Spring MVC para la navegación web y renderizado de vistas JSP.
 */
@Controller
public class ProductoViewController {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final DisponibilidadService disponibilidadService;

    public ProductoViewController(ProductoService productoService,
                                  UsuarioService usuarioService,
                                  DisponibilidadService disponibilidadService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.disponibilidadService = disponibilidadService;
    }

    /**
     * Catálogo público de bebidas artesanales (JSP: catalogo.jsp).
     */
    @GetMapping({"/", "/catalogo"})
    public String verCatalogo(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String q,
            Model model) {
        List<ProductoDTO> productos = productoService.obtenerProductosActivos(tipo, q);
        model.addAttribute("productos", productos);
        model.addAttribute("tipoActual", tipo != null ? tipo : "");
        model.addAttribute("busquedaActual", q != null ? q : "");
        model.addAttribute("paginaActiva", "catalogo");
        return "catalogo";
    }

    /**
     * Ficha técnica detallada de una bebida artesanal (JSP: producto-detalle.jsp).
     */
    @GetMapping("/productos/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model) {
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("localesDisponibles", disponibilidadService.obtenerLocalesConDisponibilidad(id));
        model.addAttribute("paginaActiva", "catalogo");
        return "producto-detalle";
    }

    /**
     * Panel privado del productor para gestionar su inventario (JSP: productor/mis-productos.jsp).
     */
    @GetMapping("/panel/productor/productos")
    public String verMisProductos(Model model) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId()
                .orElse(1L); // Fallback amigable para demostración
        List<ProductoDTO> productos = productoService.listarProductosDelProductor(usuarioId);
        model.addAttribute("productos", productos);
        model.addAttribute("paginaActiva", "mis-productos");
        return "productor/mis-productos";
    }

    /**
     * Formulario de alta para una nueva bebida (JSP: productor/nuevo-producto.jsp).
     */
    @GetMapping("/panel/productor/productos/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("productoForm", new CreateProductoRequest());
        model.addAttribute("paginaActiva", "nuevo-producto");
        return "productor/nuevo-producto";
    }

    /**
     * Procesa la creación de un nuevo producto artesanal desde el formulario JSP.
     */
    @PostMapping("/panel/productor/productos/nuevo")
    public String procesarNuevoProducto(
            @ModelAttribute("productoForm") CreateProductoRequest form,
            RedirectAttributes redirectAttributes) {
        try {
            Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);
            productoService.crearProducto(usuarioId, form);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Producto registrado exitosamente en el catálogo!");
            return "redirect:/panel/productor/productos";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/panel/productor/productos/nuevo";
        }
    }

    /**
     * Cambia el estado de un producto desde el panel JSP (ACTIVO / PAUSADO / RETIRADO).
     */
    @PostMapping("/panel/productor/productos/{id}/estado")
    public String procesarCambioEstado(
            @PathVariable Long id,
            @RequestParam("nuevoEstado") EstadoProducto nuevoEstado,
            RedirectAttributes redirectAttributes) {
        try {
            Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId().orElse(1L);
            productoService.cambiarEstado(usuarioId, id, nuevoEstado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado del producto actualizado a " + nuevoEstado);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/productor/productos";
    }
}
