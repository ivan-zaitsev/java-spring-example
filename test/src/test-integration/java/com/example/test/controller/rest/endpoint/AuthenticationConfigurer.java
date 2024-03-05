package com.example.test.controller.rest.endpoint;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Component
public class AuthenticationConfigurer {

    private Authentication authentication;

    private final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;

    private final Set<SecurityFilterChain> securityFilterChains;

    public AuthenticationConfigurer(Set<SecurityFilterChain> securityFilterChains) {
        this.securityFilterChains = securityFilterChains;
        this.authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @PostConstruct
    public void initialize() {
        for (SecurityFilterChain chain : securityFilterChains) {
            chain.getFilters().add(chain.getFilters().size() - 1, new AuthorizationFilter());
        }
    }

    public class AuthorizationFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        }

    }

}
