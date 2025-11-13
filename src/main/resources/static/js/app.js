const API_BASE = '/api';

// Navigation
document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        const section = item.dataset.section;
        showSection(section);
        
        document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
        item.classList.add('active');
    });
});

function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');
    
    // Load data for the section
    if (sectionId === 'dashboard') {
        loadDashboard();
    } else if (sectionId === 'test-cases') {
        loadTestCases();
    } else if (sectionId === 'executions') {
        loadAllExecutions();
    } else if (sectionId === 'instruments') {
        refreshInstruments();
    }
}

// Dashboard
async function loadDashboard() {
    try {
        const [executions, testCases] = await Promise.all([
            fetch(`${API_BASE}/test-executions`).then(r => r.json()),
            fetch(`${API_BASE}/test-cases`).then(r => r.json())
        ]);

        // Update stats
        const passed = executions.filter(e => e.status === 'PASSED').length;
        const failed = executions.filter(e => e.status === 'FAILED').length;
        const total = testCases.length; // Total test cases, not executions
        const completedExecutions = executions.filter(e => e.status === 'PASSED' || e.status === 'FAILED');
        const passRate = completedExecutions.length > 0 ? ((passed / completedExecutions.length) * 100).toFixed(1) : 0;

        document.getElementById('stat-passed').textContent = passed;
        document.getElementById('stat-failed').textContent = failed;
        document.getElementById('stat-total').textContent = total;
        document.getElementById('stat-pass-rate').textContent = `${passRate}%`;

        // Update recent executions
        const recentExecutions = executions.slice(0, 10).reverse();
        const tbody = document.getElementById('executions-table-body');
        
        if (recentExecutions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No executions yet. Run a test to get started.</td></tr>';
        } else {
            tbody.innerHTML = recentExecutions.map(exec => `
                <tr>
                    <td>${exec.testCaseId || 'N/A'}</td>
                    <td>${exec.technology || 'N/A'}</td>
                    <td><span class="status-badge ${exec.status.toLowerCase()}">${exec.status}</span></td>
                    <td>${exec.powerLevel ? exec.powerLevel.toFixed(2) : 'N/A'}</td>
                    <td>${exec.evm ? exec.evm.toFixed(2) : 'N/A'}</td>
                    <td>${exec.durationMs ? (exec.durationMs / 1000).toFixed(2) + 's' : 'N/A'}</td>
                    <td>${exec.startTime ? new Date(exec.startTime).toLocaleString() : 'N/A'}</td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

// Test Cases
async function loadTestCases() {
    try {
        const testCases = await fetch(`${API_BASE}/test-cases`).then(r => r.json());
        const tbody = document.getElementById('test-cases-table-body');
        
        if (testCases.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No test cases found. Create one to get started.</td></tr>';
        } else {
            tbody.innerHTML = testCases.map(tc => `
                <tr>
                    <td>${tc.testCaseId}</td>
                    <td>${tc.name}</td>
                    <td>${tc.technology}</td>
                    <td>${tc.category}</td>
                    <td><span class="status-badge ${tc.enabled ? 'passed' : 'failed'}">${tc.enabled ? 'Enabled' : 'Disabled'}</span></td>
                    <td>
                        <button class="btn btn-primary" style="padding: 0.25rem 0.75rem; font-size: 0.75rem;" onclick="runTest('${tc.testCaseId}')">Run</button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading test cases:', error);
        document.getElementById('test-cases-table-body').innerHTML = 
            '<tr><td colspan="6" class="empty-state">Error loading test cases.</td></tr>';
    }
}

// Executions
async function loadAllExecutions() {
    try {
        let url = `${API_BASE}/test-executions`;
        const statusFilter = document.getElementById('status-filter')?.value;
        if (statusFilter) {
            url = `${API_BASE}/test-executions/status/${statusFilter}`;
        }
        
        const executions = await fetch(url).then(r => r.json());
        const tbody = document.getElementById('all-executions-table-body');
        
        if (executions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="empty-state">No executions found.</td></tr>';
        } else {
            tbody.innerHTML = executions.reverse().map(exec => `
                <tr>
                    <td>${exec.id}</td>
                    <td>${exec.testCaseId || 'N/A'}</td>
                    <td>${exec.technology || 'N/A'}</td>
                    <td><span class="status-badge ${exec.status.toLowerCase()}">${exec.status}</span></td>
                    <td>${exec.powerLevel ? exec.powerLevel.toFixed(2) : 'N/A'}</td>
                    <td>${exec.evm ? exec.evm.toFixed(2) : 'N/A'}</td>
                    <td>${exec.acpr ? exec.acpr.toFixed(2) : 'N/A'}</td>
                    <td>${exec.durationMs ? (exec.durationMs / 1000).toFixed(2) + 's' : 'N/A'}</td>
                    <td>${exec.startTime ? new Date(exec.startTime).toLocaleString() : 'N/A'}</td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading executions:', error);
    }
}

function filterExecutions() {
    loadAllExecutions();
}

async function loadExecutions() {
    await loadDashboard();
}

// Run Tests
async function runTest(testCaseId) {
    try {
        const response = await fetch(`${API_BASE}/test-runner/run/${testCaseId}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            alert('Test started successfully!');
            setTimeout(() => {
                loadDashboard();
                loadAllExecutions();
            }, 2000);
        } else {
            const error = await response.json();
            alert('Error: ' + (error.message || 'Failed to start test'));
        }
    } catch (error) {
        console.error('Error running test:', error);
        alert('Error running test: ' + error.message);
    }
}

async function runAllTests() {
    try {
        const response = await fetch(`${API_BASE}/test-runner/run/all`, {
            method: 'POST'
        });
        
        if (response.ok) {
            alert('All tests started!');
            setTimeout(() => {
                loadDashboard();
                loadAllExecutions();
            }, 2000);
        } else {
            alert('Failed to start tests');
        }
    } catch (error) {
        console.error('Error running all tests:', error);
        alert('Error: ' + error.message);
    }
}

// Create Test Case Modal
function showCreateTestCaseModal() {
    document.getElementById('create-test-case-modal').classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

async function createTestCase(event) {
    event.preventDefault();
    
    const testCase = {
        testCaseId: document.getElementById('tc-id').value,
        name: document.getElementById('tc-name').value,
        description: document.getElementById('tc-description').value,
        technology: document.getElementById('tc-technology').value,
        category: document.getElementById('tc-category').value,
        expectedPowerMin: document.getElementById('tc-power-min').value ? parseFloat(document.getElementById('tc-power-min').value) : null,
        expectedPowerMax: document.getElementById('tc-power-max').value ? parseFloat(document.getElementById('tc-power-max').value) : null,
        expectedFrequencyHz: document.getElementById('tc-frequency').value ? parseFloat(document.getElementById('tc-frequency').value) : null,
        expectedEvmMax: document.getElementById('tc-evm-max').value ? parseFloat(document.getElementById('tc-evm-max').value) : null,
        expectedAcprMax: document.getElementById('tc-acpr-max').value ? parseFloat(document.getElementById('tc-acpr-max').value) : null,
        enabled: true
    };

    try {
        const response = await fetch(`${API_BASE}/test-cases`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(testCase)
        });

        if (response.ok) {
            alert('Test case created successfully!');
            closeModal('create-test-case-modal');
            document.getElementById('create-test-case-form').reset();
            loadTestCases();
        } else {
            const error = await response.json();
            alert('Error: ' + (error.message || 'Failed to create test case'));
        }
    } catch (error) {
        console.error('Error creating test case:', error);
        alert('Error: ' + error.message);
    }
}

// Reports
async function generateReport() {
    try {
        const endTime = new Date();
        const startTime = new Date(endTime.getTime() - 7 * 24 * 60 * 60 * 1000); // Last 7 days
        
        const response = await fetch(
            `${API_BASE}/test-reports/generate?testSuite=Weekly Report&startTime=${startTime.toISOString()}&endTime=${endTime.toISOString()}`,
            {
                method: 'POST'
            }
        );
        
        if (response.ok) {
            const report = await response.json();
            displayReport(report);
        } else {
            const errorText = await response.text();
            console.error('Failed to generate report:', errorText);
            alert('Failed to generate report: ' + (errorText || response.statusText));
        }
    } catch (error) {
        console.error('Error generating report:', error);
        alert('Error: ' + error.message);
    }
}

function displayReport(report) {
    const content = document.getElementById('report-content');
    content.innerHTML = `
        <div style="margin-bottom: 2rem;">
            <h3 style="margin-bottom: 1rem;">${report.testSuite}</h3>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-value">${report.totalTests}</div>
                        <div class="stat-label">Total Tests</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-value">${report.passedTests}</div>
                        <div class="stat-label">Passed</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-value">${report.failedTests}</div>
                        <div class="stat-label">Failed</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-value">${report.passRate.toFixed(1)}%</div>
                        <div class="stat-label">Pass Rate</div>
                    </div>
                </div>
            </div>
        </div>
        ${report.anomalies && report.anomalies.length > 0 ? `
            <div style="margin-top: 2rem;">
                <h4 style="margin-bottom: 1rem;">Anomalies Detected: ${report.anomalies.length}</h4>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Test Case</th>
                                <th>Metric</th>
                                <th>Value</th>
                                <th>Expected</th>
                                <th>Severity</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${report.anomalies.map(a => `
                                <tr>
                                    <td>${a.testCaseId}</td>
                                    <td>${a.metric}</td>
                                    <td>${a.value.toFixed(2)}</td>
                                    <td>${a.expected.toFixed(2)}</td>
                                    <td><span class="status-badge ${a.severity.toLowerCase()}">${a.severity}</span></td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        ` : ''}
    `;
}

// Predictions
async function loadPredictions() {
    const testCaseId = document.getElementById('prediction-test-case').value;
    if (!testCaseId) {
        alert('Please select a test case');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/predictions/test-outcome/${testCaseId}`);
        if (response.ok) {
            const prediction = await response.json();
            displayPredictions(prediction);
        } else {
            alert('Failed to load predictions');
        }
    } catch (error) {
        console.error('Error loading predictions:', error);
        alert('Error: ' + error.message);
    }
}

function displayPredictions(prediction) {
    const results = document.getElementById('prediction-results');
    const failureProb = (prediction.failureProbability * 100).toFixed(1);
    
    results.innerHTML = `
        <div class="prediction-card">
            <h4>Failure Probability</h4>
            <div class="value" style="color: ${prediction.failureProbability > 0.7 ? 'var(--danger)' : prediction.failureProbability > 0.4 ? 'var(--warning)' : 'var(--success)'}">
                ${failureProb}%
            </div>
            <div class="label">Confidence: ${prediction.confidence}</div>
        </div>
        <div class="prediction-card">
            <h4>Recommendation</h4>
            <div class="value" style="font-size: 1rem; font-weight: 400;">${prediction.recommendation}</div>
        </div>
    `;
}

// Load test cases for prediction dropdown
async function loadPredictionTestCases() {
    try {
        const testCases = await fetch(`${API_BASE}/test-cases`).then(r => r.json());
        const select = document.getElementById('prediction-test-case');
        select.innerHTML = '<option value="">Select a test case...</option>' +
            testCases.map(tc => `<option value="${tc.testCaseId}">${tc.testCaseId} - ${tc.name}</option>`).join('');
    } catch (error) {
        console.error('Error loading test cases for predictions:', error);
    }
}

// Instruments
function refreshInstruments() {
    // Simulate instrument status - in real app, this would query instrument status
    document.getElementById('siggen-status').textContent = 'Connected';
    document.getElementById('siggen-status').className = 'status-badge passed';
    document.getElementById('spectrum-status').textContent = 'Connected';
    document.getElementById('spectrum-status').className = 'status-badge passed';
    
    // Update instrument details from recent test executions
    loadRecentInstrumentUsage();
    
    // Update charts with sample data
    updateInstrumentCharts();
}

function updateInstrumentCharts() {
    // Generate sample signal generator data
    const sigGenData = [];
    const basePower = -10.0;
    for (let i = 0; i < 50; i++) {
        sigGenData.push(basePower + (Math.random() - 0.5) * 0.5);
    }
    if (typeof updateSignalGenChart === 'function') {
        updateSignalGenChart(sigGenData);
    }
    
    // Generate sample spectrum analyzer data (frequency sweep)
    const spectrumData = [];
    const centerFreq = 3500; // MHz
    for (let i = 0; i < 100; i++) {
        const offset = (i - 50) * 0.2; // MHz offset
        const power = -20 - Math.abs(offset) * 2 + (Math.random() - 0.5) * 1;
        spectrumData.push(power);
    }
    if (typeof updateSpectrumChart === 'function') {
        updateSpectrumChart(3500000000, spectrumData);
    }
}

function loadRecentInstrumentUsage() {
    // This would typically fetch from an instrument status API
    // For now, we'll show placeholder data
    const recentExecutions = document.querySelectorAll('#executions-table-body tr');
    if (recentExecutions.length > 0) {
        // Extract frequency from recent execution if available
        const firstRow = recentExecutions[0];
        const cells = firstRow.querySelectorAll('td');
        if (cells.length > 0) {
            // Would parse and display actual values
        }
    }
}

function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');
    
    // Load data for the section
    if (sectionId === 'dashboard') {
        loadDashboard();
    } else if (sectionId === 'test-cases') {
        loadTestCases();
    } else if (sectionId === 'executions') {
        loadAllExecutions();
    } else if (sectionId === 'instruments') {
        refreshInstruments();
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadDashboard();
    loadTestCases();
    loadAllExecutions();
    loadPredictionTestCases();
    
    // Refresh dashboard every 5 seconds
    setInterval(() => {
        if (document.getElementById('dashboard').classList.contains('active')) {
            loadDashboard();
        }
    }, 5000);
});

// Close modal on outside click
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
});

