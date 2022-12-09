package hu.open.assistant.rf.model.log.batch;

import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.log.event.ProfileLogEvent;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.log.ProfileLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and holds together profile logs from the same RF database. Can create a profile history in text format.
 */
public class ProfileLogBatch implements Comparable<ProfileLogBatch>, LogBatch {
    private final int serial;
    private final String name;
    private final List<ProfileLog> logs;
    private boolean hasCompensationLog;

    public ProfileLogBatch(int serial, String name) {
        this.serial = serial;
        this.name = name;
        logs = new ArrayList<>();
    }

    public int getSerial() {
        return serial;
    }

    public String getName() {
        return name;
    }

    public void addLog(ProfileLog log) {
        if (!hasCompensationLog) {
            if (log.getEvent() == ProfileLogEvent.COMPENSATION) {
                hasCompensationLog = true;
            }
        }
        logs.add(log);
    }

    public LocalDateTime getLastModificationDate() {
        return getLatestLog().getDateTime();
    }

    public LocalDateTime getSecondModificationDate() {
        if (logs.size() > 1) {
            return logs.get(1).getDateTime();
        } else {
            return null;
        }
    }

    public ProfileLog getLatestLog() {
        return logs.get(0);
    }

    public List<ProfileLog> getLogs() {
        return logs;
    }

    public List<String> getHistory(boolean wideFormat, boolean showValues) {
        List<String> history = new ArrayList<>();
        for (ProfileLog log : logs) {
            if (log.getEvent() == ProfileLogEvent.COMPENSATION) {
                if (wideFormat) {
                    history.add("    · kompenzálás: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    if (showValues) {
                        history.addAll(getValuesText(log));
                    }
                } else {
                    history.add("  • kompenzálás: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                }
            } else if (log.getEvent() == ProfileLogEvent.REVERT) {
                if (wideFormat) {
                    if (!log.getCompensation().isEmpty()) {
                        history.add("    · visszaállítás: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                        if (showValues) {
                            history.addAll(getValuesText(log));
                        }
                    } else {
                        history.add("    · visszaállítás (teljes): " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                    }
                } else {
                    history.add("  • visszaállítás: " + DateHelper.localDateTimeToTextDateTime(log.getDateTime()));
                }
            }
        }
        ProfileLog firsLog = logs.get(logs.size() - 1);
        // TODO modify when LIVE log is converted
        if (firsLog.getEvent() == ProfileLogEvent.CREATION) {
            if (wideFormat) {
                history.add("    · létrehozva: " + DateHelper.localDateTimeToTextDateTime(firsLog.getDateTime()));
            } else {
                history.add("  • létrehozva: " + DateHelper.localDateTimeToTextDateTime(firsLog.getDateTime()));
            }
            if (!firsLog.getComment().isBlank()) {
                if (wideFormat) {
                    history.add("        · " + firsLog.getComment());
                } else {
                    history.add("    · " + firsLog.getComment());
                }
            }
        } else {
            if (wideFormat) {
                history.add("    · létrehozva: nincs adat");
            } else {
                history.add("  • létrehozva: nincs adat");
            }
        }
        return history;
    }

    public void sortLogs() {
        Collections.sort(logs);
    }

    private List<String> getValuesText(ProfileLog log) {
        List<String> valuesText = new ArrayList<>();
        Compensation compensation = log.getCompensation();
        addValuesText(valuesText, compensation.getWcdma1TxValues(), "WCDMA 1 TX");
        addValuesText(valuesText, compensation.getWcdma1RxValues(), "WCDMA 1 RX");
        addValuesText(valuesText, compensation.getWcdma8TxValues(), "WCDMA 8 TX");
        addValuesText(valuesText, compensation.getWcdma8RxValues(), "WCDMA 8 RX");
        addValuesText(valuesText, compensation.getGsm900TxValues(), "GSM900 TX");
        addValuesText(valuesText, compensation.getGsm900RxValues(), "GSM900 RX");
        addValuesText(valuesText, compensation.getGsm1800TxValues(), "GSM1800 TX");
        addValuesText(valuesText, compensation.getGsm1800RxValues(), "GSM1800 RX");
        if (log.getTesterType() == TesterType.CMW) {
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte1TxValues(), "LTE1 TX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte1RxValues(), "LTE1 RX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte3TxValues(), "LTE3 TX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte3RxValues(), "LTE3 RX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte7TxValues(), "LTE7 TX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte7RxValues(), "LTE7 RX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte20TxValues(), "LTE20 TX");
            addValuesText(valuesText, ((CmwCompensation) compensation).getLte20RxValues(), "LTE20 RX");
        }
        return valuesText;
    }

    private void addValuesText(List<String> valuesText, double[] values, String text) {
        if (NumberHelper.arrayHasValues(values)) {
            valuesText.add("        · " + text + ": " + values[0] + ", " + values[1] + ", " + values[2]);
        }
    }

    @Override
    public int compareTo(ProfileLogBatch other) {
        return -1 * (getLastModificationDate().compareTo(other.getLastModificationDate()));
    }

    @Override
    public List<String> getInfo() {
        List<String> history = new ArrayList<>();
        history.add("  • RF profil: " + name + " (" + serial + ")");
        history.addAll(getHistory(true, false));
        history.add("\n");
        return history;
    }

    @Override
    public List<String> getExtendedInfo() {
        List<String> history = new ArrayList<>();
        history.add("  • RF profil: " + name + " (" + serial + ")");
        history.addAll(getHistory(true, true));
        history.add("\n");
        return history;
    }
}
