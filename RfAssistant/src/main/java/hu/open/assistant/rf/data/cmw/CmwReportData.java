package hu.open.assistant.rf.data.cmw;

import hu.open.assistant.rf.model.report.CmwReport;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.rf.model.report.values.CmwReportValues;
import hu.open.assistant.commons.data.FileHandler;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.commons.util.TextHelper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data class which reads raw CMW type reports from disk. It contains many checking stages to verify the report.
 */
public class CmwReportData {

	private static final String[] COMPATIBLE_SCRIPTS = {
			"CMW_WCDMAI_GSM900-1800_RF",
			"CMW_WCDMAI_RF",
			"CMW_LTE3-1-7-20_RF",
			"CMW_LTE1-3-7-20_RF",
			"CMW_LTE20-1-7-3_RF"};

	private final CmwReportLimits limits;
	private final FileHandler fileHandler;

	public CmwReportData(CmwReportLimits limits, FileHandler fileHandler) {
		this.limits = limits;
		this.fileHandler = fileHandler;
	}

	public CmwReport readReport(String reportFolder, String filename, int passableLimit) {
		CmwReport report = null;
		List<String> rawText = fileHandler.readUtf8TextToList(reportFolder + "\\" + filename, false);
		boolean passed = false;
		boolean process = true;
		LocalDateTime dateTime = null;
		String type;
		String manufacturer;
		String position;
		float scriptVersion;
		int serial = 0;
		long imei = 0;
		try {
			imei = Long.parseLong(filename.substring(0, 15));
			if (filename.charAt(16) == 'p') {
				passed = true;
			} else if (filename.charAt(16) != 'f') {
				System.out.println("Incorrect filename: " + filename);
				process = false;
			}
		} catch (NumberFormatException | IndexOutOfBoundsException exception) {
			System.out.println("Incorrect filename: " + filename);
			process = false;
		}
		if (rawText == null) {
			process = false;
		}
		if (process) {
			try {
				dateTime = cmwReportDateTimeToLocalDateTime(rawText.get(0));
				if (dateTime != null) {
					serial = Integer.parseInt(rawText.get(16).split(":")[2].substring(1));
				} else {
					System.out.println("Incorrect header info: " + filename);
					process = false;
				}
			} catch (NumberFormatException | IndexOutOfBoundsException exception) {
				System.out.println("Incorrect header info: " + filename);
				process = false;
			}
		}
		if (process) {
			String script;
			try {
				scriptVersion = Float.parseFloat(rawText.get(8).split(":")[1].substring(1));
				String name = rawText.get(10).split(":")[1].substring(1);
				manufacturer = TextHelper.getFirstWord(name);
				type = name.substring(manufacturer.length() + 1);
				position = rawText.get(14).split(":")[1].substring(1);
				String[] scriptParts = rawText.get(2).split("\\\\");
				script = scriptParts[scriptParts.length - 1];
				if (isScriptCompatible(script)) {
					CmwReportValues values = readValues(rawText);
					if (values == null || !allValuesPresent(values, script)) {
						System.out.println("Incomplete report: " + filename);
						values = new CmwReportValues();
					}
					report = new CmwReport(filename, reportFolder, dateTime, type, manufacturer, serial, position, scriptVersion, imei, passed, values, limits);
					report.checkValues(passableLimit);
				} else {
					System.out.println("Script not supported: " + filename);
				}
			} catch (NumberFormatException | IndexOutOfBoundsException exception) {
				System.out.println("Incorrect report format: " + filename);
			}
		}
		return report;
	}

	private boolean isScriptCompatible(String script) {
		for (String compatibleScript : COMPATIBLE_SCRIPTS) {
			if (script.contains(compatibleScript)) {
				return true;
			}
		}
		return false;
	}

	private double readValue(String line, String limit, String unit) {
		double value;
		value = Double.parseDouble(line.substring(line.indexOf(limit) + limit.length(), line.indexOf(unit)).stripLeading());
		if (Double.isNaN(value)) {
			throw new NumberFormatException();
		}
		return value;
	}

	private boolean allValuesPresent(CmwReportValues values, String script) {
		switch (script) {
			case "CMW_WCDMAI_GSM900-1800_RF2.rstp":
			case "CMW_WCDMAI_GSM900-1800_RF1.rstp":
				return NumberHelper.arrayHasValues(values.getWcdma1TxValues()) && NumberHelper.arrayHasValues(values.getWcdma1RxValues()) &&
						NumberHelper.arrayHasValues(values.getGsm900TxValues()) && NumberHelper.arrayHasValues(values.getGsm900RxValues()) &&
						NumberHelper.arrayHasValues(values.getGsm1800TxValues()) && NumberHelper.arrayHasValues(values.getGsm1800RxValues());
			case "CMW_WCDMAI_RF2.rstp":
			case "CMW_WCDMAI_RF1.rstp":
				return NumberHelper.arrayHasValues(values.getWcdma1TxValues()) && NumberHelper.arrayHasValues(values.getWcdma1RxValues());
			default:
				return NumberHelper.arrayHasValues(values.getLte1TxValues()) && NumberHelper.arrayHasValues(values.getLte1RxValues()) &&
						NumberHelper.arrayHasValues(values.getLte3TxValues()) && NumberHelper.arrayHasValues(values.getLte3RxValues()) &&
						NumberHelper.arrayHasValues(values.getLte7TxValues()) && NumberHelper.arrayHasValues(values.getLte7RxValues()) &&
						NumberHelper.arrayHasValues(values.getLte20TxValues()) && NumberHelper.arrayHasValues(values.getLte20RxValues());
		}
	}

