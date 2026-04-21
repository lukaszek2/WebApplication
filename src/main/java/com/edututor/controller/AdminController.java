package com.edututor.controller;

import com.edututor.dto.admin.AdminStatsResponse;
import com.edututor.dto.admin.RoleUpdateRequest;
import com.edututor.dto.profile.ProfileResponse;
import com.edututor.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<ProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ProfileResponse> updateRole(@PathVariable Long id,
                                                       @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateRole(id, request.getRole()));
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<ProfileResponse> suspendUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.suspendUser(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/logs")
    public ResponseEntity<Map<String, String>> getLogs() {
        return ResponseEntity.ok(Map.of("message", "Log system not yet implemented"));
    }
}
