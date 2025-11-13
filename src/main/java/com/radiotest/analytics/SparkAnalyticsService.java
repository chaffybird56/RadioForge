package com.radiotest.analytics;

import com.radiotest.model.TestExecution;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Apache Spark-based analytics service for large-scale data processing
 * Demonstrates integration with Apache Spark for telecom data analytics
 */
@Service
@Slf4j
public class SparkAnalyticsService {
    
    private SparkSession sparkSession;
    private boolean sparkAvailable = false;
    
    public SparkAnalyticsService() {
        // Initialize Spark session lazily - don't initialize in constructor
        // Spark has compatibility issues with Java 17, so we'll make it optional
        log.info("SparkAnalyticsService initialized - Spark will be initialized on first use if available");
    }
    
    private void initializeSparkIfNeeded() {
        if (sparkSession != null || sparkAvailable) {
            return;
        }
        
        // Try to initialize Spark session for analytics
        // In production, this would connect to a Spark cluster
        try {
            this.sparkSession = SparkSession.builder()
                    .appName("RadioTestAnalytics")
                    .master("local[*]")
                    .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
                    .getOrCreate();
            sparkAvailable = true;
            log.info("Spark session initialized for analytics");
        } catch (Exception e) {
            log.warn("Spark initialization failed, will use standard analytics: {}", e.getMessage());
            this.sparkSession = null;
            sparkAvailable = false;
        }
    }
    
    /**
     * Process test execution data using Spark for large-scale analytics
     */
    public Map<String, Object> processWithSpark(List<TestExecution> executions) {
        if (executions.isEmpty()) {
            return new HashMap<>();
        }
        
        // Initialize Spark lazily
        initializeSparkIfNeeded();
        
        if (sparkSession == null) {
            // Spark not available, return empty map
            return new HashMap<>();
        }
        
        try {
            // Convert to Spark DataFrame
            Dataset<Row> df = sparkSession.createDataFrame(executions, TestExecution.class);
            
            // Perform aggregations using Spark SQL
            Map<String, Object> results = new HashMap<>();
            
            // Calculate statistics using Spark
            df.createOrReplaceTempView("test_executions");
            
            // Power level statistics
            Row powerStats = sparkSession.sql(
                "SELECT AVG(powerLevel) as avgPower, STDDEV(powerLevel) as stdPower, " +
                "MIN(powerLevel) as minPower, MAX(powerLevel) as maxPower " +
                "FROM test_executions WHERE powerLevel IS NOT NULL"
            ).first();
            
            if (powerStats != null && !powerStats.isNullAt(0)) {
                results.put("spark_powerLevelMean", powerStats.getDouble(0));
                results.put("spark_powerLevelStdDev", powerStats.getDouble(1));
                results.put("spark_powerLevelMin", powerStats.getDouble(2));
                results.put("spark_powerLevelMax", powerStats.getDouble(3));
            }
            
            // EVM statistics
            Row evmStats = sparkSession.sql(
                "SELECT AVG(evm) as avgEVM, STDDEV(evm) as stdEVM, MAX(evm) as maxEVM " +
                "FROM test_executions WHERE evm IS NOT NULL"
            ).first();
            
            if (evmStats != null && !evmStats.isNullAt(0)) {
                results.put("spark_evmMean", evmStats.getDouble(0));
                results.put("spark_evmStdDev", evmStats.getDouble(1));
                results.put("spark_evmMax", evmStats.getDouble(2));
            }
            
            // Technology distribution using Spark
            Dataset<Row> techDist = sparkSession.sql(
                "SELECT technology, COUNT(*) as count " +
                "FROM test_executions WHERE technology IS NOT NULL " +
                "GROUP BY technology"
            );
            
            Map<String, Long> techDistribution = new HashMap<>();
            techDist.collectAsList().forEach(row -> {
                techDistribution.put(row.getString(0), row.getLong(1));
            });
            results.put("spark_technologyDistribution", techDistribution);
            
            log.info("Spark analytics completed for {} executions", executions.size());
            return results;
            
        } catch (Exception e) {
            log.error("Error processing with Spark", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Cleanup Spark session
     */
    public void shutdown() {
        if (sparkSession != null) {
            sparkSession.stop();
            log.info("Spark session stopped");
        }
    }
}

