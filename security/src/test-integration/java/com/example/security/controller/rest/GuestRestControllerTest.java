package com.example.security.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GuestRestControllerTest extends ControllerTestBase {

    @Test
    void findAll_sholdReturnOk_whenAuthenticationIsNotPresent() {
        String url = "/api/v1/guests";

        ResponseEntity<Void> response = buildRestClient()
            .get()
            .uri(url)
            .retrieve()
            .toBodilessEntity();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
