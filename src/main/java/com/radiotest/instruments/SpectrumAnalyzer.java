package com.radiotest.instruments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@Slf4j
public class SpectrumAnalyzer implements InstrumentInterface {
    private boolean connected = false;
    private Double currentFrequency;
    private Double currentPowerLevel;
    private final Map<String, String> parameters = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void initialize() throws InstrumentException {
        log.info("Initializing Spectrum Analyzer");
        connected = true;
    }

    @Override
    public void close() throws InstrumentException {
        log.info("Closing Spectrum Analyzer connection");
        connected = false;
    }

    @Override
    public void setParameter(String key, String value) throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        parameters.put(key, value);
        log.debug("Set parameter {} = {}", key, value);
    }

    @Override
    public String getParameter(String key) throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        return parameters.get(key);
    }

    @Override
    public void setFrequency(Double frequencyHz) throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        this.currentFrequency = frequencyHz;
        log.debug("Set center frequency to {} Hz", frequencyHz);
    }

    @Override
    public Double getFrequency() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        return currentFrequency;
    }

    @Override
    public void setPowerLevel(Double powerDbm) throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Spectrum analyzer typically doesn't set power, but we can set reference level
        this.currentPowerLevel = powerDbm;
        log.debug("Set reference level to {} dBm", powerDbm);
    }

    @Override
    public Double measurePower() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Simulate power measurement with some noise
        double basePower = currentPowerLevel != null ? currentPowerLevel : -20.0;
        double noise = (random.nextDouble() - 0.5) * 0.3; // ±0.15 dBm noise
        return basePower + noise;
    }

    @Override
    public Double measureEVM() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Spectrum analyzer can measure EVM with demodulation capability
        return 1.2 + (random.nextDouble() * 0.8); // 1.2% to 2.0%
    }

    @Override
    public Double measureACPR() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Spectrum analyzer is ideal for ACPR measurements
        return -47.0 + (random.nextDouble() * 4.0); // -47 to -43 dB
    }

    @Override
    public Double measureFrequencyOffset() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Spectrum analyzer can measure frequency offset
        return (random.nextDouble() - 0.5) * 80.0; // ±40 Hz
    }

    @Override
    public void reset() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        parameters.clear();
        currentFrequency = null;
        currentPowerLevel = null;
        log.info("Spectrum Analyzer reset");
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String getInstrumentId() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        return "SPECTRUM-ANALYZER-SIM-001";
    }
}

