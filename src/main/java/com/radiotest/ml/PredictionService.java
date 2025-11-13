package com.radiotest.ml;

import com.radiotest.model.TestExecution;
import com.radiotest.repository.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionService {
    private final TestExecutionRepository testExecutionRepository;

    /**
     * Predict the likelihood of test failure based on historical data
     */
    public PredictionResult predictTestOutcome(String testCaseId) {
        List<TestExecution> historicalExecutions = testExecutionRepository
                .findRecentExecutionsByTestCaseId(testCaseId);
        
        if (historicalExecutions.isEmpty()) {
            return new PredictionResult(0.5, "INSUFFICIENT_DATA", 
                    "No historical data available for prediction");
        }

        // Simple prediction based on recent pass rate
        long totalExecutions = historicalExecutions.size();
        long passedExecutions = historicalExecutions.stream()
                .filter(e -> "PASSED".equals(e.getStatus()))
                .count();
        
        double passRate = (double) passedExecutions / totalExecutions;
        double failureProbability = 1.0 - passRate;

        String confidence;
        if (totalExecutions >= 20) {
            confidence = "HIGH";
        } else if (totalExecutions >= 10) {
            confidence = "MEDIUM";
        } else {
            confidence = "LOW";
        }

        String recommendation;
        if (failureProbability > 0.7) {
            recommendation = "High failure probability. Review test parameters and instrument calibration.";
        } else if (failureProbability > 0.4) {
            recommendation = "Moderate failure probability. Monitor test execution closely.";
        } else {
            recommendation = "Low failure probability. Test should pass under normal conditions.";
        }

        return new PredictionResult(failureProbability, confidence, recommendation);
    }

    /**
     * Predict expected power level based on historical measurements
     */
    public Double predictPowerLevel(String testCaseId) {
        List<TestExecution> executions = testExecutionRepository.findByTestCaseId(testCaseId);
        
        List<Double> powerLevels = executions.stream()
                .filter(e -> e.getPowerLevel() != null && "PASSED".equals(e.getStatus()))
                .map(TestExecution::getPowerLevel)
                .collect(Collectors.toList());
        
        if (powerLevels.isEmpty()) {
            return null;
        }

        // Use mean of successful executions as prediction
        return powerLevels.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * Predict expected EVM based on historical measurements
     */
    public Double predictEVM(String testCaseId) {
        List<TestExecution> executions = testExecutionRepository.findByTestCaseId(testCaseId);
        
        List<Double> evmValues = executions.stream()
                .filter(e -> e.getEvm() != null && "PASSED".equals(e.getStatus()))
                .map(TestExecution::getEvm)
                .collect(Collectors.toList());
        
        if (evmValues.isEmpty()) {
            return null;
        }

        return evmValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * Predict expected test duration based on historical data
     */
    public Long predictDuration(String testCaseId) {
        List<TestExecution> executions = testExecutionRepository.findByTestCaseId(testCaseId);
        
        List<Long> durations = executions.stream()
                .filter(e -> e.getDurationMs() != null)
                .map(TestExecution::getDurationMs)
                .collect(Collectors.toList());
        
        if (durations.isEmpty()) {
            return null;
        }

        // Use median for duration prediction (more robust to outliers)
        durations.sort(Long::compareTo);
        int middle = durations.size() / 2;
        if (durations.size() % 2 == 0) {
            return (durations.get(middle - 1) + durations.get(middle)) / 2;
        } else {
            return durations.get(middle);
        }
    }

    public static class PredictionResult {
        private Double failureProbability;
        private String confidence;
        private String recommendation;

        public PredictionResult(Double failureProbability, String confidence, String recommendation) {
            this.failureProbability = failureProbability;
            this.confidence = confidence;
            this.recommendation = recommendation;
        }

        // Getters and setters
        public Double getFailureProbability() { return failureProbability; }
        public void setFailureProbability(Double failureProbability) { this.failureProbability = failureProbability; }
        public String getConfidence() { return confidence; }
        public void setConfidence(String confidence) { this.confidence = confidence; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
}

