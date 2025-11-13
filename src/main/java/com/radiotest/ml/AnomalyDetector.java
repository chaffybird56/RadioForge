package com.radiotest.ml;

import com.radiotest.model.TestExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AnomalyDetector {
    
    /**
     * Detect anomalies using statistical methods (Z-score, IQR)
     */
    public List<Anomaly> detectAnomalies(List<TestExecution> executions) {
        List<Anomaly> anomalies = new ArrayList<>();
        
        if (executions.size() < 3) {
            return anomalies; // Need at least 3 data points for meaningful anomaly detection
        }

        // Detect anomalies in power levels
        anomalies.addAll(detectPowerAnomalies(executions));
        
        // Detect anomalies in EVM
        anomalies.addAll(detectEVMAnomalies(executions));
        
        // Detect anomalies in ACPR
        anomalies.addAll(detectACPRAnomalies(executions));
        
        // Detect anomalies in duration
        anomalies.addAll(detectDurationAnomalies(executions));

        return anomalies;
    }

    private List<Anomaly> detectPowerAnomalies(List<TestExecution> executions) {
        List<Anomaly> anomalies = new ArrayList<>();
        List<Double> powerLevels = executions.stream()
                .filter(e -> e.getPowerLevel() != null)
                .map(TestExecution::getPowerLevel)
                .collect(Collectors.toList());
        
        if (powerLevels.size() < 3) {
            return anomalies;
        }

        double mean = calculateMean(powerLevels);
        double stdDev = calculateStdDev(powerLevels);
        
        for (TestExecution execution : executions) {
            if (execution.getPowerLevel() != null) {
                double zScore = Math.abs((execution.getPowerLevel() - mean) / stdDev);
                if (zScore > 3.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "powerLevel",
                            execution.getPowerLevel(),
                            mean,
                            zScore,
                            "HIGH"
                    ));
                } else if (zScore > 2.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "powerLevel",
                            execution.getPowerLevel(),
                            mean,
                            zScore,
                            "MEDIUM"
                    ));
                }
            }
        }
        
        return anomalies;
    }

    private List<Anomaly> detectEVMAnomalies(List<TestExecution> executions) {
        List<Anomaly> anomalies = new ArrayList<>();
        List<Double> evmValues = executions.stream()
                .filter(e -> e.getEvm() != null)
                .map(TestExecution::getEvm)
                .collect(Collectors.toList());
        
        if (evmValues.size() < 3) {
            return anomalies;
        }

        double mean = calculateMean(evmValues);
        double stdDev = calculateStdDev(evmValues);
        
        for (TestExecution execution : executions) {
            if (execution.getEvm() != null) {
                double zScore = Math.abs((execution.getEvm() - mean) / stdDev);
                if (zScore > 3.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "evm",
                            execution.getEvm(),
                            mean,
                            zScore,
                            "HIGH"
                    ));
                } else if (zScore > 2.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "evm",
                            execution.getEvm(),
                            mean,
                            zScore,
                            "MEDIUM"
                    ));
                }
            }
        }
        
        return anomalies;
    }

    private List<Anomaly> detectACPRAnomalies(List<TestExecution> executions) {
        List<Anomaly> anomalies = new ArrayList<>();
        List<Double> acprValues = executions.stream()
                .filter(e -> e.getAcpr() != null)
                .map(TestExecution::getAcpr)
                .collect(Collectors.toList());
        
        if (acprValues.size() < 3) {
            return anomalies;
        }

        double mean = calculateMean(acprValues);
        double stdDev = calculateStdDev(acprValues);
        
        for (TestExecution execution : executions) {
            if (execution.getAcpr() != null) {
                double zScore = Math.abs((execution.getAcpr() - mean) / stdDev);
                if (zScore > 3.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "acpr",
                            execution.getAcpr(),
                            mean,
                            zScore,
                            "HIGH"
                    ));
                } else if (zScore > 2.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "acpr",
                            execution.getAcpr(),
                            mean,
                            zScore,
                            "MEDIUM"
                    ));
                }
            }
        }
        
        return anomalies;
    }

    private List<Anomaly> detectDurationAnomalies(List<TestExecution> executions) {
        List<Anomaly> anomalies = new ArrayList<>();
        List<Long> durations = executions.stream()
                .filter(e -> e.getDurationMs() != null)
                .map(TestExecution::getDurationMs)
                .collect(Collectors.toList());
        
        if (durations.size() < 3) {
            return anomalies;
        }

        List<Double> durationsDouble = durations.stream().map(Long::doubleValue).collect(Collectors.toList());
        double mean = calculateMean(durationsDouble);
        double stdDev = calculateStdDev(durationsDouble);
        
        for (TestExecution execution : executions) {
            if (execution.getDurationMs() != null) {
                double zScore = Math.abs((execution.getDurationMs() - mean) / stdDev);
                if (zScore > 3.0) {
                    anomalies.add(new Anomaly(
                            execution.getTestCaseId(),
                            "duration",
                            execution.getDurationMs().doubleValue(),
                            mean,
                            zScore,
                            "HIGH"
                    ));
                }
            }
        }
        
        return anomalies;
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

    public static class Anomaly {
        private String testCaseId;
        private String metric;
        private Double value;
        private Double expected;
        private Double zScore;
        private String severity;

        public Anomaly(String testCaseId, String metric, Double value, Double expected, Double zScore, String severity) {
            this.testCaseId = testCaseId;
            this.metric = metric;
            this.value = value;
            this.expected = expected;
            this.zScore = zScore;
            this.severity = severity;
        }

        // Getters and setters
        public String getTestCaseId() { return testCaseId; }
        public void setTestCaseId(String testCaseId) { this.testCaseId = testCaseId; }
        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        public Double getExpected() { return expected; }
        public void setExpected(Double expected) { this.expected = expected; }
        public Double getZScore() { return zScore; }
        public void setZScore(Double zScore) { this.zScore = zScore; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
}

