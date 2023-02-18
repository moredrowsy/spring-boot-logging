package com.thuan.logging.services;

import com.thuan.logging.entities.ErrorLog;
import com.thuan.logging.repositories.ErrorLogRepository;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogService {
    private final ErrorLogRepository errorLogRepository;

    ErrorLogService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public ErrorLog save(ErrorLog errorLog) {
        return errorLogRepository.save(errorLog);
    }
}
