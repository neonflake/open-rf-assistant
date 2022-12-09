package hu.open.assistant.rf.data.cmw;

import hu.open.assistant.rf.model.database.CmwDatabase;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.values.CmwProfileValues;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.commons.data.FileHandler;
import hu.open.assistant.rf.RfAssistant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Data class which reads and writes raw CMW type RF profiles from or to disk. Profile information is stored
 * within multiple files in custom text format and XML, compatible with CMWrun application. The logical Profiles are
 * organised and handled into a logical Database. Data files (and directories) written to disk are fully generated from
 * code and there is a cleanup option for unused data. The files containing the actual profiles when corrupted or
 * missing can be regenerated with default values.
 */
public class CmwProfileData {

	private static final String MAIN_DATA_FILE = "CustomerMappingDB.txt";
	private static final String SIDE_DATA_FILE = "MappingDB.txt";
	private static final String DATAFILE_EXTENSION = ".fda";

	private static final String MISC_CHANNELS = "796000000;806000000;815900000;837000000;847000000;856900000;882400000;890200000;897400000;897600000;912600000;914800000;927400000;935200000;942400000;942600000;957600000;959800000;1710200000;1715000000;1747400000;1747500000;1779900000;1784800000;1805200000;1810000000;1842400000;1842500000;1874900000;1879800000;1922400000;1925000000;1950000000;1950000000;1974900000;1977600000;2112400000;2115000000;2140000000;2140000000;2164900000;2167600000;2505000000;2535000000;2564900000;2625000000;2655000000;2684900000";
	private static final String WCDMA_IN_CHANNELS = "882400000;897600000;912600000;1922400000;1950000000;1977600000";
	private static final String WCDMA_OUT_CHANNELS = "927400000;942600000;957600000;2112400000;2140000000;2167600000";
	private static final String GSM_IN_CHANNELS = "890200000;897400000;914800000;1710200000;1747400000;1784800000";
	private static final String GSM_OUT_CHANNELS = "935200000;942400000;959800000;1805200000;1842400000;1879800000";
	private static final String LTE_IN_CHANNELS = "837000000;847000000;856900000;1715000000;1747500000;1779900000;1925000000;1950000000;1974900000;2505000000;2535000000;2564900000";
	private static final String LTE_OUT_CHANNELS = "796000000;806000000;815900000;1810000000;1842500000;1874900000;2115000000;2140000000;2164900000;2625000000;2655000000;2684900000";

	private static final String RAW_PROFILE = "Manufacturer Type	10000000	User_Generic	---	A1	Manufacturer Type_10000000_GSM_IN@User_Generic	Manufacturer Type_10000000_GSM_OUT@User_Generic	Manufacturer Type_10000000_WCDMA_IN@User_Generic	Manufacturer Type_10000000_WCDMA_OUT@User_Generic	Manufacturer Type_10000000_LTE_IN@User_Generic	Manufacturer Type_10000000_LTE_OUT@User_Generic	---	---	---	---	---	---	---	---	---	---	Manufacturer Type_10000000_MISC@User_Generic	---	---";
	private static final String DATA_HEADER = "Mobiletype	Tac	TestSetup	PictureFile	Position	GSM_IN_FDA	GSM_OUT_FDA	WCDMA_IN_FDA	WCDMA_OUT_FDA	LTE_IN_FDA	LTE_OUT_FDA	C2K_IN_FDA	C2K_OUT_FDA	EVDO_IN_FDA	EVDO_OUT_FDA	WLAN_IN_FDA	WLAN_OUT_FDA	BT_IN_FDA	BT_OUT_FDA	TDSCDMA_IN_FDA	TDSCDMA_OUT_FDA	MISC_FDA	NBIOT_IN_FDA	NBIOT_OUT_FDA";

	private final String[] rawProfileParts = RAW_PROFILE.split("\t");
	private final String databaseFolder;
	private final FileHandler fileHandler;
	private final int centerValue;
	private List<Database> databases;
	private final RfAssistant assistant;

	public CmwProfileData(String databaseFolder, int centerValue, FileHandler fileHandler, RfAssistant assistant) {
		this.databaseFolder = databaseFolder;
		this.centerValue = centerValue;
		this.fileHandler = fileHandler;
		this.assistant = assistant;
	}

