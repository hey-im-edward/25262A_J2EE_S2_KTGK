package com.example.courseregistration.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.courseregistration.dto.CourseForm;
import com.example.courseregistration.service.CategoryService;
import com.example.courseregistration.service.CourseService;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    public AdminCourseController(CourseService courseService, CategoryService categoryService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String manageCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "admin/course-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        prepareFormModel(model, new CourseForm(), false, null);
        return "admin/course-form";
    }

    @PostMapping("/create")
    public String createCourse(
        @Valid @ModelAttribute("courseForm") CourseForm courseForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, courseForm, false, null);
            return "admin/course-form";
        }

        courseService.createCourse(courseForm);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm học phần mới.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        prepareFormModel(model, courseService.toForm(courseService.getRequiredCourse(id)), true, id);
        return "admin/course-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCourse(
        @PathVariable Long id,
        @Valid @ModelAttribute("courseForm") CourseForm courseForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, courseForm, true, id);
            return "admin/course-form";
        }

        courseService.updateCourse(id, courseForm);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật học phần.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa học phần.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa học phần này.");
        }
        return "redirect:/admin/courses";
    }

    private void prepareFormModel(Model model, CourseForm courseForm, boolean editing, Long courseId) {
        model.addAttribute("courseForm", courseForm);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("editing", editing);
        model.addAttribute("courseId", courseId);
    }
}
