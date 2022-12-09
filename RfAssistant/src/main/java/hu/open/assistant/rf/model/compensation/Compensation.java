package hu.open.assistant.rf.model.compensation;

import hu.open.assistant.commons.util.NumberHelper;

/**
 * Stores and holds together attenuation values for a generic (profile) compensation. There are 3 values for LOW, MID
 * and HIGH channels on multiple WCDMA and GSM bands, both on TX (transmit) and RX (receive) side. It can check if any
 * of its values are greater than zero or differ from another compensations values. The compensation can be reset to an
 * empty state or negated. It can create an exact copy of itself with same values. It has methods to retrieve and to
 * modify (not replace) its compensation values.
 */
public abstract class Compensation {

    protected double[] wcdma1TxValues;
    protected double[] wcdma1RxValues;
    protected double[] wcdma8TxValues;
    protected double[] wcdma8RxValues;
    protected double[] gsm900TxValues;
    protected double[] gsm900RxValues;
    protected double[] gsm1800TxValues;
    protected double[] gsm1800RxValues;
    protected int serial;
    protected String name;

    public Compensation(int serial, String name) {
        this.serial = serial;
        this.name = name;
        wcdma1TxValues = new double[3];
        wcdma1RxValues = new double[3];
        wcdma8TxValues = new double[3];
        wcdma8RxValues = new double[3];
        gsm900TxValues = new double[3];
        gsm900RxValues = new double[3];
        gsm1800TxValues = new double[3];
        gsm1800RxValues = new double[3];
    }

    public double[] getWcdma1TxValues() {
        return wcdma1TxValues;
    }

    public double[] getWcdma1RxValues() {
        return wcdma1RxValues;
    }

    public double[] getWcdma8TxValues() {
        return wcdma8TxValues;
    }

    public double[] getWcdma8RxValues() {
        return wcdma8RxValues;
    }

    public double[] getGsm900TxValues() {
        return gsm900TxValues;
    }

    public double[] getGsm900RxValues() {
        return gsm900RxValues;
    }

    public double[] getGsm1800TxValues() {
        return gsm1800TxValues;
    }

    public double[] getGsm1800RxValues() {
        return gsm1800RxValues;
    }

    public String getName() {
        return name;
    }

    public int getSerial() {
        return serial;
    }

    public boolean isEmpty() {
        return isArrayEmpty(wcdma1TxValues) && isArrayEmpty(wcdma1RxValues) &&
                isArrayEmpty(wcdma8TxValues) && isArrayEmpty(wcdma8RxValues) &&
                isArrayEmpty(gsm900TxValues) && isArrayEmpty(gsm900RxValues) &&
                isArrayEmpty(gsm1800RxValues) && isArrayEmpty(gsm1800TxValues);
    }

    protected boolean isArrayEmpty(double[] values) {
        for (double value : values) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    public void copyCompensation(Compensation compensation) {
        this.wcdma1TxValues = compensation.getWcdma1TxValues().clone();
        this.wcdma1RxValues = compensation.getWcdma1RxValues().clone();
        this.wcdma8TxValues = compensation.getWcdma8TxValues().clone();
        this.wcdma8RxValues = compensation.getWcdma8RxValues().clone();
        this.gsm900TxValues = compensation.getGsm900TxValues().clone();
        this.gsm900RxValues = compensation.getGsm900RxValues().clone();
        this.gsm1800TxValues = compensation.getGsm1800TxValues().clone();
        this.gsm1800RxValues = compensation.getGsm1800RxValues().clone();
    }

    public void resetWholeCompensation() {
        resetCompensation("wcdma1_tx");
        resetCompensation("wcdma1_rx");
        resetCompensation("wcdma8_tx");
        resetCompensation("wcdma8_rx");
        resetCompensation("gsm900_tx");
        resetCompensation("gsm900_rx");
        resetCompensation("gsm1800_tx");
        resetCompensation("gsm1800_rx");
    }

    public void negateCompensation() {
        negateValues(wcdma1TxValues);
        negateValues(wcdma1RxValues);
        negateValues(wcdma8TxValues);
        negateValues(wcdma8RxValues);
        negateValues(gsm900TxValues);
        negateValues(gsm900RxValues);
        negateValues(gsm1800TxValues);
        negateValues(gsm1800RxValues);
    }

    protected void negateValues(double[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] *= -1;
        }
    }

    public boolean differsFrom(Compensation other) {
        for (int i = 0; i < 3; i++) {
            if (this.wcdma1TxValues[i] != other.wcdma1TxValues[i] || this.wcdma1RxValues[i] != other.wcdma1RxValues[i] ||
                    this.wcdma8TxValues[i] != other.wcdma8TxValues[i] || this.wcdma8RxValues[i] != other.wcdma8RxValues[i] ||
                    this.gsm900TxValues[i] != other.gsm900TxValues[i] || this.gsm900RxValues[i] != other.gsm900RxValues[i] ||
                    this.gsm1800TxValues[i] != other.gsm1800TxValues[i] || this.gsm1800RxValues[i] != other.gsm1800RxValues[i]) {
                return true;
            }
        }
        return false;
    }

    public void resetCompensation(String band) {
        switch (band) {
            case "wcdma1_tx":
                wcdma1TxValues = new double[3];
                break;
            case "wcdma1_rx":
                wcdma1RxValues = new double[3];
                break;
            case "wcdma8_tx":
                wcdma8TxValues = new double[3];
                break;
            case "wcdma8_rx":
                wcdma8RxValues = new double[3];
                break;
            case "gsm900_tx":
                gsm900TxValues = new double[3];
                break;
            case "gsm900_rx":
                gsm900RxValues = new double[3];
                break;
            case "gsm1800_tx":
                gsm1800TxValues = new double[3];
                break;
            case "gsm1800_rx":
                gsm1800RxValues = new double[3];
                break;
        }
    }

    public void addCompensation(String band, int i, double value) {
        switch (band) {
            case "wcdma1_tx":
                wcdma1TxValues[i] = NumberHelper.oneDecimalPlaceOf(wcdma1TxValues[i] + value);
                break;
            case "wcdma1_rx":
                wcdma1RxValues[i] = NumberHelper.oneDecimalPlaceOf(wcdma1RxValues[i] + value);
                break;
            case "wcdma8_tx":
                wcdma8TxValues[i] = NumberHelper.oneDecimalPlaceOf(wcdma8TxValues[i] + value);
                break;
            case "wcdma8_rx":
                wcdma8RxValues[i] = NumberHelper.oneDecimalPlaceOf(wcdma8RxValues[i] + value);
                break;
            case "gsm900_tx":
                gsm900TxValues[i] = NumberHelper.oneDecimalPlaceOf(gsm900TxValues[i] + value);
                break;
            case "gsm900_rx":
                gsm900RxValues[i] = NumberHelper.oneDecimalPlaceOf(gsm900RxValues[i] + value);
                break;
            case "gsm1800_tx":
                gsm1800TxValues[i] = NumberHelper.oneDecimalPlaceOf(gsm1800TxValues[i] + value);
                break;
            case "gsm1800_rx":
                gsm1800RxValues[i] = NumberHelper.oneDecimalPlaceOf(gsm1800RxValues[i] + value);
                break;
        }
    }
}

