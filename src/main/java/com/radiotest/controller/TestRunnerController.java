package com.radiotest.controller;

import com.radiotest.framework.TestRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test-runner")
@RequiredArgsConstructor
public class TestRunnerController {
    private final TestRunner testRunner;

    @PostMapping("/run/{testCaseId}")
    public ResponseEntity<Map<String, String>> runTest(@PathVariable String testCaseId) {
        try {
            testRunner.runTest(testCaseId);
            return ResponseEntity.ok(Map.of("status", "started", "testCaseId", testCaseId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/run/id/{id}")
    public ResponseEntity<Map<String, String>> runTestById(@PathVariable Long id) {
        try {
            testRunner.runTest(id);
            return ResponseEntity.ok(Map.of("status", "started", "id", id.toString()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/run/technology/{technology}")
    public ResponseEntity<Map<String, String>> runTestsByTechnology(@PathVariable String technology) {
        try {
            testRunner.runTestsByTechnology(technology);
            return ResponseEntity.ok(Map.of("status", "started", "technology", technology));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/run/category/{category}")
    public ResponseEntity<Map<String, String>> runTestsByCategory(@PathVariable String category) {
        try {
            testRunner.runTestsByCategory(category);
            return ResponseEntity.ok(Map.of("status", "started", "category", category));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/run/all")
    public ResponseEntity<Map<String, String>> runAllEnabledTests() {
        try {
            testRunner.runAllEnabledTests();
            return ResponseEntity.ok(Map.of("status", "started"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}

