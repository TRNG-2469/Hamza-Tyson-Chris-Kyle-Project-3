package com.rev.rest.service;

import com.rev.rest.exceptions.StudentNotFoundException;
import com.rev.rest.model.Student;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StudentService {
    private List<Student> students = new ArrayList<>();

    public StudentService() {
        students.add(new Student(1, "Guy", "itshim@gmail.com", "Comp Sci"));
        students.add(new Student(2, "Gal", "itsher@gmail.com", "Math"));
        students.add(new Student(3, "Them", "itsthem@gmail.com", "Finance"));
    }

    public List<Student> getAllStudents() {return students;}

    public Student getStudentById(int id) {
        for (Student s : students){
            if(s.getId() == id){
                return s;
            }
        }

        throw new StudentNotFoundException(id);
    }

    public Student addStudent(Student student) {
        students.add(student);
        return student;
    }

    public Student updateStudent(int id, Student updatedStudent) {
        for(int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if(s.getId() == id){
                s.setName(updatedStudent.getName());
                s.setEmail(updatedStudent.getEmail());
                s.setCourse(updatedStudent.getCourse());

                return updatedStudent;
            }
        }

        throw new StudentNotFoundException(id);
    }

    public String deleteStudent(int id){
        for(int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if(s.getId() == id){
                students.remove(i);
                return "Student with ID " + id + " deleted successfully";
            }
        }

        throw new StudentNotFoundException(id);
    }
}
