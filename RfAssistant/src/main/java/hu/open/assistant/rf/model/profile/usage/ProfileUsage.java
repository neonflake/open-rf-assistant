package hu.open.assistant.rf.model.profile.usage;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores different IMEI numbers for a given phone type for statistical purposes.
 */
public class ProfileUsage implements Comparable<ProfileUsage> {
    private final String type;
    private final String manufacturer;
    private final List<Long> imeis;

    public ProfileUsage(String type, String manufacturer) {
        this.type = type;
        this.manufacturer = manufacturer;
        imeis = new ArrayList<>();
    }

    public String getName() {
        return manufacturer + " " + type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getUnitCount() {
        return imeis.size();
    }

    public void addSample(long imei) {
        if (!imeis.contains(imei)) {
            imeis.add(imei);
        }
    }

    @Override
    public int compareTo(ProfileUsage other) {
        return Integer.compare(other.getUnitCount(), getUnitCount());
    }
}
