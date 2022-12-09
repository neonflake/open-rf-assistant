package hu.open.assistant.commons.util;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Helper class for text processing tasks.
 */
public class TextHelper {

	/**
	 * Add an extra timestamp and hostname (separated with minuses) before the text and return the results.
	 */
	public static String createLogEntry(String text) {
		return DateHelper.localDateTimeToIsoTextDateTime(LocalDateTime.now())
				.concat(" - ")
				.concat(SystemHelper.getHostName())
				.concat(" - ")
				.concat(text);
	}

	/**
	 * Return the first word from the given text.
	 */
	public static String getFirstWord(String text) {
		return text.split(" ")[0];
	}

	/**
	 * Convert a number into text and below ten add an extra zero before the number.
	 */
	public static String addZero(int number) {
		if (number < 10) {
			return "0" + number;
		} else {
			return "" + number;
		}
	}

	/**
	 * Convert a list of strings into a single string. After each element a linebreak is added.
	 */
	public static String stringListToLineBrokenString(List<String> list) {
		StringBuilder stringBuilder = new StringBuilder();
		list.forEach(string -> stringBuilder.append(string).append('\n'));
		return stringBuilder.toString();
	}

	/**
	 * Convert an array of strings into a single string. Between elements a comma is added.
	 */
	public static String stringArrayToCommaSeparatedString(String[] array) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			stringBuilder.append(array[i]);
			if (i != array.length - 1) {
				stringBuilder.append(',');
			}
		}
		return stringBuilder.toString();
	}
}
