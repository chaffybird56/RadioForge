package com.radiotest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "test_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String testCaseId;
    private String testCaseName;
    private String technology; // 5G, LTE, W-CDMA, GSM
    private String status; // RUNNING, PASSED, FAILED, ERROR
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    
    @ElementCollection
    @CollectionTable(name = "test_measurements", joinColumns = @JoinColumn(name = "execution_id"))
    @Column(name = "measurement")
    private List<Double> measurements;
    
    private Double powerLevel;
    private Double frequencyHz;
    private Double evm; // Error Vector Magnitude
    private Double acpr; // Adjacent Channel Power Ratio
    private String errorMessage;
    
    @Lob
    private String testLog;
}

