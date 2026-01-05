package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.Permission;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.UserPermission;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.repository.PermissionRepository;
import com.tricol.springboottricolapi.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;


import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;

    private static final Map<RoleApp, Set<String>> ROLE_PERMISSIONS = Map.of(
            RoleApp.ADMIN, Set.of(
                    "CREATE_SUPPLIER", "READ_SUPPLIER", "UPDATE_SUPPLIER", "DELETE_SUPPLIER",
                    "CREATE_PRODUCT", "READ_PRODUCT", "UPDATE_PRODUCT", "DELETE_PRODUCT",
                    "CREATE_SUPPLIER_ORDER", "READ_SUPPLIER_ORDER", "UPDATE_SUPPLIER_ORDER", "DELETE_SUPPLIER_ORDER",
                    "CREATE_DELIVERY_NOTE", "READ_DELIVERY_NOTE", "RECEIVE_DELIVERY",
                    "READ_STOCK", "CREATE_EXIT_ORDER", "VALIDATE_EXIT_ORDER",
                    "MANAGE_USERS", "MANAGE_PERMISSIONS", "VIEW_AUDIT_LOGS"
            ),
            RoleApp.RESPONSABLE_ACHATS, Set.of(
                    "CREATE_SUPPLIER", "READ_SUPPLIER", "UPDATE_SUPPLIER",
                    "CREATE_PRODUCT", "READ_PRODUCT", "UPDATE_PRODUCT",
                    "CREATE_SUPPLIER_ORDER", "READ_SUPPLIER_ORDER", "UPDATE_SUPPLIER_ORDER", "VALIDATE_SUPPLIER_ORDER",
                    "READ_DELIVERY_NOTE", "READ_STOCK"
            ),
            RoleApp.MAGASINIER, Set.of(
                    "READ_SUPPLIER", "READ_PRODUCT", "READ_SUPPLIER_ORDER",
                    "CREATE_DELIVERY_NOTE", "READ_DELIVERY_NOTE", "RECEIVE_DELIVERY",
                    "READ_STOCK", "CREATE_EXIT_ORDER", "VALIDATE_EXIT_ORDER"
            ),
            RoleApp.CHEF_ATELIER, Set.of(
                    "READ_SUPPLIER", "READ_PRODUCT", "READ_SUPPLIER_ORDER",
                    "READ_DELIVERY_NOTE", "READ_STOCK", "CREATE_EXIT_ORDER"
            )
    );

    public Set<String> getUserPermissions(UserApp user) {
        Set<String> permissions = new HashSet<>();

        for (RoleApp role : user.getRoles()) {
            permissions.addAll(ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet()));
        }

        List<UserPermission> customPermissions = userPermissionRepository.findByUser(user);
        for (UserPermission up : customPermissions) {
            if (up.getGranted()) {
                permissions.add(up.getPermission().getName());
            } else {
                permissions.remove(up.getPermission().getName());
            }
        }

        return permissions;
    }

    public boolean hasPermission(UserApp user, String permissionName) {
        return getUserPermissions(user).contains(permissionName);
    }

    public void initializePermissions() {
        Set<String> allPermissions = new HashSet<>();
        ROLE_PERMISSIONS.values().forEach(allPermissions::addAll);

        for (String permName : allPermissions) {
            if (permissionRepository.findByName(permName).isEmpty()) {
                Permission permission = Permission.builder()
                        .name(permName)
                        .description("Permission: " + permName)
                        .build();
                permissionRepository.save(permission);
            }
        }
    }


    public Set<String> getUserPermissionsByRoles(Set<RoleApp> roles) {
    Set<String> permissions = new HashSet<>();
    for (RoleApp role : roles) {
        permissions.addAll(ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet()));
    }
    return permissions;
    }


}
