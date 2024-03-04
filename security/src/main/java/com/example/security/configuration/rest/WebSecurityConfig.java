package com.example.security.configuration.rest;

import com.example.security.service.internal.security.HttpUnauthorizedEntryPoint;
import com.example.security.service.internal.security.MainAuthenticationJwtConverter;
import com.example.security.service.internal.security.MainAuthenticationRequestMatcher;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.SupplierJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final MainAuthenticationRequestMatcher mainAuthenticationRequestMatcher;

    public WebSecurityConfig(MainAuthenticationRequestMatcher mainAuthenticationRequestMatcher) {
        this.mainAuthenticationRequestMatcher = mainAuthenticationRequestMatcher;
    }

    @Bean
    SupplierJwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        return new SupplierJwtDecoder(() -> {
            String issuerUri = properties.getJwt().getIssuerUri();
            List<String> audiences = properties.getJwt().getAudiences();

            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
            jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(buildValidators(issuerUri, audiences)));
            return jwtDecoder;
        });
    }

    private List<OAuth2TokenValidator<Jwt>> buildValidators(String issuerUri, List<String> audiences) {
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(JwtValidators.createDefaultWithIssuer(issuerUri));
        validators.add(new JwtClaimValidator<List<String>>(MainAuthenticationJwtConverter.AUDIENCE_CLAIM,
            aud -> aud != null && !Collections.disjoint(aud, audiences)));
        return validators;
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/error");
    }

    @Bean
    @Order(1)
    SecurityFilterChain securityFilterChainMain(HttpSecurity http) throws Exception {
        configureRest(http);

        http.securityMatcher(mainAuthenticationRequestMatcher)
            .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt -> jwt
                .jwtAuthenticationConverter(new MainAuthenticationJwtConverter())))
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
                .anyRequest().denyAll());

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChainDefault(HttpSecurity http) throws Exception {
        configureRest(http);

        http.securityMatcher(AnyRequestMatcher.INSTANCE)
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(HttpMethod.GET, "/api/v1/guests").permitAll()
                .anyRequest().denyAll());

        http.exceptionHandling(exceptionHandling -> exceptionHandling
            .defaultAuthenticationEntryPointFor(new HttpUnauthorizedEntryPoint(), AnyRequestMatcher.INSTANCE));

        return http.build();
    }

    private void configureRest(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);
    }

}
