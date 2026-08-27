package com.rev.g3.i2.ers2.converter;

import com.rev.g3.i2.ers2.enums.Type;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link Type} as its lowercase db value (e.g. "travel") instead of the Java constant name. */
@Converter(autoApply = true)
public class TypeConverter implements AttributeConverter<Type, String> {
    @Override
    public String convertToDatabaseColumn(Type attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Type convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Type.fromDbValue(dbData);
    }
}
