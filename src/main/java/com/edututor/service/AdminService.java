package com.edututor.service;

import com.edututor.dto.admin.AdminStatsResponse;
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

    public AdminService(UserRepository userRepository, CourseRepository courseRepository,
                        ResourceRepository resourceRepository, ProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.resourceRepository = resourceRepository;
        this.progressRepository = progressRepository;
    }

    public List<ProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(ProfileResponse::from)
                .collect(Collectors.toList());
    }

    public ProfileResponse updateRole(Long userId, Role role) {
        User user = getUser(userId);
        user.setRole(role);
        return ProfileResponse.from(userRepository.save(user));
    }

    public ProfileResponse suspendUser(Long userId) {
        User user = getUser(userId);
        user.setIsActive(false);
        return ProfileResponse.from(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
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

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
