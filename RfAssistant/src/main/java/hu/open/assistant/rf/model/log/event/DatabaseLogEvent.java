package hu.open.assistant.rf.model.log.event;

/**
 * Event types for database log.
 */
public enum DatabaseLogEvent {
    CHECK,
    BACKUP,
    RESTORE;

    private final String name;

    DatabaseLogEvent() {
        name = this.toString().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public static DatabaseLogEvent getByName(String name) {
        switch (name) {
            case "check":
                return DatabaseLogEvent.CHECK;
            case "backup":
                return DatabaseLogEvent.BACKUP;
            case "restore":
                return DatabaseLogEvent.RESTORE;
            default:
                return null;
        }
    }
}

