package com.rev.g3.i2.ers2.enums;

public enum Type {
    TRAVEL("travel"),
    FOOD("food"),
    LODGING("lodging"),
    OTHER("other");

    private final String dbValue;

    Type(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Type fromDbValue(String value) {
        for (Type type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown reimbursement type: " + value);
    }
}
