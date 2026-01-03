package com.tricol.springboottricolapi.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PermissionResponse {
    private String name;
    private Boolean granted;
    private String source;
}
