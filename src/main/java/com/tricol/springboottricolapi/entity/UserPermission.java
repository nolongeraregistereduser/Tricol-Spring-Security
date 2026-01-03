package com.tricol.springboottricolapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "permission_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserApp user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(nullable = false)
    private Boolean granted;
}
