package hu.open.assistant.rf.model.profile.parts;

import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.ShieldBox;
import hu.open.assistant.rf.model.TesterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores different parts for RF profile creation. These parts are different manufacturers, shield boxes and RF scripts.
 * Helps with conversion between short and normal shield box names. It can be used for both CMU and CMW profiles based
 * on the given tester type.
 */
public class ProfileParts {
	List<ShieldBox> shieldBoxes;
	List<String> manufacturers;
	List<String> scripts;
	TesterType testerType;

	public ProfileParts(TesterType testerType) {
		this.testerType = testerType;
		manufacturers = new ArrayList<>();
		shieldBoxes = new ArrayList<>();
		scripts = new ArrayList<>();
		if (testerType == TesterType.CMU) {
			scripts.add("UNKNOWN");
		}
	}

	public void sortParts() {
		Collections.sort(manufacturers);
		Collections.sort(shieldBoxes);
		Collections.sort(scripts);
		for (ShieldBox shieldBox : shieldBoxes) {
			shieldBox.sortPositions();
		}
	}

	public String longToShortBox(String longBox){
		for (ShieldBox shieldBox : shieldBoxes) {
			if (shieldBox.getName().equals(longBox)) {
				return shieldBox.getContraction().getShortName();
			}
		}
		return longBox;
	}

	public String shortToLongBox(String shortBox) {
		for (ShieldBox shieldBox : shieldBoxes) {
			if (shieldBox.getShortName().equals(shortBox)) {
				return shieldBox.getName();
			}
		}
		return shortBox;
	}

	public String[] getManufacturers() {
		return manufacturers.toArray(new String[0]);
	}

	public List<Object> getBoxes() {
		return shieldBoxes.stream().map(ShieldBox::getContraction).collect(Collectors.toList());
	}

	public List<ShieldBox> getShieldBoxes() {
		return shieldBoxes;
	}

	public String[] getStringBoxes() {
		List<String> stringList = new ArrayList<>();
		for (ShieldBox shieldBox : shieldBoxes) {
			stringList.add(shieldBox.getName());
		}
		return stringList.toArray(new String[0]);
	}

	public String[] getPositions(String name) {
		if (name != null) {
			for (ShieldBox shieldBox : shieldBoxes) {
				if (shieldBox.getName().equals(name)) {
					return shieldBox.getPositions().toArray(new String[0]);
				}
			}
		}
		return new String[0];
	}

	public TesterType getTesterType() {
		return testerType;
	}

	public String[] getScripts() {
		return scripts.toArray(new String[0]);
	}

	public void removeManufacturer(String manufacturer) {
		manufacturers.remove(manufacturer);
	}

	public void removeBox(String name) {
		for (ShieldBox shieldBox : shieldBoxes) {
			if (shieldBox.getName().equals(name)) {
				shieldBoxes.remove(shieldBox);
				return;
			}
		}
	}

	public void removePosition(String name, String position) {
		for (ShieldBox shieldBox  : shieldBoxes) {
			if (shieldBox.getName().equals(name)) {
				shieldBox.removePosition(position);
				return;
			}
		}
	}

	public void removeScript(String script) {
		scripts.remove(script);
	}

	public void addEmptyBox(Contraction contraction) {
		shieldBoxes.add(new ShieldBox(contraction));
	}

	public void addFilledBox(ShieldBox shieldBox) {
		shieldBoxes.add(shieldBox);
	}

	public void addPosition(String name, String position) {
		for (ShieldBox shieldBox  : shieldBoxes) {
			if (shieldBox.getName().equals(name)) {
				shieldBox.addPosition(position);
				return;
			}
		}
	}

	public void addScript(String script) {
		if (!script.equals("UNKNOWN")) {
			scripts.add(script);
		}
	}

	public void addManufacturer(String manufacturer) {
		manufacturers.add(manufacturer);
	}
}
