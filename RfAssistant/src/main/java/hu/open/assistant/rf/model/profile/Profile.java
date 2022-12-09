package hu.open.assistant.rf.model.profile;

import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.log.batch.ProfileLogBatch;
import hu.open.assistant.rf.model.log.event.ProfileLogEvent;
import hu.open.assistant.rf.model.profile.values.ProfileValues;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical representation of a generic RF profile. Beside type specific information it stores the attenuation values,
 * the complete log history and a compensation. The profile can check its condition based on the attenuation values and
 * those values can be modified with a compensation. It can revert itself to the last state when possible and reset its
 * values to the given center value when needed. Provides generic information from itself.
 */

public abstract class Profile implements Comparable<Profile> {

    protected static final int NORMAL_LIMIT = 25;
    protected static final int ACCEPTABLE_LIMIT = 30;

    protected String type;
    protected String manufacturer;
    protected String box;
    protected String position;
    protected int serial;
    protected Compensation compensation;
    protected ProfileValues values;
    protected int condition;
    protected ProfileLogBatch logBatch;
    protected int centerValue;
    protected boolean created = false;
    protected boolean reverted = false;
    protected TesterType testerType;

    public Profile(int serial, TesterType testerType, String type, String manufacturer, String box, String position, ProfileValues values, int centerValue) {
        this.serial = serial;
        this.testerType = testerType;
        this.type = type;
        this.manufacturer = manufacturer;
        this.box = box;
        this.position = position;
        this.logBatch = null;
        this.centerValue = centerValue;
        this.values = values;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public ProfileLogBatch getLogBatch() {
        return logBatch;
    }

    public String getBox() {
        return box;
    }

    public String getName() {
        return manufacturer + " " + type;
    }

    public String getPosition() {
        return position;
    }

    public double[] getWcdma1InValues() {
        return values.getWcdma1InValues();
    }

    public double[] getWcdma1OutValues() {
        return values.getWcdma1OutValues();
    }

    public double[] getWcdma8InValues() {
        return values.getWcdma8InValues();
    }

    public double[] getWcdma8OutValues() {
        return values.getWcdma8OutValues();
    }

    public double[] getGsm900InValues() {
        return values.getGsm900InValues();
    }

    public double[] getGsm900OutValues() {
        return values.getGsm900OutValues();
    }

    public double[] getGsm1800InValues() {
        return values.getGsm1800InValues();
    }

    public double[] getGsm1800OutValues() {
        return values.getGsm1800OutValues();
    }

    public int getCondition() {
        return condition;
    }

    public boolean isCompensated() {
        return !compensation.isEmpty();
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean isCreated() {
        return created;
    }

    public Compensation getCompensation() {
        return compensation;
    }

    public int getSerial() {
        return serial;
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setLogBatch(ProfileLogBatch logBatch) {
        this.logBatch = logBatch;
    }

    public void setReverted(boolean reverted) {
        this.reverted = reverted;
    }

    public boolean isReverted() {
        return reverted;
    }

    public void revertCompensation() {
        if (!isCompensated() && isRevertPossible()) {
            compensation.copyCompensation(logBatch.getLatestLog().getCompensation());
            compensation.negateCompensation();
            addCompensation(compensation);
            reverted = true;
        } else {
            reverted = false;
            compensation.negateCompensation();
            addCompensation(compensation);
            compensation.resetWholeCompensation();
        }
    }

    public boolean isRevertPossible() {
        return logBatch != null && logBatch.getLatestLog().getEvent() == ProfileLogEvent.COMPENSATION;
    }

    public void createCenterCompensation() {
        for (int i = 0; i < 3; i++) {
            compensation.addCompensation("wcdma1_tx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getWcdma1InValues()[i] - centerValue) * -1));
            compensation.addCompensation("wcdma1_rx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getWcdma1OutValues()[i] - centerValue) * -1));
            compensation.addCompensation("wcdma8_tx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getWcdma8InValues()[i] - centerValue) * -1));
            compensation.addCompensation("wcdma8_rx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getWcdma8OutValues()[i] - centerValue) * -1));
            compensation.addCompensation("gsm900_tx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getGsm900InValues()[i] - centerValue) * -1));
            compensation.addCompensation("gsm900_rx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getGsm900OutValues()[i] - centerValue) * -1));
            compensation.addCompensation("gsm1800_tx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getGsm1800InValues()[i] - centerValue) * -1));
            compensation.addCompensation("gsm1800_rx", i, NumberHelper
                    .oneDecimalPlaceOf((values.getGsm1800OutValues()[i] - centerValue) * -1));
        }
    }

    public void addCompensation(Compensation compensation) {
        this.compensation.copyCompensation(compensation);
        for (int i = 0; i < 3; i++) {
            values.getWcdma1OutValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getWcdma1OutValues()[i] + compensation.getWcdma1RxValues()[i]);
            values.getWcdma1InValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getWcdma1InValues()[i] + compensation.getWcdma1TxValues()[i]);
            values.getWcdma8OutValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getWcdma8OutValues()[i] + compensation.getWcdma8RxValues()[i]);
            values.getWcdma8InValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getWcdma8InValues()[i] + compensation.getWcdma8TxValues()[i]);
            values.getGsm900OutValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getGsm900OutValues()[i] + compensation.getGsm900RxValues()[i]);
            values.getGsm900InValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getGsm900InValues()[i] + compensation.getGsm900TxValues()[i]);
            values.getGsm1800OutValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getGsm1800OutValues()[i] + compensation.getGsm1800RxValues()[i]);
            values.getGsm1800InValues()[i] = NumberHelper.oneDecimalPlaceOf(values.getGsm1800InValues()[i] + compensation.getGsm1800TxValues()[i]);
        }
    }

    public void checkCondition() {
        if (values.anyValueExceedLimit(ACCEPTABLE_LIMIT)) {
            condition = 3;
        } else if (values.anyValueExceedLimit(NORMAL_LIMIT)) {
            condition = 2;
        } else {
            condition = 1;
        }
    }

    protected String addOverload(String text, double[] inValues, double[] outValues) {
        for (int i = 0; i < 3; i++) {
            if (inValues[i] > ACCEPTABLE_LIMIT || outValues[i] > ACCEPTABLE_LIMIT) {
                return text + " ← (!)";
            }
        }
        return text;
    }

    public List<String> getInfo() {
        String emptyLine = "";
        List<String> text = new ArrayList<>();
        text.add("CMU 200 RF profil");
        text.add(emptyLine);
        text.add("Állomás száma: " + serial);
        text.add(emptyLine);
        text.add("Gyártó és típus:");
        text.add(getName());
        text.add(emptyLine);
        text.add("Shield Box típusa:");
        text.add(box);
        text.add(emptyLine);
        text.add("Pozíció: " + position);
        text.add(emptyLine);
        List<String> valuesText = getWcdmaGsmInfo();
        text.addAll(valuesText);
        text.add(emptyLine);
        text.add("Történet:");
        if (logBatch != null) {
            text.addAll(logBatch.getHistory(false, false));
        } else {
            text.add("nincs adat");
        }
        return text;
    }

    protected List<String> getWcdmaGsmInfo() {
        List<String> text = new ArrayList<>();
        text.add(addOverload("WCDMA Band 1 csillapítás:", values.getWcdma1InValues(), values.getWcdma1OutValues()));
        text.add("Low - IN: " + values.getWcdma1InValues()[0] + wcdma1TxCompText(0) + " OUT: " + values.getWcdma1OutValues()[0] + wcdma1RxCompText(0));
        text.add("Mid - IN: " + values.getWcdma1InValues()[1] + wcdma1TxCompText(1) + " OUT: " + values.getWcdma1OutValues()[1] + wcdma1RxCompText(1));
        text.add("High - IN: " + values.getWcdma1InValues()[2] + wcdma1TxCompText(2) + " OUT: " + values.getWcdma1OutValues()[2] + wcdma1RxCompText(2));
        text.add("");
        text.add(addOverload("WCDMA Band 8 csillapítás:", values.getWcdma8InValues(), values.getWcdma8OutValues()));
        text.add("Low - IN: " + values.getWcdma8InValues()[0] + wcdma8TxCompText(0) + " OUT: " + values.getWcdma8OutValues()[0] + wcdma8RxCompText(0));
        text.add("Mid - IN: " + values.getWcdma8InValues()[1] + wcdma8TxCompText(1) + " OUT: " + values.getWcdma8OutValues()[1] + wcdma8RxCompText(1));
        text.add("High - IN: " + values.getWcdma8InValues()[2] + wcdma8TxCompText(2) + " OUT: " + values.getWcdma8OutValues()[2] + wcdma8RxCompText(2));
        text.add("");
        text.add(addOverload("GSM 900 csillapítás:", values.getGsm900InValues(), values.getGsm900OutValues()));
        text.add("Low - IN: " + values.getGsm900InValues()[0] + gsm900TxCompText(0) + " OUT: " + values.getGsm900OutValues()[0] + gsm900RxCompText(0));
        text.add("Mid - IN: " + values.getGsm900InValues()[1] + gsm900TxCompText(1) + " OUT: " + values.getGsm900OutValues()[1] + gsm900RxCompText(1));
        text.add("High - IN: " + values.getGsm900InValues()[2] + gsm900TxCompText(2) + " OUT: " + values.getGsm900OutValues()[2] + gsm900RxCompText(2));
        text.add("");
        text.add(addOverload("GSM 1800 csillapítás:", values.getGsm1800InValues(), values.getGsm1800OutValues()));
        text.add("Low - IN: " + values.getGsm1800InValues()[0] + gsm1800TxCompText(0) + " OUT: " + values.getGsm1800OutValues()[0] + gsm1800RxCompText(0));
        text.add("Mid - IN: " + values.getGsm1800InValues()[1] + gsm1800TxCompText(1) + " OUT: " + values.getGsm1800OutValues()[1] + gsm1800RxCompText(1));
        text.add("High - IN: " + values.getGsm1800InValues()[2] + gsm1800TxCompText(2) + " OUT: " + values.getGsm1800OutValues()[2] + gsm1800RxCompText(2));
        return text;
    }

    protected String wcdma1RxCompText(int i) {
        if (compensation.getWcdma1RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getWcdma1OutValues()[i] - compensation.getWcdma1RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String wcdma1TxCompText(int i) {
        if (compensation.getWcdma1TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getWcdma1InValues()[i] - compensation.getWcdma1TxValues()[i]) + ") ";
        }
        return "";
    }

    protected String wcdma8RxCompText(int i) {
        if (compensation.getWcdma8RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getWcdma8OutValues()[i] - compensation.getWcdma8RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String wcdma8TxCompText(int i) {
        if (compensation.getWcdma8TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getWcdma8InValues()[i] - compensation.getWcdma8TxValues()[i]) + ") ";
        }
        return "";
    }

    protected String gsm900RxCompText(int i) {
        if (compensation.getGsm900RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getGsm900OutValues()[i] - compensation.getGsm900RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String gsm900TxCompText(int i) {
        if (compensation.getGsm900TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getGsm900InValues()[i] - compensation.getGsm900TxValues()[i]) + ") ";
        }
        return "";
    }

    protected String gsm1800RxCompText(int i) {
        if (compensation.getGsm1800RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getGsm1800OutValues()[i] - compensation.getGsm1800RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String gsm1800TxCompText(int i) {
        if (compensation.getGsm1800TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(values.getGsm1800InValues()[i] - compensation.getGsm1800TxValues()[i]) + ") ";
        }
        return "";
    }

    @Override
    public int compareTo(Profile other) {
        return getName().compareTo(other.getName());
    }
}

