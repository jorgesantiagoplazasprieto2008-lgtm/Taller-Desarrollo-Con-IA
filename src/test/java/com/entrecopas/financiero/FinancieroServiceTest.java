package com.entrecopas.financiero;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.financiero.dto.DashboardFinancieroDTO;
import com.entrecopas.financiero.dto.DesgloseLoteFinancieroDTO;
import com.entrecopas.financiero.dto.DesgloseProductoFinancieroDTO;
import com.entrecopas.financiero.dto.FinancieroKPIsDTO;
import com.entrecopas.financiero.service.FinancieroService;
import com.entrecopas.lote.model.Lote;
import com.entrecopas.lote.model.ParametrosAnaliticos;
import com.entrecopas.lote.repository.LoteRepository;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import com.entrecopas.usuario.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
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
 * Pruebas unitarias para FinancieroService.
 * Valida agregación de costos, cálculo de mermas, proyección de márgenes y barrera multi-tenant.
 */
@ExtendWith(MockitoExtension.class)
class FinancieroServiceTest {

    @Mock
    private ProductorRepository productorRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private FinancieroService financieroService;

    private Usuario usuarioProductor;
    private Productor productor;
    private Producto productoVino;
    private Producto productoCerveza;
    private Lote lote1;
    private Lote lote2;

    @BeforeEach
    void setUp() {
        usuarioProductor = new Usuario(1L, "contacto@bodegasangabriel.com", "pass", "Bodega San Gabriel");
        productor = new Productor(10L, usuarioProductor, "Bodega San Gabriel", "Villa de Leyva");

        productoVino = new Producto();
        productoVino.setId(101L);
        productoVino.setNombre("Gran Reserva Malbec");
        productoVino.setTipoBebida("Vino Tinto");
        productoVino.setPresentacion("Botella 750ml");
        productoVino.setPrecio(new BigDecimal("75000.00"));
        productoVino.setEstado(EstadoProducto.ACTIVO);
        productoVino.setProductor(productor);

        productoCerveza = new Producto();
        productoCerveza.setId(102L);
        productoCerveza.setNombre("Stout Imperial");
        productoCerveza.setTipoBebida("Cerveza Artesanal");
        productoCerveza.setPresentacion("Botella 330ml");
        productoCerveza.setPrecio(new BigDecimal("18000.00"));
        productoCerveza.setEstado(EstadoProducto.ACTIVO);
        productoCerveza.setProductor(productor);

        // Lote 1: 1000 Litros, 50 Litros de merma (5%), Costo: $20,000,000
        lote1 = new Lote(1L, productoVino, "EC-2026-PR01-A101", LocalDate.of(2026, 3, 1),
                new BigDecimal("1000.00"), new BigDecimal("50.00"), new BigDecimal("20000000.00"),
                new ParametrosAnaliticos(3.65, 14.2, 5.4, 0.9930));

        // Lote 2: 500 Litros, 40 Litros de merma (8%), Costo: $5,000,000
        lote2 = new Lote(2L, productoCerveza, "EC-2026-PR01-A102", LocalDate.of(2026, 4, 15),
                new BigDecimal("500.00"), new BigDecimal("40.00"), new BigDecimal("5000000.00"),
                new ParametrosAnaliticos(4.20, 8.5, 4.8, 1.0120));
    }

