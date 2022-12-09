package hu.open.assistant.rf.data;

import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.CmuReport;
import hu.open.assistant.rf.model.report.CmwReport;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.cache.ReportCache;
import hu.open.assistant.rf.model.report.limits.CmuReportLimits;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.rf.model.report.values.CmuReportValues;
import hu.open.assistant.rf.model.report.values.CmwReportValues;
import hu.open.assistant.rf.model.report.values.ReportValues;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.commons.data.CsvParser;
import hu.open.assistant.commons.data.FileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data class which reads pre-processed test reports and writes logical reports from or to the disk. A daily amount of
 * test reports (CMU or CMW type) are stored separately in a compact CSV format.
 */
public class ReportCacheData {

    private static final String DATAFILE_EXTENSION = ".csv";

    private static final String[] HEADER = {"filename", "dateTime", "type", "manufacturer", "serial", "testerType", "position", "version", "imei", "passed", "values"};

    private final CmuReportLimits limits;
    private final CmwReportLimits cmwLimits;
    private final CsvParser csvParser;
    private final ReportCache reportCache = new ReportCache();
    private final FileHandler fileHandler;

    public ReportCacheData(CmuReportLimits limits, CmwReportLimits cmwLimits, FileHandler fileHandler, CsvParser csvParser) {
        this.limits = limits;
        this.cmwLimits = cmwLimits;
        this.fileHandler = fileHandler;
        this.csvParser = csvParser;
    }

    public List<Report> readReportCache(String originalPath, String cacheFolder, String cacheFilename, int passableLimit) {
        List<Report> reports = reportCache.getReports(cacheFilename);
        if (reports == null) {
            if (fileHandler.fileExists(cacheFolder + "\\" + cacheFilename + DATAFILE_EXTENSION)) {
                reports = new ArrayList<>();
                List<String[]> records = csvParser.readCsvFile(cacheFolder + "\\" + cacheFilename + DATAFILE_EXTENSION);
                if (Arrays.equals(records.get(0), HEADER)) {
                    records.remove(0);
                    long imei;
                    float scriptVersion;
                    String type;
                    String manufacturer;
                    int serial;
                    boolean passed;
                    String filename;
                    String position;
                    LocalDateTime dateTime;
                    TesterType testerType;
                    for (String[] parts : records) {
                        filename = parts[0];
                        dateTime = DateHelper.isoTextDateTimeToLocalDateTime(parts[1]);
                        type = parts[2];
                        manufacturer = parts[3];
                        serial = Integer.parseInt(parts[4]);
                        testerType = TesterType.getByName(parts[5]);
                        if (!parts[6].isBlank()) {
                            position = parts[6];
                        } else {
                            position = "";
                        }
                        scriptVersion = Float.parseFloat(parts[7]);
                        imei = Long.parseLong(parts[8]);
                        passed = Boolean.parseBoolean(parts[9]);
                        String[] valueParts = parts[10].split(",");
                        Report report;
                        if (testerType == TesterType.CMU) {
                            report = new CmuReport(filename, originalPath + "\\" + cacheFilename, dateTime, type, manufacturer, serial, position,
                                    scriptVersion, imei, passed, (CmuReportValues) fillValues(valueParts, TesterType.CMU), limits);
                        } else {
                            report = new CmwReport(filename, originalPath + "\\" + cacheFilename, dateTime, type, manufacturer, serial, position,
                                    scriptVersion, imei, passed, (CmwReportValues) fillValues(valueParts, TesterType.CMW), cmwLimits);
                        }
                        report.checkValues(passableLimit);
                        reports.add(report);
                    }
                }
            }
            reportCache.addCache(cacheFilename, reports);
        } else {
            for (Report report : reports) {
                report.checkValues(passableLimit);
            }
        }
        return reports;
    }

