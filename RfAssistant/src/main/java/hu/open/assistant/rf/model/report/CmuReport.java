package hu.open.assistant.rf.model.report;

import hu.open.assistant.rf.model.report.limits.ReportLimits;
import hu.open.assistant.rf.model.report.values.CmuReportValues;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;

/**
 * A logical representation of a CMU type RF test report. It has the same functionalities as the generic variant.
 **/
public class CmuReport extends Report {

    public CmuReport(String filename, String folder, LocalDateTime dateTime, String type, String manufacturer, int serial, String position, double scriptVersion, long imei, boolean passed, CmuReportValues values, ReportLimits limits) {
        super(filename, folder, dateTime, type, manufacturer, serial, TesterType.CMU, position, scriptVersion, imei, passed, values, limits);
    }
}