package hu.open.assistant.rf.model.report.batch;

import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.limits.ReportLimits;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and holds together reports from the same RF profile at a given RF station. It can calculate spread for the
 * affected RF profile and provide information about pass rate and which reports are usable for calculation. Spread
 * calculation is available for separate bands and a combined one (both TX and RX). The spread can be calculated for
 * the actual and old reports as well with comparison. Based on the spread the batch can determine its condition. It
 * helps with marking reports for text display within a graphical list. Multiple compare methods are available. Beside
 * the actual RF profile, it stores a compensation which can be modified using an interactive graph.
 */
public abstract class ReportBatch implements Comparable<ReportBatch> {
    protected int unitCount;
    protected int passCount;
    protected int passableCount;
    protected int failCount;
    protected int usableWcdma1Count;
    protected int usableGsmCount;
    protected int usableWcdma8Count;
    protected int oldUsableCount;
    protected int oldUsableWcdma1Count;
    protected int oldUsableGsmCount;
    protected int oldUsableWcdma8Count;
    protected String type;
    protected String manufacturer;
    protected int serial;
    protected TesterType testerType;
    protected List<Report> reports;
    protected float[] wcdma1TxSpread = new float[3];
    protected float[] wcdma1RxSpread = new float[3];
    protected float[] wcdma8TxSpread = new float[3];
    protected float[] wcdma8RxSpread = new float[3];
    protected float[] gsm900TxSpread = new float[3];
    protected float[] gsm900RxSpread = new float[3];
    protected float[] gsm1800TxSpread = new float[3];
    protected float[] gsm1800RxSpread = new float[3];
    protected float[] oldWcdma1TxSpread = new float[3];
    protected float[] oldWcdma1RxSpread = new float[3];
    protected float[] oldWcdma8TxSpread = new float[3];
    protected float[] oldWcdma8RxSpread = new float[3];
    protected float[] oldGsm900TxSpread = new float[3];
    protected float[] oldGsm900RxSpread = new float[3];
    protected float[] oldGsm1800TxSpread = new float[3];
    protected float[] oldGsm1800RxSpread = new float[3];
    protected float oldCombinedTxSpread;
    protected float oldCombinedRxSpread;
    protected float combinedTxSpread;
    protected float combinedRxSpread;
    protected ReportLimits limits;
    protected int condition;
    protected Compensation compensation;
    protected String sortMethod;
    protected Profile profile;
    protected LocalDateTime lastDate;
    protected LocalDateTime firstDate;
    protected String[] channels;
    protected List<Long> imeis;

