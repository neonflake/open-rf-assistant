package hu.open.assistant.commons.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * An extension of the org.json (JSON) parser. The JSON files can be read and written to disk. Ideally a JSON file must
 * start with a root JSON object but a raw JSON array is also supported. Processing is done in the UTF-8 format.
 */
public class JsonParser {

    private final FileHandler fileHandler;

    /**
     * Initialise the JSON parser with the given FileHandler which provides disk access.
     */
    public JsonParser(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    /**
     * Read and parse a JSON file from disk which contains a root JSON object.
     *
     * @param filepath   .json file to process
     * @param checkInUse check if target file is currently used by another application
     * @return a root JSON object or null if file doesn't exist (or is in use)
     */
    public JSONObject readJsonObject(String filepath, boolean checkInUse) {
        if (!checkInUse || fileHandler.fileNotInUse(filepath)) {
            try {
                InputStream inputStream = new FileInputStream(filepath);
                JSONObject jsonObject = parseJSON(inputStream);
                inputStream.close();
                return jsonObject;
            } catch (IOException exception) {
                System.out.println("Json file read error: " + filepath);
            }
        }
        return null;
    }

    /**
     * Create a .json file on disk with the given data.
     *
     * @param filePath   target .json file
     * @param jsonObject root JSON object as data
     */
    public void writeJsonObject(String filePath, JSONObject jsonObject) {
        try {
            FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonObject.toString(1));
            writer.close();
            fileWriter.close();
        } catch (IOException exception) {
            System.out.println("Json file write error: " + filePath);
        }
    }

    private JSONObject parseJSON(InputStream inputStream) throws IOException {
        JSONObject parsedObject = null;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            parsedObject = new JSONObject(new JSONTokener(bufferedReader));
        } catch (JSONException exception) {
            System.out.println(exception.getMessage());
        }
        bufferedReader.close();
        inputStreamReader.close();
        return parsedObject;
    }
}
