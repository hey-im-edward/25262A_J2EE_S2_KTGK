package com.example.courseregistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseregistration.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<Enrollment> findByStudent_IdOrderByEnrollDateDesc(Long studentId);

    @Modifying
    @Query("delete from Enrollment e where e.course.id = :courseId")
    void deleteAllByCourseId(@Param("courseId") Long courseId);
}
