package hu.open.assistant.rf;

import hu.open.assistant.rf.data.ConfigData;
import hu.open.assistant.rf.data.DatabaseBackupData;
import hu.open.assistant.rf.data.DatabaseLogData;
import hu.open.assistant.rf.data.EquipmentData;
import hu.open.assistant.rf.data.ProfileLogData;
import hu.open.assistant.rf.data.ReportCacheData;
import hu.open.assistant.rf.data.cmu.CmuProfileData;
import hu.open.assistant.rf.data.cmu.CmuReportData;
import hu.open.assistant.rf.data.cmu.ShortcutData;
import hu.open.assistant.rf.data.cmw.CmwProfileData;
import hu.open.assistant.rf.data.cmw.CmwReportData;
import hu.open.assistant.rf.filter.DatabaseLogBatchFilter;
import hu.open.assistant.rf.filter.EquipmentFilter;
import hu.open.assistant.rf.filter.ProfileLogBatchFilter;
import hu.open.assistant.rf.filter.ShortcutFilter;
import hu.open.assistant.rf.graphical.RfNotice;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.Equipment;
import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.database.CmuDatabase;
import hu.open.assistant.rf.model.database.CmwDatabase;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.database.backup.CmuDatabaseBackup;
import hu.open.assistant.rf.model.database.backup.CmwDatabaseBackup;
import hu.open.assistant.rf.model.database.backup.DatabaseBackup;
import hu.open.assistant.rf.model.log.DatabaseLog;
import hu.open.assistant.rf.model.log.ProfileLog;
import hu.open.assistant.rf.model.log.batch.DatabaseLogBatch;
import hu.open.assistant.rf.model.log.batch.ProfileLogBatch;
import hu.open.assistant.rf.model.log.event.ProfileLogEvent;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.rf.model.report.CmuReport;
import hu.open.assistant.rf.model.report.CmwReport;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.limits.CmuReportLimits;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.rf.model.station.CmuStation;
import hu.open.assistant.rf.model.station.CmwStation;
import hu.open.assistant.rf.model.station.Station;
import hu.open.assistant.commons.data.IniParser;
import hu.open.assistant.commons.data.JsonParser;
import hu.open.assistant.commons.data.UpdateHandler;
import hu.open.assistant.commons.util.SystemHelper;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.commons.data.CsvParser;
import hu.open.assistant.commons.data.FileHandler;
import hu.open.assistant.rf.data.ProfilePartsData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Data controller for the application as well as the main entry point. It holds various data classes responsible for
 * raw data processing (read and write) and all panels have access to the data controller to request read, write and
 * data related functions. It also contains equipment creation and handling, source folder listing, report handling,
 * log handling, shortcut handling, network folder creation and validation.
 */
public class RfAssistant {

	private static final double VERSION = 1.409;
	private static final boolean PASSWORD_ENABLED = true;

	private final CmuReportData cmuReportData;
	private final CmwReportData cmwReportData;
	private final CmwProfileData cmwProfileData;
	private final CmuProfileData cmuProfileData;
	private final ReportCacheData reportCacheData;
	private final ShortcutData shortcutData;
	private final ProfilePartsData profilePartsData;
	private final EquipmentData equipmentData;
	private final ProfileLogData profileLogData;
	private final DatabaseLogData databaseLogData;
	private final DatabaseBackupData databaseBackupData;
	private final ConfigData configData;
	private final CmuReportLimits cmuLimits;
	private final CmwReportLimits cmwLimits;
	private final int minYear;
	private final FileHandler fileHandler = new FileHandler();
	private final String networkFolder;
	private final RfWindow window;
	private ProfileParts cmuProfileParts;
	private String notificationBuffer = "";
	private Config localConfig;
	private final Config globalConfig;
	private boolean cmuDatabaseRefresh;
	private boolean cmwDatabaseRefresh;
	private boolean databaseCleanup;

	public static void main(String[] args) {
		new RfAssistant(false);
	}

