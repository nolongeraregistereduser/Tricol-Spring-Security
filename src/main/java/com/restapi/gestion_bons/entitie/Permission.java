package com.restapi.gestion_bons.entitie;


import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String resource;

    @Column(length = 20)
    private String action;

    @ManyToMany(mappedBy = "defaultPermissions")
    @Builder.Default
    private Set<RoleApp> roles = new HashSet<>();

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserPermission> userPermissions = new HashSet<>();
}