package com.edututor.service;

import com.edututor.dto.admin.AdminStatsResponse;
import com.edututor.dto.admin.SystemLogResponse;
import com.edututor.dto.profile.ProfileResponse;
import com.edututor.entity.Role;
import com.edututor.entity.User;
import com.edututor.exception.ResourceNotFoundException;
import com.edututor.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ResourceRepository resourceRepository;
    private final ProgressRepository progressRepository;
    private final SystemLogService systemLogService;

    public AdminService(UserRepository userRepository, CourseRepository courseRepository,
                        ResourceRepository resourceRepository, ProgressRepository progressRepository,
                        SystemLogService systemLogService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.resourceRepository = resourceRepository;
        this.progressRepository = progressRepository;
        this.systemLogService = systemLogService;
    }

    public List<ProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(ProfileResponse::from)
                .collect(Collectors.toList());
    }

    public ProfileResponse updateRole(Long userId, Role role) {
        User user = getUser(userId);
        String oldRole = user.getRole().name();
        user.setRole(role);
        User saved = userRepository.save(user);
        systemLogService.log("ROLE_CHANGE", "Zmiana roli " + user.getEmail() + ": " + oldRole + " → " + role.name(), userId, user.getEmail());
        return ProfileResponse.from(saved);
    }

    public ProfileResponse suspendUser(Long userId) {
        User user = getUser(userId);
        boolean wasActive = Boolean.TRUE.equals(user.getIsActive());
        user.setIsActive(!wasActive);
        User saved = userRepository.save(user);
        String action = wasActive ? "SUSPEND" : "ACTIVATE";
        systemLogService.log(action, (wasActive ? "Zawieszono konto: " : "Aktywowano konto: ") + user.getEmail(), userId, user.getEmail());
        return ProfileResponse.from(saved);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        systemLogService.log("DELETE_USER", "Usunięto konto: " + user.getEmail(), userId, user.getEmail());
        userRepository.deleteById(userId);
    }

    public AdminStatsResponse getStats() {
        long users = userRepository.count();
        long courses = courseRepository.count();
        long resources = resourceRepository.count();
        long monthlyActive = progressRepository
                .findByCompletedAtAfter(LocalDateTime.now().minusDays(30))
                .stream()
                .map(p -> p.getStudent().getId())
                .distinct()
                .count();
        return new AdminStatsResponse(users, courses, resources, monthlyActive);
    }

    public List<SystemLogResponse> getLogs() {
        return systemLogService.getLogs();
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
