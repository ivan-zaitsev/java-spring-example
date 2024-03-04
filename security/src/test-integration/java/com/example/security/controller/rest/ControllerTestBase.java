package com.example.security.controller.rest;

import com.example.security.testdata.service.AuthenticationConfigurer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.Set;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ControllerTestBase {

    @LocalServerPort
    private int localPort;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    protected AuthenticationConfigurer authenticationConfigurer;

    @BeforeEach
    void reset() {
        authenticationConfigurer.setAuthentication(null);
    }

    public RestClient buildRestClient() {
        return restClientBuilder
            .defaultStatusHandler(new NoOpResponseErrorHandler())
            .baseUrl("http://localhost:" + localPort)
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build();
    }

    public Authentication buildMainAuthentication(String subject) {
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim(JwtClaimNames.SUB, subject)
            .build();

        return new JwtAuthenticationToken(jwt, Set.of(), subject);
    }

    private static class NoOpResponseErrorHandler extends DefaultResponseErrorHandler {

        @Override
        public void handleError(ClientHttpResponse response) {
        }

    }

}
