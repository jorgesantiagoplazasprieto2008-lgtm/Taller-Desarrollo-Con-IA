package com.entrecopas.lote.service;

import com.entrecopas.lote.repository.LoteRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Generador algorítmico determinista del código único de trazabilidad.
 * Formato canónico: EC-YYYY-PRXX-XXXX
 */
@Component
public class CodigoTrazabilidadGenerator {

    private final LoteRepository loteRepository;

    public CodigoTrazabilidadGenerator(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    /**
     * Genera un código de trazabilidad único con validación de no colisión.
     * Ejemplo: EC-2026-PR01-A101
     */
    public String generarCodigo(Long productorId, LocalDate fechaProduccion) {
        int anio = fechaProduccion != null ? fechaProduccion.getYear() : LocalDate.now().getYear();
        String prodStr = String.format("PR%02d", productorId);

        long count = loteRepository.countByProductoProductorId(productorId) + 1;
        String codigoBase = String.format("EC-%d-%s-L%03d", anio, prodStr, count);

        // En caso excepcional de colisión, se ajusta secuencialmente
        int offset = 0;
        String codigoFinal = codigoBase;
        while (loteRepository.existsByCodigoTrazabilidad(codigoFinal)) {
            offset++;
            codigoFinal = String.format("EC-%d-%s-L%03d", anio, prodStr, count + offset);
        }

        return codigoFinal;
    }
}