    public ReportBatch(String type, String manufacturer, ReportLimits limits, int serial, TesterType testerType, Profile profile) {
        this.profile = profile;
        this.manufacturer = manufacturer;
        this.type = type;
        this.limits = limits;
        this.serial = serial;
        this.testerType = testerType;
        reports = new ArrayList<>();
        sortMethod = "condition";
        if (profile.getLogBatch() == null) {
            lastDate = LocalDateTime.now().minusYears(1);
            firstDate = LocalDateTime.now().minusYears(1);
        } else {
            lastDate = profile.getLogBatch().getLastModificationDate();
            if (profile.getLogBatch().getSecondModificationDate() == null) {
                firstDate = LocalDateTime.now().minusYears(1);
            } else {
                firstDate = profile.getLogBatch().getSecondModificationDate();
            }
        }
        imeis = new ArrayList<>();
        usableWcdma1Count = 0;
        usableGsmCount = 0;
        usableWcdma8Count = 0;
        oldUsableWcdma1Count = 0;
        oldUsableGsmCount = 0;
        oldUsableWcdma8Count = 0;
        unitCount = 0;
        passCount = 0;
        passableCount = 0;
        failCount = 0;
        oldUsableCount = 0;
        initChannels();
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public LocalDateTime getLastDate() {
        return lastDate;
    }

    public List<String> getInfo() {
        List<String> text = new ArrayList<>();
        text.add("CMU 200 riport csokor");
        text.add("");
        text.addAll(getGenericInfo());
        if (combinedTxSpread + combinedRxSpread > 0 || oldCombinedTxSpread + oldCombinedRxSpread > 0) {
            text.add("");
            text.addAll(getSpreadInfo());
        }
        if (combinedTxSpread + combinedRxSpread > 0) {
            text.add("");
            text.addAll(getReportInfo());
            text.add("");
            text.add("Aktuális átlagszórások csatornánként:");
            text.addAll(getWcdmaSpread());
            text.addAll(getGsmSpread());
        }

        if (oldCombinedTxSpread + oldCombinedRxSpread > 0) {
            text.add("");
            text.add("Előző átlagszórások csatornánként:");
            text.addAll(getOldWcdmaSpread());
            text.addAll(getOldGsmSpread());
        }
        if (combinedTxSpread + combinedRxSpread > 0 && oldCombinedTxSpread + oldCombinedRxSpread > 0) {
            text.add("");
            text.add("Változás mértéke csatornánként:");
            text.addAll(getWcdmaSpreadChange());
            text.addAll(getGsmSpreadChange());
        }
        return text;
    }

    public ReportLimits getLimits() {
        return limits;
    }

    public float getCombinedTxSpread() {
        return combinedTxSpread;
    }

    public float getCombinedRxSpread() {
        return combinedRxSpread;
    }

    public int getOldUsableWcdma1Count() {
        return oldUsableWcdma1Count;
    }

    public int getOldUsableGsmCount() {
        return oldUsableGsmCount;
    }

    public int getOldUsableWcdma8Count() {
        return oldUsableWcdma8Count;
    }

    public int getOldUsableCount() {
        return oldUsableCount;
    }

    public List<Report> getReports() {
        return reports;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getName() {
        return manufacturer + " " + type;
    }

    public String getType() {
        return type;
    }

    public int getSerial() {
        return serial;
    }

    public int getCondition() {
        return condition;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getPassableCount() {
        return passableCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getUsableWcdma1Count() {
        return usableWcdma1Count;
    }

    public int getUsableGsmCount() {
        return usableGsmCount;
    }

    public int getUsableWcdma8Count() {
        return usableWcdma8Count;
    }

    public int getUsableCount() {
        return passableCount + passCount;
    }

    public void setSortMethod(String sortMethod) {
        this.sortMethod = sortMethod;
    }

    public void transferCompensation(Compensation compensation) {
        this.compensation.copyCompensation(compensation);
    }

    public Compensation getCompensation() {
        return compensation;
    }

    public void addReport(Report report) {
        reports.add(report);
        if (report.getDateTime().isAfter(lastDate)) {
            if (report.isPassable()) {
                if (report.isPassed()) {
                    passCount++;
                } else {
                    passableCount++;
                }
                if (report.hasWcdma1()) {
                    usableWcdma1Count++;
                }
                if (report.hasWcdma8()) {
                    usableWcdma8Count++;
                }
                if (report.hasGsm()) {
                    usableGsmCount++;
                }
                long imei = report.getImei();
                if (!imeis.contains(imei)) {
                    imeis.add(imei);
                    unitCount++;
                }
            } else {
                failCount++;
            }
        } else if (report.getDateTime().isAfter(firstDate) && report.getDateTime().isBefore(lastDate)) {
            if (report.isPassable()) {
                oldUsableCount++;
                if (report.hasWcdma1()) {
                    oldUsableWcdma1Count++;
                }
                if (report.hasWcdma8()) {
                    oldUsableWcdma8Count++;
                }
                if (report.hasGsm()) {
                    oldUsableGsmCount++;
                }
            }
        }
    }

    public List<Report> getValidNonPassedUsableReports() {
        List<Report> nonPassedReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.isPassable() && !report.isPassed()) {
                if (report.getDateTime().isAfter(lastDate)) {
                    nonPassedReports.add(report);
                }
            }
        }
        return nonPassedReports;
    }

    public List<Report> getValidUsableReports() {
        List<Report> usableReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.isPassable()) {
                if (report.getDateTime().isAfter(lastDate)) {
                    usableReports.add(report);
                }
            }
        }
        return usableReports;
    }

