package com.example.courseregistration.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseregistration.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByCategory_Id(Long categoryId);
}
