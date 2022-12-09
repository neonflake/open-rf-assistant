package hu.open.assistant.rf.model.report.values;

/**
 * Stores and holds together values for a CMW type RF test report. It extends the generic variant with storing values
 * from multiple LTE bands.
 */
public class CmwReportValues extends ReportValues {
    private final double[] lte1TxValues = new double[3];
    private final double[] lte1RxValues = new double[3];
    private final double[] lte3TxValues = new double[3];
    private final double[] lte3RxValues = new double[3];
    private final double[] lte7TxValues = new double[3];
    private final double[] lte7RxValues = new double[3];
    private final double[] lte20TxValues = new double[3];
    private final double[] lte20RxValues = new double[3];

    public double[] getLte1TxValues() {
        return lte1TxValues;
    }

    public void setLte1TxValue(double value, int index) {
        lte1TxValues[index] = value;
    }

    public double[] getLte1RxValues() {
        return lte1RxValues;
    }

    public void setLte1RxValue(double value, int index) {
        lte1RxValues[index] = value;
    }

    public double[] getLte3TxValues() {
        return lte3TxValues;
    }

    public void setLte3TxValue(double value, int index) {
        lte3TxValues[index] = value;
    }

    public double[] getLte3RxValues() {
        return lte3RxValues;
    }

    public void setLte3RxValue(double value, int index) {
        lte3RxValues[index] = value;
    }

    public double[] getLte7TxValues() {
        return lte7TxValues;
    }

    public void setLte7TxValue(double value, int index) {
        lte7TxValues[index] = value;
    }

    public double[] getLte7RxValues() {
        return lte7RxValues;
    }

    public void setLte7RxValue(double value, int index) {
        lte7RxValues[index] = value;
    }

    public double[] getLte20TxValues() {
        return lte20TxValues;
    }

    public void setLte20TxValue(double value, int index) {
        lte20TxValues[index] = value;
    }

    public double[] getLte20RxValues() {
        return lte20RxValues;
    }

    public void setLte20RxValue(double value, int index) {
        lte20RxValues[index] = value;
    }
}
