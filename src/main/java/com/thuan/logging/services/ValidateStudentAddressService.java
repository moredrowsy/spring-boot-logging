package com.thuan.logging.services;

import com.thuan.logging.entities.Address;
import com.thuan.logging.entities.Student;
import com.thuan.logging.errorLogging.ErrorLogging;
import com.thuan.logging.exceptions.ValidateAddressException;
import org.springframework.stereotype.Service;

@Service
public class ValidateStudentAddressService {
    @ErrorLogging
    public boolean isValid(Student student, Address address) throws ValidateAddressException {
        throw new ValidateAddressException("My validating exception test");
    }
}
