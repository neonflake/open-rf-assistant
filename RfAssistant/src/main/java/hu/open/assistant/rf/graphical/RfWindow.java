package hu.open.assistant.rf.graphical;

import hu.open.assistant.rf.graphical.panel.CreateProfile;
import hu.open.assistant.rf.graphical.panel.EditConfig;
import hu.open.assistant.rf.graphical.panel.EditEquipment;
import hu.open.assistant.rf.graphical.panel.EditProfile;
import hu.open.assistant.rf.graphical.panel.SearchReport;
import hu.open.assistant.rf.graphical.panel.SelectBackup;
import hu.open.assistant.rf.graphical.panel.SelectEquipment;
import hu.open.assistant.rf.graphical.panel.SelectProfile;
import hu.open.assistant.rf.graphical.panel.SelectProfilePart;
import hu.open.assistant.rf.graphical.panel.SelectProfileUsage;
import hu.open.assistant.rf.graphical.panel.SelectReport;
import hu.open.assistant.rf.graphical.panel.SelectReportBatch;
import hu.open.assistant.rf.graphical.panel.SelectSourceFolder;
import hu.open.assistant.rf.graphical.panel.SelectTac;
import hu.open.assistant.rf.graphical.panel.SelectTask;
import hu.open.assistant.rf.graphical.panel.ShowGraph;
import hu.open.assistant.rf.graphical.panel.ShowLog;
import hu.open.assistant.rf.graphical.panel.SyncProfile;
import hu.open.assistant.commons.graphical.notification.AssNotification;
import hu.open.assistant.commons.graphical.task.AssTask;
import hu.open.assistant.commons.graphical.task.CommonTaskName;
import hu.open.assistant.commons.graphical.task.TaskType;
import hu.open.assistant.commons.util.SystemHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssComboBox;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssMenuItem;
import hu.open.assistant.commons.graphical.AssWindow;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.rf.Config;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.model.report.CmuReport;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.batch.ReportBatch;

import javax.swing.JComponent;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

/**
 * Graphical controller of the application as well as the actual program window which extends the base window. It holds
 * all the panels which contain the GUI elements and a menu for additional control. Most of the user inputs are handled
 * here and are distributed back to the panels. It also handles data exchange between different panels, performs a check
 * on startup and enables admin related functions when a valid password is given. The panels and data controller have
 * access to the window to show notifications. The window can start different disk access related tasks on another thread.
 */
public class RfWindow extends AssWindow {

	private static final String WINDOW_TITLE = "Open RF Assistant v";
	private static final int WINDOW_WIDTH = 960;
	private static final int WINDOW_HEIGHT = 800;
	public static final int OPERATOR_GRAPH_INTERVAL = 90;
	public static final int OPERATOR_GRAPH_LIMIT = 5;

	private final RfAssistant assistant;
	private final SelectTask selectTask;
	private final SelectReportBatch selectReportBatch;
	private final SelectProfile selectProfile;
	private final SelectProfileUsage selectProfileUsage;
	private final SelectSourceFolder selectSourceFolder;
	private final SelectProfilePart selectProfilePart;
	private final SelectTac selectTac;
	private final SelectReport selectReport;
	private final SelectEquipment selectEquipment;
	private final SelectBackup selectBackup;
	private final SearchReport searchReport;
	private final EditProfile editProfile;
	private final EditEquipment editEquipment;
	private final CreateProfile createProfile;
	private final ShowGraph showGraph;
	private final RfMenu menu;
	private final ShowLog showLog;
	private final EditConfig editConfig;
	private final SyncProfile syncProfile;
	private boolean supportEnabled;
	private boolean networkFailure;
	private String networkFolder = "";

	public RfWindow(RfAssistant assistant, boolean testMode) {
		super(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE + assistant.getVersion());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.assistant = assistant;
		menu = new RfMenu(this);
		this.setJMenuBar(menu);
		selectSourceFolder = new SelectSourceFolder(this, assistant);
		selectTask = new SelectTask(this, assistant);
		selectReportBatch = new SelectReportBatch(this, assistant);
		selectProfile = new SelectProfile(this, assistant);
		selectProfileUsage = new SelectProfileUsage(this, assistant);
		selectReport = new SelectReport(this, assistant);
		selectEquipment = new SelectEquipment(this, assistant);
		selectBackup = new SelectBackup(this, assistant);
		searchReport = new SearchReport(this, assistant);
		syncProfile = new SyncProfile(this, assistant);
		showLog = new ShowLog(this, assistant);
		showGraph = new ShowGraph(this, assistant);
		editProfile = new EditProfile(this, assistant);
		editEquipment = new EditEquipment(this, assistant);
		createProfile = new CreateProfile(this, assistant);
		selectProfilePart = new SelectProfilePart(this, assistant);
		selectTac = new SelectTac(this, assistant);
		editConfig = new EditConfig(this, assistant);
		notifier = new RfNotifier(this);
		if (!assistant.isPasswordEnabled()) {
			enableSupport(false);
		}
		setVisible(!testMode);
	}

	public void startUpCheck() {
		if (!assistant.isNetworkFolderValid(assistant.getLocalConfig().getNetworkFolder())) {
			menu.disableOperator();
			selectTask.disableTasks();
			networkFailure = true;
			showNotification(RfNotice.SELECT_TASK_NETWORK_ERROR);
		} else if (assistant.updateNeeded()) {
			SystemHelper.runJar("Updater.jar -force");
			System.exit(0);
		} else if (assistant.isGlobalConfigValid()) {
			showNotification(RfNotice.SELECT_TASK_CONFIG_ERROR);
		} else {
			startTask(RfTaskName.PREPARE_PRELOAD, TaskType.DISK_ACCESS);
		}
		selectTask();
	}

	private void enableSupport(boolean openSupportMenu) {
		menu.enableSupport();
		supportEnabled = true;
		selectTask.setMode("support");
		editConfig.enableSupport();
		showLog.enableSupport();
		if (openSupportMenu) {
			menu.openSupport();
		}
	}

	// Notifications