    private ReportValues fillValues(String[] valueParts, TesterType testerType) {
        ReportValues values;
        int expectedValues;
        if (testerType == TesterType.CMU) {
            values = new CmuReportValues();
            expectedValues = 24;
        } else {
            values = new CmwReportValues();
            expectedValues = 48;
        }
        for (int i = 0; i < expectedValues; i++) {
            double value = Double.parseDouble(valueParts[i]);
            if (i < 3) {
                values.setWcdma1TxValue(value, i);
            } else if (i < 6) {
                values.setWcdma1RxValue(value, i - 3);
            } else if (i < 9) {
                values.setWcdma8TxValue(value, i - 6);
            } else if (i < 12) {
                values.setWcdma8RxValue(value, i - 9);
            } else if (i < 15) {
                values.setGsm900TxValue(value, i - 12);
            } else if (i < 18) {
                values.setGsm900RxValue(value, i - 15);
            } else if (i < 21) {
                values.setGsm1800TxValue(value, i - 18);
            } else if (i < 24) {
                values.setGsm1800RxValue(value, i - 21);
            } else if (i < 27) {
                ((CmwReportValues) values).setLte1TxValue(value, i - 24);
            } else if (i < 30) {
                ((CmwReportValues) values).setLte1RxValue(value, i - 27);
            } else if (i < 33) {
                ((CmwReportValues) values).setLte3TxValue(value, i - 30);
            } else if (i < 36) {
                ((CmwReportValues) values).setLte3RxValue(value, i - 33);
            } else if (i < 39) {
                ((CmwReportValues) values).setLte7TxValue(value, i - 36);
            } else if (i < 42) {
                ((CmwReportValues) values).setLte7RxValue(value, i - 39);
            } else if (i < 45) {
                ((CmwReportValues) values).setLte20TxValue(value, i - 42);
            } else {
                ((CmwReportValues) values).setLte20RxValue(value, i - 45);
            }
        }
        return values;
    }

    public void writeReportCache(String cacheFolder, String filename, List<Report> reports) {
        List<String[]> records = new ArrayList<>();
        for (Report report : reports) {
            String[] record = new String[HEADER.length];
            record[0] = report.getFilename();
            record[1] = DateHelper.localDateTimeToIsoTextDateTime(report.getDateTime());
            record[2] = report.getType();
            record[3] = report.getManufacturer();
            record[4] = "" + report.getSerial();
            record[5] = report.getTesterType().getName();
            record[6] = fillBlank(report.getPosition());
            record[7] = "" + NumberHelper.twoDecimalPlaceOf(report.getScriptVersion());
            record[8] = "" + report.getImei();
            record[9] = "" + report.isPassed();
            record[10] = chainValues(report);
            records.add(record);
        }
        if (!fileHandler.directoryExists(cacheFolder)) {
            fileHandler.createDirectory(cacheFolder);
        }
        csvParser.writeCsvFile(cacheFolder + "\\" + filename, records, HEADER, false);
    }

    private String fillBlank(String string) {
        if (string.isEmpty()) {
            return (" ");
        } else {
            return string;
        }
    }

    protected String chainValues(Report report) {
        String values = "";
        values = writeValues(values, report.getWcdma1TxValues(), false);
        values = writeValues(values, report.getWcdma1RxValues(), false);
        values = writeValues(values, report.getWcdma8TxValues(), false);
        values = writeValues(values, report.getWcdma8RxValues(), false);
        values = writeValues(values, report.getGsm900TxValues(), false);
        values = writeValues(values, report.getGsm900RxValues(), false);
        values = writeValues(values, report.getGsm1800TxValues(), false);
        values = writeValues(values, report.getGSM1800RxValues(), true);
        if (report.getTesterType() == TesterType.CMW) {
            return values + chainLteValues((CmwReport) report);
        }
        return values;
    }

    protected String chainLteValues(CmwReport report) {
        String values = ",";
        values = writeValues(values, report.getLte1TxValues(), false);
        values = writeValues(values, report.getLte1RxValues(), false);
        values = writeValues(values, report.getLte3TxValues(), false);
        values = writeValues(values, report.getLte3RxValues(), false);
        values = writeValues(values, report.getLte7TxValues(), false);
        values = writeValues(values, report.getLte7RxValues(), false);
        values = writeValues(values, report.getLte20TxValues(), false);
        values = writeValues(values, report.getLte20RxValues(), true);
        return values;
    }

    private String writeValues(String line, double[] values, boolean end) {
        for (int i = 0; i < 3; i++) {
            line = line.concat(String.valueOf(NumberHelper.twoDecimalPlaceOf(values[i])));
            if (!(i == 2 && end)) {
                line = line.concat(",");
            }
        }
        return line;
    }
}
