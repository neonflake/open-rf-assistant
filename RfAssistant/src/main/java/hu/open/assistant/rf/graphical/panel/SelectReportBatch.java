package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.ReportBatchListRenderer;
import hu.open.assistant.rf.graphical.renderer.ReportListRenderer;
import hu.open.assistant.rf.graphical.renderer.StationListRenderer;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.batch.ReportBatch;
import hu.open.assistant.rf.model.station.Station;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GUI for viewing the processed report batches. The first list is filled with CMU or CMW stations depending on the
 * previous folder selection. The second list is filled with report batches when there is a selection on the first list.
 * Selecting an element from the second list updates the text area with report batch related information. There are
 * controls for changing the sorting method  for the report batches (condition, sample count, name), displaying the
 * selected report batch on a point graph or transferring the compensation from the report batches to the RF profiles.
 * There is a separate button to jump to the RF profile linked to the report batch or when a station is selected the same
 * button can be used to create a check type database log on the database associated with the selected station. When
 * the RF profile modifications are saved the affected report batches need to be removed from the lists.
 */
public class SelectReportBatch extends RfPanel {

    private static final Dimension STATION_LIST_DIMENSION = new Dimension(300, 120);
    private static final Dimension REPORT_LIST_DIMENSION = new Dimension(300, 120);
    private static final Dimension BATCH_LIST_DIMENSION = new Dimension(300, 560);
    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 270);
    private static final Dimension LABEL_DIMENSION = new Dimension(300, 50);
    private static final int SMALL_TEXT_SIZE = 12;
    private static final int MEDIUM_TEXT_SIZE = 14;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssList<Station> stationList;
    private final AssList<Report> reportList;
    private final AssList<ReportBatch> reportBatchList;
    private final AssTextArea textArea;
    private final AssButton graphButton;
    private final AssButton transferButton;
    private final AssButton conditionButton;
    private final AssButton passableButton;
    private final AssButton modificationButton;
    private final AssButton nameButton;
    private final AssButton checkOrJumpButton;
    private final AssLabel stationLabel;
    private List<Station> stations;
    private String sortMethod;
    private ReportBatch selectedReportBatch;
    private Station selectedStation;

    public SelectReportBatch(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectReportBatch");
        //placer.enableDebug();
        sortMethod = "condition";
        AssLabel titleLabel = new AssLabel("Mérések összesítése", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        stationList = new AssList<>("SelectReportBatch stationList", MEDIUM_TEXT_SIZE, STATION_LIST_DIMENSION, listener, new StationListRenderer());
        stationList.enableMouseListening();
        textArea = new AssTextArea("SelectReportBatch textArea", MEDIUM_TEXT_SIZE, TEXT_AREA_DIMENSION, true);
        ReportListRenderer renderer = new ReportListRenderer();
        renderer.setRenderMode("batch");
        reportList = new AssList<>("SelectReportBatch reportList", SMALL_TEXT_SIZE, REPORT_LIST_DIMENSION, listener, renderer);
        reportList.enableMouseListening();
        graphButton = new AssButton("SelectReportBatch graphButton", "Pontgrafikon", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        transferButton = new AssButton("SelectReportBatch transferButton", "Kompenzálások adatbázisba", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssLabel batchLabel = new AssLabel("Telefon típusok: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        stationLabel = new AssLabel("Állomások: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        checkOrJumpButton = new AssButton("SelectReportBatch checkButton", "Ellenőrzés elvégezve", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        conditionButton = new AssButton("SelectReportBatch conditionButton", "Besorolás szerint", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        passableButton = new AssButton("SelectReportBatch passableButton", "Minták száma szerint", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        modificationButton = new AssButton("SelectReportBatch modificationButton", "Módosítása dátuma szerint", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        nameButton = new AssButton("SelectReportBatch nameButton", "Gyártó és típus szerint", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton rebuildButton = new AssButton("SelectReportBatch rebuildButton", "Újrafeldolgozás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        reportBatchList = new AssList<>("SelectReportBatch reportBatchList", MEDIUM_TEXT_SIZE, BATCH_LIST_DIMENSION, listener, new ReportBatchListRenderer());
        reportBatchList.enableMouseListening();
        AssButton backButton = new AssButton("SelectReportBatch backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(stationLabel, 1, 2, 1, 1);
        placer.addComponent(batchLabel, 2, 2, 2, 1);
        placer.addComponent(graphButton, 4, 1, 1, 1);
        placer.addComponent(stationList, 1, 3, 1, 2);
        placer.addComponent(reportBatchList, 2, 3, 2, 8);
        placer.addComponent(rebuildButton, 4, 2, 1, 1);
        placer.addComponent(textArea, 1, 5, 1, 4);
        placer.addComponent(checkOrJumpButton, 4, 3, 1, 1);
        placer.addComponent(conditionButton, 4, 4, 1, 1);
        placer.addComponent(passableButton, 4, 5, 1, 1);
        placer.addComponent(nameButton, 4, 6, 1, 1);
        placer.addComponent(modificationButton, 4, 7, 1, 1);
        placer.addComponent(transferButton, 4, 8, 1, 1);
        placer.addComponent(reportList, 1, 9, 1, 2);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getStationCount() {
        return stationList.getModelSize();
    }

    public void setSelectedStation() {
        selectedStation = stationList.getSelectedValue();
        if (!checkOrJumpButton.isEnabled()) {
            checkOrJumpButton.setEnabled(true);
        }
        checkOrJumpButton.setText("Ellenőrzés elvégezve");
        checkOrJumpButton.setName("SelectReportBatch checkButton");
        updateReportBatches();
    }

    public void markDatabaseAsChecked() {
        assistant.writeCheckDatabaseLog(selectedStation.getDatabase());
    }

    public ReportBatch getSpecificReportBatch(int serial, String name, List<String> selectedFolders, int limit, TesterType testerType) {
        List<Station> tempStations = assistant.processReports(testerType, selectedFolders, limit);
        for (Station station : tempStations) {
            if (station.getSerial() == serial) {
                for (ReportBatch batch : station.getReportBatches()) {
                    if (batch.getName().equals(name)) {
                        return batch;
                    }
                }
            }
        }
        return null;
    }

    public void setSelectedReportBatch() {
        selectedReportBatch = reportBatchList.getSelectedValue();
        graphButton.setEnabled(selectedReportBatch.getUsableCount() > 0 || selectedReportBatch.getOldUsableCount() > 0);
        checkOrJumpButton.setText("Ugrás a profilra");
        checkOrJumpButton.setName("SelectReportBatch jumpButton");
        updateValues();
    }

    public ReportBatch getSelectedBatch() {
        return selectedReportBatch;
    }

    public void setSortMethod(String sortMethod) {
        this.sortMethod = sortMethod;
        enableSortControls();
    }

    private void updateValues() {
        textArea.setText(TextHelper.stringListToLineBrokenString(selectedReportBatch.getInfo()));
        textArea.setCaretPosition(0);
        reportList.changeModel(selectedReportBatch.getMarkedReports(), true);
        reportList.setToolTipText("Szórás számolásához felhasznált riportok.");
    }

    public List<Compensation> getCompensations() {
        List<Compensation> compensations = new ArrayList<>();
        for (Station station : stations) {
            for (ReportBatch reportBatch : station.getReportBatches()) {
                Compensation compensation = reportBatch.getCompensation();
                if (!compensation.isEmpty()) {
                    compensations.add(compensation);
                }
            }
        }
        return compensations;
    }

    private void clearContent() {
        reportList.clearModel();
        textArea.setText("");
        checkOrJumpButton.setEnabled(false);
        graphButton.setEnabled(false);
        transferButton.setEnabled(false);
        conditionButton.setEnabled(false);
        passableButton.setEnabled(false);
        nameButton.setEnabled(false);
        modificationButton.setEnabled(false);
    }

    private void enableSortControls() {
        switch (sortMethod) {
            case "condition":
                conditionButton.setEnabled(false);
                passableButton.setEnabled(true);
                nameButton.setEnabled(true);
                modificationButton.setEnabled(true);
                break;
            case "passable":
                conditionButton.setEnabled(true);
                passableButton.setEnabled(false);
                nameButton.setEnabled(true);
                modificationButton.setEnabled(true);
                break;
            case "name":
                conditionButton.setEnabled(true);
                passableButton.setEnabled(true);
                nameButton.setEnabled(false);
                modificationButton.setEnabled(true);
                break;
            default:
                conditionButton.setEnabled(true);
                passableButton.setEnabled(true);
                nameButton.setEnabled(true);
                modificationButton.setEnabled(false);
                break;
        }
    }


    public void prepareStations(List<String> selectedFolders, int limit, TesterType testerType) {
        if (testerType == TesterType.CMU) {
            stationLabel.setText("CMU 200 állomások: ");
        } else {
            stationLabel.setText("CMW 290 állomások: ");
        }
        clearContent();
        placer.disableComponents();
        reportBatchList.clearModel();
        stationList.clearModel();
        stations = assistant.processReports(testerType, selectedFolders, limit);
        stationList.changeModel(stations, false);
        placer.enableComponents();
    }

    public void removeSavedElements() {
        for (int i = 0; i< stationList.getModelSize(); i++) {
            List<ReportBatch> obsoleteBatches = new ArrayList<>();
            for (ReportBatch reportBatch : stationList.getElement(i).getReportBatches()) {
                if (!reportBatch.getCompensation().isEmpty()) {
                    obsoleteBatches.add(reportBatch);
                }
            }
            for (ReportBatch reportBatch : obsoleteBatches) {
                stationList.getElement(i).getReportBatches().remove(reportBatch);
            }
        }
    }

    public void checkTransferButton() {
        transferButton.setEnabled(stationIsCompensated());
    }

    private boolean stationIsCompensated() {
        for (Station station : stationList.getModelAsArrayList()) {
            if (station.isCompensated()) {
                return true;
            }
        }
        return false;
    }

    public void updateReportBatches() {
        List<ReportBatch> reportBatches = selectedStation.getReportBatches();
        textArea.setText(TextHelper.stringListToLineBrokenString(selectedStation.getInfo()));
        textArea.setCaretPosition(0);
        reportList.changeModel(selectedStation.getValidNonPassedUsableReports(), true);
        reportList.setToolTipText("Kis eltéréseket tartalmazó riportok.");
        for (ReportBatch reportBatch : reportBatches) {
            reportBatch.setSortMethod(sortMethod);
        }
        Collections.sort(reportBatches);
        reportBatchList.changeModel(reportBatches, false);
        graphButton.setEnabled(false);
        enableSortControls();
    }
}
