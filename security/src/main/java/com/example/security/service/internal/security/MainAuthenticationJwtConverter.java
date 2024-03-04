package com.example.security.service.internal.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.List;

public class MainAuthenticationJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    public static final String SUBJECT_CLAIM = JwtClaimNames.SUB;
    public static final String AUDIENCE_CLAIM = JwtClaimNames.AUD;

    private static final String SCOPE_CLAIM = "scp";
    private static final String SCOPE_PREFIX = "SCOPE_";

    private static final String ROLE_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
    private final JwtGrantedAuthoritiesConverter roleConverter = new JwtGrantedAuthoritiesConverter();

    public MainAuthenticationJwtConverter() {
        scopeConverter.setAuthoritiesClaimName(SCOPE_CLAIM);
        scopeConverter.setAuthorityPrefix(SCOPE_PREFIX);

        roleConverter.setAuthoritiesClaimName(ROLE_CLAIM);
        roleConverter.setAuthorityPrefix(ROLE_PREFIX);
    }

    @Override
    public final AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(scopeConverter.convert(jwt));
        authorities.addAll(roleConverter.convert(jwt));

        String principalClaimValue = jwt.getClaimAsString(SUBJECT_CLAIM);
        return new JwtAuthenticationToken(jwt, authorities, principalClaimValue);
    }

}
