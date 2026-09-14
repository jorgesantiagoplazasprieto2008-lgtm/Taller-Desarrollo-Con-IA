package com.entrecopas.producto.controller;

import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.producto.dto.CreateProductoRequest;
import com.entrecopas.producto.dto.ProductoDTO;
import com.entrecopas.producto.dto.UpdateEstadoRequest;
import com.entrecopas.producto.service.ProductoService;
import com.entrecopas.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el catálogo público y la gestión de productos del productor.
 */
@RestController
@RequestMapping("/api/v1")
public class ProductoRestController {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public ProductoRestController(ProductoService productoService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    // =========================================================================
    // ENDPOINTS PÚBLICOS DEL CATÁLOGO
    // =========================================================================

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> obtenerCatalogo(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String q) {
        List<ProductoDTO> productos = productoService.obtenerProductosActivos(tipo, q);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> obtenerDetalleProducto(@PathVariable Long id) {
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    // =========================================================================
    // ENDPOINTS PRIVADOS DEL PRODUCTOR AUTENTICADO
    // =========================================================================

    @GetMapping("/productor/productos")
    public ResponseEntity<List<ProductoDTO>> listarMisProductos() {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId()
                .orElseThrow(() -> new UnauthorizedDomainException("Debe iniciar sesión como productor."));
        List<ProductoDTO> productos = productoService.listarProductosDelProductor(usuarioId);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/productor/productos")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody CreateProductoRequest request) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId()
                .orElseThrow(() -> new UnauthorizedDomainException("Debe iniciar sesión como productor."));
        ProductoDTO creado = productoService.crearProducto(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/productor/productos/{id}/estado")
    public ResponseEntity<ProductoDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoRequest request) {
        Long usuarioId = usuarioService.obtenerUsuarioAutenticadoId()
                .orElseThrow(() -> new UnauthorizedDomainException("Debe iniciar sesión como productor."));
        ProductoDTO actualizado = productoService.cambiarEstado(usuarioId, id, request.getNuevoEstado());
        return ResponseEntity.ok(actualizado);
    }
}
