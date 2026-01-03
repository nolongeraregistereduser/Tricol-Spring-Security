package com.tricol.springboottricolapi.dto.Response;

import com.tricol.springboottricolapi.entity.enums.RoleApp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<RoleApp> roles;
    private Boolean enabled;
}
