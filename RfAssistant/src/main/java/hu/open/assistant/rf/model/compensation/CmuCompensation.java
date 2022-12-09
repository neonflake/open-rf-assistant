package hu.open.assistant.rf.model.compensation;

/**
 * Stores and holds together attenuation values for a CMU type (profile) compensation. It has the same functionalities
 * as the generic variant.
 */
public class CmuCompensation extends Compensation {

    public CmuCompensation(int serial, String name) {
        super(serial, name);
    }
}
