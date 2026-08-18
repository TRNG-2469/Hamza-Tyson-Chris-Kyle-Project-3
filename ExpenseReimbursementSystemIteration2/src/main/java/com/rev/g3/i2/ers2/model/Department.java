package com.rev.g3.i2.ers2.model;

import java.util.Objects;

public class Department {
    private int departmentId;
    private String departmentName;

    private Department() {}

    public Department(int departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + departmentId +
                ", name='" + departmentName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Department that)) return false;
        return getDepartmentId() == that.getDepartmentId() && Objects.equals(getDepartmentName(), that.getDepartmentName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDepartmentId(), getDepartmentName());
    }
}
