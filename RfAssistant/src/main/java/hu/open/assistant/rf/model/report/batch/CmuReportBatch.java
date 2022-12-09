package hu.open.assistant.rf.model.report.batch;

import hu.open.assistant.rf.model.compensation.CmuCompensation;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.report.limits.ReportLimits;
import hu.open.assistant.rf.model.TesterType;

/**
 * Stores and holds together reports from the same RF profile at a given CMU type RF station. It has the same
 * functionalities as the generic variant. Initialised with a CMU type compensation.
 */
public class CmuReportBatch extends ReportBatch {

    public CmuReportBatch(String type, String manufacturer, ReportLimits limits, int serial, Profile profile) {
        super(type, manufacturer, limits, serial, TesterType.CMU, profile);
        compensation = new CmuCompensation(serial, manufacturer + " " + type);
    }
}