package hu.open.assistant.commons.util;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Helper class for tasks involving the operating system.
 */
public class SystemHelper {

    /**
     * Copy the text to system clipboard.
     */
    public static void stringToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(text);
        clipboard.setContents(data, data);
    }

    /**
     * Open a file in the software associated to the file type.
     */
    public static void openFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException exception) {
                System.out.println("error opening file: " + filePath);
            }
        }
    }

    /**
     * Run an external .jar file with the given command (filepath -parameter).
     */
    public static void runJar(String command) {
        String[] commandParts = command.split("-");
        String filePath = commandParts[0].trim();
        String parameter = commandParts.length > 1 ? "-" + commandParts[1] : "";
        ProcessBuilder processBuilder = new ProcessBuilder("javaw", "-jar", filePath, parameter);
        try {
            processBuilder.start();
        } catch (Exception IOException) {
            System.out.println("Error while running external jar!");
        }
    }

    /**
     * Return the current system's host name or an empty string if it cannot be resolved.
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException exception) {
            System.out.println("Unable to resolve host name!");
            return "";
        }
    }
}
