package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.CmwReport;
import hu.open.assistant.rf.model.report.Report;

import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;

/**
 * Responsible for displaying a report on a graphical list.
 */
public class ReportListRenderer extends AssListRenderer<Report> {

    public ReportListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Report> list, Report report, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, report, index, isSelected, cellHasFocus);
        String text = "";
        switch (renderMode) {
            case "simple_full":
                String filename = report.getTesterType() == TesterType.CMW ? ((CmwReport) report).getShortFileName() : report.getFilename();
                String equipment = report.getTesterType().toString();
                text = equipment + " " + report.getSerial() + " - " + DateHelper.localDateTimeToShortIsoTextDate(report.getDateTime()) +
                        " " + DateHelper.localDateTimeToIsoTextTime(report.getDateTime()) +
                        " - " + filename + " - " + report.getName();
                break;
            case "simple_basic":
                if (report.isPassed()) {
                    text = "PASS - ";
                } else {
                    text = "FAIL - ";
                }
                text = text + report.getImei() + " - " + report.getName();
                break;
            case "batch":
                if (report.getMark() == 2 || report.getMark() == 0) {
                    setForeground(Color.black);
                } else {
                    setForeground(Color.gray);
                }
                text = report.getExtendedFilename();
                break;
        }
        this.setText(text);
        return component;
    }
}
