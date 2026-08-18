package com.rev.g3.i2.ers2.enums;

public enum Status {
    PENDING("pending"),
    APPROVED("approved"),
    DENIED("denied");

    private final String dbValue;

    Status(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Status fromDbValue(String value) {
        for (Status status : values()) {
            if (status.dbValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
