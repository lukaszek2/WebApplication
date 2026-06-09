package com.edututor.controller;

import com.edututor.dto.category.CategoryRequest;
import com.edututor.dto.category.CategoryResponse;
import com.edututor.dto.course.CourseDetailResponse;
import com.edututor.dto.course.CourseRequest;
import com.edututor.dto.course.CourseResponse;
import com.edututor.dto.profile.ProfileResponse;
import com.edututor.dto.resource.ResourceRequest;
import com.edututor.dto.resource.ResourceResponse;
import com.edututor.dto.section.SectionRequest;
import com.edututor.dto.section.SectionResponse;
import com.edututor.service.*;
import com.edututor.util.FileStorageService;
import com.edututor.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final SecurityUtils securityUtils;
    private final CategoryService categoryService;
    private final CourseService courseService;
    private final SectionService sectionService;
    private final ResourceService resourceService;
    private final EnrollmentService enrollmentService;
    private final TeacherDashboardService dashboardService;
    private final FileStorageService fileStorageService;

    public TeacherController(SecurityUtils securityUtils, CategoryService categoryService,
                              CourseService courseService, SectionService sectionService,
                              ResourceService resourceService, EnrollmentService enrollmentService,
                              TeacherDashboardService dashboardService, FileStorageService fileStorageService) {
        this.securityUtils = securityUtils;
        this.categoryService = categoryService;
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.resourceService = resourceService;
        this.enrollmentService = enrollmentService;
        this.dashboardService = dashboardService;
        this.fileStorageService = fileStorageService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard(currentUserId()));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(dashboardService.getAnalytics(currentUserId()));
    }

    // Categories
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(request, currentUserId()));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Courses
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getCourses() {
        return ResponseEntity.ok(courseService.getTeacherCourses(currentUserId()));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(request, currentUserId()));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDetailResponse> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseDetail(id));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id,
                                                        @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, currentUserId()));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id, currentUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/courses/{id}/publish")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.publishCourse(id, currentUserId()));
    }

    // Sections
    @GetMapping("/courses/{id}/sections")
    public ResponseEntity<List<SectionResponse>> getSections(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSections(id));
    }

    @PostMapping("/courses/{id}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long id,
                                                          @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sectionService.create(id, request));
    }

    @PutMapping("/courses/{id}/sections/{sid}")
    public ResponseEntity<SectionResponse> updateSection(@PathVariable Long id, @PathVariable Long sid,
                                                          @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.update(id, sid, request));
    }

    @DeleteMapping("/courses/{id}/sections/{sid}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @PathVariable Long sid) {
        sectionService.delete(id, sid);
        return ResponseEntity.noContent().build();
    }

    // Resources
    @GetMapping("/sections/{sid}/resources")
    public ResponseEntity<List<ResourceResponse>> getResources(@PathVariable Long sid) {
        return ResponseEntity.ok(resourceService.getResources(sid));
    }

    @PostMapping("/sections/{sid}/resources")
    public ResponseEntity<ResourceResponse> createResource(@PathVariable Long sid,
                                                            @Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.create(sid, request));
    }

    @PutMapping("/resources/{rid}")
    public ResponseEntity<ResourceResponse> updateResource(@PathVariable Long rid,
                                                            @Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.ok(resourceService.update(rid, request));
    }

    @DeleteMapping("/resources/{rid}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long rid) {
        resourceService.delete(rid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resources/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String path = fileStorageService.store(file);
        return ResponseEntity.ok(Map.of("filePath", path));
    }

    // Students
    @GetMapping("/courses/{id}/students")
    public ResponseEntity<List<ProfileResponse>> getStudents(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getStudentsInCourse(id));
    }

    @PostMapping("/courses/{id}/students/{sid}")
    public ResponseEntity<Void> enrollStudent(@PathVariable Long id, @PathVariable Long sid) {
        enrollmentService.enrollStudent(id, sid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/courses/{id}/students/{sid}")
    public ResponseEntity<Void> unenrollStudent(@PathVariable Long id, @PathVariable Long sid) {
        enrollmentService.unenrollStudent(id, sid);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId() {
        return securityUtils.getCurrentUser().getId();
    }
}
