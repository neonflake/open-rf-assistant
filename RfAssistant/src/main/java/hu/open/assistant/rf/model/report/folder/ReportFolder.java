package hu.open.assistant.rf.model.report.folder;

import hu.open.assistant.rf.model.report.Report;

import java.util.List;

/**
 * A logical representation of an actual folder containing RF test reports. It stores processed logical reports.
 */
public class ReportFolder {
    String folder;
    List<Report> reports;

    public ReportFolder(String folder, List<Report> reports) {
        this.folder = folder;
        this.reports = reports;
    }

    public String getFolder(){
        return folder;
    }

    public List<Report> getReports() {
        return reports;
    }
}