	public RfAssistant(boolean testMode) {
		System.out.println("Starting... RfAssistant v" + VERSION);
		CsvParser csvParser = new CsvParser(fileHandler);
		IniParser iniParser = new IniParser(fileHandler);
		JsonParser jsonParser = new JsonParser(fileHandler);
		configData = new ConfigData(iniParser, testMode);
		localConfig = configData.readLocalConfig();
		networkFolder = localConfig.getNetworkFolder();
		globalConfig = configData.readGlobalConfig();
		profilePartsData = new ProfilePartsData(networkFolder + "\\Data", jsonParser);
		equipmentData = new EquipmentData(networkFolder + "\\Data", jsonParser);
		cmuProfileParts = profilePartsData.readProfileParts(TesterType.CMU);
		shortcutData = new ShortcutData(globalConfig.getCmuShortcutPath(), fileHandler, cmuProfileParts);
		cmuProfileData = new CmuProfileData(globalConfig.getCmuDatabasePath(), globalConfig.getDefaultValue(), fileHandler, cmuProfileParts);
		cmwProfileData = new CmwProfileData(globalConfig.getCmwDatabasePath(), globalConfig.getDefaultValue(), fileHandler, this);
		profileLogData = new ProfileLogData(networkFolder + "\\Data", csvParser);
		databaseLogData = new DatabaseLogData(networkFolder + "\\Data", csvParser);
		databaseBackupData = new DatabaseBackupData(networkFolder + "\\Data\\Backup", jsonParser, fileHandler, cmuProfileParts, readEquipments());
		cmuLimits = globalConfig.getCmuLimits();
		cmwLimits = globalConfig.getCmwLimits();
		cmuReportData = new CmuReportData(cmuLimits, fileHandler);
		cmwReportData = new CmwReportData(cmwLimits, fileHandler);
		reportCacheData = new ReportCacheData(cmuLimits, cmwLimits, fileHandler, csvParser);
		cmuDatabaseRefresh = true;
		cmwDatabaseRefresh = true;
		LocalDate date = LocalDate.now();
		date = date.minusYears(1);
		minYear = date.getYear();
		window = new RfWindow(this, testMode);
		if (!testMode) {
			window.startUpCheck();
		}
	}

	// Get methods

	public double getVersion() {
		return VERSION;
	}

	public boolean isPasswordEnabled() {
		return PASSWORD_ENABLED;
	}

	public FileHandler getFileHandler() {
		return fileHandler;
	}

	public CmuReportLimits getCmuLimits() {
		return cmuLimits;
	}

	public CmwReportLimits getCmwLimits() {
		return cmwLimits;
	}

	public Config getGlobalConfig() {
		return globalConfig;
	}

	public Config getLocalConfig() {
		return localConfig;
	}

	public String getNotificationBuffer() {
		return notificationBuffer;
	}

	public ProfileParts getCmuProfileParts() {
		return cmuProfileParts;
	}

	// Set methods

	public void setDatabaseCleanup(boolean databaseCleanup) {
		this.databaseCleanup = databaseCleanup;
	}

	public void setNotificationBuffer(String text) {
		notificationBuffer = text;
	}

	// Equipments

	public List<Equipment> readEquipments() {
		return equipmentData.readEquipments();
	}

	public void writeEquipments(List<Equipment> equipments) {
		equipmentData.writeEquipments(equipments);
		databaseBackupData.setEquipments(equipments);
	}

	private void clearEquipmentData(List<Profile> profiles) {
		List<Equipment> equipments = equipmentData.readEquipments();
		for (Profile profile : profiles) {
			Equipment equipment = EquipmentFilter.getEquipmentByName(equipments, profile.getName());
			if (equipment != null) {
				if (profile.getTesterType() == TesterType.CMW) {
					equipment.setCmwBox("");
					equipment.setCmwPosition("");
					equipment.setCmwPositionDetail("");
					equipment.setCmwPositionImage("");
				} else {
					equipment.setCmuBox("");
					equipment.setCmuPosition("");
					equipment.setCmuPositionDetail("");
					equipment.setCmuPositionImage("");
				}
			}
		}
		writeEquipments(equipments);
	}

	public void addEquipmentData(Profile profile) {
		List<Equipment> equipments = equipmentData.readEquipments();
		Equipment equipment = EquipmentFilter.getEquipmentByName(equipments, profile.getName());
		if (equipment != null) {
			fillDefaultPositionData(profile, equipment);
		} else {
			equipment = createEquipment(profile);
			fillDefaultPositionData(profile, equipment);
			equipments.add(equipment);
		}
		writeEquipments(equipments);
	}

	private Equipment createEquipment(Profile profile) {
		Equipment equipment = new Equipment();
		equipment.setType(profile.getType());
		equipment.setManufacturer(profile.getManufacturer());
		equipment.setUsage("normális");
		equipment.initDefaultValues();
		String[] supportedNetworks = {"2G", "3G", "4G"};
		equipment.setSupportedNetworks(supportedNetworks);
		return equipment;
	}

	private void addMissingEquipments(List<Equipment> equipments, List<Database> databases) {
		for (Database database : databases) {
			for (Profile profile : database.getProfiles()) {
				if (EquipmentFilter.getEquipmentByName(equipments, profile.getName()) == null) {
					equipments.add(createEquipment(profile));
				}
			}
		}
	}

	private void refreshEquipmentCmuInfo(Equipment equipment, Profile profile) {
		if (profile != null) {
			if (equipment.getCmuBox().isEmpty()) {
				equipment.setCmuBox(profile.getBox());
			}
			if (equipment.getCmuPosition().isEmpty() || !equipment.getCmuPosition().equals(profile.getPosition())) {
				equipment.setCmuPosition(profile.getPosition());
			}
			if (equipment.getCmuPositionDetail().isEmpty()) {
				equipment.setCmuPositionDetail("--");
			}
			if (equipment.getCmuPositionImage().isEmpty()) {
				equipment.setCmuPositionImage("auto");
			}
		} else {
			if (!equipment.getCmuBox().isEmpty()) {
				equipment.setCmuBox("");
			}
			if (!equipment.getCmuPosition().isEmpty()) {
				equipment.setCmuPosition("");
			}
			if (!equipment.getCmuPositionDetail().isEmpty()) {
				equipment.setCmuPositionDetail("");
			}
			if (!equipment.getCmuPositionImage().isEmpty()) {
				equipment.setCmuPositionImage("");
			}
		}
	}

