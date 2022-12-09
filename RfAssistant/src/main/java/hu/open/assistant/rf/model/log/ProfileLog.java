package hu.open.assistant.rf.model.log;

import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.log.event.ProfileLogEvent;
import hu.open.assistant.rf.model.TesterType;

import java.time.LocalDateTime;

/**
 * Logical representation of a profile log. It stores the date-time, event type and a comment text or a compensation
 * depending on the event type.
 */
public class ProfileLog implements Comparable<ProfileLog> {
    private final LocalDateTime dateTime;
    private final ProfileLogEvent event;
    private final int serial;
    private final TesterType testerType;
    private final String name;
    private String comment = "";
    private Compensation compensation = null;

    public ProfileLog(LocalDateTime dateTime, ProfileLogEvent event, int serial, TesterType testerType, String name, Compensation compensation) {
        this(dateTime, event, serial, testerType, name);
        this.compensation = compensation;
    }

    public ProfileLog(LocalDateTime dateTime, ProfileLogEvent event, int serial, TesterType testerType, String name, String comment) {
        this(dateTime, event, serial, testerType, name);
        this.comment = comment;
    }

    public ProfileLog(LocalDateTime dateTime, ProfileLogEvent event, int serial, TesterType testerType, String name) {
        this.dateTime = dateTime;
        this.event = event;
        this.serial = serial;
        this.testerType = testerType;
        this.name = name;
    }

    public int getSerial() {
        return serial;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public String getName() {
        return name;
    }

    public ProfileLogEvent getEvent() {
        return event;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Compensation getCompensation() {
        return compensation;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public int compareTo(ProfileLog other) {
        if (this.dateTime.isBefore(other.getDateTime())) {
            return 1;
        } else if (this.dateTime.isAfter(other.getDateTime())) {
            return -1;
        }
        return 0;
    }
}
