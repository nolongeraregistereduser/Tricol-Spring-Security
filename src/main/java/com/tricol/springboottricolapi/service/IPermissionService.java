package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.enums.RoleApp;

import java.util.Set;

public interface IPermissionService {
    Set<String> getUserPermissions(UserApp user);
    boolean hasPermission(UserApp user, String permissionName);
    void initializePermissions();
    Set<String> getUserPermissionsByRoles(Set<RoleApp> roles);
}
