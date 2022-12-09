package hu.open.assistant.rf.filter;

import hu.open.assistant.rf.model.log.batch.ProfileLogBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that filters ProfileLogBatches from the provided list.
 */
public class ProfileLogBatchFilter {

    private ProfileLogBatchFilter() {

    }

    public static List<ProfileLogBatch> getProfileLogBatchesBySerial(List<ProfileLogBatch> profileLogBatches, int serial) {
        List<ProfileLogBatch> filteredProfileLogBatches = new ArrayList<>();
        for (ProfileLogBatch profileLogBatch : profileLogBatches) {
            if (profileLogBatch.getSerial() == serial) {
                filteredProfileLogBatches.add(profileLogBatch);
            }
        }
        return filteredProfileLogBatches;
    }

    public static ProfileLogBatch getProfileLogBatchBySerialAndByName(List<ProfileLogBatch> profileLogBatches, int serial, String name) {
        for (ProfileLogBatch profileLogBatch : profileLogBatches) {
            if (profileLogBatch.getSerial() == serial && profileLogBatch.getName().equals(name)) {
                return profileLogBatch;
            }
        }
        return null;
    }

    public static List<ProfileLogBatch> getProfileLogBatchesByNameLike(List<ProfileLogBatch> profileLogBatches, String name) {
        List<ProfileLogBatch> filteredProfileLogBatches = new ArrayList<>();
        for (ProfileLogBatch profileLogBatch : profileLogBatches) {
            if (profileLogBatch.getName().contains(name)) {
                filteredProfileLogBatches.add(profileLogBatch);
            }
        }
        return filteredProfileLogBatches;
    }
}
