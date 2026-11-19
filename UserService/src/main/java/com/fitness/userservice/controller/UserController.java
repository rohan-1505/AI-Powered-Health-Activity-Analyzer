
package com.fitness.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fitness.userservice.DTO.Registerrequest;
import com.fitness.userservice.DTO.UserResponse;
import com.fitness.userservice.Service.userService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final userService userService;

    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody Registerrequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    // Validate user using Keycloak ID
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                userService.existByUserId(userId)
        );
    }

    // Get user profile
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                userService.getUserProfile(userId)
        );
    }
}
