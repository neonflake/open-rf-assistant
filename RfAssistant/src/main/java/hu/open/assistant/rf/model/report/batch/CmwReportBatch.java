package hu.open.assistant.rf.model.report.batch;

import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.limits.ReportLimits;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.CmwReport;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and holds together reports from the same RF profile at a given CMW type RF station. Extends the generic
 * variant with additional information and spread calculation for multiple LTE bands. Condition checking is also
 * dependent on LTE spread. Initialised with a CMW type compensation.
 */
public class CmwReportBatch extends ReportBatch {
    protected int usableLteCount;
    protected int oldUsableLteCount;
    protected float[] lte1TxSpread = new float[3];
    protected float[] lte1RxSpread = new float[3];
    protected float[] lte3TxSpread = new float[3];
    protected float[] lte3RxSpread = new float[3];
    protected float[] lte7TxSpread = new float[3];
    protected float[] lte7RxSpread = new float[3];
    protected float[] lte20TxSpread = new float[3];
    protected float[] lte20RxSpread = new float[3];
    protected float[] oldLte1TxSpread = new float[3];
    protected float[] oldLte1RxSpread = new float[3];
    protected float[] oldLte3TxSpread = new float[3];
    protected float[] oldLte3RxSpread = new float[3];
    protected float[] oldLte7TxSpread = new float[3];
    protected float[] oldLte7RxSpread = new float[3];
    protected float[] oldLte20TxSpread = new float[3];
    protected float[] oldLte20RxSpread = new float[3];
    protected float oldCombinedLteTxSpread;
    protected float oldCombinedLteRxSpread;
    protected float combinedLteTxSpread;
    protected float combinedLteRxSpread;
    protected CmwReportLimits cmwLimits;

    public CmwReportBatch(String type, String manufacturer, ReportLimits limits, int serial, Profile profile) {
        super(type, manufacturer, limits, serial, TesterType.CMW, profile);
        compensation = new CmwCompensation(serial, manufacturer + " " + type);
        usableLteCount = 0;
        cmwLimits = (CmwReportLimits) limits;
    }

    public int getUsableLteCount() {
        return usableLteCount;
    }

    public int getUsableNonLteCount() {
        return getUsableCount() - usableLteCount;
    }

    public int getOldUsableNonLteCount() {
        return getOldUsableCount() - oldUsableLteCount;
    }

    public int getOldUsableLteCount() {
        return oldUsableLteCount;
    }

    public float getCombinedLteTxSpread() {
        return combinedLteTxSpread;
    }

    public float getCombinedLteRxSpread() {
        return combinedLteRxSpread;
    }

    @Override
    public void addReport(Report report) {
        super.addReport(report);
        CmwReport cmwReport = (CmwReport) report;
        if (cmwReport.getDateTime().isAfter(lastDate)) {
            if (cmwReport.isPassable()) {
                if (cmwReport.hasLte()) {
                    usableLteCount++;
                }
            }
        } else if (report.getDateTime().isAfter(firstDate) && report.getDateTime().isBefore(lastDate)) {
            if (report.isPassable()) {
                if (cmwReport.hasLte()) {
                    oldUsableLteCount++;
                }
            }
        }
    }

    @Override
    public void calculateCombinedSpreads() {
        super.calculateCombinedSpreads();
        combinedLteTxSpread = calculateCombinedLteSpread(usableLteCount, lte1TxSpread, lte3TxSpread, lte7TxSpread, lte20TxSpread);
        combinedLteRxSpread = calculateCombinedLteSpread(usableLteCount, lte1RxSpread, lte3RxSpread, lte7RxSpread, lte20RxSpread);
        oldCombinedLteTxSpread = calculateCombinedLteSpread(oldUsableLteCount, oldLte1TxSpread, oldLte3TxSpread, oldLte7TxSpread, oldLte20TxSpread);
        oldCombinedLteRxSpread = calculateCombinedLteSpread(oldUsableLteCount, oldLte1RxSpread, oldLte3RxSpread, oldLte7RxSpread, oldLte20RxSpread);
    }

    protected float calculateCombinedLteSpread(int usableLteCount, float[] lte1Spread, float[] lte3Spread, float[] lte7Spread, float[] lte20Spread) {
        float value = 0;
        int divider = 12;
        if (usableLteCount > 0) {
            for (int i = 0; i < 3; i++) {
                value += lte1Spread[i];
                value += lte3Spread[i];
                value += lte7Spread[i];
                value += lte20Spread[i];
            }
        }
        value = value / divider;
        return value;
    }

