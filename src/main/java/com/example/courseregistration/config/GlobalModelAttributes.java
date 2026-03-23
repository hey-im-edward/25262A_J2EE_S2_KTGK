package com.example.courseregistration.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private static final Set<String> DEMO_GOOGLE_CLIENT_IDS = Set.of("", "demo-google-client-id");

    @Value("${app.google.client-id:}")
    private String googleClientId;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Authentication authentication) {
        boolean authenticated = authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);

        boolean admin = false;
        boolean student = false;
        String currentUserName = null;

        if (authenticated) {
            currentUserName = authentication.getName();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    admin = true;
                }
                if ("ROLE_STUDENT".equals(authority.getAuthority())) {
                    student = true;
                }
            }
        }

        model.addAttribute("isAuthenticated", authenticated);
        model.addAttribute("isAdmin", admin);
        model.addAttribute("isStudent", student);
        model.addAttribute("currentUserName", currentUserName);
        model.addAttribute("googleLoginReady", !DEMO_GOOGLE_CLIENT_IDS.contains(googleClientId));
    }
}
