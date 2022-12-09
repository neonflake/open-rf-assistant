package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.rf.Config;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.graphical.renderer.ReportListRenderer;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.Report;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for displaying the current days test reports. The list is filled with reports according to the selected filter
 * option (CMU, CMW, both or a specific station). The text representing the reports in the list can also be changed to
 * a simple or more complex view. There is a search field which can be used to narrow down the list: number searches for
 * IMEI mixed characters searches for name (empty search gives back the full results). There are controls for deleting,
 * opening a report or copying its IMEI or full path to the clipboard. There is a button to re-read the reports from disk.
 */
public class SelectReport extends RfPanel {

	private static final Dimension SEARCH_FIELD_DIMENSION = new Dimension(600, 50);
	private static final Dimension REPORT_LIST_DIMENSION = new Dimension(600, 560);
	private static final int SMALL_TEXT_SIZE = 14;
	private static final int LARGE_TEXT_SIZE = 16;

	private final AssButton copyPathButton;
	private final AssButton copyImeiButton;
	private final AssButton openButton;
	private final AssButton deleteButton;
	private final AssButton searchButton;
	private final AssButton displayButton;
	private final AssButton filterButton;
	private final AssTextField searchField;
	private final AssList<Report> reportList;
	private final AssLabel titleLabel;
	private String filterOption;
	private String displayOption;
	private List<Report> reports;
	private String keyword;