	private void refreshEquipmentCmwInfo(Equipment equipment, Profile profile) {
		if (profile != null) {
			if (equipment.getCmwBox().isEmpty()) {
				equipment.setCmwBox(profile.getBox());
			}
			if (equipment.getCmwPosition().isEmpty() || !equipment.getCmwPosition().equals(profile.getPosition())) {
				equipment.setCmwPosition(profile.getPosition());
			}
			if (equipment.getCmwPositionDetail().isEmpty()) {
				equipment.setCmwPositionDetail("--");
			}
			if (equipment.getCmwPositionImage().isEmpty()) {
				equipment.setCmwPositionImage("auto-cmw");
			}
		} else {
			if (!equipment.getCmwBox().isEmpty()) {
				equipment.setCmwBox("");
			}
			if (!equipment.getCmwPosition().isEmpty()) {
				equipment.setCmwPosition("");
			}
			if (!equipment.getCmwPositionDetail().isEmpty()) {
				equipment.setCmwPositionDetail("");
			}
			if (!equipment.getCmwPositionImage().isEmpty()) {
				equipment.setCmwPositionImage("");
			}
		}
	}

	public List<Equipment> readCheckedEquipments() {
		List<Equipment> equipments = readEquipments();
		List<Database> cmuDatabases = readDatabases(TesterType.CMU);
		List<Database> cmwDatabases = readDatabases(TesterType.CMW);
		if (cmuDatabases.size() > 0 && cmwDatabases.size() > 0) {
			addMissingEquipments(equipments, cmuDatabases);
			addMissingEquipments(equipments, cmwDatabases);
			for (Equipment equipment : equipments) {
				refreshEquipmentCmuInfo(equipment, findProfileByName(cmuDatabases, equipment.getName()));
				refreshEquipmentCmwInfo(equipment, findProfileByName(cmwDatabases, equipment.getName()));
			}
		} else {
			if (cmuDatabases.size() == 0 && cmwDatabases.size() == 0) {
                window.showNotification(RfNotice.CMU_CMW_PROFILE_ERROR);
			} else if (cmuDatabases.size() == 0) {
                window.showNotification(RfNotice.CMU_PROFILE_ERROR);
			} else {
                window.showNotification(RfNotice.CMW_PROFILE_ERROR);
			}
		}
		return equipments;
	}

	private void updateEquipments(TesterType testerType, List<Equipment> backupEquipments) {
		List<Equipment> equipments = readEquipments();
		List<Database> databases = readDatabases(testerType);
		addMissingEquipments(equipments, databases);
		for (Equipment equipment : equipments) {
			Equipment backupEquipment = EquipmentFilter.getEquipmentByName(backupEquipments, equipment.getName());
			if (testerType == TesterType.CMU) {
				refreshEquipmentCmuInfo(equipment, findProfileByName(databases, equipment.getName()));
				if (backupEquipment != null) {
					equipment.setCmuPositionDetail(backupEquipment.getCmuPositionDetail());
				}
			} else {
				refreshEquipmentCmwInfo(equipment, findProfileByName(databases, equipment.getName()));
				if (backupEquipment != null) {
					equipment.setCmwPositionDetail(backupEquipment.getCmwPositionDetail());
				}
			}
		}
		writeEquipments(equipments);
	}

	private void fillDefaultPositionData(Profile profile, Equipment equipment) {
		if (profile.getTesterType() == TesterType.CMW) {
			equipment.setCmwBox(profile.getBox());
			equipment.setCmwPosition(profile.getPosition());
			equipment.setCmwPositionDetail("--");
			equipment.setCmwPositionImage("auto-cmw");
		} else {
			equipment.setCmuBox(profile.getBox());
			equipment.setCmuPosition(profile.getPosition());
			equipment.setCmuPositionDetail("--");
			equipment.setCmuPositionImage("auto");
		}
	}

	// Shortcuts

	public List<Shortcut> readShortcuts() {
		return shortcutData.readShortcuts();
	}

	public void writeShortcuts(List<Shortcut> shortcuts) {
		shortcutData.writeShortcuts(shortcuts);
	}

	// ProfileParts

	public ProfileParts readProfileParts(TesterType testerType) {
		return profilePartsData.readProfileParts(testerType);
	}

	public void writeProfileParts(ProfileParts parts) {
		profilePartsData.writeProfileParts(parts);
		if (parts.getTesterType() == TesterType.CMU) {
			cmuProfileParts = parts;
			cmuProfileData.setProfileParts(parts);
			shortcutData.setProfileParts(parts);
			databaseBackupData.setProfileParts(parts);
		}
	}

