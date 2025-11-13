package com.radiotest.service;

import com.radiotest.model.TestExecution;
import com.radiotest.model.TestReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    public Map<String, Object> calculateStatistics(List<TestExecution> executions) {
        Map<String, Object> stats = new HashMap<>();
        
        if (executions.isEmpty()) {
            return stats;
        }

        // Power Level Statistics
        List<Double> powerLevels = executions.stream()
                .filter(e -> e.getPowerLevel() != null)
                .map(TestExecution::getPowerLevel)
                .collect(Collectors.toList());
        
        if (!powerLevels.isEmpty()) {
            stats.put("powerLevelMean", calculateMean(powerLevels));
            stats.put("powerLevelStdDev", calculateStdDev(powerLevels));
            stats.put("powerLevelMin", Collections.min(powerLevels));
            stats.put("powerLevelMax", Collections.max(powerLevels));
        }

        // Frequency Statistics
        List<Double> frequencies = executions.stream()
                .filter(e -> e.getFrequencyHz() != null)
                .map(TestExecution::getFrequencyHz)
                .collect(Collectors.toList());
        
        if (!frequencies.isEmpty()) {
            stats.put("frequencyMean", calculateMean(frequencies));
            stats.put("frequencyStdDev", calculateStdDev(frequencies));
        }

        // EVM Statistics
        List<Double> evmValues = executions.stream()
                .filter(e -> e.getEvm() != null)
                .map(TestExecution::getEvm)
                .collect(Collectors.toList());
        
        if (!evmValues.isEmpty()) {
            stats.put("evmMean", calculateMean(evmValues));
            stats.put("evmStdDev", calculateStdDev(evmValues));
            stats.put("evmMax", Collections.max(evmValues));
        }

        // ACPR Statistics
        List<Double> acprValues = executions.stream()
                .filter(e -> e.getAcpr() != null)
                .map(TestExecution::getAcpr)
                .collect(Collectors.toList());
        
        if (!acprValues.isEmpty()) {
            stats.put("acprMean", calculateMean(acprValues));
            stats.put("acprStdDev", calculateStdDev(acprValues));
            stats.put("acprMax", Collections.max(acprValues));
        }

        // Duration Statistics
        List<Long> durations = executions.stream()
                .filter(e -> e.getDurationMs() != null)
                .map(TestExecution::getDurationMs)
                .collect(Collectors.toList());
        
        if (!durations.isEmpty()) {
            List<Double> durationsDouble = durations.stream().map(Long::doubleValue).collect(Collectors.toList());
            stats.put("durationMean", calculateMean(durationsDouble));
            stats.put("durationStdDev", calculateStdDev(durationsDouble));
            stats.put("durationMin", Collections.min(durations));
            stats.put("durationMax", Collections.max(durations));
        }

        // Status Distribution
        Map<String, Long> statusCount = executions.stream()
                .collect(Collectors.groupingBy(
                        TestExecution::getStatus,
                        Collectors.counting()
                ));
        stats.put("statusDistribution", statusCount);

        // Technology Distribution
        Map<String, Long> technologyCount = executions.stream()
                .filter(e -> e.getTechnology() != null)
                .collect(Collectors.groupingBy(
                        TestExecution::getTechnology,
                        Collectors.counting()
                ));
        stats.put("technologyDistribution", technologyCount);

        return stats;
    }

    public List<TestReport.AnomalyDetection> detectAnomalies(List<TestExecution> executions) {
        List<TestReport.AnomalyDetection> anomalies = new ArrayList<>();
        
        if (executions.isEmpty()) {
            return anomalies;
        }

        // Calculate statistics for anomaly detection
        Map<String, Object> stats = calculateStatistics(executions);
        
        for (TestExecution execution : executions) {
            // Power Level Anomaly Detection
            if (execution.getPowerLevel() != null && stats.containsKey("powerLevelMean")) {
                double mean = (Double) stats.get("powerLevelMean");
                double stdDev = (Double) stats.get("powerLevelStdDev");
                double deviation = Math.abs(execution.getPowerLevel() - mean);
                
                if (deviation > 3 * stdDev) {
                    anomalies.add(createAnomaly(execution, "powerLevel", execution.getPowerLevel(), mean, deviation, "HIGH"));
                } else if (deviation > 2 * stdDev) {
                    anomalies.add(createAnomaly(execution, "powerLevel", execution.getPowerLevel(), mean, deviation, "MEDIUM"));
                }
            }

            // EVM Anomaly Detection
            if (execution.getEvm() != null && stats.containsKey("evmMean")) {
                double mean = (Double) stats.get("evmMean");
                double stdDev = (Double) stats.get("evmStdDev");
                double deviation = Math.abs(execution.getEvm() - mean);
                
                if (deviation > 3 * stdDev) {
                    anomalies.add(createAnomaly(execution, "evm", execution.getEvm(), mean, deviation, "HIGH"));
                } else if (deviation > 2 * stdDev) {
                    anomalies.add(createAnomaly(execution, "evm", execution.getEvm(), mean, deviation, "MEDIUM"));
                }
            }

            // ACPR Anomaly Detection
            if (execution.getAcpr() != null && stats.containsKey("acprMean")) {
                double mean = (Double) stats.get("acprMean");
                double stdDev = (Double) stats.get("acprStdDev");
                double deviation = Math.abs(execution.getAcpr() - mean);
                
                if (deviation > 3 * stdDev) {
                    anomalies.add(createAnomaly(execution, "acpr", execution.getAcpr(), mean, deviation, "HIGH"));
                } else if (deviation > 2 * stdDev) {
                    anomalies.add(createAnomaly(execution, "acpr", execution.getAcpr(), mean, deviation, "MEDIUM"));
                }
            }
        }

        return anomalies;
    }

    private TestReport.AnomalyDetection createAnomaly(TestExecution execution, String metric, 
                                                       Double value, Double expected, Double deviation, String severity) {
        TestReport.AnomalyDetection anomaly = new TestReport.AnomalyDetection();
        anomaly.setTestCaseId(execution.getTestCaseId());
        anomaly.setMetric(metric);
        anomaly.setValue(value);
        anomaly.setExpected(expected);
        anomaly.setDeviation(deviation);
        anomaly.setSeverity(severity);
        return anomaly;
    }

    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double calculateStdDev(List<Double> values) {
        if (values.size() < 2) {
            return 0.0;
        }
        double mean = calculateMean(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
}

