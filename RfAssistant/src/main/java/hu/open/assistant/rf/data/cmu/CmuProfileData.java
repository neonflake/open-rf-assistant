package hu.open.assistant.rf.data.cmu;

import hu.open.assistant.rf.model.database.CmuDatabase;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.CmuProfile;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.rf.model.profile.values.CmuProfileValues;
import hu.open.assistant.commons.data.FileHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data class which reads and writes raw CMU type RF profiles from or to the disk. Profile information is stored in a
 * custom text format compatible with CMUgo application. The logical profiles are organised and handled into a logical
 * database. Data files written to disk are fully generated from code and there is a cleanup option for unused data.
 */
public class CmuProfileData {

	private static final String DEFAULT_DATAFILE = "DEFAULT.TXT";
	private static final String TAC_DATAFILE = "PATHLOSS.TXT";
	private static final String MAIN_DATAFILE = "GSMWCDMAPATHLOSS.TXT";
	private static final String TAC_BACKUP_FILE = "PATHLOSS.BAK";
	private static final String MAIN_BACKUP_FILE = "GSMWCDMAPATHLOSS.BAK";

	private static final String DEFAULT_DATAFILE_HEADER = "Mobiletype	TAC	GSM400_L_IN	GSM400_L_OUT	GSM400_M_IN	GSM400_M_OUT	GSM400_H_IN	GSM400_H_OUT	GSM850_L_IN	GSM850_L_OUT	GSM850_M_IN	GSM850_M_OUT	GSM850_H_IN	GSM850_H_OUT	GSM900_L_IN	GSM900_L_OUT	GSM900_M_IN	GSM900_M_OUT	GSM900_H_IN	GSM900_H_OUT	GSM1800_L_IN	GSM1800_L_OUT	GSM1800_M_IN	GSM1800_M_OUT	GSM1800_H_IN	GSM1800_H_OUT	GSM1900_L_IN	GSM1900_L_OUT	GSM1900_M_IN	GSM1900_M_OUT	GSM1900_H_IN	GSM1900_H_OUT	WCDMA1_L_IN	WCDMA1_L_OUT	WCDMA1_M_IN	WCDMA1_M_OUT	WCDMA1_H_IN	WCDMA1_H_OUT	WCDMA2_L_IN	WCDMA2_L_OUT	WCDMA2_M_IN	WCDMA2_M_OUT	WCDMA2_H_IN	WCDMA2_H_OUT	WCDMA3_L_IN	WCDMA3_L_OUT	WCDMA3_M_IN	WCDMA3_M_OUT	WCDMA3_H_IN	WCDMA3_H_OUT	WCDMA4_L_IN	WCDMA4_L_OUT	WCDMA4_M_IN	WCDMA4_M_OUT	WCDMA4_H_IN	WCDMA4_H_OUT	WCDMA5_L_IN	WCDMA5_L_OUT	WCDMA5_M_IN	WCDMA5_M_OUT	WCDMA5_H_IN	WCDMA5_H_OUT	WCDMA6_L_IN	WCDMA6_L_OUT	WCDMA6_M_IN	WCDMA6_M_OUT	WCDMA6_H_IN	WCDMA6_H_OUT	Position	Picturefile	RequiresHandling";
	private static final String DEFAULT_DATAFILE_DEFAULT_PROFILE = "Default	0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10	10	yes";
	private static final String TAC_DATAFILE_HEADER = "Mobiletype	TAC	GSM400_L_IN	GSM400_L_OUT	GSM400_M_IN	GSM400_M_OUT	GSM850_L_IN	GSM850_L_OUT	GSM850_M_IN	GSM850_M_OUT	GSM850_H_IN	GSM850_H_OUT	GSM900_L_IN	GSM900_L_OUT	GSM900_M_IN	GSM900_M_OUT	GSM900_H_IN	GSM900_H_OUT	GSM1800_L_IN	GSM1800_L_OUT	GSM1800_M_IN	GSM1800_M_OUT	GSM1800_H_IN	GSM1800_H_OUT	GSM1900_L_IN	GSM1900_L_OUT	GSM1900_M_IN	GSM1900_M_OUT	GSM1900_H_IN	GSM1900_H_OUT	WCDMA1_L_IN	WCDMA1_L_OUT	WCDMA1_M_IN	WCDMA1_M_OUT	WCDMA1_H_IN	WCDMA1_H_OUT	WCDMA2_L_IN	WCDMA2_L_OUT	WCDMA2_M_IN	WCDMA2_M_OUT	WCDMA2_H_IN	WCDMA2_H_OUT	WCDMA3_L_IN	WCDMA3_L_OUT	WCDMA3_M_IN	WCDMA3_M_OUT	WCDMA3_H_IN	WCDMA3_H_OUT	WCDMA4_L_IN	WCDMA4_L_OUT	WCDMA4_M_IN	WCDMA4_M_OUT	WCDMA4_H_IN	WCDMA4_H_OUT	WCDMA5_L_IN	WCDMA5_L_OUT	WCDMA5_M_IN	WCDMA5_M_OUT	WCDMA5_H_IN	WCDMA5_H_OUT	WCDMA6_L_IN	WCDMA6_L_OUT	WCDMA6_M_IN	WCDMA6_M_OUT	WCDMA7_L_IN	WCDMA7_L_OUT	WCDMA7_M_IN	WCDMA7_M_OUT	WCDMA7_H_IN	WCDMA7_H_OUT	WCDMA8_L_IN	WCDMA8_L_OUT	WCDMA8_M_IN	WCDMA8_M_OUT	WCDMA8_H_IN	WCDMA8_H_OUT	WCDMA9_L_IN	WCDMA9_L_OUT	WCDMA9_M_IN	WCDMA9_M_OUT	WCDMA9_H_IN	WCDMA9_H_OUT	Position	Picturefile	RequiresHandling	SelectedBands";
	private static final String MAIN_DATAFILE_HEADER = "Shortcut	GSM400_L_IN	GSM400_L_OUT	GSM400_M_IN	GSM400_M_OUT	GSM850_L_IN	GSM850_L_OUT	GSM850_M_IN	GSM850_M_OUT	GSM850_H_IN	GSM850_H_OUT	GSM900_L_IN	GSM900_L_OUT	GSM900_M_IN	GSM900_M_OUT	GSM900_H_IN	GSM900_H_OUT	GSM1800_L_IN	GSM1800_L_OUT	GSM1800_M_IN	GSM1800_M_OUT	GSM1800_H_IN	GSM1800_H_OUT	GSM1900_L_IN	GSM1900_L_OUT	GSM1900_M_IN	GSM1900_M_OUT	GSM1900_H_IN	GSM1900_H_OUT	WCDMA1_L_IN	WCDMA1_L_OUT	WCDMA1_M_IN	WCDMA1_M_OUT	WCDMA1_H_IN	WCDMA1_H_OUT	WCDMA2_L_IN	WCDMA2_L_OUT	WCDMA2_M_IN	WCDMA2_M_OUT	WCDMA2_H_IN	WCDMA2_H_OUT	WCDMA3_L_IN	WCDMA3_L_OUT	WCDMA3_M_IN	WCDMA3_M_OUT	WCDMA3_H_IN	WCDMA3_H_OUT	WCDMA4_L_IN	WCDMA4_L_OUT	WCDMA4_M_IN	WCDMA4_M_OUT	WCDMA4_H_IN	WCDMA4_H_OUT	WCDMA5_L_IN	WCDMA5_L_OUT	WCDMA5_M_IN	WCDMA5_M_OUT	WCDMA5_H_IN	WCDMA5_H_OUT	WCDMA6_L_IN	WCDMA6_L_OUT	WCDMA6_M_IN	WCDMA6_M_OUT	WCDMA7_L_IN	WCDMA7_L_OUT	WCDMA7_M_IN	WCDMA7_M_OUT	WCDMA7_H_IN	WCDMA7_H_OUT	WCDMA8_L_IN	WCDMA8_L_OUT	WCDMA8_M_IN	WCDMA8_M_OUT	WCDMA8_H_IN	WCDMA8_H_OUT	WCDMA9_L_IN	WCDMA9_L_OUT	WCDMA9_M_IN	WCDMA9_M_OUT	WCDMA9_H_IN	WCDMA9_H_OUT	Position	Picturefile	RequiresHandling	SelectedBands";
	private static final String MAIN_DATAFILE_EMPTY_PROFILE = "- - -	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0			no	4140";
	private static final String MAIN_DATAFILE_RAW_PROFILE = "Manufacturer Type Z11 A1	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	10,0	0,0	0,0	0,0	0,0	0,0	0,0	10,0	10,0	10,0	10,0	10,0	10,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	0,0	10,0	10,0	10,0	10,0	10,0	10,0	0,0	0,0	0,0	0,0	0,0	0,0			no	4140";

