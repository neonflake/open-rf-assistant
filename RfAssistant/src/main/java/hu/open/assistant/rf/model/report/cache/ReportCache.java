package hu.open.assistant.rf.model.report.cache;

import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.folder.ReportFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical cache containing processed logical reports. The reports are sorted in logical report folders within the
 * cache.
 */
public class ReportCache {
    List<ReportFolder> reportFolders;

    public ReportCache() {
        reportFolders = new ArrayList<>();
    }

    public List<Report> getReports(String folder) {
        for (ReportFolder reportFolder : reportFolders) {
            if (folder.equals(reportFolder.getFolder())) {
                return reportFolder.getReports();
            }
        }
        return null;
    }

    public void addCache(String folder, List<Report> reports) {
        reportFolders.add(new ReportFolder(folder, reports));
    }

}
