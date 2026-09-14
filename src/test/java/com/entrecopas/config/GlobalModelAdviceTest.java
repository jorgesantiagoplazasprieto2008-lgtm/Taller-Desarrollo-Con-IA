package com.entrecopas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.entrecopas.usuario.model.Usuario;
import com.entrecopas.usuario.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Pruebas unitarias para GlobalModelAdvice.
 */
@ExtendWith(MockitoExtension.class)
class GlobalModelAdviceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @InjectMocks
  private GlobalModelAdvice globalModelAdvice;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Usuario anónimo debe resolverse con banderas falsas y etiqueta Visitante")
  void testUsuarioAnonimo() {
    AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
        "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    SecurityContextHolder.getContext().setAuthentication(anonymous);

    assertFalse(globalModelAdvice.isUsuarioAutenticado());
    assertEquals("Visitante Invitado", globalModelAdvice.getUsuarioNombre());
    assertEquals("ANONYMOUS", globalModelAdvice.getUsuarioRol());
    assertEquals("Visitante Invitado", globalModelAdvice.getRolEtiqueta());
    assertEquals("👤", globalModelAdvice.getRolIcono());
    assertFalse(globalModelAdvice.isProductor());
    assertFalse(globalModelAdvice.isHosteleria());
    assertFalse(globalModelAdvice.isConsumidor());
    assertFalse(globalModelAdvice.isAdmin());
  }

  @Test
  @DisplayName("Usuario Productor autenticado debe exponer banderas y etiquetas de Productor")
  void testUsuarioProductor() {
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        "contacto@bodegasangabriel.com", "pass",
        List.of(new SimpleGrantedAuthority("ROLE_PRODUCTOR")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    Usuario u = new Usuario();
    u.setNombreCompleto("Carlos Mendoza");
    when(usuarioRepository.findByEmail("contacto@bodegasangabriel.com")).thenReturn(Optional.of(u));

    assertTrue(globalModelAdvice.isUsuarioAutenticado());
    assertEquals("Carlos Mendoza", globalModelAdvice.getUsuarioNombre());
    assertEquals("ROLE_PRODUCTOR", globalModelAdvice.getUsuarioRol());
    assertEquals("Productor Artesanal", globalModelAdvice.getRolEtiqueta());
    assertEquals("🍷", globalModelAdvice.getRolIcono());
    assertTrue(globalModelAdvice.isProductor());
    assertFalse(globalModelAdvice.isHosteleria());
    assertFalse(globalModelAdvice.isConsumidor());
  }

  @Test
  @DisplayName("Usuario Hostelería autenticado debe exponer banderas y etiquetas de Hostelería")
  void testUsuarioHosteleria() {
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        "gerencia@rincongourmet.com", "pass",
        List.of(new SimpleGrantedAuthority("ROLE_HOSTELERIA")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    Usuario u = new Usuario();
    u.setNombreCompleto("El Rincón Gourmet");
    when(usuarioRepository.findByEmail("gerencia@rincongourmet.com")).thenReturn(Optional.of(u));

    assertTrue(globalModelAdvice.isUsuarioAutenticado());
    assertEquals("El Rincón Gourmet", globalModelAdvice.getUsuarioNombre());
    assertEquals("ROLE_HOSTELERIA", globalModelAdvice.getUsuarioRol());
    assertEquals("Hostelería / Barra", globalModelAdvice.getRolEtiqueta());
    assertEquals("🍽️", globalModelAdvice.getRolIcono());
    assertFalse(globalModelAdvice.isProductor());
    assertTrue(globalModelAdvice.isHosteleria());
  }
}
