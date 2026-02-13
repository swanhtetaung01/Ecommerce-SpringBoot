package com.ecommerce.project.controller;

import com.ecommerce.project.security.jwt.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        
    }
}