	// Databases - profiles

	public void cmuDatabaseRefresh() {
		cmuDatabaseRefresh = true;
	}

	public void cmwDatabaseRefresh() {
		cmwDatabaseRefresh = true;
	}

	public List<String> readDatabaseNames(TesterType testerType) {
		List<String> allNames = new ArrayList<>();
		List<String> cmuNames = readCmuDatabaseFolders();
		for (String name : cmuNames) {
			if (testerType == null || testerType == TesterType.CMU) {
				allNames.add("CMU " + name);
			}
		}
		List<String> cmwNames = readCmwDatabaseFolders();
		for (String name : cmwNames) {
			if (testerType == null || testerType == TesterType.CMW) {
				allNames.add("CMW " + name);
			}
		}
		return allNames;
	}

	public List<String> readCmuDatabaseFolders() {
		return fileHandler.listDirectories(globalConfig.getCmuDatabasePath());
	}

	public List<String> readCmwDatabaseFolders() {
		return fileHandler.listDirectories(globalConfig.getCmwDatabasePath());
	}

	public void preloadResources() {
		if (localConfig.isPreloadCmu()) {
			readDatabases(TesterType.CMU);
		}
		if (localConfig.isPreloadCmw()) {
			readDatabases(TesterType.CMW);
		}
		if (localConfig.isPreloadReports()) {
			readReports(90);
		}
	}

	public List<Database> readDatabases(TesterType testerType) {
		List<Database> databases;
		if (testerType == TesterType.CMU) {
			if (cmuDatabaseRefresh) {
                window.showNotification(RfNotice.CMU_DATABASE_READ);
                databases = cmuProfileData.readDatabases(readCmuDatabaseFolders());
				cmuDatabaseRefresh = false;
			} else {
				databases = cmuProfileData.getDatabases();
			}
		} else {
			if (cmwDatabaseRefresh) {
                window.showNotification(RfNotice.CMW_DATABASE_READ);
                databases = cmwProfileData.readDatabases(readCmwDatabaseFolders());
				cmwDatabaseRefresh = false;
			} else {
				databases = cmwProfileData.getDatabases();
			}
		}
		List<DatabaseLogBatch> databaseLogBatches = readDatabaseLogs();
		List<ProfileLogBatch> profileLogBatches = readProfileLogs();
		for (Database database : databases) {
			database.setLogBatch(DatabaseLogBatchFilter.getDatabaseLogBatchBySerial(databaseLogBatches, database.getSerial()));
			for (Profile profile : database.getProfiles()) {
				profile.setLogBatch(ProfileLogBatchFilter.getProfileLogBatchBySerialAndByName(profileLogBatches, profile.getSerial(), profile.getName()));
			}
		}
		window.closeNotification();
		return databases;
	}

	public void writeDatabases(List<Database> databases) {
		List<ProfileLogBatch> excessLogs = new ArrayList<>();
		List<Profile> lastProfiles = new ArrayList<>();
		if (databases.get(0).getTesterType() == TesterType.CMU) {
			boolean shortcutRemoved = false;
			List<Shortcut> shortcuts = readShortcuts();
			for (Database database : databases) {
				for (Profile profile : database.getRemovedProfiles()) {
					excessLogs.add(profile.getLogBatch());
					if (getMatchingProfileCount(databases, profile.getName()) == 0) {
						shortcuts.remove(ShortcutFilter.getShortcutByName(shortcuts, profile.getName()));
						shortcutRemoved = true;
						lastProfiles.add(profile);
					}
				}
			}
			if (shortcutRemoved) {
				writeShortcuts(shortcuts);
			}
		} else {
			for (Database database : databases) {
				for (Profile profile : database.getRemovedProfiles()) {
					deleteProfileData((CmwProfile) profile);
					excessLogs.add(profile.getLogBatch());
					if (getMatchingProfileCount(databases, profile.getName()) == 0) {
						lastProfiles.add(profile);
					}
				}
			}
		}
		if (excessLogs.size() > 0) {
			clearExcessProfileLogs(excessLogs);
		}
		if (lastProfiles.size() > 0) {
			clearEquipmentData(lastProfiles);
		}
		for (Database database : databases) {
			if (database.isModified()) {
				writeDatabase(database);
			}
		}
	}

	public void writeDatabase(Database database) {
		for (Profile profile : database.getProfiles()) {
			if (profile.isCompensated() && !profile.isCreated()) {
				profileLogData.writeProfileCompensationLog(profile);
			}
			if (profile.isCreated()) {
				profileLogData.writeProfileLog(profile.getLogBatch().getLogs().get(0));
			}
		}
		if (database.getTesterType() == TesterType.CMW) {
			window.showNotification(RfNotice.CMW_DATABASE_WRITE);
			cmwProfileData.writeDatabase((CmwDatabase) database, databaseCleanup);
			cmwDatabaseRefresh();
		} else {
			window.showNotification(RfNotice.CMU_DATABASE_WRITE);
			cmuProfileData.writeDatabase((CmuDatabase) database, databaseCleanup);
			cmuDatabaseRefresh();
		}
		window.closeNotification();
	}


