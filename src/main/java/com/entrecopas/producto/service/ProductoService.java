package com.entrecopas.producto.service;

import com.entrecopas.exception.BusinessException;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.producto.dto.CreateProductoRequest;
import com.entrecopas.producto.dto.ProductoDTO;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio para la gestión de bebidas artesanales con aislamiento multi-tenant estricto.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductorRepository productorRepository;

    public ProductoService(ProductoRepository productoRepository, ProductorRepository productorRepository) {
        this.productoRepository = productoRepository;
        this.productorRepository = productorRepository;
    }

    /**
     * Consulta pública del catálogo: solo retorna productos con estado ACTIVO.
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosActivos(String tipoBebida, String busqueda) {
        String tipo = (tipoBebida != null && !tipoBebida.trim().isEmpty()) ? tipoBebida.trim() : null;
        String q = (busqueda != null && !busqueda.trim().isEmpty()) ? busqueda.trim() : null;

        List<Producto> productos = productoRepository.buscarActivos(EstadoProducto.ACTIVO, tipo, q);
        return productos.stream().map(ProductoDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * Detalle público de un producto por ID.
     */
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return ProductoDTO.fromEntity(producto);
    }

    /**
     * Listado privado de productos de un productor autenticado.
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarProductosDelProductor(Long usuarioId) {
        Productor productor = obtenerProductorPorUsuario(usuarioId);
        return productoRepository.findByProductorIdOrderByCreatedAtDesc(productor.getId())
                .stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Registro de nuevo producto artesanal asegurando asignación al productor autenticado.
     */
    @Transactional
    public ProductoDTO crearProducto(Long usuarioId, CreateProductoRequest request) {
        Productor productor = obtenerProductorPorUsuario(usuarioId);

        if (request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio del producto debe ser mayor a cero.");
        }

        Producto producto = new Producto();
        producto.setProductor(productor);
        producto.setNombre(request.getNombre().trim());
        producto.setTipoBebida(request.getTipoBebida().trim());
        producto.setDescripcion(request.getDescripcion().trim());
        producto.setPresentacion(request.getPresentacion().trim());
        producto.setPrecio(request.getPrecio());
        producto.setEstado(EstadoProducto.ACTIVO);

        Producto guardado = productoRepository.save(producto);
        return ProductoDTO.fromEntity(guardado);
    }

    /**
     * Actualización de estado con barrera de seguridad multi-tenant.
     */
    @Transactional
    public ProductoDTO cambiarEstado(Long usuarioId, Long productoId, EstadoProducto nuevoEstado) {
        Productor productor = obtenerProductorPorUsuario(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        // Barrera no negociable de seguridad multi-tenant
        if (!producto.getProductor().getId().equals(productor.getId())) {
            throw new UnauthorizedDomainException("No tiene permisos para modificar productos de otro productor.");
        }

        producto.setEstado(nuevoEstado);
        Producto actualizado = productoRepository.save(producto);
        return ProductoDTO.fromEntity(actualizado);
    }

    private Productor obtenerProductorPorUsuario(Long usuarioId) {
        return productorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de productor no configurado para este usuario."));
    }
}
