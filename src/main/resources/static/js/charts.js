// Chart.js for instrument visualizations
let signalGenChart = null;
let spectrumChart = null;

function initializeCharts() {
    // Signal Generator Chart
    const sigGenCtx = document.getElementById('signalGenChart');
    if (sigGenCtx) {
        signalGenChart = new Chart(sigGenCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Power Level (dBm)',
                    data: [],
                    borderColor: '#8b6f47',
                    backgroundColor: 'rgba(139, 111, 71, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Signal Generator Output',
                        font: {
                            family: 'Roboto',
                            size: 14,
                            weight: 600
                        }
                    },
                    legend: {
                        display: true,
                        position: 'top'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        title: {
                            display: true,
                            text: 'Power (dBm)'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Time (s)'
                        }
                    }
                }
            }
        });
    }

    // Spectrum Analyzer Chart
    const spectrumCtx = document.getElementById('spectrumChart');
    if (spectrumCtx) {
        spectrumChart = new Chart(spectrumCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Power Spectrum (dBm)',
                    data: [],
                    borderColor: '#a0826d',
                    backgroundColor: 'rgba(160, 130, 109, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Spectrum Analyzer Measurement',
                        font: {
                            family: 'Roboto',
                            size: 14,
                            weight: 600
                        }
                    },
                    legend: {
                        display: true,
                        position: 'top'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        title: {
                            display: true,
                            text: 'Power (dBm)'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Frequency Offset (MHz)'
                        }
                    }
                }
            }
        });
    }
}

function updateSignalGenChart(measurements) {
    if (!signalGenChart || !measurements || measurements.length === 0) return;
    
    const labels = measurements.map((_, i) => (i * 0.1).toFixed(1));
    signalGenChart.data.labels = labels;
    signalGenChart.data.datasets[0].data = measurements;
    signalGenChart.update('none');
}

function updateSpectrumChart(frequencyHz, measurements) {
    if (!spectrumChart || !measurements || measurements.length === 0) return;
    
    // Create frequency offset labels (center frequency ± range)
    const centerFreqMHz = (frequencyHz || 3500000000) / 1000000;
    const spanMHz = 20; // 20 MHz span
    const numPoints = measurements.length;
    const labels = Array.from({ length: numPoints }, (_, i) => {
        const offset = (i - numPoints / 2) * (spanMHz / numPoints);
        return (centerFreqMHz + offset).toFixed(2);
    });
    
    spectrumChart.data.labels = labels;
    spectrumChart.data.datasets[0].data = measurements;
    spectrumChart.update('none');
}

// Load Chart.js from CDN
function loadChartJS() {
    if (typeof Chart === 'undefined') {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js';
        script.onload = initializeCharts;
        document.head.appendChild(script);
    } else {
        initializeCharts();
    }
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadChartJS);
} else {
    loadChartJS();
}

