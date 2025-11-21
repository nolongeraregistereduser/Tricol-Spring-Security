package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionDAO extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
