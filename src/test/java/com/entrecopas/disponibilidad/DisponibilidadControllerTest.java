package com.entrecopas.disponibilidad;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.entrecopas.disponibilidad.controller.DisponibilidadRestController;
import com.entrecopas.disponibilidad.controller.HosteleriaViewController;
import com.entrecopas.disponibilidad.dto.DisponibilidadProductoDTO;
import com.entrecopas.disponibilidad.dto.EstablecimientoLocalDTO;
import com.entrecopas.disponibilidad.dto.ToggleDisponibilidadRequest;
import com.entrecopas.disponibilidad.model.Disponibilidad;
import com.entrecopas.disponibilidad.service.DisponibilidadService;
import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.establecimiento.service.EstablecimientoService;
import com.entrecopas.exception.GlobalExceptionHandler;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.usuario.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Pruebas unitarias con MockMvc para DisponibilidadRestController y HosteleriaViewController.
 */
@ExtendWith(MockitoExtension.class)
class DisponibilidadControllerTest {

    private MockMvc restMockMvc;
    private MockMvc mvcMockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DisponibilidadService disponibilidadService;

    @Mock
    private EstablecimientoService establecimientoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private DisponibilidadRestController restController;

    @InjectMocks
    private HosteleriaViewController viewController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

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
    @DisplayName("REST: POST /api/v1/hosteleria/disponibilidad/toggle debe responder 200 OK con JSON")
    void testToggleDisponibilidadRest() throws Exception {
        when(usuarioService.obtenerUsuarioHosteleriaAutenticadoId()).thenReturn(Optional.of(2L));

        Producto p = new Producto();
        p.setId(101L);
        Establecimiento e = new Establecimiento();
        e.setId(10L);

        Disponibilidad disp = new Disponibilidad(1L, p, e, true);
        disp.setUpdatedAt(LocalDateTime.now());

        when(disponibilidadService.alternarDisponibilidad(2L, 101L, true)).thenReturn(disp);

        ToggleDisponibilidadRequest request = new ToggleDisponibilidadRequest(101L, true);

        restMockMvc.perform(post("/api/v1/hosteleria/disponibilidad/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.productoId").value(101))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @DisplayName("REST: GET /api/v1/hosteleria/mis-productos debe retornar lista de productos y stock")
    void testListarProductosHosteleriaRest() throws Exception {
        when(usuarioService.obtenerUsuarioHosteleriaAutenticadoId()).thenReturn(Optional.of(2L));

        DisponibilidadProductoDTO dto = new DisponibilidadProductoDTO(
                101L, "Reserva Malbec", "Vino Tinto", "750ml", "Bodega San Gabriel",
                new BigDecimal("65000.00"), true, LocalDateTime.now()
        );
        when(disponibilidadService.listarProductosParaHosteleria(2L)).thenReturn(List.of(dto));

        restMockMvc.perform(get("/api/v1/hosteleria/mis-productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productoNombre").value("Reserva Malbec"))
                .andExpect(jsonPath("$[0].disponible").value(true));
    }

    @Test
    @DisplayName("REST: GET /api/v1/productos/{id}/disponibilidad debe retornar locales con stock activo")
    void testConsultarDisponibilidadPublicaRest() throws Exception {
        EstablecimientoLocalDTO local = new EstablecimientoLocalDTO(
                10L, "Rincón Gourmet", "Cra 7 # 45-20", "Bogotá", true, LocalDateTime.now()
        );
        when(disponibilidadService.obtenerLocalesConDisponibilidad(101L)).thenReturn(List.of(local));

        restMockMvc.perform(get("/api/v1/productos/101/disponibilidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Rincón Gourmet"))
                .andExpect(jsonPath("$[0].ciudad").value("Bogotá"));
    }

    @Test
    @DisplayName("MVC: GET /panel/hosteleria/disponibilidad debe renderizar vista hosteleria/disponibilidad")
    void testVerPanelHosteleriaMvc() throws Exception {
        when(usuarioService.obtenerUsuarioHosteleriaAutenticadoId()).thenReturn(Optional.of(2L));

        Establecimiento est = new Establecimiento();
        est.setId(10L);
        est.setNombre("Rincón Gourmet");
        when(establecimientoService.obtenerPorUsuario(2L)).thenReturn(est);

        when(disponibilidadService.listarProductosParaHosteleria(2L)).thenReturn(List.of());

        mvcMockMvc.perform(get("/panel/hosteleria/disponibilidad"))
                .andExpect(status().isOk())
                .andExpect(view().name("hosteleria/disponibilidad"))
                .andExpect(model().attributeExists("establecimiento"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attribute("paginaActiva", "hosteleria"));
    }

    @Test
    @DisplayName("MVC: GET /panel/hosteleria/disponibilidad sin local debe renderizar vista con advertencia y lista vacía")
    void testVerPanelHosteleriaSinEstablecimiento() throws Exception {
        when(usuarioService.obtenerUsuarioHosteleriaAutenticadoId()).thenReturn(Optional.of(99L));
        when(establecimientoService.obtenerPorUsuario(99L))
                .thenThrow(new com.entrecopas.exception.ResourceNotFoundException("No configurado"));

        mvcMockMvc.perform(get("/panel/hosteleria/disponibilidad"))
                .andExpect(status().isOk())
                .andExpect(view().name("hosteleria/disponibilidad"))
                .andExpect(model().attributeExists("advertencia"))
                .andExpect(model().attribute("productos", List.of()))
                .andExpect(model().attribute("paginaActiva", "hosteleria"));
    }

    @Test
    @DisplayName("MVC: POST /panel/hosteleria/disponibilidad/toggle debe redirigir con flash attribute")
    void testProcesarToggleMvc() throws Exception {
        when(usuarioService.obtenerUsuarioHosteleriaAutenticadoId()).thenReturn(Optional.of(2L));

        mvcMockMvc.perform(post("/panel/hosteleria/disponibilidad/toggle")
                        .param("productoId", "101")
                        .param("disponible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/panel/hosteleria/disponibilidad"))
                .andExpect(flash().attributeExists("mensajeExito"));
    }
}
