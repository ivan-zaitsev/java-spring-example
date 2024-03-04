package com.example.security.testdata.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Configuration
public class AuthenticationConfigurer {

    private static final Class<? extends Filter> TARGET_FILTER_CLASS = BearerTokenAuthenticationFilter.class;

    private final Set<SecurityFilterChain> securityFilterChains;

    private Authentication authentication;

    public AuthenticationConfigurer(Set<SecurityFilterChain> securityFilterChains) {
        this.securityFilterChains = securityFilterChains;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @PostConstruct
    public void initialize() {
        for (SecurityFilterChain chain : securityFilterChains) {
            int filterIndex = findAuthenticationFilterIndex(chain.getFilters());
            if (filterIndex != -1) {
                chain.getFilters().add(filterIndex, new AuthenticationFilter());
            }
        }
    }

    private int findAuthenticationFilterIndex(List<Filter> filters) {
        for (int i = 0; i < filters.size(); i++) {
            if (TARGET_FILTER_CLASS.isInstance(filters.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Bean
    FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
        FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthenticationFilter());
        registration.addUrlPatterns("*");
        registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER);
        return registration;
    }

    public class AuthenticationFilter implements Filter {

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
