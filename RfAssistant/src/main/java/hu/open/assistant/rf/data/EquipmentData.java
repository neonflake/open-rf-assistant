package hu.open.assistant.rf.data;

import hu.open.assistant.rf.model.Equipment;
import hu.open.assistant.commons.data.JsonParser;
import hu.open.assistant.commons.util.TextHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data class which reads and writes equipments from or to the disk. CMU and CMW profile parts are stored separately in
 * JSON format.
 */
public class EquipmentData {

    private static final String DATAFILE = "equipments.json";

    private final String dataFolder;
    private final JsonParser jsonParser;

    public EquipmentData(String dataFolder, JsonParser jsonParser) {
        this.dataFolder = dataFolder;
        this.jsonParser = jsonParser;
    }

    public List<Equipment> readEquipments() {
        List<Equipment> equipments = new ArrayList<>();
        JSONObject rootJsonObject = jsonParser.readJsonObject(dataFolder + "\\" + DATAFILE, false);
        if (rootJsonObject != null) {
            JSONArray jsonArray = rootJsonObject.optJSONArray("equipments");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Equipment equipment = new Equipment();
                    try {
                        equipment.setType(jsonObject.getString("type"));
                        equipment.setManufacturer(jsonObject.getString("manufacturer"));
                        equipment.setSupportedNetworks(jsonObject.getString("supportedNetworks").split(","));
                        equipment.setUsage(jsonObject.getString("usage"));
                        equipment.setCmuBox(jsonObject.getString("cmuBox"));
                        equipment.setCmuPosition(jsonObject.getString("cmuPosition"));
                        equipment.setCmuPositionDetail(jsonObject.getString("cmuPositionDetail"));
                        equipment.setCmuPositionImage(jsonObject.getString("cmuPositionImage"));
                        equipment.setCmwBox(jsonObject.getString("cmwBox"));
                        equipment.setCmwPosition(jsonObject.getString("cmwPosition"));
                        equipment.setCmwPositionDetail(jsonObject.getString("cmwPositionDetail"));
                        equipment.setCmwPositionImage(jsonObject.getString("cmwPositionImage"));
                        equipment.setGenericInfo(jsonObject.getString("genericInfo"));
                        equipment.setCmuInfo(jsonObject.getString("cmuInfo"));
                        equipment.setCmwInfo(jsonObject.getString("cmwInfo"));
                        equipment.initDefaultValues();
                        equipments.add(equipment);
                    } catch (JSONException exception) {
                        System.out.println(exception.getMessage());
                        System.out.println("Equipment processing error: " + jsonObject);
                    }
                }
            }
        }
        Collections.sort(equipments);
        return equipments;
    }

    public void writeEquipments(List<Equipment> equipments) {
        Collections.sort(equipments);
        JSONArray jsonArray = new JSONArray();
        for (Equipment equipment : equipments) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", equipment.getType());
            jsonObject.put("manufacturer", equipment.getManufacturer());
            jsonObject.put("supportedNetworks", TextHelper.stringArrayToCommaSeparatedString(equipment.getSupportedNetworks()));
            jsonObject.put("usage", equipment.getUsage());
            jsonObject.put("cmuBox", equipment.getCmuBox());
            jsonObject.put("cmuPosition", equipment.getCmuPosition());
            jsonObject.put("cmuPositionDetail", equipment.getCmuPositionDetail());
            jsonObject.put("cmuPositionImage", equipment.getCmuPositionImage());
            jsonObject.put("cmwBox", equipment.getCmwBox());
            jsonObject.put("cmwPosition", equipment.getCmwPosition());
            jsonObject.put("cmwPositionDetail", equipment.getCmwPositionDetail());
            jsonObject.put("cmwPositionImage", equipment.getCmwPositionImage());
            jsonObject.put("genericInfo", equipment.getGenericInfo());
            jsonObject.put("cmuInfo", equipment.getCmuInfo());
            jsonObject.put("cmwInfo", equipment.getCmwInfo());
            jsonArray.put(jsonObject);
        }
        jsonParser.writeJsonObject(dataFolder + "\\" + DATAFILE, new JSONObject().put("equipments", jsonArray));
    }
}