	public void deleteProfileData(CmwProfile oldProfile) {
		cmwProfileData.deleteProfileData(oldProfile);
	}

	private int getMatchingProfileCount(List<Database> databases, String name) {
		int count = 0;
		for (Database database : databases) {
			if (database.getProfileByName(name) != null) {
				count++;
			}
		}
		return count;
	}

	public boolean isProfileNameMatched(List<Database> databases, String name) {
		return databases.stream().flatMap(database -> database.getProfiles().stream())
				.anyMatch(profile -> profile.getName().equals(name));
	}

	private Profile findProfileByName(List<Database> databases, String name) {
		for (Database database : databases) {
			Profile profile = database.getProfileByName(name);
			if (profile != null) {
				return profile;
			}
		}
		return null;
	}

	// SourceFolder

	public List<String> readCmuSourceFolders() {
        window.showNotification(RfNotice.SELECT_SOURCE_FOLDER_READ);
		List<String> folderNames = listSourceFolders(globalConfig.getCmuReportPath(), minYear);
		window.closeNotification();
		return folderNames;
	}

	public List<String> readCmwSourceFolders() {
        window.showNotification(RfNotice.SELECT_SOURCE_FOLDER_READ);
		List<String> folderNames = listSourceFolders(globalConfig.getCmwReportPath(), minYear);
		window.closeNotification();
		return folderNames;
	}

	public List<String> listSourceFolders(String path, int minYear) {
		List<String> filesList = fileHandler.listDirectories(path);
		List<String> filteredList = new ArrayList<>();
		for (String name : filesList) {
			try {
				if (Integer.parseInt(name.substring(0, 4)) >= minYear) {
					filteredList.add(name);
				}
			} catch (NumberFormatException exception) {
				System.out.println("Conversion error: " + name.substring(0, 4));
			}
		}
		return filteredList;
	}

	// Report

	public void reportContentToClipboard(Report report, String content) {
		if (fileHandler.fileExists(report.getFolder() + "\\" + report.getFilename())) {
			if (content.equals("path")) {
                SystemHelper.stringToClipboard("RF: " + report.getFolder().replace("autosave", "autosave_read") + "\\" + report.getFilename());
                window.showNotification(RfNotice.REPORT_PATH_SUCCESS);
			} else if (content.equals("imei")) {
                SystemHelper.stringToClipboard("" + report.getImei());
                window.showNotification(RfNotice.REPORT_IMEI_SUCCESS);
			}
		} else {
            window.showNotification(RfNotice.REPORT_MISSING);
		}
	}

	public void openReport(Report report) {
		if (fileHandler.fileExists(report.getFolder() + "\\" + report.getFilename())) {
			SystemHelper.openFile(report.getFolder() + "\\" + report.getFilename());
		} else {
            window.showNotification(RfNotice.REPORT_MISSING);
		}
	}

	public boolean deleteReport(Report report) {
		boolean success = true;
		String path = report.getFolder() + "\\" + report.getFilename();
		if (fileHandler.fileExists(path)) {
			if (!fileHandler.deleteFile(path)) {
                window.showNotification(RfNotice.SELECT_REPORT_DELETE_ERROR);
                success = false;
			}
			if (success && report.getTesterType() == TesterType.CMW) {
				if (!fileHandler.deleteFile(report.getFolder() + "\\" + ((CmwReport) report).getRawFilename())) {
                    window.showNotification(RfNotice.SELECT_REPORT_DELETE_ERROR);
                    success = false;
				}
			}
		} else {
            window.showNotification(RfNotice.REPORT_MISSING);
            success = false;
		}
		if (success) {
            window.showNotification(RfNotice.SELECT_REPORT_DELETE_SUCCESS);
		}
		return success;
	}

	public List<Report> readReports(int interval) {
        window.showNotification(RfNotice.REPORT_PROCESS);
		List<Report> allReports = new ArrayList<>();
		for (int i = 0; i < interval; i++) {
			String folder = localDateTimeToCmuFolder(LocalDateTime.now().minusDays(i));
			List<Report> reports = readCmuReports(folder, LocalDate.now(), 5);
			allReports.addAll(reports);
			folder = DateHelper.localDateTimeToIsoTextDate(LocalDateTime.now().minusDays(i));
			reports = readCmwReports(folder, LocalDate.now(), 5);
			allReports.addAll(reports);
		}
		Collections.sort(allReports);
		window.closeNotification();
		return allReports;
	}

	public String localDateTimeToCmuFolder(LocalDateTime dateTime) {
		return dateTime.getYear() + TextHelper.addZero(dateTime.getMonthValue()) + TextHelper.addZero(dateTime.getDayOfMonth());
	}

