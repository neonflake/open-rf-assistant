package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.batch.CmwReportBatch;
import hu.open.assistant.rf.model.report.batch.ReportBatch;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a report batch on a graphical list.
 */
public class ReportBatchListRenderer extends AssListRenderer<ReportBatch> {

    public ReportBatchListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ReportBatch> list, ReportBatch reportBatch, int index, boolean isSelected, boolean cellHasFocus) {
        setIcon(new AssImage(getClass().getResource("/images/arrow_" + reportBatch.getCondition() + ".png")));
        if (reportBatch.getTesterType() == TesterType.CMU) {
            if (!reportBatch.getCompensation().isEmpty()) {
                setText("[" + reportBatch.getUsableCount() + "] " + reportBatch.getName() + " (kompenzálva)");
            } else {
                setText("[" + reportBatch.getUsableCount() + "] " + reportBatch.getName());
            }
        } else {
            if (!reportBatch.getCompensation().isEmpty()) {
                setText("[" + ((CmwReportBatch) reportBatch).getUsableNonLteCount() + "] [" + ((CmwReportBatch) reportBatch).getUsableLteCount() + "] " + reportBatch.getName() + " (kompenzálva)");
            } else {
                setText("[" + ((CmwReportBatch) reportBatch).getUsableNonLteCount() + "] [" + ((CmwReportBatch) reportBatch).getUsableLteCount() + "] " + reportBatch.getName());
            }
        }
        return super.getListCellRendererComponent(list, reportBatch, index, isSelected, cellHasFocus);
    }
}
