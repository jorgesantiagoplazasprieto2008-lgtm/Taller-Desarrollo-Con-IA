package com.entrecopas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta acceder o modificar recursos fuera del tenant asignado (HTTP 403).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedDomainException extends RuntimeException {

    public UnauthorizedDomainException(String message) {
        super(message);
    }
}
