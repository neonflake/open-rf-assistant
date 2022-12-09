package hu.open.assistant.rf.model.station;

import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.batch.ReportBatch;
import hu.open.assistant.rf.model.report.limits.ReportLimits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A logical representation of a generic RF tester station. It stores the stations attenuation database and the related
 * report batches. Can create a summarised spread information and fail / pass statistics. It helps with report storing
 * during processing stage.
 */
public abstract class Station {
    protected int serial;
    protected double percentage;
    protected int passCount;
    protected int passableCount;
    protected int failCount;
    protected int unitCount;
    protected float combinedTxSpread;
    protected float combinedRxSpread;
    protected List<ReportBatch> reportBatches;
    protected Database database;
    protected ReportLimits limits;
    protected double scriptVersion;
    protected TesterType testerType;

    public Station(int serial, TesterType testerType, ReportLimits limits, double scriptVersion, Database database) {
        this.serial = serial;
        this.testerType = testerType;
        this.database = database;
        this.limits = limits;
        this.scriptVersion = scriptVersion;
        reportBatches = new ArrayList<>();
    }

    public Database getDatabase() {
        return database;
    }

    protected boolean isReportBatchNeeded(Report report) {
        for (ReportBatch batch : reportBatches) {
            if (batch.getName().equals(report.getName())) {
                batch.addReport(report);
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return reportBatches.isEmpty();
    }

    protected boolean isReportValid(Report report, Profile profile) {
        return report.getScriptVersion() >= scriptVersion && report.hasValues() && profile != null;
    }

    public ReportLimits getLimits() {
        return limits;
    }

    public int getSerial() {
        return serial;
    }

    public double getPercentage() {
        return percentage;
    }

    public boolean isCompensated() {
        for (ReportBatch batch : reportBatches) {
            if (!batch.getCompensation().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<ReportBatch> getReportBatches() {
        return reportBatches;
    }

    public void initStation() {
        for (ReportBatch reportBatch : reportBatches) {
            reportBatch.calculateAverageSpreads();
            reportBatch.calculateCombinedSpreads();
            reportBatch.checkCondition();
        }
        calculatePercentage();
        calculateSpread();
    }

    protected void calculateSpread() {
        combinedTxSpread = 0;
        combinedRxSpread = 0;
        int txCount = 0;
        int rxCount = 0;
        for (ReportBatch reportBatch : reportBatches) {
            if ((reportBatch.getPassableCount() + reportBatch.getPassCount()) > 0) {
                if (reportBatch.getCombinedTxSpread() > 0) {
                    combinedTxSpread = combinedTxSpread + reportBatch.getCombinedTxSpread();
                    txCount++;
                }
                if (reportBatch.getCombinedRxSpread() > 0) {
                    combinedRxSpread = combinedRxSpread + reportBatch.getCombinedRxSpread();
                    rxCount++;
                }
            }
        }
        if (txCount > 0) {
            combinedTxSpread = combinedTxSpread / txCount;
        }
        if (rxCount > 0) {
            combinedRxSpread = combinedRxSpread / rxCount;
        }
    }

    protected void calculatePercentage() {
        passCount = 0;
        passableCount = 0;
        failCount = 0;
        unitCount = 0;
        for (ReportBatch reportBatch : reportBatches) {
            passCount = passCount + reportBatch.getPassCount();
            passableCount = passableCount + reportBatch.getPassableCount();
            failCount = failCount + reportBatch.getFailCount();
            unitCount = unitCount + reportBatch.getUnitCount();
        }
        percentage = passableCount;
        percentage = 100 - (percentage / (passCount + passableCount)) * 100;
    }

    protected List<String> getPassableInfo() {
        List<String> text = new ArrayList<>();
        int otherFail = 0;
        int rxFail = 0;
        int txFail = 0;
        int txRxFail = 0;
        if (passCount > 0 || passableCount > 0) {
            text.add("Hiba nélküli / kis eltérések: " + String.format("%.2f", percentage) + "%");
            if (passableCount > 0) {
                for (ReportBatch reportBatch : reportBatches) {
                    for (Report report : reportBatch.getValidUsableReports()) {
                        if (!report.isPassed()) {
                            if (!report.isRxFail() && !report.isTxFail()) {
                                otherFail++;
                            } else {
                                if (report.isTxFail() && report.isRxFail()) {
                                    txRxFail++;
                                } else {
                                    if (report.isTxFail()) {
                                        txFail++;

                                    } else if (report.isRxFail()) {
                                        rxFail++;
                                    }
                                }
                            }
                        }
                    }
                }
                text.add("");
                text.add("Kis eltérést kiváltó hiba:");
                text.add("   - TX és RX: " + txRxFail);
                text.add("   - csak TX: " + txFail);
                text.add("   - csak RX: " + rxFail);
                text.add("   - egyéb: " + otherFail);
            }
        } else {
            text.add("Hiba nélküli / kis eltérések: nincs adat");
        }
        return text;
    }

    protected List<String> getSpreadInfo() {
        List<String> text = new ArrayList<>();
        text.add("Kombinált átlagszórás (2G/3G): ");
        if (combinedTxSpread + combinedRxSpread > 0) {
            text.add("   - aktuális: " + String.format("%.2f", (combinedTxSpread + combinedRxSpread) / 2) + " (TX: " + String.format("%.2f", combinedTxSpread) + " / RX: " + String.format("%.2f", combinedRxSpread) + ")");
        }
        return text;
    }

    protected List<String> getGenericInfo() {
        List<String> text = new ArrayList<>();
        text.add("Aktuális feldolgozott riportok: " + (passCount + passableCount + failCount));
        text.add("   - hiba nélküli: " + passCount);
        text.add("   - hibás, kis eltérésekkel: " + passableCount);
        text.add("   - hibás, nagy eltérésekkel: " + failCount);
        return text;
    }

    public List<Report> getValidNonPassedUsableReports() {
        List<Report> reports = new ArrayList<>();
        for (ReportBatch batch : reportBatches) {
            reports.addAll(batch.getValidNonPassedUsableReports());
        }
        Collections.sort(reports);
        return reports;
    }

    public List<String> getInfo() {
        List<String> text = new ArrayList<>();
        text.add(testerType.getFullName() + " állomás száma: " + serial);
        text.add("");
        text.addAll(getGenericInfo());
        text.add("");
        text.addAll(getSpreadInfo());
        text.add("");
        text.addAll(getPassableInfo());
        return text;
    }

    public abstract void addReport(Report report);
}
