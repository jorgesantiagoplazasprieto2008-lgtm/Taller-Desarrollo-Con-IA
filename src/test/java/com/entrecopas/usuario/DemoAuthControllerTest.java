package com.entrecopas.usuario;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entrecopas.usuario.controller.DemoAuthController;
import com.entrecopas.usuario.service.CustomUserDetailsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Pruebas unitarias para el controlador de conmutación de roles de prueba DemoAuthController.
 */
@ExtendWith(MockitoExtension.class)
class DemoAuthControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CustomUserDetailsService userDetailsService;

  @InjectMocks
  private DemoAuthController demoAuthController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(demoAuthController).build();
  }

  @Test
  @DisplayName("Conmutar a Productor debe autenticar y redirigir a /panel/productor/productos")
  void testSwitchRoleProductor() throws Exception {
    UserDetails userDetails = new User(
        "contacto@bodegasangabriel.com",
        "encodedPass",
        List.of(new SimpleGrantedAuthority("ROLE_PRODUCTOR")));
    when(userDetailsService.loadUserByUsername("contacto@bodegasangabriel.com"))
        .thenReturn(userDetails);

    mockMvc.perform(get("/demo/switch-role").param("role", "productor"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panel/productor/productos"));
  }

  @Test
  @DisplayName("Conmutar a Hostelería debe autenticar y redirigir a /panel/hosteleria/disponibilidad")
  void testSwitchRoleHosteleria() throws Exception {
    UserDetails userDetails = new User(
        "gerencia@rincongourmet.com",
        "encodedPass",
        List.of(new SimpleGrantedAuthority("ROLE_HOSTELERIA")));
    when(userDetailsService.loadUserByUsername("gerencia@rincongourmet.com"))
        .thenReturn(userDetails);

    mockMvc.perform(get("/demo/switch-role").param("role", "hosteleria"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panel/hosteleria/disponibilidad"));
  }

  @Test
  @DisplayName("Conmutar a Consumidor debe autenticar y redirigir a /catalogo")
  void testSwitchRoleConsumidor() throws Exception {
    UserDetails userDetails = new User(
        "juan.consumidor@gmail.com",
        "encodedPass",
        List.of(new SimpleGrantedAuthority("ROLE_CONSUMIDOR")));
    when(userDetailsService.loadUserByUsername("juan.consumidor@gmail.com"))
        .thenReturn(userDetails);

    mockMvc.perform(get("/demo/switch-role").param("role", "consumidor"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/catalogo"));
  }

  @Test
  @DisplayName("Conmutar a Visitante debe invalidar sesión y redirigir a /catalogo")
  void testSwitchRoleVisitante() throws Exception {
    mockMvc.perform(get("/demo/switch-role").param("role", "visitante"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/catalogo"));
  }
}
