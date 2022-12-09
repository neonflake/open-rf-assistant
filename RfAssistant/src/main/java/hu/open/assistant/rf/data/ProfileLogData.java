package hu.open.assistant.rf.data;

import hu.open.assistant.rf.filter.ProfileLogBatchFilter;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.compensation.CmuCompensation;
import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.log.ProfileLog;
import hu.open.assistant.rf.model.log.batch.ProfileLogBatch;
import hu.open.assistant.rf.model.log.event.ProfileLogEvent;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.data.CsvParser;
import hu.open.assistant.commons.util.NumberHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Data class which reads and writes profile log from or to the disk. CMU and CMW profile log is stored separately
 * in a compact CSV format. Specific log types can be written to disk without replacing the data file (appending). There
 * is an option to clear the excess logs (logs from non-existing profiles). When reading a compensation or revert type
 * log a compensation is also created during the process and stored in the logical log. The logical logs are organised
 * into report batches and placed in a report batch container.
 */
public class ProfileLogData {

    private static final String DATAFILE = "profile_log.csv";

    private static final String[] HEADER = {"dateTime", "event", "serial", "testerType", "name", "comment", "wcdma1Values", "wcdma8Values", "gsm900Values", "gsm1800Values", "lte1Values", "lte3Values", "lte7Values", "lte20Values"};

    private final String dataFolder;
    private final CsvParser csvParser;

    public ProfileLogData(String dataFolder, CsvParser csvParser) {
        this.dataFolder = dataFolder;
        this.csvParser = csvParser;
    }

    public void clearExcessLogs(List<ProfileLogBatch> excessLogs) {
        List<ProfileLogBatch> profileLogBatches = readProfileLogs();
        for (ProfileLogBatch excessLog : excessLogs) {
            if (excessLog != null) {
                ProfileLogBatch logBatch = ProfileLogBatchFilter.getProfileLogBatchBySerialAndByName(profileLogBatches, excessLog.getSerial(), excessLog.getName());
                if (logBatch != null) {
                    profileLogBatches.remove(logBatch);
                }
            }
        }
        writeLogs(profileLogBatches);
    }

