package hu.open.assistant.rf.data;

import hu.open.assistant.rf.filter.DatabaseLogBatchFilter;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.log.DatabaseLog;
import hu.open.assistant.rf.model.log.batch.DatabaseLogBatch;
import hu.open.assistant.rf.model.log.event.DatabaseLogEvent;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.data.CsvParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data class which reads and writes database log from or to the disk. CMU and CMW database log is stored separately
 * in a compact CSV format. All log types can be written to disk without replacing the data file (appending). There
 * is an option to clear the excess log (when a database backup is removed). The logical logs are organised into
 * database log batches and placed in a database log batch container.
 */
public class DatabaseLogData {

    private static final String DATAFILE = "database_log.csv";

    private static final String[] HEADER = {"dateTime", "event", "serial", "testerType"};

    private final String dataFolder;
    private final CsvParser csvParser;

    public DatabaseLogData(String logPath, CsvParser csvParser) {
        this.dataFolder = logPath;
        this.csvParser = csvParser;
    }

    public List<DatabaseLogBatch> readDatabaseLogs() {
        List<DatabaseLogBatch> logBatches = new ArrayList<>();
        List<String[]> records = csvParser.readCsvFile(dataFolder + "\\" + DATAFILE);
        if (!records.isEmpty()) {
            if (Arrays.equals(records.get(0), HEADER)) {
                records.remove(0);
                List<DatabaseLog> logs = new ArrayList<>();
                LocalDateTime dateTime;
                DatabaseLogEvent event;
                int serial;
                TesterType testerType;
                for (String[] record : records) {
                    dateTime = DateHelper.isoTextDateTimeToLocalDateTime(record[0]);
                    event = DatabaseLogEvent.getByName(record[1]);
                    serial = Integer.parseInt(record[2]);
                    testerType = TesterType.getByName(record[3]);
                    logs.add(new DatabaseLog(dateTime, event, serial, testerType));
                }
                for (DatabaseLog log : logs) {
                    sortLogIntoLogBatches(log, logBatches);
                }
                for (DatabaseLogBatch batch : logBatches) {
                    batch.sortLogs();
                }
                Collections.sort(logBatches);
            }
        }
        return logBatches;
    }

    public List<DatabaseLog> readDatabaseBackupLogs() {
        List<DatabaseLog> backupLogs = new ArrayList<>();
        List<String[]> records = csvParser.readCsvFile(dataFolder + "\\" + DATAFILE);
        if (!records.isEmpty()) {
            if (Arrays.equals(records.get(0), HEADER)) {
                records.remove(0);
                LocalDateTime dateTime;
                DatabaseLogEvent event;
                int serial;
                TesterType testerType;
                for (String[] record : records) {
                    dateTime = DateHelper.isoTextDateTimeToLocalDateTime(record[0]);
                    event = DatabaseLogEvent.getByName(record[1]);
                    serial = Integer.parseInt(record[2]);
                    testerType = TesterType.getByName(record[3]);
                    if (event == DatabaseLogEvent.BACKUP) {
                        backupLogs.add(new DatabaseLog(dateTime, event, serial, testerType));
                    }
                }
                Collections.sort(backupLogs);
            }
        }
        return backupLogs;
    }

    public void removeExcessDatabaseLog(int serial, LocalDateTime dateTime) {
        List<DatabaseLog> databaseLogs = readDatabaseLogs().stream()
                .flatMap(databaseLogBatch -> databaseLogBatch.getLogs().stream()).collect(Collectors.toList());
        DatabaseLog excessLog = databaseLogs.stream().filter(databaseLog ->
                databaseLog.getEvent().equals(DatabaseLogEvent.BACKUP) &&
                        databaseLog.getSerial() == serial &&
                        databaseLog.getDateTime().equals(dateTime)).findAny().orElse(null);
        databaseLogs.remove(excessLog);
        Collections.sort(databaseLogs);
        writeDatabaseLogs(databaseLogs);
    }

    private void writeDatabaseLogs(List<DatabaseLog> databaseLogs) {
        List<String[]> records = new ArrayList<>();
        databaseLogs.forEach(log -> records.add(logToRecord(log)));
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, false);
    }

    public void writeDatabaseCheckLog(int serial, TesterType testerType) {
        List<String[]> records = new ArrayList<>();
        DatabaseLog log = new DatabaseLog(LocalDateTime.now(), DatabaseLogEvent.CHECK, serial, testerType);
        records.add(logToRecord(log));
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, true);
    }

    public void writeDatabaseBackupLog(int serial, TesterType testerType, LocalDateTime localDateTime) {
        List<String[]> records = new ArrayList<>();
        DatabaseLog log = new DatabaseLog(localDateTime, DatabaseLogEvent.BACKUP, serial, testerType);
        records.add(logToRecord(log));
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, true);
    }

    public void writeDatabaseRestoreLog(int serial, TesterType testerType) {
        List<String[]> records = new ArrayList<>();
        DatabaseLog log = new DatabaseLog(LocalDateTime.now(), DatabaseLogEvent.RESTORE, serial, testerType);
        records.add(logToRecord(log));
        csvParser.writeCsvFile(dataFolder + "\\" + DATAFILE, records, HEADER, true);
    }

    private String[] logToRecord(DatabaseLog log) {
        String[] record = new String[HEADER.length];
        record[0] = DateHelper.localDateTimeToIsoTextDateTime(log.getDateTime());
        record[1] = log.getEvent().getName();
        record[2] = String.valueOf(log.getSerial());
        record[3] = log.getTesterType().getName();
        return record;
    }

    private void sortLogIntoLogBatches(DatabaseLog log, List<DatabaseLogBatch> databaseLogs) {
        DatabaseLogBatch logBatch = DatabaseLogBatchFilter.getDatabaseLogBatchBySerial(databaseLogs, log.getSerial());
        if (logBatch != null) {
            logBatch.addLog(log);
        } else {
            logBatch = new DatabaseLogBatch(log.getSerial());
            logBatch.addLog(log);
            databaseLogs.add(logBatch);
        }
    }
}
