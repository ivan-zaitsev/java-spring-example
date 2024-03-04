package com.example.security.service.internal.security;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class MainAuthenticationRequestMatcher implements RequestMatcher {

    private static final Class<? extends Authentication> AUTHENTICATION_CLASS = JwtAuthenticationToken.class;

    private final OAuth2ResourceServerProperties properties;
    private final BearerTokenResolver bearerTokenResolver;

    public MainAuthenticationRequestMatcher(OAuth2ResourceServerProperties properties) {
        this.properties = properties;
        this.bearerTokenResolver = new DefaultBearerTokenResolver();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return isAuthenticationTokenPresent(request) || isAuthenticationPresent();
    }

    private boolean isAuthenticationTokenPresent(HttpServletRequest request) {
        try {
            String token = bearerTokenResolver.resolve(request);
            String issuer = JWTParser.parse(token).getJWTClaimsSet().getIssuer();
            return properties.getJwt().getIssuerUri().equals(issuer);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAuthenticationPresent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && AUTHENTICATION_CLASS.equals(authentication.getClass());
    }

}
