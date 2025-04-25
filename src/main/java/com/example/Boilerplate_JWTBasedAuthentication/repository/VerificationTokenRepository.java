package com.example.Boilerplate_JWTBasedAuthentication.repository;

import com.example.Boilerplate_JWTBasedAuthentication.entity.User;
import com.example.Boilerplate_JWTBasedAuthentication.entity.VeryficationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VeryficationToken, Long> {
    Optional<VeryficationToken> findByToken(String token);
    void deleteByUser(User user);
}
