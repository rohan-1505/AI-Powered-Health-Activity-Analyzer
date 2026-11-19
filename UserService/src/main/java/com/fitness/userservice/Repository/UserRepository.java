
package com.fitness.userservice.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitness.userservice.enitites.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByKeycloakId(String keycloakId);

    Optional<User> findByKeycloakId(String keycloakId);
}
