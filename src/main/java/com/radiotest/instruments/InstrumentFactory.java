package com.radiotest.instruments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstrumentFactory {
    private final SignalGenerator signalGenerator;
    private final SpectrumAnalyzer spectrumAnalyzer;

    public InstrumentInterface getInstrument(String instrumentType) {
        return switch (instrumentType.toUpperCase()) {
            case "SIGNAL_GENERATOR", "SIGGEN" -> signalGenerator;
            case "SPECTRUM_ANALYZER", "SPECTRUM" -> spectrumAnalyzer;
            default -> {
                // Default to signal generator for test execution
                yield signalGenerator;
            }
        };
    }

    public InstrumentInterface getDefaultInstrument() {
        return signalGenerator;
    }
}

