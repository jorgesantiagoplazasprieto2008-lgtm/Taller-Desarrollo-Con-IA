package com.entrecopas.lote.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA para transformar ParametrosAnaliticos a JSON y viceversa.
 */
@Converter(autoApply = false)
public class ParametrosAnaliticosConverter implements AttributeConverter<ParametrosAnaliticos, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ParametrosAnaliticos attribute) {
        if (attribute == null) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error al serializar ParametrosAnaliticos a JSON", e);
        }
    }

    @Override
    public ParametrosAnaliticos convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ParametrosAnaliticos();
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, ParametrosAnaliticos.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error al deserializar JSON a ParametrosAnaliticos", e);
        }
    }
}
