package hu.open.assistant.update.model;

/**
 * Defines the available update targets. An update target has an application name (with related launch file) and the
 * main class.
 */
public enum UpdateTarget {
    RF("RFAssistant", "RfAssistant"),
    UNKNOWN("", "");

    private final String launchFile;
    private final String mainClass;
    private final String name;

    UpdateTarget(String name, String mainClass) {
        this.name = name;
        this.launchFile = name.concat(".jar");
        this.mainClass = mainClass;
    }

    public String getName() {
        return name;
    }

    public String getLaunchFile() {
        return launchFile;
    }

    public String getMainClass() {
        return mainClass;
    }
}
