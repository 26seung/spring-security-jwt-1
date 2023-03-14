package com.backend.jwt.repository;

import com.backend.jwt.domain.RefreshToken;
import com.backend.jwt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

//    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByUuid(String uuid);

}
