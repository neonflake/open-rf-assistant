package hu.open.assistant.rf.model.report.limits;

/**
 * Stores limit values for a generic RF test report. The minimum and maximum limits are for WCDMA and GSM bands both for
 * IN and OUT direction. Can calculate the interval and expected value from the min. and max. values.
 */
public abstract class ReportLimits {

    private static final int DEFAULT_WCDMA_TX_MIN = 19;
    private static final int DEFAULT_WCDMA_TX_MAX = 29;
    private static final int DEFAULT_WCDMA_RX_MIN = -81;
    private static final int DEFAULT_WCDMA_RX_MAX = -67;
    private static final int DEFAULT_GSM900_TX_MIN = 28;
    private static final int DEFAULT_GSM900_TX_MAX = 38;
    private static final int DEFAULT_GSM1800_TX_MIN = 25;
    private static final int DEFAULT_GSM1800_TX_MAX = 35;
    private static final int DEFAULT_GSM_RX_MIN = 18;
    private static final int DEFAULT_GSM_RX_MAX = 32;

    protected int wcdma1TxMax = DEFAULT_WCDMA_TX_MAX;
    protected int wcdma1RxMax = DEFAULT_WCDMA_RX_MAX;
    protected int wcdma8TxMax = DEFAULT_WCDMA_TX_MAX;
    protected int wcdma8RxMax = DEFAULT_WCDMA_RX_MAX;
    protected int gsm900TxMax = DEFAULT_GSM900_TX_MAX;
    protected int gsm900RxMax = DEFAULT_GSM_RX_MAX;
    protected int gsm1800TxMax = DEFAULT_GSM1800_TX_MAX;
    protected int gsm1800RxMax = DEFAULT_GSM_RX_MAX;
    protected int wcdma1TxMin = DEFAULT_WCDMA_TX_MIN;
    protected int wcdma1RxMin = DEFAULT_WCDMA_RX_MIN;
    protected int wcdma8TxMin = DEFAULT_WCDMA_TX_MIN;
    protected int wcdma8RxMin = DEFAULT_WCDMA_RX_MIN;
    protected int gsm900TxMin = DEFAULT_GSM900_TX_MIN;
    protected int gsm900RxMin = DEFAULT_GSM_RX_MIN;
    protected int gsm1800TxMin = DEFAULT_GSM1800_TX_MIN;
    protected int gsm1800RxMin = DEFAULT_GSM_RX_MIN;

    public int getWcdma1TxMax() {
        return wcdma1TxMax;
    }

    public void setWcdma1TxMax(int wcdma1TxMax) {
        this.wcdma1TxMax = wcdma1TxMax;
    }

    public int getWcdma1RxMax() {
        return wcdma1RxMax;
    }

    public void setWcdma1RxMax(int wcdma1RxMax) {
        this.wcdma1RxMax = wcdma1RxMax;
    }

    public int getWcdma8TxMax() {
        return wcdma8TxMax;
    }

    public void setWcdma8TxMax(int wcdma8TxMax) {
        this.wcdma8TxMax = wcdma8TxMax;
    }

    public int getWcdma8RxMax() {
        return wcdma8RxMax;
    }

    public void setWcdma8RxMax(int wcdma8RxMax) {
        this.wcdma8RxMax = wcdma8RxMax;
    }

    public int getGsm900TxMax() {
        return gsm900TxMax;
    }

    public void setGsm900TxMax(int gsm900TxMax) {
        this.gsm900TxMax = gsm900TxMax;
    }

    public int getGsm900RxMax() {
        return gsm900RxMax;
    }

    public void setGsm900RxMax(int gsm900RxMax) {
        this.gsm900RxMax = gsm900RxMax;
    }

    public int getGsm1800TxMax() {
        return gsm1800TxMax;
    }

    public void setGsm1800TxMax(int gsm1800TxMax) {
        this.gsm1800TxMax = gsm1800TxMax;
    }

    public int getGsm1800RxMax() {
        return gsm1800RxMax;
    }

