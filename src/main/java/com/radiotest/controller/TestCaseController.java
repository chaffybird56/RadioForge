package com.radiotest.controller;

import com.radiotest.model.TestCase;
import com.radiotest.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-cases")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @GetMapping
    public ResponseEntity<List<TestCase>> getAllTestCases() {
        return ResponseEntity.ok(testCaseService.getAllTestCases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCase> getTestCaseById(@PathVariable Long id) {
        return testCaseService.getTestCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-test-case-id/{testCaseId}")
    public ResponseEntity<TestCase> getTestCaseByTestCaseId(@PathVariable String testCaseId) {
        return testCaseService.getTestCaseByTestCaseId(testCaseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/technology/{technology}")
    public ResponseEntity<List<TestCase>> getTestCasesByTechnology(@PathVariable String technology) {
        return ResponseEntity.ok(testCaseService.getTestCasesByTechnology(technology));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TestCase>> getTestCasesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(testCaseService.getTestCasesByCategory(category));
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<TestCase>> getEnabledTestCases() {
        return ResponseEntity.ok(testCaseService.getEnabledTestCases());
    }

    @GetMapping("/enabled/technology/{technology}")
    public ResponseEntity<List<TestCase>> getEnabledTestCasesByTechnology(@PathVariable String technology) {
        return ResponseEntity.ok(testCaseService.getEnabledTestCasesByTechnology(technology));
    }

    @PostMapping
    public ResponseEntity<TestCase> createTestCase(@RequestBody TestCase testCase) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(testCaseService.createTestCase(testCase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestCase> updateTestCase(@PathVariable Long id, @RequestBody TestCase testCase) {
        try {
            return ResponseEntity.ok(testCaseService.updateTestCase(id, testCase));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<Void> enableTestCase(@PathVariable Long id) {
        try {
            testCaseService.enableTestCase(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<Void> disableTestCase(@PathVariable Long id) {
        try {
            testCaseService.disableTestCase(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

