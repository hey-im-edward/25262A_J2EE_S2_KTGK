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

import com.example.courseregistration.dto.CategoryForm;
import com.example.courseregistration.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/category-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        prepareForm(model, new CategoryForm(), false, null);
        return "admin/category-form";
    }

    @PostMapping("/create")
    public String createCategory(
        @Valid @ModelAttribute("categoryForm") CategoryForm categoryForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, categoryForm, false, null);
            return "admin/category-form";
        }

        try {
            categoryService.createCategory(categoryForm);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm danh mục mới.");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("categoryError", exception.getMessage());
            prepareForm(model, categoryForm, false, null);
            return "admin/category-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        prepareForm(model, categoryService.toForm(categoryService.getRequiredCategory(id)), true, id);
        return "admin/category-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(
        @PathVariable Long id,
        @Valid @ModelAttribute("categoryForm") CategoryForm categoryForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, categoryForm, true, id);
            return "admin/category-form";
        }

        try {
            categoryService.updateCategory(id, categoryForm);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật danh mục.");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("categoryError", exception.getMessage());
            prepareForm(model, categoryForm, true, id);
            return "admin/category-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa danh mục.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/categories";
    }

    private void prepareForm(Model model, CategoryForm categoryForm, boolean editing, Long categoryId) {
        model.addAttribute("categoryForm", categoryForm);
        model.addAttribute("editing", editing);
        model.addAttribute("categoryId", categoryId);
    }
}
