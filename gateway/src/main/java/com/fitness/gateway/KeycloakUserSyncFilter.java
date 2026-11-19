
package com.fitness.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.ResponseStatusException;

import com.fitness.gateway.user.Registerrequest;
import com.fitness.gateway.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        // Get authenticated JWT principal
        return exchange.getPrincipal()

                .ofType(JwtAuthenticationToken.class)

                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authentication required"
                        )
                ))

                .flatMap(authentication -> {

                    // Extract Keycloak user ID
                    String userId = authentication
                            .getToken()
                            .getSubject();

                    // Extract user details
                    Registerrequest request =
                            getUserDetails(authentication);

                    // Validate user
                    return userService.validateUser(userId)

                            .flatMap(exists -> {

                                // User does not exist
                                if (!exists) {

                                    log.info(
                                            "User does not exist: {}",
                                            userId
                                    );

                                    // Register user
                                    return userService.registerUser(request)

                                            .doOnSuccess(response ->
                                                    log.info(
                                                            "User registered successfully: {}",
                                                            userId
                                                    )
                                            )

                                            .then(Mono.defer(() ->
                                                    continueFilter(
                                                            exchange,
                                                            chain,
                                                            userId
                                                    )
                                            ));
                                }

                                // User already exists
                                log.info(
                                        "User already exists: {}",
                                        userId
                                );

                                return continueFilter(
                                        exchange,
                                        chain,
                                        userId
                                );
                            });
                });
    }

    // Extract details from Keycloak JWT
    private Registerrequest getUserDetails(
            JwtAuthenticationToken authentication) {

        Registerrequest request = new Registerrequest();

        request.setEmail(
                authentication.getToken()
                        .getClaimAsString("email")
        );

        request.setKeycloakId(
                authentication.getToken()
                        .getSubject()
        );

        request.setFirstName(
                authentication.getToken()
                        .getClaimAsString("given_name")
        );

        request.setLastName(
                authentication.getToken()
                        .getClaimAsString("family_name")
        );

        // Keycloak manages the password
        request.setPassword(null);

        return request;
    }

    // Continue filter chain
    private Mono<Void> continueFilter(
            ServerWebExchange exchange,
            WebFilterChain chain,
            String userId) {

        ServerHttpRequest modifiedRequest = exchange
                .getRequest()
                .mutate()
                .headers(headers -> {

                    headers.remove("X-User-ID");

                    headers.add("X-User-ID", userId);
                })
                .build();

        ServerWebExchange modifiedExchange = exchange
                .mutate()
                .request(modifiedRequest)
                .build();

        return chain.filter(modifiedExchange);
    }
}
