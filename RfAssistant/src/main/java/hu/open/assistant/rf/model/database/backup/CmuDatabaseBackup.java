package hu.open.assistant.rf.model.database.backup;

import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logical representation of a CMU type database backup. Extends the generic variant with storing shortcuts related to
 * the profiles stored in the database.
 */
public class CmuDatabaseBackup extends DatabaseBackup {

    private List<Shortcut> shortcuts = null;

    public CmuDatabaseBackup(int serial, LocalDateTime dateTime) {
        super(serial, dateTime, TesterType.CMU);
    }

    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }

    public void setShortcuts(List<Shortcut> shortcuts) {
        this.shortcuts = shortcuts;
    }
}
