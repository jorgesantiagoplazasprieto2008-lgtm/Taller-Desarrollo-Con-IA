package com.entrecopas.lote;

import com.entrecopas.exception.GlobalExceptionHandler;
import com.entrecopas.lote.controller.LoteRestController;
import com.entrecopas.lote.controller.LoteViewController;
import com.entrecopas.lote.dto.CreateLoteRequest;
import com.entrecopas.lote.dto.LoteDTO;
import com.entrecopas.lote.dto.TrazabilidadPublicaDTO;
import com.entrecopas.lote.model.ParametrosAnaliticos;
import com.entrecopas.lote.service.LoteService;
import com.entrecopas.producto.service.ProductoService;
import com.entrecopas.usuario.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para controladores de trazabilidad y lotes (MVC y REST).
 */
@ExtendWith(MockitoExtension.class)
class LoteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LoteService loteService;

    @Mock
    private ProductoService productoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private LoteViewController viewController;

    @InjectMocks
    private LoteRestController restController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders
                .standaloneSetup(viewController, restController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("Debe resolver la vista JSP trazabilidad con HTTP 200 y certificado cargado")
    void testVerTrazabilidadJsp() throws Exception {
        TrazabilidadPublicaDTO dto = new TrazabilidadPublicaDTO();
        dto.setCodigoTrazabilidad("EC-2026-PR01-A101");
        dto.setProductoNombre("Vino Cabernet");
        dto.setParametrosAnaliticos(new ParametrosAnaliticos(3.60, 13.5, 5.5, 0.993));

        when(loteService.obtenerTrazabilidadPublica("EC-2026-PR01-A101")).thenReturn(dto);

        mockMvc.perform(get("/trazabilidad/EC-2026-PR01-A101"))
                .andExpect(status().isOk())
                .andExpect(view().name("trazabilidad"))
                .andExpect(model().attributeExists("trazabilidad"));
    }

    @Test
    @DisplayName("Debe retornar HTTP 200 y JSON certificado en /api/v1/trazabilidad/{codigo}")
    void testApiTrazabilidadJson() throws Exception {
        TrazabilidadPublicaDTO dto = new TrazabilidadPublicaDTO();
        dto.setCodigoTrazabilidad("EC-2026-PR01-A101");
        dto.setProductoNombre("Cerveza Stout Artesanal");
        dto.setParametrosAnaliticos(new ParametrosAnaliticos(4.20, 6.5, 3.8, 1.012));

        when(loteService.obtenerTrazabilidadPublica("EC-2026-PR01-A101")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/trazabilidad/EC-2026-PR01-A101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoTrazabilidad").value("EC-2026-PR01-A101"))
                .andExpect(jsonPath("$.productoNombre").value("Cerveza Stout Artesanal"))
                .andExpect(jsonPath("$.parametrosAnaliticos.ph").value(4.20))
                .andExpect(jsonPath("$.costoTotal").doesNotExist()) // Oculto por seguridad PRD
                .andExpect(jsonPath("$.mermaLitros").doesNotExist()); // Oculto por seguridad PRD
    }

    @Test
    @DisplayName("Debe crear lote vía API REST con HTTP 201 Created")
    void testApiCrearLoteExitoso() throws Exception {
        CreateLoteRequest request = new CreateLoteRequest(
                1L, LocalDate.of(2026, 9, 7),
                new BigDecimal("500.00"), new BigDecimal("20.00"), new BigDecimal("3000000.00"),
                3.70, 13.0, 5.6, 0.995
        );

        LoteDTO responseDto = new LoteDTO();
        responseDto.setId(99L);
        responseDto.setCodigoTrazabilidad("EC-2026-PR01-L001");
        responseDto.setVolumenLitros(new BigDecimal("500.00"));

        when(usuarioService.obtenerUsuarioAutenticadoId()).thenReturn(Optional.of(1L));
        when(loteService.crearLote(eq(1L), any(CreateLoteRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/productor/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.codigoTrazabilidad").value("EC-2026-PR01-L001"));
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando los parámetros analíticos violan las restricciones Jakarta")
    void testApiCrearLoteValidacionInvalida() throws Exception {
        // Enviar pH fuera de rango (1.0 < min 2.0) y volumen negativo
        CreateLoteRequest request = new CreateLoteRequest(
                1L, LocalDate.of(2026, 9, 7),
                new BigDecimal("-50.00"), new BigDecimal("20.00"), new BigDecimal("100000.00"),
                1.0, 13.0, 5.6, 0.995
        );

        mockMvc.perform(post("/api/v1/productor/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.volumenLitros").exists())
                .andExpect(jsonPath("$.campos.ph").exists());
    }
}
