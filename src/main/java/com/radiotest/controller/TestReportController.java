package com.radiotest.controller;

import com.radiotest.model.TestReport;
import com.radiotest.service.TestReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test-reports")
@RequiredArgsConstructor
public class TestReportController {
    private final TestReportService testReportService;

    @PostMapping("/generate")
    public ResponseEntity<TestReport> generateReport(
            @RequestParam String testSuite,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(testReportService.generateReport(testSuite, startTime, endTime));
    }

    @GetMapping("/test-case/{testCaseId}")
    public ResponseEntity<TestReport> generateReportByTestCaseId(@PathVariable String testCaseId) {
        return ResponseEntity.ok(testReportService.generateReportByTestCaseId(testCaseId));
    }

    @GetMapping("/technology/{technology}")
    public ResponseEntity<TestReport> generateReportByTechnology(@PathVariable String technology) {
        return ResponseEntity.ok(testReportService.generateReportByTechnology(technology));
    }
}

