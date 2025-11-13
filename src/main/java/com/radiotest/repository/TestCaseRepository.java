package com.radiotest.repository;

import com.radiotest.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    Optional<TestCase> findByTestCaseId(String testCaseId);
    List<TestCase> findByTechnology(String technology);
    List<TestCase> findByCategory(String category);
    List<TestCase> findByEnabledTrue();
    List<TestCase> findByTechnologyAndEnabledTrue(String technology);
}

