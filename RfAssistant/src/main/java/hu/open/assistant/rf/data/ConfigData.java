package hu.open.assistant.rf.data;

import hu.open.assistant.rf.model.report.limits.CmuReportLimits;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.commons.data.IniParser;
import hu.open.assistant.commons.util.CodingHelper;
import hu.open.assistant.rf.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class which reads and writes program configuration from or to the disk. Local and global configuration options
 * are stored separately in INI format. On corrupted configuration the missing options will be replaced with default
 * values defined in constants.
 */
public class ConfigData {

	private static final String DATAFILE = "config.ini";
	private static final String DATAFILE_TEST = "config_test.ini";

	private static final List<String> VALID_FILTER_OPTIONS = Arrays.asList("all_combined", "all_cmw", "all_cmu");
	private static final List<String> VALID_DISPLAY_OPTIONS = Arrays.asList("basic", "full");
	private static final List<String> VALID_SOURCE_OPTIONS = Arrays.asList("cmu", "cmw");
	private static final double DEFAULT_MIN_VERSION = 1.0;
	private static final int DEFAULT_VALUE = 10;
	private static final String DEFAULT_PASSWORD = "123";
	private static final String DEFAULT_MANUFACTURER = "Test";
	private static final String DEFAULT_POSITIONS = "CmuBox.A1,CmwBox.A1";
	private static final String DEFAULT_SCRIPTS = "Box1.Script1,Box2.Script2";
	private static final String DEFAULT_CMU_REPORT_FOLDER = "\\Reports\\cmu_autosave";
	private static final String DEFAULT_CMW_REPORT_FOLDER = "\\Reports\\cmw_autosave";
	private static final String DEFAULT_CMU_CACHE_FOLDER = "\\CmuReportCache";
	private static final String DEFAULT_CMW_CACHE_FOLDER = "\\CmwReportCache";
	private static final String DEFAULT_CMU_DATABASE_FOLDER = "\\Profiles\\CMUgo Folders\\KnownTAC";
	private static final String DEFAULT_CMW_DATABASE_FOLDER = "\\Profiles\\CMWrun Folders";
	private static final String DEFAULT_CMU_SHORTCUT_FOLDER = "\\Profiles\\CMUgo Folders\\Scripts";
	private static final int PASSWORD_SHIFT_VALUE = 0;

	private final IniParser iniParser;
	private String networkFolder;
	private boolean parseError;
	private final boolean testMode;

	public ConfigData(IniParser iniParser, boolean testMode) {
		this.iniParser = iniParser;
		this.testMode = testMode;
	}

	public boolean hasParseErrorOccurred() {
		return parseError;
	}

	public void writeDefaultGlobalConfig(String networkFolder) {
		this.networkFolder = networkFolder;
		Config defaultConfig = new Config();
		defaultConfig.setMinCmuScriptVersion(DEFAULT_MIN_VERSION);
		defaultConfig.setMinCmwScriptVersion(DEFAULT_MIN_VERSION);
		defaultConfig.setMinVersion(DEFAULT_MIN_VERSION);
		defaultConfig.setDefaultValue(DEFAULT_VALUE);
		defaultConfig.setSupportPassword(DEFAULT_PASSWORD);
		defaultConfig.setDefaultManufacturer(DEFAULT_MANUFACTURER);
		defaultConfig.setDefaultPositions(DEFAULT_POSITIONS);
		defaultConfig.setDefaultScripts(DEFAULT_SCRIPTS);
		defaultConfig.setCmuReportPath(networkFolder + DEFAULT_CMU_REPORT_FOLDER);
		defaultConfig.setCmwReportPath(networkFolder + DEFAULT_CMW_REPORT_FOLDER);
		defaultConfig.setCmuDatabasePath(networkFolder + DEFAULT_CMU_DATABASE_FOLDER);
		defaultConfig.setCmwDatabasePath(networkFolder + DEFAULT_CMW_DATABASE_FOLDER);
		defaultConfig.setCmuCachePath(networkFolder + DEFAULT_CMU_CACHE_FOLDER);
		defaultConfig.setCmwCachePath(networkFolder + DEFAULT_CMW_CACHE_FOLDER);
		defaultConfig.setCmuShortcutPath(networkFolder + DEFAULT_CMU_SHORTCUT_FOLDER);
		defaultConfig.setCmuLimits(new CmuReportLimits());
		defaultConfig.setCmwLimits(new CmwReportLimits());
		writeGlobalConfig(defaultConfig);
	}