    public void setGsm1800RxMax(int gsm1800RxMax) {
        this.gsm1800RxMax = gsm1800RxMax;
    }

    public int getWcdma1TxMin() {
        return wcdma1TxMin;
    }

    public void setWcdma1TxMin(int wcdma1TxMin) {
        this.wcdma1TxMin = wcdma1TxMin;
    }

    public int getWcdma1RxMin() {
        return wcdma1RxMin;
    }

    public void setWcdma1RxMin(int wcdma1RxMin) {
        this.wcdma1RxMin = wcdma1RxMin;
    }

    public int getWcdma8TxMin() {
        return wcdma8TxMin;
    }

    public void setWcdma8TxMin(int wcdma8TxMin) {
        this.wcdma8TxMin = wcdma8TxMin;
    }

    public int getWcdma8RxMin() {
        return wcdma8RxMin;
    }

    public void setWcdma8RxMin(int wcdma8RxMin) {
        this.wcdma8RxMin = wcdma8RxMin;
    }

    public int getGsm900TxMin() {
        return gsm900TxMin;
    }

    public void setGsm900TxMin(int gsm900TxMin) {
        this.gsm900TxMin = gsm900TxMin;
    }

    public int getGsm900RxMin() {
        return gsm900RxMin;
    }

    public void setGsm900RxMin(int gsm900RxMin) {
        this.gsm900RxMin = gsm900RxMin;
    }

    public int getGsm1800TxMin() {
        return gsm1800TxMin;
    }

    public void setGsm1800TxMin(int gsm1800TxMin) {
        this.gsm1800TxMin = gsm1800TxMin;
    }

    public int getGsm1800RxMin() {
        return gsm1800RxMin;
    }

    public void setGsm1800RxMin(int gsm1800RxMin) {
        this.gsm1800RxMin = gsm1800RxMin;
    }

    public int getWcdma1TxExp() {
        return calculateExpected(wcdma1TxMin, wcdma1TxMax);
    }

    public int getWcdma1RxExp() {
        return calculateExpected(wcdma1RxMin, wcdma1RxMax);
    }

    public int getWcdma8TxExp() {
        return calculateExpected(wcdma8TxMin, wcdma8TxMax);
    }

    public int getWcdma8RxExp() {
        return calculateExpected(wcdma8RxMin, wcdma8RxMax);
    }

    public int getGsm900TxExp() {
        return calculateExpected(gsm900TxMin, gsm900TxMax);
    }

    public int getGsm900RxExp() {
        return calculateExpected(gsm900RxMin, gsm900RxMax);
    }

    public int getGsm1800TxExp() {
        return calculateExpected(gsm1800TxMin, gsm1800TxMax);
    }

    public int getGsm1800RxExp() {
        return calculateExpected(gsm1800RxMin, gsm1800RxMax);
    }

    public int getWcdma1TxInterval() {
        return calculateInterval(wcdma1TxMin, wcdma1TxMax);
    }

    public int getWcdma1RxInterval() {
        return calculateInterval(wcdma1RxMin, wcdma1RxMax);
    }

    public int getWcdma8TxInterval() {
        return calculateInterval(wcdma8TxMin, wcdma8TxMax);
    }

    public int getWcdma8RxInterval() {
        return calculateInterval(wcdma8RxMin, wcdma8RxMax);
    }

    public int getGsm900TxInterval() {
        return calculateInterval(gsm900TxMin, gsm900TxMax);
    }

    public int getGsm900RxInterval() {
        return calculateInterval(gsm900RxMin, gsm900RxMax);
    }

    public int getGsm1800TxInterval() {
        return calculateInterval(gsm1800TxMin, gsm1800TxMax);
    }

    public int getGsm1800RxInterval() {
        return calculateInterval(gsm1800RxMin, gsm1800RxMax);
    }

    protected int calculateExpected(int minValue, int maxValue) {
        return minValue + (maxValue - minValue) / 2;
    }

    protected int calculateInterval(int minValue, int maxValue) {
        return Math.abs((maxValue - minValue) / 2);
    }
}
