# RadioTest Automation - Testing & Screenshot Guide

## ✅ Backend Testing Status

All backend functionalities have been tested and verified:

- ✅ **Application Startup**: Application runs successfully on port 8080
- ✅ **Health Check**: `/actuator/health` returns `{"status":"UP"}`
- ✅ **Test Cases API**: All CRUD operations working (`/api/test-cases`)
- ✅ **Test Execution API**: Execution endpoints responding (`/api/test-executions`)
- ✅ **Test Runner API**: Test execution triggers working (`/api/test-runner/run/{testCaseId}`)
- ✅ **Predictions API**: ML predictions working (`/api/predictions/test-outcome/{testCaseId}`)
- ✅ **Frontend UI**: Dashboard loads correctly at `http://localhost:8080`

## 🚀 How to Run the Application

### Step 1: Build the Project

```bash
cd RadioTest-Automation
mvn clean install
```

**What this does**: Compiles all Java source files, runs tests, and packages the application into a JAR file.

**Expected output**: `BUILD SUCCESS` message

### Step 2: Start the Application

```bash
mvn spring-boot:run
```

**What this does**: Starts the Spring Boot application server on port 8080. The application includes:
- REST API backend (`/api/*`)
- Web dashboard frontend (`/`)
- H2 database console (`/h2-console`)
- Health monitoring (`/actuator/health`)

**Expected output**: 
```
Started RadioTestApplication in X.XXX seconds
Tomcat started on port 8080 (http) with context path ''
```

### Step 3: Verify Application is Running

Open a new terminal and run:

```bash
curl http://localhost:8080/actuator/health
```

**Expected output**: `{"status":"UP"}`

## 📸 Screenshot Guide

### 1. Dashboard Screenshot

**URL**: `http://localhost:8080`

**What to capture**:
- The main dashboard showing:
  - Header with "RadioTest Automation" logo
  - Stats cards showing:
    - Tests Passed (green card)
    - Tests Failed (brown card)
    - Total Tests (blue card)
    - Pass Rate (green card)
  - "Recent Test Executions" table with columns:
    - Test Case ID
    - Technology
    - Status (with colored badges)
    - Power Level
    - EVM
    - Duration
    - Time

**How to prepare**:
1. Start the application
2. Run a few tests (click "Run All Tests" button or run individual tests)
3. Wait for executions to complete
4. Take screenshot showing the dashboard with statistics

**File name**: `dashboard-final.png` (save to `docs/screenshots/`)

---

### 2. Test Cases Screenshot

**URL**: `http://localhost:8080` (click "Test Cases" in sidebar)

**What to capture**:
- Test Cases section showing:
  - "Create Test Case" button (top right)
  - Table with columns:
    - Test Case ID
    - Name
    - Technology (5G, LTE, W-CDMA, GSM)
    - Category (Power, Frequency, Modulation, etc.)
    - Status (Enabled/Disabled badge)
    - Actions (Run button)
  - Multiple test cases visible (at least 3-5 different technologies)

**How to prepare**:
1. Navigate to Test Cases section
2. Ensure there are test cases in the table (if empty, create some using the "Create Test Case" button)
3. Take screenshot showing the test cases table

**File name**: `test-cases-final.png` (save to `docs/screenshots/`)

