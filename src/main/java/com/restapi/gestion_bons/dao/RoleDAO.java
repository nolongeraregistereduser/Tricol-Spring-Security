package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.RoleApp;
import com.restapi.gestion_bons.entitie.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDAO extends JpaRepository<RoleApp, Long> {
    Optional<RoleApp> findByName(RoleName name);
}
