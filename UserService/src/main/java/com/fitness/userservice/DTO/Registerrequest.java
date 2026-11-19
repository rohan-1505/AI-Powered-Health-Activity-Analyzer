
package com.fitness.userservice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Registerrequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    private String keycloakId;

    private String password;

    private String firstName;

    private String lastName;
}