**Optional**: Also capture the "Create Test Case" modal:
- Click "Create Test Case" button
- Fill in the form (don't submit)
- Take screenshot of the modal
- **File name**: `test-cases-create-modal.png`

---

### 3. Executions Screenshot

**URL**: `http://localhost:8080` (click "Executions" in sidebar)

**What to capture**:
- Executions section showing:
  - Status filter dropdown (top right)
  - Table with columns:
    - ID
    - Test Case
    - Technology
    - Status (colored badges: PASSED, FAILED, RUNNING, ERROR)
    - Power (dBm)
    - EVM (%)
    - ACPR (dB)
    - Duration
    - Time
  - Multiple execution records with different statuses

**How to prepare**:
1. Navigate to Executions section
2. Run several tests to generate execution records
3. Ensure you have executions with different statuses (PASSED, FAILED)
4. Optionally filter by status using the dropdown
5. Take screenshot showing the executions table with data

**File name**: `executions-final.png` (save to `docs/screenshots/`)

---

### 4. Reports Screenshot

**URL**: `http://localhost:8080` (click "Reports" in sidebar)

**What to capture**:
- Reports section showing:
  - "Generate Report" button (top right)
  - Report content area displaying:
    - Report title (e.g., "Weekly Report")
    - Statistics cards:
      - Total Tests
      - Passed
      - Failed
      - Pass Rate
    - Anomalies table (if any detected):
      - Test Case
      - Metric
      - Value
      - Expected
      - Severity

**How to prepare**:
1. Navigate to Reports section
2. Click "Generate Report" button
3. Wait for report to generate
4. Take screenshot showing the generated report with statistics and anomalies (if any)

**File name**: `reports-final.png` (save to `docs/screenshots/`)

---

### 5. Predictions Screenshot

**URL**: `http://localhost:8080` (click "Predictions" in sidebar)

**What to capture**:
- Predictions section showing:
  - Test case dropdown selector
  - "Get Predictions" button
  - Prediction results displaying:
    - Failure Probability (percentage with color coding)
    - Confidence level (LOW, MEDIUM, HIGH)
    - Recommendation text

**How to prepare**:
1. Navigate to Predictions section
2. Select a test case from the dropdown (one that has execution history)
3. Click "Get Predictions" button
4. Wait for predictions to load
5. Take screenshot showing the prediction results

**File name**: `predictions-final.png` (save to `docs/screenshots/`)

---

### 6. Instruments Screenshot

**URL**: `http://localhost:8080` (click "Instruments" in sidebar)

**What to capture**:
- Instruments section showing:
  - "Refresh Status" button (top right)
  - Two instrument cards:
    - **Signal Generator**:
      - Status: Connected (green badge)
      - Frequency: Current value or "Not Set"
      - Power Level: Current value or "Not Set"
      - Capabilities listed
    - **Spectrum Analyzer**:
      - Status: Connected (green badge)
      - Center Frequency: Current value or "Not Set"
      - Reference Level: Current value or "Not Set"
      - Capabilities listed
  - Instrument Visualizations section:
    - Signal Generator chart (power level over time)
    - Spectrum Analyzer chart (frequency domain spectrum)

**How to prepare**:
1. Navigate to Instruments section
2. Click "Refresh Status" button
3. Wait for charts to update
4. Take screenshot showing both instrument cards and the visualization charts

**File name**: `instruments-final.png` (save to `docs/screenshots/`)

---

## 🎯 Quick Test Workflow

To generate all screenshots efficiently:

1. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Open browser**: Navigate to `http://localhost:8080`

3. **Create test data** (if needed):
   - Go to Test Cases section
   - Click "Create Test Case" and create 2-3 test cases for different technologies

4. **Run tests**:
   - Go to Dashboard
   - Click "Run All Tests" button
   - Wait for executions to complete (refresh page)

5. **Generate report**:
   - Go to Reports section
   - Click "Generate Report"

6. **Get predictions**:
   - Go to Predictions section
   - Select a test case and click "Get Predictions"

7. **Check instruments**:
   - Go to Instruments section
   - Click "Refresh Status"

8. **Take screenshots** in this order:
   - Dashboard
   - Test Cases
   - Executions
   - Reports
   - Predictions
   - Instruments

## 📝 Notes

- All screenshots should be taken in **full-screen browser mode** for best quality
- Ensure the browser window is wide enough to show all columns in tables
- Use a modern browser (Chrome, Firefox, Safari, Edge)
- Screenshots will be automatically added to the README.md once you provide them

## 🔧 Troubleshooting

**If dashboard shows no data**:
- Run some tests first to generate execution data
- Check browser console for JavaScript errors (F12)

**If tests fail to run**:
- Check that test cases are enabled (Status = "Enabled")
- Verify application logs for errors

**If reports don't generate**:
- Ensure there are test executions in the date range
- Check browser console for API errors

**If predictions don't load**:
- Select a test case that has execution history
- Check that the test case ID exists

