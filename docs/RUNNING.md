# Running RadioTest Automation Framework

## Quick Start Guide

### Prerequisites Check

```bash
# Check Java version (should be 17+)
java -version

# Check Maven
mvn -version
```

### Step-by-Step Execution

#### 1. Set Java 17 Environment

```bash
# macOS with Homebrew
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home

# Or use java_home utility
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Verify
echo $JAVA_HOME
$JAVA_HOME/bin/java -version
```

#### 2. Build the Project

```bash
cd RadioTest-Automation
mvn clean install
```

**Expected Output:**
```
[INFO] Scanning for projects...
[INFO] Building RadioTest Automation 1.0.0
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ radiotest-automation ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 29 source files with javac [debug release 17] to target/classes
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXX s
```

#### 3. Start the Application

```bash
mvn spring-boot:run
```

**Expected Startup Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| []| | ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v3.2.0)

2025-11-13 XX:XX:XX - Starting RadioTestApplication
2025-11-13 XX:XX:XX - The following 1 profile is active: "default"
2025-11-13 XX:XX:XX - HikariPool-1 - Starting...
2025-11-13 XX:XX:XX - HikariPool-1 - Start completed.
2025-11-13 XX:XX:XX - HHH000204: Processing PersistenceUnitInfo
2025-11-13 XX:XX:XX - HHH000412: Hibernate ORM core version 6.4.1.Final
2025-11-13 XX:XX:XX - HHH000206: hibernate.properties not found
2025-11-13 XX:XX:XX - HHH000021: Bytecode provider name : bytebuddy
2025-11-13 XX:XX:XX - HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
2025-11-13 XX:XX:XX - HHH000182: No default (no-argument) constructor for class: com.radiotest.model.TestCase
2025-11-13 XX:XX:XX - HHH000182: No default (no-argument) constructor for class: com.radiotest.model.TestExecution
2025-11-13 XX:XX:XX - HHH000228: Running hbm2ddl schema export
2025-11-13 XX:XX:XX - Schema export complete
2025-11-13 XX:XX:XX - Starting service [Tomcat]
2025-11-13 XX:XX:XX - Starting Servlet engine: [Apache Tomcat/10.1.16]
2025-11-13 XX:XX:XX - Initializing Spring embedded WebApplicationContext
2025-11-13 XX:XX:XX - Root WebApplicationContext: initialization completed in XXX ms
2025-11-13 XX:XX:XX - Exposing 2 endpoint(s) beneath base path '/actuator'
2025-11-13 XX:XX:XX - Tomcat started on port 8080 (http) with context path ''
2025-11-13 XX:XX:XX - Started RadioTestApplication in X.XXX seconds (process running for X.XXX)
```

#### 4. Verify Application is Running

```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Expected: {"status":"UP"}

# Check API
curl http://localhost:8080/api/test-cases

# Expected: [] (empty array initially)
```

#### 5. Access Web Interface

Open your browser and navigate to:
- **Main Dashboard**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:radiotestdb`
  - Username: `sa`
  - Password: (leave empty)

## Testing the Application

### Create a Test Case via API

```bash
curl -X POST http://localhost:8080/api/test-cases \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC-5G-DEMO",
    "name": "5G Demo Test",
    "technology": "5G",
    "category": "Power",
    "expectedPowerMin": -10.0,
    "expectedPowerMax": -5.0,
    "expectedFrequencyHz": 3500000000,
    "enabled": true
  }'
```

### Run a Test

```bash
curl -X POST http://localhost:8080/api/test-runner/run/TC-5G-DEMO
```

### View Results

```bash
# Get all executions
curl http://localhost:8080/api/test-executions | python3 -m json.tool

# Get executions by status
curl http://localhost:8080/api/test-executions/status/PASSED
```

### Generate a Report

```bash
curl "http://localhost:8080/api/test-reports/generate?testSuite=Demo&startTime=2025-11-13T00:00:00&endTime=2025-11-13T23:59:59" | python3 -m json.tool
```

## Using the Web Interface

1. **Dashboard**: View statistics and recent executions
2. **Test Cases**: Create and manage test cases
3. **Executions**: View all test execution results
4. **Reports**: Generate comprehensive test reports
5. **Predictions**: Get ML-based predictions for test outcomes
6. **Instruments**: Monitor Signal Generator and Spectrum Analyzer status with visualizations

## Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Java Version Issues

```bash
# Ensure Java 17 is being used
export JAVA_HOME=/path/to/java17
mvn clean compile
```

### Compilation Errors

```bash
# Clean and rebuild
mvn clean
mvn compile
```

### Application Won't Start

Check logs:
```bash
tail -f /tmp/radiotest.log
# Or if running in foreground, check console output
```

## Production Deployment

For production, update `application.properties`:

```properties
# Use PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/radiotest
spring.datasource.username=your_username
spring.datasource.password=your_password

# Configure Kafka
spring.kafka.bootstrap-servers=kafka-server:9092
```

## Performance Notes

- Application starts in ~2-3 seconds
- Test execution is asynchronous (non-blocking)
- Database uses H2 in-memory for development (fast, no persistence)
- Kafka integration is optional (application works without it)
- Spark analytics initialized on first use

