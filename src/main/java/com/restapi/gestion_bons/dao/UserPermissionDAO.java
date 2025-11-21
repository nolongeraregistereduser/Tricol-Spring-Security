package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPermissionDAO extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Long permissionId);
}
