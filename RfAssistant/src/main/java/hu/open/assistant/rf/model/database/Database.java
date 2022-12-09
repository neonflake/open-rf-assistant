package hu.open.assistant.rf.model.database;

import hu.open.assistant.rf.filter.ProfileFilter;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.log.batch.DatabaseLogBatch;
import hu.open.assistant.rf.model.profile.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Logical representation of an actual generic RF attenuation database. It stores the RF profiles and related
 * statistics values. It also contains the full database log and flags for profile modification or creation. The
 * database can be reverted to its default state and provides a text based information.
 */
public abstract class Database {

    protected List<Profile> profiles;
    protected List<Profile> removedProfiles;
    protected TesterType testerType;
    protected int serial;
    protected int normalProfiles;
    protected int acceptableProfiles;
    protected int problematicProfiles;
    protected boolean modified;
    protected boolean profileCreated;
    protected DatabaseLogBatch logBatch = null;

    public Database(int serial, TesterType testerType) {
        this.serial = serial;
        this.testerType = testerType;
        profiles = new ArrayList<>();
        removedProfiles = new ArrayList<>();
        normalProfiles = 0;
        acceptableProfiles = 0;
        problematicProfiles = 0;
        modified = false;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public void setLogBatch(DatabaseLogBatch logBatch) {
        this.logBatch = logBatch;
    }

    public List<Profile> getRemovedProfiles() {
        return removedProfiles;
    }

    public void setProfileCreated(boolean profileCreated) {
        this.profileCreated = profileCreated;
    }

    public int getSerial() {
        return serial;
    }

    public void resetProfiles() {
        profiles.addAll(removedProfiles);
        sortProfiles();
        removedProfiles.clear();
        for (Profile profile : profiles) {
            if (profile.isCompensated()) {
                profile.revertCompensation();
            }
            if (profile.getTesterType() == TesterType.CMW) {
                ((CmwProfile) profile).revertTacList();
            }
        }
        modified = false;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void sortProfiles() {
        Collections.sort(profiles);
    }

    public boolean isModified() {
        for (Profile profile : profiles) {
            if (profile.isCompensated()) {
                return true;
            }
        }
        return removedProfiles.size() > 0 || profileCreated;
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile);
        removedProfiles.add(profile);
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
        checkProfile(profile);
    }

    public Profile getProfileByName(String name) {
        return ProfileFilter.getProfileByName(profiles, name);
    }

    protected void checkProfile(Profile profile) {
        if (profile.getCondition() == 1) {
            normalProfiles++;
        } else if (profile.getCondition() == 2) {
            acceptableProfiles++;
        } else if (profile.getCondition() == 3) {
            problematicProfiles++;
        }
    }

    public List<String> getInfo() {
        List<String> text = new ArrayList<>();
        text.add(testerType.getFullName() + " csillapítás adatbázis");
        text.add("");
        text.addAll(getDetailedInfo());
        text.add("");
        text.add("Történet:");
        if (logBatch != null) {
            text.addAll(logBatch.getHistory(false));
        } else {
            text.add("nincs adat");
        }
        return text;
    }

    protected List<String> getDetailedInfo() {
        List<String> text = new ArrayList<>();
        text.add("Állomás száma: " + serial);
        text.add("");
        text.add("Profilok száma: " + profiles.size());
        text.add("    - normális (max. 25 dB) : " + normalProfiles);
        text.add("    - elfogadható (25 dB felett) : " + acceptableProfiles);
        text.add("    - problémás (30 dB felett) : " + problematicProfiles);
        return text;
    }
}

