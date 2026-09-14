package com.entrecopas.usuario.controller;

import com.entrecopas.usuario.dto.RegistroRequest;
import com.entrecopas.usuario.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para las vistas de inicio de sesión y registro de usuarios.
 */
@Controller
public class AuthViewController {

    private final AuthService authService;

    public AuthViewController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registrado", required = false) String registrado,
            Model model) {
        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales incorrectas. Verifique su correo y contraseña.");
        }
        if (logout != null) {
            model.addAttribute("mensajeExito", "Ha cerrado sesión de forma segura.");
        }
        if (registrado != null) {
            model.addAttribute("mensajeExito", "¡Cuenta creada exitosamente! Inicie sesión con sus credenciales.");
        }
        model.addAttribute("paginaActiva", "login");
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroForm", new RegistroRequest());
        model.addAttribute("paginaActiva", "registro");
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @ModelAttribute("registroForm") RegistroRequest form,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registrarUsuario(form);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/registro";
        }
    }
}
