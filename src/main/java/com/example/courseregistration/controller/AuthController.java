package com.example.courseregistration.controller;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.courseregistration.dto.StudentRegistrationRequest;
import com.example.courseregistration.service.StudentService;

@Controller
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("studentRequest")) {
            model.addAttribute("studentRequest", new StudentRegistrationRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerStudent(
        @Valid @ModelAttribute("studentRequest") StudentRegistrationRequest studentRequest,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            studentService.registerStudent(studentRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công. Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("registerError", exception.getMessage());
            return "auth/register";
        }
    }
}
