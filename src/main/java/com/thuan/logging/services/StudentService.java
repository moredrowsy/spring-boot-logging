package com.thuan.logging.services;

import com.thuan.logging.entities.Address;
import com.thuan.logging.errorLogging.ErrorLogging;
import com.thuan.logging.entities.Student;
import com.thuan.logging.exceptions.StudentException;
import com.thuan.logging.exceptions.ValidateAddressException;
import com.thuan.logging.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private final ValidateStuentAddressService validateStuentAddressService;

    StudentService(StudentRepository studentRepository, ValidateStuentAddressService validateStuentAddressService) {
        this.studentRepository = studentRepository;
        this.validateStuentAddressService = validateStuentAddressService;
    }

    @ErrorLogging(type = "GetStudent")
    public Student getStudent(Integer id) throws StudentException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.orElseThrow(() -> new StudentException("Student not found"));
    }

    @ErrorLogging(type = "CreateStudent")
    public Student createStudent(Student student) throws StudentException {
        return studentRepository.save(student);
    }

    @ErrorLogging(type = "CreateStudentWithAddressValidation")
    public Student createStudentWithAddress(Student student, Address address) throws ValidateAddressException {
        boolean valid = validateStuentAddressService.isValid(student, address);
        return studentRepository.save(student);
    }
}