	public void writeDatabase(CmwDatabase database, boolean cleanup) {
		List<String> newData = new ArrayList<>();
		newData.add(DATA_HEADER);
		List<Profile> profiles = database.getProfiles();
		for (Profile profile : profiles) {
			pushUpdatedData((CmwProfile) profile, newData);
			updateFileData((CmwProfile) profile);
		}
		fileHandler.writeUtf8Text(databaseFolder + "\\" + database.getSerial() + "\\TAC_DB\\" + MAIN_DATA_FILE, newData, false);
		if (cleanup) {
			clearUnusedFdaFolders(database.getSerial());
		}
	}

	public void createEmptyDatabaseFolder(int serial) {
		fileHandler.createDirectory(databaseFolder + "\\" + serial);
		String mainFolder = databaseFolder + "\\" + serial + "\\TAC_DB";
		String defaultsFolder = databaseFolder + "\\" + serial + "\\TAC_DB\\" + "Defaults";
		fileHandler.createDirectory(mainFolder);
		fileHandler.createDirectory(defaultsFolder);
		int[] defaultValues = {1, 2, 5, 10, 12, 15, 20, 25};
		for (int value : defaultValues) {
			writeDefaultData(serial, value);
		}
		fileHandler.writeUtf8Text(mainFolder + "\\" + SIDE_DATA_FILE, Collections.singletonList(DATA_HEADER), false);
	}

	private void writeDefaultData(int serial, int value) {
		String folderPath = databaseFolder + "\\" + serial + "\\TAC_DB\\Defaults";
		String channels = "400000000;6000000000";
		String values = value + ".0;" + value + ".0";
		String name = "Const_" + value + "dB";
		int channelCount = 2;
		List<String> rawData = createDataFileHeader();
		rawData.add("         <TableName value=\"" + name + "\" type=\"bstr\" />");
		rawData.add("         <TableSize value=\"" + channelCount + "\" type=\"i4\" />");
		rawData.add("         <FrequencyNodes type=\"array|r8\">");
		rawData.add("             <Vector count=\"" + channelCount + "\" type=\"r8\" data=\"" + channels + "\" />");
		rawData.add("         </FrequencyNodes>");
		rawData.add("         <CorrectionValues type=\"array|r8\">");
		rawData.add("             <Vector count=\"" + channelCount + "\" type=\"r8\" data=\"" + values + "\" />");
		rawData.add("         </CorrectionValues>");
		addDataFileFooter(rawData);
		fileHandler.writeUtf8Text(folderPath + "\\" + name + ".fda", rawData, false);
	}

	private void clearUnusedFdaFolders(int serial) {
		String databasePath = databaseFolder + "\\" + serial + "\\TAC_DB";
		List<String> databaseFolders = fileHandler.listDirectories(databasePath);
		for (String databaseFolder : databaseFolders) {
			List<String> folders = fileHandler.listDirectories(databasePath + "\\" + databaseFolder);
			for (String folder : folders) {
				fileHandler.deleteWholeDirectory(databasePath + "\\" + databaseFolder + "\\" + folder);
			}
		}
	}

	public List<Database> getDatabases() {
		return databases;
	}