	private void notificationButtonPress(String name) {

		closeNotification();

		switch (name) {

			// YES button
			case "AppNotification yesButton":
				switch ((RfNotice) notification.getNotice()) {
					case SELECT_PROFILE_DELETE:
						selectProfile.deleteSelectedProfile();
						break;
					case SELECT_PROFILE_REVERT:
						selectProfile.revertCompensation();
						break;
					case SELECT_PROFILE_RELOAD:
						selectProfile.initReload();
						selectProfile(selectProfile.getTesterType(), false, false);
						break;
					case SELECT_PROFILE_SAVE:
						assistant.setDatabaseCleanup(true);
						startTask(RfTaskName.PREPARE_DATABASE_SAVE, TaskType.DISK_ACCESS);
						break;
					case EDIT_PROFILE_RESET:
						editProfile.setDefaultValues();
						break;
					case SELECT_REPORT_BATCH_DATABASE_CHECK:
						selectReportBatch.markDatabaseAsChecked();
						break;
					case SELECT_PROFILE_USAGE_CONFIRM:
						selectProfileUsage.moveUnusedProfiles();
						showNotification(RfNotice.SELECT_PROFILE_USAGE_SAVE);
						selectTask();
						break;
					case EDIT_CONFIG_UPDATE_FOLDER_PATHS:
						editConfig.updateFolderPaths();
						break;
					case EDIT_CONFIG_UPDATE_CMU_LIMITS:
						editConfig.updateCmuLimits();
						break;
					case EDIT_CONFIG_UPDATE_CMW_LIMITS:
						editConfig.updateCmwLimits();
						break;
					case EDIT_CONFIG_UPDATE_CMW_LTE_LIMITS:
						editConfig.updateCmwLteLimits();
						break;
					case EDIT_CONFIG_UPDATE_PROGRAM_CONFIG:
						editConfig.updateProgramConfig();
						break;
					case EDIT_CONFIG_UPDATE_PRELOAD_CONFIG:
						editConfig.updatePreloadConfig();
						break;
					case CREATE_PROFILE_SAVE:
						assistant.setDatabaseCleanup(true);
						startTask(RfTaskName.PREPARE_PROFILE_CREATE, TaskType.DISK_ACCESS);
						break;
					case SELECT_REPORT_DELETE:
						selectReport.deleteReport();
						break;
					case EDIT_EQUIPMENT_DELETE_CONFIRM:
						editEquipment.removeEquipment();
						break;
					case NETWORK_FOLDER_CREATE:
						assistant.createNetworkFolder(networkFolder);
						Config config = assistant.getLocalConfig();
						config.setNetworkFolder(networkFolder);
						assistant.writeLocalConfig(config);
						System.exit(0);
						break;
					case SELECT_BACKUP_DELETE_CONFIRM:
						selectBackup.deleteBackup();
						selectTask();
						break;
					case SELECT_BACKUP_RESTORE_CONFIRM:
						startTask(RfTaskName.PREPARE_BACKUP_RESTORE, TaskType.DISK_ACCESS);
						break;
					case GENERIC_MODIFIED:
						String destination = notification.getValue();
						if (!destination.contains("MenuItem")) {
							switch (destination) {
								case "SelectProfile":
									if (selectProfile.isJumped()) {
										selectReportBatch();
									} else {
										selectTask();
									}
									break;
								case "ShowGraph":
									selectReportBatch();
									break;
								case "CreateProfile":
								case "EditProfile":
								case "SelectTac":
									changePanel(selectProfile);
									break;
								case "EditEquipment":
									selectTask();
									break;
								case "SelectProfilePart":
									changePanel(createProfile);
									break;
							}
						} else {
							selectPanel(destination);
						}
						break;
				}
				break;

			// NO button
			case "AppNotification noButton":
				switch ((RfNotice) notification.getNotice()) {
					case SELECT_PROFILE_SAVE:
						assistant.setDatabaseCleanup(false);
						startTask(RfTaskName.PREPARE_DATABASE_SAVE, TaskType.DISK_ACCESS);
						break;
					case CREATE_PROFILE_SAVE:
						assistant.setDatabaseCleanup(false);
						startTask(RfTaskName.PREPARE_PROFILE_CREATE, TaskType.DISK_ACCESS);
						break;
				}
				break;

			// OK button
			case "AppNotification okButton":
				switch ((RfNotice) notification.getNotice()) {
					case SELECT_REPORT_FILTER:
						selectReport.setFilterOption(notification.getSelectedOption());
						selectReport();
						break;
					case SELECT_EQUIPMENT_FILTER:
						startTask(RfTaskName.SHOW_OPERATOR_GRAPH, TaskType.DISK_ACCESS);
						break;
					case NETWORK_FOLDER_CONFIG:
						networkFolder = (notification).getInput();
						if (assistant.isNetworkFolderValid(networkFolder)) {
							Config config = assistant.getLocalConfig();
							config.setNetworkFolder(networkFolder);
							assistant.writeLocalConfig(config);
							System.exit(0);
						} else {
							showNotification(RfNotice.NETWORK_FOLDER_CREATE);
						}
						break;
					case EDIT_EQUIPMENT_CREATE:
						editEquipment.createEquipment(notification.getInput());
						break;
					case EDIT_PROFILE_IMPORT:
						editProfile.importValues(selectProfile.getSelectedDatabase().getProfileByName(notification.getSelectedOption()));
						break;
					case SELECT_BACKUP_CREATE:
						startTask(RfTaskName.PREPARE_BACKUP_CREATE, TaskType.DISK_ACCESS);
						break;
					case SELECT_TASK_SUPPORT_PASSWORD:
						if (notification.getInput().equals(assistant.getGlobalConfig().getSupportPassword())) {
							enableSupport(notification.getValue().equals("menu_clicked"));
						} else {
							showNotification(RfNotice.SELECT_TASK_WRONG_PASSWORD);
						}
						break;
				}
				break;
		}
	}

	public AssNotification getNotification() {
		return notification;
	}

	// Change checking

	private boolean noChangeOccurred(String destination) {
		boolean noChangeOccurred = true;
		String currentPanel = getContentPane().getName();
		switch (currentPanel) {
			case "ShowGraph":
				noChangeOccurred = !showGraph.isModified();
				break;
			case "CreateProfile":
				noChangeOccurred = !createProfile.isModified();
				break;
			case "SelectProfilePart":
				noChangeOccurred = !selectProfilePart.isModified();
				break;
			case "SelectProfile":
				noChangeOccurred = !selectProfile.isDatabaseModified();
				break;
			case "EditProfile":
				noChangeOccurred = !editProfile.isModified();
				break;
			case "EditEquipment":
				noChangeOccurred = !editEquipment.isModified();
				break;
			case "SelectTac":
				noChangeOccurred = !selectTac.isModified();
				break;
		}
		if (!noChangeOccurred) {
			if (destination == null) {
				destination = currentPanel;
			}
			showNotification(RfNotice.GENERIC_MODIFIED, destination);
		}
		return noChangeOccurred;
	}

	// AppMenu panel navigation

	private void selectPanel(String menuItemSelected) {
		switch (menuItemSelected) {
			case "reportBatchMenuItem":
				selectReportBatch();
				break;
			case "cmuProfileMenuItem":
				selectProfile(TesterType.CMU, false, false);
				break;
			case "cmwProfileMenuItem":
				selectProfile(TesterType.CMW, false, false);
				break;
			case "sourceMenuItem":
				selectSourceFolder();
				break;
			case "syncProfileMenuItem":
				syncProfile();
				break;
			case "usageMenuItem":
				selectProfileUsage();
				break;
			case "logMenuItem":
				showLog();
				break;
			case "configMenuItem":
				editConfig();
				break;
			case "reportListMenuItem":
				selectReport();
				break;
			case "reportSearchMenuItem":
				searchReport("", "select_task");
				break;
			case "equipmentInfoMenuItem":
				selectEquipment();
				break;
			case "equipmentListMenuItem":
				editEquipment();
				break;
			case "backupMenuItem":
				selectBackup();
				break;
			default:
				if (menuItemSelected.contains("SelectTask")) {
					selectTask();
				} else if (menuItemSelected.contains("SelectEquipment")) {
					selectEquipment();
				} else if (menuItemSelected.contains("SelectReport")) {
					selectReport();
				}
		}
	}

