package com.radiotest.instruments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@Slf4j
public class SignalGenerator implements InstrumentInterface {
    private boolean connected = false;
    private Double currentFrequency;
    private Double currentPowerLevel;
    private final Map<String, String> parameters = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void initialize() throws InstrumentException {
        log.info("Initializing Signal Generator");
        connected = true;
    }

    @Override
    public void close() throws InstrumentException {
        log.info("Closing Signal Generator connection");
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
        log.debug("Set frequency to {} Hz", frequencyHz);
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
        this.currentPowerLevel = powerDbm;
        log.debug("Set power level to {} dBm", powerDbm);
    }

    @Override
    public Double measurePower() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Simulate power measurement with some noise
        double basePower = currentPowerLevel != null ? currentPowerLevel : -10.0;
        double noise = (random.nextDouble() - 0.5) * 0.5; // ±0.25 dBm noise
        return basePower + noise;
    }

    @Override
    public Double measureEVM() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Simulate EVM measurement (typically 0.5% - 2% for good signals)
        return 1.0 + (random.nextDouble() * 1.0); // 1.0% to 2.0%
    }

    @Override
    public Double measureACPR() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Simulate ACPR measurement (typically -40 to -50 dB for good signals)
        return -45.0 + (random.nextDouble() * 5.0); // -45 to -40 dB
    }

    @Override
    public Double measureFrequencyOffset() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        // Simulate frequency offset measurement (typically very small, in Hz)
        return (random.nextDouble() - 0.5) * 100.0; // ±50 Hz
    }

    @Override
    public void reset() throws InstrumentException {
        if (!connected) {
            throw new InstrumentException("Instrument not connected");
        }
        parameters.clear();
        currentFrequency = null;
        currentPowerLevel = null;
        log.info("Signal Generator reset");
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
        return "SIGGEN-SIM-001";
    }
}

