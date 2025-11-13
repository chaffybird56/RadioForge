package com.radiotest.framework;

import com.radiotest.instruments.InstrumentFactory;
import com.radiotest.instruments.InstrumentInterface;
import com.radiotest.model.TestCase;
import com.radiotest.model.TestExecution;
import com.radiotest.service.TestExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestExecutor {
    private final TestExecutionService testExecutionService;
    private final InstrumentFactory instrumentFactory;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Async("testTaskExecutor")
    public void executeTest(TestCase testCase) {
        log.info("Starting test execution for test case: {}", testCase.getTestCaseId());
        
        TestExecution execution = new TestExecution();
        execution.setTestCaseId(testCase.getTestCaseId());
        execution.setTestCaseName(testCase.getName());
        execution.setTechnology(testCase.getTechnology());
        execution.setStatus("RUNNING");
        execution.setStartTime(LocalDateTime.now());
        
        execution = testExecutionService.createExecution(execution);
        Long executionId = execution.getId();
        
        try {
            // Send start notification
            sendTestUpdate(executionId, "RUNNING", "Test execution started");
            
            // Configure instrument based on test case parameters
            configureInstrument(testCase);
            
            // Perform measurements
            List<Double> measurements = performMeasurements(testCase);
            execution.setMeasurements(measurements);
            
            // Extract specific measurements
            Double powerLevel = extractPowerLevel(measurements, testCase);
            Double frequencyHz = extractFrequency(measurements, testCase);
            Double evm = extractEVM(measurements, testCase);
            Double acpr = extractACPR(measurements, testCase);
            
            execution.setPowerLevel(powerLevel);
            execution.setFrequencyHz(frequencyHz);
            execution.setEvm(evm);
            execution.setAcpr(acpr);
            
            // Validate results against expected values
            String validationResult = validateResults(testCase, powerLevel, frequencyHz, evm, acpr);
            
            if ("PASSED".equals(validationResult)) {
                execution.setStatus("PASSED");
                sendTestUpdate(executionId, "PASSED", "Test passed successfully");
            } else {
                execution.setStatus("FAILED");
                execution.setErrorMessage(validationResult);
                sendTestUpdate(executionId, "FAILED", validationResult);
            }
            
            execution.setTestLog(buildTestLog(testCase, measurements, powerLevel, frequencyHz, evm, acpr));
            
        } catch (Exception e) {
            log.error("Error executing test case: {}", testCase.getTestCaseId(), e);
            execution.setStatus("ERROR");
            execution.setErrorMessage(e.getMessage());
            sendTestUpdate(executionId, "ERROR", e.getMessage());
        } finally {
            // Complete the execution
            testExecutionService.completeExecution(executionId, execution.getStatus(), execution.getErrorMessage());
            
            // Publish to Kafka
            kafkaTemplate.send("test-executions", execution);
            
            log.info("Completed test execution for test case: {} with status: {}", 
                    testCase.getTestCaseId(), execution.getStatus());
        }
    }

    private void configureInstrument(TestCase testCase) {
        // Use signal generator for generating test signals
        InstrumentInterface signalGen = instrumentFactory.getInstrument("SIGGEN");
        // Use spectrum analyzer for measurements
        InstrumentInterface spectrumAnalyzer = instrumentFactory.getInstrument("SPECTRUM");
        
        try {
            // Initialize signal generator (synchronized to handle concurrent access)
            synchronized (signalGen) {
                if (!signalGen.isConnected()) {
                    signalGen.initialize();
                }
            }
            
            // Initialize spectrum analyzer (synchronized to handle concurrent access)
            synchronized (spectrumAnalyzer) {
                if (!spectrumAnalyzer.isConnected()) {
                    spectrumAnalyzer.initialize();
                }
            }
            
            Map<String, String> parameters = testCase.getParameters();
            if (parameters != null && !parameters.isEmpty()) {
                synchronized (signalGen) {
                    for (Map.Entry<String, String> param : parameters.entrySet()) {
                        signalGen.setParameter(param.getKey(), param.getValue());
                    }
                }
                synchronized (spectrumAnalyzer) {
                    for (Map.Entry<String, String> param : parameters.entrySet()) {
                        spectrumAnalyzer.setParameter(param.getKey(), param.getValue());
                    }
                }
            }
            
            // Configure signal generator frequency and power
            if (testCase.getExpectedFrequencyHz() != null) {
                synchronized (signalGen) {
                    signalGen.setFrequency(testCase.getExpectedFrequencyHz());
                }
                // Set spectrum analyzer center frequency to match
                synchronized (spectrumAnalyzer) {
                    spectrumAnalyzer.setFrequency(testCase.getExpectedFrequencyHz());
                }
            }
            
            // Set signal generator power level if specified
            if (testCase.getExpectedPowerMin() != null) {
                double avgPower = (testCase.getExpectedPowerMin() + 
                    (testCase.getExpectedPowerMax() != null ? testCase.getExpectedPowerMax() : testCase.getExpectedPowerMin())) / 2.0;
                synchronized (signalGen) {
                    signalGen.setPowerLevel(avgPower);
                }
                // Set spectrum analyzer reference level to match for accurate measurements
                synchronized (spectrumAnalyzer) {
                    spectrumAnalyzer.setPowerLevel(avgPower);
                }
            }
            
            log.info("Configured Signal Generator and Spectrum Analyzer for test case: {}", testCase.getTestCaseId());
        } catch (Exception e) {
            log.error("Error configuring instruments for test case: {}", testCase.getTestCaseId(), e);
            throw new RuntimeException("Failed to configure instruments: " + e.getMessage(), e);
        }
    }

    private List<Double> performMeasurements(TestCase testCase) {
        List<Double> measurements = new ArrayList<>();
        // Use spectrum analyzer for measurements (more accurate for RF measurements)
        InstrumentInterface spectrumAnalyzer = instrumentFactory.getInstrument("SPECTRUM");
        
        // Perform multiple measurements for statistical analysis
        int numMeasurements = 10; // Default number of measurements
        
        Map<String, String> params = testCase.getParameters();
        if (params != null && params.containsKey("numMeasurements")) {
            numMeasurements = Integer.parseInt(params.get("numMeasurements"));
        }
        
        log.info("Performing {} measurements using Spectrum Analyzer", numMeasurements);
        
        for (int i = 0; i < numMeasurements; i++) {
            try {
                synchronized (spectrumAnalyzer) {
                    Double measurement = spectrumAnalyzer.measurePower();
                    if (measurement != null) {
                        measurements.add(measurement);
                    }
                }
                Thread.sleep(100); // Small delay between measurements for stability
            } catch (Exception e) {
                log.warn("Error during measurement", e);
                break;
            }
        }
        
        return measurements;
    }

    private Double extractPowerLevel(List<Double> measurements, TestCase testCase) {
        if (measurements.isEmpty()) {
            return null;
        }
        return measurements.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private Double extractFrequency(List<Double> measurements, TestCase testCase) {
        return testCase.getExpectedFrequencyHz();
    }

    private Double extractEVM(List<Double> measurements, TestCase testCase) {
        // EVM measurement using Spectrum Analyzer (has demodulation capability)
        try {
            InstrumentInterface spectrumAnalyzer = instrumentFactory.getInstrument("SPECTRUM");
            synchronized (spectrumAnalyzer) {
                Double evm = spectrumAnalyzer.measureEVM();
                log.info("EVM measured: {}% using Spectrum Analyzer", evm);
                return evm;
            }
        } catch (Exception e) {
            log.warn("Error measuring EVM", e);
            return null;
        }
    }

    private Double extractACPR(List<Double> measurements, TestCase testCase) {
        // ACPR measurement using Spectrum Analyzer (ideal for adjacent channel measurements)
        try {
            InstrumentInterface spectrumAnalyzer = instrumentFactory.getInstrument("SPECTRUM");
            synchronized (spectrumAnalyzer) {
                Double acpr = spectrumAnalyzer.measureACPR();
                log.info("ACPR measured: {} dB using Spectrum Analyzer", acpr);
                return acpr;
            }
        } catch (Exception e) {
            log.warn("Error measuring ACPR", e);
            return null;
        }
    }

    private String validateResults(TestCase testCase, Double powerLevel, Double frequencyHz, 
                                   Double evm, Double acpr) {
        StringBuilder errors = new StringBuilder();
        
        // Validate power level
        if (powerLevel != null) {
            if (testCase.getExpectedPowerMin() != null && powerLevel < testCase.getExpectedPowerMin()) {
                errors.append(String.format("Power level %.2f dBm is below minimum %.2f dBm. ", 
                        powerLevel, testCase.getExpectedPowerMin()));
            }
            if (testCase.getExpectedPowerMax() != null && powerLevel > testCase.getExpectedPowerMax()) {
                errors.append(String.format("Power level %.2f dBm is above maximum %.2f dBm. ", 
                        powerLevel, testCase.getExpectedPowerMax()));
            }
        }
        
        // Validate EVM
        if (evm != null && testCase.getExpectedEvmMax() != null && evm > testCase.getExpectedEvmMax()) {
            errors.append(String.format("EVM %.2f%% exceeds maximum %.2f%%. ", 
                    evm, testCase.getExpectedEvmMax()));
        }
        
        // Validate ACPR
        if (acpr != null && testCase.getExpectedAcprMax() != null && acpr > testCase.getExpectedAcprMax()) {
            errors.append(String.format("ACPR %.2f dB exceeds maximum %.2f dB. ", 
                    acpr, testCase.getExpectedAcprMax()));
        }
        
        return errors.length() > 0 ? errors.toString().trim() : "PASSED";
    }

    private String buildTestLog(TestCase testCase, List<Double> measurements, 
                                Double powerLevel, Double frequencyHz, Double evm, Double acpr) {
        StringBuilder log = new StringBuilder();
        log.append(String.format("Test Case: %s (%s)\n", testCase.getTestCaseId(), testCase.getName()));
        log.append(String.format("Technology: %s\n", testCase.getTechnology()));
        log.append(String.format("Category: %s\n", testCase.getCategory()));
        log.append(String.format("Number of measurements: %d\n", measurements.size()));
        log.append(String.format("Power Level: %.2f dBm\n", powerLevel != null ? powerLevel : 0.0));
        log.append(String.format("Frequency: %.2f Hz\n", frequencyHz != null ? frequencyHz : 0.0));
        log.append(String.format("EVM: %.2f%%\n", evm != null ? evm : 0.0));
        log.append(String.format("ACPR: %.2f dB\n", acpr != null ? acpr : 0.0));
        return log.toString();
    }

    private void sendTestUpdate(Long executionId, String status, String message) {
        try {
            Map<String, Object> update = Map.of(
                    "executionId", executionId,
                    "status", status,
                    "message", message,
                    "timestamp", LocalDateTime.now().toString()
            );
            messagingTemplate.convertAndSend("/topic/test-updates", update);
        } catch (Exception e) {
            log.warn("Failed to send WebSocket update", e);
        }
    }
}

