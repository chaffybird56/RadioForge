package com.radiotest.controller;

import com.radiotest.model.TestExecution;
import com.radiotest.service.TestExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/test-executions")
@RequiredArgsConstructor
public class TestExecutionController {
    private final TestExecutionService testExecutionService;

    @GetMapping
    public ResponseEntity<List<TestExecution>> getAllExecutions() {
        return ResponseEntity.ok(testExecutionService.getAllExecutions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestExecution> getExecutionById(@PathVariable Long id) {
        return testExecutionService.getExecutionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test-case/{testCaseId}")
    public ResponseEntity<List<TestExecution>> getExecutionsByTestCaseId(@PathVariable String testCaseId) {
        return ResponseEntity.ok(testExecutionService.getExecutionsByTestCaseId(testCaseId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TestExecution>> getExecutionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(testExecutionService.getExecutionsByStatus(status));
    }

    @GetMapping("/technology/{technology}")
    public ResponseEntity<List<TestExecution>> getExecutionsByTechnology(@PathVariable String technology) {
        return ResponseEntity.ok(testExecutionService.getExecutionsByTechnology(technology));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TestExecution>> getExecutionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(testExecutionService.getExecutionsByDateRange(start, end));
    }

    @GetMapping("/recent/test-case/{testCaseId}")
    public ResponseEntity<List<TestExecution>> getRecentExecutionsByTestCaseId(@PathVariable String testCaseId) {
        return ResponseEntity.ok(testExecutionService.getRecentExecutionsByTestCaseId(testCaseId));
    }

    @PostMapping
    public ResponseEntity<TestExecution> createExecution(@RequestBody TestExecution execution) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(testExecutionService.createExecution(execution));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestExecution> updateExecution(@PathVariable Long id, @RequestBody TestExecution execution) {
        try {
            return ResponseEntity.ok(testExecutionService.updateExecution(id, execution));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TestExecution> completeExecution(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String errorMessage) {
        try {
            return ResponseEntity.ok(testExecutionService.completeExecution(id, status, errorMessage));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> countByStatus(@RequestParam String status) {
        return ResponseEntity.ok(testExecutionService.countByStatus(status));
    }
}

