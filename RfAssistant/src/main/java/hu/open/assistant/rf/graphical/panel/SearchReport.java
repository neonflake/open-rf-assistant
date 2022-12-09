package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.ReportListRenderer;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.report.Report;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for searching for test reports within a 90 days period. The reports are processed first and the panel waits for a
 * user input in the search field: number searches for IMEI, mixed characters searches for name (empty search gives back
 * no result). The resulting reports are loaded into the list below. There are controls for opening a report or copying
 * its IMEI or full path to the clipboard.
 */
public class SearchReport extends RfPanel {

    private static final Dimension REPORT_LIST_DIMENSION = new Dimension(600, 560);
    private static final Dimension SEARCH_FIELD_DIMENSION = new Dimension(600, 50);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int LARGE_TEXT_SIZE = 16;
    private static final int SEARCH_INTERVAL = 90;

    private final AssButton searchButton;
    private final AssButton copyPathButton;
    private final AssButton copyImeiButton;
    private final AssButton openButton;
    private final AssTextField searchField;
    private final AssList<Report> reportList;
    private List<Report> unfilteredReports;
    private boolean keywordSet;
    private String lastPanel;

    public SearchReport(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SearchReport");
        //placer.enableDebug();
        AssLabel label = new AssLabel("Riport keresés 90 napra visszamenőleg", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        ReportListRenderer renderer = new ReportListRenderer();
        renderer.setRenderMode("simple_full");
        reportList = new AssList<>("SearchReport reportList", SMALL_TEXT_SIZE, REPORT_LIST_DIMENSION, listener, renderer);
        reportList.enableMouseListening();
        searchField = new AssTextField("SearchReport searchField", SEARCH_FIELD_DIMENSION, LARGE_TEXT_SIZE, listener, "", true);
        searchField.addKeyListener((KeyListener) listener);
        searchButton = new AssButton("SearchReport searchButton", "Keresés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        copyPathButton = new AssButton("SearchReport copyPathButton", "Elérést a vágólapra", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        copyImeiButton = new AssButton("SearchReport copyImeiButton", "IMEI-t a vágólapra", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        openButton = new AssButton("SearchReport openButton", "Megnyitás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("SearchReport backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(label, 1, 1, 3, 1);
        placer.addComponent(searchButton, 4, 1, 1, 1);
        placer.addComponent(searchField, 1, 2, 3, 1);
        placer.addComponent(copyPathButton, 4, 2, 1, 1);
        placer.addComponent(reportList, 1, 3, 3, 8);
        placer.addComponent(copyImeiButton, 4, 3, 1, 1);
        placer.addComponent(openButton, 4, 4, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public void focusSearchField() {
        searchField.requestFocus();
    }

    public void reportPathToClipboard() {
        Report report = reportList.getSelectedValue();
        assistant.reportContentToClipboard(report, "path");
    }

    public void reportImeiToClipboard() {
        Report report = reportList.getSelectedValue();
        assistant.reportContentToClipboard(report, "imei");
    }

    public void openReport() {
        Report report = reportList.getSelectedValue();
        assistant.openReport(report);
    }

    public void clearSearch() {
        keywordSet = false;
        reportList.clearModel();
        searchField.setText("");
        searchField.setEnabled(true);
        searchButton.setEnabled(true);
    }

    private void enableReportControls(){
        copyPathButton.setEnabled(true);
        copyImeiButton.setEnabled(true);
        openButton.setEnabled(true);
    }

    private void disableReportControls(){
        copyPathButton.setEnabled(false);
        copyImeiButton.setEnabled(false);
        openButton.setEnabled(false);
    }

    public void setKeyword(String keyword) {
        keywordSet = true;
        searchField.setText(keyword);
        searchField.setEnabled(false);
        searchButton.setEnabled(false);
    }

    public String getLastPanel() {
        return lastPanel;
    }

    public void setLastPanel(String lastPanel) {
        this.lastPanel = lastPanel;
    }

    public void searchReport() {
        disableReportControls();
        reportList.clearModel();
        List<Report> filteredReports = new ArrayList<>();
        String keyword = searchField.getText().toLowerCase();
        if (!keyword.isBlank()) {
            boolean keywordNumeric = ValidationHelper.hasOnlyNumbers(keyword);
            if (keywordNumeric) {
                keyword = (keyword.trim());
            }
            for (Report report : unfilteredReports) {
                if (keywordNumeric && String.valueOf(report.getImei()).contains(keyword)) {
                    filteredReports.add(0, report);
                } else if (!keywordNumeric && report.getName().toLowerCase().contains(keyword)) {
                    filteredReports.add(0, report);
                }
            }
            reportList.changeModel(filteredReports, false);
        } else {
            reportList.changeModel(unfilteredReports, true);
        }
        if (reportList.getModelSize() > 0) {
            reportList.setSelectedValue(reportList.getElement(0), true);
            enableReportControls();
        }
        if (!keywordSet) {
            searchField.setText("");
            searchField.requestFocus();
        }
    }

    public void prepareReports() {
        disableReportControls();
        placer.disableComponents();
        keywordSet = false;
        unfilteredReports = assistant.readReports(SEARCH_INTERVAL);
        placer.enableComponents();
        searchField.requestFocus();
    }
}
