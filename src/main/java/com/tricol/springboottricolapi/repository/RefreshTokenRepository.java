package com.tricol.springboottricolapi.repository;

import com.tricol.springboottricolapi.entity.RefreshToken;
import com.tricol.springboottricolapi.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(UserApp user);
}