	// Panel methods

	// CreateProfile

	private void createProfile() {
		createProfile.clearFields(selectProfile.getDatabases(), selectProfile.getSelectedDatabase(), null);
		changePanel(createProfile);
	}

	private void repositionProfile() {
		createProfile.clearFields(selectProfile.getDatabases(), selectProfile.getSelectedDatabase(), selectProfile.getSelectedProfile());
		changePanel(createProfile);
	}

	private void createProfileButtonPress(String name) {
		switch (name) {
			case "CreateProfile saveButton":
				showNotification(RfNotice.CREATE_PROFILE_SAVE);
				break;
			case "CreateProfile backButton":
				if (noChangeOccurred(null)) {
					changePanel(selectProfile);
				}
				break;
			case "CreateProfile manufacturerButton":
				createProfile.saveSelections();
				selectPart("manufacturer");
				break;
			case "CreateProfile categoryButton":
				createProfile.saveSelections();
				selectPart("category");
				break;
			case "CreateProfile boxButton":
				createProfile.saveSelections();
				selectPart("box");
				break;
			case "CreateProfile positionButton":
				createProfile.saveSelections();
				selectPart("position");
				break;
			case "CreateProfile scriptButton":
				createProfile.saveSelections();
				selectPart("script");
				break;
		}
	}

	// EditConfig

	private void editConfig() {
		if (supportEnabled) {
			editConfig.showConfig("folder_paths");
		} else {
			editConfig.showConfig("preload_config");
		}
		changePanel(editConfig);
		menu.disableJumpMenuItem();
	}

	private void editConfigButtonPress(String name) {
		switch (name) {
			case "EditConfig folderButton":
				editConfig.showConfig("folder_paths");
				break;
			case "EditConfig cmuLimitButton":
				editConfig.showConfig("cmu_limits");
				break;
			case "EditConfig cmwLimitButton":
				editConfig.showConfig("cmw_limits");
				break;
			case "EditConfig cmwLteLimitButton":
				editConfig.showConfig("cmw_lte_limits");
				break;
			case "EditConfig programConfigButton":
				editConfig.showConfig("program_config");
				break;
			case "EditConfig preloadButton":
				editConfig.showConfig("preload_config");
				break;
			case "EditConfig saveButton":
				switch (editConfig.getSelectedConfig()) {
					case "folder_paths":
						showNotification(RfNotice.EDIT_CONFIG_UPDATE_FOLDER_PATHS);
						break;
					case "cmu_limits":
						showNotification(RfNotice.EDIT_CONFIG_UPDATE_CMU_LIMITS);
						break;
					case "cmw_limits":
						showNotification(RfNotice.EDIT_CONFIG_UPDATE_CMW_LIMITS);
						break;
					case "cmw_lte_limits":
						showNotification(RfNotice.EDIT_CONFIG_UPDATE_CMW_LTE_LIMITS);
						break;
					case "program_config":
						showNotification(RfNotice.EDIT_CONFIG_UPDATE_PROGRAM_CONFIG);
						break;
					case "preload_config":
						showNotification(RfNotice.EDIT_CONFIG_UPDATE_PRELOAD_CONFIG);
						break;
				}
				break;
			case "EditConfig backButton":
				selectTask();
				break;
		}
	}

	// EditEquipment

	private void editEquipment() {
		changePanel(editEquipment);
		menu.disableJumpMenuItem();
		startTask(RfTaskName.PREPARE_EQUIPMENTS_EDIT, TaskType.DISK_ACCESS);
	}

	private void editEquipmentButtonPress(String name) {
		switch (name) {
			case "EditEquipment backButton":
				if (noChangeOccurred(null)) {
					selectTask();
				}
				break;
			case "EditEquipment deleteButton":
				editEquipment.deleteEquipment();
				break;
			case "EditEquipment modifyButton":
				editEquipment.modifyEquipment();
				break;
			case "EditEquipment saveButton":
				editEquipment.saveEquipments();
				selectTask();
				showNotification(RfNotice.EDIT_EQUIPMENT_SAVE);
				break;
			case "EditEquipment createButton":
				showNotification(RfNotice.EDIT_EQUIPMENT_CREATE);
				break;
			case "EditEquipment folderButton":
				assistant.openPositionFolder();
				break;
			case "EditEquipment reloadButton":
				startTask(RfTaskName.PREPARE_EQUIPMENTS_EDIT_CHECKED, TaskType.DISK_ACCESS);
				break;
		}
	}

	// EditProfile

	private void editProfile() {
		editProfile.openProfile(selectProfile.getSelectedProfile());
		changePanel(editProfile);
	}

	private void editProfileButtonPress(String name) {
		switch (name) {
			case "EditProfile wcdma1Button":
				editProfile.setSelectedBand("wcdma1");
				break;
			case "EditProfile wcdma8Button":
				editProfile.setSelectedBand("wcdma8");
				break;
			case "EditProfile gsm900Button":
				editProfile.setSelectedBand("gsm900");
				break;
			case "EditProfile gsm1800Button":
				editProfile.setSelectedBand("gsm1800");
				break;
			case "EditProfile lte1Button":
				editProfile.setSelectedBand("lte1");
				break;
			case "EditProfile lte3Button":
				editProfile.setSelectedBand("lte3");
				break;
			case "EditProfile lte7Button":
				editProfile.setSelectedBand("lte7");
				break;
			case "EditProfile lte20Button":
				editProfile.setSelectedBand("lte20");
				break;
			case "EditProfile bandButton":
				editProfile.switchBands();
				break;
			case "EditProfile fixButton":
				if (editProfile.saveCompensation()) {
					selectProfile.updateSaveButton();
					selectProfile.setSelectedProfile(selectProfile.getSelectedProfile());
					changePanel(selectProfile);
				}
				break;
			case "EditProfile importButton":
				showNotification(RfNotice.EDIT_PROFILE_IMPORT, selectReport.getSelectedFilterOption(), selectProfile.getImportOptions());
				break;
			case "EditProfile defaultButton":
				showNotification(RfNotice.EDIT_PROFILE_RESET);
				break;
			case "EditProfile backButton":
				if (noChangeOccurred(null)) {
					changePanel(selectProfile);
				}
				break;
		}
	}

	// SearchReport

	private void searchReport(String keyword, String lastPanel) {
		searchReport.clearSearch();
		if (keyword.isBlank()) {
			startTask(RfTaskName.PREPARE_REPORT_SEARCH, TaskType.DISK_ACCESS);
		} else {
			startTask(RfTaskName.PREPARE_REPORT_SEARCH, TaskType.DISK_ACCESS, keyword);
		}
		searchReport.setLastPanel(lastPanel);
		changePanel(searchReport);
		menu.disableJumpMenuItem();
	}

