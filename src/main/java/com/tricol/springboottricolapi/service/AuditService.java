package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.AuditLog;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(UserApp user, String action, String resource, String resourceId, String details) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .resource(resource)
                .resourceId(resourceId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getLogsByUser(UserApp user, Pageable pageable) {
        return auditLogRepository.findByUser(user, pageable);
    }
}