	public void writeGlobalConfig(Config config) {
		CmuReportLimits cmuLimits = config.getCmuLimits();
		CmwReportLimits cmwLimits = config.getCmwLimits();
		Map<String, String> settingsMap = new HashMap<>();
		settingsMap.put("minCmuScriptVersion", String.valueOf(config.getMinCmuScriptVersion()));
		settingsMap.put("minCmwScriptVersion", String.valueOf(config.getMinCmwScriptVersion()));
		settingsMap.put("minVersion", String.valueOf(config.getMinVersion()));
		settingsMap.put("defaultValue", String.valueOf(config.getDefaultValue()));
		settingsMap.put("supportPassword", CodingHelper.stringToAsciCesarCode(config.getSupportPassword(), PASSWORD_SHIFT_VALUE));
		settingsMap.put("defaultManufacturer", config.getDefaultManufacturer());
		settingsMap.put("defaultPositions", config.getDefaultPositions());
		settingsMap.put("defaultScripts", config.getDefaultScripts());
		settingsMap.put("cmuReportPath", config.getCmuReportPath());
		settingsMap.put("cmwReportPath", config.getCmwReportPath());
		settingsMap.put("cmuDatabasePath", config.getCmuDatabasePath());
		settingsMap.put("cmwDatabasePath", config.getCmwDatabasePath());
		settingsMap.put("cmuCachePath", config.getCmuCachePath());
		settingsMap.put("cmwCachePath", config.getCmwCachePath());
		settingsMap.put("cmuShortcutPath", config.getCmuShortcutPath());
		settingsMap.put("cmuWcdma1Limits", cmuLimits.getWcdma1TxMin() + "," + cmuLimits.getWcdma1TxMax() + "," +
				cmuLimits.getWcdma1RxMin() + "," + cmuLimits.getWcdma1RxMax());
		settingsMap.put("cmuWcdma8Limits", cmuLimits.getWcdma8TxMin() + "," + cmuLimits.getWcdma8TxMax() + "," +
				cmuLimits.getWcdma8RxMin() + "," + cmuLimits.getWcdma8RxMax());
		settingsMap.put("cmuGsm900Limits", cmuLimits.getGsm900TxMin() + "," + cmuLimits.getGsm900TxMax() + "," +
				cmuLimits.getGsm900RxMin() + "," + cmuLimits.getGsm900RxMax());
		settingsMap.put("cmuGsm1800Limits", cmuLimits.getGsm1800TxMin() + "," + cmuLimits.getGsm1800TxMax() + "," +
				cmuLimits.getGsm1800RxMin() + "," + cmuLimits.getGsm1800RxMax());
		settingsMap.put("cmwWcdma1Limits", cmwLimits.getWcdma1TxMin() + "," + cmwLimits.getWcdma1TxMax() + "," +
				cmwLimits.getWcdma1RxMin() + "," + cmwLimits.getWcdma1RxMax());
		settingsMap.put("cmwWcdma8Limits", cmwLimits.getWcdma8TxMin() + "," + cmwLimits.getWcdma8TxMax() + "," +
				cmwLimits.getWcdma8RxMin() + "," + cmwLimits.getWcdma8RxMax());
		settingsMap.put("cmwGsm900Limits", cmwLimits.getGsm900TxMin() + "," + cmwLimits.getGsm900TxMax() + "," +
				cmwLimits.getGsm900RxMin() + "," + cmwLimits.getGsm900RxMax());
		settingsMap.put("cmwGsm1800Limits", cmwLimits.getGsm1800TxMin() + "," + cmwLimits.getGsm1800TxMax() + "," +
				cmwLimits.getGsm1800RxMin() + "," + cmwLimits.getGsm1800RxMax());
		settingsMap.put("cmwLte1Limits", cmwLimits.getLte1TxMin() + "," + cmwLimits.getLte1TxMax() + "," +
				cmwLimits.getLte1RxMin() + "," + cmwLimits.getLte1RxMax());
		settingsMap.put("cmwLte3Limits", cmwLimits.getLte3TxMin() + "," + cmwLimits.getLte3TxMax() + "," +
				cmwLimits.getLte3RxMin() + "," + cmwLimits.getLte3RxMax());
		settingsMap.put("cmwLte7Limits", cmwLimits.getLte7TxMin() + "," + cmwLimits.getLte7TxMax() + "," +
				cmwLimits.getLte7RxMin() + "," + cmwLimits.getLte7RxMax());
		settingsMap.put("cmwLte20Limits", cmwLimits.getLte20TxMin() + "," + cmwLimits.getLte20TxMax() + "," +
				cmwLimits.getLte20RxMin() + "," + cmwLimits.getLte20RxMax());
		iniParser.writeIniFile(networkFolder + "\\" + DATAFILE, settingsMap);
	}