	private final String[] rawProfileParts = MAIN_DATAFILE_RAW_PROFILE.split("\t");
	private final String databaseFolder;
	private final FileHandler fileHandler;
	private final int centerValue;
	private List<Database> databases;
	private ProfileParts profileParts;

	public CmuProfileData(String databaseFolder, int centerValue, FileHandler fileHandler, ProfileParts profileParts) {
		this.databaseFolder = databaseFolder;
		this.centerValue = centerValue;
		this.fileHandler = fileHandler;
		this.profileParts = profileParts;
	}

	public void setProfileParts(ProfileParts profileParts) {
		this.profileParts = profileParts;
	}

	public List<Database> getDatabases() {
		return databases;
	}

	public void createEmptyDatabaseFolder(int serial) {
		fileHandler.createDirectory(databaseFolder + "\\" + serial);
		createDefaultDataFile(serial);
		createTacDataFile(serial);
	}

	private void clearTacDataFile(int serial) {
		List<String> rawText = fileHandler.readUtf8TextToList(databaseFolder + "\\" + serial + "\\" + TAC_DATAFILE, false);
		if (rawText != null && rawText.size() > 1) {
			createTacDataFile(serial);
		}
	}

	private void clearBackupFiles(int serial) {
		String tacBackupPath = databaseFolder + "\\" + serial + "\\" + TAC_BACKUP_FILE;
		String mainBackupPath = databaseFolder + "\\" + serial + "\\" + MAIN_BACKUP_FILE;
		if (fileHandler.fileExists(tacBackupPath)) {
			fileHandler.deleteFile(tacBackupPath);
		}
		if (fileHandler.fileExists(mainBackupPath)) {
			fileHandler.deleteFile(mainBackupPath);
		}
	}

