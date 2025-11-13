package com.radiotest.repository;

import com.radiotest.model.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {
    List<TestExecution> findByTestCaseId(String testCaseId);
    List<TestExecution> findByStatus(String status);
    List<TestExecution> findByTechnology(String technology);
    List<TestExecution> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT e FROM TestExecution e WHERE e.testCaseId = :testCaseId ORDER BY e.startTime DESC")
    List<TestExecution> findRecentExecutionsByTestCaseId(String testCaseId);
    
    @Query("SELECT COUNT(e) FROM TestExecution e WHERE e.status = :status")
    Long countByStatus(String status);
}

