package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<UserApp, Long> {
    
    Optional<UserApp> findByUsername(String username);
    
    Optional<UserApp> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM UserApp u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.defaultPermissions WHERE u.username = :username")
    Optional<UserApp> findByUsernameWithRoleAndPermissions(String username);
}
