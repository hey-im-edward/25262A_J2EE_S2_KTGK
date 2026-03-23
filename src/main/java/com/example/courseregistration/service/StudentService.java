package com.example.courseregistration.service;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.courseregistration.dto.StudentRegistrationRequest;
import com.example.courseregistration.entity.Role;
import com.example.courseregistration.entity.RoleName;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.RoleRepository;
import com.example.courseregistration.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
        StudentRepository studentRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Student registerStudent(StudentRegistrationRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (studentRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Student student = new Student();
        student.setUsername(username);
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setEmail(email);
        student.setRoles(new LinkedHashSet<>(Set.of(getRequiredRole(RoleName.STUDENT))));
        return studentRepository.save(student);
    }

    @Transactional
    public Student findOrCreateGoogleStudent(String email, String name) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Tài khoản Google không trả về email");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return studentRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseGet(() -> createGoogleStudent(normalizedEmail, name));
    }

    @Transactional(readOnly = true)
    public Student getRequiredByIdentifier(String identifier) {
        String normalizedIdentifier = identifier == null ? "" : identifier.trim();
        return studentRepository.findByUsernameIgnoreCase(normalizedIdentifier)
            .or(() -> studentRepository.findByEmailIgnoreCase(normalizedIdentifier.toLowerCase(Locale.ROOT)))
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên"));
    }

    private Student createGoogleStudent(String email, String name) {
        Student student = new Student();
        student.setEmail(email.trim().toLowerCase(Locale.ROOT));
        student.setUsername(generateUniqueUsername(email, name));
        student.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        student.setRoles(new LinkedHashSet<>(Set.of(getRequiredRole(RoleName.STUDENT))));
        return studentRepository.save(student);
    }

    private String generateUniqueUsername(String email, String name) {
        String base = name;
        if (!StringUtils.hasText(base)) {
            base = email.substring(0, email.indexOf('@'));
        }
        base = base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
        if (!StringUtils.hasText(base)) {
            base = "googleuser";
        }
        String candidate = base;
        int suffix = 1;
        while (studentRepository.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    private Role getRequiredRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalStateException("Thiếu role " + roleName));
    }
}
