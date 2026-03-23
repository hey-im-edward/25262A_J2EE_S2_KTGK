package com.example.courseregistration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.courseregistration.entity.Category;
import com.example.courseregistration.entity.Course;
import com.example.courseregistration.entity.Enrollment;
import com.example.courseregistration.entity.Role;
import com.example.courseregistration.entity.RoleName;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.CategoryRepository;
import com.example.courseregistration.repository.CourseRepository;
import com.example.courseregistration.repository.EnrollmentRepository;
import com.example.courseregistration.repository.RoleRepository;
import com.example.courseregistration.repository.StudentRepository;
import com.example.courseregistration.service.CourseService;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:course_delete_test;DB_CLOSE_DELAY=-1;MODE=LEGACY",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CourseDeletionIntegrationTests {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deleteCourseShouldAlsoRemoveEnrollments() {
        Role studentRole = roleRepository.findByName(RoleName.STUDENT)
            .orElseGet(() -> roleRepository.save(new Role(RoleName.STUDENT)));

        Category category = new Category("Test Category " + UUID.randomUUID());
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setName("Test Course " + UUID.randomUUID());
        course.setImage("/images/course-java.svg");
        course.setCredits(3);
        course.setLecturer("Test Lecturer");
        course.setCategory(category);
        course = courseRepository.save(course);

        Student student = new Student();
        student.setUsername("student" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        student.setEmail("student" + UUID.randomUUID() + "@mail.com");
        student.setPassword(passwordEncoder.encode("123456"));
        student.setRoles(new LinkedHashSet<>(Set.of(studentRole)));
        student = studentRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        courseService.deleteCourse(course.getId());

        assertFalse(courseRepository.existsById(course.getId()));
        assertFalse(enrollmentRepository.existsByStudent_IdAndCourse_Id(student.getId(), course.getId()));
        assertTrue(categoryRepository.existsById(category.getId()));
    }
}