	private CmwReportValues readValues(List<String> rawText) {
		String line;
		CmwReportValues values = new CmwReportValues();
		int sampleWcdmaTx = 0;
		int sampleWcdmaRx = 0;
		int sampleGsmTx = 0;
		int sampleGsmRx = 0;
		int sampleLteTx = 0;
		int sampleLteRx = 0;
		int sampleLteAllTx = 0;
		String lteBand = "";
		try {
			for (int i = 16; i < rawText.size(); i++) {
				line = rawText.get(i);
				if (line.contains("Max. Power")) {
					if (sampleWcdmaTx < 3) {
						values.setWcdma1TxValue(readValue(line, "" + limits.getWcdma1TxMax(), "dBm"), sampleWcdmaTx);
					} else {
						values.setWcdma8TxValue(readValue(line, "" + limits.getWcdma8TxMax(), "dBm"), sampleWcdmaTx - 3);
					}
					sampleWcdmaTx++;
				} else if (line.contains("CPICH RSCP - Lower")) {
					if (sampleWcdmaRx < 3) {
						values.setWcdma1RxValue(readValue(rawText.get(i + 1), "" + limits.getWcdma1RxMax(), "dBm"), sampleWcdmaRx);
					} else {
						values.setWcdma1RxValue(readValue(rawText.get(i + 1), "" + limits.getWcdma8RxMax(), "dBm"), sampleWcdmaRx - 3);
					}
					sampleWcdmaRx++;
				} else if (line.contains("Average Power")) {
					if (sampleGsmTx < 3) {
						values.setGsm900TxValue(readValue(rawText.get(i + 1), "" + limits.getGsm900TxMax(), "dBm"), sampleGsmTx);
					} else {
						values.setGsm1800TxValue(readValue(rawText.get(i + 1), "" + limits.getGsm1800TxMax(), "dBm"), sampleGsmTx - 3);
					}
					sampleGsmTx++;
				} else if (line.contains("RX LEV")) {
					if (sampleGsmRx < 3) {
						values.setGsm900RxValue(readValue(line, "" + limits.getGsm900RxMax(), "---"), sampleGsmRx);
					} else {
						values.setGsm1800RxValue(readValue(line, "" + limits.getGsm1800RxMax(), "---"), sampleGsmRx - 3);
					}
					sampleGsmRx++;
				} else if (line.contains("Max. Output Power")) {
					switch (lteBand) {
						case "Band1":
							values.setLte1TxValue(readValue(rawText.get(i + 1), "" + limits.getLte1TxMax() + ".00", "dBm"), sampleLteTx);
							break;
						case "Band3":
							values.setLte3TxValue(readValue(rawText.get(i + 1), "" + limits.getLte3TxMax() + ".00", "dBm"), sampleLteTx);
							break;
						case "Band7":
							values.setLte7TxValue(readValue(rawText.get(i + 1), "" + limits.getLte7TxMax() + ".00", "dBm"), sampleLteTx);
							break;
						case "Band20":
							values.setLte20TxValue(readValue(rawText.get(i + 1), "" + limits.getLte20TxMax() + ".00", "dBm"), sampleLteTx);
							break;
					}
					sampleLteTx++;
					sampleLteAllTx++;
				} else if (line.contains("RSRP")) {
					switch (lteBand) {
						case "Band1":
							values.setLte1RxValue(readValue(line, "(", "..."), sampleLteRx);
							break;
						case "Band3":
							values.setLte3RxValue(readValue(line, "(", "..."), sampleLteRx);
							break;
						case "Band7":
							values.setLte7RxValue(readValue(line, "(", "..."), sampleLteRx);
							break;
						case "Band20":
							values.setLte20RxValue(readValue(line, "(", "..."), sampleLteRx);
							break;
					}
					sampleLteRx++;
				} else if (line.contains("UE Measurement Report")) {
					String bandIndicator = line.split(" ")[7];
					if (!lteBand.isEmpty() && !bandIndicator.equals(lteBand)) {
						sampleLteTx = 0;
						sampleLteRx = 0;
					}
					lteBand = bandIndicator;
				}
				if (sampleGsmRx == 6 || sampleLteAllTx == 12) {
					return values;
				}
			}
		} catch (NumberFormatException | IndexOutOfBoundsException exception) {
			values = null;
		}
		return values;
	}

	private LocalDateTime cmwReportDateTimeToLocalDateTime(String text) {
		LocalDateTime dateTime = null;
		try {
			String[] parts = text.split(" ");
			String[] dateParts = parts[1].split("/");
			String[] timeParts = parts[parts.length - 1].split(":");
			int month = Integer.parseInt(dateParts[0]);
			int day = Integer.parseInt(dateParts[1]);
			int year = Integer.parseInt(dateParts[2]);
			int hour = Integer.parseInt(timeParts[0]);
			int minute = Integer.parseInt(timeParts[1]);
			int second = Integer.parseInt(timeParts[2]);
			dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
		} catch (NumberFormatException | IndexOutOfBoundsException exception) {
			System.out.println("Invalid date format: " + text);
		}
		return dateTime;
	}
}
