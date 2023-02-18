package com.thuan.logging.services;

import com.thuan.logging.entities.RequestLog;
import com.thuan.logging.repositories.RequestLogRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestLogService {
    private final RequestLogRepository requestLogRepository;

    RequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    public RequestLog save(RequestLog requestLog) {
        return requestLogRepository.save(requestLog);
    }
}
