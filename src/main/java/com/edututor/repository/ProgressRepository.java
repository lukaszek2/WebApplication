package com.edututor.repository;

import com.edututor.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByStudentIdAndResourceId(Long studentId, Long resourceId);
    boolean existsByStudentIdAndResourceId(Long studentId, Long resourceId);
    List<Progress> findByStudentId(Long studentId);
    long countByStudentId(Long studentId);
    List<Progress> findByCompletedAtAfter(LocalDateTime date);
}