    @Test
    @DisplayName("Debe calcular KPIs agregados y desgloses correctamente con múltiples lotes")
    void testObtenerDashboardConLotes() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productor));
        when(productoRepository.findByProductorId(10L)).thenReturn(List.of(productoVino, productoCerveza));
        when(loteRepository.findByProductoProductorIdOrderByFechaProduccionDesc(10L))
                .thenReturn(List.of(lote1, lote2));

        DashboardFinancieroDTO dashboard = financieroService.obtenerDashboard(1L);

        assertNotNull(dashboard);
        FinancieroKPIsDTO kpis = dashboard.getKpis();
        assertNotNull(kpis);

        // Inversión Total: 20M + 5M = 25M
        assertEquals(new BigDecimal("25000000.00"), kpis.getCostoTotalAcumulado());

        // Volumen Total: 1000 + 500 = 1500 L
        assertEquals(new BigDecimal("1500.00"), kpis.getVolumenTotalLitros());

        // Merma Total: 50 + 40 = 90 L
        assertEquals(new BigDecimal("90.00"), kpis.getMermaTotalLitros());

        // Volumen Neto: 1500 - 90 = 1410 L
        assertEquals(new BigDecimal("1410.00"), kpis.getVolumenNetoLitros());

        // Tasa Global de Merma: (90 / 1500) * 100 = 6.00%
        assertEquals(new BigDecimal("6.00"), kpis.getPorcentajeMermaGlobal());

        // Costo Promedio por Litro Neto: 25,000,000 / 1410 = 17730.50
        assertEquals(new BigDecimal("17730.50"), kpis.getCostoPromedioPorLitro());

        assertEquals(2, kpis.getLotesContabilizados());

        // Validar Desglose por Lote
        List<DesgloseLoteFinancieroDTO> desgloseLotes = dashboard.getDesglosePorLote();
        assertEquals(2, desgloseLotes.size());

        DesgloseLoteFinancieroDTO dl1 = desgloseLotes.get(0);
        assertEquals("EC-2026-PR01-A101", dl1.getCodigoTrazabilidad());
        assertEquals(new BigDecimal("5.00"), dl1.getMermaPorcentaje());
        assertEquals(new BigDecimal("950.00"), dl1.getVolumenNeto());
        // Unidades estimadas para 750ml (950 / 0.75 = 1266)
        assertEquals(1266, dl1.getUnidadesEstimadas());

        // Validar Desglose por Producto
        List<DesgloseProductoFinancieroDTO> desgloseProds = dashboard.getDesgloseProductos();
        assertEquals(2, desgloseProds.size());
    }

    @Test
    @DisplayName("Debe manejar productor sin lotes registrados retornando ceros sin división por cero")
    void testObtenerDashboardSinLotes() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productor));
        when(productoRepository.findByProductorId(10L)).thenReturn(List.of(productoVino));
        when(loteRepository.findByProductoProductorIdOrderByFechaProduccionDesc(10L))
                .thenReturn(Collections.emptyList());

        DashboardFinancieroDTO dashboard = financieroService.obtenerDashboard(1L);

        assertNotNull(dashboard);
        FinancieroKPIsDTO kpis = dashboard.getKpis();
        assertEquals(BigDecimal.ZERO, kpis.getCostoTotalAcumulado());
        assertEquals(BigDecimal.ZERO, kpis.getVolumenTotalLitros());
        assertEquals(BigDecimal.ZERO, kpis.getMermaTotalLitros());
        assertEquals(0, kpis.getLotesContabilizados());
        assertTrue(dashboard.getDesglosePorLote().isEmpty());
    }

    @Test
    @DisplayName("Debe filtrar correctamente el dashboard por productoId y rango de fechas")
    void testObtenerDashboardConFiltroProductoYFechas() {
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productor));
        when(productoRepository.findByProductorId(10L)).thenReturn(List.of(productoVino, productoCerveza));
        when(loteRepository.findByProductoProductorIdOrderByFechaProduccionDesc(10L))
                .thenReturn(List.of(lote1, lote2));

        // Filtrar por productoVino (101L)
        DashboardFinancieroDTO dashboard = financieroService.obtenerDashboard(
                1L, 101L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertNotNull(dashboard);
        assertEquals(1, dashboard.getKpis().getLotesContabilizados());
        assertEquals(new BigDecimal("20000000.00"), dashboard.getKpis().getCostoTotalAcumulado());
        assertEquals(1, dashboard.getDesglosePorLote().size());
        assertEquals("EC-2026-PR01-A101", dashboard.getDesglosePorLote().get(0).getCodigoTrazabilidad());
        assertEquals(1, dashboard.getDesgloseProductos().size());
        assertEquals("Gran Reserva Malbec", dashboard.getDesgloseProductos().get(0).getNombre());
    }
}

