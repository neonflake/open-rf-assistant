package hu.open.assistant.rf.model.log.event;

/**
 * Event types for profile log.
 */
public enum ProfileLogEvent {
    CREATION,
    COMPENSATION,
    REVERT;

    private final String name;

    ProfileLogEvent() {
        name = this.toString().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public static ProfileLogEvent getByName(String name) {
        switch (name) {
            case "creation":
                return ProfileLogEvent.CREATION;
            case "compensation":
                return ProfileLogEvent.COMPENSATION;
            case "revert":
                return ProfileLogEvent.REVERT;
            default:
                return null;
        }
    }
}

