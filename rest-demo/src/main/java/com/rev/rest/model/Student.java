package com.rev.rest.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data // Generates, getters, setters, toString, equals, and hashCode
//@NoArgsConstructor // Generates a generic constructor
//@AllArgsConstructor // Generates a paramaterized constructor
public class Student {
    private int id;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 50, message = "Name must be an appropriate size.")
    private String name;
    @Email(message = "Must be a valid email.")
    private String email;
    private String course;

    public Student() {
    }
    public Student(int id, String name, String email, String course) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
