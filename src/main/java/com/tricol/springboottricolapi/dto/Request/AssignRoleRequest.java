package com.tricol.springboottricolapi.dto.Request;

import com.tricol.springboottricolapi.entity.enums.RoleApp;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @NotNull
    private RoleApp role;
}
