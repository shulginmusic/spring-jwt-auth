package com.example.MusiciansAPI.security.service;

import com.example.MusiciansAPI.exception.TokenRefreshException;
import com.example.MusiciansAPI.model.RefreshToken;
import com.example.MusiciansAPI.model.User;
import com.example.MusiciansAPI.repository.RefreshTokenRepository;
import com.example.MusiciansAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        verifyThatExists(token);
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByUser(User user) {
        //TODO: fix the verification: weird exception thrown
//        verifyThatExists(user);
        return refreshTokenRepository.findByUser(user);
    }

    public RefreshToken createRefreshToken(Long userId) {
        var refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public void verifyThatExists(String token) {
        if (refreshTokenRepository.findByToken(token).isEmpty()) {
            throw new TokenRefreshException(token, "This Refresh token " +
                    " isn't assigned to a user. Please login and try again using new refresh access token");
        }
    }

    //TODO: fix this method signature
//    public void verifyThatExists(User user) {
//        if (refreshTokenRepository.findByUser(user).isEmpty()) {
//            throw new TokenRefreshException(user.getUsername(), "This Refresh token " +
//                    " isn't assigned to a user. Please login and try again using new refresh access token");
//        }
//    }

    public void verifyExpiration(String token) {
        RefreshToken refreshToken = findByToken(token).get();
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(refreshToken.getToken(), "Token expired," +
                    " please login again and use new refresh token");
        }
    }


    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
