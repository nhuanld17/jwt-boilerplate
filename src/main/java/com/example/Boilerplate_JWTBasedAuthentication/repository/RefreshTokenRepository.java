package com.example.Boilerplate_JWTBasedAuthentication.repository;


import com.example.Boilerplate_JWTBasedAuthentication.entity.RefreshToken;
import com.example.Boilerplate_JWTBasedAuthentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
