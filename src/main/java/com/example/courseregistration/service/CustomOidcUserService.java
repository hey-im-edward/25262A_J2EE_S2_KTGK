package com.example.courseregistration.service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.courseregistration.entity.Student;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final StudentService studentService;

    public CustomOidcUserService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        if (!StringUtils.hasText(name)) {
            name = oidcUser.getAttribute("name");
        }
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("missing_email"),
                "Google account không trả về email"
            );
        }

        Student student = studentService.findOrCreateGoogleStudent(email, name);
        Set<GrantedAuthority> authorities = new LinkedHashSet<>(oidcUser.getAuthorities());
        authorities.addAll(student.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
            .collect(Collectors.toSet()));

        Map<String, Object> claims = new LinkedHashMap<>(oidcUser.getClaims());
        claims.put("localUsername", student.getUsername());

        return new DefaultOidcUser(
            authorities,
            oidcUser.getIdToken(),
            new OidcUserInfo(claims),
            "email"
        );
    }
}
