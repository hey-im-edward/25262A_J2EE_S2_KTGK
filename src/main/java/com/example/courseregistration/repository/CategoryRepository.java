package com.example.courseregistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseregistration.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);
}
