package com.example.courseregistration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.EntityManager;

import com.example.courseregistration.dto.CourseForm;
import com.example.courseregistration.entity.Category;
import com.example.courseregistration.entity.Course;
import com.example.courseregistration.repository.CourseRepository;
import com.example.courseregistration.repository.EnrollmentRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryService categoryService;
    private final EnrollmentRepository enrollmentRepository;
    private final EntityManager entityManager;
    private final int pageSize;

    public CourseService(
        CourseRepository courseRepository,
        CategoryService categoryService,
        EnrollmentRepository enrollmentRepository,
        EntityManager entityManager,
        @Value("${app.page-size:5}") int pageSize
    ) {
        this.courseRepository = courseRepository;
        this.categoryService = categoryService;
        this.enrollmentRepository = enrollmentRepository;
        this.entityManager = entityManager;
        this.pageSize = pageSize;
    }

    @Transactional(readOnly = true)
    public Page<Course> findCourses(String keyword, int page) {
        int normalizedPage = Math.max(page, 0);
        PageRequest pageable = PageRequest.of(normalizedPage, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        if (!StringUtils.hasText(keyword)) {
            return courseRepository.findAll(pageable);
        }
        return courseRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public Course getRequiredCourse(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần"));
    }

    @Transactional
    public Course createCourse(CourseForm form) {
        Course course = new Course();
        mapForm(course, form);
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, CourseForm form) {
        Course course = getRequiredCourse(id);
        mapForm(course, form);
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        getRequiredCourse(id);
        enrollmentRepository.deleteAllByCourseId(id);
        entityManager.flush();
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CourseForm toForm(Course course) {
        CourseForm form = new CourseForm();
        form.setName(course.getName());
        form.setImage(course.getImage());
        form.setCredits(course.getCredits());
        form.setLecturer(course.getLecturer());
        form.setCategoryId(course.getCategory().getId());
        return form;
    }

    private void mapForm(Course course, CourseForm form) {
        Category category = categoryService.getRequiredCategory(form.getCategoryId());
        course.setName(form.getName().trim());
        course.setImage(form.getImage().trim());
        course.setCredits(form.getCredits());
        course.setLecturer(form.getLecturer().trim());
        course.setCategory(category);
    }
}
