package hu.open.assistant.rf.model.report.values;

/**
 * Stores and holds together values for a generic RF test report. There are 3 values for LOW, MID and HIGH channels on
 * multiple GSM and WCDMA bands, both on TX (transmit) and RX (receive) side.
 */
public abstract class ReportValues {
    private final double[] wcdma1TxValues = new double[3];
    private final double[] wcdma1RxValues = new double[3];
    private final double[] wcdma8TxValues = new double[3];
    private final double[] wcdma8RxValues = new double[3];
    private final double[] gsm900TxValues = new double[3];
    private final double[] gsm900RxValues = new double[3];
    private final double[] gsm1800TxValues = new double[3];
    private final double[] gsm1800RxValues = new double[3];

    public double[] getWcdma1TxValues() {
        return wcdma1TxValues;
    }

    public void setWcdma1TxValue(double value, int index) {
        wcdma1TxValues[index] = value;
    }

    public double[] getWcdma1RxValues() {
        return wcdma1RxValues;
    }

    public void setWcdma1RxValue(double value, int index) {
        wcdma1RxValues[index] = value;
    }

    public double[] getWcdma8TxValues() {
        return wcdma8TxValues;
    }

    public void setWcdma8TxValue(double value, int index) {
        wcdma8TxValues[index] = value;
    }

    public double[] getWcdma8RxValues() {
        return wcdma8RxValues;
    }

    public void setWcdma8RxValue(double value, int index) {
        wcdma8RxValues[index] = value;
    }

    public double[] getGsm900TxValues() {
        return gsm900TxValues;
    }

    public void setGsm900TxValue(double value, int index) {
        gsm900TxValues[index] = value;
    }

    public double[] getGsm900RxValues() {
        return gsm900RxValues;
    }

    public void setGsm900RxValue(double value, int index) {
        gsm900RxValues[index] = value;
    }

    public double[] getGsm1800TxValues() {
        return gsm1800TxValues;
    }

    public void setGsm1800TxValue(double value, int index) {
        gsm1800TxValues[index] = value;
    }

    public double[] getGsm1800RxValues() {
        return gsm1800RxValues;
    }

    public void setGsm1800RxValue(double value, int index) {
        gsm1800RxValues[index] = value;
    }
}

