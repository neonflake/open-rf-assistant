package hu.open.assistant.rf.filter;

import hu.open.assistant.rf.model.profile.Profile;

import java.util.List;

/**
 * Helper class that filters Profiles from the provided list.
 */
public class ProfileFilter {

    private ProfileFilter() {

    }

    public static Profile getProfileByName(List<Profile> profiles, String name) {
        for (Profile profile : profiles) {
            if (profile.getName().equals(name)) {
                return profile;
            }
        }
        return null;
    }
}
