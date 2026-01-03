package com.tricol.springboottricolapi.repository;

import com.tricol.springboottricolapi.entity.AuditLog;
import com.tricol.springboottricolapi.entity.UserApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUser(UserApp user, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
}
