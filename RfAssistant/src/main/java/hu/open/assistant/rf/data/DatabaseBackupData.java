package hu.open.assistant.rf.data;

import hu.open.assistant.rf.filter.EquipmentFilter;
import hu.open.assistant.rf.filter.ShortcutFilter;
import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.Equipment;
import hu.open.assistant.rf.model.ShieldBox;
import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.database.CmuDatabase;
import hu.open.assistant.rf.model.database.CmwDatabase;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.database.backup.CmuDatabaseBackup;
import hu.open.assistant.rf.model.database.backup.DatabaseBackup;
import hu.open.assistant.rf.model.profile.CmuProfile;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.rf.model.profile.values.CmuProfileValues;
import hu.open.assistant.rf.model.profile.values.CmwProfileValues;
import hu.open.assistant.rf.model.profile.values.ProfileValues;
import hu.open.assistant.commons.data.FileHandler;
import hu.open.assistant.commons.data.JsonParser;
import hu.open.assistant.commons.util.DateHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class which reads pre-processed attenuation databases and writes logical databases from or to the disk. Beside
 * the database and its profiles the backup file also contains extra position and script (for CMU) information related
 * to the profiles. Backup files are stored separately by each time a backup is made (CMU or CMW type) in JSON format.
 * The profiles, equipments and shortcuts are placed and handled within a logical profile backup (CMU or CMW type).
 * There is an option to delete an already existing backup file.
 */
public class DatabaseBackupData {

    private static final String DATAFILE_EXTENSION = ".json";

    private final JsonParser jsonParser;
    private final FileHandler fileHandler;
    private final String backupFolder;
    private ProfileParts profileParts;
    private List<Equipment> equipments;

    public DatabaseBackupData(String backupFolder, JsonParser jsonParser, FileHandler fileHandler, ProfileParts profileParts, List<Equipment> equipments) {
        this.backupFolder = backupFolder;
        this.jsonParser = jsonParser;
        this.fileHandler = fileHandler;
        this.profileParts = profileParts;
        this.equipments = equipments;
    }

    public void setProfileParts(ProfileParts profileParts) {
        this.profileParts = profileParts;
    }

    public void setEquipments(List<Equipment> equipments) {
        this.equipments = equipments;
    }

    private String getBackupFilename(int serial, LocalDateTime dateTime) {
        return serial + "_" + DateHelper.localDateTimeToFilename(dateTime) + DATAFILE_EXTENSION;
    }

    public void deleteBackup(DatabaseBackup databaseBackup) {
        String filename = getBackupFilename(databaseBackup.getSerial(), databaseBackup.getDateTime());
        fileHandler.deleteFile(backupFolder + "\\" + filename);
    }