	private void searchReportButtonPress(String name) {
		switch (name) {
			case "SearchReport backButton":
				if (searchReport.getLastPanel().equals("select_report")) {
					selectReport();
				} else if (searchReport.getLastPanel().equals("select_equipment")) {
					selectEquipment();
				} else {
					selectTask();
				}
				break;
			case "SearchReport searchButton":
				searchReport.searchReport();
				break;
			case "SearchReport openButton":
				searchReport.openReport();
				break;
			case "SearchReport copyPathButton":
				searchReport.reportPathToClipboard();
				break;
			case "SearchReport copyImeiButton":
				searchReport.reportImeiToClipboard();
				break;
		}
	}

	// SelectBackup

	private void selectBackup() {
		selectBackup.updateBackups();
		changePanel(selectBackup);
		menu.disableJumpMenuItem();
	}

	private void selectBackupButtonPress(String name) {
		switch (name) {
			case "SelectBackup backButton":
				selectTask();
				break;
			case "SelectBackup deleteButton":
				showNotification(RfNotice.SELECT_BACKUP_DELETE_CONFIRM);
				break;
			case "SelectBackup cmuBackupButton":
				showNotification(RfNotice.SELECT_BACKUP_CREATE, selectBackup.getCmuFilterOptions());
				break;
			case "SelectBackup cmwBackupButton":
				showNotification(RfNotice.SELECT_BACKUP_CREATE, selectBackup.getCmwFilterOptions());
				break;
			case "SelectBackup restoreButton":
				showNotification(RfNotice.SELECT_BACKUP_RESTORE_CONFIRM);
				break;
		}
	}

	// SelectEquipment

	private void selectEquipment() {
		changePanel(selectEquipment);
		selectEquipment.focusSearchField();
		if (selectEquipment.isRefreshNeeded()) {
			startTask(RfTaskName.PREPARE_EQUIPMENTS, TaskType.DISK_ACCESS);
		}
		menu.enableJumpMenuItem("SelectReport");
	}

	private void selectEquipmentButtonPress(String name) {
		switch (name) {
			case "SelectEquipment backButton":
				selectTask();
				break;
			case "SelectEquipment searchButton":
				searchReport(selectEquipment.getSelectedEquipment().getName(), "select_equipment");
				break;
			case "SelectEquipment graphButton":
				showNotification(RfNotice.SELECT_EQUIPMENT_FILTER, selectEquipment.getFilterOptions());
				break;
			case "SelectEquipment refreshButton":
				assistant.cmuDatabaseRefresh();
				assistant.cmwDatabaseRefresh();
				selectEquipment.refreshEquipments();
				selectEquipment();
				break;
		}
	}

	// SelectProfile

	private void selectProfile(TesterType testerType, boolean transfer, boolean jump) {
		selectProfile.clearLists();
		if (transfer) {
			startTask(RfTaskName.PREPARE_DATABASES_WITH_TRANSFER, TaskType.DISK_ACCESS);
		} else if (jump) {
			startTask(RfTaskName.PREPARE_DATABASES_WITH_JUMP, TaskType.DISK_ACCESS);
		} else {
			if (testerType == TesterType.CMW) {
				startTask(RfTaskName.PREPARE_CMW_DATABASES, TaskType.DISK_ACCESS);
			} else {
				startTask(RfTaskName.PREPARE_CMU_DATABASES, TaskType.DISK_ACCESS);
			}
		}
		changePanel(selectProfile);
		menu.disableJumpMenuItem();
	}

	private void selectProfileButtonPress(String name) {
		switch (name) {
			case "SelectProfile saveButton":
				showNotification(RfNotice.SELECT_PROFILE_SAVE);
				break;
			case "SelectProfile backButton":
				if (noChangeOccurred(null)) {
					if (selectProfile.isJumped()) {
						selectReportBatch();
					} else {
						selectTask();
					}
				}
				break;
			case "SelectProfile deleteButton":
				showNotification(RfNotice.SELECT_PROFILE_DELETE);
				break;
			case "SelectProfile editButton":
				editProfile();
				break;
			case "SelectProfile createButton":
				createProfile();
				break;
			case "SelectProfile repositionButton":
				repositionProfile();
				break;
			case "SelectProfile tacButton":
				selectTac();
				break;
			case "SelectProfile revertButton":
				showNotification(RfNotice.SELECT_PROFILE_REVERT);
				break;
			case "SelectProfile removeButton":
				selectProfile.revertCompensation();
				break;
			case "SelectProfile reloadButton":
				showNotification(RfNotice.SELECT_PROFILE_RELOAD);
				break;
		}
	}

	// SelectProfilePart

	private void selectPart(String part) {
		selectProfilePart.openPart(part, createProfile.getSelectedBox(), createProfile.getTesterType());
		changePanel(selectProfilePart);
	}

	private void selectPartButtonPress(String name) {
		switch (name) {
			case "SelectProfilePart backButton":
				if (noChangeOccurred(null)) {
					changePanel(createProfile);
				}
				break;
			case "SelectProfilePart newButton":
				selectProfilePart.addPart();
				break;
			case "SelectProfilePart deleteButton":
				selectProfilePart.deletePart();
				break;
			case "SelectProfilePart saveButton":
				selectProfilePart.writeParts();
				createProfile.updateControls();
				showNotification(RfNotice.SELECT_PROFILE_PART_SAVE);
				changePanel(createProfile);
				break;
		}
	}

	// SelectProfileUsage

	private void selectProfileUsage() {
		selectProfileUsage.updateProfileUsages(selectSourceFolder.getSelectedFolders(), selectReportBatch.getStations());
		changePanel(selectProfileUsage);
		menu.disableJumpMenuItem();
	}

	private void selectProfileUsageButtonPress(String name) {
		switch (name) {
			case "SelectProfileUsage backButton":
				selectTask();
				break;
			case "SelectProfileUsage primaryButton":
				selectProfileUsage.moveToPrimary();
				break;
			case "SelectProfileUsage legacyButton":
				selectProfileUsage.moveToLegacy();
				break;
			case "SelectProfileUsage saveButton":
				showNotification(RfNotice.SELECT_PROFILE_USAGE_CONFIRM);
				break;
		}
	}

	// SelectReport

	private void selectReport() {
		startTask(RfTaskName.PREPARE_REPORTS, TaskType.DISK_ACCESS);
		changePanel(selectReport);
		menu.enableJumpMenuItem("SelectEquipment");
	}

	private void selectReportButtonPress(String name) {
		switch (name) {
			case "SelectReport backButton":
				selectTask();
				break;
			case "SelectReport deleteButton":
				showNotification(RfNotice.SELECT_REPORT_DELETE);
				break;
			case "SelectReport updateButton":
				selectReport();
				break;
			case "SelectReport openButton":
				selectReport.openReport();
				break;
			case "SelectReport searchButton":
				searchReport(selectReport.getKeyword(), "select_report");
				break;
			case "SelectReport copyPathButton":
				selectReport.reportPathToClipboard();
				break;
			case "SelectReport copyImeiButton":
				selectReport.reportImeiToClipboard();
				break;
			case "SelectReport displayButton":
				selectReport.setDisplayOption();
				break;
			case "SelectReport filterButton":
				showNotification(RfNotice.SELECT_REPORT_FILTER, selectReport.getSelectedFilterOption(), selectReport.getFilterOptions());
				break;
		}
	}