	public void createTacDataFile(int serial) {
		fileHandler.writeUtf8Text(databaseFolder + "\\" + serial + "\\" + TAC_DATAFILE,
				Collections.singletonList(TAC_DATAFILE_HEADER), false);
	}

	public void createDefaultDataFile(int serial) {
		fileHandler.writeUtf8Text(databaseFolder + "\\" + serial + "\\" + DEFAULT_DATAFILE,
				Collections.singletonList(DEFAULT_DATAFILE_HEADER + "\n" + DEFAULT_DATAFILE_DEFAULT_PROFILE), false);
	}

	public void writeDatabase(CmuDatabase database, boolean cleanup) {
		List<String> newData = new ArrayList<>();
		newData.add(MAIN_DATAFILE_HEADER);
		newData.add(MAIN_DATAFILE_EMPTY_PROFILE);
		for (Profile profile : database.getProfiles()) {
			newData.add(getUpdatedData(profile));
		}
		fileHandler.writeUtf8Text(databaseFolder + "\\" + database.getSerial() + "\\" + MAIN_DATAFILE, newData, false);
		if (cleanup) {
			clearTacDataFile(database.getSerial());
			clearBackupFiles(database.getSerial());
		}
	}

	public List<Database> readDatabases(List<String> folderNames) {
		databases = new ArrayList<>();
		for (String folder : folderNames) {
			databases.add(new CmuDatabase(Integer.parseInt(folder)));
		}
		for (Database database : databases) {
			List<String> databaseData = fileHandler.readUtf8TextToList(databaseFolder + "\\" + database.getSerial() + "\\" + MAIN_DATAFILE, false);
			if (databaseData == null) {
				return new ArrayList<>();
			}
			for (int i = 2; i < databaseData.size(); i++) {
				String[] parts = databaseData.get(i).split("\t");
				if (!parts[0].equals("- - -")) {
					String[] phoneParts = parts[0].split(" ");
					String manufacturer = phoneParts[0];
					String box;
					String position;
					int typeEnd;
					if (!manufacturer.equals("Generic")) {
						position = phoneParts[phoneParts.length - 1];
						box = profileParts.shortToLongBox(phoneParts[phoneParts.length - 2]);
						typeEnd = phoneParts.length - 2;
					} else {
						box = "RF_Cable";
						position = "---";
						typeEnd = phoneParts.length;
					}
					String type = "";
					for (int pointer = 1; pointer < typeEnd; pointer++) {
						if (pointer != 1) {
							type = type.concat(" ");
						}
						type = type.concat(phoneParts[pointer]);
					}
					CmuProfile profile = new CmuProfile(database.getSerial(), type, manufacturer, box, position, processValues(parts), centerValue);
					profile.checkCondition();
					database.addProfile(profile);
				}
			}
			database.sortProfiles();
		}
		return databases;
	}

