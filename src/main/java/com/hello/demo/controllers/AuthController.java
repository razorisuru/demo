package com.hello.demo.controllers;

import com.hello.demo.dto.*;
import com.hello.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        return service.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest req) {
        return service.refresh(req);
    }
}