	private List<Report> readCmuReports(String folder, LocalDate currentDate, int passableLimit) {
		window.changeNotificationText("Teszt riportok feldolgozása: " + folder);
		List<Report> reports = reportCacheData.readReportCache(globalConfig.getCmuReportPath(), globalConfig.getCmuCachePath(), folder, passableLimit);
		if (reports == null) {
			reports = new ArrayList<>();
			LocalDate folderDate = LocalDate.of(Integer.parseInt(folder.substring(0, 4)), Integer.parseInt(folder.substring(4, 6)), Integer.parseInt(folder.substring(6, 8)));
			String reportPath = globalConfig.getCmuReportPath() + "\\" + folder;
			List<String> filesList = fileHandler.listFiles(reportPath);
			for (String filename : filesList) {
				CmuReport report = cmuReportData.readReport(reportPath, filename, passableLimit);
				if (report != null) {
					reports.add(report);
				}
			}
			if (folderDate.isBefore(currentDate)) {
				List<Report> validReports = new ArrayList<>();
				for (Report report : reports) {
					if (report.getScriptVersion() >= globalConfig.getMinCmuScriptVersion()) {
						validReports.add(report);
					}
				}
				if (!validReports.isEmpty()) {
					reportCacheData.writeReportCache(globalConfig.getCmuCachePath(), folder + ".csv", validReports);
				}
			}
		}
		return reports;
	}

	private List<Report> readCmwReports(String folder, LocalDate currentDate, int passableLimit) {
		window.changeNotificationText("Teszt riportok feldolgozása: " + folder);
		List<Report> reports = reportCacheData.readReportCache(globalConfig.getCmwReportPath(), globalConfig.getCmwCachePath(), folder, passableLimit);
		if (reports == null) {
			reports = new ArrayList<>();
			LocalDate folderDate = LocalDate.of(Integer.parseInt(folder.substring(0, 4)), Integer.parseInt(folder.substring(5, 7)), Integer.parseInt(folder.substring(8, 10)));
			String reportPath = globalConfig.getCmwReportPath() + "\\" + folder;
			List<String> filesList = fileHandler.listFiles(reportPath);
			for (String filename : filesList) {
				Report report = cmwReportData.readReport(reportPath, filename, passableLimit);
				if (report != null) {
					reports.add(report);
				}
			}
			if (folderDate.isBefore(currentDate)) {
				for (String filename : filesList) {
					if (filename.contains(".rsmr")) {
						fileHandler.deleteFile(reportPath + "\\" + filename);
					}
				}
				List<Report> validReports = new ArrayList<>();
				for (Report report : reports) {
					if (report.getScriptVersion() >= globalConfig.getMinCmwScriptVersion()) {
						validReports.add(report);
					}
				}
				if (!validReports.isEmpty()) {
					reportCacheData.writeReportCache(globalConfig.getCmwCachePath(), folder + ".csv", validReports);
				}
			}
		}
		return reports;
	}

	public List<Station> processReports(TesterType testerType, List<String> folderNames, int passableLimit) {
		List<Integer> serials = new ArrayList<>();
		List<Station> stations = new ArrayList<>();
		LocalDate currentDate = LocalDate.now();
		List<Database> databases = readDatabases(testerType);
		if (databases.size() > 0) {
            window.showNotification(RfNotice.REPORT_PROCESS);
			for (String folder : folderNames) {
				if (testerType == TesterType.CMU) {
					sortReports(TesterType.CMU, serials, stations, readCmuReports(folder, currentDate, passableLimit), databases);
				} else {
					sortReports(TesterType.CMW, serials, stations, readCmwReports(folder, currentDate, passableLimit), databases);
				}
			}
			for (Station station : stations) {
				station.initStation();
			}
			window.closeNotification();
		} else {
			if (testerType == TesterType.CMW) {
                window.showNotification(RfNotice.CMW_PROFILE_ERROR);
			} else {
                window.showNotification(RfNotice.CMU_PROFILE_ERROR);
			}
		}
		return stations;
	}

	private void sortReports(TesterType testerType, List<Integer> serials, List<Station> stations, List<Report> reports, List<Database> databases) {
		for (Report report : reports) {
			if (!serials.contains(report.getSerial())) {
				serials.add(report.getSerial());
				Database databaseMatch = null;
				for (Database database : databases) {
					if (database.getSerial() == report.getSerial()) {
						databaseMatch = database;
					}
				}
				if (testerType == TesterType.CMU) {
					stations.add(new CmuStation(report.getSerial(), (CmuDatabase) databaseMatch, cmuLimits, globalConfig.getMinCmuScriptVersion()));
				} else {
					stations.add(new CmwStation(report.getSerial(), (CmwDatabase) databaseMatch, cmwLimits, globalConfig.getMinCmwScriptVersion()));
				}
			}
		}
		for (Report report : reports) {
			for (Station station : stations) {
				if (station.getSerial() == report.getSerial()) {
					station.addReport(report);
				}
			}
		}
	}

	// ProfileLog

	public void clearExcessProfileLogs(List<ProfileLogBatch> excessLogs) {
		profileLogData.clearExcessLogs(excessLogs);
	}

