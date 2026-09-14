package com.entrecopas.config;

import com.entrecopas.usuario.model.Usuario;
import com.entrecopas.usuario.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Advice global de Spring MVC para inyectar información de autenticación y rol
 * en todas las vistas JSP de la aplicación.
 */
@ControllerAdvice
public class GlobalModelAdvice {

  private final UsuarioRepository usuarioRepository;

  public GlobalModelAdvice(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  /**
   * Indica si el usuario actual en sesión se encuentra autenticado y no es anónimo.
   */
  @ModelAttribute("usuarioAutenticado")
  public boolean isUsuarioAutenticado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken);
  }

  /**
   * Retorna el correo electrónico del usuario autenticado o null si es anónimo.
   */
  @ModelAttribute("usuarioEmail")
  public String getUsuarioEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken)) {
      return auth.getName();
    }
    return null;
  }

  /**
   * Retorna el nombre legible del usuario autenticado o 'Visitante' si es anónimo.
   */
  @ModelAttribute("usuarioNombre")
  public String getUsuarioNombre() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken)) {
      return usuarioRepository.findByEmail(auth.getName())
          .map(Usuario::getNombreCompleto)
          .orElse(auth.getName());
    }
    return "Visitante Invitado";
  }

  /**
   * Retorna el rol principal del usuario autenticado.
   */
  @ModelAttribute("usuarioRol")
  public String getUsuarioRol() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken)) {
      for (GrantedAuthority authority : auth.getAuthorities()) {
        String role = authority.getAuthority();
        if (role.startsWith("ROLE_")) {
          return role;
        }
      }
    }
    return "ANONYMOUS";
  }

  /**
   * Retorna la etiqueta legible del rol actual.
   */
  @ModelAttribute("rolEtiqueta")
  public String getRolEtiqueta() {
    String rol = getUsuarioRol();
    return switch (rol) {
      case "ROLE_PRODUCTOR" -> "Productor Artesanal";
      case "ROLE_HOSTELERIA" -> "Hostelería / Barra";
      case "ROLE_CONSUMIDOR" -> "Consumidor";
      case "ROLE_ADMIN" -> "Administrador";
      default -> "Visitante Invitado";
    };
  }

  /**
   * Retorna el icono o emoji alusivo al rol actual.
   */
  @ModelAttribute("rolIcono")
  public String getRolIcono() {
    String rol = getUsuarioRol();
    return switch (rol) {
      case "ROLE_PRODUCTOR" -> "🍷";
      case "ROLE_HOSTELERIA" -> "🍽️";
      case "ROLE_CONSUMIDOR" -> "🥂";
      case "ROLE_ADMIN" -> "🛡️";
      default -> "👤";
    };
  }

  /**
   * Indica si el usuario actual tiene permisos de Productor (incluye Administrador).
   */
  @ModelAttribute("esProductor")
  public boolean isProductor() {
    String rol = getUsuarioRol();
    return "ROLE_PRODUCTOR".equals(rol) || "ROLE_ADMIN".equals(rol);
  }

  /**
   * Indica si el usuario actual tiene permisos de Hostelería (incluye Administrador).
   */
  @ModelAttribute("esHosteleria")
  public boolean isHosteleria() {
    String rol = getUsuarioRol();
    return "ROLE_HOSTELERIA".equals(rol) || "ROLE_ADMIN".equals(rol);
  }

  /**
   * Indica si el usuario actual es Administrador.
   */
  @ModelAttribute("esAdmin")
  public boolean isAdmin() {
    return "ROLE_ADMIN".equals(getUsuarioRol());
  }

  /**
   * Indica si el usuario actual es Consumidor final.
   */
  @ModelAttribute("esConsumidor")
  public boolean isConsumidor() {
    return "ROLE_CONSUMIDOR".equals(getUsuarioRol());
  }
}