	public SelectReport(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "SelectReport");
		//placer.enableDebug();
		filterOption = assistant.getLocalConfig().getFilterOption();
		displayOption = assistant.getLocalConfig().getDisplayOption().equals("full") ? "basic" : "full";
		titleLabel = new AssLabel("", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
		keyword = "";
		changeTitle();
		searchField = new AssTextField("SelectReport searchField", SEARCH_FIELD_DIMENSION, LARGE_TEXT_SIZE, listener, "", true);
		searchField.addKeyListener((KeyListener) listener);
		reportList = new AssList<>("SelectReport reportList", SMALL_TEXT_SIZE, REPORT_LIST_DIMENSION, listener, new ReportListRenderer());
		reportList.enableMouseListening();
		AssButton updateButton = new AssButton("SelectReport updateButton", "Frissítés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		copyPathButton = new AssButton("SelectReport copyPathButton", "Elérést a vágólapra", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		copyImeiButton = new AssButton("SelectReport copyImeiButton", "IMEI-t a vágólapra", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		openButton = new AssButton("SelectReport openButton", "Megnyitás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		deleteButton = new AssButton("SelectReport deleteButton", "Törlés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		searchButton = new AssButton("SelectReport searchButton", "Riport keresés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		displayButton = new AssButton("SelectReport displayButton", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		filterButton = new AssButton("SelectReport filterButton", "Szűrés beállítás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton backButton = new AssButton("SelectReport backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addComponent(updateButton, 4, 1, 1, 1);
		placer.addComponent(searchField, 1, 2, 3, 1);
		placer.addComponent(copyPathButton, 4, 2, 1, 1);
		placer.addComponent(reportList, 1, 3, 3, 8);
		placer.addComponent(copyImeiButton, 4, 3, 1, 1);
		placer.addComponent(openButton, 4, 4, 1, 1);
		placer.addComponent(deleteButton, 4, 5, 1, 1);
		placer.addComponent(searchButton, 4, 6, 1, 1);
		placer.addComponent(displayButton, 4, 7, 1, 1);
		placer.addComponent(filterButton, 4, 8, 1, 1);
		placer.addComponent(backButton, 4, 9, 1, 1);
		placer.addImageComponent(logoImage, 4, 10, 1, 1);
		toggleDisplayOption();
	}

	public String[] getFilterOptions() {
		List<String> optionsList = assistant.readDatabaseNames(null);
		optionsList.add("Minden CMU");
		optionsList.add("Minden CMW");
		optionsList.add("Minden CMU és CMW");
		return optionsList.toArray(new String[0]);
	}

	public String getSelectedFilterOption() {
		if (filterOption.contains("combined")) {
			return "Minden CMU és CMW";
		} else if (filterOption.contains("all")) {
			if (filterOption.contains("cmw")) {
				return "Minden CMW";
			} else {
				return "Minden CMU";
			}
		} else {
			String[] filterParts = filterOption.split("_");
			return filterParts[0].toUpperCase() + " " + filterParts[1];
		}
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

	public void deleteReport() {
		Report report = reportList.getSelectedValue();
		boolean success = assistant.deleteReport(report);
		if (success) {
			reportList.removeElement(report);
		}
		if (reportList.getModelSize() > 0) {
			reportList.setSelectedValue(reportList.getElement(0), true);
			enableReportControls();
		} else {
			disableReportControls();
		}
	}

	public void setFilterOption(String optionSelected) {
		if (optionSelected.contains("és")) {
			filterOption = "all_combined";
		} else if (optionSelected.contains("Minden")) {
			if (optionSelected.contains("CMW")) {
				filterOption = "all_cmw";
			} else {
				filterOption = "all_cmu";
			}
		} else {
			String[] optionParts = optionSelected.split(" ");
			filterOption = optionParts[0].toLowerCase() + "_" + optionParts[1];
		}
		changeTitle();
		Config config = assistant.getLocalConfig();
		config.setFilterOption(filterOption);
		assistant.writeLocalConfig(config);
	}

	public void setDisplayOption() {
		toggleDisplayOption();
		Config config = assistant.getLocalConfig();
		config.setDisplayOption(displayOption);
		assistant.writeLocalConfig(config);
	}

	private void toggleDisplayOption() {
		if (displayOption.equals("full")) {
			displayOption = "basic";
			((AssListRenderer<?>) reportList.getCellRenderer()).setRenderMode("simple_basic");
			displayButton.setText("Megjelenés: egyszerű");
		} else {
			displayOption = "full";
			((AssListRenderer<?>) reportList.getCellRenderer()).setRenderMode("simple_full");
			displayButton.setText("Megjelenés: teljes");
		}
		reportList.revalidate();
		reportList.repaint();
	}

	public void focusSearchField() {
		searchField.requestFocus();
	}

	private void disableReportControls() {
		copyImeiButton.setEnabled(false);
		copyPathButton.setEnabled(false);
		openButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}

	private void enableReportControls() {
		copyImeiButton.setEnabled(true);
		copyPathButton.setEnabled(true);
		openButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}

	private void changeTitle() {
		String title;
		if (!keyword.isBlank()) {
			title = "Találatok: " + keyword;
		} else if (filterOption.contains("combined")) {
			title = "Minden CMU és CMW állomás mai riportja";
		} else if (filterOption.contains("all")) {
			title = "Minden " + filterOption.split("_")[1].toUpperCase() + " állomás mai riportja";
		} else {
			String[] filterParts = filterOption.split("_");
			title = filterParts[0].toUpperCase() + " " + filterParts[1] + " állomás mai riportja";
		}
		titleLabel.setText(title);
	}

	public String getKeyword(){
		return keyword;
	}

	public void searchReport() {
		keyword = searchField.getText();
		clearReports();
		if (!keyword.isBlank()) {
			keyword = keyword.toLowerCase();
			filterButton.setEnabled(false);
			searchButton.setText("Továbbiak keresése");
			List<Report> filteredReports = new ArrayList<>();
			if (ValidationHelper.hasOnlyNumbers(keyword)) {
				keyword = keyword.trim();
				for (Report report : reports) {
					if (String.valueOf(report.getImei()).contains(keyword)) {
						filteredReports.add(0, report);
					}
				}
			} else {
				for (Report report : reports) {
					if (report.getName().toLowerCase().contains(keyword)) {
						filteredReports.add(0, report);
					}
				}
			}
			reportList.changeModel(filteredReports, false);
			selectFirstReport();
		} else {
			searchButton.setText("Riport keresés");
			filterButton.setEnabled(true);
			filterReports();
		}
		changeTitle();
	}

	private void clearReports(){
		disableReportControls();
		searchField.setText("");
		reportList.clearModel();
	}

	private void filterReports() {
		clearReports();
		List<Report> filteredReports = new ArrayList<>();
		int filterSerial = 0;
		if (!filterOption.contains("all")) {
			filterSerial = Integer.parseInt(filterOption.split("_")[1]);
		}
		boolean listCmu = filterOption.contains("cmu") || filterOption.contains("combined");
		boolean listCmw = filterOption.contains("cmw") || filterOption.contains("combined");
		for (Report report : reports) {
			if ((report.getTesterType() == TesterType.CMU && listCmu) || (report.getTesterType() == TesterType.CMW && listCmw)) {
				if (filterSerial == 0 || filterSerial == report.getSerial()) {
					filteredReports.add(0, report);
				}
			}
		}
		reportList.changeModel(filteredReports, false);
		selectFirstReport();
	}

	private void selectFirstReport(){
		if (reportList.getModelSize() > 0) {
			reportList.setSelectedValue(reportList.getElement(0), true);
			enableReportControls();
		}
		focusSearchField();
	}

	public void prepareReports() {
		clearReports();
		keyword = "";
		changeTitle();
		searchButton.setText("Riport keresés");
		filterButton.setEnabled(true);
		placer.disableComponents();
		reports = assistant.readReports(1);
		filterReports();
		placer.enableComponents();
	}
}
