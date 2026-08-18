package com.rev.g3.i2.ers2.enums;

public enum Role {
    EMPLOYEE("employee"),
    MANAGER("manager");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
