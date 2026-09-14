package com.entrecopas.producto;

import com.entrecopas.exception.BusinessException;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.producto.dto.CreateProductoRequest;
import com.entrecopas.producto.dto.ProductoDTO;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.producto.service.ProductoService;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import com.entrecopas.usuario.model.Usuario;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoService y verificación del aislamiento multi-tenant.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductorRepository productorRepository;

    @InjectMocks
    private ProductoService productoService;

    private Productor productorA;
    private Productor productorB;
    private Producto productoA;

    @BeforeEach
    void setUp() {
        Usuario usuarioA = new Usuario(1L, "productorA@test.com", "pass", "Productor A");
        productorA = new Productor(10L, usuarioA, "Bodega A", "Valle de Leyva");

        Usuario usuarioB = new Usuario(2L, "productorB@test.com", "pass", "Productor B");
        productorB = new Productor(20L, usuarioB, "Cervecería B", "Medellín");

        productoA = new Producto(100L, productorA, "Vino Tinto Roble", "Vino",
                "Notas a frutos rojos y roble", "Botella 750ml", new BigDecimal("45000.00"), EstadoProducto.ACTIVO);
    }

    @Test
    @DisplayName("Debe listar únicamente productos con estado ACTIVO para el catálogo público")
    void testObtenerProductosActivos() {
        when(productoRepository.buscarActivos(EstadoProducto.ACTIVO, null, null))
                .thenReturn(List.of(productoA));

        List<ProductoDTO> resultado = productoService.obtenerProductosActivos(null, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Vino Tinto Roble", resultado.get(0).getNombre());
        assertEquals(EstadoProducto.ACTIVO, resultado.get(0).getEstado());
        verify(productoRepository).buscarActivos(EstadoProducto.ACTIVO, null, null);
    }

    @Test
    @DisplayName("Debe crear producto asociado estrictamente al productor autenticado")
    void testCrearProductoExitoso() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productorA));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            p.setId(101L);
            return p;
        });

        CreateProductoRequest request = new CreateProductoRequest(
                "Hidromiel Ancestral", "Hidromiel", "Miel pura", "Botella 500ml", new BigDecimal("35000.00"));

        ProductoDTO dto = productoService.crearProducto(1L, request);

        assertNotNull(dto);
        assertEquals(101L, dto.getId());
        assertEquals("Hidromiel Ancestral", dto.getNombre());
        assertEquals(10L, dto.getProductorId());
        assertEquals(EstadoProducto.ACTIVO, dto.getEstado());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe rechazar la creación de producto con precio menor o igual a cero")
    void testCrearProductoPrecioInvalido() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productorA));

        CreateProductoRequest request = new CreateProductoRequest(
                "Invalido", "Vino", "Desc", "750ml", BigDecimal.ZERO);

        assertThrows(BusinessException.class, () -> productoService.crearProducto(1L, request));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe cambiar el estado del producto si pertenece al productor en sesión")
    void testCambiarEstadoProductorAutorizado() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productorA));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoA));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoDTO actualizado = productoService.cambiarEstado(1L, 100L, EstadoProducto.PAUSADO);

        assertNotNull(actualizado);
        assertEquals(EstadoProducto.PAUSADO, actualizado.getEstado());
        verify(productoRepository).save(productoA);
    }

    @Test
    @DisplayName("BARRERA MULTI-TENANT: Debe bloquear con 403 (UnauthorizedDomainException) si el producto es de otro productor")
    void testCambiarEstadoBloqueoMultiTenant() {
        // El usuario 2 (Productor B) intenta modificar el producto 100 de Productor A
        when(productorRepository.findByUsuarioId(2L)).thenReturn(Optional.of(productorB));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoA));

        UnauthorizedDomainException exception = assertThrows(
                UnauthorizedDomainException.class,
                () -> productoService.cambiarEstado(2L, 100L, EstadoProducto.RETIRADO)
        );

        assertTrue(exception.getMessage().contains("No tiene permisos para modificar productos de otro productor"));
        verify(productoRepository, never()).save(any(Producto.class));
    }
}
