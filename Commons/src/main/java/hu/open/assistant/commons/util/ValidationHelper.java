package hu.open.assistant.commons.util;

import java.util.Arrays;
import java.util.List;

/**
 * Helper class for data validation tasks.
 */
public class ValidationHelper {

    private static final List<Character> FORBIDDEN_CHARACTERS = Arrays.asList('"', '{', '}', '[', ']', '=');

    /**
     * Return the characters that are forbidden for user input as a readable text.
     */
    public static String getForbiddenCharacters() {
        StringBuilder stringBuilder = new StringBuilder(" ");
        FORBIDDEN_CHARACTERS.forEach(character -> stringBuilder.append(character).append(" "));
        return stringBuilder.toString();
    }

    /**
     * Check if text contains forbidden character and return the results.
     */
    public static boolean hasForbiddenCharacter(String text) {
        for (char character : text.toCharArray()) {
            if (FORBIDDEN_CHARACTERS.contains(character)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if text contains only numbers and return the results.
     */
    public static boolean hasOnlyNumbers(String text) {
        for (char character : text.toCharArray()) {
            if (!Character.isDigit(character)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if text is not suitable for double conversion.
     *
     * @param text to check
     * @return true when text is not convertible to double
     */
    public static boolean isInvalidDouble(String text) {
        try {
            return Double.isNaN(Double.parseDouble(text));
        } catch (NumberFormatException exception) {
            return true;
        }
    }

    /**
     * Check if text is not suitable for long conversion.
     *
     * @param text to check
     * @return true when text is not convertible to long
     */
    public static boolean isInvalidLong(String text) {
        try {
            Long.parseLong(text);
            return false;
        } catch (NumberFormatException exception) {
            return true;
        }
    }

    /**
     * Check if text is not suitable for integer conversion.
     *
     * @param text to check
     * @return true when text is not convertible to integer
     */
    public static boolean isInvalidInteger(String text) {
        try {
            Integer.parseInt(text);
            return false;
        } catch (NumberFormatException exception) {
            return true;
        }
    }
}
