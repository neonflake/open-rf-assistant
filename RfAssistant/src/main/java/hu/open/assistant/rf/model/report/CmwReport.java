package hu.open.assistant.rf.model.report;

import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.rf.model.report.values.CmwReportValues;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;

/**
 * A logical representation of a CMW type RF test report. It extends the generic variant with additional file
 * information and to store and check values from multiple LTE bands.
 */
public class CmwReport extends Report {

    public CmwReport(String filename, String folder, LocalDateTime dateTime, String type, String manufacturer, int serial, String position, double scriptVersion, long imei, boolean passed, CmwReportValues values, CmwReportLimits limits) {
        super(filename, folder, dateTime, type, manufacturer, serial, TesterType.CMW, position, scriptVersion, imei, passed, values, limits);
    }

    @Override
    public boolean hasValues() {
        return hasWcdma1() || hasWcdma8() || hasGsm() || hasLte();
    }

    public String getRawFilename() {
        String rawFilename = filename.split("\\.")[0];
        if (passed) {
            rawFilename = rawFilename + ".rsmrp";
        } else {
            rawFilename = rawFilename + ".rsmrf";
        }
        return rawFilename;
    }

    public boolean hasLte() {
        return NumberHelper.arrayHasValues(((CmwReportValues) values).getLte1TxValues());
    }

    public double[] getLte1TxValues() {
        return ((CmwReportValues) values).getLte1TxValues();
    }

    public double[] getLte1RxValues() {
        return ((CmwReportValues) values).getLte1RxValues();
    }

    public double[] getLte3TxValues() {
        return ((CmwReportValues) values).getLte3TxValues();
    }

    public double[] getLte3RxValues() {
        return ((CmwReportValues) values).getLte3RxValues();
    }

    public double[] getLte7TxValues() {
        return ((CmwReportValues) values).getLte7TxValues();
    }

    public double[] getLte7RxValues() {
        return ((CmwReportValues) values).getLte7RxValues();
    }

    public double[] getLte20TxValues() {
        return ((CmwReportValues) values).getLte20TxValues();
    }

    public double[] getLte20RxValues() {
        return ((CmwReportValues) values).getLte20RxValues();
    }

    public String getShortFileName() {
        String[] nameParts = filename.split("_");
        return nameParts[0] + "_" + nameParts[1] + "_..._" + nameParts[4];
    }

    @Override
    public String getExtendedFilename() {
        return "(" + getTopFolder() + ") - " + getShortFileName();
    }

    @Override
    public void checkValues(int passableLimit) {
        if (hasValues() || hasLte()) {
            super.checkValues(passableLimit);
            if (hasLte()) {
                checkLteBounds(0);
                passable = checkLteBounds(passableLimit);
            }
        } else {
            passable = false;
        }
    }

    protected boolean checkLteBounds(int limit) {
        boolean inBounds;
        inBounds = checkBound(((CmwReportValues) values).getLte1TxValues(), ((CmwReportLimits) limits).getLte1TxMax(), ((CmwReportLimits) limits).getLte1TxMin(), true, limit, true);
        inBounds = checkBound(((CmwReportValues) values).getLte1RxValues(), ((CmwReportLimits) limits).getLte1RxMax(), ((CmwReportLimits) limits).getLte1RxMin(), inBounds, limit, false);
        inBounds = checkBound(((CmwReportValues) values).getLte3TxValues(), ((CmwReportLimits) limits).getLte3TxMax(), ((CmwReportLimits) limits).getLte3TxMin(), inBounds, limit, true);
        inBounds = checkBound(((CmwReportValues) values).getLte3RxValues(), ((CmwReportLimits) limits).getLte3RxMax(), ((CmwReportLimits) limits).getLte3RxMin(), inBounds, limit, false);
        inBounds = checkBound(((CmwReportValues) values).getLte7TxValues(), ((CmwReportLimits) limits).getLte7TxMax(), ((CmwReportLimits) limits).getLte7TxMin(), inBounds, limit, true);
        inBounds = checkBound(((CmwReportValues) values).getLte7RxValues(), ((CmwReportLimits) limits).getLte7RxMax(), ((CmwReportLimits) limits).getLte7RxMin(), inBounds, limit, false);
        inBounds = checkBound(((CmwReportValues) values).getLte20TxValues(), ((CmwReportLimits) limits).getLte20TxMax(), ((CmwReportLimits) limits).getLte20TxMin(), inBounds, limit, true);
        inBounds = checkBound(((CmwReportValues) values).getLte20RxValues(), ((CmwReportLimits) limits).getLte20RxMax(), ((CmwReportLimits) limits).getLte20RxMin(), inBounds, limit, false);
        return inBounds;
    }
}
