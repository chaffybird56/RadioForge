package com.radiotest.service;

import com.radiotest.model.TestExecution;
import com.radiotest.repository.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestExecutionService {
    private final TestExecutionRepository testExecutionRepository;

    public List<TestExecution> getAllExecutions() {
        return testExecutionRepository.findAll();
    }

    public Optional<TestExecution> getExecutionById(Long id) {
        return testExecutionRepository.findById(id);
    }

    public List<TestExecution> getExecutionsByTestCaseId(String testCaseId) {
        return testExecutionRepository.findByTestCaseId(testCaseId);
    }

    public List<TestExecution> getExecutionsByStatus(String status) {
        return testExecutionRepository.findByStatus(status);
    }

    public List<TestExecution> getExecutionsByTechnology(String technology) {
        return testExecutionRepository.findByTechnology(technology);
    }

    public List<TestExecution> getExecutionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return testExecutionRepository.findByStartTimeBetween(start, end);
    }

    public List<TestExecution> getRecentExecutionsByTestCaseId(String testCaseId) {
        return testExecutionRepository.findRecentExecutionsByTestCaseId(testCaseId);
    }

    @Transactional
    public TestExecution createExecution(TestExecution execution) {
        if (execution.getStartTime() == null) {
            execution.setStartTime(LocalDateTime.now());
        }
        if (execution.getStatus() == null) {
            execution.setStatus("RUNNING");
        }
        return testExecutionRepository.save(execution);
    }

    @Transactional
    public TestExecution updateExecution(Long id, TestExecution execution) {
        TestExecution existing = testExecutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestExecution not found with id: " + id));
        
        existing.setStatus(execution.getStatus());
        existing.setEndTime(execution.getEndTime());
        existing.setDurationMs(execution.getDurationMs());
        existing.setMeasurements(execution.getMeasurements());
        existing.setPowerLevel(execution.getPowerLevel());
        existing.setFrequencyHz(execution.getFrequencyHz());
        existing.setEvm(execution.getEvm());
        existing.setAcpr(execution.getAcpr());
        existing.setErrorMessage(execution.getErrorMessage());
        existing.setTestLog(execution.getTestLog());
        
        return testExecutionRepository.save(existing);
    }

    @Transactional
    public TestExecution completeExecution(Long id, String status, String errorMessage) {
        TestExecution execution = testExecutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestExecution not found with id: " + id));
        
        execution.setStatus(status);
        execution.setEndTime(LocalDateTime.now());
        if (execution.getStartTime() != null) {
            long duration = java.time.Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis();
            execution.setDurationMs(duration);
        }
        if (errorMessage != null) {
            execution.setErrorMessage(errorMessage);
        }
        
        return testExecutionRepository.save(execution);
    }

    public Long countByStatus(String status) {
        return testExecutionRepository.countByStatus(status);
    }
}

