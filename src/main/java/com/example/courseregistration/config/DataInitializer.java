package com.example.courseregistration.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.courseregistration.entity.Category;
import com.example.courseregistration.entity.Course;
import com.example.courseregistration.entity.Role;
import com.example.courseregistration.entity.RoleName;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.CategoryRepository;
import com.example.courseregistration.repository.CourseRepository;
import com.example.courseregistration.repository.RoleRepository;
import com.example.courseregistration.repository.StudentRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(
        RoleRepository roleRepository,
        StudentRepository studentRepository,
        CategoryRepository categoryRepository,
        CourseRepository courseRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMIN)));
            Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.STUDENT)));

            if (studentRepository.findByUsernameIgnoreCase("admin").isEmpty()) {
                Student admin = new Student();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@coursehub.local");
                admin.setRoles(new java.util.LinkedHashSet<>(Set.of(adminRole)));
                studentRepository.save(admin);
            }

            if (studentRepository.findByUsernameIgnoreCase("student").isEmpty()) {
                Student student = new Student();
                student.setUsername("student");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setEmail("student@coursehub.local");
                student.setRoles(new java.util.LinkedHashSet<>(Set.of(studentRole)));
                studentRepository.save(student);
            }

            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                    new Category("Công nghệ phần mềm"),
                    new Category("Mạng máy tính"),
                    new Category("Cơ sở dữ liệu"),
                    new Category("Trí tuệ nhân tạo")
                ));
            }

            if (courseRepository.count() == 0) {
                Map<String, Category> categories = new LinkedHashMap<>();
                categoryRepository.findAll().forEach(category -> categories.put(category.getName(), category));

                courseRepository.saveAll(List.of(
                    buildCourse("Lập trình Java nâng cao", "/images/course-java.svg", 3, "ThS. Trần Minh", categories.get("Công nghệ phần mềm")),
                    buildCourse("Phân tích và thiết kế hệ thống", "/images/course-analysis.svg", 3, "ThS. Nguyễn Bình", categories.get("Công nghệ phần mềm")),
                    buildCourse("Spring Boot thực chiến", "/images/course-spring.svg", 4, "ThS. Lê Huy", categories.get("Công nghệ phần mềm")),
                    buildCourse("Quản trị cơ sở dữ liệu", "/images/course-database.svg", 3, "ThS. Đoàn Phúc", categories.get("Cơ sở dữ liệu")),
                    buildCourse("Thiết kế mạng doanh nghiệp", "/images/course-network.svg", 3, "ThS. Phạm Sơn", categories.get("Mạng máy tính")),
                    buildCourse("An toàn bảo mật thông tin", "/images/course-security.svg", 2, "ThS. Võ Nam", categories.get("Mạng máy tính")),
                    buildCourse("Khai phá dữ liệu", "/images/course-ai.svg", 3, "ThS. Huỳnh Khôi", categories.get("Trí tuệ nhân tạo")),
                    buildCourse("Machine Learning cơ bản", "/images/course-ml.svg", 4, "ThS. Hoàng Anh", categories.get("Trí tuệ nhân tạo")),
                    buildCourse("Hệ quản trị SQL Server", "/images/course-sql.svg", 3, "ThS. Trần Trang", categories.get("Cơ sở dữ liệu")),
                    buildCourse("Kiểm thử phần mềm", "/images/course-testing.svg", 2, "ThS. Phan Hà", categories.get("Công nghệ phần mềm"))
                ));
            }
        };
    }

    private Course buildCourse(String name, String image, int credits, String lecturer, Category category) {
        Course course = new Course();
        course.setName(name);
        course.setImage(image);
        course.setCredits(credits);
        course.setLecturer(lecturer);
        course.setCategory(category);
        return course;
    }
}
