package com.entrecopas.financiero;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.entrecopas.exception.GlobalExceptionHandler;
import com.entrecopas.financiero.controller.FinancieroRestController;
import com.entrecopas.financiero.controller.FinancieroViewController;
import com.entrecopas.financiero.dto.DashboardFinancieroDTO;
import com.entrecopas.financiero.dto.FinancieroKPIsDTO;
import com.entrecopas.financiero.service.FinancieroService;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import com.entrecopas.usuario.model.Usuario;
import com.entrecopas.usuario.service.UsuarioService;
import java.math.BigDecimal;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Pruebas unitarias MockMvc para FinancieroRestController y FinancieroViewController.
 */
@ExtendWith(MockitoExtension.class)
class FinancieroControllerTest {

    private MockMvc restMockMvc;
    private MockMvc mvcMockMvc;

    @Mock
    private FinancieroService financieroService;

    @Mock
    private ProductorRepository productorRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private FinancieroRestController restController;

    @InjectMocks
    private FinancieroViewController viewController;

    private Productor productor;
    private DashboardFinancieroDTO dashboardDTO;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario(1L, "contacto@bodega.com", "pass", "Bodega San Gabriel");
        productor = new Productor(10L, usuario, "Bodega San Gabriel", "Villa de Leyva");

        FinancieroKPIsDTO kpis = new FinancieroKPIsDTO(
                new BigDecimal("25000000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("90.00"),
                new BigDecimal("1410.00"),
                new BigDecimal("6.00"),
                new BigDecimal("17730.50"),
                2
        );
        dashboardDTO = new DashboardFinancieroDTO(kpis, List.of(), List.of());

        restMockMvc = MockMvcBuilders.standaloneSetup(restController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mvcMockMvc = MockMvcBuilders.standaloneSetup(viewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("REST: GET /api/v1/financiero/resumen debe responder 200 OK con JSON de KPIs")
    void testObtenerResumenFinancieroRest() throws Exception {
        when(usuarioService.obtenerUsuarioAutenticadoId()).thenReturn(Optional.of(1L));
        when(financieroService.obtenerDashboard(1L, null, null, null)).thenReturn(dashboardDTO);

        restMockMvc.perform(get("/api/v1/financiero/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpis.costoTotalAcumulado").value(25000000.00))
                .andExpect(jsonPath("$.kpis.volumenTotalLitros").value(1500.00))
                .andExpect(jsonPath("$.kpis.lotesContabilizados").value(2));
    }

    @Test
    @DisplayName("MVC: GET /panel/financiero debe renderizar la vista JSP financiero/dashboard")
    void testVerDashboardFinancieroMvc() throws Exception {
        when(usuarioService.obtenerUsuarioAutenticadoId()).thenReturn(Optional.of(1L));
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productor));
        when(productoRepository.findByProductorId(10L)).thenReturn(Collections.emptyList());
        when(financieroService.obtenerDashboard(1L, null, null, null)).thenReturn(dashboardDTO);

        mvcMockMvc.perform(get("/panel/financiero"))
                .andExpect(status().isOk())
                .andExpect(view().name("financiero/dashboard"))
                .andExpect(model().attributeExists("productor"))
                .andExpect(model().attributeExists("dashboard"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attribute("paginaActiva", "financiero"));
    }

    @Test
    @DisplayName("MVC: GET /panel/financiero con filtros de producto y período debe procesar parámetros")
    void testVerDashboardFinancieroConFiltros() throws Exception {
        when(usuarioService.obtenerUsuarioAutenticadoId()).thenReturn(Optional.of(1L));
        when(productorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(productor));
        when(productoRepository.findByProductorId(10L)).thenReturn(Collections.emptyList());
        when(financieroService.obtenerDashboard(eq(1L), eq(5L), any(), any())).thenReturn(dashboardDTO);

        mvcMockMvc.perform(get("/panel/financiero")
                        .param("productoId", "5")
                        .param("periodo", "1m"))
                .andExpect(status().isOk())
                .andExpect(view().name("financiero/dashboard"))
                .andExpect(model().attribute("selectedProductoId", 5L))
                .andExpect(model().attribute("selectedPeriodo", "1m"));
    }
}
