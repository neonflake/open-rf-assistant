package hu.open.assistant.rf.model.database;

import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.profile.Profile;

/**
 * Logical representation of a CMW type RF attenuation database. It extends the generic variants modification check with
 * TAC modification checking.
 */
public class CmwDatabase extends Database {

    public CmwDatabase(int serial) {
        super(serial, TesterType.CMW);
    }

    @Override
    public boolean isModified() {
        for (Profile profile : profiles) {
            if (((CmwProfile) profile).isTacModified()) {
                return true;
            }
        }
        return super.isModified();
    }
}
