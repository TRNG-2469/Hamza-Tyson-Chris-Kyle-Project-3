package com.rev.g3.i2.ers2.converter;

import com.rev.g3.i2.ers2.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link Role} as its lowercase db value (e.g. "employee") instead of the Java constant name. */
@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Role.fromDbValue(dbData);
    }
}
