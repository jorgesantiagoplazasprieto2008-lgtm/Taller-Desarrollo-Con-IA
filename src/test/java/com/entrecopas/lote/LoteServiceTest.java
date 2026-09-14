package com.entrecopas.lote;

import com.entrecopas.exception.BusinessException;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.lote.dto.CreateLoteRequest;
import com.entrecopas.lote.dto.LoteDTO;
import com.entrecopas.lote.dto.TrazabilidadPublicaDTO;
import com.entrecopas.lote.model.Lote;
import com.entrecopas.lote.model.ParametrosAnaliticos;
import com.entrecopas.lote.repository.LoteRepository;
import com.entrecopas.lote.service.CodigoTrazabilidadGenerator;
import com.entrecopas.lote.service.LoteService;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import com.entrecopas.usuario.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
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
 * Pruebas unitarias para LoteService: algoritmo de trazabilidad, mermas,
 * barrera multi-tenant y enmascaramiento estricto de secretos industriales.
 */
@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductorRepository productorRepository;

    @Mock
    private CodigoTrazabilidadGenerator codigoGenerator;

    @InjectMocks
    private LoteService loteService;

    private Productor productorA;
    private Productor productorB;
    private Producto productoA;
    private Lote loteA;

    @BeforeEach
    void setUp() {
        Usuario usuarioA = new Usuario(1L, "bodegaA@test.com", "pass", "Bodega A");
        productorA = new Productor(10L, usuarioA, "Bodega San Gabriel", "Villa de Leyva");

        Usuario usuarioB = new Usuario(2L, "cervezaB@test.com", "pass", "Cervecería B");
        productorB = new Productor(20L, usuarioB, "Cerveza Artesanal Andina", "Medellín");

        productoA = new Producto(100L, productorA, "Vino Tinto Roble", "Vino",
                "Notas a mora y roble", "Botella 750ml", new BigDecimal("45000.00"), EstadoProducto.ACTIVO);

        ParametrosAnaliticos params = new ParametrosAnaliticos(3.65, 13.5, 5.80, 0.994);
        loteA = new Lote(500L, productoA, "EC-2026-PR10-L001", LocalDate.of(2026, 9, 7),
                new BigDecimal("1000.00"), new BigDecimal("50.00"), new BigDecimal("6000000.00"), params);
    }

    @Test
    @DisplayName("Debe registrar un nuevo lote generando código algorítmico y calculando mermas")
    void testCrearLoteExitoso() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productorA));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoA));
        when(codigoGenerator.generarCodigo(10L, LocalDate.of(2026, 9, 7))).thenReturn("EC-2026-PR10-L001");
        when(loteRepository.save(any(Lote.class))).thenAnswer(invocation -> {
            Lote l = invocation.getArgument(0);
            l.setId(501L);
            return l;
        });

        CreateLoteRequest request = new CreateLoteRequest(
                100L, LocalDate.of(2026, 9, 7),
                new BigDecimal("1000.00"), new BigDecimal("50.00"), new BigDecimal("6000000.00"),
                3.65, 13.5, 5.80, 0.994
        );

        LoteDTO dto = loteService.crearLote(1L, request);

        assertNotNull(dto);
        assertEquals("EC-2026-PR10-L001", dto.getCodigoTrazabilidad());
        assertEquals(new BigDecimal("5.00"), dto.getPorcentajeMerma());
        assertEquals(new BigDecimal("6315.79"), dto.getCostoPorLitro()); // 6,000,000 / 950 L útiles
        verify(loteRepository).save(any(Lote.class));
    }

    @Test
    @DisplayName("Debe rechazar la creación si la merma es mayor al volumen total del lote")
    void testCrearLoteMermaExcesiva() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productorA));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoA));

        CreateLoteRequest request = new CreateLoteRequest(
                100L, LocalDate.of(2026, 9, 7),
                new BigDecimal("100.00"), new BigDecimal("150.00"), new BigDecimal("1000000.00"),
                3.65, 13.5, 5.80, 0.994
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> loteService.crearLote(1L, request));
        assertTrue(ex.getMessage().contains("La merma en litros no puede exceder el volumen total"));
        verify(loteRepository, never()).save(any(Lote.class));
    }

    @Test
    @DisplayName("BARRERA MULTI-TENANT: Bloquea con 403 si el productor intenta registrar lotes de un producto ajeno")
    void testCrearLoteBarreraMultiTenant() {
        // Usuario 2 (Productor B) intenta crear lote para productoA (de Productor A)
        when(productorRepository.findByUsuarioId(2L)).thenReturn(Optional.of(productorB));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoA));

        CreateLoteRequest request = new CreateLoteRequest(
                100L, LocalDate.of(2026, 9, 7),
                new BigDecimal("500.00"), new BigDecimal("20.00"), new BigDecimal("2000000.00"),
                3.65, 13.5, 5.80, 0.994
        );

        UnauthorizedDomainException ex = assertThrows(
                UnauthorizedDomainException.class,
                () -> loteService.crearLote(2L, request)
        );
        assertTrue(ex.getMessage().contains("No tiene permisos para registrar lotes de un producto ajeno"));
        verify(loteRepository, never()).save(any(Lote.class));
    }

    @Test
    @DisplayName("ENMASCARAMIENTO DE SECRETOS (PRD): Trazabilidad pública no expone costos ni mermas")
    void testTrazabilidadPublicaEnmascaraSecretos() {
        when(loteRepository.findByCodigoTrazabilidad("EC-2026-PR10-L001")).thenReturn(Optional.of(loteA));

        TrazabilidadPublicaDTO dto = loteService.obtenerTrazabilidadPublica("EC-2026-PR10-L001");

        assertNotNull(dto);
        assertEquals("EC-2026-PR10-L001", dto.getCodigoTrazabilidad());
        assertEquals("Vino Tinto Roble", dto.getProductoNombre());
        assertEquals("Bodega San Gabriel", dto.getProductorNombre());
        assertEquals("Villa de Leyva", dto.getProductorUbicacion());
        assertEquals(3.65, dto.getParametrosAnaliticos().getPh());
        assertEquals(13.5, dto.getParametrosAnaliticos().getGraduacionAlcoholica());
        assertTrue(dto.isCertificadoValido());

        // Verificación de integridad: la clase TrazabilidadPublicaDTO no cuenta con getters de costo ni merma
        assertFalse(TrazabilidadPublicaDTO.class.toGenericString().contains("getCostoTotal"));
        assertFalse(TrazabilidadPublicaDTO.class.toGenericString().contains("getMermaLitros"));
    }

    @Test
    @DisplayName("Debe lanzar 404 si el código de trazabilidad no existe")
    void testTrazabilidadCodigoInexistente() {
        when(loteRepository.findByCodigoTrazabilidad("EC-INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loteService.obtenerTrazabilidadPublica("EC-INEXISTENTE"));
    }
}
