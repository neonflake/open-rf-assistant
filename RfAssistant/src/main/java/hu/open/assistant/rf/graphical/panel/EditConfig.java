package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssComboBox;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.rf.Config;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.rf.model.report.limits.CmuReportLimits;
import hu.open.assistant.rf.model.report.limits.ReportLimits;
import hu.open.assistant.rf.graphical.RfNotice;

import java.awt.Dimension;

/**
 * GUI for modifying the entire program configuration. The preload options are available in dropdown boxes. When admin
 * mode is enabled other categories become available using the side controls. Options range from limit settings, folder
 * settings and program behavior settings. Switching between options replaces some GUI elements for proper display
 * (includes labels, text fields and dropdown boxes). Most of the options must meet some formal requirements when set.
 */
public class EditConfig extends RfPanel {

	private static final Dimension LABEL_DIMENSION = new Dimension(200, 50);
	private static final Dimension SMALL_FIELD_DIMENSION = new Dimension(200, 50);
	private static final Dimension LARGE_FIELD_DIMENSION = new Dimension(400, 50);
	private static final int SMALL_TEXT_SIZE = 14;
	private static final int MEDIUM_TEXT_SIZE = 16;
	private static final int LARGE_TEXT_SIZE = 20;

	private final AssButton foldersButton;
	private final AssButton cmuLimitsButton;
	private final AssButton cmwLimitsButton;
	private final AssButton cmwLteLimitsButton;
	private final AssButton programConfigButton;
	private final AssButton preloadButton;
	private final AssLabel titleLabel;
	private final AssLabel minLabel;
	private final AssLabel maxLabel;
	private final AssLabel setting1Label;
	private final AssLabel setting2Label;
	private final AssLabel setting3Label;
	private final AssLabel setting4Label;
	private final AssLabel setting5Label;
	private final AssLabel setting6Label;
	private final AssLabel setting7Label;
	private final AssLabel setting8Label;
	private final CmuReportLimits cmuLimits;
	private final CmwReportLimits cmwLimits;
	private final Config globalConfig;
	private final Config localConfig;
	private AssComboBox preloadCmuBox;
	private AssComboBox preloadCmwBox;
	private AssComboBox preloadReportsBox;
	private AssTextField cmuReportField;
	private AssTextField cmwReportField;
	private AssTextField cmuDatabaseField;
	private AssTextField cmwDatabaseField;
	private AssTextField cmuShortcutField;
	private AssTextField cmuCacheField;
	private AssTextField cmwCacheField;
	private AssTextField minCmuScriptField;
	private AssTextField minCmwScriptField;
	private AssTextField minVersionField;
	private AssTextField defaultSourceField;
	private AssTextField defaultValueField;
	private AssTextField supportPasswordField;
	private AssTextField defaultManufacturerField;
	private AssTextField defaultPositionsField;
	private AssTextField defaultScriptsField;
	private AssTextField value1MinField;
	private AssTextField value2MinField;
	private AssTextField value3MinField;
	private AssTextField value4MinField;
	private AssTextField value5MinField;
	private AssTextField value6MinField;
	private AssTextField value7MinField;
	private AssTextField value8MinField;
	private AssTextField value1MaxField;
	private AssTextField value2MaxField;
	private AssTextField value3MaxField;
	private AssTextField value4MaxField;
	private AssTextField value5MaxField;
	private AssTextField value6MaxField;
	private AssTextField value7MaxField;
	private AssTextField value8MaxField;
	private String selectedConfig;
	private boolean supportEnabled;

