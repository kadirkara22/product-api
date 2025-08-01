package com.kadirkara.auth.controller;


import com.kadirkara.auth.dto.AuthRequest;
import com.kadirkara.auth.dto.AuthResponse;
import com.kadirkara.auth.entity.User;
import com.kadirkara.auth.service.UserService;
import com.kadirkara.auth.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest authRequest) {
        User user = this.userService.registerUser(authRequest.getUsername(), authRequest.getPassword(), Collections.singleton("ROLE_USER"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(), authRequest.getPassword()
                )
        );
        User user = userService.loadUserByUsername(authRequest.getUsername());
        if (user != null) {
            String jwt = jwtUtil.generateToken(authRequest.getUsername(), user.getRoles());
            return ResponseEntity.ok(new AuthResponse(jwt));
        } else {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}
