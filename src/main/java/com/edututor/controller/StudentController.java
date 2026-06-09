package com.edututor.controller;

import com.edututor.dto.course.CourseResponse;
import com.edututor.dto.profile.ProfileRequest;
import com.edututor.dto.profile.ProfileResponse;
import com.edututor.entity.User;
import com.edututor.repository.EnrollmentRepository;
import com.edututor.repository.UserRepository;
import com.edututor.service.ProgressService;
import com.edututor.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StudentController {

    private final SecurityUtils securityUtils;
    private final EnrollmentRepository enrollmentRepository;
    private final ProgressService progressService;
    private final UserRepository userRepository;

    public StudentController(SecurityUtils securityUtils, EnrollmentRepository enrollmentRepository,
                              ProgressService progressService, UserRepository userRepository) {
        this.securityUtils = securityUtils;
        this.enrollmentRepository = enrollmentRepository;
        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        User user = securityUtils.getCurrentUser();
        long enrolled = enrollmentRepository.countByStudentId(user.getId());
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "name", user.getName(),
                "role", user.getRole(),
                "enrolledCourses", enrolled
        ));
    }

    @GetMapping("/api/my-courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        User user = securityUtils.getCurrentUser();
        List<CourseResponse> courses = enrollmentRepository.findByStudentId(user.getId()).stream()
                .map(e -> CourseResponse.from(e.getCourse()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/api/progress/{resourceId}/complete")
    public ResponseEntity<Void> markComplete(@PathVariable Long resourceId) {
        User user = securityUtils.getCurrentUser();
        progressService.markComplete(user.getId(), resourceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/profile")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(ProfileResponse.from(securityUtils.getCurrentUser()));
    }

    @PutMapping("/api/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody ProfileRequest request) {
        User user = securityUtils.getCurrentUser();
        user.setName(request.getName());
        return ResponseEntity.ok(ProfileResponse.from(userRepository.save(user)));
    }
}