    public void readDatabaseBackupContent(DatabaseBackup databaseBackup) {
        Database database;
        List<Shortcut> shortcuts = null;
        String filename = getBackupFilename(databaseBackup.getSerial(), databaseBackup.getDateTime());
        JSONObject rootJsonObject = jsonParser.readJsonObject(backupFolder + "\\" + filename, false);
        if (rootJsonObject == null) {
            System.out.println("Backup file validation failed: " + filename);
            return;
        }
        TesterType backupType = null;
        if (rootJsonObject.optString("testerType").equals("cmu")) {
            backupType = TesterType.CMU;
            shortcuts = new ArrayList<>();
        } else if (rootJsonObject.optString("testerType").equals("cmw")) {
            backupType = TesterType.CMW;
        }
        int serial = rootJsonObject.optInt("serial");
        LocalDateTime dateTime = DateHelper.isoTextDateTimeToLocalDateTime(rootJsonObject.getString("dateTime"));
        if (backupType == null || serial != databaseBackup.getSerial() || !dateTime.equals(databaseBackup.getDateTime())) {
            System.out.println("Backup file validation failed: " + filename);
            return;
        }
        database = backupType == TesterType.CMU ? new CmuDatabase(serial) : new CmwDatabase(serial);
        JSONArray jsonArray = rootJsonObject.optJSONArray("profiles");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String profileType;
                String profileManufacturer;
                String profileBox;
                String profilePosition;
                long storeTac;
                Profile profile;
                Equipment equipment = new Equipment();
                ProfileValues values = backupType == TesterType.CMU ? new CmuProfileValues() : new CmwProfileValues();
                try {
                    profileType = jsonObject.getString("type");
                    profileManufacturer = jsonObject.getString("manufacturer");
                    profileBox = jsonObject.getString("box");
                    profilePosition = jsonObject.getString("position");
                    for (int j = 0; j < 3; j++) {
                        values.setWcdma1InValue(jsonObject.getJSONArray("wcdma1InValues").getDouble(j), j);
                        values.setWcdma1OutValue(jsonObject.getJSONArray("wcdma1OutValues").getDouble(j), j);
                        values.setWcdma8InValue(jsonObject.getJSONArray("wcdma8InValues").getDouble(j), j);
                        values.setWcdma8OutValue(jsonObject.getJSONArray("wcdma8OutValues").getDouble(j), j);
                        values.setGsm900InValue(jsonObject.getJSONArray("gsm900InValues").getDouble(j), j);
                        values.setGsm900OutValue(jsonObject.getJSONArray("gsm900OutValues").getDouble(j), j);
                        values.setGsm1800InValue(jsonObject.getJSONArray("gsm1800InValues").getDouble(j), j);
                        values.setGsm1800OutValue(jsonObject.getJSONArray("gsm1800OutValues").getDouble(j), j);
                    }
                    equipment.setType(profileType);
                    equipment.setManufacturer(profileManufacturer);
                    if (backupType == TesterType.CMU) {
                        equipment.setCmuPositionDetail(jsonObject.getString("positionDetail"));
                        profile = new CmuProfile(serial, profileType, profileManufacturer, profileBox, profilePosition, (CmuProfileValues) values, 0);
                        int listNumber = jsonObject.getInt("listNumber");
                        String script = jsonObject.getString("script");
                        String shortBox = jsonObject.getString("shortBox");
                        shortcuts.add(new Shortcut(listNumber, profileManufacturer, profileType, new ShieldBox(new Contraction(profileBox, shortBox)), profilePosition, script));
                    } else {
                        equipment.setCmwPositionDetail(jsonObject.getString("positionDetail"));
                        for (int j = 0; j < 3; j++) {
                            ((CmwProfileValues) values).setLte1InValue(jsonObject.getJSONArray("lte1InValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte1OutValue(jsonObject.getJSONArray("lte1OutValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte3InValue(jsonObject.getJSONArray("lte3InValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte3OutValue(jsonObject.getJSONArray("lte3OutValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte7InValue(jsonObject.getJSONArray("lte7InValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte7OutValue(jsonObject.getJSONArray("lte7OutValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte20InValue(jsonObject.getJSONArray("lte20InValues").getDouble(j), j);
                            ((CmwProfileValues) values).setLte20OutValue(jsonObject.getJSONArray("lte20OutValues").getDouble(j), j);
                        }
                        storeTac = jsonObject.getLong("storeTac");
                        JSONArray tacJsonArray = jsonObject.getJSONArray("tacList");
                        List<Long> tacList = new ArrayList<>();
                        for (int j = 0; j < tacJsonArray.length(); j++) {
                            tacList.add(tacJsonArray.getLong(j));
                        }
                        profile = new CmwProfile(serial, profileType, profileManufacturer, profileBox, profilePosition, (CmwProfileValues) values, 0, storeTac, tacList);
                    }
                    profile.checkCondition();
                    database.addProfile(profile);
                    equipment.initDefaultValues();
                    databaseBackup.getEquipments().add(equipment);
                } catch (JSONException exception) {
                    System.out.println("Backup processing error: ");
                    System.out.println(exception.getMessage());
                    return;
                }
            }
        }
        databaseBackup.setDatabase(database);
        if (backupType == TesterType.CMU) {
            ((CmuDatabaseBackup) databaseBackup).setShortcuts(shortcuts);
        }
    }

    public void writeDatabaseBackup(DatabaseBackup backup) {
        JSONObject rootJsonObject = new JSONObject();
        LocalDateTime dateTime = backup.getDateTime();
        rootJsonObject.put("serial", backup.getSerial());
        rootJsonObject.put("testerType", backup.getTesterType().getName());
        rootJsonObject.put("dateTime", DateHelper.localDateTimeToIsoTextDateTime(dateTime));
        JSONArray jsonArray = new JSONArray();
        for (Profile profile : backup.getDatabase().getProfiles()) {
            JSONObject jsonObject = new JSONObject();
            Equipment equipment = EquipmentFilter.getEquipmentByName(equipments, profile.getName());
            jsonObject.put("type", profile.getType());
            jsonObject.put("manufacturer", profile.getManufacturer());
            jsonObject.put("position", profile.getPosition());
            jsonObject.put("box", profile.getBox());
            jsonObject.put("wcdma1InValues", new JSONArray(profile.getWcdma1InValues()));
            jsonObject.put("wcdma1OutValues", new JSONArray(profile.getWcdma1OutValues()));
            jsonObject.put("wcdma8InValues", new JSONArray(profile.getWcdma8InValues()));
            jsonObject.put("wcdma8OutValues", new JSONArray(profile.getWcdma8OutValues()));
            jsonObject.put("gsm900InValues", new JSONArray(profile.getGsm900InValues()));
            jsonObject.put("gsm900OutValues", new JSONArray(profile.getGsm900OutValues()));
            jsonObject.put("gsm1800InValues", new JSONArray(profile.getGsm1800InValues()));
            jsonObject.put("gsm1800OutValues", new JSONArray(profile.getGsm1800OutValues()));
            if (backup.getTesterType() == TesterType.CMW) {
                jsonObject.put("lte1InValues", new JSONArray(((CmwProfile) profile).getLte1InValues()));
                jsonObject.put("lte1OutValues", new JSONArray(((CmwProfile) profile).getLte1OutValues()));
                jsonObject.put("lte3InValues", new JSONArray(((CmwProfile) profile).getLte3InValues()));
                jsonObject.put("lte3OutValues", new JSONArray(((CmwProfile) profile).getLte3OutValues()));
                jsonObject.put("lte7InValues", new JSONArray(((CmwProfile) profile).getLte7InValues()));
                jsonObject.put("lte7OutValues", new JSONArray(((CmwProfile) profile).getLte7OutValues()));
                jsonObject.put("lte20InValues", new JSONArray(((CmwProfile) profile).getLte20InValues()));
                jsonObject.put("lte20OutValues", new JSONArray(((CmwProfile) profile).getLte20OutValues()));
                jsonObject.put("storeTac", ((CmwProfile) profile).getStoreTac());
                jsonObject.put("tacList", new JSONArray(((CmwProfile) profile).getTacList()));
                if (equipment != null) {
                    jsonObject.put("positionDetail", equipment.getCmwPositionDetail());
                } else {
                    jsonObject.put("positionDetail", "");
                }
            } else {
                jsonObject.put("shortBox", profileParts.longToShortBox(profile.getBox()));
                Shortcut shortcut = ShortcutFilter.getShortcutByName(((CmuDatabaseBackup) backup).getShortcuts(), profile.getName());
                if (shortcut != null) {
                    jsonObject.put("script", shortcut.getScript());
                    jsonObject.put("listNumber", shortcut.getListNumber());
                } else {
                    jsonObject.put("script", "UNKNOWN");
                    jsonObject.put("listNumber", 1);
                }
                if (equipment != null) {
                    jsonObject.put("positionDetail", equipment.getCmuPositionDetail());
                } else {
                    jsonObject.put("positionDetail", "");
                }
            }
            jsonArray.put(jsonObject);
        }
        rootJsonObject.put("profiles", jsonArray);
        jsonParser.writeJsonObject(backupFolder + "\\" + getBackupFilename(backup.getSerial(), backup.getDateTime()), rootJsonObject);
    }
}
