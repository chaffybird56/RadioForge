package com.radiotest.framework;

import com.radiotest.model.TestCase;
import com.radiotest.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestRunner {
    private final TestCaseService testCaseService;
    private final TestExecutor testExecutor;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void runTest(String testCaseId) {
        TestCase testCase = testCaseService.getTestCaseByTestCaseId(testCaseId)
                .orElseThrow(() -> new RuntimeException("Test case not found: " + testCaseId));
        
        if (!testCase.getEnabled()) {
            throw new RuntimeException("Test case is disabled: " + testCaseId);
        }
        
        log.info("Running test case: {}", testCaseId);
        testExecutor.executeTest(testCase);
    }

    public void runTest(Long testCaseId) {
        TestCase testCase = testCaseService.getTestCaseById(testCaseId)
                .orElseThrow(() -> new RuntimeException("Test case not found with id: " + testCaseId));
        
        if (!testCase.getEnabled()) {
            throw new RuntimeException("Test case is disabled: " + testCase.getTestCaseId());
        }
        
        log.info("Running test case: {}", testCase.getTestCaseId());
        testExecutor.executeTest(testCase);
    }

    public void runTestsByTechnology(String technology) {
        List<TestCase> testCases = testCaseService.getEnabledTestCasesByTechnology(technology);
        log.info("Running {} test cases for technology: {}", testCases.size(), technology);
        
        for (TestCase testCase : testCases) {
            testExecutor.executeTest(testCase);
        }
        
        // Publish test suite start event
        kafkaTemplate.send("test-suites", Map.of(
                "technology", technology,
                "testCaseCount", testCases.size(),
                "status", "STARTED"
        ));
    }

    public void runTestsByCategory(String category) {
        List<TestCase> testCases = testCaseService.getTestCasesByCategory(category);
        log.info("Running {} test cases for category: {}", testCases.size(), category);
        
        for (TestCase testCase : testCases) {
            if (testCase.getEnabled()) {
                testExecutor.executeTest(testCase);
            }
        }
    }

    public void runAllEnabledTests() {
        List<TestCase> testCases = testCaseService.getEnabledTestCases();
        log.info("Running {} enabled test cases", testCases.size());
        
        for (TestCase testCase : testCases) {
            testExecutor.executeTest(testCase);
        }
    }
}

