package com.entrecopas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal de arranque para la plataforma Entre Copas.
 */
@SpringBootApplication
public class EntreCopasApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EntreCopasApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EntreCopasApplication.class, args);
    }
}
