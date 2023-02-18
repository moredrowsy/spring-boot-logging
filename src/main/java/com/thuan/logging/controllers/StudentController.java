package com.thuan.logging.controllers;

import com.thuan.logging.entities.Address;
import com.thuan.logging.entities.Student;
import com.thuan.logging.exceptions.StudentException;
import com.thuan.logging.exceptions.ValidateAddressException;
import com.thuan.logging.services.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {
    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/student/{id}")
    public Student getStudentById(
            @PathVariable Integer id) throws StudentException {
        return studentService.getStudent(id);
    }

    @PostMapping("/student")
    public Student createStudent(@RequestBody Student student) throws StudentException {
        return studentService.createStudent(student);
    }

    @PostMapping("/studentWithAddress")
    public Student createStudentWithAddress(@RequestBody Student student) throws ValidateAddressException {
        Address address = new Address();
        address.setNumber("123");
        address.setStreet("My Blvd.");
        address.setCity("Austin");
        address.setState("TX");
        address.setZip("987654");
        return studentService.createStudentWithAddress(student, address);
    }
}