	public List<Database> readDatabases(List<String> folderNames) {
		databases = new ArrayList<>();
		List<String> invalidTacs = new ArrayList<>();
		try {
			for (String folder : folderNames) {
				databases.add(new CmwDatabase(Integer.parseInt(folder)));
			}
		} catch (NumberFormatException exception) {
			System.out.println("invalid folder");
		}
		for (Database database : databases) {
			int serial = database.getSerial();
			List<String> tacData = fileHandler.readUtf8TextToList(databaseFolder + "\\" + serial + "\\TAC_DB\\CustomerMappingDB.txt", false);
			if (tacData == null) {
				return new ArrayList<>();
			}
			String name = "";
			String type = "";
			String manufacturer = "";
			List<Long> tacList = new ArrayList<>();
			long tac = 0;
			String shieldBoxType = "";
			String position = "";
			boolean tacError = false;
			for (int i = 1; i < tacData.size(); i++) {
				String[] parts = tacData.get(i).split("\t");
				if (parts[0].equals("Generic Splitter") || !parts[2].equals("RF_Cable")) {
					if (!parts[0].equals(name)) {
						addProfileToDatabase(database, serial, name, type, manufacturer, tacList, tac, shieldBoxType, position, tacError);
						name = parts[0];
						String[] nameparts = name.split(" ");
						manufacturer = nameparts[0];
						type = name.substring(manufacturer.length() + 1);
						tacList = new ArrayList<>();
						tac = Long.parseLong(parts[9].split("_")[1]);
						shieldBoxType = parts[2];
						position = parts[4];
						tacError = processAssociatedTac(tacList, invalidTacs, serial, name, parts[1]);
					} else {
						if (processAssociatedTac(tacList, invalidTacs, serial, name, parts[1])) {
							tacError = true;
						}
					}
					if (i == tacData.size() - 1) {
						addProfileToDatabase(database, serial, name, type, manufacturer, tacList, tac, shieldBoxType, position, tacError);
					}
				}
			}
			database.sortProfiles();
		}
		if (invalidTacs.size() > 0) {
			assistant.setNotificationBuffer("Érvényetelen TAC szám az adatbázisban!\n\n" + TextHelper.stringListToLineBrokenString(invalidTacs));
		} else {
			assistant.setNotificationBuffer("");
		}
		return databases;
	}

	private void addProfileToDatabase(Database database, int serial, String name, String type, String manufacturer, List<Long> tacList,
									  long tac, String shieldBoxType, String position, boolean tacError) {
		if (!name.isEmpty() && tac != 0) {
			CmwProfileValues values = readValues(serial, type, manufacturer, shieldBoxType, tac);
			CmwProfile profile = new CmwProfile(serial, type, manufacturer, shieldBoxType, position, values, centerValue, tac, tacList);
			profile.checkCondition();
			if (tacError) {
				profile.enableTacError();
			}
			database.addProfile(profile);
		}
	}

	private boolean processAssociatedTac(List<Long> tacList, List<String> invalidTacs, int serial, String name, String text) {
		long associatedTac;
		try {
			associatedTac = Long.parseLong(text);
		} catch (NumberFormatException exception) {
			invalidTacs.add(serial + " " + name + ": " + text);
			return true;
		}
		if (associatedTac <= 0) {
			invalidTacs.add(serial + " " + name + ": " + text);
			return true;
		}
		if (!tacList.contains(associatedTac)) {
			tacList.add(associatedTac);
		}
		return false;
	}

