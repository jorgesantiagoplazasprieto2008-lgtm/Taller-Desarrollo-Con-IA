package com.entrecopas.usuario.controller;

import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.usuario.dto.RegistroRequest;
import com.entrecopas.usuario.dto.UsuarioResponseDTO;
import com.entrecopas.usuario.service.AuthService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para registro y consulta del usuario autenticado.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroRequest request) {
        UsuarioResponseDTO registrado = authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedDomainException("No hay una sesión activa.");
        }
        UsuarioResponseDTO usuario = authService.obtenerUsuarioActual(principal.getName());
        return ResponseEntity.ok(usuario);
    }
}