	public String getUpdatedData(Profile profile) {
		String[] parts = rawProfileParts.clone();
		String phone;
		if (!profile.getManufacturer().equals("Generic")) {
			phone = profile.getName() + " " + profileParts.longToShortBox(profile.getBox()) + " " + profile.getPosition();
		} else {
			phone = profile.getName();
		}
		parts[0] = phone;
		int pointer = 11;
		for (int i = 0; i < 3; i++) {
			parts[pointer] = String.valueOf(profile.getGsm900InValues()[i]).replace('.', ',');
			pointer++;
			parts[pointer] = String.valueOf(profile.getGsm900OutValues()[i]).replace('.', ',');
			pointer++;
		}
		for (int i = 0; i < 3; i++) {
			parts[pointer] = String.valueOf(profile.getGsm1800InValues()[i]).replace('.', ',');
			pointer++;
			parts[pointer] = String.valueOf(profile.getGsm1800OutValues()[i]).replace('.', ',');
			pointer++;
		}
		pointer = 29;
		for (int i = 0; i < 3; i++) {
			parts[pointer] = String.valueOf(profile.getWcdma1InValues()[i]).replace('.', ',');
			pointer++;
			parts[pointer] = String.valueOf(profile.getWcdma1OutValues()[i]).replace('.', ',');
			pointer++;
		}
		pointer = 69;
		for (int i = 0; i < 3; i++) {
			parts[pointer] = String.valueOf(profile.getWcdma8InValues()[i]).replace('.', ',');
			pointer++;
			parts[pointer] = String.valueOf(profile.getWcdma8OutValues()[i]).replace('.', ',');
			pointer++;
		}
		String output = "";
		for (int i = 0; i < parts.length; i++) {
			if (i == 0) {
				output = output.concat(parts[i]);
			} else {
				output = output.concat("\t" + parts[i]);
			}
		}
		return output;
	}


	private CmuProfileValues processValues(String[] parts) {
		CmuProfileValues values = new CmuProfileValues();
		int rawPointer = 11;
		boolean setIn = true;
		int valuePointer = 0;
		try {
			for (int i = 0; i < 6; i++) {
				if (setIn) {
					values.setGsm900InValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
				} else {
					values.setGsm900OutValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
					valuePointer++;
				}
				setIn = !setIn;
			}
			valuePointer = 0;
			for (int i = 6; i < 12; i++) {
				if (setIn) {
					values.setGsm1800InValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
				} else {
					values.setGsm1800OutValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
					valuePointer++;
				}
				setIn = !setIn;
			}
			rawPointer = 29;
			valuePointer = 0;
			for (int i = 0; i < 6; i++) {
				if (setIn) {
					values.setWcdma1InValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
				} else {
					values.setWcdma1OutValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
					valuePointer++;
				}
				setIn = !setIn;
			}
			rawPointer = 69;
			valuePointer = 0;
			for (int i = 0; i < 6; i++) {
				if (setIn) {
					values.setWcdma8InValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
				} else {
					values.setWcdma8OutValue(Double.parseDouble(parts[rawPointer + i].replace(',', '.')), valuePointer);
					valuePointer++;
				}
				setIn = !setIn;
			}
		} catch (NumberFormatException exception) {
			values = new CmuProfileValues();
		}
		return values;
	}
}