    @Override
    public void checkCondition() {
        super.checkCondition();
        if (usableLteCount > 0) {
            for (int i = 0; i < 3; i++) {
                condition = checkPartCondition(lte1TxSpread[i], condition, cmwLimits.getLte1TxInterval());
                condition = checkPartCondition(lte1RxSpread[i], condition, cmwLimits.getLte1RxInterval());
                condition = checkPartCondition(lte3TxSpread[i], condition, cmwLimits.getLte3TxInterval());
                condition = checkPartCondition(lte3RxSpread[i], condition, cmwLimits.getLte3RxInterval());
                condition = checkPartCondition(lte7TxSpread[i], condition, cmwLimits.getLte7TxInterval());
                condition = checkPartCondition(lte7RxSpread[i], condition, cmwLimits.getLte7RxInterval());
                condition = checkPartCondition(lte20TxSpread[i], condition, cmwLimits.getLte20TxInterval());
                condition = checkPartCondition(lte20RxSpread[i], condition, cmwLimits.getLte20RxInterval());
            }
        }
    }

    @Override
    public void calculateAverageSpreads() {
        super.calculateAverageSpreads();
        calculateAverageLteSpread(getValidReports(), usableLteCount, lte1TxSpread, lte1RxSpread,
                lte3TxSpread, lte3RxSpread, lte7TxSpread, lte7RxSpread, lte20TxSpread, lte20RxSpread);
        calculateAverageLteSpread(getOldReports(), oldUsableLteCount, oldLte1TxSpread, oldLte1RxSpread,
                oldLte3TxSpread, oldLte3RxSpread, oldLte7TxSpread, oldLte7RxSpread, oldLte20TxSpread, oldLte20RxSpread);
    }

    protected void calculateAverageLteSpread(List<Report> reports, int usableLteCount, float[] lte1TxSpread, float[] lte1RxSpread,
                                             float[] lte3TxSpread, float[] lte3RxSpread, float[] lte7TxSpread, float[] lte7RxSpread, float[] lte20TxSpread, float[] lte20RxSpread) {
        for (Report report : reports) {
            CmwReport cmwReport = (CmwReport) report;
            if (cmwReport.hasLte()) {
                for (int i = 0; i < 3; i++) {
                    lte1TxSpread[i] += Math.abs(cmwReport.getLte1TxValues()[i] - cmwLimits.getLte1TxExp());
                    lte1RxSpread[i] += Math.abs(cmwReport.getLte1RxValues()[i] - cmwLimits.getLte1RxExp());
                    lte3TxSpread[i] += Math.abs(cmwReport.getLte3TxValues()[i] - cmwLimits.getLte3TxExp());
                    lte3RxSpread[i] += Math.abs(cmwReport.getLte3RxValues()[i] - cmwLimits.getLte3RxExp());
                    lte7TxSpread[i] += Math.abs(cmwReport.getLte7TxValues()[i] - cmwLimits.getLte7TxExp());
                    lte7RxSpread[i] += Math.abs(cmwReport.getLte7RxValues()[i] - cmwLimits.getLte7RxExp());
                    lte20TxSpread[i] += Math.abs(cmwReport.getLte20TxValues()[i] - cmwLimits.getLte20TxExp());
                    lte20RxSpread[i] += Math.abs(cmwReport.getLte20RxValues()[i] - cmwLimits.getLte20RxExp());
                }
            }
        }
        if (usableLteCount > 0) {
            for (int i = 0; i < 3; i++) {
                lte1TxSpread[i] = lte1TxSpread[i] / usableLteCount;
                lte1RxSpread[i] = lte1RxSpread[i] / usableLteCount;
                lte3TxSpread[i] = lte3TxSpread[i] / usableLteCount;
                lte3RxSpread[i] = lte3RxSpread[i] / usableLteCount;
                lte7TxSpread[i] = lte7TxSpread[i] / usableLteCount;
                lte7RxSpread[i] = lte7RxSpread[i] / usableLteCount;
                lte20TxSpread[i] = lte20TxSpread[i] / usableLteCount;
                lte20RxSpread[i] = lte20RxSpread[i] / usableLteCount;
            }
        }
    }

