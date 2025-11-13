package com.radiotest.controller;

import com.radiotest.ml.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {
    private final PredictionService predictionService;

    @GetMapping("/test-outcome/{testCaseId}")
    public ResponseEntity<PredictionService.PredictionResult> predictTestOutcome(
            @PathVariable String testCaseId) {
        return ResponseEntity.ok(predictionService.predictTestOutcome(testCaseId));
    }

    @GetMapping("/power-level/{testCaseId}")
    public ResponseEntity<Map<String, Double>> predictPowerLevel(@PathVariable String testCaseId) {
        Double predictedPower = predictionService.predictPowerLevel(testCaseId);
        if (predictedPower != null) {
            return ResponseEntity.ok(Map.of("predictedPowerLevel", predictedPower));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/evm/{testCaseId}")
    public ResponseEntity<Map<String, Double>> predictEVM(@PathVariable String testCaseId) {
        Double predictedEVM = predictionService.predictEVM(testCaseId);
        if (predictedEVM != null) {
            return ResponseEntity.ok(Map.of("predictedEVM", predictedEVM));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/duration/{testCaseId}")
    public ResponseEntity<Map<String, Long>> predictDuration(@PathVariable String testCaseId) {
        Long predictedDuration = predictionService.predictDuration(testCaseId);
        if (predictedDuration != null) {
            return ResponseEntity.ok(Map.of("predictedDurationMs", predictedDuration));
        }
        return ResponseEntity.notFound().build();
    }
}