	public Config readGlobalConfig() {
		Config config = new Config();
		CmuReportLimits cmuLimits = new CmuReportLimits();
		CmwReportLimits cmwLimits = new CmwReportLimits();
		Integer[] values;
		Map<String, String> settingsMap = iniParser.readIniFile(networkFolder + "\\" + DATAFILE);
		for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
			switch (entry.getKey()) {
				case "minCmuScriptVersion":
					try {
						config.setMinCmuScriptVersion((Double.parseDouble(entry.getValue())));
					} catch (NumberFormatException exception) {
						parseError = true;
					}
					break;
				case "minCmwScriptVersion":
					try {
						config.setMinCmwScriptVersion((Double.parseDouble(entry.getValue())));
					} catch (NumberFormatException exception) {
						parseError = true;
					}
					break;
				case "minVersion":
					try {
						config.setMinVersion((Double.parseDouble(entry.getValue())));
					} catch (NumberFormatException exception) {
						parseError = true;
					}
					break;
				case "defaultValue":
					try {
						config.setDefaultValue(Integer.parseInt(entry.getValue()));
					} catch (NumberFormatException exception) {
						parseError = true;
					}
					break;
				case "supportPassword":
					config.setSupportPassword(CodingHelper.asciCesarCodeToString(entry.getValue(), PASSWORD_SHIFT_VALUE));
					break;
				case "defaultManufacturer":
					config.setDefaultManufacturer(entry.getValue());
					break;
				case "defaultPositions":
					config.setDefaultPositions(entry.getValue());
					break;
				case "defaultScripts":
					config.setDefaultScripts(entry.getValue());
					break;
				case "cmuReportPath":
					config.setCmuReportPath(entry.getValue());
					break;
				case "cmuDatabasePath":
					config.setCmuDatabasePath(entry.getValue());
					break;
				case "cmuCachePath":
					config.setCmuCachePath(entry.getValue());
					break;
				case "cmwCachePath":
					config.setCmwCachePath(entry.getValue());
					break;
				case "cmuShortcutPath":
					config.setCmuShortcutPath(entry.getValue());
					break;
				case "cmwReportPath":
					config.setCmwReportPath(entry.getValue());
					break;
				case "cmwDatabasePath":
					config.setCmwDatabasePath(entry.getValue());
					break;
				case "cmuWcdma1Limits":
					values = readValues(entry.getValue());
					cmuLimits.setWcdma1TxMin(values[0]);
					cmuLimits.setWcdma1TxMax(values[1]);
					cmuLimits.setWcdma1RxMin(values[2]);
					cmuLimits.setWcdma1RxMax(values[3]);
					break;
				case "cmuWcdma8Limits":
					values = readValues(entry.getValue());
					cmuLimits.setWcdma8TxMin(values[0]);
					cmuLimits.setWcdma8TxMax(values[1]);
					cmuLimits.setWcdma8RxMin(values[2]);
					cmuLimits.setWcdma8RxMax(values[3]);
					break;
				case "cmuGsm900Limits":
					values = readValues(entry.getValue());
					cmuLimits.setGsm900TxMin(values[0]);
					cmuLimits.setGsm900TxMax(values[1]);
					cmuLimits.setGsm900RxMin(values[2]);
					cmuLimits.setGsm900RxMax(values[3]);
					break;
				case "cmuGsm1800Limits":
					values = readValues(entry.getValue());
					cmuLimits.setGsm1800TxMin(values[0]);
					cmuLimits.setGsm1800TxMax(values[1]);
					cmuLimits.setGsm1800RxMin(values[2]);
					cmuLimits.setGsm1800RxMax(values[3]);
					break;
				case "cmwWcdma1Limits":
					values = readValues(entry.getValue());
					cmwLimits.setWcdma1TxMin(values[0]);
					cmwLimits.setWcdma1TxMax(values[1]);
					cmwLimits.setWcdma1RxMin(values[2]);
					cmwLimits.setWcdma1RxMax(values[3]);
					break;
				case "cmwWcdma8Limits":
					values = readValues(entry.getValue());
					cmwLimits.setWcdma8TxMin(values[0]);
					cmwLimits.setWcdma8TxMax(values[1]);
					cmwLimits.setWcdma8RxMin(values[2]);
					cmwLimits.setWcdma8RxMax(values[3]);
					break;
				case "cmwGsm900Limits": {
					values = readValues(entry.getValue());
					cmwLimits.setGsm900TxMin(values[0]);
					cmwLimits.setGsm900TxMax(values[1]);
					cmwLimits.setGsm900RxMin(values[2]);
					cmwLimits.setGsm900RxMax(values[3]);
					break;
				}
				case "cmwGsm1800Limits": {
					values = readValues(entry.getValue());
					cmwLimits.setGsm1800TxMin(values[0]);
					cmwLimits.setGsm1800TxMax(values[1]);
					cmwLimits.setGsm1800RxMin(values[2]);
					cmwLimits.setGsm1800RxMax(values[3]);
					break;
				}
				case "cmwLte1Limits": {
					values = readValues(entry.getValue());
					cmwLimits.setLte1TxMin(values[0]);
					cmwLimits.setLte1TxMax(values[1]);
					cmwLimits.setLte1RxMin(values[2]);
					cmwLimits.setLte1RxMax(values[3]);
					break;
				}
				case "cmwLte3Limits": {
					values = readValues(entry.getValue());
					cmwLimits.setLte3TxMin(values[0]);
					cmwLimits.setLte3TxMax(values[1]);
					cmwLimits.setLte3RxMin(values[2]);
					cmwLimits.setLte3RxMax(values[3]);
					break;
				}
				case "cmwLte7Limits": {
					values = readValues(entry.getValue());
					cmwLimits.setLte7TxMin(values[0]);
					cmwLimits.setLte7TxMax(values[1]);
					cmwLimits.setLte7RxMin(values[2]);
					cmwLimits.setLte7RxMax(values[3]);
					break;
				}
				case "cmwLte20Limits": {
					values = readValues(entry.getValue());
					cmwLimits.setLte20TxMin(values[0]);
					cmwLimits.setLte20TxMax(values[1]);
					cmwLimits.setLte20RxMin(values[2]);
					cmwLimits.setLte20RxMax(values[3]);
					break;
				}
			}
		}
		config.setCmuLimits(cmuLimits);
		config.setCmwLimits(cmwLimits);
		if (config.getSupportPassword().isBlank()) {
			config.setSupportPassword(DEFAULT_PASSWORD);
		}
		return config;
	}

	private Integer[] readValues(String text) {
		try {
			return Arrays.stream(text.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
		} catch (NumberFormatException exception) {
			parseError = true;
			return new Integer[]{0, 0, 0, 0};
		}
	}

	public void writeLocalConfig(Config config) {
		Map<String, String> settingsMap = new HashMap<>();
		settingsMap.put("networkFolder", config.getNetworkFolder());
		settingsMap.put("filterOption", config.getFilterOption());
		settingsMap.put("displayOption", config.getDisplayOption());
		settingsMap.put("defaultSource", config.getDefaultSource());
		settingsMap.put("preloadCmu", String.valueOf(config.isPreloadCmu()));
		settingsMap.put("preloadCmw", String.valueOf(config.isPreloadCmw()));
		settingsMap.put("preloadReports", String.valueOf(config.isPreloadReports()));
		String dataFile = testMode ? DATAFILE_TEST : DATAFILE;
		iniParser.writeIniFile(dataFile, settingsMap);
	}

	public Config readLocalConfig() {
		Config config = new Config();
		String dataFile = testMode ? DATAFILE_TEST : DATAFILE;
		Map<String, String> settingsMap = iniParser.readIniFile(dataFile);
		for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
			switch (entry.getKey()) {
				case "networkFolder":
					config.setNetworkFolder(entry.getValue());
					networkFolder = config.getNetworkFolder();
					break;
				case "filterOption":
					if (VALID_FILTER_OPTIONS.contains(entry.getValue()) || isFilterOptionValid(entry.getValue())) {
						config.setFilterOption(entry.getValue());
					}
					break;
				case "displayOption":
					if (VALID_DISPLAY_OPTIONS.contains(entry.getValue())) {
						config.setDisplayOption(entry.getValue());
					}
					break;
				case "defaultSource":
					if (VALID_SOURCE_OPTIONS.contains(entry.getValue())) {
						config.setDefaultSource(entry.getValue());
					}
					break;
				case "preloadCmu":
					config.setPreloadCmu(Boolean.parseBoolean(entry.getValue()));
					break;
				case "preloadCmw":
					config.setPreloadCmw(Boolean.parseBoolean(entry.getValue()));
					break;
				case "preloadReports":
					config.setPreloadReports(Boolean.parseBoolean(entry.getValue()));
					break;
			}
		}
		if (config.getFilterOption().isEmpty()) {
			config.setFilterOption(VALID_FILTER_OPTIONS.get(0));
		}
		if (config.getDisplayOption().isEmpty()) {
			config.setDisplayOption(VALID_DISPLAY_OPTIONS.get(0));
		}
		if (config.getDefaultSource().isEmpty()) {
			config.setDefaultSource(VALID_SOURCE_OPTIONS.get(0));
		}
		return config;
	}

	private boolean isFilterOptionValid(String option) {
		String[] optionParts = option.split("_");
		if (optionParts.length == 2) {
			int serial;
			try {
				serial = Integer.parseInt(optionParts[1]);
			} catch (Exception numberFormatException) {
				serial = 0;
			}
			return serial > 0;
		}
		return false;
	}
}
