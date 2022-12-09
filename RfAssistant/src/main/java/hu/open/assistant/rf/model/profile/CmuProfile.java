package hu.open.assistant.rf.model.profile;

import hu.open.assistant.rf.model.compensation.CmuCompensation;
import hu.open.assistant.rf.model.profile.values.CmuProfileValues;
import hu.open.assistant.rf.model.TesterType;

/**
 * Logical representation of a logical CMU type RF profile. It has the same functionalities as the generic variant.
 * Initialised with a CMU type compensation.
 */
public class CmuProfile extends Profile {

    public CmuProfile(int serial, String type, String manufacturer, String box, String position, CmuProfileValues values, int centerValue) {
        super(serial, TesterType.CMU, type, manufacturer, box, position, values, centerValue);
        compensation = new CmuCompensation(serial, manufacturer + " " + type);
    }
}