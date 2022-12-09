package hu.open.assistant.rf.data;

import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.ShieldBox;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.commons.data.JsonParser;
import hu.open.assistant.commons.util.TextHelper;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Data class which reads and writes profile parts from or to the disk. CMU and CMW profile parts are stored separately
 * in JSON format.
 */
public class ProfilePartsData {

	private static final String DATAFILE = "_profile_parts.json";

	private final String dataFolder;
	private final JsonParser jsonParser;

	public ProfilePartsData(String dataFolder, JsonParser jsonParser) {
		this.dataFolder = dataFolder;
		this.jsonParser = jsonParser;
	}

	public ProfileParts readProfileParts(TesterType testerType) {
		ProfileParts profileParts;
		JSONObject rootJsonObject;
		if (testerType == TesterType.CMU) {
			profileParts = new ProfileParts(TesterType.CMU);
			rootJsonObject = jsonParser.readJsonObject(dataFolder + "\\cmu" + DATAFILE, false);
		} else {
			profileParts = new ProfileParts(TesterType.CMW);
			rootJsonObject = jsonParser.readJsonObject(dataFolder + "\\cmw" + DATAFILE, false);
		}
		if (rootJsonObject != null) {
			JSONArray jsonArray = rootJsonObject.optJSONArray("profileParts");
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					switch (jsonObject.getString("type")) {
						case "manufacturer":
							profileParts.addManufacturer(jsonObject.getString("name"));
							break;
						case "box":
							ShieldBox box = new ShieldBox(new Contraction(jsonObject.getString("name"), jsonObject.getString("shortName")));
							String[] positions = jsonObject.getString("positions").split(",");
							for (String position : positions) {
								if (!position.isBlank()) {
									box.addPosition(position);
								}
							}
							profileParts.addFilledBox(box);
							break;
						case "script":
							profileParts.addScript(jsonObject.getString("name"));
							break;
					}
				}
			}
		}
		profileParts.sortParts();
		return profileParts;
	}

	public void writeProfileParts(ProfileParts parts) {
		parts.sortParts();
		JSONArray jsonArray = new JSONArray();
		for (String manufacturer : parts.getManufacturers()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "manufacturer");
			jsonObject.put("name", manufacturer);
			jsonArray.put(jsonObject);
		}
		for (ShieldBox box : parts.getShieldBoxes()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "box");
			jsonObject.put("name", box.getName());
			jsonObject.put("shortName", box.getShortName());
			jsonObject.put("positions", TextHelper.stringArrayToCommaSeparatedString(parts.getPositions(box.getName())));
			jsonArray.put(jsonObject);
		}
		for (String script : parts.getScripts()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "script");
			jsonObject.put("name", script);
			jsonArray.put(jsonObject);
		}
		jsonParser.writeJsonObject(dataFolder + "\\" + parts.getTesterType().getName() + DATAFILE, new JSONObject().put("profileParts", jsonArray));
	}
}
