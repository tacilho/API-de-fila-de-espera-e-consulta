package com.example.restaurant.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PrioridadeConverter implements AttributeConverter<Integer, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Integer attribute) {
        return attribute != null ? attribute : 0;
    }

    @Override
    public Integer convertToEntityAttribute(Integer dbData) {
        return dbData != null ? dbData : 0;
    }
} 