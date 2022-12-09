package hu.open.assistant.commons.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom CSV parser. The CSV files can be read and written to disk. The supported CSV format is normal comma (,)
 * separated values that are surrounded by quotation ("") marks. Processing is done in the UTF-8 format.
 */
public class CsvParser {

	private final FileHandler fileHandler;

	/**
	 * Initialise the CSV parser with the given FileHandler which provides disk access.
	 */
	public CsvParser(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	/**
	 * Read and parse the content of a CSV file.
	 *
	 * @param filePath .csv file to process
	 * @return list of String array where one list element represents a row and the cells are in the String array.
	 */
	public List<String[]> readCsvFile(String filePath) {
		StringBuilder stringBuilder = new StringBuilder();
		List<String[]> records = new ArrayList<>();
		List<String> rawText = fileHandler.readUtf8TextToList(filePath, false);
		int fieldCount = 0;
		if (rawText != null) {
			fieldCount = checkFieldCount(rawText.get(0));
		}
		if (fieldCount > 0) {
			int pointer;
			for (String line : rawText) {
				pointer = 0;
				String[] fields = new String[fieldCount];
				char[] characters = line.toCharArray();
				boolean bufferOpen = false;
				for (char character : characters) {
					if (character == '"') {
						if (!bufferOpen) {
							stringBuilder.setLength(0);
							bufferOpen = true;
						} else {
							fields[pointer] = stringBuilder.toString();
							pointer++;
							bufferOpen = false;
						}
					} else if (bufferOpen) {
						stringBuilder.append(character);
					}
				}
				records.add(fields);
			}
		}
		return records;
	}

	private int checkFieldCount(String line) {
		char[] characters = line.toCharArray();
		int count = 0;
		for (char character : characters) {
			if (character == '"') {
				count++;
			}
		}
		return count / 2;
	}

	/**
	 * Create a .csv file on disk with the given data.
	 *
	 * @param filePath target .csv file
	 * @param records  list of cells containing data
	 * @param header   list of cells containing header
	 * @param append   append content to the end of an existing .csv file (do not overwrite whole file)
	 */
	public void writeCsvFile(String filePath, List<String[]> records, String[] header, boolean append) {
		if (!append || !fileHandler.fileExists(filePath)) {
			records.add(0, header);
		}
		StringBuilder stringBuilder = new StringBuilder();
		List<String> rawText = new ArrayList<>();
		for (String[] fields : records) {
			stringBuilder.setLength(0);
			for (String field : fields) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(",");
				}
				if (field != null) {
					stringBuilder.append('"').append(field).append('"');
				} else {
					stringBuilder.append("\"\"");
				}
			}
			rawText.add(stringBuilder.toString());
		}
		if (rawText.size() > 0) {
			fileHandler.writeUtf8Text(filePath, rawText, append);
		}
	}
}
