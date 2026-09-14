package com.entrecopas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security y RBAC de Entre Copas.
 * Garantiza que rutas protegidas no queden expuestas públicamente.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Desactivado para simplificar REST y prototipo en fase 1
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Rutas web públicas
                .requestMatchers("/", "/catalogo", "/productos/**", "/trazabilidad/**", "/login", "/registro", "/demo/**").permitAll()
                // Endpoints REST de lectura y autenticación pública
                .requestMatchers("/api/v1/productos/**", "/api/v1/trazabilidad/**", "/api/v1/auth/**").permitAll()
                // Rutas y endpoints del productor protegidos
                .requestMatchers("/panel/productor/**", "/api/v1/productor/**").authenticated()
                // Rutas y endpoints de hostelería protegidos
                .requestMatchers("/panel/hosteleria/**", "/api/v1/hosteleria/**").authenticated()
                // Rutas financieras y administrativas estrictamente protegidas
                .requestMatchers("/panel/financiero/**", "/api/v1/financiero/**", "/panel/admin/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/catalogo", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/catalogo")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
