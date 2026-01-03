package com.tricol.springboottricolapi.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePermissionRequest {
    @NotBlank
    private String permissionName;

    @NotNull
    private Boolean granted;
}
