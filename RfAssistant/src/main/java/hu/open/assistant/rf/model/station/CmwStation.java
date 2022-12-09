package hu.open.assistant.rf.model.station;

import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.database.CmwDatabase;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.batch.CmwReportBatch;
import hu.open.assistant.rf.model.report.batch.ReportBatch;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;

import java.util.List;

/**
 * A logical representation of a CMW type RF tester station. It extends the generic variant with summarised LTE spread
 * calculation.
 */
public class CmwStation extends Station {

    protected float combinedLteTxSpread;
    protected float combinedLteRxSpread;

    public CmwStation(int serial, CmwDatabase database, CmwReportLimits limits, double scriptVersion) {
        super(serial, TesterType.CMW, limits, scriptVersion, database);
    }

    @Override
    public void addReport(Report report) {
        Profile profile = database.getProfileByName(report.getName());
        if (report.getTesterType() == TesterType.CMW && isReportValid(report, profile)) {
            if (isReportBatchNeeded(report)) {
                CmwReportBatch batch = new CmwReportBatch(report.getType(), report.getManufacturer(), limits, report.getSerial(), profile);
                batch.addReport(report);
                reportBatches.add(batch);
            }
        }
    }

    @Override
    protected void calculateSpread() {
        combinedTxSpread = 0;
        combinedRxSpread = 0;
        combinedLteTxSpread = 0;
        combinedLteRxSpread = 0;
        int txCount = 0;
        int rxCount = 0;
        int lteTxCount = 0;
        int lteRxCount = 0;
        for (ReportBatch reportBatch : reportBatches) {
            CmwReportBatch cmwReportBatch = (CmwReportBatch) reportBatch;
            if (cmwReportBatch.getCombinedTxSpread() > 0) {
                combinedTxSpread = combinedTxSpread + reportBatch.getCombinedTxSpread();
                txCount++;
            }
            if (cmwReportBatch.getCombinedRxSpread() > 0) {
                combinedRxSpread = combinedRxSpread + reportBatch.getCombinedRxSpread();
                rxCount++;
            }
            if (cmwReportBatch.getCombinedLteTxSpread() > 0) {
                combinedLteTxSpread = combinedLteTxSpread + cmwReportBatch.getCombinedLteTxSpread();
                lteTxCount++;
            }
            if (cmwReportBatch.getCombinedLteRxSpread() > 0) {
                combinedLteRxSpread = combinedLteRxSpread + cmwReportBatch.getCombinedLteRxSpread();
                lteRxCount++;
            }
        }
        combinedTxSpread = combinedTxSpread / txCount;
        combinedRxSpread = combinedRxSpread / rxCount;
        combinedLteTxSpread = combinedLteTxSpread / lteTxCount;
        combinedLteRxSpread = combinedLteRxSpread / lteRxCount;
    }

    @Override
    protected List<String> getSpreadInfo() {
        List<String> text = super.getSpreadInfo();
        text.add("");
        text.add("Kombinált átlagszórás (4G): ");
        if (combinedLteTxSpread + combinedLteRxSpread > 0) {
            text.add("   - aktuális: " + String.format("%.2f", (combinedLteTxSpread + combinedLteRxSpread) / 2) + " (OUT: " + String.format("%.2f", combinedLteTxSpread) + " / IN: " + String.format("%.2f", combinedLteRxSpread) + ")");
        }
        return text;
    }
}