    public List<Report> getAllUsableReports() {
        List<Report> usableReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.isPassable()) {
                usableReports.add(report);
            }
        }
        Collections.sort(usableReports);
        return usableReports;
    }

    protected List<String> getWcdmaSpread() {
        return getWcdmaSpreadText(usableWcdma1Count, wcdma1TxSpread, wcdma1RxSpread, usableWcdma8Count, wcdma8TxSpread, wcdma8TxSpread);
    }

    protected List<String> getOldWcdmaSpread() {
        return getWcdmaSpreadText(oldUsableWcdma1Count, oldWcdma1TxSpread, oldWcdma1RxSpread, oldUsableWcdma8Count, oldWcdma8TxSpread, oldWcdma8RxSpread);
    }

    protected List<String> getGsmSpread() {
        return getGsmSpreadText(usableGsmCount, gsm900TxSpread, gsm900RxSpread, gsm1800TxSpread, gsm1800RxSpread);
    }

    protected List<String> getOldGsmSpread() {
        return getGsmSpreadText(oldUsableGsmCount, oldGsm900TxSpread, oldGsm900RxSpread, oldGsm1800TxSpread, oldGsm1800RxSpread);
    }

    private List<String> getGsmSpreadText(int usableGsmCount, float[] gsm900TxSpread, float[] gsm900RxSpread, float[] gsm1800TxSpread, float[] gsm1800RxSpread) {
        List<String> text = new ArrayList<>();
        if (usableGsmCount > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 12] + String.format("%.2f", gsm900TxSpread[i]) + markCondition(gsm900TxSpread[i], limits.getGsm900TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 15] + String.format("%.2f", gsm900RxSpread[i]) + markCondition(gsm900RxSpread[i], limits.getGsm900RxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 18] + String.format("%.2f", gsm1800TxSpread[i]) + markCondition(gsm1800TxSpread[i], limits.getGsm1800TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 21] + String.format("%.2f", gsm1800RxSpread[i]) + markCondition(gsm1800RxSpread[i], limits.getGsm1800RxInterval()));
            }
        }
        return text;
    }

    private List<String> getWcdmaSpreadText(int oldUsableWcdma1Count, float[] oldWcdma1TxSpread, float[] oldWcdma1RxSpread, int oldUsableWcdma8Count, float[] oldWcdma8TxSpread, float[] oldWcdma8RxSpread) {
        List<String> text = new ArrayList<>();
        if (oldUsableWcdma1Count > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i] + String.format("%.2f", oldWcdma1TxSpread[i]) + markCondition(oldWcdma1TxSpread[i], limits.getWcdma1TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 3] + String.format("%.2f", oldWcdma1RxSpread[i]) + markCondition(oldWcdma1RxSpread[i], limits.getWcdma1RxInterval()));
            }
        }
        if (oldUsableWcdma8Count > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 6] + String.format("%.2f", oldWcdma8TxSpread[i]) + markCondition(oldWcdma8TxSpread[i], limits.getWcdma8TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 9] + String.format("%.2f", oldWcdma8RxSpread[i]) + markCondition(oldWcdma8RxSpread[i], limits.getWcdma8RxInterval()));
            }
        }
        return text;
    }

    protected List<String> getWcdmaSpreadChange() {
        List<String> text = new ArrayList<>();
        if (oldUsableWcdma1Count > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i] + showChange(wcdma1TxSpread[i], oldWcdma1TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 3] + showChange(wcdma1RxSpread[i], oldWcdma1RxSpread[i]));
            }
        }
        if (oldUsableWcdma8Count > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 6] + showChange(wcdma8TxSpread[i], oldWcdma8TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 9] + showChange(wcdma8RxSpread[i], oldWcdma8RxSpread[i]));
            }
        }
        return text;
    }

    protected List<String> getGsmSpreadChange() {
        List<String> text = new ArrayList<>();
        if (oldUsableGsmCount > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 12] + showChange(gsm900TxSpread[i], oldGsm900TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 15] + showChange(gsm900RxSpread[i], oldGsm900RxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 18] + showChange(gsm1800TxSpread[i], oldGsm1800TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 21] + showChange(gsm1800RxSpread[i], oldGsm1800RxSpread[i]));
            }
        }
        return text;
    }

    public List<Report> getMarkedReports() {
        List<Report> markedReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.isPassable()) {
                if (report.getDateTime().isAfter(lastDate)) {
                    report.setMark(2);
                    markedReports.add(report);
                } else if (report.getDateTime().isAfter(firstDate) && report.getDateTime().isBefore(lastDate)) {
                    report.setMark(1);
                    markedReports.add(report);
                }
            }
        }
        Collections.sort(markedReports);
        return markedReports;
    }

    public void calculateCombinedSpreads() {
        combinedTxSpread = calculateCombinedSpread(usableWcdma1Count, usableWcdma8Count, usableGsmCount,
                wcdma1TxSpread, wcdma8TxSpread, gsm900TxSpread, gsm1800TxSpread);
        combinedRxSpread = calculateCombinedSpread(usableWcdma1Count, usableWcdma8Count, usableGsmCount,
                wcdma1RxSpread, wcdma8RxSpread, gsm900RxSpread, gsm1800RxSpread);
        oldCombinedTxSpread = calculateCombinedSpread(oldUsableWcdma1Count, oldUsableWcdma8Count, oldUsableGsmCount,
                oldWcdma1TxSpread, oldWcdma8TxSpread, oldGsm900TxSpread, oldGsm1800TxSpread);
        oldCombinedRxSpread = calculateCombinedSpread(oldUsableWcdma1Count, oldUsableWcdma8Count, oldUsableGsmCount,
                oldWcdma1RxSpread, oldWcdma8RxSpread, oldGsm900RxSpread, oldGsm1800RxSpread);
    }

    protected float calculateCombinedSpread(int usableWcdma1Count, int usableWcdma8Count, int usableGsmCount,
                                            float[] wcdma1Spread, float[] wcdma8Spread, float[] gsm900Spread, float[] gsm1800Spread) {
        float value = 0;
        int divider = 0;
        if (usableWcdma1Count > 0) {
            divider += 3;
            for (int i = 0; i < 3; i++) {
                value += wcdma1Spread[i];
            }
        }
        if (usableWcdma8Count > 0) {
            divider += 3;
            for (int i = 0; i < 3; i++) {
                value += wcdma8Spread[i];
            }
        }
        if (usableGsmCount > 0) {
            divider += 6;
            for (int i = 0; i < 3; i++) {
                value += gsm900Spread[i];
                value += gsm1800Spread[i];
            }
        }
        if (divider > 0) {
            value = value / divider;
        }
        return value;
    }

    protected List<Report> getValidReports() {
        List<Report> validReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.isPassable() && report.getDateTime().isAfter(lastDate)) {
                validReports.add(report);
            }
        }
        return validReports;
    }

    protected List<Report> getOldReports() {
        List<Report> oldReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.isPassable() && report.getDateTime().isAfter(firstDate) && report.getDateTime().isBefore(lastDate)) {
                oldReports.add(report);
            }
        }
        return oldReports;
    }

    public void calculateAverageSpreads() {
        calculateAverageSpread(getValidReports(), usableWcdma1Count, usableWcdma8Count, usableGsmCount,
                wcdma1TxSpread, wcdma1RxSpread, wcdma8TxSpread, wcdma8RxSpread,
                gsm900TxSpread, gsm900RxSpread, gsm1800TxSpread, gsm1800RxSpread);
        calculateAverageSpread(getOldReports(), oldUsableWcdma1Count, oldUsableWcdma8Count, oldUsableGsmCount,
                oldWcdma1TxSpread, oldWcdma1RxSpread, oldWcdma8TxSpread, oldWcdma8RxSpread,
                oldGsm900TxSpread, oldGsm900RxSpread, oldGsm1800TxSpread, oldGsm1800RxSpread);
    }

    protected void calculateAverageSpread(List<Report> reports, int usableWcdma1Count, int usableWcdma8Count, int usableGsmCount,
                                          float[] wcdma1TxSpread, float[] wcdma1RxSpread, float[] wcdma8TxSpread, float[] wcdma8RxSpread,
                                          float[] gsm900TxSpread, float[] gsm900RxSpread, float[] gsm1800TxSpread, float[] gsm1800RxSpread) {
        for (Report report : reports) {
            if (report.hasWcdma1()) {
                for (int i = 0; i < 3; i++) {
                    wcdma1TxSpread[i] += Math.abs(report.getWcdma1TxValues()[i] - limits.getWcdma1TxExp());
                    wcdma1RxSpread[i] += Math.abs(report.getWcdma1RxValues()[i] - limits.getWcdma1RxExp());
                }
                if (report.hasWcdma8()) {
                    for (int i = 0; i < 3; i++) {
                        wcdma8TxSpread[i] += Math.abs(report.getWcdma8TxValues()[i] - limits.getWcdma8TxExp());
                        wcdma8RxSpread[i] += Math.abs(report.getWcdma8RxValues()[i] - limits.getWcdma8RxExp());
                    }
                }
            }
            if (report.hasGsm()) {
                for (int i = 0; i < 3; i++) {
                    gsm900TxSpread[i] += Math.abs(report.getGsm900TxValues()[i] - limits.getGsm900TxExp());
                    gsm900RxSpread[i] += Math.abs(report.getGsm900RxValues()[i] - limits.getGsm900RxExp());
                    gsm1800TxSpread[i] += Math.abs(report.getGsm1800TxValues()[i] - limits.getGsm1800TxExp());
                    gsm1800RxSpread[i] += Math.abs(report.getGSM1800RxValues()[i] - limits.getGsm1800RxExp());
                }
            }
        }
        if (usableWcdma1Count > 0) {
            for (int i = 0; i < 3; i++) {
                wcdma1TxSpread[i] = wcdma1TxSpread[i] / usableWcdma1Count;
                wcdma1RxSpread[i] = wcdma1RxSpread[i] / usableWcdma1Count;
            }
            if (usableWcdma8Count > 0) {
                for (int i = 0; i < 3; i++) {
                    wcdma8TxSpread[i] = wcdma8TxSpread[i] / usableWcdma8Count;
                    wcdma8RxSpread[i] = wcdma8RxSpread[i] / usableWcdma8Count;
                }
            }
        }
        if (usableGsmCount > 0) {
            for (int i = 0; i < 3; i++) {
                gsm900TxSpread[i] = gsm900TxSpread[i] / usableGsmCount;
                gsm900RxSpread[i] = gsm900RxSpread[i] / usableGsmCount;
                gsm1800TxSpread[i] = gsm1800TxSpread[i] / usableGsmCount;
                gsm1800RxSpread[i] = gsm1800RxSpread[i] / usableGsmCount;
            }
        }
    }

    protected void initChannels() {
        channels = new String[24];
        channels[0] = ("   WCDMA B1 - TX Low: ");
        channels[1] = ("   WCDMA B1 - TX Mid: ");
        channels[2] = ("   WCDMA B1 - TX High: ");
        channels[3] = ("   WCDMA B1 - RX Low: ");
        channels[4] = ("   WCDMA B1 - RX Mid: ");
        channels[5] = ("   WCDMA B1 - RX High: ");
        channels[6] = ("   WCDMA B8 - TX Low: ");
        channels[7] = ("   WCDMA B8 - TX Mid: ");
        channels[8] = ("   WCDMA B8 - TX High: ");
        channels[9] = ("   WCDMA B8 - RX Low: ");
        channels[10] = ("   WCDMA B8 - RX Mid: ");
        channels[11] = ("   WCDMA B8 - RX High: ");
        channels[12] = ("   GSM 900 - TX Low: ");
        channels[13] = ("   GSM 900 - TX Mid: ");
        channels[14] = ("   GSM 900 - TX High: ");
        channels[15] = ("   GSM 900 - RX Low: ");
        channels[16] = ("   GSM 900 - RX Mid: ");
        channels[17] = ("   GSM 900 - RX High: ");
        channels[18] = ("   GSM 1800 - TX Low: ");
        channels[19] = ("   GSM 1800 - TX Mid: ");
        channels[20] = ("   GSM 1800 - TX High: ");
        channels[21] = ("   GSM 1800 - RX Low: ");
        channels[22] = ("   GSM 1800 - RX Mid: ");
        channels[23] = ("   GSM 1800 - RX High: ");
    }

    protected List<String> getGenericInfo() {
        List<String> text = new ArrayList<>();
        text.add("Állomás száma: " + serial);
        text.add("Típus: " + manufacturer + " " + type);
        text.add("");
        text.add("Profil besorolása: " + checkProfileCondition());
        text.add("Utolsó kompenzálások:");
        if (profile.getLogBatch() != null) {
            text.add("   - " + DateHelper.localDateTimeToTextDateTime(profile.getLogBatch().getLastModificationDate()));
            if (profile.getLogBatch().getSecondModificationDate() != null) {
                text.add("   - " + DateHelper.localDateTimeToTextDateTime(profile.getLogBatch().getSecondModificationDate()));
            }
        } else {
            text.add("   nem történt módosítás");
        }
        return text;
    }

    protected List<String> getReportInfo() {
        List<String> text = new ArrayList<>();
        text.add("Aktuális feldolgozott riportok: " + (passCount + passableCount + failCount));
        text.add("   - hiba nélküli: " + passCount);
        text.add("   - hibás, kis eltérésekkel: " + passableCount);
        text.add("   - hibás, nagy eltérésekkel: " + failCount);
        if (getUsableCount() > 0) {
            text.add("   (átlaghoz felhasznált riport: " + (getUsableCount()) + ")");
            text.add("   (átlaghoz felhasznált készülék: " + unitCount + ")");
        }
        return text;
    }

    protected List<String> getSpreadInfo() {
        List<String> text = new ArrayList<>();
        text.add("Kombinált átlagszórás (2G/3G): ");
        if (combinedTxSpread + combinedRxSpread > 0) {
            text.add("   - aktuális: " + String.format("%.2f", (combinedTxSpread + combinedRxSpread) / 2) + " (TX: " + String.format("%.2f", combinedTxSpread) + ", RX: " + String.format("%.2f", combinedRxSpread) + ")");
        }
        if (oldCombinedTxSpread + oldCombinedRxSpread > 0) {
            text.add("   - előző: " + String.format("%.2f", (oldCombinedTxSpread + oldCombinedRxSpread) / 2) + " (TX: " + String.format("%.2f", oldCombinedTxSpread) + ", RX: " + String.format("%.2f", oldCombinedRxSpread) + ")");
        }
        if ((combinedTxSpread + combinedRxSpread > 0 && oldCombinedTxSpread + oldCombinedRxSpread > 0)) {
            text.add("   - változás: " + showChange((combinedTxSpread + combinedRxSpread) / 2, (oldCombinedTxSpread + oldCombinedRxSpread) / 2) + " (TX: " + showChange(combinedTxSpread, oldCombinedTxSpread) + ", RX: " + showChange(combinedRxSpread, oldCombinedRxSpread) + ")");
        }
        if (combinedTxSpread + combinedRxSpread == 0 && oldCombinedTxSpread + oldCombinedRxSpread == 0) {
            text.add("   nincs adat");
        }
        return text;
    }

    protected String showChange(float actual, float old) {
        String text;
        if (actual - old < 0) {
            text = String.format("%.2f", actual - old);
        } else if (actual - old > 0) {
            text = "+" + String.format("%.2f", actual - old);
        } else {
            text = "0,00";
        }
        return text;
    }

    protected String checkProfileCondition() {
        String profileCondition = "";
        if (profile != null) {
            if (profile.getCondition() == 1) {
                profileCondition = "kiváló";
            } else if (profile.getCondition() == 2) {
                profileCondition = "jó";
            } else if (profile.getCondition() == 3) {
                profileCondition = "problémás";
            }
        } else {
            profileCondition = "Nincs ilyen profil!";
        }
        return profileCondition;
    }

    public void checkCondition() {
        condition = 1;
        if (usableWcdma1Count > 0 || usableGsmCount > 0) {
            for (int i = 0; i < 3; i++) {
                if (usableWcdma1Count > 0) {
                    condition = checkPartCondition(wcdma1TxSpread[i], condition, limits.getWcdma1TxInterval());
                    condition = checkPartCondition(wcdma1RxSpread[i], condition, limits.getWcdma1RxInterval());
                    if (usableWcdma8Count > 0) {
                        condition = checkPartCondition(wcdma8TxSpread[i], condition, limits.getWcdma8TxInterval());
                        condition = checkPartCondition(wcdma8RxSpread[i], condition, limits.getWcdma8RxInterval());
                    }
                }
                if (usableGsmCount > 0) {
                    condition = checkPartCondition(gsm900TxSpread[i], condition, limits.getGsm900TxInterval());
                    condition = checkPartCondition(gsm900RxSpread[i], condition, limits.getGsm900RxInterval());
                    condition = checkPartCondition(gsm1800TxSpread[i], condition, limits.getGsm1800TxInterval());
                    condition = checkPartCondition(gsm1800RxSpread[i], condition, limits.getGsm1800RxInterval());
                }
            }
        } else {
            condition = 0;
        }
    }

    protected int checkPartCondition(float spread, int condition, int interval) {
        int border = 0;
        if (interval == 5) {
            border = 2;
        } else if (interval == 7) {
            border = 3;
        }
        int partCondition = 1;
        if (spread > border) {
            partCondition = 2;
        }
        if (spread > border * 2) {
            partCondition = 3;
        }
        if (partCondition > condition) {
            condition = partCondition;
        }
        return condition;
    }

    protected String markCondition(float spread, int interval) {
        String marking = "";
        int outCondition = checkPartCondition(spread, 1, interval);
        if (outCondition == 2) {
            marking = " - !";
        } else if (outCondition == 3) {
            marking = " - ! !";
        }
        return marking;
    }

    @Override
    public int compareTo(ReportBatch other) {
        int number = 0;
        switch (sortMethod) {
            case "condition":
                if (condition > other.getCondition()) {
                    number = -1;
                } else if (condition < other.getCondition()) {
                    number = 1;
                } else {
                    if (getUsableCount() > other.getUsableCount()) {
                        number = -1;
                    } else if (getUsableCount() < other.getUsableCount()) {
                        number = 1;
                    }
                }
                break;
            case "passable":
                if (getUsableCount() > other.getUsableCount()) {
                    number = -1;
                } else if (getUsableCount() < other.getUsableCount()) {
                    number = 1;
                }
                break;
            case "name":
                number = getName().compareTo(other.getName());
                break;
            case "modification":
                number = -1 * (lastDate.compareTo(other.getLastDate()));
                break;
        }
        return number;
    }
}