	// SelectReportBatch

	private void selectReportBatch() {
		if (selectSourceFolder.isRebuildNeeded()) {
			selectSourceFolder.setRebuildNeeded(false);
			if (selectSourceFolder.getTesterType() == TesterType.CMU) {
				startTask(RfTaskName.PREPARE_CMU_STATIONS, TaskType.DISK_ACCESS);
			} else {
				startTask(RfTaskName.PREPARE_CMW_STATIONS, TaskType.DISK_ACCESS);
			}
		}
		changePanel(selectReportBatch);
		menu.disableJumpMenuItem();
	}

	private void selectReportBatchButtonPress(String name) {
		switch (name) {
			case "SelectReportBatch graphButton":
				showGraph(selectReportBatch.getSelectedBatch(), false);
				break;
			case "SelectReportBatch transferButton":
				selectProfile(null, true, false);
				break;
			case "SelectReportBatch backButton":
				selectTask();
				break;
			case "SelectReportBatch jumpButton":
				selectProfile(null, false, true);
				break;
			case "SelectReportBatch checkButton":
				showNotification(RfNotice.SELECT_REPORT_BATCH_DATABASE_CHECK);
				break;
			case "SelectReportBatch conditionButton":
				selectReportBatch.setSortMethod("condition");
				selectReportBatch.updateReportBatches();
				break;
			case "SelectReportBatch passableButton":
				selectReportBatch.setSortMethod("passable");
				selectReportBatch.updateReportBatches();
				break;
			case "SelectReportBatch nameButton":
				selectReportBatch.setSortMethod("name");
				selectReportBatch.updateReportBatches();
				break;
			case "SelectReportBatch modificationButton":
				selectReportBatch.setSortMethod("modification");
				selectReportBatch.updateReportBatches();
				break;
			case "SelectReportBatch rebuildButton":
				selectSourceFolder.useSelection();
				selectReportBatch();
				break;
		}
	}

	// SelectSourceFolder

	private void selectSourceFolder() {
		if (selectSourceFolder.isScanned()) {
			selectSourceFolder.clearLists();
			startTask(RfTaskName.PREPARE_SOURCE_FOLDERS, TaskType.DISK_ACCESS);
		}
		changePanel(selectSourceFolder);
		menu.disableJumpMenuItem();
	}

	private void selectSourceFolderButtonPress(String name) {
		switch (name) {
			case "SelectSourceFolder addButton":
				selectSourceFolder.addSelection();
				break;
			case "SelectSourceFolder removeButton":
				selectSourceFolder.removeSelection();
				break;
			case "SelectSourceFolder clearButton":
				selectSourceFolder.clearLists();
				break;
			case "SelectSourceFolder useButton":
				selectSourceFolder.useSelection();
				selectTask.setSourceNumber(selectSourceFolder.getSelectedFolders().size(), selectSourceFolder.getTesterType());
				selectTask.enableReportBatchButton();
				selectTask.disableProfileUsageButton();
				menu.enableBatchMenuItem();
				menu.disableUsageMenuItem();
				selectTask();
				break;
			case "SelectSourceFolder limitButton":
				selectSourceFolder.changePassableLimit();
				break;
			case "SelectSourceFolder rescanButton":
				startTask(RfTaskName.PREPARE_SOURCE_FOLDERS, TaskType.DISK_ACCESS);
				break;
			case "SelectSourceFolder cmuButton":
				selectSourceFolder.changeTesterType(TesterType.CMU);
				startTask(RfTaskName.PREPARE_SOURCE_FOLDERS, TaskType.DISK_ACCESS);
				break;
			case "SelectSourceFolder cmwButton":
				selectSourceFolder.changeTesterType(TesterType.CMW);
				startTask(RfTaskName.PREPARE_SOURCE_FOLDERS, TaskType.DISK_ACCESS);
				break;
			case "SelectSourceFolder backButton":
				selectTask();
				break;
		}
	}

	// SelectTac

	private void selectTac() {
		selectTac.openTacList(selectProfile.getSelectedCmwProfile().getTacList(), selectProfile.getSelectedCmwProfile().getStoreTac(), selectProfile.getSelectedCmwProfile().getName());
		changePanel(selectTac);
	}

	private void selectTacButtonPress(String name) {
		switch (name) {
			case "SelectTac backButton":
				if (noChangeOccurred(null)) {
					changePanel(selectProfile);
				}

				break;
			case "SelectTac newButton":
				selectTac.addTac();
				break;
			case "SelectTac deleteButton":
				selectTac.deleteTac();
				break;
			case "SelectTac fixButton":
				selectProfile.getSelectedCmwProfile().updateTacList(selectTac.getTacList());
				selectProfile.setSelectedProfile(selectProfile.getSelectedProfile());
				selectProfile.updateSaveButton();
				changePanel(selectProfile);
				break;
		}
	}

	// SelectTask

	private void selectTask() {
		changePanel(selectTask);
		menu.disableJumpMenuItem();
	}

	private void selectTaskButtonPress(String name) {
		switch (name) {
			case "SelectTask updateButton":
				SystemHelper.runJar("Updater.jar");
				System.exit(0);
			case "SelectTask manualButton":
				SystemHelper.openFile(assistant.getLocalConfig().getNetworkFolder() + "\\Documentation\\Használati útmutató.pdf");
				break;
			case "SelectTask sourceButton":
				selectSourceFolder();
				break;
			case "SelectTask reportBatchButton":
				selectReportBatch();
				break;
			case "SelectTask cmuProfileButton":
				selectProfile(TesterType.CMU, false, false);
				break;
			case "SelectTask profileUsageButton":
				selectProfileUsage();
				break;
			case "SelectTask cmwProfileButton":
				selectProfile(TesterType.CMW, false, false);
				break;
			case "SelectTask syncProfileButton":
				syncProfile();
				break;
			case "SelectTask backupButton":
				selectBackup();
				break;
			case "SelectTask logButton":
				showLog();
				break;
			case "SelectTask operatorButton":
				selectTask.setMode("operator");
				break;
			case "SelectTask configButton":
				editConfig();
				break;
			case "SelectTask supportButton":
				// comment out "supportEnabled = true;" to enable password
				//supportEnabled = true;
				if (supportEnabled) {
					selectTask.setMode("support");
				} else {
					showNotification(RfNotice.SELECT_TASK_SUPPORT_PASSWORD);
				}
				if (!selectSourceFolder.getSelectedFolders().isEmpty()) {
					selectTask.enableReportBatchButton();
					if (!selectSourceFolder.isRebuildNeeded()) {
						selectTask.enableProfileUsageButton();
						selectTask.setSourceNumber(0, null);
					} else {
						selectTask.setSourceNumber(selectSourceFolder.getSelectedFolders().size(), selectSourceFolder.getTesterType());
					}
				}
				break;
			case "SelectTask equipmentInfoButton":
				selectEquipment();
				break;
			case "SelectTask reportListButton":
				selectReport();
				break;
			case "SelectTask reportSearchButton":
				searchReport("", "select_task");
				break;
			case "SelectTask equipmentListButton":
				editEquipment();
				break;
			case "SelectTask backButton":
				if (selectTask.getMode().equals("operator") || selectTask.getMode().equals("support")) {
					selectTask.setMode("select");
				} else {
					System.exit(0);
				}
				break;
		}
	}