    protected List<String> getLteSpread() {
        return getLteSpreadText(usableLteCount, lte1TxSpread, lte1RxSpread, lte3TxSpread, lte3RxSpread, lte7TxSpread, lte7RxSpread, lte20TxSpread, lte20RxSpread);
    }

    protected List<String> getOldLteSpread() {
        return getLteSpreadText(oldUsableLteCount, oldLte1TxSpread, oldLte1RxSpread, oldLte3TxSpread, oldLte3RxSpread, oldLte7TxSpread, oldLte7RxSpread, oldLte20TxSpread, oldLte20RxSpread);
    }

    private List<String> getLteSpreadText(int usableLteCount, float[] lte1TxSpread, float[] lte1RxSpread, float[] lte3TxSpread, float[] lte3RxSpread, float[] lte7TxSpread, float[] lte7RxSpread, float[] lte20TxSpread, float[] lte20RxSpread) {
        List<String> text = new ArrayList<>();
        if (usableLteCount > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 24] + String.format("%.2f", lte1TxSpread[i]) + markCondition(lte1TxSpread[i], cmwLimits.getLte1TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 27] + String.format("%.2f", lte1RxSpread[i]) + markCondition(lte1RxSpread[i], cmwLimits.getLte1RxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 30] + String.format("%.2f", lte3TxSpread[i]) + markCondition(lte3TxSpread[i], cmwLimits.getLte3TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 33] + String.format("%.2f", lte3RxSpread[i]) + markCondition(lte3RxSpread[i], cmwLimits.getLte3RxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 36] + String.format("%.2f", lte7TxSpread[i]) + markCondition(lte7TxSpread[i], cmwLimits.getLte7TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 39] + String.format("%.2f", lte7RxSpread[i]) + markCondition(lte7RxSpread[i], cmwLimits.getLte7RxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 42] + String.format("%.2f", lte20TxSpread[i]) + markCondition(lte20TxSpread[i], cmwLimits.getLte20TxInterval()));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 45] + String.format("%.2f", lte20RxSpread[i]) + markCondition(lte20RxSpread[i], cmwLimits.getLte20RxInterval()));
            }
        }
        return text;
    }

    protected List<String> getLteSpreadInfo() {
        List<String> text = new ArrayList<>();
        text.add("Kombinált átlagszórás (4G): ");
        if (combinedLteTxSpread + combinedLteRxSpread > 0) {
            text.add("   - aktuális: " + String.format("%.2f", (combinedLteTxSpread + combinedLteRxSpread) / 2) + " (OUT: " + String.format("%.2f", combinedLteTxSpread) + ", IN: " + String.format("%.2f", combinedLteRxSpread) + ")");
        }
        if (oldCombinedLteTxSpread + oldCombinedLteRxSpread > 0) {
            text.add("   - előző: " + String.format("%.2f", (oldCombinedLteTxSpread + oldCombinedLteRxSpread) / 2) + " (OUT: " + String.format("%.2f", oldCombinedLteTxSpread) + ", IN: " + String.format("%.2f", oldCombinedLteRxSpread) + ")");
        }
        if ((combinedLteTxSpread + combinedLteRxSpread > 0 && oldCombinedLteTxSpread + oldCombinedLteRxSpread > 0)) {
            text.add("   - változás: " + showChange((combinedLteTxSpread + combinedLteRxSpread) / 2, (oldCombinedLteTxSpread + oldCombinedLteRxSpread) / 2) + " (OUT: " + showChange(combinedLteTxSpread, oldCombinedLteTxSpread) + ", IN: " + showChange(combinedLteRxSpread, oldCombinedLteRxSpread) + ")");
        }
        if (combinedLteTxSpread + combinedLteRxSpread == 0 && oldCombinedLteTxSpread + oldCombinedLteRxSpread == 0) {
            text.add("   nincs adat");
        }
        return text;
    }


    @Override
    public List<String> getInfo() {
        List<String> text = new ArrayList<>();
        text.add("CMW 290 riport csokor");
        text.add("");
        text.addAll(getGenericInfo());
        if (combinedTxSpread + combinedRxSpread > 0 || oldCombinedTxSpread + oldCombinedRxSpread > 0) {
            text.add("");
            text.addAll(getSpreadInfo());
        }
        if (combinedLteTxSpread + combinedLteRxSpread > 0 || oldCombinedLteTxSpread + oldCombinedLteRxSpread > 0) {
            text.add("");
            text.addAll(getLteSpreadInfo());
        }
        if (combinedTxSpread + combinedRxSpread > 0 || combinedLteTxSpread + combinedLteRxSpread > 0) {
            text.add("");
            text.addAll(getReportInfo());
            text.add("");
            text.add("Aktuális átlagszórások csatornánként:");
            text.addAll(getWcdmaSpread());
            text.addAll(getGsmSpread());
            text.addAll(getLteSpread());
        }
        if (oldCombinedTxSpread + oldCombinedRxSpread > 0 || oldCombinedLteTxSpread + oldCombinedLteRxSpread > 0) {
            text.add("");
            text.add("Előző átlagszórások csatornánként:");
            text.addAll(getOldWcdmaSpread());
            text.addAll(getOldGsmSpread());
            text.addAll(getOldLteSpread());
        }
        if ((combinedTxSpread + combinedRxSpread > 0 && oldCombinedTxSpread + oldCombinedRxSpread > 0) || (combinedLteTxSpread + combinedLteRxSpread > 0 && oldCombinedLteTxSpread + oldCombinedLteRxSpread > 0)) {
            text.add("");
            text.add("Változás mértéke csatornánként:");
            if (usableWcdma1Count > 0 || usableWcdma8Count > 0) {
                text.addAll(getWcdmaSpreadChange());
            }
            if (usableGsmCount > 0) {
                text.addAll(getGsmSpreadChange());
            }
            if (usableLteCount > 0) {
                text.addAll(getLteSpreadChange());
            }
        }
        return text;
    }

    protected List<String> getLteSpreadChange() {
        List<String> text = new ArrayList<>();
        if (oldUsableLteCount > 0) {
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 24] + showChange(lte1TxSpread[i], oldLte1TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 27] + showChange(lte1RxSpread[i], oldLte1RxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 30] + showChange(lte3TxSpread[i], oldLte3TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 33] + showChange(lte3RxSpread[i], oldLte3RxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 36] + showChange(lte7TxSpread[i], oldLte7TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 39] + showChange(lte7RxSpread[i], oldLte7RxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 42] + showChange(lte20TxSpread[i], oldLte20TxSpread[i]));
            }
            for (int i = 0; i < 3; i++) {
                text.add(channels[i + 45] + showChange(lte20RxSpread[i], oldLte20RxSpread[i]));
            }
        }
        return text;
    }

    @Override
    protected void initChannels() {
        super.initChannels();
        String[] allChannels = new String[48];
        System.arraycopy(channels, 0, allChannels, 0, 24);
        allChannels[24] = ("   LTE B1 - OUT Low: ");
        allChannels[25] = ("   LTE B1 - OUT Mid: ");
        allChannels[26] = ("   LTE B1 - OUT High: ");
        allChannels[27] = ("   LTE B1 - IN Low: ");
        allChannels[28] = ("   LTE B1 - IN Mid: ");
        allChannels[29] = ("   LTE B1 - IN High: ");
        allChannels[30] = ("   LTE B3 - OUT Low: ");
        allChannels[31] = ("   LTE B3 - OUT Mid: ");
        allChannels[32] = ("   LTE B3 - OUT High: ");
        allChannels[33] = ("   LTE B3 - IN Low: ");
        allChannels[34] = ("   LTE B3 - IN Mid: ");
        allChannels[35] = ("   LTE B3 - IN High: ");
        allChannels[36] = ("   LTE B7 - OUT Low: ");
        allChannels[37] = ("   LTE B7 - OUT Mid: ");
        allChannels[38] = ("   LTE B7 - OUT High: ");
        allChannels[39] = ("   LTE B7 - IN Low: ");
        allChannels[40] = ("   LTE B7 - IN Mid: ");
        allChannels[41] = ("   LTE B7 - IN High: ");
        allChannels[42] = ("   LTE B20 - OUT Low: ");
        allChannels[43] = ("   LTE B20 - OUT Mid: ");
        allChannels[44] = ("   LTE B20 - OUT High: ");
        allChannels[45] = ("   LTE B20 - IN Low: ");
        allChannels[46] = ("   LTE B20 - IN Mid: ");
        allChannels[47] = ("   LTE B20 - IN High: ");
        channels = allChannels;
    }
}
