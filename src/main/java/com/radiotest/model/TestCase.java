package com.radiotest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(name = "test_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String testCaseId;
    private String name;
    private String description;
    private String technology; // 5G, LTE, W-CDMA, GSM
    private String category; // Power, Frequency, Modulation, etc.
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "test_parameters", joinColumns = @JoinColumn(name = "test_case_id"))
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    private Map<String, String> parameters;
    
    private Double expectedPowerMin;
    private Double expectedPowerMax;
    private Double expectedFrequencyHz;
    private Double expectedEvmMax;
    private Double expectedAcprMax;
    
    private Boolean enabled = true;
}

