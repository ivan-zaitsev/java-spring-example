package com.example.test.controller.rest.endpoint;

import com.example.test.repository.jpa.PostgresContainerInitializer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgresContainerInitializer.class)
public class ControllerTestBase {

    @LocalServerPort
    private int localPort;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @PostConstruct
    private void initialize() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);

        restTemplateBuilder = restTemplateBuilder.requestFactory(() -> requestFactory);
        restTemplateBuilder = restTemplateBuilder.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        restTemplateBuilder = restTemplateBuilder.rootUri("http://localhost:" + localPort);
    }

    public TestRestTemplate buildRestTemplate() {
        return new TestRestTemplate(restTemplateBuilder);
    }

    public JwtAuthenticationToken buildMainAuthentication() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, "user")
                .build();
        return new JwtAuthenticationToken(jwt, List.of(), "user");
    }

}
