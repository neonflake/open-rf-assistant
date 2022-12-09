package hu.open.assistant.rf.model.log.batch;

import hu.open.assistant.rf.model.log.DatabaseLog;
import hu.open.assistant.commons.util.DateHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and holds together database logs from the same RF database. Can create a database history in text format.
 */
public class DatabaseLogBatch implements Comparable<DatabaseLogBatch>, LogBatch {
    private final int serial;
    private final List<DatabaseLog> logs;

    public DatabaseLogBatch(int serial) {
        this.serial = serial;
        logs = new ArrayList<>();
    }

    public int getSerial() {
        return serial;
    }

    public void addLog(DatabaseLog log) {
        logs.add(log);
    }

    public void sortLogs() {
        Collections.sort(logs);
    }

    public List<DatabaseLog> getLogs() {
        return logs;
    }

    public LocalDateTime getLastModificationDate() {
        return getLatestLog().getDateTime();
    }

    public DatabaseLog getLatestLog() {
        return logs.get(0);
    }

    public List<String> getHistory(boolean wideFormat) {
        List<String> history = new ArrayList<>();
        for (DatabaseLog log : logs) {
            switch (log.getEvent()) {
                case CHECK:
                    if (wideFormat) {
                        history.add("    · ellenőrzés elvégezve: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    } else {
                        history.add("  • ellenőrzés elvégezve:\n    · " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    }
                    break;
                case BACKUP:
                    if (wideFormat) {
                        history.add("    · biztonsági másolat: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    } else {
                        history.add("  • biztonsági másolat:\n    · " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    }
                    break;
                case RESTORE:
                    if (wideFormat) {
                        history.add("    · teljes visszaállítás: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    } else {
                        history.add("  • teljes visszaállítás:\n    · " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    }
                    break;
            }
        }
        return history;
    }

    @Override
    public int compareTo(DatabaseLogBatch other) {
        return -1 * (getLastModificationDate().compareTo(other.getLastModificationDate()));
    }

    @Override
    public List<String> getInfo() {
        List<String> history = new ArrayList<>();
        history.add("  • Csillapítás adatbázis: " + serial);
        history.addAll(getHistory(true));
        history.add("\n");
        return history;
    }

    @Override
    public List<String> getExtendedInfo() {
        return getInfo();
    }
}
