package com.edututor.repository;

import com.edututor.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findBySectionIdOrderByOrderIndex(Long sectionId);
    long countBySectionCourseId(Long courseId);
}
