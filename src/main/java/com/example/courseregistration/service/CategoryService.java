package com.example.courseregistration.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.courseregistration.dto.CategoryForm;
import com.example.courseregistration.entity.Category;
import com.example.courseregistration.repository.CategoryRepository;
import com.example.courseregistration.repository.CourseRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional(readOnly = true)
    public Category getRequiredCategory(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
    }

    @Transactional
    public Category createCategory(CategoryForm form) {
        String name = form.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryForm form) {
        Category category = getRequiredCategory(id);
        String name = form.getName().trim();
        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        category.setName(name);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getRequiredCategory(id);
        if (courseRepository.existsByCategory_Id(id)) {
            throw new IllegalArgumentException("Danh mục đang được sử dụng bởi học phần, không thể xóa");
        }
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryForm toForm(Category category) {
        CategoryForm form = new CategoryForm();
        form.setName(category.getName());
        return form;
    }
}