	// ShowGraph

	private void showGraph(ReportBatch reportBatch, boolean operatorMode) {
		showGraph.displayGraph(reportBatch, operatorMode);
		changePanel(showGraph);
		menu.disableJumpMenuItem();
	}

	private void showGraphButtonPress(String name) {
		switch (name) {
			case "ShowGraph backButton":
				if (!showGraph.isOperatorMode()) {
					if (noChangeOccurred(null)) {
						selectReportBatch();
					}
				} else {
					selectEquipment();
				}
				break;
			case "ShowGraph fixButton":
				selectReportBatch.getSelectedBatch().transferCompensation(showGraph.getCompensation());
				selectReportBatch.checkTransferButton();
				selectReportBatch();
				break;
			case "ShowGraph resetButton":
				showGraph.resetCompensation();
				showGraph.reDraw();
				break;
			case "ShowGraph directionButton":
				showGraph.switchDirection();
				showGraph.reDraw();
				break;
			case "ShowGraph wcdma1Button":
				showGraph.setGraphType("wcdma1");
				showGraph.reDraw();
				break;
			case "ShowGraph wcdma8Button":
				showGraph.setGraphType("wcdma8");
				showGraph.reDraw();
				break;
			case "ShowGraph gsm900Button":
				showGraph.setGraphType("gsm900");
				showGraph.reDraw();
				break;
			case "ShowGraph gsm1800Button":
				showGraph.setGraphType("gsm1800");
				showGraph.reDraw();
				break;
			case "ShowGraph lte1Button":
				showGraph.setGraphType("lte1");
				showGraph.reDraw();
				break;
			case "ShowGraph lte3Button":
				showGraph.setGraphType("lte3");
				showGraph.reDraw();
				break;
			case "ShowGraph lte7Button":
				showGraph.setGraphType("lte7");
				showGraph.reDraw();
				break;
			case "ShowGraph lte20Button":
				showGraph.setGraphType("lte20");
				showGraph.reDraw();
				break;
			case "ShowGraph bandButton":
				showGraph.switchBands();
				showGraph.reDraw();
				break;
			case "ShowGraph lowUpButton":
				showGraph.addCompensation(0, 1);
				showGraph.reDraw();
				break;
			case "ShowGraph midUpButton":
				showGraph.addCompensation(1, 1);
				showGraph.reDraw();
				break;
			case "ShowGraph highUpButton":
				showGraph.addCompensation(2, 1);
				showGraph.reDraw();
				break;
			case "ShowGraph lowDownButton":
				showGraph.addCompensation(0, -1);
				showGraph.reDraw();
				break;
			case "ShowGraph midDownButton":
				showGraph.addCompensation(1, -1);
				showGraph.reDraw();
				break;
			case "ShowGraph highDownButton":
				showGraph.addCompensation(2, -1);
				showGraph.reDraw();
				break;
		}
	}

	// ShowLog

	private void showLog() {
		showLog.refreshLog("profile");
		changePanel(showLog);
		menu.disableJumpMenuItem();
	}

	private void showLogButtonPress(String name) {
		switch (name) {
			case "ShowLog backButton":
				selectTask();
				break;
			case "ShowLog modeButton":
				showLog.toggleMode();
				break;
			case "ShowLog valueButton":
				showLog.toggleShowValues();
				break;
		}
	}

	// SyncProfile

	private void syncProfile() {
		startTask(RfTaskName.PREPARE_DATABASE_SYNC, TaskType.DISK_ACCESS);
		changePanel(syncProfile);
		menu.disableJumpMenuItem();
	}

	private void syncProfileButtonPress(String name) {
		switch (name) {
			case "SyncProfile transferButton":
				startTask(RfTaskName.WRITE_PROFILES_TO_TARGET, TaskType.DISK_ACCESS);
				break;
			case "SyncProfile transferAllButton":
				startTask(RfTaskName.WRITE_PROFILES_TO_ALL, TaskType.DISK_ACCESS);
				break;
			case "SyncProfile backButton":
				selectTask();
				break;
		}
	}

	// Other thread

