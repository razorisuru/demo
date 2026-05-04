package com.hello.demo.service;

import com.hello.demo.dto.*;
import com.hello.demo.entity.Role;
import com.hello.demo.entity.User;
import com.hello.demo.repository.UserRepository;
import com.hello.demo.security.JwtService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final StringRedisTemplate redis;

    public AuthResponse register(RegisterRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepo.save(user);

        return generateTokens(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow();

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {
        String access = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getEmail());

        redis.opsForValue().set("refresh:" + user.getEmail(), refresh, Duration.ofDays(7));

        return new AuthResponse(access, refresh);
    }

    public AuthResponse refresh(RefreshRequest req) {
        String email = jwtService.extractEmail(req.getRefreshToken());

        String stored = redis.opsForValue().get("refresh:" + email);
        redis.expire("refresh:" + email, Duration.ofDays(7));

        if (!req.getRefreshToken().equals(stored)) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepo.findByEmail(email).orElseThrow();

        String newAccess = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(newAccess, req.getRefreshToken());
    }
}