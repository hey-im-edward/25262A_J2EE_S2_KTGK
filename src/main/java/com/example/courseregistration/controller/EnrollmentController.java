package com.example.courseregistration.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.courseregistration.service.EnrollmentService;

@Controller
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll/course/{courseId}")
    public String enrollCourse(
        @PathVariable Long courseId,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            enrollmentService.enrollCourse(authentication.getName(), courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký học phần thành công.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/home";
    }

    @GetMapping("/enroll/my-courses")
    public String myCourses(Authentication authentication, Model model) {
        model.addAttribute("enrollments", enrollmentService.getStudentEnrollments(authentication.getName()));
        return "enrollment/my-courses";
    }
}
