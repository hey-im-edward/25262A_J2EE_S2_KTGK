package com.example.courseregistration.service;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthenticatedUserService {

    public Optional<String> resolveCurrentUserIdentifier(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            String localUsername = oauth2User.getAttribute("localUsername");
            if (StringUtils.hasText(localUsername)) {
                return Optional.of(localUsername);
            }

            String email = oauth2User.getAttribute("email");
            if (StringUtils.hasText(email)) {
                return Optional.of(email);
            }
        }

        if (principal instanceof UserDetails userDetails && StringUtils.hasText(userDetails.getUsername())) {
            return Optional.of(userDetails.getUsername());
        }

        if (StringUtils.hasText(authentication.getName())) {
            return Optional.of(authentication.getName());
        }

        return Optional.empty();
    }

    public Optional<String> resolveCurrentUserDisplayName(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            String name = oauth2User.getAttribute("name");
            if (StringUtils.hasText(name)) {
                return Optional.of(name);
            }

            String email = oauth2User.getAttribute("email");
            if (StringUtils.hasText(email)) {
                return Optional.of(email);
            }

            String localUsername = oauth2User.getAttribute("localUsername");
            if (StringUtils.hasText(localUsername)) {
                return Optional.of(localUsername);
            }
        }

        if (principal instanceof UserDetails userDetails && StringUtils.hasText(userDetails.getUsername())) {
            return Optional.of(userDetails.getUsername());
        }

        if (StringUtils.hasText(authentication.getName())) {
            return Optional.of(authentication.getName());
        }

        return Optional.empty();
    }

    public String getRequiredCurrentUserIdentifier(Authentication authentication) {
        return resolveCurrentUserIdentifier(authentication)
            .orElseThrow(() -> new IllegalArgumentException("Không xác định được người dùng hiện tại"));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
