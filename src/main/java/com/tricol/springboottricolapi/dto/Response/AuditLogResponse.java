package com.tricol.springboottricolapi.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String username;
    private String action;
    private String resource;
    private String resourceId;
    private String details;
    private LocalDateTime timestamp;
}