	private void updateFileData(CmwProfile profile) {
		String folderPath = createProfileFolderPath(profile.getSerial(), profile.getBox());
		if (!fileHandler.directoryExists(folderPath)) {
			fileHandler.createDirectory(folderPath);
		}
		String filepath = createProfileFilepath(profile);
		double[] gsmInValues = new double[6];
		double[] gsmOutValues = new double[6];
		double[] wcdmaInValues = new double[6];
		double[] wcdmaOutValues = new double[6];
		double[] lteInValues = new double[12];
		double[] lteOutValues = new double[12];
		for (int i = 0; i < 3; i++) {
			gsmInValues[i] = profile.getGsm900InValues()[i];
			wcdmaInValues[i] = profile.getWcdma8InValues()[i];
			lteInValues[i] = profile.getLte20InValues()[i];
			gsmOutValues[i] = profile.getGsm900OutValues()[i];
			wcdmaOutValues[i] = profile.getWcdma8OutValues()[i];
			lteOutValues[i] = profile.getLte20OutValues()[i];
		}
		for (int i = 3; i < 6; i++) {
			gsmInValues[i] = profile.getGsm1800InValues()[i - 3];
			wcdmaInValues[i] = profile.getWcdma1InValues()[i - 3];
			lteInValues[i] = profile.getLte3InValues()[i - 3];
			gsmOutValues[i] = profile.getGsm1800OutValues()[i - 3];
			wcdmaOutValues[i] = profile.getWcdma1OutValues()[i - 3];
			lteOutValues[i] = profile.getLte3OutValues()[i - 3];
		}
		for (int i = 6; i < 9; i++) {
			lteInValues[i] = profile.getLte1InValues()[i - 6];
			lteOutValues[i] = profile.getLte1OutValues()[i - 6];
		}
		for (int i = 9; i < 12; i++) {
			lteInValues[i] = profile.getLte7InValues()[i - 9];
			lteOutValues[i] = profile.getLte7OutValues()[i - 9];
		}
		writeUpdatedData(gsmInValues, "_GSM_IN@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
		writeUpdatedData(gsmOutValues, "_GSM_OUT@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
		writeUpdatedData(wcdmaInValues, "_WCDMA_IN@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
		writeUpdatedData(wcdmaOutValues, "_WCDMA_OUT@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
		writeUpdatedData(lteInValues, "_LTE_IN@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
		writeUpdatedData(lteOutValues, "_LTE_OUT@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
		writeUpdatedData(new double[MISC_CHANNELS.length()], "_MISC@", profile.getName(), profile.getStoreTac(), profile.getBox(), filepath);
	}

	private List<String> writeUpdatedData(double[] values, String valuesType, String name, long storeTac, String shieldBoxType, String filepath) {
		List<String> updatedData = createEmptyData(valuesType, name, storeTac, shieldBoxType);
		updatedData.set(11, changeValues(values));
		fileHandler.writeUtf8Text(filepath + valuesType + shieldBoxType + ".fda", updatedData, false);
		return updatedData;
	}

	private String changeValues(double[] values) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			builder.append(values[i]);
			if (i != values.length - 1) {
				builder.append(";");
			}
		}
		return "             <Vector count=\"" + values.length + "\" type=\"r8\" data=\"" + builder + "\" />";
	}

	private void pushUpdatedData(CmwProfile profile, List<String> newData) {
		String[] parts = rawProfileParts.clone();
		parts[0] = profile.getName();
		parts[2] = profile.getBox();
		parts[4] = profile.getPosition();
		parts[5] = profile.getName() + "_" + profile.getStoreTac() + "_GSM_IN@" + profile.getBox();
		parts[6] = profile.getName() + "_" + profile.getStoreTac() + "_GSM_OUT@" + profile.getBox();
		parts[7] = profile.getName() + "_" + profile.getStoreTac() + "_WCDMA_IN@" + profile.getBox();
		parts[8] = profile.getName() + "_" + profile.getStoreTac() + "_WCDMA_OUT@" + profile.getBox();
		parts[9] = profile.getName() + "_" + profile.getStoreTac() + "_LTE_IN@" + profile.getBox();
		parts[10] = profile.getName() + "_" + profile.getStoreTac() + "_LTE_OUT@" + profile.getBox();
		parts[21] = profile.getName() + "_" + profile.getStoreTac() + "_MISC@" + profile.getBox();
		List<Long> tacList = profile.getTacList();
		Collections.sort(tacList);
		for (long tac : tacList) {
			parts[1] = String.valueOf(tac);
			String output = "";
			for (int i = 0; i < parts.length; i++) {
				if (i == 0) {
					output = output.concat(parts[i]);
				} else {
					output = output.concat("\t" + parts[i]);
				}
			}
			newData.add(output);
		}
	}

	private String createProfileFolderPath(int serial, String shieldBoxType) {
		return databaseFolder + "\\" + serial + "\\TAC_DB\\" + shieldBoxType;
	}

	private String createProfileFilepath(CmwProfile profile) {
		return createProfileFilepath(createProfileFolderPath(profile.getSerial(), profile.getBox()),
				profile.getType(), profile.getManufacturer(), profile.getStoreTac());
	}

	private String createProfileFilepath(String folderPath, String type, String manufacturer, long storeTac) {
		return folderPath + "\\" + manufacturer + " " + type + "_" + storeTac;
	}

	private double[] getCenterValues(String channels) {
		double[] values = new double[channels.split(";").length];
		Arrays.fill(values, 10);
		return values;
	}

	private CmwProfileValues readValues(int serial, String type, String manufacturer, String shieldBoxType, long storeTac) {
		CmwProfileValues values = new CmwProfileValues();
		String folderPath = createProfileFolderPath(serial, shieldBoxType);
		String filepath = createProfileFilepath(folderPath, type, manufacturer, storeTac);
		String name = manufacturer + " " + type;
		List<String> gsmInRawData = fileHandler.readUtf8TextToList(filepath + "_GSM_IN@" + shieldBoxType + DATAFILE_EXTENSION, false);
		List<String> gsmOutRawData = fileHandler.readUtf8TextToList(filepath + "_GSM_OUT@" + shieldBoxType + DATAFILE_EXTENSION, false);
		List<String> wcdmaInRawData = fileHandler.readUtf8TextToList(filepath + "_WCDMA_IN@" + shieldBoxType + DATAFILE_EXTENSION, false);
		List<String> wcdmaOutRawData = fileHandler.readUtf8TextToList(filepath + "_WCDMA_OUT@" + shieldBoxType + DATAFILE_EXTENSION, false);
		List<String> lteInRawData = fileHandler.readUtf8TextToList(filepath + "_LTE_IN@" + shieldBoxType + DATAFILE_EXTENSION, false);
		List<String> lteOutRawData = fileHandler.readUtf8TextToList(filepath + "_LTE_OUT@" + shieldBoxType + DATAFILE_EXTENSION, false);
		List<String> miscRawData = fileHandler.readUtf8TextToList(filepath + "_MISC@" + shieldBoxType + DATAFILE_EXTENSION, false);
		if (gsmInRawData == null) {
			gsmInRawData = writeUpdatedData(getCenterValues(GSM_IN_CHANNELS), "_GSM_IN@", name, storeTac, shieldBoxType, filepath);
		}
		if (gsmOutRawData == null) {
			gsmOutRawData = writeUpdatedData(getCenterValues(GSM_OUT_CHANNELS), "_GSM_OUT@", name, storeTac, shieldBoxType, filepath);
		}
		if (wcdmaInRawData == null) {
			wcdmaInRawData = writeUpdatedData(getCenterValues(WCDMA_IN_CHANNELS), "_WCDMA_IN@", name, storeTac, shieldBoxType, filepath);
		}
		if (wcdmaOutRawData == null) {
			wcdmaOutRawData = writeUpdatedData(getCenterValues(WCDMA_OUT_CHANNELS), "_WCDMA_OUT@", name, storeTac, shieldBoxType, filepath);
		}
		if (lteInRawData == null) {
			lteInRawData = writeUpdatedData(getCenterValues(LTE_IN_CHANNELS), "_LTE_IN@", name, storeTac, shieldBoxType, filepath);
		}
		if (lteOutRawData == null) {
			lteOutRawData = writeUpdatedData(getCenterValues(LTE_OUT_CHANNELS), "_LTE_OUT@", name, storeTac, shieldBoxType, filepath);
		}
		if (miscRawData == null) {
			writeUpdatedData(getCenterValues(MISC_CHANNELS), "_MISC@", name, storeTac, shieldBoxType, filepath);
		}
		String[] gsmInParts = processValues(gsmInRawData);
		String[] gsmOutParts = processValues(gsmOutRawData);
		String[] wcdmaInParts = processValues(wcdmaInRawData);
		String[] wcdmaOutParts = processValues(wcdmaOutRawData);
		String[] lteInParts = processValues(lteInRawData);
		String[] lteOutParts = processValues(lteOutRawData);
		try {
			for (int i = 0; i < 3; i++) {
				values.setGsm900InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(gsmInParts[i])), i);
				values.setGsm900OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(gsmOutParts[i])), i);
				values.setWcdma8InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(wcdmaInParts[i])), i);
				values.setWcdma8OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(wcdmaOutParts[i])), i);
				values.setLte20InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteInParts[i])), i);
				values.setLte20OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteOutParts[i])), i);
			}
			for (int i = 3; i < 6; i++) {
				values.setGsm1800InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(gsmInParts[i])), i - 3);
				values.setGsm1800OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(gsmOutParts[i])), i - 3);
				values.setWcdma1InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(wcdmaInParts[i])), i - 3);
				values.setWcdma1OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(wcdmaOutParts[i])), i - 3);
				values.setLte3InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteInParts[i])), i - 3);
				values.setLte3OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteOutParts[i])), i - 3);
			}
			for (int i = 6; i < 9; i++) {
				values.setLte1InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteInParts[i])), i - 6);
				values.setLte1OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteOutParts[i])), i - 6);
			}
			for (int i = 9; i < 12; i++) {
				values.setLte7InValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteInParts[i])), i - 9);
				values.setLte7OutValue(NumberHelper.oneDecimalPlaceOf(Double.parseDouble(lteOutParts[i])), i - 9);
			}
		} catch (NumberFormatException exception) {
			deleteFdaFiles(filepath, shieldBoxType);
			values = new CmwProfileValues();
		}
		return values;
	}

	private String[] processValues(List<String> rawData) {
		return rawData.get(11).split("\"")[5].split(";");
	}


	public void deleteProfileData(CmwProfile profile) {
		String filepath = createProfileFilepath(profile);
		deleteFdaFiles(filepath, profile.getBox());
	}

	private void deleteFdaFiles(String filepath, String shieldBoxType) {
		fileHandler.deleteFile(filepath + "_GSM_IN@" + shieldBoxType + DATAFILE_EXTENSION);
		fileHandler.deleteFile(filepath + "_GSM_OUT@" + shieldBoxType + DATAFILE_EXTENSION);
		fileHandler.deleteFile(filepath + "_WCDMA_IN@" + shieldBoxType + DATAFILE_EXTENSION);
		fileHandler.deleteFile(filepath + "_WCDMA_OUT@" + shieldBoxType + DATAFILE_EXTENSION);
		fileHandler.deleteFile(filepath + "_LTE_IN@" + shieldBoxType + DATAFILE_EXTENSION);
		fileHandler.deleteFile(filepath + "_LTE_OUT@" + shieldBoxType + DATAFILE_EXTENSION);
		fileHandler.deleteFile(filepath + "_MISC@" + shieldBoxType + DATAFILE_EXTENSION);
	}

	private List<String> createEmptyData(String fileType, String name, long storeTAC, String shieldBoxType) {
		String channels;
		switch (fileType) {
			case "_WCDMA_IN@":
				channels = WCDMA_IN_CHANNELS;
				break;
			case "_WCDMA_OUT@":
				channels = WCDMA_OUT_CHANNELS;
				break;
			case "_GSM_IN@":
				channels = GSM_IN_CHANNELS;
				break;
			case "_GSM_OUT@":
				channels = GSM_OUT_CHANNELS;
				break;
			case "_LTE_IN@":
				channels = LTE_IN_CHANNELS;
				break;
			case "_LTE_OUT@":
				channels = LTE_OUT_CHANNELS;
				break;
			default:
				channels = MISC_CHANNELS;
		}
		int channelCount = channels.split(";").length;
		List<String> rawData = createDataFileHeader();
		rawData.add("         <TableName value=\"" + name + "_" + storeTAC + fileType + shieldBoxType + "\" type=\"bstr\" />");
		rawData.add("         <TableSize value=\"" + channelCount + "\" type=\"i4\" />");
		rawData.add("         <FrequencyNodes type=\"array|r8\">");
		rawData.add("             <Vector count=\"" + channelCount + "\" type=\"r8\" data=\"" + channels + "\" />");
		rawData.add("         </FrequencyNodes>");
		rawData.add("         <CorrectionValues type=\"array|r8\">");
		rawData.add("");
		rawData.add("         </CorrectionValues>");
		addDataFileFooter(rawData);
		return rawData;
	}

	private List<String> createDataFileHeader() {
		List<String> rawData = new ArrayList<>();
		rawData.add("<?xml version=\"1.0\" standalone=\"no\" ?>");
		rawData.add("<!DOCTYPE swpl>");
		rawData.add("<swpl>");
		rawData.add(" <FrequencyDependantExternalAttenuationCorrectionTable properties=\"true\">");
		rawData.add("     <properties>");
		return rawData;
	}

	private void addDataFileFooter(List<String> rawData) {
		rawData.add("     </properties>");
		rawData.add(" </FrequencyDependantExternalAttenuationCorrectionTable>");
		rawData.add("</swpl>");
	}
}
