package com.entrecopas.producto;

import com.entrecopas.exception.GlobalExceptionHandler;
import com.entrecopas.producto.controller.ProductoRestController;
import com.entrecopas.producto.controller.ProductoViewController;
import com.entrecopas.producto.dto.ProductoDTO;
import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.service.ProductoService;
import com.entrecopas.usuario.service.UsuarioService;
import java.math.BigDecimal;
import java.util.List;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de controladores MVC (JSP) y REST de productos utilizando MockMvc Standalone.
 * Compatible con JDK 24 sin depender de transformadores de agentes dinámicos.
 */
@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private com.entrecopas.disponibilidad.service.DisponibilidadService disponibilidadService;

    @InjectMocks
    private ProductoViewController viewController;

    @InjectMocks
    private ProductoRestController restController;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Debe resolver la vista JSP catalogo con HTTP 200 y modelo cargado")
    void testVerCatalogoJsp() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(1L);
        dto.setNombre("Cerveza IPA Artesanal");
        dto.setTipoBebida("Cerveza");
        dto.setPrecio(new BigDecimal("18000.00"));

        when(productoService.obtenerProductosActivos(any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/catalogo"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogo"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attributeExists("paginaActiva"));
    }

    @Test
    @DisplayName("Debe retornar listado JSON en /api/v1/productos con HTTP 200")
    void testApiCatalogoJson() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(1L);
        dto.setNombre("Vino Merlot Reserva");
        dto.setTipoBebida("Vino");
        dto.setPrecio(new BigDecimal("52000.00"));
        dto.setEstado(EstadoProducto.ACTIVO);

        when(productoService.obtenerProductosActivos(any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Vino Merlot Reserva"))
                .andExpect(jsonPath("$[0].tipoBebida").value("Vino"));
    }

    @Test
    @DisplayName("Debe retornar detalle JSON en /api/v1/productos/{id} con HTTP 200")
    void testApiDetalleJson() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(5L);
        dto.setNombre("Hidromiel Especiada");
        dto.setTipoBebida("Hidromiel");
        dto.setPrecio(new BigDecimal("32000.00"));

        when(productoService.obtenerProductoPorId(5L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/productos/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Hidromiel Especiada"));
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando el payload de creación no cumple validaciones Jakarta")
    void testValidacionPayloadCreacion() throws Exception {
        // Enviar payload vacío para detonar MethodArgumentNotValidException manejada por GlobalExceptionHandler
        mockMvc.perform(post("/api/v1/productor/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.nombre").exists())
                .andExpect(jsonPath("$.campos.precio").exists());
    }
}
