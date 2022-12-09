package hu.open.assistant.rf.model;

/**
 * Logical representation of a shortcut used by the CMUGo application. A shortcuts main purpose is to stores the RF
 * script associated to the given RF profile and to determine the list in which the profile is shown.
 */
public class Shortcut implements Comparable<Shortcut> {
    private int listNumber;
    private final String type;
    private String script;
    private final String manufacturer;
    private final ShieldBox box;
    private final String position;

    public Shortcut(int listNumber, String manufacturer, String type, ShieldBox box, String position, String script) {
        this.listNumber = listNumber;
        this.manufacturer = manufacturer;
        this.type = type;
        this.box = box;
        this.position = position;
        this.script = script;
    }

    public String getName() {
        return manufacturer + " " + type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public ShieldBox getBox() {
        return box;
    }

    public String getPosition() {
        return position;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

    public String getType() {
        return type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public int compareTo(Shortcut other) {
        return (getName().compareTo(other.getName()));
    }
}
