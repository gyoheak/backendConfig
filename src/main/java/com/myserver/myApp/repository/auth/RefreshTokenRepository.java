package com.myserver.myApp.repository.auth;

import org.springframework.stereotype.Repository;

import com.myserver.myApp.entity.auth.RefreshToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByKey(String key);
}
