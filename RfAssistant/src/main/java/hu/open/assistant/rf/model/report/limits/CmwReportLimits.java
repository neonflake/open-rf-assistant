package hu.open.assistant.rf.model.report.limits;

/**
 * Stores limit values for a CMW type RF test report. Extends the generic variant by storing additional limits for
 * multiple LTE bands.
 */
public class CmwReportLimits extends ReportLimits {

    private static final int DEFAULT_LTE_TX_MIN = 18;
    private static final int DEFAULT_LTE_TX_MAX = 28;
    private static final int DEFAULT_LTE_RX_MIN = -87;
    private static final int DEFAULT_LTE_RX_MAX = -73;

    protected int lte1TxMax = DEFAULT_LTE_TX_MAX;
    protected int lte1RxMax = DEFAULT_LTE_RX_MAX;
    protected int lte3TxMax = DEFAULT_LTE_TX_MAX;
    protected int lte3RxMax = DEFAULT_LTE_RX_MAX;
    protected int lte7TxMax = DEFAULT_LTE_TX_MAX;
    protected int lte7RxMax = DEFAULT_LTE_RX_MAX;
    protected int lte20TxMax = DEFAULT_LTE_TX_MAX;
    protected int lte20RxMax = DEFAULT_LTE_RX_MAX;
    protected int lte1TxMin = DEFAULT_LTE_TX_MIN;
    protected int lte1RxMin = DEFAULT_LTE_RX_MIN;
    protected int lte3TxMin = DEFAULT_LTE_TX_MIN;
    protected int lte3RxMin = DEFAULT_LTE_RX_MIN;
    protected int lte7TxMin = DEFAULT_LTE_TX_MIN;
    protected int lte7RxMin = DEFAULT_LTE_RX_MIN;
    protected int lte20TxMin = DEFAULT_LTE_TX_MIN;
    protected int lte20RxMin = DEFAULT_LTE_RX_MIN;

    public int getLte1TxMax() {
        return lte1TxMax;
    }

    public void setLte1TxMax(int lte1TxMax) {
        this.lte1TxMax = lte1TxMax;
    }

    public int getLte1RxMax() {
        return lte1RxMax;
    }

    public void setLte1RxMax(int lte1RxMax) {
        this.lte1RxMax = lte1RxMax;
    }

    public int getLte3TxMax() {
        return lte3TxMax;
    }

    public void setLte3TxMax(int lte3TxMax) {
        this.lte3TxMax = lte3TxMax;
    }

    public int getLte3RxMax() {
        return lte3RxMax;
    }

    public void setLte3RxMax(int lte3RxMax) {
        this.lte3RxMax = lte3RxMax;
    }

    public int getLte7TxMax() {
        return lte7TxMax;
    }

    public void setLte7TxMax(int lte7TxMax) {
        this.lte7TxMax = lte7TxMax;
    }

    public int getLte7RxMax() {
        return lte7RxMax;
    }

    public void setLte7RxMax(int lte7RxMax) {
        this.lte7RxMax = lte7RxMax;
    }

    public int getLte20TxMax() {
        return lte20TxMax;
    }

    public void setLte20TxMax(int lte20TxMax) {
        this.lte20TxMax = lte20TxMax;
    }

    public int getLte20RxMax() {
        return lte20RxMax;
    }

    public void setLte20RxMax(int lte20RxMax) {
        this.lte20RxMax = lte20RxMax;
    }

    public int getLte1TxMin() {
        return lte1TxMin;
    }

    public void setLte1TxMin(int lte1TxMin) {
        this.lte1TxMin = lte1TxMin;
    }

    public int getLte1RxMin() {
        return lte1RxMin;
    }

    public void setLte1RxMin(int lte1RxMin) {
        this.lte1RxMin = lte1RxMin;
    }

    public int getLte3TxMin() {
        return lte3TxMin;
    }

    public void setLte3TxMin(int lte3TxMin) {
        this.lte3TxMin = lte3TxMin;
    }

    public int getLte3RxMin() {
        return lte3RxMin;
    }

    public void setLte3RxMin(int lte3RxMin) {
        this.lte3RxMin = lte3RxMin;
    }

    public int getLte7TxMin() {
        return lte7TxMin;
    }

    public void setLte7TxMin(int lte7TxMin) {
        this.lte7TxMin = lte7TxMin;
    }

    public int getLte7RxMin() {
        return lte7RxMin;
    }

    public void setLte7RxMin(int lte7RxMin) {
        this.lte7RxMin = lte7RxMin;
    }

    public int getLte20TxMin() {
        return lte20TxMin;
    }

    public void setLte20TxMin(int lte20TxMin) {
        this.lte20TxMin = lte20TxMin;
    }

    public int getLte20RxMin() {
        return lte20RxMin;
    }

    public void setLte20RxMin(int lte20RxMin) {
        this.lte20RxMin = lte20RxMin;
    }

    public int getLte1TxExp() {
        return calculateExpected(lte1TxMin, lte1TxMax);
    }

    public int getLte1RxExp() {
        return calculateExpected(lte1RxMin, lte1RxMax);
    }

    public int getLte3TxExp() {
        return calculateExpected(lte3TxMin, lte3TxMax);
    }

    public int getLte3RxExp() {
        return calculateExpected(lte3RxMin, lte3RxMax);
    }

    public int getLte7TxExp() {
        return calculateExpected(lte7TxMin, lte7TxMax);
    }

    public int getLte7RxExp() {
        return calculateExpected(lte7RxMin, lte7RxMax);
    }

    public int getLte20TxExp() {
        return calculateExpected(lte20TxMin, lte20TxMax);
    }

    public int getLte20RxExp() {
        return calculateExpected(lte20RxMin, lte20RxMax);
    }

    public int getLte1TxInterval() {
        return calculateInterval(lte1TxMin, lte1TxMax);
    }

    public int getLte1RxInterval() {
        return calculateInterval(lte1RxMin, lte1RxMax);
    }

    public int getLte3TxInterval() {
        return calculateInterval(lte3TxMin, lte3TxMax);
    }

    public int getLte3RxInterval() {
        return calculateInterval(lte3RxMin, lte3RxMax);
    }

    public int getLte7TxInterval() {
        return calculateInterval(lte7TxMin, lte7TxMax);
    }

    public int getLte7RxInterval() {
        return calculateInterval(lte7RxMin, lte7RxMax);
    }

    public int getLte20TxInterval() {
        return calculateInterval(lte20TxMin, lte20TxMax);
    }

    public int getLte20RxInterval() {
        return calculateInterval(lte20RxMin, lte20RxMax);
    }
}
