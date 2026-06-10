package com.edututor.service;

import com.edututor.dto.admin.SystemLogResponse;
import com.edututor.entity.SystemLog;
import com.edututor.repository.SystemLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    public void log(String action, String details, Long userId, String userEmail) {
        systemLogRepository.save(new SystemLog(action, details, userId, userEmail));
    }

    public List<SystemLogResponse> getLogs() {
        return systemLogRepository.findTop100ByOrderByCreatedAtDesc()
                .stream()
                .map(SystemLogResponse::from)
                .collect(Collectors.toList());
    }
}
