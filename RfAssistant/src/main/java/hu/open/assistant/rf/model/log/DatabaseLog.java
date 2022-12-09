package hu.open.assistant.rf.model.log;

import hu.open.assistant.rf.model.log.event.DatabaseLogEvent;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;

/**
 * Logical representation of a database log. It stores the date-time, event type and the database serial.
 */
public class DatabaseLog implements Comparable<DatabaseLog> {

    private final LocalDateTime dateTime;
    private final DatabaseLogEvent event;
    private final int serial;
    private final TesterType testerType;

    public DatabaseLog(LocalDateTime dateTime, DatabaseLogEvent event, int serial, TesterType testerType) {
        this.dateTime = dateTime;
        this.event = event;
        this.serial = serial;
        this.testerType = testerType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public DatabaseLogEvent getEvent() {
        return event;
    }

    public int getSerial() {
        return serial;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    @Override
    public int compareTo(DatabaseLog other) {
        if (this.dateTime.isBefore(other.getDateTime())) {
            return 1;
        } else if (this.dateTime.isAfter(other.getDateTime())) {
            return -1;
        }
        return 0;
    }
}
