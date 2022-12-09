package hu.open.assistant.rf.model.log.batch;

import java.util.List;

/**
 * Common interface for log batches to display information in text format.
 */
public interface LogBatch {

    List<String> getInfo();

    List<String> getExtendedInfo();

}
