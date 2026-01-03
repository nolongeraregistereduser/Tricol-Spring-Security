package com.tricol.springboottricolapi.repository;

import com.tricol.springboottricolapi.entity.Permission;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUser(UserApp user);
    Optional<UserPermission> findByUserAndPermissionName(UserApp user, String permissionName);
    Optional<UserPermission> findByUserAndPermission(UserApp user, Permission permission);
}
