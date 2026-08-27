package com.rev.g3.i2.ers2.converter;

import com.rev.g3.i2.ers2.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link Status} as its lowercase db value (e.g. "pending") instead of the Java constant name. */
@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Status.fromDbValue(dbData);
    }
}
