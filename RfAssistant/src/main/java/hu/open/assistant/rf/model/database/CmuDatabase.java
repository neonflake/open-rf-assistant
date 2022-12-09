package hu.open.assistant.rf.model.database;

import hu.open.assistant.rf.model.TesterType;

/**
 * Logical representation of a CMU type RF attenuation database. It has the same functionalities as the generic variant.
 */
public class CmuDatabase extends Database {

    public CmuDatabase(int serial) {
        super(serial, TesterType.CMU);
    }
}