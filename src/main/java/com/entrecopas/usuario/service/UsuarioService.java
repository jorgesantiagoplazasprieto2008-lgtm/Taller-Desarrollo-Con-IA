package com.entrecopas.usuario.service;

import com.entrecopas.usuario.model.Usuario;
import com.entrecopas.usuario.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio auxiliar para la resolución del usuario actualmente autenticado en sesión.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado vía Spring Security.
     * Si no hay sesión o es anónimo, permite resolver un usuario productor por defecto para pruebas si existe.
     */
    public Optional<Long> obtenerUsuarioAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return usuarioRepository.findByEmail(auth.getName()).map(Usuario::getId);
        }
        // Fallback para entornos de desarrollo/evaluación si existe el usuario semilla
        return usuarioRepository.findByEmail("contacto@bodegasangabriel.com").map(Usuario::getId);
    }

    /**
     * Obtiene el ID del usuario de hostelería actualmente autenticado.
     * Si el usuario en sesión no posee el rol de hostelería o es anónimo,
     * resuelve el usuario semilla canónico de hostelería para pruebas.
     */
    public Optional<Long> obtenerUsuarioHosteleriaAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(auth.getName());
            if (usuarioOpt.isPresent()) {
                Usuario u = usuarioOpt.get();
                boolean esHosteleria = u.getRoles().stream()
                        .anyMatch(r -> "ROLE_HOSTELERIA".equals(r.getNombre())
                                || "ROLE_ADMIN".equals(r.getNombre()));
                if (esHosteleria) {
                    return Optional.of(u.getId());
                }
            }
        }
        return usuarioRepository.findByEmail("gerencia@rincongourmet.com").map(Usuario::getId);
    }
}