	@Override
	public void runTask(AssTask task) {
		if (task.getType() == TaskType.DISK_ACCESS) {
			menu.disableMenu();
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			switch ((RfTaskName) task.getName()) {
				case PREPARE_SOURCE_FOLDERS:
					selectSourceFolder.prepareSourceFolders();
					menu.enableUsageMenuItem();
					menu.disableUsageMenuItem();
					break;
				case PREPARE_BACKUP_CREATE:
					selectBackup.createBackup(notification.getSelectedOption());
					break;
				case PREPARE_BACKUP_RESTORE:
					selectBackup.restoreBackup();
					break;
				case PREPARE_DATABASES_WITH_TRANSFER:
					selectProfile.prepareDatabases(selectReportBatch.getSelectedBatch().getTesterType() == TesterType.CMW ?
							TesterType.CMW : TesterType.CMU, selectReportBatch.getCompensations(), null);
					break;
				case PREPARE_DATABASES_WITH_JUMP:
					selectProfile.prepareDatabases(selectReportBatch.getSelectedBatch().getTesterType() == TesterType.CMW ?
							TesterType.CMW : TesterType.CMU, null, selectReportBatch.getSelectedBatch().getProfile());
					break;
				case PREPARE_CMW_DATABASES:
					selectProfile.prepareDatabases(TesterType.CMW, null, null);
					break;
				case PREPARE_CMU_DATABASES:
					selectProfile.prepareDatabases(TesterType.CMU, null, null);
					break;
				case PREPARE_REPORTS:
					selectReport.prepareReports();
					break;
				case PREPARE_CMU_STATIONS:
					selectReportBatch.prepareStations(selectSourceFolder.getSelectedFolders(), selectSourceFolder.getPassableLimit(), TesterType.CMU);
					selectTask.setSourceNumber(0, null);
					selectTask.enableProfileUsageButton();
					menu.enableUsageMenuItem();
					break;
				case PREPARE_CMW_STATIONS:
					selectReportBatch.prepareStations(selectSourceFolder.getSelectedFolders(), selectSourceFolder.getPassableLimit(), TesterType.CMW);
					selectTask.setSourceNumber(0, null);
					break;
				case PREPARE_EQUIPMENTS:
					selectEquipment.prepareEquipments();
					break;
				case PREPARE_EQUIPMENTS_EDIT:
					editEquipment.prepareEquipments(false);
					break;
				case PREPARE_EQUIPMENTS_EDIT_CHECKED:
					editEquipment.prepareEquipments(true);
				case PREPARE_PROFILE_CREATE:
					if (createProfile.createProfile()) {
						selectTask();
					}
					break;
				case PREPARE_DATABASE_SAVE:
					selectProfile.saveDatabases();
					if (selectReportBatch.getStationCount() > 0) {
						selectReportBatch.removeSavedElements();
						selectReportBatch.updateReportBatches();
						selectReportBatch.checkTransferButton();
					}
					selectTask();
					break;
				case PREPARE_REPORT_SEARCH:
					searchReport.prepareReports();
					if (!task.getParameter().isEmpty()) {
						searchReport.setKeyword(task.getParameter());
					}
					searchReport.searchReport();
					break;
				case PREPARE_PRELOAD:
					selectTask.disableButtons();
					assistant.preloadResources();
					selectTask.enableButtons();
					break;
				case SHOW_OPERATOR_GRAPH:
					selectEquipment.disableControlsForGraph();
					String selectedOption = (notification.getSelectedOption());
					if (selectedOption != null) {
						int serial = Integer.parseInt(selectedOption.substring(4));
						TesterType testerType = selectedOption.startsWith("CMW") ? TesterType.CMW : TesterType.CMU;
						ReportBatch reportBatch = selectReportBatch.getSpecificReportBatch(serial, selectEquipment.getSelectedEquipment().getName(),
								selectSourceFolder.getGeneratedSelection(testerType, OPERATOR_GRAPH_INTERVAL), OPERATOR_GRAPH_LIMIT, testerType);
						if (reportBatch != null) {
							if (reportBatch.getUsableCount() > 0) {
								showGraph(reportBatch, true);
							} else {
								showNotification(RfNotice.SELECT_EQUIPMENT_NO_VALID_REPORT);
							}
						} else {
							showNotification(RfNotice.SELECT_EQUIPMENT_NO_REPORT);
						}
					}
					selectEquipment.enableControlsForGraph();
					break;
				case WRITE_PROFILES_TO_TARGET:
					syncProfile.writeProfilesToTarget();
					showNotification(RfNotice.SYNC_PROFILE_SAVE);
					selectTask();
					break;
				case WRITE_PROFILES_TO_ALL:
					syncProfile.writeProfilesToAll();
					showNotification(RfNotice.SYNC_PROFILE_SAVE);
					selectTask();
					break;
				case PREPARE_DATABASE_SYNC:
					syncProfile.updateList();
					break;
			}
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			menu.enableMenu();
		} else if (task.getName() == CommonTaskName.CLOSE_NOTIFICATION) {
			String taskId = task.getParameter();
			if (notification != null) {
				if (notification.getValue().equals(taskId)) {
					closeNotification();
				}
			}
		}
	}

	// Event handling

	@Override
	public void actionPerformed(ActionEvent event) {

		Component sourceComponent = (Component) event.getSource();
		String componentName = sourceComponent.getName();

		// ActionEvent handling

		if (event.getActionCommand().equals("prepare_equipments_done")) {
			selectEquipment.updateFilterBox();
		} else if (event.getActionCommand().equals("backup_create_done")) {
			selectTask();
			showNotification(RfNotice.SELECT_BACKUP_CREATE_DONE);
		} else if (event.getActionCommand().equals("backup_restore_done")) {
			selectTask();
			showNotification(RfNotice.SELECT_BACKUP_RESTORE_DONE);
		} else if (event.getActionCommand().equals("backup_restore_failed")) {
			selectTask();
		}

		// AppComboBox handling

		else if (sourceComponent instanceof AssComboBox) {
			switch (componentName) {
				case "CreateProfile boxBox":
					createProfile.updatePositionBox();
					break;
				case "CreateProfile positionBox":
				case "CreateProfile scriptBox":
					createProfile.checkSaveButton();
					break;
				case "SelectEquipment manufacturerBox":
					selectEquipment.filterList();
					break;
			}
		}

		//	AppMenuItem handling

		else if (sourceComponent instanceof AssMenuItem) {
			switch (componentName) {
				case "exitMenuItem":
					System.exit(0);
				case "updateMenuItem":
					SystemHelper.runJar("Updater.jar");
					System.exit(0);
				case "helpMenuItem":
					SystemHelper.openFile(assistant.getLocalConfig().getNetworkFolder() + "\\Documentation\\Használati útmutató.pdf");
					break;
				case "networkMenuItem":
					showNotification(RfNotice.NETWORK_FOLDER_CONFIG, assistant.getLocalConfig().getNetworkFolder());
					break;
				default:
					if (noChangeOccurred(componentName)) {
						selectPanel(componentName);
					}
					break;
			}
		}

		// AppButton handling

		else if (sourceComponent instanceof AssButton) {
			switch (TextHelper.getFirstWord(componentName)) {
				case "AppNotification":
					notificationButtonPress(componentName);
					break;
				case "SelectReportBatch":
					selectReportBatchButtonPress(componentName);
					break;
				case "ShowLog":
					showLogButtonPress(componentName);
					break;
				case "SelectProfile":
					selectProfileButtonPress(componentName);
					break;
				case "SelectProfileUsage":
					selectProfileUsageButtonPress(componentName);
					break;
				case "SelectTac":
					selectTacButtonPress(componentName);
					break;
				case "EditProfile":
					editProfileButtonPress(componentName);
					break;
				case "CreateProfile":
					createProfileButtonPress(componentName);
					break;
				case "SelectProfilePart":
					selectPartButtonPress(componentName);
					break;
				case "ShowGraph":
					showGraphButtonPress(componentName);
					break;
				case "SelectTask":
					selectTaskButtonPress(componentName);
					break;
				case "SelectReport":
					selectReportButtonPress(componentName);
					break;
				case "SearchReport":
					searchReportButtonPress(componentName);
					break;
				case "SelectEquipment":
					selectEquipmentButtonPress(componentName);
					break;
				case "EditEquipment":
					editEquipmentButtonPress(componentName);
					break;
				case "EditConfig":
					editConfigButtonPress(componentName);
					break;
				case "SelectSourceFolder":
					selectSourceFolderButtonPress(componentName);
					break;
				case "SyncProfile":
					syncProfileButtonPress(componentName);
					break;
				case "SelectBackup":
					selectBackupButtonPress(componentName);
					break;
			}
		}
	}

	// List handling

