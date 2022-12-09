package hu.open.assistant.commons.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom INI parser. The INI files can be read and written to disk. The INI format is in key value pairs separated
 * by an equals (=) sign. Processing is done in the UTF-8 format.
 */
public class IniParser {

    private final FileHandler fileHandler;

    /**
     * Initialise the INI parser with the given FileHandler which provides disk access.
     */
    public IniParser(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    /**
     * Read and parse the content of an INI file.
     *
     * @param filePath target .ini file
     * @return a map of key value pairs
     */
    public Map<String, String> readIniFile(String filePath) {
        Map<String, String> settingsMap = new HashMap<>();
        List<String> rawText = fileHandler.readUtf8TextToList(filePath, false);
        if (rawText != null) {
            for (String line : rawText) {
                if (line.contains("=")) {
                    line = line.replace(" = ", "=");
                    String[] parts = line.split("=");
                    if (parts.length > 1) {
                        settingsMap.put(parts[0], parts[1]);
                    }
                }
            }
        }
        return settingsMap;
    }

    /**
     * Create a .ini file on disk with the given data
     *
     * @param targetPath  target .ini file
     * @param settingsMap key value pairs as data
     */
    public void writeIniFile(String targetPath, Map<String, String> settingsMap) {
        List<String> rawText = new ArrayList<>();
        for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
            rawText.add(entry.getKey().concat(" = ").concat(entry.getValue()));
            fileHandler.writeUtf8Text(targetPath, rawText, false);
        }
    }
}
