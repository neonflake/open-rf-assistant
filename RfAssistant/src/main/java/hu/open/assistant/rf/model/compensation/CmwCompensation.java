package hu.open.assistant.rf.model.compensation;

import hu.open.assistant.commons.util.NumberHelper;

/**
 * Stores and holds together attenuation values for a CMW type (profile) compensation. Extends the generic variant
 * with storing values for multiple LTE bands. All functional methods are modified to take the LTE values into account.
 */
public class CmwCompensation extends Compensation {

    protected double[] lte1TxValues;
    protected double[] lte1RxValues;
    protected double[] lte3TxValues;
    protected double[] lte3RxValues;
    protected double[] lte7TxValues;
    protected double[] lte7RxValues;
    protected double[] lte20TxValues;
    protected double[] lte20RxValues;

    public CmwCompensation(int serial, String name) {
        super(serial, name);
        lte1TxValues = new double[3];
        lte1RxValues = new double[3];
        lte3TxValues = new double[3];
        lte3RxValues = new double[3];
        lte7TxValues = new double[3];
        lte7RxValues = new double[3];
        lte20TxValues = new double[3];
        lte20RxValues = new double[3];
    }

    public double[] getLte1TxValues() {
        return lte1TxValues;
    }

    public double[] getLte1RxValues() {
        return lte1RxValues;
    }

    public double[] getLte3TxValues() {
        return lte3TxValues;
    }

    public double[] getLte3RxValues() {
        return lte3RxValues;
    }

    public double[] getLte7TxValues() {
        return lte7TxValues;
    }

    public double[] getLte7RxValues() {
        return lte7RxValues;
    }

    public double[] getLte20TxValues() {
        return lte20TxValues;
    }

    public double[] getLte20RxValues() {
        return lte20RxValues;
    }

    @Override
    public void copyCompensation(Compensation compensation) {
        super.copyCompensation(compensation);
        CmwCompensation cmwCompensation = (CmwCompensation) compensation;
        this.lte1TxValues = cmwCompensation.getLte1TxValues().clone();
        this.lte1RxValues = cmwCompensation.getLte1RxValues().clone();
        this.lte3TxValues = cmwCompensation.getLte3TxValues().clone();
        this.lte3RxValues = cmwCompensation.getLte3RxValues().clone();
        this.lte7TxValues = cmwCompensation.getLte7TxValues().clone();
        this.lte7RxValues = cmwCompensation.getLte7RxValues().clone();
        this.lte20TxValues = cmwCompensation.getLte20TxValues().clone();
        this.lte20RxValues = cmwCompensation.getLte20RxValues().clone();
    }

    @Override
    public boolean isEmpty() {
        if (super.isEmpty()) {
            return isArrayEmpty(lte1TxValues) && isArrayEmpty(lte1RxValues) &&
                    isArrayEmpty(lte3TxValues) && isArrayEmpty(lte3RxValues) &&
                    isArrayEmpty(lte7TxValues) && isArrayEmpty(lte7RxValues) &&
                    isArrayEmpty(lte20TxValues) && isArrayEmpty(lte20RxValues);
        } else {
            return false;
        }
    }

    @Override
    public boolean differsFrom(Compensation other) {
        if (super.differsFrom(other)) {
            return true;
        } else {
            for (int i = 0; i < 3; i++) {
                if (this.lte1TxValues[i] != ((CmwCompensation) other).lte1TxValues[i] ||
                        this.lte1RxValues[i] != ((CmwCompensation) other).lte1RxValues[i] ||
                        this.lte3TxValues[i] != ((CmwCompensation) other).lte3TxValues[i] ||
                        this.lte3RxValues[i] != ((CmwCompensation) other).lte3RxValues[i] ||
                        this.lte7TxValues[i] != ((CmwCompensation) other).lte7TxValues[i] ||
                        this.lte7RxValues[i] != ((CmwCompensation) other).lte7RxValues[i] ||
                        this.lte20TxValues[i] != ((CmwCompensation) other).lte20TxValues[i] ||
                        this.lte20RxValues[i] != ((CmwCompensation) other).lte20RxValues[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void negateCompensation() {
        super.negateCompensation();
        negateValues(lte1TxValues);
        negateValues(lte1RxValues);
        negateValues(lte3TxValues);
        negateValues(lte3RxValues);
        negateValues(lte7TxValues);
        negateValues(lte7RxValues);
        negateValues(lte20TxValues);
        negateValues(lte20RxValues);
    }

    @Override
    public void resetWholeCompensation() {
        super.resetWholeCompensation();
        resetCompensation("lte1_tx");
        resetCompensation("lte1_rx");
        resetCompensation("lte3_tx");
        resetCompensation("lte3_rx");
        resetCompensation("lte7_tx");
        resetCompensation("lte7_rx");
        resetCompensation("lte20_tx");
        resetCompensation("lte20_rx");
    }

    @Override
    public void resetCompensation(String band) {
        super.resetCompensation(band);
        for (int i = 0; i < 3; i++) {
            switch (band) {
                case "lte1_tx":
                    lte1TxValues[i] = 0;
                    break;
                case "lte1_rx":
                    lte1RxValues[i] = 0;
                    break;
                case "lte3_tx":
                    lte3TxValues[i] = 0;
                    break;
                case "lte3_rx":
                    lte3RxValues[i] = 0;
                    break;
                case "lte7_tx":
                    lte7TxValues[i] = 0;
                    break;
                case "lte7_rx":
                    lte7RxValues[i] = 0;
                    break;
                case "lte20_tx":
                    lte20TxValues[i] = 0;
                    break;
                case "lte20_rx":
                    lte20RxValues[i] = 0;
                    break;
            }
        }
    }

    @Override
    public void addCompensation(String band, int i, double value) {
        super.addCompensation(band, i, value);
        switch (band) {
            case "lte1_tx":
                lte1TxValues[i] = NumberHelper.oneDecimalPlaceOf(lte1TxValues[i] + value);
                break;
            case "lte1_rx":
                lte1RxValues[i] = NumberHelper.oneDecimalPlaceOf(lte1RxValues[i] + value);
                break;
            case "lte3_tx":
                lte3TxValues[i] = NumberHelper.oneDecimalPlaceOf(lte3TxValues[i] + value);
                break;
            case "lte3_rx":
                lte3RxValues[i] = NumberHelper.oneDecimalPlaceOf(lte3RxValues[i] + value);
                break;
            case "lte7_tx":
                lte7TxValues[i] = NumberHelper.oneDecimalPlaceOf(lte7TxValues[i] + value);
                break;
            case "lte7_rx":
                lte7RxValues[i] = NumberHelper.oneDecimalPlaceOf(lte7RxValues[i] + value);
                break;
            case "lte20_tx":
                lte20TxValues[i] = NumberHelper.oneDecimalPlaceOf(lte20TxValues[i] + value);
                break;
            case "lte20_rx":
                lte20RxValues[i] = NumberHelper.oneDecimalPlaceOf(lte20RxValues[i] + value);
                break;
        }
    }
}