	@Override
	public void valueChanged(ListSelectionEvent listEvent) {
		AssList<?> sourceComponent = (AssList<?>) listEvent.getSource();
		// hasFocus() ignores extra third event on double click from another component
		if (!listEvent.getValueIsAdjusting() && sourceComponent.hasFocus()) {
			switch (sourceComponent.getName()) {
				case "SelectReportBatch stationList":
					selectReportBatch.setSelectedStation();
					break;
				case "SelectReportBatch reportBatchList":
					selectReportBatch.setSelectedReportBatch();
					break;
				case "SelectProfile databaseList":
					selectProfile.setSelectedDatabase(null);
					break;
				case "SelectProfile profileList":
					selectProfile.setSelectedProfile(null);
					break;
				case "SelectProfilePart partList":
					selectProfilePart.setSelectedPart();
					break;
				case "SelectProfileUsage primaryProfileList":
					selectProfileUsage.setSelectedProfileUsage(true);
					break;
				case "SelectProfileUsage legacyProfileList":
					selectProfileUsage.setSelectedProfileUsage(false);
					break;
				case "SelectSourceFolder sourceFolderList":
					selectSourceFolder.enableAdding();
					break;
				case "SelectSourceFolder selectedFolderList":
					selectSourceFolder.enableRemoving();
					break;
				case "SyncProfile sourceDatabaseList":
					syncProfile.setSourceDatabase();
					break;
				case "SyncProfile targetDatabaseList":
					syncProfile.setTargetDatabase();
					break;
				case "SelectTac tacList":
					selectTac.setSelectedTac();
					break;
				case "SelectEquipment equipmentList":
					selectEquipment.setSelectedEquipment(null);
					break;
				case "EditEquipment equipmentList":
					editEquipment.setSelectedEquipment();
					break;
				case "SelectBackup cmuBackupList":
					selectBackup.setSelectedBackup(TesterType.CMU);
					break;
				case "SelectBackup cmwBackupList":
					selectBackup.setSelectedBackup(TesterType.CMW);
					break;
				case "SelectBackup profileList":
					selectBackup.setSelectedProfile();
					break;
			}
		}
	}

	// Mouse handling

	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getSource() instanceof AssList) {
			AssList<?> sourceList = (AssList<?>) event.getSource();
			if (event.getClickCount() == 2 && !sourceList.isSelectionEmpty()) {
				switch (sourceList.getName()) {
					case "SelectSourceFolder sourceFolderList": {
						selectSourceFolder.addSelection();
						break;
					}
					case "SelectSourceFolder selectedFolderList": {
						selectSourceFolder.removeSelection();
						break;
					}
					case "SelectProfileUsage primaryProfileList": {
						selectProfileUsage.moveToLegacy();
						break;
					}
					case "SelectProfileUsage legacyProfileList": {
						selectProfileUsage.moveToPrimary();
						break;
					}
					case "SelectReportBatch reportBatchList": {
						showGraph(selectReportBatch.getSelectedBatch(), false);
						break;
					}
					case "SelectReportBatch reportList": {
						assistant.openReport((CmuReport) sourceList.getSelectedValue());
						break;
					}
					case "SelectProfile databaseList": {
						selectProfile.setSelectedDatabase(null);
						break;
					}
					case "SelectReportBatch stationList": {
						selectReportBatch.setSelectedStation();
						break;
					}
					case "SelectReport reportList": {
						selectReport.openReport();
						break;
					}
					case "SearchReport reportList": {
						searchReport.openReport();
						break;
					}
					case "SelectBackup cmuBackupList": {
						selectBackup.setSelectedBackup(TesterType.CMU);
						break;
					}
					case "SelectBackup cmwBackupList": {
						selectBackup.setSelectedBackup(TesterType.CMW);
						break;
					}
				}
			}
		} else if (event.getSource() instanceof AssLabel) {
			AssLabel sourceLabel = (AssLabel) event.getSource();
			switch (sourceLabel.getName()) {
				case "cmuPositionImageLabel":
					if (sourceLabel.getIcon() != null) {
						showNotification(RfNotice.SELECT_EQUIPMENT_CMU_IMAGE, selectEquipment.getCmuImage() + ".png");
					}
					break;
				case "cmwPositionImageLabel":
					if (sourceLabel.getIcon() != null) {
						showNotification(RfNotice.SELECT_EQUIPMENT_CMW_IMAGE, selectEquipment.getCmwImage() + ".png");
					}
					break;
				case "cmuPositionValueLabel":
				case "cmwPositionValueLabel":
					String info = "";
					if (sourceLabel.getText().contains("FF")) {
						info = "fejjel felfele";
					} else if (sourceLabel.getText().contains("FL")) {
						info = "fejjel lefele";
					} else if (sourceLabel.getText().contains("FJ")) {
						info = "fejjel jobbra";
					} else if (sourceLabel.getText().contains("FB")) {
						info = "fejjel balra";
					} else if (sourceLabel.getText().contains("--")) {
						info = "egyéni\n\n(Lásd: műszerre vonatkozó információ)";
					}
					if (!info.isBlank()) {
						showNotification(RfNotice.SELECT_EQUIPMENT_POSITION);
						notification.changeText("A készülék elhelyezése: " + info);
					}
					break;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		switch (((JComponent) event.getSource()).getName()) {
			case "supportTaskMenu":
				if (!supportEnabled) {
					if (!networkFailure) {
						showNotification(RfNotice.SELECT_TASK_SUPPORT_PASSWORD, "menu_clicked");
					}
				}
				break;
			case "ShowGraph pointGraph":
				showGraph.selectPoints(event.getX(), event.getY());
				break;
		}
	}

	// Keyboard handling

	@Override
	public void keyPressed(KeyEvent event) {
		JComponent sourceComponent = (JComponent) event.getSource();
		int keyCode = event.getKeyCode();
		switch (sourceComponent.getName()) {
			case "SelectEquipment searchField":
				if (keyCode == KeyEvent.VK_DOWN) {
					selectEquipment.focusEquipmentList();
				}
				break;
			case "SelectEquipment equipmentList":
				if (keyCode == KeyEvent.VK_UP) {
					if (((AssList<?>) sourceComponent).getSelectedIndex() == 0) {
						selectEquipment.focusSearchField();
					}
				}
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		String componentName = ((JComponent) event.getSource()).getName();
		int keyCode = event.getKeyCode();
		switch (componentName) {
			case "SearchReport searchField":
				if (keyCode == KeyEvent.VK_ENTER) {
					searchReport.searchReport();
				}
				break;
			case "SelectReport searchField":
				if (keyCode == KeyEvent.VK_ENTER) {
					selectReport.searchReport();
				}
				break;
			case "SelectEquipment searchField":
				if (keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN) {
					selectEquipment.filterList();
				}
				break;
			case "EditEquipment searchField":
				editEquipment.filterList();
				break;
			default:
				if (componentName.contains("EditEquipment")) {
					if (keyCode == KeyEvent.VK_ENTER) {
						editEquipment.modifyEquipment();
					}
				}
		}
	}

	// Focus handling

	@Override
	public void windowGainedFocus(WindowEvent event) {
		super.windowGainedFocus(event);
		switch (this.getContentPane().getName()) {
			case "SearchReport":
				searchReport.focusSearchField();
				break;
			case "SelectReport":
				selectReport.focusSearchField();
				break;
			case "SelectEquipment":
				selectEquipment.focusSearchField();
				selectEquipment.selectSearchText();
				break;
		}
	}

	@Override
	public void focusGained(FocusEvent event) {
		String componentName = ((JComponent) event.getSource()).getName();
		if (componentName.equals("SelectEquipment searchField")) {
			selectEquipment.selectSearchText();
		}
	}
}