    public void writeLogs(List<ProfileLogBatch> profileLogBatches) {
        List<String[]> records = new ArrayList<>();
        List<ProfileLog> logs = new ArrayList<>();
        for (ProfileLogBatch logBatch : profileLogBatches) {
            logs.addAll(logBatch.getLogs());
        }
        Collections.sort(logs);
        for (ProfileLog log : logs) {
            records.add(0, logToRecord(log));
        }
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, false);
    }

    private String[] logToRecord(ProfileLog log) {
        String[] record = new String[HEADER.length];
        record[0] = DateHelper.localDateTimeToIsoTextDateTime(log.getDateTime());
        record[1] = log.getEvent().getName();
        record[2] = String.valueOf(log.getSerial());
        record[3] = log.getTesterType().getName();
        record[4] = log.getName();
        if (log.getEvent() == ProfileLogEvent.CREATION) {
            record[5] = log.getComment();
        } else {
            Compensation compensation = log.getCompensation();
            record[6] = valuesToString(compensation.getWcdma1RxValues(), compensation.getWcdma1TxValues());
            record[7] = valuesToString(compensation.getWcdma8RxValues(), compensation.getWcdma8TxValues());
            record[8] = valuesToString(compensation.getGsm900RxValues(), compensation.getGsm900TxValues());
            record[9] = valuesToString(compensation.getGsm1800RxValues(), compensation.getGsm1800TxValues());
            if (log.getTesterType() == TesterType.CMW) {
                record[10] = valuesToString(((CmwCompensation) compensation).getLte1RxValues(), ((CmwCompensation) compensation).getLte1TxValues());
                record[11] = valuesToString(((CmwCompensation) compensation).getLte3RxValues(), ((CmwCompensation) compensation).getLte3TxValues());
                record[12] = valuesToString(((CmwCompensation) compensation).getLte7RxValues(), ((CmwCompensation) compensation).getLte7TxValues());
                record[13] = valuesToString(((CmwCompensation) compensation).getLte20RxValues(), ((CmwCompensation) compensation).getLte20TxValues());
            }
        }
        return record;
    }

    private String valuesToString(double[] rxValues, double[] txValues) {
        if (NumberHelper.arrayHasValues(rxValues) || NumberHelper.arrayHasValues(txValues)) {
            return rxValues[0] + "," + txValues[0] + "," + rxValues[1] + "," + txValues[1] + "," + rxValues[2] + "," + txValues[2];
        } else {
            return "";
        }
    }

    public List<ProfileLogBatch> readProfileLogs() {
        List<ProfileLogBatch> profileLogBatches = new ArrayList<>();
        List<String[]> records = csvParser.readCsvFile(dataFolder + "\\" + DATAFILE);
        if (!records.isEmpty()) {
            if (Arrays.equals(records.get(0), HEADER)) {
                records.remove(0);
                List<ProfileLog> logs = new ArrayList<>();
                LocalDateTime dateTime;
                ProfileLogEvent event;
                TesterType testerType;
                int serial;
                String name;
                String comment;
                double[] compValues;
                for (String[] record : records) {
                    dateTime = DateHelper.isoTextDateTimeToLocalDateTime(record[0]);
                    event = ProfileLogEvent.getByName(record[1]);
                    serial = Integer.parseInt(record[2]);
                    testerType = TesterType.getByName(record[3]);
                    name = record[4];
                    comment = record[5];
                    if (event == ProfileLogEvent.CREATION) {
                        logs.add(new ProfileLog(dateTime, event, serial, testerType, name, comment));
                    } else if (event == ProfileLogEvent.COMPENSATION || event == ProfileLogEvent.REVERT) {
                        Compensation compensation;
                        if (testerType == TesterType.CMU) {
                            compValues = readValues(new double[24], record);
                            compensation = new CmuCompensation(serial, name);
                            fillCompensation(compensation, readValues(compValues, record));
                        } else {
                            compValues = readValues(new double[48], record);
                            compensation = new CmwCompensation(serial, name);
                            fillCompensation(compensation, compValues);
                            fillLteCompensation((CmwCompensation) compensation, compValues);
                        }
                        logs.add(new ProfileLog(dateTime, event, serial, testerType, name, compensation));
                    }
                }
                for (ProfileLog log : logs) {
                    sortLogIntoLogBatches(log, profileLogBatches);
                }
                for (ProfileLogBatch logBatch : profileLogBatches) {
                    logBatch.sortLogs();
                }
                Collections.sort(profileLogBatches);
            }
        }
        return profileLogBatches;
    }

    private double[] readValues(double[] compValues, String[] record) {
        for (int i = 0; i < compValues.length / 6; i++) {
            String[] stringValues = record[6 + i].split(",");
            if (stringValues.length == 6) {
                for (int j = 0; j < 6; j++) {
                    compValues[j + i * 6] = Double.parseDouble(stringValues[j]);
                }
            }
        }
        return compValues;
    }

    public void writeProfileSyncLog(List<Profile> missingProfiles, int sourceSerial, int targetSerial) {
        List<String[]> records = new ArrayList<>();
        for (Profile profile : missingProfiles) {
            ProfileLog log = new ProfileLog(LocalDateTime.now(), ProfileLogEvent.CREATION, targetSerial, profile.getTesterType(),
                    profile.getName(), "szinkronizálva, forrás: " + sourceSerial);
            records.add(logToRecord(log));
        }
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, true);
    }

    public void writeProfileCompensationLog(Profile profile) {
        List<String[]> records = new ArrayList<>();
        ProfileLog log = new ProfileLog(LocalDateTime.now(), profile.isReverted() ? ProfileLogEvent.REVERT : ProfileLogEvent.COMPENSATION,
                profile.getSerial(), profile.getTesterType(), profile.getName(), profile.getCompensation());
        records.add(logToRecord(log));
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, true);
    }

    public void writeProfileLog(ProfileLog log) {
        List<String[]> records = new ArrayList<>();
        records.add(logToRecord(log));
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, true);
    }

    private void fillLteCompensation(CmwCompensation compensation, double[] values) {
        boolean setIn = true;
        int pointer = 0;
        for (int i = 24; i < 48; i++) {
            if (i < 30) {
                if (setIn) {
                    compensation.addCompensation("lte1_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("lte1_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            } else if (i < 36) {
                if (setIn) {
                    compensation.addCompensation("lte3_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("lte3_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            } else if (i < 42) {
                if (setIn) {
                    compensation.addCompensation("lte7_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("lte7_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            } else {
                if (setIn) {
                    compensation.addCompensation("lte20_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("lte20_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            }
        }
    }

    private void fillCompensation(Compensation compensation, double[] values) {
        boolean setIn = true;
        int pointer = 0;
        for (int i = 0; i < 24; i++) {
            if (i < 6) {
                if (setIn) {
                    compensation.addCompensation("wcdma1_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("wcdma1_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            } else if (i < 12) {
                if (setIn) {
                    compensation.addCompensation("wcdma8_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("wcdma8_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            } else if (i < 18) {
                if (setIn) {
                    compensation.addCompensation("gsm900_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("gsm900_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            } else {
                if (setIn) {
                    compensation.addCompensation("gsm1800_rx", pointer, values[i]);
                    setIn = false;
                } else {
                    compensation.addCompensation("gsm1800_tx", pointer, values[i]);
                    setIn = true;
                    if (pointer < 2) {
                        pointer++;
                    } else {
                        pointer = 0;
                    }
                }
            }
        }
    }

    private void sortLogIntoLogBatches(ProfileLog log, List<ProfileLogBatch> profileLogBatches) {
        ProfileLogBatch logBatch = ProfileLogBatchFilter.getProfileLogBatchBySerialAndByName(profileLogBatches, log.getSerial(), log.getName());
        if (logBatch != null) {
            logBatch.addLog(log);
        } else {
            logBatch = new ProfileLogBatch(log.getSerial(), log.getName());
            logBatch.addLog(log);
            profileLogBatches.add(logBatch);
        }
    }
}
