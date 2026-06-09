package com.edututor.controller;

import com.edututor.dto.category.CategoryResponse;
import com.edututor.dto.course.CourseDetailResponse;
import com.edututor.dto.course.CourseResponse;
import com.edututor.dto.resource.ResourceResponse;
import com.edututor.service.CategoryService;
import com.edututor.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    public CourseController(CourseService courseService, CategoryService categoryService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getPublishedCourses() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseDetail(id));
    }

    @GetMapping("/courses/{id}/resources/{resourceId}")
    public ResponseEntity<ResourceResponse> getResource(@PathVariable Long id,
                                                         @PathVariable Long resourceId) {
        return ResponseEntity.ok(courseService.getResource(id, resourceId));
    }
}