	public List<ProfileLogBatch> readProfileLogs() {
		return profileLogData.readProfileLogs();
	}

	public void writeProfileSyncLog(List<Profile> missingProfiles, int sourceSerial, int targetSerial) {
		profileLogData.writeProfileSyncLog(missingProfiles, sourceSerial, targetSerial);
	}

	private void updateProfileLogs(Database database, LocalDateTime restoreDateTime) {
		List<ProfileLogBatch> profileLogBatches = readProfileLogs();
		List<ProfileLogBatch> excessLogs = new ArrayList<>();
		for (ProfileLogBatch logBatch : ProfileLogBatchFilter.getProfileLogBatchesBySerial(profileLogBatches, database.getSerial())) {
			Profile profile = database.getProfileByName(logBatch.getName());
			if (profile == null) {
				excessLogs.add(logBatch);
			} else {
				if (logBatch.getLatestLog().getDateTime().isAfter(restoreDateTime)) {
					profile.setReverted(true);
					profileLogData.writeProfileCompensationLog(profile);
				}
			}
		}
		for (Profile profile : database.getProfiles()) {
			if (ProfileLogBatchFilter.getProfileLogBatchBySerialAndByName(profileLogBatches, profile.getSerial(), profile.getName()) == null) {
				profile.setReverted(true);
				profileLogData.writeProfileLog(new ProfileLog(LocalDateTime.now(), ProfileLogEvent.CREATION, profile.getSerial(), profile.getTesterType(), profile.getName(), "(visszaállításból létrehozva)"));
			}
		}
		clearExcessProfileLogs(excessLogs);
	}

	// DatabaseLog

	public List<DatabaseLogBatch> readDatabaseLogs() {
		return databaseLogData.readDatabaseLogs();
	}

	public void writeCheckDatabaseLog(Database database) {
		databaseLogData.writeDatabaseCheckLog(database.getSerial(), database.getTesterType());
	}

	public List<DatabaseBackup> readDatabaseBackupHeaders() {
		List<DatabaseBackup> backups = new ArrayList<>();
		List<DatabaseLog> backupLogs = databaseLogData.readDatabaseBackupLogs();
		for (DatabaseLog log : backupLogs) {
			if (log.getTesterType() == TesterType.CMW) {
				backups.add(new CmwDatabaseBackup(log.getSerial(), log.getDateTime()));
			} else {
				backups.add(new CmuDatabaseBackup(log.getSerial(), log.getDateTime()));
			}
		}
		return backups;
	}

	// DatabaseBackup

	public void deleteBackup(DatabaseBackup backup) {
		databaseLogData.removeExcessDatabaseLog(backup.getSerial(), backup.getDateTime());
		databaseBackupData.deleteBackup(backup);
	}

	public void createDatabaseBackup(Database database) {
		DatabaseBackup backup;
		LocalDateTime dateTime = LocalDateTime.now();
		if (database.getTesterType() == TesterType.CMW) {
			backup = new CmwDatabaseBackup(database.getSerial(), dateTime);
		} else {
			backup = new CmuDatabaseBackup(database.getSerial(), dateTime);
			((CmuDatabaseBackup) backup).setShortcuts(readShortcuts());
		}
		backup.setDatabase(database);
		databaseBackupData.writeDatabaseBackup(backup);
		databaseLogData.writeDatabaseBackupLog(database.getSerial(), database.getTesterType(), dateTime);
	}

	public void readDatabaseBackupContent(DatabaseBackup databaseBackup) {
		databaseBackupData.readDatabaseBackupContent(databaseBackup);
	}

	public void createEmptyCmuDatabaseFolder(int serial) {
		cmuProfileData.createEmptyDatabaseFolder(serial);
	}

	public void createEmptyCmwDatabaseFolder(int serial) {
		cmwProfileData.createEmptyDatabaseFolder(serial);
	}

	public boolean restoreDatabaseBackup(DatabaseBackup backup) {
		Database database;
		int databaseSerial = backup.getSerial();
		String databaseFolder = backup.getTesterType() == TesterType.CMU ?
				globalConfig.getCmuDatabasePath() + "\\" + databaseSerial : globalConfig.getCmwDatabasePath() + "\\" + databaseSerial;
		database = backup.getDatabase();
		List<String> nonRemovableFiles = fileHandler.deleteWholeDirectory(databaseFolder);
		if (nonRemovableFiles.isEmpty()) {
			if (backup.getTesterType() == TesterType.CMU) {
				createEmptyCmuDatabaseFolder(databaseSerial);
				cmuProfileData.writeDatabase((CmuDatabase) database, false);
				cmuDatabaseRefresh();
				updateProfileLogs(database, backup.getDateTime());
				updateShortcuts(((CmuDatabaseBackup) backup).getShortcuts());
				updateProfileParts(backup.getDatabase().getProfiles(), ((CmuDatabaseBackup) backup).getShortcuts());
				updateEquipments(backup.getTesterType(), backup.getEquipments());
			} else {
				createEmptyCmwDatabaseFolder(databaseSerial);
				cmwProfileData.writeDatabase((CmwDatabase) database, false);
				cmwDatabaseRefresh();
				updateProfileLogs(database, backup.getDateTime());
				updateProfileParts(backup.getDatabase().getProfiles(), null);
				updateEquipments(backup.getTesterType(), backup.getEquipments());
			}
			databaseLogData.writeDatabaseRestoreLog(backup.getDatabase().getSerial(), backup.getDatabase().getTesterType());
		} else {
            window.showNotification(RfNotice.EMPTY);
			String notificationText = "A következő fájlokat nem sikerült törölni:";
			for (String filename : nonRemovableFiles) {
				notificationText = notificationText.concat("\n").concat(filename);
			}
			notificationText = notificationText.concat("\n\nA visszállítás művelet megszakítva!");
			window.changeNotificationText(notificationText);
			if (backup.getTesterType() == TesterType.CMU) {
				cmuDatabaseRefresh();
			} else {
				cmwDatabaseRefresh();
			}
			return false;
		}
		return true;
	}

