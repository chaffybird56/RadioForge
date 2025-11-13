package com.radiotest.service;

import com.radiotest.model.TestCase;
import com.radiotest.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;

    public List<TestCase> getAllTestCases() {
        return testCaseRepository.findAll();
    }

    public Optional<TestCase> getTestCaseById(Long id) {
        return testCaseRepository.findById(id);
    }

    public Optional<TestCase> getTestCaseByTestCaseId(String testCaseId) {
        return testCaseRepository.findByTestCaseId(testCaseId);
    }

    public List<TestCase> getTestCasesByTechnology(String technology) {
        return testCaseRepository.findByTechnology(technology);
    }

    public List<TestCase> getTestCasesByCategory(String category) {
        return testCaseRepository.findByCategory(category);
    }

    public List<TestCase> getEnabledTestCases() {
        return testCaseRepository.findByEnabledTrue();
    }

    public List<TestCase> getEnabledTestCasesByTechnology(String technology) {
        return testCaseRepository.findByTechnologyAndEnabledTrue(technology);
    }

    @Transactional
    public TestCase createTestCase(TestCase testCase) {
        return testCaseRepository.save(testCase);
    }

    @Transactional
    public TestCase updateTestCase(Long id, TestCase testCase) {
        TestCase existing = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestCase not found with id: " + id));
        
        existing.setTestCaseId(testCase.getTestCaseId());
        existing.setName(testCase.getName());
        existing.setDescription(testCase.getDescription());
        existing.setTechnology(testCase.getTechnology());
        existing.setCategory(testCase.getCategory());
        existing.setParameters(testCase.getParameters());
        existing.setExpectedPowerMin(testCase.getExpectedPowerMin());
        existing.setExpectedPowerMax(testCase.getExpectedPowerMax());
        existing.setExpectedFrequencyHz(testCase.getExpectedFrequencyHz());
        existing.setExpectedEvmMax(testCase.getExpectedEvmMax());
        existing.setExpectedAcprMax(testCase.getExpectedAcprMax());
        existing.setEnabled(testCase.getEnabled());
        
        return testCaseRepository.save(existing);
    }

    @Transactional
    public void deleteTestCase(Long id) {
        testCaseRepository.deleteById(id);
    }

    @Transactional
    public void enableTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestCase not found with id: " + id));
        testCase.setEnabled(true);
        testCaseRepository.save(testCase);
    }

    @Transactional
    public void disableTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestCase not found with id: " + id));
        testCase.setEnabled(false);
        testCaseRepository.save(testCase);
    }
}

