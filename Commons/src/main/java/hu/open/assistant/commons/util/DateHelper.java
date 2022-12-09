package hu.open.assistant.commons.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Helper class for date handling tasks.
 */
public class DateHelper {

    /**
     * Convert LocalDate to a dot separated (HU format) date text.
     */
    public static String localDateToTextDate(LocalDate date) {
        return TextHelper.addZero(date.getYear()) + "." + TextHelper.addZero(date.getMonthValue()) + "." + TextHelper.addZero(date.getDayOfMonth());
    }

    /**
     * Convert LocalDateTime to a dot and colon separated (HU format) date and time text.
     */
    public static String localDateTimeToTextDateTime(LocalDateTime date) {
        return date.getYear() + "." + TextHelper.addZero(date.getMonthValue()) + "." + TextHelper.addZero(date.getDayOfMonth()) + " - "
                + TextHelper.addZero(date.getHour()) + ":" + TextHelper.addZero(date.getMinute()) + ":" + TextHelper.addZero(date.getSecond());
    }

    /**
     * Convert LocalDateTime to a hyphen and underscore separated date and time text (ideal for file storage).
     */
    public static String localDateTimeToFilename(LocalDateTime dateTime) {
        return dateTime.getYear() + "-" + TextHelper.addZero(dateTime.getMonthValue()) + "-" + TextHelper.addZero(dateTime.getDayOfMonth()) + "_" +
                TextHelper.addZero(dateTime.getHour()) + "-" + TextHelper.addZero(dateTime.getMinute()) + "-" + TextHelper.addZero(dateTime.getSecond());
    }

    /**
     * Convert LocalDateTime to ISO format date and time text.
     */
    public static String localDateTimeToIsoTextDateTime(LocalDateTime dateTime) {
        return dateTime.getYear() + "-" + TextHelper.addZero(dateTime.getMonthValue()) + "-" + TextHelper.addZero(dateTime.getDayOfMonth()) +
                " " + TextHelper.addZero(dateTime.getHour()) + ":" + TextHelper.addZero(dateTime.getMinute()) + ":" + TextHelper.addZero(dateTime.getSecond());
    }

    /**
     * Convert LocalDateTime to ISO format time text.
     */
    public static String localDateTimeToIsoTextTime(LocalDateTime dateTime) {
        return TextHelper.addZero(dateTime.getHour()) + ":" + TextHelper.addZero(dateTime.getMinute()) + ":" + TextHelper.addZero(dateTime.getSecond());
    }

    /**
     * Convert LocalDateTime to ISO format date text.
     */
    public static String localDateTimeToIsoTextDate(LocalDateTime dateTime) {
        return dateTime.getYear() + "-" + TextHelper.addZero(dateTime.getMonthValue()) + "-" + TextHelper.addZero(dateTime.getDayOfMonth());
    }

    /**
     * Convert LocalDateTime to ISO format date text with month and day only.
     */
    public static String localDateTimeToShortIsoTextDate(LocalDateTime dateTime) {
        return TextHelper.addZero(dateTime.getMonthValue()) + "-" + TextHelper.addZero(dateTime.getDayOfMonth());
    }

    /**
     * Convert ISO format date and time to LocalDateTime.
     */
    public static LocalDateTime isoTextDateTimeToLocalDateTime(String text) {
        String[] parts = text.split(" ");
        String[] dateParts = parts[0].split("-");
        String[] timeParts = parts[1].split(":");
        return (LocalDateTime.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]),
                Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), Integer.parseInt(timeParts[2])));
    }

    /**
     * Convert HU month name to month number (of year).
     *
     * @param text HU month name
     * @return month number or 0 when invalid name is given
     */
    public static int monthTextToNumber(String text) {
        switch (text) {
            case "január":
                return 1;
            case "február":
                return 2;
            case "március":
                return 3;
            case "április":
                return 4;
            case "május":
                return 5;
            case "június":
                return 6;
            case "július":
                return 7;
            case "augusztus":
                return 8;
            case "szeptember":
                return 9;
            case "október":
                return 10;
            case "november":
                return 11;
            case "december":
                return 12;
            default:
                return 0;
        }
    }
}