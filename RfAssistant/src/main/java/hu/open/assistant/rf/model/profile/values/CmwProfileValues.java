package hu.open.assistant.rf.model.profile.values;

/**
 * Stores and holds together attenuation values for a CMW type RF profile. Extends the generic variant with LTE
 * attenuation value storage. Limit checking also depends on LTE values.
 */
public class CmwProfileValues extends CmuProfileValues {
    private final double[] lte1InValues = new double[3];
    private final double[] lte1OutValues = new double[3];
    private final double[] lte3InValues = new double[3];
    private final double[] lte3OutValues = new double[3];
    private final double[] lte7InValues = new double[3];
    private final double[] lte7OutValues = new double[3];
    private final double[] lte20InValues = new double[3];
    private final double[] lte20OutValues = new double[3];

    public double[] getLte1InValues() {
        return lte1InValues;
    }

    public void setLte1InValue(double value, int index) {
        lte1InValues[index] = value;
    }

    public double[] getLte1OutValues() {
        return lte1OutValues;
    }

    public void setLte1OutValue(double value, int index) {
        lte1OutValues[index] = value;
    }

    public double[] getLte3InValues() {
        return lte3InValues;
    }

    public void setLte3InValue(double value, int index) {
        lte3InValues[index] = value;
    }

    public double[] getLte3OutValues() {
        return lte3OutValues;
    }

    public void setLte3OutValue(double value, int index) {
        lte3OutValues[index] = value;
    }

    public double[] getLte7InValues() {
        return lte7InValues;
    }

    public void setLte7InValue(double value, int index) {
        lte7InValues[index] = value;
    }

    public double[] getLte7OutValues() {
        return lte7OutValues;
    }

    public void setLte7OutValue(double value, int index) {
        lte7OutValues[index] = value;
    }

    public double[] getLte20InValues() {
        return lte20InValues;
    }

    public void setLte20InValue(double value, int index) {
        lte20InValues[index] = value;
    }

    public double[] getLte20OutValues() {
        return lte20OutValues;
    }

    public void setLte20OutValue(double value, int index) {
        lte20OutValues[index] = value;
    }

    public boolean anyValueExceedLimit(int limit) {
        return super.anyValueExceedLimit(limit) ||
                valuesExceedLimit(lte1InValues, limit) || valuesExceedLimit(lte1OutValues, limit) ||
                valuesExceedLimit(lte3InValues, limit) || valuesExceedLimit(lte3OutValues, limit) ||
                valuesExceedLimit(lte7InValues, limit) || valuesExceedLimit(lte7OutValues, limit) ||
                valuesExceedLimit(lte20InValues, limit) || valuesExceedLimit(lte20OutValues, limit);
    }
}
