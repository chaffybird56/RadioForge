package com.radiotest.instruments;

public interface InstrumentInterface {
    /**
     * Initialize the instrument connection
     */
    void initialize() throws InstrumentException;

    /**
     * Close the instrument connection
     */
    void close() throws InstrumentException;

    /**
     * Set a parameter on the instrument
     */
    void setParameter(String key, String value) throws InstrumentException;

    /**
     * Get a parameter value from the instrument
     */
    String getParameter(String key) throws InstrumentException;

    /**
     * Set the frequency in Hz
     */
    void setFrequency(Double frequencyHz) throws InstrumentException;

    /**
     * Get the current frequency in Hz
     */
    Double getFrequency() throws InstrumentException;

    /**
     * Set the power level in dBm
     */
    void setPowerLevel(Double powerDbm) throws InstrumentException;

    /**
     * Measure power level in dBm
     */
    Double measurePower() throws InstrumentException;

    /**
     * Measure Error Vector Magnitude (EVM) in percentage
     */
    Double measureEVM() throws InstrumentException;

    /**
     * Measure Adjacent Channel Power Ratio (ACPR) in dB
     */
    Double measureACPR() throws InstrumentException;

    /**
     * Measure frequency offset in Hz
     */
    Double measureFrequencyOffset() throws InstrumentException;

    /**
     * Reset the instrument to default state
     */
    void reset() throws InstrumentException;

    /**
     * Check if instrument is connected and ready
     */
    boolean isConnected();

    /**
     * Get instrument identification string
     */
    String getInstrumentId() throws InstrumentException;
}

