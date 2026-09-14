package com.entrecopas.usuario.service;

import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.establecimiento.repository.EstablecimientoRepository;
import com.entrecopas.exception.BusinessException;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import com.entrecopas.usuario.dto.RegistroRequest;
import com.entrecopas.usuario.dto.UsuarioResponseDTO;
import com.entrecopas.usuario.model.Rol;
import com.entrecopas.usuario.model.Usuario;
import com.entrecopas.usuario.repository.RolRepository;
import com.entrecopas.usuario.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para registro y gestión de credenciales y perfiles de usuario.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ProductorRepository productorRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       ProductorRepository productorRepository,
                       EstablecimientoRepository establecimientoRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.productorRepository = productorRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO registrarUsuario(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail().trim())) {
            throw new BusinessException("El correo electrónico ya se encuentra registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto().trim());
        usuario.setTelefono(request.getTelefono() != null ? request.getTelefono().trim() : null);
        usuario.setActivo(true);

        String rolNombre = request.getRol() != null && !request.getRol().trim().isEmpty()
                ? request.getRol().trim().toUpperCase()
                : "ROLE_CONSUMIDOR";

        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseGet(() -> rolRepository.save(new Rol(null, rolNombre, "Rol asignado en registro")));

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Si el usuario es un productor, se inicializa su ficha productiva
        if ("ROLE_PRODUCTOR".equals(rolNombre)) {
            Productor productor = new Productor();
            productor.setUsuario(usuarioGuardado);
            productor.setNombreComercial(request.getNombreComercial() != null && !request.getNombreComercial().trim().isEmpty()
                    ? request.getNombreComercial().trim()
                    : request.getNombreCompleto().trim());
            productor.setUbicacionOrigen(request.getUbicacionOrigen() != null && !request.getUbicacionOrigen().trim().isEmpty()
                    ? request.getUbicacionOrigen().trim()
                    : "Origen Artesanal No Especificado");
            productor.setRegistroSanitario(request.getRegistroSanitario() != null ? request.getRegistroSanitario().trim() : null);
            productor.setDescripcionHistoria(request.getDescripcionHistoria() != null ? request.getDescripcionHistoria().trim() : null);
            productorRepository.save(productor);
        }

        // Si el usuario es de hostelería, se inicializa su establecimiento
        if ("ROLE_HOSTELERIA".equals(rolNombre)) {
            Establecimiento est = new Establecimiento();
            est.setUsuario(usuarioGuardado);
            est.setNombre(request.getNombreEstablecimiento() != null && !request.getNombreEstablecimiento().trim().isEmpty()
                    ? request.getNombreEstablecimiento().trim()
                    : request.getNombreCompleto().trim());
            est.setDireccion(request.getDireccionEstablecimiento() != null && !request.getDireccionEstablecimiento().trim().isEmpty()
                    ? request.getDireccionEstablecimiento().trim()
                    : "Calle Principal # 10-20");
            est.setCiudad(request.getCiudadEstablecimiento() != null && !request.getCiudadEstablecimiento().trim().isEmpty()
                    ? request.getCiudadEstablecimiento().trim()
                    : "Bogotá");
            est.setActivo(true);
            establecimientoRepository.save(est);
        }

        return UsuarioResponseDTO.fromEntity(usuarioGuardado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioActual(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return UsuarioResponseDTO.fromEntity(usuario);
    }
}
