package com.thuan.logging.repositories;

import com.thuan.logging.entities.ErrorLog;
import com.thuan.logging.entities.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository extends JpaRepository <RequestLog, String> {
}
