package hu.open.assistant.rf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Logical representation of an RF shield box. It stores the logical positions and helps with handling the shorter
 * shield box names used with the CMUgo application.
 */
public class ShieldBox implements Comparable<ShieldBox> {

    Contraction contraction;
    List<String> positions;

    public ShieldBox(Contraction contraction) {
        this.contraction = contraction;
        positions = new ArrayList<>();
    }

    public void sortPositions() {
        Collections.sort(positions);
    }

    public String getName() {
        return contraction.getName();
    }

    public String getShortName() {
        return contraction.getShortName();
    }

    public Contraction getContraction() {
        return contraction;
    }

    public void addPosition(String position) {
        positions.add(position);
    }

    public void removePosition(String position) {
        positions.remove(position);
    }

    public List<String> getPositions() {
        return positions;
    }

    @Override
    public int compareTo(ShieldBox otherbox) {
        return this.getName().compareTo(otherbox.getName());
    }

}
