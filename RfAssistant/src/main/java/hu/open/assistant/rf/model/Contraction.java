package hu.open.assistant.rf.model;

/**
 * Simple logical contraction with normal and short name variant.
 */
public class Contraction implements Comparable<Contraction> {
    String name;
    String shortName;

    public Contraction(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public int compareTo(Contraction other) {
        return this.getName().compareTo(other.getName());
    }
}
