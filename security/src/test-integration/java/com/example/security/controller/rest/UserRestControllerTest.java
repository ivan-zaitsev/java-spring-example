package com.example.security.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRestControllerTest extends ControllerTestBase {

    @Test
    void finaAll_shouldReturnUnauthorized_whenAuthenticationIsNotPresent() {
        String url = "/api/v1/users";

        ResponseEntity<Void> response = buildRestClient()
            .get()
            .uri(url)
            .retrieve()
            .toBodilessEntity();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void finaAll_shouldReturnOk_whenAuthenticationIsPresent() {
        authenticationConfigurer.setAuthentication(buildMainAuthentication("username"));

        String url = "/api/v1/users";

        ResponseEntity<Void> response = buildRestClient()
            .get()
            .uri(url)
            .retrieve()
            .toBodilessEntity();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
