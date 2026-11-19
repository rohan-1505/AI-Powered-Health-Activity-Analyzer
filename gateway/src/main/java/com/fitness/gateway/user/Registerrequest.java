
package com.fitness.gateway.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Registerrequest {

    private String email;

    private String keycloakId;

    private String firstName;

    private String lastName;

    private String password;
}
