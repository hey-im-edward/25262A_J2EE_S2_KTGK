package com.example.courseregistration.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.courseregistration.entity.Course;
import com.example.courseregistration.entity.Enrollment;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.EnrollmentRepository;

@Service("enrollmentService")
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentService(
        EnrollmentRepository enrollmentRepository,
        StudentService studentService,
        CourseService courseService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @Transactional
    public void enrollCourse(String identifier, Long courseId) {
        Student student = studentService.getRequiredByIdentifier(identifier);
        Course course = courseService.getRequiredCourse(courseId);

        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), courseId)) {
            throw new IllegalArgumentException("Bạn đã đăng ký học phần này rồi");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollments(String identifier) {
        Student student = studentService.getRequiredByIdentifier(identifier);
        return enrollmentRepository.findByStudent_IdOrderByEnrollDateDesc(student.getId());
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(String identifier, Long courseId) {
        if (!StringUtils.hasText(identifier) || courseId == null) {
            return false;
        }
        try {
            Student student = studentService.getRequiredByIdentifier(identifier);
            return enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), courseId);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
