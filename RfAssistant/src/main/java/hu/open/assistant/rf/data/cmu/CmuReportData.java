package hu.open.assistant.rf.data.cmu;

import hu.open.assistant.rf.model.report.CmuReport;
import hu.open.assistant.rf.model.report.limits.CmuReportLimits;
import hu.open.assistant.rf.model.report.values.CmuReportValues;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.data.FileHandler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data class which reads raw CMU type reports from disk. It contains many checking stages to verify the report.
 */
public class CmuReportData {

	private static final String[] COMPATIBLE_SCRIPTS = {
			"MU_WCDMAI-VIII_GSM900-1800_RF",
			"MU_WCDMAI-VIII_RF",
			"MU_WCDMAI_GSM900-1800_RF",
			"MU_GSM900-1800_RF",
			"MU_WCDMAI_RF"};

	private final CmuReportLimits limits;
	private final FileHandler fileHandler;

	public CmuReportData(CmuReportLimits limits, FileHandler fileHandler) {
		this.limits = limits;
		this.fileHandler = fileHandler;
	}

	public CmuReport readReport(String reportFolder, String filename, int passableLimit) {
		CmuReport report = null;
		boolean process = true;
		List<String> fileContent = fileHandler.readIsoTextToList(reportFolder + "\\" + filename, false);
		long imei = 0;
		float scriptVersion;
		String script;
		String name;
		String manufacturer;
		String type = "";
		String position;
		int serial = 0;
		String version;
		int deviation = 0;
		boolean passed = false;
		LocalDateTime dateTime = null;
		try {
			if (filename.length() == 22) {
				imei = Long.parseLong(filename.split("_")[1].split("\\.")[0]);
			} else {
				imei = Long.parseLong(filename.split("_")[1].split("\\.")[0].split("-")[0]);
			}
			if (filename.startsWith("P")) {
				passed = true;
			} else if (!filename.startsWith("F")) {
				System.out.println("Incorrect filename: " + filename);
				process = false;
			}
		} catch (NumberFormatException exception) {
			System.out.println("Incorrect filename: " + filename);
			process = false;
		}
		if (fileContent == null) {
			process = false;
		}
		if (process) {
			dateTime = cmuReportDateToDate(fileContent.get(29).split("\t")[1].split("<")[0]);
			if (dateTime != null) {
				try {
					serial = Integer.parseInt(fileContent.get(31).substring(32, 38));
				} catch (IndexOutOfBoundsException | NumberFormatException exception) {
					System.out.println("Incorrect header info: " + filename);
					process = false;
				}
			} else {
				System.out.println("Incorrect header info: " + filename);
				process = false;
			}
		}
		if (process) {
			try {
				version = fileContent.get(32).substring(43, 45);
				if (version.equals("02")) {
					deviation = -1;
				}
				scriptVersion = Float.parseFloat((fileContent.get(62 + deviation).split("-")[1].substring(2, 6)));
				script = fileContent.get(37 + deviation).substring(10, fileContent.get(37 + deviation).indexOf('<'));
				name = fileContent.get(38 + deviation).substring(10, fileContent.get(38 + deviation).indexOf('<'));
				String[] parts = name.split(" ");
				manufacturer = parts[0];
				if (!manufacturer.isEmpty() && !name.equals("- - -")) {
					if (isScriptCompatible(script)) {
						int typeEnd;
						if (!manufacturer.equals("Generic")) {
							position = parts[parts.length - 1];
							typeEnd = parts.length - 2;
						} else {
							position = "";
							typeEnd = parts.length;
						}
						for (int pointer = 1; pointer < typeEnd; pointer++) {
							if (pointer != 1) {
								type = type.concat(" ");
							}
							type = type.concat(parts[pointer]);
						}
						CmuReportValues values = readValues(fileContent, filename);
						if (values == null) {
							values = new CmuReportValues();
						}
						report = new CmuReport(filename, reportFolder, dateTime, type, manufacturer, serial, position, scriptVersion, imei, passed, values, limits);
						report.checkValues(passableLimit);
					}
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

	private double readValue(String lineContent) {
		double number = Double.parseDouble(lineContent.substring(lineContent.indexOf('>') + 1, lineContent.lastIndexOf('<')));
		if (Double.isNaN(number)) {
			throw new NumberFormatException();
		}
		return number;
	}

	private LocalDateTime cmuReportDateToDate(String text) {
		LocalDateTime dateTime = null;
		try {
			String[] parts = text.split(" ");
			String[] timeParts = parts[5].split(":");
			int day;
			if (parts[2].charAt(1) == '.') {
				day = Integer.parseInt(parts[2].substring(0, 1));
			} else {
				day = Integer.parseInt(parts[2].substring(0, 2));
			}
			dateTime = LocalDateTime.of(Integer.parseInt(parts[0].substring(0, 4)), DateHelper.monthTextToNumber(parts[1]), day, Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), Integer.parseInt(timeParts[2]));
		} catch (NumberFormatException | IndexOutOfBoundsException exception) {
			System.out.println("Invalid date format: " + text);
		}
		return dateTime;
	}

	private CmuReportValues readValues(List<String> fileContent, String filename) {
		int sampleWcdmaTx = 0;
		int sampleWcdmaRx = 0;
		int sampleGsmTx = 0;
		int sampleGsmRx = 0;
		CmuReportValues values = new CmuReportValues();
		try {
			for (int i = 25; i < fileContent.size(); i++) {
				String line = fileContent.get(i);
				switch (line) {
					case "  <td>Maximum RMS Power:</td>":
						if (sampleWcdmaTx < 3) {
							values.setWcdma1TxValue(readValue(fileContent.get(i + 3)), sampleWcdmaTx);
						} else {
							values.setWcdma8TxValue(readValue(fileContent.get(i + 3)), sampleWcdmaTx - 3);
						}
						sampleWcdmaTx++;
						break;
					case "  <td>UE Report CPICH RSCP Min:</td>":
						if (sampleWcdmaRx < 3) {
							values.setWcdma1RxValue(readValue(fileContent.get(i + 3)), sampleWcdmaRx);
						} else {
							values.setWcdma8TxValue(readValue(fileContent.get(i + 3)), sampleWcdmaRx - 3);
						}
						sampleWcdmaRx++;
						break;
					case "  <td>Average Power: <i>10 Bursts</i></td>":
						if (sampleGsmTx < 3) {
							values.setGsm900TxValue(readValue(fileContent.get(i + 3)), sampleGsmTx);
						} else {
							values.setGsm1800TxValue(readValue(fileContent.get(i + 3)), sampleGsmTx - 3);
						}
						sampleGsmTx++;
						break;
					case "  <td>RX Lev:</td>":
						if (sampleGsmRx < 3) {
							values.setGsm900RxValue(readValue(fileContent.get(i + 3)), sampleGsmRx);
						} else {
							values.setGsm1800RxValue(readValue(fileContent.get(i + 3)), sampleGsmRx - 3);
						}
						sampleGsmRx++;
						break;
				}
				if (sampleGsmRx == 6) {
					return values;
				}
			}
		} catch (NumberFormatException exception) {
			System.out.println("Incomplete report: " + filename);
			values = null;
		}
		return values;
	}
}
