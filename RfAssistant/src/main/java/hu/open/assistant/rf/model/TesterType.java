package hu.open.assistant.rf.model;

/**
 * Defines the RF tester types available. The tester type has a full and normal name. A tester type can be retrieved by
 * name.
 */
public enum TesterType {
    CMU("CMU 200"),
    CMW("CMW 290");

    private final String fullName;
    private final String name;

    TesterType(String fullName) {
        this.fullName = fullName;
        this.name = this.toString().toLowerCase();
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    public static TesterType getByName(String name) {
        switch (name) {
            case "cmu":
                return TesterType.CMU;
            case "cmw":
                return TesterType.CMW;
            default:
                return null;
        }
    }
}
