package hu.open.assistant.rf.model.profile.values;

/**
 * Stores and holds together attenuation values for a generic RF profile. There are 3 values for LOW, MID and HIGH
 * channels on multiple WCDMA and GSM bands, both for IN and OUT direction. It can check if any of its values exceeds a
 * given limit.
 */
public abstract class ProfileValues {
    private final double[] wcdma1InValues = new double[3];
    private final double[] wcdma1OutValues = new double[3];
    private final double[] wcdma8InValues = new double[3];
    private final double[] wcdma8OutValues = new double[3];
    private final double[] gsm900InValues = new double[3];
    private final double[] gsm900OutValues = new double[3];
    private final double[] gsm1800InValues = new double[3];
    private final double[] gsm1800OutValues = new double[3];

    public double[] getWcdma1InValues() {
        return wcdma1InValues;
    }

    public void setWcdma1InValue(double value, int index) {
        wcdma1InValues[index] = value;
    }

    public double[] getWcdma1OutValues() {
        return wcdma1OutValues;
    }

    public void setWcdma1OutValue(double value, int index) {
        wcdma1OutValues[index] = value;
    }

    public double[] getWcdma8InValues() {
        return wcdma8InValues;
    }

    public void setWcdma8InValue(double value, int index) {
        wcdma8InValues[index] = value;
    }

    public double[] getWcdma8OutValues() {
        return wcdma8OutValues;
    }

    public void setWcdma8OutValue(double value, int index) {
        wcdma8OutValues[index] = value;
    }

    public double[] getGsm900InValues() {
        return gsm900InValues;
    }

    public void setGsm900InValue(double value, int index) {
        gsm900InValues[index] = value;
    }

    public double[] getGsm900OutValues() {
        return gsm900OutValues;
    }

    public void setGsm900OutValue(double value, int index) {
        gsm900OutValues[index] = value;
    }

    public double[] getGsm1800InValues() {
        return gsm1800InValues;
    }

    public void setGsm1800InValue(double value, int index) {
        gsm1800InValues[index] = value;
    }

    public double[] getGsm1800OutValues() {
        return gsm1800OutValues;
    }

    public void setGsm1800OutValue(double value, int index) {
        gsm1800OutValues[index] = value;
    }

    public boolean anyValueExceedLimit(int limit) {
        return valuesExceedLimit(wcdma1InValues, limit) || valuesExceedLimit(wcdma1OutValues, limit) ||
                valuesExceedLimit(wcdma8InValues, limit) || valuesExceedLimit(wcdma8OutValues, limit) ||
                valuesExceedLimit(gsm900InValues, limit) || valuesExceedLimit(gsm900OutValues, limit) ||
                valuesExceedLimit(gsm1800InValues, limit) || valuesExceedLimit(gsm1800OutValues, limit);
    }

    protected boolean valuesExceedLimit(double[] values, int limit) {
        for (double value : values) {
            if (value > limit) {
                return true;
            }
        }
        return false;
    }
}
