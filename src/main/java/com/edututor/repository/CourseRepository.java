package com.edututor.repository;

import com.edututor.entity.Course;
import com.edututor.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByTeacherId(Long teacherId);
    long countByTeacherId(Long teacherId);
}
