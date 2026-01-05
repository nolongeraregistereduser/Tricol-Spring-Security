package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.AuditLog;
import com.tricol.springboottricolapi.entity.UserApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAuditService {
    void logAction(UserApp user, String action, String resource, String resourceId, String details);
    Page<AuditLog> getAllLogs(Pageable pageable);
    Page<AuditLog> getLogsByUser(UserApp user, Pageable pageable);
}
