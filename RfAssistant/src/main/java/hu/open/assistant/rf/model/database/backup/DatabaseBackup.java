package hu.open.assistant.rf.model.database.backup;

import hu.open.assistant.rf.model.Equipment;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.database.Database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Logical representation of a generic database backup. Beside a logical attenuation database it stores header
 * information about it and the date-time the backup was created. It also stores equipment information related to the
 * profiles in the database and can give text based information about itself.
 */
public abstract class DatabaseBackup {
    protected final LocalDateTime dateTime;
    protected final int serial;
    protected final TesterType testerType;
    protected final List<Equipment> equipments = new ArrayList<>();
    protected Database database = null;

    public DatabaseBackup(int serial, LocalDateTime dateTime, TesterType type) {
        this.serial = serial;
        this.dateTime = dateTime;
        this.testerType = type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getSerial() {
        return serial;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public Database getDatabase() {
        return database;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public List<String> getInfo() {
        List<String> text = new ArrayList<>();
        text.add("Biztonsági mentés tartalma:\n");
        if (database != null) {
            text.addAll(database.getInfo());
        }
        return text;
    }
}
