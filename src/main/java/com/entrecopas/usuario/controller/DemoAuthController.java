package com.entrecopas.usuario.controller;

import com.entrecopas.usuario.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador auxiliar de demostración y evaluación para conmutar roles en sesión
 * sin requerir digitar credenciales manualmente durante pruebas de usabilidad.
 */
@Controller
public class DemoAuthController {

  private final CustomUserDetailsService userDetailsService;

  public DemoAuthController(CustomUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  /**
   * Conmuta la sesión del usuario al rol de demostración solicitado.
   */
  @GetMapping("/demo/switch-role")
  public String switchRole(
      @RequestParam(value = "role", defaultValue = "visitante") String role,
      HttpServletRequest request) {

    String targetEmail = switch (role.toLowerCase()) {
      case "productor" -> "contacto@bodegasangabriel.com";
      case "hosteleria" -> "gerencia@rincongourmet.com";
      case "consumidor" -> "juan.consumidor@gmail.com";
      case "admin" -> "admin@entrecopas.com";
      default -> null;
    };

    if (targetEmail != null) {
      try {
        UserDetails userDetails = userDetailsService.loadUserByUsername(targetEmail);
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            context);

        return switch (role.toLowerCase()) {
          case "productor" -> "redirect:/panel/productor/productos";
          case "hosteleria" -> "redirect:/panel/hosteleria/disponibilidad";
          case "consumidor" -> "redirect:/catalogo";
          case "admin" -> "redirect:/panel/financiero";
          default -> "redirect:/catalogo";
        };
      } catch (Exception e) {
        // En caso de que el usuario no exista en DB de pruebas, redirigir a catalogo
        return "redirect:/catalogo";
      }
    }

    // Si se solicita visitante o anónimo, se limpia la sesión
    SecurityContextHolder.clearContext();
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    return "redirect:/catalogo";
  }
}
