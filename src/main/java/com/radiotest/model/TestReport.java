package com.radiotest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestReport {
    private Long id;
    private String reportId;
    private LocalDateTime generatedAt;
    private String testSuite;
    private Integer totalTests;
    private Integer passedTests;
    private Integer failedTests;
    private Integer errorTests;
    private Double passRate;
    private Long totalDurationMs;
    private List<TestExecutionSummary> executions;
    private Map<String, Object> statistics;
    private List<AnomalyDetection> anomalies;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestExecutionSummary {
        private String testCaseId;
        private String testCaseName;
        private String status;
        private Long durationMs;
        private String errorMessage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnomalyDetection {
        private String testCaseId;
        private String metric;
        private Double value;
        private Double expected;
        private Double deviation;
        private String severity; // LOW, MEDIUM, HIGH
    }
}

