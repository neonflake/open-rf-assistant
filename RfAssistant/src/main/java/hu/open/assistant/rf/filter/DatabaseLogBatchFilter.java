package hu.open.assistant.rf.filter;

import hu.open.assistant.rf.model.log.batch.DatabaseLogBatch;

import java.util.List;

/**
 * Helper class that filters DatabaseLogBatches from the provided list.
 */
public class DatabaseLogBatchFilter {

    private DatabaseLogBatchFilter() {

    }

    public static DatabaseLogBatch getDatabaseLogBatchBySerial(List<DatabaseLogBatch> databaseLogBatches, int serial) {
        for (DatabaseLogBatch databaseLogBatch : databaseLogBatches) {
            if (databaseLogBatch.getSerial() == serial) {
                return databaseLogBatch;
            }
        }
        return null;
    }
}

