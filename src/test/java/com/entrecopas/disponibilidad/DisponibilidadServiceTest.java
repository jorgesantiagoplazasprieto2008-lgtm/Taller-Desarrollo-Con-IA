package com.entrecopas.disponibilidad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.entrecopas.disponibilidad.dto.DisponibilidadProductoDTO;
import com.entrecopas.disponibilidad.dto.EstablecimientoLocalDTO;
import com.entrecopas.disponibilidad.model.Disponibilidad;
import com.entrecopas.disponibilidad.repository.DisponibilidadRepository;
import com.entrecopas.disponibilidad.service.DisponibilidadService;
import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.establecimiento.repository.EstablecimientoRepository;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.usuario.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para DisponibilidadService.
 * Valida la gestión de stock local, aislamiento multi-tenant y consulta pública.
 */
@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private EstablecimientoRepository establecimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    private Usuario usuarioHosteleria;
    private Establecimiento establecimiento;
    private Productor productor;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        usuarioHosteleria = new Usuario(2L, "gerencia@rincongourmet.com", "pass", "Rincón Gourmet");
        establecimiento = new Establecimiento(10L, usuarioHosteleria, "Rincón Gourmet Bar", "Calle 93 # 12-40", "Bogotá");

        Usuario usuarioProductor = new Usuario(1L, "contacto@bodega.com", "pass", "Bodega San Gabriel");
        productor = new Productor(5L, usuarioProductor, "Bodega San Gabriel", "Villa de Leyva");

        producto1 = new Producto();
        producto1.setId(101L);
        producto1.setNombre("Reserva Especial Malbec");
        producto1.setTipoBebida("Vino Tinto");
        producto1.setPresentacion("Botella 750ml");
        producto1.setPrecio(new BigDecimal("65000.00"));
        producto1.setEstado(EstadoProducto.ACTIVO);
        producto1.setProductor(productor);

        producto2 = new Producto();
        producto2.setId(102L);
        producto2.setNombre("Cerveza Imperial Stout");
        producto2.setTipoBebida("Cerveza Artesanal");
        producto2.setPresentacion("Botella 330ml");
        producto2.setPrecio(new BigDecimal("18000.00"));
        producto2.setEstado(EstadoProducto.ACTIVO);
        producto2.setProductor(productor);
    }

    @Test
    @DisplayName("Debe listar productos para hostelería mapeando correctamente disponibilidad")
    void testListarProductosParaHosteleria() {
        when(establecimientoRepository.findByUsuarioId(2L)).thenReturn(Optional.of(establecimiento));
        when(productoRepository.findByEstado(EstadoProducto.ACTIVO)).thenReturn(List.of(producto1, producto2));

        Disponibilidad disp1 = new Disponibilidad(1L, producto1, establecimiento, true);
        disp1.setUpdatedAt(LocalDateTime.now());
        when(disponibilidadRepository.findByEstablecimientoId(10L)).thenReturn(List.of(disp1));

        List<DisponibilidadProductoDTO> resultado = disponibilidadService.listarProductosParaHosteleria(2L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        DisponibilidadProductoDTO dto1 = resultado.stream()
                .filter(p -> p.getProductoId().equals(101L))
                .findFirst()
                .orElse(null);
        assertNotNull(dto1);
        assertTrue(dto1.isDisponible(), "El producto 101 debe estar marcado como disponible en barra");

        DisponibilidadProductoDTO dto2 = resultado.stream()
                .filter(p -> p.getProductoId().equals(102L))
                .findFirst()
                .orElse(null);
        assertNotNull(dto2);
        assertFalse(dto2.isDisponible(), "El producto 102 debe estar sin stock por defecto");
    }

    @Test
    @DisplayName("Debe fallar al listar productos si el usuario no tiene establecimiento configurado")
    void testListarProductosSinEstablecimientoLanzaExcepcion() {
        when(establecimientoRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            disponibilidadService.listarProductosParaHosteleria(99L);
        });
    }

    @Test
    @DisplayName("Debe crear nueva Disponibilidad cuando no existía previamente para el local")
    void testAlternarDisponibilidadCreacionNueva() {
        when(establecimientoRepository.findByUsuarioId(2L)).thenReturn(Optional.of(establecimiento));
        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));
        when(disponibilidadRepository.findByProductoIdAndEstablecimientoId(101L, 10L)).thenReturn(Optional.empty());
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(inv -> inv.getArgument(0));

        Disponibilidad resultado = disponibilidadService.alternarDisponibilidad(2L, 101L, true);

        assertNotNull(resultado);
        assertTrue(resultado.getDisponible());
        assertEquals(producto1, resultado.getProducto());
        assertEquals(establecimiento, resultado.getEstablecimiento());
        verify(disponibilidadRepository).save(any(Disponibilidad.class));
    }

    @Test
    @DisplayName("Debe actualizar Disponibilidad existente a falso (agotado)")
    void testAlternarDisponibilidadActualizarExistente() {
        Disponibilidad dispExistente = new Disponibilidad(1L, producto1, establecimiento, true);

        when(establecimientoRepository.findByUsuarioId(2L)).thenReturn(Optional.of(establecimiento));
        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));
        when(disponibilidadRepository.findByProductoIdAndEstablecimientoId(101L, 10L))
                .thenReturn(Optional.of(dispExistente));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(inv -> inv.getArgument(0));

        Disponibilidad resultado = disponibilidadService.alternarDisponibilidad(2L, 101L, false);

        assertNotNull(resultado);
        assertFalse(resultado.getDisponible());
        verify(disponibilidadRepository).save(dispExistente);
    }

    @Test
    @DisplayName("Debe consultar locales públicos con stock disponible en barra")
    void testObtenerLocalesConDisponibilidad() {
        Disponibilidad disp = new Disponibilidad(1L, producto1, establecimiento, true);
        disp.setUpdatedAt(LocalDateTime.now());

        when(disponibilidadRepository.findByProductoIdAndDisponibleTrue(101L))
                .thenReturn(List.of(disp));

        List<EstablecimientoLocalDTO> locales = disponibilidadService.obtenerLocalesConDisponibilidad(101L);

        assertNotNull(locales);
        assertEquals(1, locales.size());
        assertEquals("Rincón Gourmet Bar", locales.get(0).getNombre());
        assertEquals("Bogotá", locales.get(0).getCiudad());
        assertTrue(locales.get(0).isDisponible());
    }

    @Test
    @DisplayName("Debe retornar lista vacía si ningún local tiene stock disponible")
    void testObtenerLocalesConDisponibilidadVacia() {
        when(disponibilidadRepository.findByProductoIdAndDisponibleTrue(102L))
                .thenReturn(Collections.emptyList());

        List<EstablecimientoLocalDTO> locales = disponibilidadService.obtenerLocalesConDisponibilidad(102L);

        assertNotNull(locales);
        assertTrue(locales.isEmpty());
    }
}
