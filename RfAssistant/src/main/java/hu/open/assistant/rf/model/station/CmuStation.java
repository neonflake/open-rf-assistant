package hu.open.assistant.rf.model.station;

import hu.open.assistant.rf.model.database.CmuDatabase;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.batch.CmuReportBatch;
import hu.open.assistant.rf.model.report.limits.CmuReportLimits;

/**
 * A logical representation of a CMU type RF tester station. It has the same functionalities as the generic variant.
 */
public class CmuStation extends Station {

    public CmuStation(int serial, CmuDatabase database, CmuReportLimits limits, double scriptVersion) {
        super(serial, TesterType.CMU, limits, scriptVersion, database);
    }

    @Override
    public void addReport(Report report) {
        Profile profile = database.getProfileByName(report.getName());
        if (report.getTesterType() == TesterType.CMU && isReportValid(report, profile)) {
            if (isReportBatchNeeded(report)) {
                CmuReportBatch batch = new CmuReportBatch(report.getType(), report.getManufacturer(), limits, report.getSerial(), profile);
                batch.addReport(report);
                reportBatches.add(batch);
            }
        }
    }
}