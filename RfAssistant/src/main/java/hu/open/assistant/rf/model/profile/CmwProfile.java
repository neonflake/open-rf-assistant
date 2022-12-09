package hu.open.assistant.rf.model.profile;

import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.profile.values.CmwProfileValues;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.rf.model.TesterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Logical representation of a logical CMW type RF profile. Extends the generic variant with additional LTE band
 * attenuation values and tac number storage. It is initialised with a CMW type compensation.
 */
public class CmwProfile extends Profile {
    protected List<Long> tacList;
    protected List<Long> defaultTacList;
    protected long storeTac;
    protected boolean tacError;

    public CmwProfile(int serial, String type, String manufacturer, String box, String position, CmwProfileValues values, int centerValue, long storeTac, List<Long> tacList) {
        super(serial, TesterType.CMW, type, manufacturer, box, position, values, centerValue);
        this.storeTac = storeTac;
        this.tacList = tacList;
        Collections.sort(tacList);
        defaultTacList = new ArrayList<>();
        defaultTacList.addAll(tacList);
        compensation = new CmwCompensation(serial, manufacturer + " " + type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public double[] getLte1OutValues() {
        return ((CmwProfileValues) values).getLte1OutValues();
    }

    public double[] getLte1InValues() {
        return ((CmwProfileValues) values).getLte1InValues();
    }

    public double[] getLte3OutValues() {
        return ((CmwProfileValues) values).getLte3OutValues();
    }

    public double[] getLte3InValues() {
        return ((CmwProfileValues) values).getLte3InValues();
    }

    public double[] getLte7OutValues() {
        return ((CmwProfileValues) values).getLte7OutValues();
    }

    public double[] getLte7InValues() {
        return ((CmwProfileValues) values).getLte7InValues();
    }

    public double[] getLte20OutValues() {
        return ((CmwProfileValues) values).getLte20OutValues();
    }

    public double[] getLte20InValues() {
        return ((CmwProfileValues) values).getLte20InValues();
    }

    public List<Long> getTacList() {
        return tacList;
    }

    public boolean isTacModified() {
        return (!tacList.equals(defaultTacList) || tacError);
    }

    public void enableTacError() {
        tacError = true;
    }

    public void revertTacList() {
        tacList.clear();
        tacList.addAll(defaultTacList);
    }

    public void updateTacList(List<Long> tacList) {
        Collections.sort(tacList);
        this.tacList = tacList;
    }

    public long getStoreTac() {
        return storeTac;
    }

    @Override
    public void createCenterCompensation() {
        super.createCenterCompensation();
        for (int i = 0; i < 3; i++) {
            compensation.addCompensation("lte1_tx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte1InValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte1_rx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte1OutValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte3_tx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte3InValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte3_rx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte3OutValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte7_tx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte7InValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte7_rx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte7OutValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte20_tx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte20InValues()[i] - centerValue) * -1));
            compensation.addCompensation("lte20_rx", i, NumberHelper.oneDecimalPlaceOf((((CmwProfileValues) values).getLte20OutValues()[i] - centerValue) * -1));
        }
    }

    @Override
    public void addCompensation(Compensation compensation) {
        CmwCompensation cmwCompensation = (CmwCompensation) compensation;
        for (int i = 0; i < 3; i++) {
            ((CmwProfileValues) values).getLte1OutValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte1OutValues()[i] + cmwCompensation.getLte1RxValues()[i]);
            ((CmwProfileValues) values).getLte1InValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte1InValues()[i] + cmwCompensation.getLte1TxValues()[i]);
            ((CmwProfileValues) values).getLte3OutValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte3OutValues()[i] + cmwCompensation.getLte3RxValues()[i]);
            ((CmwProfileValues) values).getLte3InValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte3InValues()[i] + cmwCompensation.getLte3TxValues()[i]);
            ((CmwProfileValues) values).getLte7OutValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte7OutValues()[i] + cmwCompensation.getLte7RxValues()[i]);
            ((CmwProfileValues) values).getLte7InValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte7InValues()[i] + cmwCompensation.getLte7TxValues()[i]);
            ((CmwProfileValues) values).getLte20OutValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte20OutValues()[i] + cmwCompensation.getLte20RxValues()[i]);
            ((CmwProfileValues) values).getLte20InValues()[i] = NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte20InValues()[i] + cmwCompensation.getLte20TxValues()[i]);
        }
        super.addCompensation(compensation);
    }

    protected String lte1RxCompText(int i) {
        if (((CmwCompensation) compensation).getLte1RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte1OutValues()[i] - ((CmwCompensation) compensation).getLte1RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte1TxCompText(int i) {
        if (((CmwCompensation) compensation).getLte1TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte1InValues()[i] - ((CmwCompensation) compensation).getLte1TxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte3RxCompText(int i) {
        if (((CmwCompensation) compensation).getLte3RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte3OutValues()[i] - ((CmwCompensation) compensation).getLte3RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte3TxCompText(int i) {
        if (((CmwCompensation) compensation).getLte3TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte3InValues()[i] - ((CmwCompensation) compensation).getLte3TxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte7RxCompText(int i) {
        if (((CmwCompensation) compensation).getLte7RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte7OutValues()[i] - ((CmwCompensation) compensation).getLte7RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte7TxCompText(int i) {
        if (((CmwCompensation) compensation).getLte7TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte7InValues()[i] - ((CmwCompensation) compensation).getLte7TxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte20RxCompText(int i) {
        if (((CmwCompensation) compensation).getLte20RxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte20OutValues()[i] - ((CmwCompensation) compensation).getLte20RxValues()[i]) + ") ";
        }
        return "";
    }

    protected String lte20TxCompText(int i) {
        if (((CmwCompensation) compensation).getLte20TxValues()[i] != 0) {
            return " (" + NumberHelper.oneDecimalPlaceOf(((CmwProfileValues) values).getLte20InValues()[i] - ((CmwCompensation) compensation).getLte20TxValues()[i]) + ") ";
        }
        return "";
    }

    protected List<String> getLteInfo() {
        List<String> text = new ArrayList<>();
        text.add(addOverload("LTE 1 csillapítás:", ((CmwProfileValues) values).getLte1InValues(), ((CmwProfileValues) values).getLte1OutValues()));
        text.add("Low - IN: " + ((CmwProfileValues) values).getLte1InValues()[0] + lte1TxCompText(0) + " OUT: " + ((CmwProfileValues) values).getLte1OutValues()[0] + lte1RxCompText(0));
        text.add("Mid - IN: " + ((CmwProfileValues) values).getLte1InValues()[1] + lte1TxCompText(1) + " OUT: " + ((CmwProfileValues) values).getLte1OutValues()[1] + lte1RxCompText(1));
        text.add("High - IN: " + ((CmwProfileValues) values).getLte1InValues()[2] + lte1TxCompText(2) + " OUT: " + ((CmwProfileValues) values).getLte1OutValues()[2] + lte1RxCompText(2));
        text.add("");
        text.add(addOverload("LTE 3 csillapítás:", ((CmwProfileValues) values).getLte3InValues(), ((CmwProfileValues) values).getLte3OutValues()));
        text.add("Low - IN: " + ((CmwProfileValues) values).getLte3InValues()[0] + lte3TxCompText(0) + " OUT: " + ((CmwProfileValues) values).getLte3OutValues()[0] + lte3RxCompText(0));
        text.add("Mid - IN: " + ((CmwProfileValues) values).getLte3InValues()[1] + lte3TxCompText(1) + " OUT: " + ((CmwProfileValues) values).getLte3OutValues()[1] + lte3RxCompText(1));
        text.add("High - IN: " + ((CmwProfileValues) values).getLte3InValues()[2] + lte3TxCompText(2) + " OUT: " + ((CmwProfileValues) values).getLte3OutValues()[2] + lte3RxCompText(2));
        text.add("");
        text.add(addOverload("LTE 7 csillapítás:", ((CmwProfileValues) values).getLte7InValues(), ((CmwProfileValues) values).getLte7OutValues()));
        text.add("Low - IN: " + ((CmwProfileValues) values).getLte7InValues()[0] + lte7TxCompText(0) + " OUT: " + ((CmwProfileValues) values).getLte7OutValues()[0] + lte7RxCompText(0));
        text.add("Mid - IN: " + ((CmwProfileValues) values).getLte7InValues()[1] + lte7TxCompText(1) + " OUT: " + ((CmwProfileValues) values).getLte7OutValues()[1] + lte7RxCompText(1));
        text.add("High - IN: " + ((CmwProfileValues) values).getLte7InValues()[2] + lte7TxCompText(2) + " OUT: " + ((CmwProfileValues) values).getLte7OutValues()[2] + lte7RxCompText(2));
        text.add("");
        text.add(addOverload("LTE 20 csillapítás:", ((CmwProfileValues) values).getLte20InValues(), ((CmwProfileValues) values).getLte20OutValues()));
        text.add("Low - IN: " + ((CmwProfileValues) values).getLte20InValues()[0] + lte20TxCompText(0) + " OUT: " + ((CmwProfileValues) values).getLte20OutValues()[0] + lte20RxCompText(0));
        text.add("Mid - IN: " + ((CmwProfileValues) values).getLte20InValues()[1] + lte20TxCompText(1) + " OUT: " + ((CmwProfileValues) values).getLte20OutValues()[1] + lte20RxCompText(1));
        text.add("High - IN: " + ((CmwProfileValues) values).getLte20InValues()[2] + lte20TxCompText(2) + " OUT: " + ((CmwProfileValues) values).getLte20OutValues()[2] + lte20RxCompText(2));
        return text;
    }

    @Override
    public List<String> getInfo() {
        List<String> text = new ArrayList<>();
        text.add("CMW 290 RF profil");
        text.add("");
        text.add("Állomás száma: " + serial);
        text.add("");
        text.add("Gyártó és típus:");
        text.add(getName());
        text.add("");
        text.add("Shield Box típusa:");
        text.add(box);
        text.add("");
        text.add("Pozíció: " + position);
        text.add("");
        text.add("Fájlnév TAC szám: " + storeTac);
        text.add("");
        text.add("Társított TAC számok:");
        for (Long tac : tacList) {
            text.add("" + tac);
        }
        text.add("");
        text.addAll(getWcdmaGsmInfo());
        text.add("");
        text.addAll(getLteInfo());
        text.add("");
        text.add("Történet:");
        if (logBatch != null) {
            text.addAll(logBatch.getHistory(false, false));
        } else {
            text.add("nincs adat");
        }
        return text;
    }
}
