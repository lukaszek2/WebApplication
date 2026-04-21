package com.edututor.service;

import com.edututor.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherDashboardService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProgressRepository progressRepository;
    private final ResourceRepository resourceRepository;

    public TeacherDashboardService(CourseRepository courseRepository,
                                   EnrollmentRepository enrollmentRepository,
                                   ProgressRepository progressRepository,
                                   ResourceRepository resourceRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
        this.resourceRepository = resourceRepository;
    }

    public Map<String, Object> getDashboard(Long teacherId) {
        long totalCourses = courseRepository.countByTeacherId(teacherId);
        long totalStudents = courseRepository.findByTeacherId(teacherId).stream()
                .mapToLong(c -> enrollmentRepository.countByCourseId(c.getId()))
                .sum();

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalCourses", totalCourses);
        dashboard.put("totalStudents", totalStudents);
        return dashboard;
    }

    public Map<String, Object> getAnalytics(Long teacherId) {
        var courses = courseRepository.findByTeacherId(teacherId);

        // Enrollment chart: enrollments per course
        List<Map<String, Object>> enrollmentChart = courses.stream().map(c -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("courseId", c.getId());
            entry.put("title", c.getTitle());
            entry.put("enrollments", enrollmentRepository.countByCourseId(c.getId()));
            return entry;
        }).collect(Collectors.toList());

        // Activity timeline: completed resources in last 30 days grouped by day
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        Map<String, Long> timeline = progressRepository.findByCompletedAtAfter(since).stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCompletedAt().toLocalDate().toString(),
                        Collectors.counting()
                ));

        // Completion rate per course
        List<Map<String, Object>> completionRates = courses.stream().map(c -> {
            long totalResources = resourceRepository.countBySectionCourseId(c.getId());
            long totalEnrollments = enrollmentRepository.countByCourseId(c.getId());
            long completions = 0;
            if (totalResources > 0 && totalEnrollments > 0) {
                completions = progressRepository.findByCompletedAtAfter(LocalDateTime.MIN).stream()
                        .filter(p -> p.getResource().getSection().getCourse().getId().equals(c.getId()))
                        .count();
            }
            double rate = (totalResources * totalEnrollments > 0)
                    ? (double) completions / (totalResources * totalEnrollments) * 100 : 0;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("courseId", c.getId());
            entry.put("title", c.getTitle());
            entry.put("completionRate", Math.round(rate * 10.0) / 10.0);
            return entry;
        }).collect(Collectors.toList());

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("enrollmentChart", enrollmentChart);
        analytics.put("activityTimeline", timeline);
        analytics.put("completionRates", completionRates);
        return analytics;
    }
}
