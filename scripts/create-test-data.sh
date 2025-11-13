#!/bin/bash
# Script to create comprehensive test data for RadioTest Automation

BASE_URL="http://localhost:8080/api"

echo "Creating test cases..."

# 5G Test Cases
curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-5G-POWER-001",
  "name": "5G Power Level Test",
  "description": "Verify power level for 5G NR signal at 3.5 GHz",
  "technology": "5G",
  "category": "Power",
  "expectedPowerMin": -10.0,
  "expectedPowerMax": -5.0,
  "expectedFrequencyHz": 3500000000,
  "expectedEvmMax": 2.0,
  "expectedAcprMax": -45.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-5G-EVM-001",
  "name": "5G EVM Measurement",
  "description": "Measure Error Vector Magnitude for 5G modulation",
  "technology": "5G",
  "category": "Modulation",
  "expectedPowerMin": -8.0,
  "expectedPowerMax": -3.0,
  "expectedFrequencyHz": 3500000000,
  "expectedEvmMax": 1.5,
  "expectedAcprMax": -48.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-5G-ACPR-001",
  "name": "5G ACPR Test",
  "description": "Adjacent Channel Power Ratio measurement for 5G",
  "technology": "5G",
  "category": "Spectral",
  "expectedPowerMin": -12.0,
  "expectedPowerMax": -7.0,
  "expectedFrequencyHz": 3600000000,
  "expectedEvmMax": 2.5,
  "expectedAcprMax": -42.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

# LTE Test Cases
curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-LTE-POWER-001",
  "name": "LTE Power Level Test",
  "description": "Verify power level for LTE signal at 2.6 GHz",
  "technology": "LTE",
  "category": "Power",
  "expectedPowerMin": -9.0,
  "expectedPowerMax": -4.0,
  "expectedFrequencyHz": 2600000000,
  "expectedEvmMax": 3.0,
  "expectedAcprMax": -40.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-LTE-FREQ-001",
  "name": "LTE Frequency Accuracy",
  "description": "Measure frequency accuracy for LTE carrier",
  "technology": "LTE",
  "category": "Frequency",
  "expectedPowerMin": -11.0,
  "expectedPowerMax": -6.0,
  "expectedFrequencyHz": 2600000000,
  "expectedEvmMax": 2.8,
  "expectedAcprMax": -43.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

# W-CDMA Test Cases
curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-WCDMA-POWER-001",
  "name": "W-CDMA Power Test",
  "description": "Power measurement for W-CDMA signal at 2.1 GHz",
  "technology": "W-CDMA",
  "category": "Power",
  "expectedPowerMin": -10.5,
  "expectedPowerMax": -5.5,
  "expectedFrequencyHz": 2100000000,
  "expectedEvmMax": 3.5,
  "expectedAcprMax": -38.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-WCDMA-MOD-001",
  "name": "W-CDMA Modulation Quality",
  "description": "Modulation quality test for W-CDMA",
  "technology": "W-CDMA",
  "category": "Modulation",
  "expectedPowerMin": -9.5,
  "expectedPowerMax": -4.5,
  "expectedFrequencyHz": 2100000000,
  "expectedEvmMax": 4.0,
  "expectedAcprMax": -35.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

# GSM Test Cases
curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-GSM-POWER-001",
  "name": "GSM Power Level",
  "description": "Power level verification for GSM at 900 MHz",
  "technology": "GSM",
  "category": "Power",
  "expectedPowerMin": -8.0,
  "expectedPowerMax": -3.0,
  "expectedFrequencyHz": 900000000,
  "expectedEvmMax": 5.0,
  "expectedAcprMax": -30.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

curl -X POST "$BASE_URL/test-cases" -H "Content-Type: application/json" -d '{
  "testCaseId": "TC-GSM-FREQ-001",
  "name": "GSM Frequency Stability",
  "description": "Frequency stability test for GSM carrier",
  "technology": "GSM",
  "category": "Frequency",
  "expectedPowerMin": -7.5,
  "expectedPowerMax": -2.5,
  "expectedFrequencyHz": 900000000,
  "expectedEvmMax": 5.5,
  "expectedAcprMax": -28.0,
  "enabled": true
}' 2>/dev/null | python3 -m json.tool | head -3

echo ""
echo "Test cases created. Now running test executions..."

# Run tests for each test case
for testCase in "TC-5G-POWER-001" "TC-5G-EVM-001" "TC-5G-ACPR-001" "TC-LTE-POWER-001" "TC-LTE-FREQ-001" "TC-WCDMA-POWER-001" "TC-WCDMA-MOD-001" "TC-GSM-POWER-001" "TC-GSM-FREQ-001"; do
  echo "Running $testCase..."
  curl -X POST "$BASE_URL/test-runner/run/$testCase" 2>/dev/null | python3 -m json.tool | head -2
  sleep 1
done

echo ""
echo "Test data creation complete!"


