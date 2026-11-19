
package com.fitness.userservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitness.userservice.DTO.Registerrequest;
import com.fitness.userservice.DTO.UserResponse;
import com.fitness.userservice.Repository.UserRepository;
import com.fitness.userservice.enitites.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class userService {

    private final UserRepository userrepo;

    @Transactional
    public UserResponse register(Registerrequest request) {

        User user;

        // 1. Check whether Keycloak user already exists
        if (request.getKeycloakId() != null) {

            user = userrepo.findByKeycloakId(
                    request.getKeycloakId()
            ).orElse(null);

            if (user != null) {
                return convertToResponse(user);
            }
        }

        // 2. Check whether email already exists
        user = userrepo.findByEmail(request.getEmail())
                .orElse(null);

        if (user != null) {

            // Prevent linking an email already linked to another
            // Keycloak account
            if (user.getKeycloakId() != null
                    && !user.getKeycloakId().equals(
                            request.getKeycloakId())) {

                throw new IllegalStateException(
                        "Email is already linked to another account"
                );
            }

            // Link existing user with Keycloak
            user.setKeycloakId(request.getKeycloakId());

            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            User updatedUser = userrepo.save(user);

            return convertToResponse(updatedUser);
        }

        // 3. Create a completely new user
        user = new User();

        user.setEmail(request.getEmail());
        user.setKeycloakId(request.getKeycloakId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Only save password if supplied
        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            user.setPassword(request.getPassword());
        }

        User savedUser = userrepo.save(user);

        return convertToResponse(savedUser);
    }

    // Get user profile
    public UserResponse getUserProfile(String userId) {

        User user = userrepo.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + userId
                        )
                );

        return convertToResponse(user);
    }

    // Validate user using Keycloak ID
    public Boolean existByUserId(String userId) {

        return userrepo.existsByKeycloakId(userId);
    }

    // Convert Entity into DTO
    private UserResponse convertToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setKeycloakId(user.getKeycloakId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