	private void updateProfileParts(List<Profile> profiles, List<Shortcut> shortcuts) {
		TesterType testerType = profiles.get(0).getTesterType();
		ProfileParts profileParts = readProfileParts(testerType);
		for (Profile profile : profiles) {
			if (Arrays.stream(profileParts.getManufacturers()).noneMatch(manufacturer -> manufacturer.equals(profile.getManufacturer()))) {
				profileParts.addManufacturer(profile.getManufacturer());
			}
			if (profileParts.getShieldBoxes().stream().noneMatch(box -> box.getName().equals(profile.getBox()))) {
				if (testerType == TesterType.CMU) {
					profileParts.addEmptyBox(new Contraction(profile.getBox(), cmuProfileParts.longToShortBox(profile.getBox())));
				} else {
					profileParts.addEmptyBox(new Contraction(profile.getBox(), ""));
				}
			}
			if (Arrays.stream(profileParts.getPositions(profile.getBox())).noneMatch(position -> position.equals(profile.getPosition()))) {
				profileParts.addPosition(profile.getBox(), profile.getPosition());
			}
		}
		if (testerType == TesterType.CMU) {
			for (Shortcut shortcut : shortcuts) {
				if (Arrays.stream(profileParts.getScripts()).noneMatch(script -> script.equals(shortcut.getScript()))) {
					profileParts.addScript(shortcut.getScript());
				}
			}
		}
		writeProfileParts(profileParts);
	}

	private void updateShortcuts(List<Shortcut> backupShortcuts) {
		List<Shortcut> actualShortcuts = readShortcuts();
		for (Shortcut backupShortcut : backupShortcuts) {
			Shortcut actualShortcut = ShortcutFilter.getShortcutByName(actualShortcuts, backupShortcut.getName());
			if (actualShortcut != null) {
				actualShortcut.setScript(backupShortcut.getScript());
			} else {
				actualShortcuts.add(backupShortcut);
			}
		}
		List<Database> databases = readDatabases(TesterType.CMU);
		List<Shortcut> unusedShortcuts = new ArrayList<>();
		for (Shortcut actualShortcut : actualShortcuts) {
			if (findProfileByName(databases, actualShortcut.getName()) == null) {
				unusedShortcuts.add(actualShortcut);
			}
		}
		unusedShortcuts.forEach(actualShortcuts::remove);
		writeShortcuts(actualShortcuts);
	}


	// Config

	public void writeGlobalConfig(Config config) {
		configData.writeGlobalConfig(config);
	}

	public void writeLocalConfig(Config config) {
		localConfig = config;
		configData.writeLocalConfig(config);
	}

	public boolean isGlobalConfigValid() {
		return configData.hasParseErrorOccurred();
	}

	// Other

	public boolean hasInvalidSeparatorCharacter(String text) {
		char[] characters = text.toCharArray();
		for (char character : characters) {
			if (character == '_' || character == '\t') {
				return true;
			}
		}
		return false;
	}

	public void openPositionFolder() {
		SystemHelper.openFile(networkFolder + "\\Data\\Positions");
	}

	public boolean updateNeeded() {
		new UpdateHandler(fileHandler).updateUpdater(networkFolder);
		return VERSION < globalConfig.getMinVersion();
	}

	public boolean isNetworkFolderValid(String folder) {
		return fileHandler.fileExists(folder + "\\config.ini") && fileHandler.directoryExists(folder + "\\Data") &&
				fileHandler.directoryExists(folder + "\\Data\\Positions") && fileHandler.directoryExists(folder + "\\Data\\Backup");
	}

	public void createNetworkFolder(String networkFolder) {
		fileHandler.createDirectory(networkFolder);
		fileHandler.createDirectory(networkFolder + "\\Data");
		fileHandler.createDirectory(networkFolder + "\\Data\\Positions");
		fileHandler.createDirectory(networkFolder + "\\Data\\Backup");
		configData.writeDefaultGlobalConfig(networkFolder);
	}

}
