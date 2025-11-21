package com.restapi.gestion_bons.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizePermissionRequest {
    
    @NotNull(message = "Permission ID is required")
    private Long permissionId;
    
    @NotNull(message = "Granted status is required")
    private Boolean granted;
}
