package hu.open.assistant.commons.util;

import java.text.DecimalFormat;

/**
 * Helper class for number related tasks.
 */
public class NumberHelper {

    /**
     * Round a double to two decimal places.
     *
     * @param number double to round
     * @return rounded number
     */
    public static double twoDecimalPlaceOf(double number) {
        DecimalFormat format = new DecimalFormat("#.##");
        return Double.parseDouble(format.format(number).replace(',', '.'));
    }

    /**
     * Round a double to one decimal place.
     *
     * @param number double to round
     * @return rounded number
     */
    public static double oneDecimalPlaceOf(double number) {
        DecimalFormat format = new DecimalFormat("#.#");
        return Double.parseDouble(format.format(number).replace(',', '.'));
    }

    /**
     * Check if an array contains any values that differ from zero.
     *
     * @param array to check
     * @return true if any value is greater or lower than zero, false otherwise.
     */
    public static boolean arrayHasValues(double[] array) {
        for (double value : array) {
            if (value != 0) {
                return true;
            }
        }
        return false;
    }
}