	public EditConfig(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "EditConfig");
		//placer.enableDebug();
		foldersButton = new AssButton("EditConfig folderButton", "Mappák és jelszó", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		cmuLimitsButton = new AssButton("EditConfig cmuLimitButton", "CMU határértékek", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		cmwLimitsButton = new AssButton("EditConfig cmwLimitButton", "CMW határértékek", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		cmwLteLimitsButton = new AssButton("EditConfig cmwLteLimitButton", "CMW LTE határértékek", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		programConfigButton = new AssButton("EditConfig programConfigButton", "Működés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		preloadButton = new AssButton("EditConfig preloadButton", "Előtöltés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton saveButton = new AssButton("EditConfig saveButton", "Mentés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton backButton = new AssButton("EditConfig backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		titleLabel = new AssLabel("", TITLE_LABEL_TEXT_SIZE, new Dimension(TITLE_LABEL_DIMENSION));
		maxLabel = new AssLabel("Maximum", LARGE_TEXT_SIZE, LABEL_DIMENSION);
		minLabel = new AssLabel("Minimum", LARGE_TEXT_SIZE, LABEL_DIMENSION);
		setting1Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting2Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting3Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting4Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting5Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting6Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting7Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		setting8Label = new AssLabel("", SMALL_TEXT_SIZE, LABEL_DIMENSION);
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addComponent(foldersButton, 4, 1, 1, 1);
		placer.addComponent(minLabel, 2, 2, 1, 1);
		placer.addComponent(maxLabel, 3, 2, 1, 1);
		placer.addComponent(cmuLimitsButton, 4, 2, 1, 1);
		placer.addComponent(setting1Label, 1, 3, 1, 1);
		placer.addComponent(cmwLimitsButton, 4, 3, 1, 1);
		placer.addComponent(setting2Label, 1, 4, 1, 1);
		placer.addComponent(cmwLteLimitsButton, 4, 4, 1, 1);
		placer.addComponent(setting3Label, 1, 5, 1, 1);
		placer.addComponent(programConfigButton,4,5,1,1);
		placer.addComponent(setting4Label, 1, 6, 1, 1);
		placer.addComponent(preloadButton, 4, 6, 1, 1);
		placer.addComponent(setting5Label, 1, 7, 1, 1);
		placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
		placer.addComponent(setting6Label, 1, 8, 1, 1);
		placer.addComponent(saveButton,4,8,1,1);
		placer.addComponent(setting7Label,1,9,1,1);
		placer.addComponent(backButton,4,9,1,1);
		placer.addComponent(setting8Label,1,10,1,1);
		placer.addImageComponent(logoImage,4,10,1,1);
		selectedConfig = "";
		cmuLimits = assistant.getCmuLimits();
		cmwLimits = assistant.getCmwLimits();
		globalConfig = assistant.getGlobalConfig();
		localConfig = assistant.getLocalConfig();
	}

	public String getSelectedConfig() {
		return selectedConfig;
	}

	public void enableSupport() {
		supportEnabled = true;
		foldersButton.setEnabled(true);
		cmuLimitsButton.setEnabled(true);
		cmwLimitsButton.setEnabled(true);
		cmwLteLimitsButton.setEnabled(true);
		programConfigButton.setEnabled(true);
	}

	public void showConfig(String selectedConfig) {
		this.selectedConfig = selectedConfig;
		removeFolderFields();
		removeConfigFields();
		removeValueFields();
		removeConfigBoxes();
		if (selectedConfig.contains("limits")) {
			showValueFields();
			minLabel.setText("Minimum");
			maxLabel.setText("Maximum");
			foldersButton.setEnabled(true);
			programConfigButton.setEnabled(true);
			switch (selectedConfig) {
				case "cmu_limits":
					titleLabel.setText("CMU határértékek megadása");
					setting1Label.setText("WCDMA 1 TX");
					setting2Label.setText("WCDMA 1 RX");
					setting3Label.setText("WCDMA 8 TX");
					setting4Label.setText("WCDMA 8 RX");
					setting5Label.setText("GSM 900 TX");
					setting6Label.setText("GSM 900 RX");
					setting7Label.setText("GSM 1800 TX");
					setting8Label.setText("GSM 1800 RX");
					value1MinField.setText("" + cmuLimits.getWcdma1TxMin());
					value1MaxField.setText("" + cmuLimits.getWcdma1TxMax());
					value2MinField.setText("" + cmuLimits.getWcdma1RxMin());
					value2MaxField.setText("" + cmuLimits.getWcdma1RxMax());
					value3MinField.setText("" + cmuLimits.getWcdma8TxMin());
					value3MaxField.setText("" + cmuLimits.getWcdma8TxMax());
					value4MinField.setText("" + cmuLimits.getWcdma8RxMin());
					value4MaxField.setText("" + cmuLimits.getWcdma8RxMax());
					value5MinField.setText("" + cmuLimits.getGsm900TxMin());
					value5MaxField.setText("" + cmuLimits.getGsm900TxMax());
					value6MinField.setText("" + cmuLimits.getGsm900RxMin());
					value6MaxField.setText("" + cmuLimits.getGsm900RxMax());
					value7MinField.setText("" + cmuLimits.getGsm1800TxMin());
					value7MaxField.setText("" + cmuLimits.getGsm1800TxMax());
					value8MinField.setText("" + cmuLimits.getGsm1800RxMin());
					value8MaxField.setText("" + cmuLimits.getGsm1800RxMax());
					cmuLimitsButton.setEnabled(false);
					cmwLimitsButton.setEnabled(true);
					cmwLteLimitsButton.setEnabled(true);
					preloadButton.setEnabled(true);
					break;
				case "cmw_limits":
					titleLabel.setText("CMW határértékek megadása");
					setting1Label.setText("WCDMA 1 TX");
					setting2Label.setText("WCDMA 1 RX");
					setting3Label.setText("WCDMA 8 TX");
					setting4Label.setText("WCDMA 8 RX");
					setting5Label.setText("GSM 900 TX");
					setting6Label.setText("GSM 900 RX");
					setting7Label.setText("GSM 1800 TX");
					setting8Label.setText("GSM 1800 RX");
					value1MinField.setText("" + cmwLimits.getWcdma1TxMin());
					value1MaxField.setText("" + cmwLimits.getWcdma1TxMax());
					value2MinField.setText("" + cmwLimits.getWcdma1RxMin());
					value2MaxField.setText("" + cmwLimits.getWcdma1RxMax());
					value3MinField.setText("" + cmwLimits.getWcdma8TxMin());
					value3MaxField.setText("" + cmwLimits.getWcdma8TxMax());
					value4MinField.setText("" + cmwLimits.getWcdma8RxMin());
					value4MaxField.setText("" + cmwLimits.getWcdma8RxMax());
					value5MinField.setText("" + cmwLimits.getGsm900TxMin());
					value5MaxField.setText("" + cmwLimits.getGsm900TxMax());
					value6MinField.setText("" + cmwLimits.getGsm900RxMin());
					value6MaxField.setText("" + cmwLimits.getGsm900RxMax());
					value7MinField.setText("" + cmwLimits.getGsm1800TxMin());
					value7MaxField.setText("" + cmwLimits.getGsm1800TxMax());
					value8MinField.setText("" + cmwLimits.getGsm1800RxMin());
					value8MaxField.setText("" + cmwLimits.getGsm1800RxMax());
					cmwLimitsButton.setEnabled(false);
					cmuLimitsButton.setEnabled(true);
					cmwLteLimitsButton.setEnabled(true);
					preloadButton.setEnabled(true);
					break;
				case "cmw_lte_limits":
					titleLabel.setText("CMW LTE határértékek megadása");
					setting1Label.setText("LTE 1 TX");
					setting2Label.setText("LTE 1 RX");
					setting3Label.setText("LTE 3 TX");
					setting4Label.setText("LTE 3 RX");
					setting5Label.setText("LTE 7 TX");
					setting6Label.setText("LTE 7 RX");
					setting7Label.setText("LTE 20 TX");
					setting8Label.setText("LTE 20 RX");
					value1MinField.setText("" + cmwLimits.getLte1TxMin());
					value1MaxField.setText("" + cmwLimits.getLte1TxMax());
					value2MinField.setText("" + cmwLimits.getLte1RxMin());
					value2MaxField.setText("" + cmwLimits.getLte1RxMax());
					value3MinField.setText("" + cmwLimits.getLte3TxMin());
					value3MaxField.setText("" + cmwLimits.getLte3TxMax());
					value4MinField.setText("" + cmwLimits.getLte3RxMin());
					value4MaxField.setText("" + cmwLimits.getLte3RxMax());
					value5MinField.setText("" + cmwLimits.getLte7TxMin());
					value5MaxField.setText("" + cmwLimits.getLte7TxMax());
					value6MinField.setText("" + cmwLimits.getLte7RxMin());
					value6MaxField.setText("" + cmwLimits.getLte7RxMax());
					value7MinField.setText("" + cmwLimits.getLte20TxMin());
					value7MaxField.setText("" + cmwLimits.getLte20TxMax());
					value8MinField.setText("" + cmwLimits.getLte20RxMin());
					value8MaxField.setText("" + cmwLimits.getLte20RxMax());
					cmwLteLimitsButton.setEnabled(false);
					cmuLimitsButton.setEnabled(true);
					cmwLimitsButton.setEnabled(true);
					preloadButton.setEnabled(true);
					break;
			}
		} else {
			if (supportEnabled) {
				cmuLimitsButton.setEnabled(true);
				cmwLimitsButton.setEnabled(true);
				cmwLteLimitsButton.setEnabled(true);
			} else {
				cmuLimitsButton.setEnabled(false);
				cmwLimitsButton.setEnabled(false);
				cmwLteLimitsButton.setEnabled(false);
			}
			switch (selectedConfig) {
				case "folder_paths":
					showFolderFields();
					titleLabel.setText("Programhoz szükséges mappák és jelszó");
					setting1Label.setText("CMU riportok");
					setting2Label.setText("CMW riportok");
					setting3Label.setText("CMU adatbázis");
					setting4Label.setText("CMW adatbázis");
					setting5Label.setText("CMU script társítás");
					setting6Label.setText("CMU riport cache");
					setting7Label.setText("CMW riport cache");
					setting8Label.setText("Karbantartói jelszó");
					cmuReportField.setText(globalConfig.getCmuReportPath());
					cmwReportField.setText(globalConfig.getCmwReportPath());
					cmuDatabaseField.setText(globalConfig.getCmuDatabasePath());
					cmwDatabaseField.setText(globalConfig.getCmwDatabasePath());
					cmuShortcutField.setText(globalConfig.getCmuShortcutPath());
					cmuCacheField.setText(globalConfig.getCmuCachePath());
					cmwCacheField.setText(globalConfig.getCmwCachePath());
					supportPasswordField.setText(globalConfig.getSupportPassword());
					if (supportEnabled) {
						foldersButton.setEnabled(false);
						programConfigButton.setEnabled(true);
						preloadButton.setEnabled(true);
					}
					break;
				case "program_config":
					showConfigFields();
					titleLabel.setText("Működéssel kapcsolatos beállítások");
					setting1Label.setText("Min. CMU script verzió");
					setting2Label.setText("Min. CMW script verzió");
					setting3Label.setText("Alapértelmezett forrás");
					setting4Label.setText("Profil alaphelyzet értéke");
					setting5Label.setText("Min. program verzió");
					setting6Label.setText("Preferált gyártó");
					setting7Label.setText("Preferált CMU, CMW pozíció");
					setting8Label.setText("Preferált script");
					minCmuScriptField.setText("" + globalConfig.getMinCmuScriptVersion());
					minCmwScriptField.setText("" + globalConfig.getMinCmwScriptVersion());
					defaultSourceField.setText(assistant.getLocalConfig().getDefaultSource());
					defaultValueField.setText("" + globalConfig.getDefaultValue());
					minVersionField.setText("" + globalConfig.getMinVersion());
					defaultManufacturerField.setText(globalConfig.getDefaultManufacturer());
					defaultPositionsField.setText(globalConfig.getDefaultPositions());
					defaultScriptsField.setText(globalConfig.getDefaultScripts());
					if (supportEnabled) {
						foldersButton.setEnabled(true);
						programConfigButton.setEnabled(false);
						preloadButton.setEnabled(true);
					}
					break;
				case "preload_config":
					showConfigBoxes();
					titleLabel.setText("Erőforrások előtöltése induláskor");
					setting1Label.setText("CMU adatbázis");
					setting2Label.setText("CMW adatbázis");
					setting3Label.setText("Tesztriportok (90 nap)");
					setting4Label.setText("");
					setting5Label.setText("");
					setting6Label.setText("");
					setting7Label.setText("");
					setting8Label.setText("");
					preloadCmuBox.setSelectedIndex(localConfig.isPreloadCmu() ? 1 : 0);
					preloadCmwBox.setSelectedIndex(localConfig.isPreloadCmw() ? 1 : 0);
					preloadReportsBox.setSelectedIndex(localConfig.isPreloadReports() ? 1 : 0);
					if (supportEnabled) {
						foldersButton.setEnabled(true);
						programConfigButton.setEnabled(true);
					} else {
						foldersButton.setEnabled(false);
						programConfigButton.setEnabled(false);
					}
					preloadButton.setEnabled(false);
					break;
			}
		}
		this.reDraw();
	}

	public boolean isInputValid() {
		switch (selectedConfig) {
			case "program_config":
				String minCmuScript = minCmuScriptField.getText();
				String minCmwScript = minCmwScriptField.getText();
				String defaultSource = defaultSourceField.getText();
				String defaultValue = defaultValueField.getText();
				String minVersion = minVersionField.getText();
				String defaultManufacturer = defaultManufacturerField.getText();
				String defaultPositions = defaultPositionsField.getText();
				String defaultScripts = defaultScriptsField.getText();
				if (minCmuScript.isBlank() || minCmwScript.isBlank() || defaultSource.isBlank() ||
						defaultValue.isBlank() || minVersion.isBlank() || defaultManufacturer.isBlank() ||
						defaultPositions.isBlank() || defaultScripts.isBlank()) {
					window.showNotification(RfNotice.GENERIC_EMPTY_FIELD);
					return false;
				}
				if (ValidationHelper.isInvalidDouble(minCmuScript) || ValidationHelper.isInvalidDouble(minCmwScript) ||
						ValidationHelper.isInvalidDouble(minVersion) || ValidationHelper.isInvalidInteger(defaultValue)) {
					window.showNotification(RfNotice.EDIT_CONFIG_VERSION_INVALID);
					return false;
				}
				if (!(defaultSourceField.getText().equals("cmu") || defaultSourceField.getText().equals("cmw"))) {
					window.showNotification(RfNotice.EDIT_CONFIG_SOURCE_INVALID);
					return false;
				}
				if (ValidationHelper.hasForbiddenCharacter(defaultManufacturer) || ValidationHelper.hasForbiddenCharacter(defaultPositions) ||
						ValidationHelper.hasForbiddenCharacter(defaultScripts)) {
					window.showNotification(RfNotice.GENERIC_INVALID_CHARACTER);
					return false;
				}
				break;
			case "folder_paths":
				if (cmuReportField.getText().isBlank() || cmwReportField.getText().isBlank() || cmuDatabaseField.getText().isBlank() ||
						cmwDatabaseField.getText().isBlank() || cmuShortcutField.getText().isBlank() || cmuCacheField.getText().isBlank() ||
						cmwCacheField.getText().isBlank() || supportPasswordField.getText().isBlank()) {
					window.showNotification(RfNotice.GENERIC_EMPTY_FIELD);
					return false;
				}
				if (supportPasswordField.getText().length() < 3) {
					window.showNotification(RfNotice.EDIT_CONFIG_PASSWORD_SHORT);
					return false;
				}
				break;
			default:
				String value1Min = value1MinField.getText();
				String value2Min = value2MinField.getText();
				String value3Min = value3MinField.getText();
				String value4Min = value4MinField.getText();
				String value5Min = value5MinField.getText();
				String value6Min = value6MinField.getText();
				String value7Min = value7MinField.getText();
				String value8Min = value8MinField.getText();
				String value1Max = value1MaxField.getText();
				String value2Max = value2MaxField.getText();
				String value3Max = value3MaxField.getText();
				String value4Max = value4MaxField.getText();
				String value5Max = value5MaxField.getText();
				String value6Max = value6MaxField.getText();
				String value7Max = value7MaxField.getText();
				String value8Max = value8MaxField.getText();
				if (value1Min.isBlank() || value2Min.isBlank() || value3Min.isBlank() || value4Min.isBlank() ||
						value5Min.isBlank() || value6Min.isBlank() || value7Min.isBlank() || value8Min.isBlank()) {
					window.showNotification(RfNotice.GENERIC_EMPTY_FIELD);
					return false;
				}
				if (ValidationHelper.isInvalidDouble(value1Min) || ValidationHelper.isInvalidDouble(value2Min) ||
						ValidationHelper.isInvalidDouble(value3Min) || ValidationHelper.isInvalidDouble(value4Min) ||
						ValidationHelper.isInvalidDouble(value5Min) || ValidationHelper.isInvalidDouble(value6Min) ||
						ValidationHelper.isInvalidDouble(value7Min) || ValidationHelper.isInvalidDouble(value8Min) ||
						ValidationHelper.isInvalidDouble(value1Max) || ValidationHelper.isInvalidDouble(value2Max) ||
						ValidationHelper.isInvalidDouble(value3Max) || ValidationHelper.isInvalidDouble(value4Max) ||
						ValidationHelper.isInvalidDouble(value5Max) || ValidationHelper.isInvalidDouble(value6Max) ||
						ValidationHelper.isInvalidDouble(value7Max) || ValidationHelper.isInvalidDouble(value8Max)) {
					window.showNotification(RfNotice.GENERIC_NOT_NUMBER);
					return false;
				}
		}
		return true;
	}

	public void updateProgramConfig() {
		if (isInputValid()) {
			globalConfig.setDefaultManufacturer(defaultManufacturerField.getText());
			globalConfig.setDefaultPositions(defaultPositionsField.getText());
			globalConfig.setDefaultScripts(defaultScriptsField.getText());
			globalConfig.setMinCmuScriptVersion(Double.parseDouble(minCmuScriptField.getText()));
			globalConfig.setMinCmwScriptVersion(Double.parseDouble(minCmwScriptField.getText()));
			globalConfig.setMinVersion(Double.parseDouble(minVersionField.getText()));
			globalConfig.setDefaultValue(Integer.parseInt(defaultValueField.getText()));
			localConfig.setDefaultSource(defaultSourceField.getText());
			assistant.writeLocalConfig(localConfig);
			assistant.writeGlobalConfig(globalConfig);
			System.exit(0);
		}
	}

	public void updatePreloadConfig() {
		localConfig.setPreloadCmu(preloadCmuBox.getSelectedIndex() == 1);
		localConfig.setPreloadCmw(preloadCmwBox.getSelectedIndex() == 1);
		localConfig.setPreloadReports(preloadReportsBox.getSelectedIndex() == 1);
		assistant.writeLocalConfig(localConfig);
		System.exit(0);
	}

	public void updateFolderPaths() {
		if (isInputValid()) {
			globalConfig.setCmuReportPath(cmuReportField.getText());
			globalConfig.setCmwReportPath(cmwReportField.getText());
			globalConfig.setCmuDatabasePath(cmuDatabaseField.getText());
			globalConfig.setCmwDatabasePath(cmwDatabaseField.getText());
			globalConfig.setCmuShortcutPath(cmuShortcutField.getText());
			globalConfig.setCmuReportPath(cmuReportField.getText());
			globalConfig.setCmwReportPath(cmwReportField.getText());
			globalConfig.setCmuCachePath(cmuCacheField.getText());
			globalConfig.setCmwCachePath(cmwCacheField.getText());
			globalConfig.setSupportPassword(supportPasswordField.getText());
			assistant.writeGlobalConfig(globalConfig);
			System.exit(0);
		}
	}

	public void updateCmuLimits() {
		if (isInputValid()) {
			fillGsmAndWcdmaValues(cmuLimits);
			globalConfig.setCmuLimits(cmuLimits);
			assistant.writeGlobalConfig(globalConfig);
			System.exit(0);
		}
	}

	public void updateCmwLimits() {
		if (isInputValid()) {
			fillGsmAndWcdmaValues(cmwLimits);
			globalConfig.setCmwLimits(cmwLimits);
			assistant.writeGlobalConfig(globalConfig);
			System.exit(0);
		}
	}

	private void fillGsmAndWcdmaValues(ReportLimits limits) {
		limits.setWcdma1TxMin(value1MinField.getTextAsInteger());
		limits.setWcdma1TxMax(value1MaxField.getTextAsInteger());
		limits.setWcdma1RxMin(value2MinField.getTextAsInteger());
		limits.setWcdma1RxMax(value2MaxField.getTextAsInteger());
		limits.setWcdma8TxMin(value3MinField.getTextAsInteger());
		limits.setWcdma8TxMax(value3MaxField.getTextAsInteger());
		limits.setWcdma8RxMin(value4MinField.getTextAsInteger());
		limits.setWcdma8RxMax(value4MaxField.getTextAsInteger());
		limits.setGsm900TxMin(value5MinField.getTextAsInteger());
		limits.setGsm900TxMax(value5MaxField.getTextAsInteger());
		limits.setGsm900RxMin(value6MinField.getTextAsInteger());
		limits.setGsm900RxMax(value6MaxField.getTextAsInteger());
		limits.setGsm1800TxMin(value7MinField.getTextAsInteger());
		limits.setGsm1800TxMax(value7MaxField.getTextAsInteger());
		limits.setGsm1800RxMin(value8MinField.getTextAsInteger());
		limits.setGsm1800RxMax(value8MaxField.getTextAsInteger());
	}

	public void updateCmwLteLimits() {
		if (isInputValid()) {
			cmwLimits.setLte1TxMin(value1MinField.getTextAsInteger());
			cmwLimits.setLte1TxMax(value1MaxField.getTextAsInteger());
			cmwLimits.setLte1RxMin(value2MinField.getTextAsInteger());
			cmwLimits.setLte1RxMax(value2MaxField.getTextAsInteger());
			cmwLimits.setLte3TxMin(value3MinField.getTextAsInteger());
			cmwLimits.setLte3TxMax(value3MaxField.getTextAsInteger());
			cmwLimits.setLte3RxMin(value4MinField.getTextAsInteger());
			cmwLimits.setLte3RxMax(value4MaxField.getTextAsInteger());
			cmwLimits.setLte7TxMin(value5MinField.getTextAsInteger());
			cmwLimits.setLte7TxMax(value5MaxField.getTextAsInteger());
			cmwLimits.setLte7RxMin(value6MinField.getTextAsInteger());
			cmwLimits.setLte7RxMax(value6MaxField.getTextAsInteger());
			cmwLimits.setLte20TxMin(value7MinField.getTextAsInteger());
			cmwLimits.setLte20TxMax(value7MaxField.getTextAsInteger());
			cmwLimits.setLte20RxMin(value8MinField.getTextAsInteger());
			cmwLimits.setLte20RxMax(value8MaxField.getTextAsInteger());
			globalConfig.setCmwLimits(cmwLimits);
			assistant.writeGlobalConfig(globalConfig);
			System.exit(0);
		}
	}

	private void removeConfigFields() {
		placer.removeComponent("EditConfig minCmuScriptField");
		placer.removeComponent("EditConfig minCmwScriptField");
		placer.removeComponent("EditConfig defaultSourceField");
		placer.removeComponent("EditConfig defaultValueField");
		placer.removeComponent("EditConfig minVersionField");
		placer.removeComponent("EditConfig defaultManufacturerField");
		placer.removeComponent("EditConfig defaultPositionsField");
		placer.removeComponent("EditConfig defaultScriptField");
	}

	private void removeFolderFields() {
		placer.removeComponent("EditConfig cmuReportField");
		placer.removeComponent("EditConfig cmwReportField");
		placer.removeComponent("EditConfig cmuDatabaseField");
		placer.removeComponent("EditConfig cmwDatabaseField");
		placer.removeComponent("EditConfig cmuShortcutField");
		placer.removeComponent("EditConfig cmuCacheField");
		placer.removeComponent("EditConfig cmwCacheField");
		placer.removeComponent("EditConfig supportPasswordField");
	}

	private void removeConfigBoxes() {
		placer.removeComponent("EditConfig preloadCmuBox");
		placer.removeComponent("EditConfig preloadCmwBox");
		placer.removeComponent("EditConfig preloadReportsBox");
	}

	private void removeValueFields() {
		minLabel.setText("");
		maxLabel.setText("");
		placer.removeComponent("EditConfig value1MinField");
		placer.removeComponent("EditConfig value2MinField");
		placer.removeComponent("EditConfig value3MinField");
		placer.removeComponent("EditConfig value4MinField");
		placer.removeComponent("EditConfig value5MinField");
		placer.removeComponent("EditConfig value6MinField");
		placer.removeComponent("EditConfig value7MinField");
		placer.removeComponent("EditConfig value8MinField");
		placer.removeComponent("EditConfig value1MaxField");
		placer.removeComponent("EditConfig value2MaxField");
		placer.removeComponent("EditConfig value3MaxField");
		placer.removeComponent("EditConfig value4MaxField");
		placer.removeComponent("EditConfig value5MaxField");
		placer.removeComponent("EditConfig value6MaxField");
		placer.removeComponent("EditConfig value7MaxField");
		placer.removeComponent("EditConfig value8MaxField");
	}

	private void showValueFields() {
		minLabel.setText("Minimum");
		maxLabel.setText("Maximum");
		value1MinField = new AssTextField("EditConfig value1MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value2MinField = new AssTextField("EditConfig value2MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value3MinField = new AssTextField("EditConfig value3MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value4MinField = new AssTextField("EditConfig value4MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value5MinField = new AssTextField("EditConfig value5MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value6MinField = new AssTextField("EditConfig value6MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value7MinField = new AssTextField("EditConfig value7MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value8MinField = new AssTextField("EditConfig value8MinField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value1MaxField = new AssTextField("EditConfig value1MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value2MaxField = new AssTextField("EditConfig value2MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value3MaxField = new AssTextField("EditConfig value3MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value4MaxField = new AssTextField("EditConfig value4MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value5MaxField = new AssTextField("EditConfig value5MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value6MaxField = new AssTextField("EditConfig value6MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value7MaxField = new AssTextField("EditConfig value7MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		value8MaxField = new AssTextField("EditConfig value8MaxField", SMALL_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		placer.addComponent(value1MinField, 2, 3, 1, 1);
		placer.addComponent(value2MinField, 2, 4, 1, 1);
		placer.addComponent(value3MinField, 2, 5, 1, 1);
		placer.addComponent(value4MinField, 2, 6, 1, 1);
		placer.addComponent(value5MinField, 2, 7, 1, 1);
		placer.addComponent(value6MinField, 2, 8, 1, 1);
		placer.addComponent(value7MinField, 2, 9, 1, 1);
		placer.addComponent(value8MinField, 2, 10, 1, 1);
		placer.addComponent(value1MaxField, 3, 3, 1, 1);
		placer.addComponent(value2MaxField, 3, 4, 1, 1);
		placer.addComponent(value3MaxField, 3, 5, 1, 1);
		placer.addComponent(value4MaxField, 3, 6, 1, 1);
		placer.addComponent(value5MaxField, 3, 7, 1, 1);
		placer.addComponent(value6MaxField, 3, 8, 1, 1);
		placer.addComponent(value7MaxField, 3, 9, 1, 1);
		placer.addComponent(value8MaxField, 3, 10, 1, 1);
	}

	private void showConfigBoxes() {
		String[] options = new String[2];
		options[0] = "Kikapcsolva";
		options[1] = "Bekapcsolva";
		preloadCmuBox = new AssComboBox("EditConfig preloadCmuBox", options, MEDIUM_TEXT_SIZE, LARGE_FIELD_DIMENSION, listener);
		preloadCmwBox = new AssComboBox("EditConfig preloadCmwBox", options, MEDIUM_TEXT_SIZE, LARGE_FIELD_DIMENSION, listener);
		preloadReportsBox = new AssComboBox("EditConfig preloadReportsBox", options, MEDIUM_TEXT_SIZE, LARGE_FIELD_DIMENSION, listener);
		placer.addComponent(preloadCmuBox, 2, 3, 2, 1);
		placer.addComponent(preloadCmwBox, 2, 4, 2, 1);
		placer.addComponent(preloadReportsBox, 2, 5, 2, 1);
	}

	private void showConfigFields() {
		minCmuScriptField = new AssTextField("EditConfig minCmuScriptField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		minCmwScriptField = new AssTextField("EditConfig minCmwScriptField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		defaultSourceField = new AssTextField("EditConfig defaultSourceField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		defaultValueField = new AssTextField("EditConfig defaultValueField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		minVersionField = new AssTextField("EditConfig minVersionField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		defaultManufacturerField = new AssTextField("EditConfig defaultManufacturerField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		defaultPositionsField = new AssTextField("EditConfig defaultPositionsField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		defaultScriptsField = new AssTextField("EditConfig defaultScriptField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		placeFields(minCmuScriptField, minCmwScriptField, defaultSourceField, defaultValueField, minVersionField, defaultManufacturerField, defaultPositionsField, defaultScriptsField);
	}

	private void showFolderFields() {
		cmuReportField = new AssTextField("EditConfig cmuReportField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmwReportField = new AssTextField("EditConfig cmwReportField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmuDatabaseField = new AssTextField("EditConfig cmuDatabaseField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmwDatabaseField = new AssTextField("EditConfig cmwDatabaseField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmuShortcutField = new AssTextField("EditConfig cmuShortcutField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmuCacheField = new AssTextField("EditConfig cmuCacheField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmwCacheField = new AssTextField("EditConfig cmwCacheField", LARGE_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		supportPasswordField = new AssTextField("EditConfig supportPasswordField", LARGE_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
		placeFields(cmuReportField, cmwReportField, cmuDatabaseField, cmwDatabaseField, cmuShortcutField, cmuCacheField, cmwCacheField, supportPasswordField);
	}

	private void placeFields(AssTextField minCmuScriptField, AssTextField minCmwScriptField, AssTextField defaultSourceField, AssTextField defaultValueField, AssTextField minVersionField, AssTextField defaultManufacturerField, AssTextField defaultPositionsField, AssTextField defaultScriptField) {
		placer.addComponent(minCmuScriptField, 2, 3, 2, 1);
		placer.addComponent(minCmwScriptField, 2, 4, 2, 1);
		placer.addComponent(defaultSourceField, 2, 5, 2, 1);
		placer.addComponent(defaultValueField, 2, 6, 2, 1);
		placer.addComponent(minVersionField, 2, 7, 2, 1);
		placer.addComponent(defaultManufacturerField, 2, 8, 2, 1);
		placer.addComponent(defaultPositionsField, 2, 9, 2, 1);
		placer.addComponent(defaultScriptField, 2, 10, 2, 1);
	}
}
