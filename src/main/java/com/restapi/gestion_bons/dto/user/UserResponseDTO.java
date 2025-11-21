package com.restapi.gestion_bons.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private String role;
    private LocalDateTime createdAt;
    private List<String> permissions;
}
