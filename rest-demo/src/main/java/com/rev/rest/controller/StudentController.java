package com.rev.rest.controller;

import com.rev.rest.model.Student;
import com.rev.rest.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/students")
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        return ResponseEntity.status(201).body(studentService.addStudent(student));
    }

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable int id, @RequestBody Student updatedStudent) {
        return studentService.updateStudent(id, updatedStudent);
    }

    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable int id){
        return studentService.deleteStudent(id);
    }
}
