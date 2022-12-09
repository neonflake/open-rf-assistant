package hu.open.assistant.rf.model.database.backup;

import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;

/**
 * Logical representation of a CMW type database backup. It has the same functionalities as the generic variant.
 */
public class CmwDatabaseBackup extends DatabaseBackup {

    public CmwDatabaseBackup(int serial, LocalDateTime dateTime) {
        super(serial, dateTime, TesterType.CMW);
    }
}

