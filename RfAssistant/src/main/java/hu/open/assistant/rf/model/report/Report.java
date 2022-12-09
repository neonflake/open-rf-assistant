package hu.open.assistant.rf.model.report;

import hu.open.assistant.rf.model.report.limits.ReportLimits;
import hu.open.assistant.rf.model.report.values.ReportValues;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;

/**
 * A logical representation of a generic RF test report. Contains the RF test results for a given phone type, the script
 * that was executed (and when), as well as some additional file information. If the test didn't pass, it can determine
 * the failure type and the possibility that the report can be used for further processing (is it within bound with
 * the given limits).
 */
public abstract class Report implements Comparable<Report> {
    protected LocalDateTime dateTime;
    protected String filename;
    protected String folder;
    protected long imei;
    protected int serial;
    protected boolean passed;
    protected boolean passable;
    protected boolean txFail;
    protected boolean rxFail;
    protected int mark;
    protected String manufacturer;
    protected String type;
    protected String position;
    protected double scriptVersion;
    protected ReportLimits limits;
    protected ReportValues values;
    protected TesterType testerType;

    public Report(String filename, String folder, LocalDateTime dateTime, String type, String manufacturer, int serial, TesterType testerType, String position, double scriptVersion, long imei, boolean passed, ReportValues values, ReportLimits limits) {
        this.filename = filename;
        this.folder = folder;
        this.dateTime = dateTime;
        this.type = type;
        this.manufacturer = manufacturer;
        this.serial = serial;
        this.testerType = testerType;
        this.scriptVersion = scriptVersion;
        this.imei = imei;
        this.passed = passed;
        this.limits = limits;
        this.position = position;
        this.values = values;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
        // 0 - invalid, 1 - old, 2 - fresh
    }

    public boolean hasValues() {
        return hasWcdma1() || hasWcdma8() || hasGsm();
    }

    public String getPosition() {
        return position;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getName() {
        return manufacturer + " " + type;
    }

    public double getScriptVersion() {
        return scriptVersion;
    }

    public boolean hasGsm() {
        return NumberHelper.arrayHasValues(values.getGsm900TxValues());
    }

    public boolean hasWcdma1() {
        return NumberHelper.arrayHasValues(values.getWcdma1TxValues());
    }

    public boolean hasWcdma8() {
        return NumberHelper.arrayHasValues(values.getWcdma8TxValues());
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getSerial() {
        return serial;
    }

    public boolean isPassed() {
        return passed;
    }

    public boolean isPassable() {
        return passable;
    }

    public boolean isTxFail() {
        return txFail;
    }

    public boolean isRxFail() {
        return rxFail;
    }

    public String getFilename() {
        return filename;
    }

    public String getFolder() {
        return folder;
    }

    protected String getTopFolder() {
        String[] folderParts = folder.split("\\\\");
        return folderParts[folderParts.length - 1];
    }

    public String getExtendedFilename() {
        return "(" + getTopFolder() + ") - " + filename;
    }

    public long getImei() {
        return imei;
    }

    public double[] getWcdma1TxValues() {
        return values.getWcdma1TxValues();
    }

    public double[] getWcdma1RxValues() {
        return values.getWcdma1RxValues();
    }

    public double[] getWcdma8TxValues() {
        return values.getWcdma8TxValues();
    }

    public double[] getWcdma8RxValues() {
        return values.getWcdma8RxValues();
    }

    public double[] getGsm900TxValues() {
        return values.getGsm900TxValues();
    }

    public double[] getGsm900RxValues() {
        return values.getGsm900RxValues();
    }

    public double[] getGsm1800TxValues() {
        return values.getGsm1800TxValues();
    }

    public double[] getGSM1800RxValues() {
        return values.getGsm1800RxValues();
    }

    public void checkValues(int passableLimit) {
        if (hasValues()) {
            txFail = false;
            rxFail = false;
            passable = true;
            if (!passed) {
                checkBounds(0);
                passable = checkBounds(passableLimit);
            }
        } else {
            passable = false;
        }
    }

    protected boolean checkBound(double[] values, int max, int min, boolean inBounds, int limit, boolean tx) {
        for (double value : values) {
            if (value > max + limit) {
                inBounds = false;
                if (limit == 0) {
                    if (tx) {
                        txFail = true;
                    } else {
                        rxFail = true;
                    }
                }
            }
            if (value < min - limit) {
                inBounds = false;
                if (limit == 0) {
                    if (tx) {
                        txFail = true;
                    } else {
                        rxFail = true;
                    }
                }
            }
        }
        return inBounds;
    }

    protected boolean checkBounds(int limit) {
        boolean inBounds = true;
        if (hasWcdma1()) {
            inBounds = checkBound(values.getWcdma1TxValues(), limits.getWcdma1TxMax(), limits.getWcdma1TxMin(), true, limit, true);
            inBounds = checkBound(values.getWcdma1RxValues(), limits.getWcdma1RxMax(), limits.getWcdma1RxMin(), inBounds, limit, false);
            if (hasWcdma8()) {
                inBounds = checkBound(values.getWcdma8TxValues(), limits.getWcdma8TxMax(), limits.getWcdma8TxMin(), inBounds, limit, true);
                inBounds = checkBound(values.getWcdma8RxValues(), limits.getWcdma8RxMax(), limits.getWcdma8RxMin(), inBounds, limit, false);
            }
        }
        if (hasGsm()) {
            inBounds = checkBound(values.getGsm900TxValues(), limits.getGsm900TxMax(), limits.getGsm900TxMin(), inBounds, limit, true);
            inBounds = checkBound(values.getGsm900RxValues(), limits.getGsm900RxMax(), limits.getGsm900RxMin(), inBounds, limit, false);
            inBounds = checkBound(values.getGsm1800TxValues(), limits.getGsm1800TxMax(), limits.getGsm1800TxMin(), inBounds, limit, true);
            inBounds = checkBound(values.getGsm1800RxValues(), limits.getGsm1800RxMax(), limits.getGsm1800RxMin(), inBounds, limit, false);
        }
        return inBounds;
    }

    @Override
    public int compareTo(Report other) {
        if (this.mark > other.mark) {
            return 1;
        } else if (this.mark < other.mark) {
            return -1;
        } else {
            return (dateTime.compareTo(other.getDateTime()));
        }
    }
}
