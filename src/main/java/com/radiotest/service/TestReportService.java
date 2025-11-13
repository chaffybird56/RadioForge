package com.radiotest.service;

import com.radiotest.analytics.SparkAnalyticsService;
import com.radiotest.model.TestExecution;
import com.radiotest.model.TestReport;
import com.radiotest.repository.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestReportService {
    private final TestExecutionRepository testExecutionRepository;
    private final AnalyticsService analyticsService;
    private final SparkAnalyticsService sparkAnalyticsService;

    public TestReport generateReport(String testSuite, LocalDateTime startTime, LocalDateTime endTime) {
        List<TestExecution> executions = testExecutionRepository.findByStartTimeBetween(startTime, endTime);
        
        if (executions.isEmpty()) {
            return createEmptyReport(testSuite);
        }

        int totalTests = executions.size();
        int passedTests = (int) executions.stream().filter(e -> "PASSED".equals(e.getStatus())).count();
        int failedTests = (int) executions.stream().filter(e -> "FAILED".equals(e.getStatus())).count();
        int errorTests = (int) executions.stream().filter(e -> "ERROR".equals(e.getStatus())).count();
        double passRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0.0;
        
        long totalDurationMs = executions.stream()
                .filter(e -> e.getDurationMs() != null)
                .mapToLong(TestExecution::getDurationMs)
                .sum();

        List<TestReport.TestExecutionSummary> summaries = executions.stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        Map<String, Object> statistics = analyticsService.calculateStatistics(executions);
        
        // Add Spark-based analytics for large-scale processing (if available)
        try {
            Map<String, Object> sparkStats = sparkAnalyticsService.processWithSpark(executions);
            if (sparkStats != null && !sparkStats.isEmpty()) {
                statistics.putAll(sparkStats);
            }
        } catch (Exception e) {
            // Spark not available or failed - continue without it
            log.debug("Spark analytics not available: {}", e.getMessage());
        }
        
        List<TestReport.AnomalyDetection> anomalies = analyticsService.detectAnomalies(executions);

        TestReport report = new TestReport();
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedAt(LocalDateTime.now());
        report.setTestSuite(testSuite);
        report.setTotalTests(totalTests);
        report.setPassedTests(passedTests);
        report.setFailedTests(failedTests);
        report.setErrorTests(errorTests);
        report.setPassRate(passRate);
        report.setTotalDurationMs(totalDurationMs);
        report.setExecutions(summaries);
        report.setStatistics(statistics);
        report.setAnomalies(anomalies);

        return report;
    }

    public TestReport generateReportByTestCaseId(String testCaseId) {
        List<TestExecution> executions = testExecutionRepository.findByTestCaseId(testCaseId);
        
        if (executions.isEmpty()) {
            return createEmptyReport("Test Case: " + testCaseId);
        }

        return generateReportFromExecutions("Test Case: " + testCaseId, executions);
    }

    public TestReport generateReportByTechnology(String technology) {
        List<TestExecution> executions = testExecutionRepository.findByTechnology(technology);
        
        if (executions.isEmpty()) {
            return createEmptyReport("Technology: " + technology);
        }

        return generateReportFromExecutions("Technology: " + technology, executions);
    }

    private TestReport generateReportFromExecutions(String testSuite, List<TestExecution> executions) {
        int totalTests = executions.size();
        int passedTests = (int) executions.stream().filter(e -> "PASSED".equals(e.getStatus())).count();
        int failedTests = (int) executions.stream().filter(e -> "FAILED".equals(e.getStatus())).count();
        int errorTests = (int) executions.stream().filter(e -> "ERROR".equals(e.getStatus())).count();
        double passRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0.0;
        
        long totalDurationMs = executions.stream()
                .filter(e -> e.getDurationMs() != null)
                .mapToLong(TestExecution::getDurationMs)
                .sum();

        List<TestReport.TestExecutionSummary> summaries = executions.stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        Map<String, Object> statistics = analyticsService.calculateStatistics(executions);
        List<TestReport.AnomalyDetection> anomalies = analyticsService.detectAnomalies(executions);

        TestReport report = new TestReport();
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedAt(LocalDateTime.now());
        report.setTestSuite(testSuite);
        report.setTotalTests(totalTests);
        report.setPassedTests(passedTests);
        report.setFailedTests(failedTests);
        report.setErrorTests(errorTests);
        report.setPassRate(passRate);
        report.setTotalDurationMs(totalDurationMs);
        report.setExecutions(summaries);
        report.setStatistics(statistics);
        report.setAnomalies(anomalies);

        return report;
    }

    private TestReport.TestExecutionSummary mapToSummary(TestExecution execution) {
        TestReport.TestExecutionSummary summary = new TestReport.TestExecutionSummary();
        summary.setTestCaseId(execution.getTestCaseId());
        summary.setTestCaseName(execution.getTestCaseName());
        summary.setStatus(execution.getStatus());
        summary.setDurationMs(execution.getDurationMs());
        summary.setErrorMessage(execution.getErrorMessage());
        return summary;
    }

    private TestReport createEmptyReport(String testSuite) {
        TestReport report = new TestReport();
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedAt(LocalDateTime.now());
        report.setTestSuite(testSuite);
        report.setTotalTests(0);
        report.setPassedTests(0);
        report.setFailedTests(0);
        report.setErrorTests(0);
        report.setPassRate(0.0);
        report.setTotalDurationMs(0L);
        report.setExecutions(new ArrayList<>());
        report.setStatistics(new HashMap<>());
        report.setAnomalies(new ArrayList<>());
        return report;
    }
}

