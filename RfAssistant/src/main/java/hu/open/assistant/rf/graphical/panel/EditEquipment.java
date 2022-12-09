package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.EquipmentListRenderer;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssComboBox;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.filter.EquipmentFilter;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.Equipment;
import hu.open.assistant.rf.graphical.RfNotice;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GUI for editing equipment information. A list is loaded with equipments which can be narrowed down with a search
 * field. By selecting an equipment the text fields are filled with generic and profile related information. Position
 * information and illustration can only be edited when the associated RF profiles exists. There is a option to check
 * for profile consistency which if finds an error, corrects it and the modifications can be seen in the list.
 * Equipments are created automatically on profile creation but manual creation is also available on this panel.
 * Equipment deletion is only available when there are no associated profiles for the equipment. Network and profile
 * related information must be given in a fix format defined in constants.
 */
public class EditEquipment extends RfPanel {

	private static final List<String> SUPPORTED_POSITIONS = Arrays.asList("FF", "FL", "FJ", "FB", "--");
	private static final List<String> SUPPORTED_NETWORKS = Arrays.asList("2G", "3G", "4G", "5G");

	private static final Dimension SMALL_LABEL_DIMENSION = new Dimension(180, 50);
	private static final Dimension MEDIUM_LABEL_DIMENSION = new Dimension(220, 50);
	private static final Dimension LARGE_LABEL_DIMENSION = new Dimension(350, 50);
	private static final Dimension SMALL_INPUT_DIMENSION = new Dimension(200, 50);
	private static final Dimension MEDIUM_INPUT_DIMENSION = new Dimension(220, 50);
	private static final Dimension EQUIPMENT_LIST_DIMENSION = new Dimension(220, 290);
	private static final int SMALL_TEXT_SIZE = 14;
	private static final int LARGE_TEXT_SIZE = 16;
	private static final Color ACTIVE_COLOR = Color.black;
	private static final Color INACTIVE_COLOR = Color.lightGray;

	private final AssButton modifyButton;
	private final AssButton saveButton;
	private final AssButton deleteButton;
	private final AssLabel cmuProfileLabel;
	private final AssLabel cmuPositionLabel;
	private final AssLabel cmuImageLabel;
	private final AssLabel cmwProfileLabel;
	private final AssLabel cmwPositionLabel;
	private final AssLabel cmwImageLabel;
	private final AssTextField searchField;
	private final AssList<Equipment> equipmentList;
	private final AssTextField genericInfoField;
	private final AssTextField supportedNetworksField;
	private final AssTextField cmuPositionField;
	private final AssTextField cmuImageField;
	private final AssTextField cmuInfoField;
	private final AssTextField cmwPositionField;
	private final AssTextField cmwImageField;
	private final AssTextField cmwInfoField;
	private final AssComboBox usageBox;
	private final List<Equipment> removedEquipments = new ArrayList<>();
	private List<Equipment> equipments;
	private Equipment selectedEquipment;

	public EditEquipment(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "EditEquipment");
		//placer.enableDebug();
		AssLabel label = new AssLabel("Készülékek szerkesztése", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
		AssLabel genericInfoLabel = new AssLabel("Általános információ:", LARGE_TEXT_SIZE, MEDIUM_LABEL_DIMENSION);
		AssLabel supportedNetworksLabel = new AssLabel("Támogatott hálózatok:", LARGE_TEXT_SIZE, MEDIUM_LABEL_DIMENSION);
		cmuProfileLabel = new AssLabel("", SMALL_TEXT_SIZE, LARGE_LABEL_DIMENSION);
		cmuPositionLabel = new AssLabel("CMU pozíció kiegészítés:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		cmuImageLabel = new AssLabel("CMU pozíció képe:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel cmuInfoLabel = new AssLabel("CMU műszer információ:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		cmwProfileLabel = new AssLabel("", SMALL_TEXT_SIZE, LARGE_LABEL_DIMENSION);
		cmwPositionLabel = new AssLabel("CMW pozíció kiegészítés:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		cmwImageLabel = new AssLabel("CMW pozíció képe:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel usageLabel = new AssLabel("Készülék kezelése:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel cmwInfoLabel = new AssLabel("CMW műszer információ:", SMALL_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssButton folderButton = new AssButton("EditEquipment folderButton", "Pozíciós képek mappa", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		modifyButton = new AssButton("EditEquipment modifyButton", "Módosít", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton createButton = new AssButton("EditEquipment createButton", "Létrehozás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		deleteButton = new AssButton("EditEquipment deleteButton", "Törlés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		saveButton = new AssButton("EditEquipment saveButton", "Mentés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton reloadButton = new AssButton("EditEquipment reloadButton", "Profilok ellenőrzése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton backButton = new AssButton("EditEquipment backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		searchField = new AssTextField("EditEquipment searchField", MEDIUM_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		genericInfoField = new AssTextField("EditEquipment genericInfoField", MEDIUM_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		supportedNetworksField = new AssTextField("EditEquipment supportedNetworksField", MEDIUM_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		equipmentList = new AssList<>("EditEquipment equipmentList", SMALL_TEXT_SIZE, EQUIPMENT_LIST_DIMENSION, listener, new EquipmentListRenderer());
		cmuPositionField = new AssTextField("EditEquipment cmuPositionField", SMALL_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmuImageField = new AssTextField("EditEquipment cmuImageField", SMALL_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmuInfoField = new AssTextField("EditEquipment cmuInfoField", SMALL_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmwPositionField = new AssTextField("EditEquipment cmwPositionField", SMALL_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmwImageField = new AssTextField("EditEquipment cmwImageField", SMALL_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		cmwInfoField = new AssTextField("EditEquipment cmwInfoField", SMALL_INPUT_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		searchField.addKeyListener(window);
		genericInfoField.addKeyListener(window);
		supportedNetworksField.addKeyListener(window);
		cmuPositionField.addKeyListener(window);
		cmuImageField.addKeyListener(window);
		cmuInfoField.addKeyListener(window);
		cmwPositionField.addKeyListener(window);
		cmwImageField.addKeyListener(window);
		cmwInfoField.addKeyListener(window);
		String[] usages = new String[2];
		usages[0] = "normális";
		usages[1] = "problémás";
		usageBox = new AssComboBox("EditEquipment usageBox", usages, SMALL_TEXT_SIZE, SMALL_INPUT_DIMENSION, listener);
		placer.addComponent(label, 1, 1, 3, 1);
		placer.addComponent(modifyButton, 4, 1, 1, 1);
		placer.addComponent(cmuProfileLabel, 2, 2, 2, 1);
		placer.addComponent(createButton, 4, 2, 1, 1);
		placer.addComponent(searchField, 1, 2, 1, 1);
		placer.addComponent(equipmentList, 1, 3, 1, 4);
		placer.addComponent(cmuPositionLabel, 2, 3, 1, 1);
		placer.addComponent(cmuPositionField, 3, 3, 1, 1);
		placer.addComponent(deleteButton, 4, 3, 1, 1);
		placer.addComponent(cmuImageLabel, 2, 4, 1, 1);
		placer.addComponent(cmuImageField, 3, 4, 1, 1);
		placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
		placer.addComponent(cmuInfoLabel, 2, 5, 1, 1);
		placer.addComponent(cmuInfoField,3,5,1,1);
		placer.addComponent(saveButton,4,5,1,1);
		placer.addComponent(genericInfoLabel,1,6,1,1);
		placer.addComponent(cmwProfileLabel, 2, 6, 2, 1);
		placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
		placer.addComponent(genericInfoLabel, 1, 7, 1, 1);
		placer.addComponent(cmwPositionLabel,2,7,1,1);
		placer.addComponent(cmwPositionField,3,7,1,1);
		placer.addComponent(folderButton,4,7,1,1);
		placer.addComponent(genericInfoField,1,8,1,1);
		placer.addComponent(cmwImageLabel,2,8,1,1);
		placer.addComponent(cmwImageField,3,8,1,1);
		placer.addComponent(reloadButton,4,8,1,1);
		placer.addComponent(supportedNetworksLabel,1,9,1,1);
		placer.addComponent(cmwInfoLabel,2,9,1,1);
		placer.addComponent(cmwInfoField,3,9,1,1);
		placer.addComponent(backButton,4,9,1,1);
		placer.addComponent(supportedNetworksField,1,10,1,1);
		placer.addComponent(usageLabel,2,10,1,1);
		placer.addComponent(usageBox,3,10,1,1);
		placer.addImageComponent(logoImage,4,10,1,1);
	}

	private boolean isNameInputValid(String name) {
		if (name.split(" ").length < 2) {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_CREATE_ERROR);
			return false;
		}
		if (assistant.hasInvalidSeparatorCharacter(name)) {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_CREATE_FORMAT);
			return false;
		}
		if (ValidationHelper.hasForbiddenCharacter(name)) {
			window.showNotification(RfNotice.GENERIC_INVALID_CHARACTER);
			return false;
		}
		return true;
	}

	public void createEquipment(String name) {
		if (isNameInputValid(name)) {
			String[] nameParts = name.split(" ");
			Equipment equipment = new Equipment();
			equipment.setManufacturer(nameParts[0]);
			String type = "";
			for (int i = 1; i < nameParts.length; i++) {
				if (i != 1) {
					type = type.concat(" ");
				}
				type = type.concat(nameParts[i]);
			}
			equipment.setType(type);
			equipment.setUsage("normális");
			equipment.setCmuPositionImage("");
			equipment.setCmwPositionImage("");
			equipment.initDefaultValues();
			String[] supportedNetworks = {"2G", "3G", "4G"};
			equipment.setSupportedNetworks(supportedNetworks);
			equipments.add(equipment);
			Collections.sort(equipments);
			equipmentList.changeModel(equipments, false);
			equipmentList.setSelectedValue(equipment, true);
			setSelectedEquipment();
			saveButton.setEnabled(isModified());
		}
	}

	public boolean isModified() {
		return isAnyEquipmentModified(equipmentList.getModelAsArrayList()) || !removedEquipments.isEmpty();
	}

	public void saveEquipments() {
		assistant.writeEquipments(equipments);
	}

	private boolean isSupportedNetworkValid(String line) {
		String[] elements = line.split(",");
		if (elements.length > 0 && line.charAt(line.length() - 1) != ',') {
			for (String element : elements) {
				if (!SUPPORTED_NETWORKS.contains(element)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean isInputValid() {
		String cmuPosition = cmuPositionField.getText();
		String cmwPosition = cmwPositionField.getText();
		String supportedNetworks = supportedNetworksField.getText();
		if ((!cmuPosition.isBlank() && !SUPPORTED_POSITIONS.contains(cmuPosition)) ||
				(!cmwPosition.isBlank() && !SUPPORTED_POSITIONS.contains(cmwPosition))) {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_POSITION_ERROR);
			return false;
		}
		if (supportedNetworks.isBlank()) {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_NETWORK_MISSING_ERROR);
			return false;
		}
		if (!isSupportedNetworkValid(supportedNetworks)) {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_NETWORK_FORMAT_ERROR);
			return false;
		}
		if (ValidationHelper.hasForbiddenCharacter(cmuInfoField.getText()) || ValidationHelper.hasForbiddenCharacter(cmwInfoField.getText()) ||
				ValidationHelper.hasForbiddenCharacter(genericInfoField.getText())) {
			window.showNotification(RfNotice.GENERIC_INVALID_CHARACTER);
			return false;
		}
		return true;
	}

	public void modifyEquipment() {
		if (isInputValid()) {
			selectedEquipment.setGenericInfo(genericInfoField.getText());
			selectedEquipment.setCmuInfo(cmuInfoField.getText());
			selectedEquipment.setCmwInfo(cmwInfoField.getText());
			selectedEquipment.setCmuPositionDetail(cmuPositionField.getText());
			selectedEquipment.setCmwPositionDetail(cmwPositionField.getText());
			selectedEquipment.setCmuPositionImage(cmuImageField.getText());
			selectedEquipment.setCmwPositionImage(cmwImageField.getText());
			selectedEquipment.setUsage((String) usageBox.getSelectedItem());
			selectedEquipment.setSupportedNetworks(supportedNetworksField.getText().split(","));
			saveButton.setEnabled(isModified());
			equipmentList.repaint();
		}
	}

	public void deleteEquipment() {
		if (selectedEquipment.getCmuBox().isBlank() && selectedEquipment.getCmwBox().isBlank()) {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_DELETE_CONFIRM);
		} else {
			window.showNotification(RfNotice.EDIT_EQUIPMENT_DELETE_FAIL);
		}
	}

	public void removeEquipment() {
		equipmentList.removeElement(selectedEquipment);
		equipments.remove(selectedEquipment);
		removedEquipments.add(selectedEquipment);
		saveButton.setEnabled(isModified());
		clearEquipmentInfo();
	}

	public void setSelectedEquipment() {
		selectedEquipment = equipmentList.getSelectedValue();
		enableInfoFields();
		enableEquipmentButtons();
		updateEquipmentInfo();
	}

	private void enableInfoFields() {
		cmuInfoField.setEnabled(true);
		cmwInfoField.setEnabled(true);
		supportedNetworksField.setEnabled(true);
		genericInfoField.setEnabled(true);
		usageBox.setEnabled(true);
	}

	private void enableCmwFields() {
		cmwProfileLabel.setForeground(ACTIVE_COLOR);
		cmwPositionField.setEnabled(true);
		cmwImageField.setEnabled(true);
		cmwPositionLabel.setForeground(ACTIVE_COLOR);
		cmwImageLabel.setForeground(ACTIVE_COLOR);
	}

	private void disableCmwFields() {
		cmwProfileLabel.setForeground(INACTIVE_COLOR);
		cmwPositionField.setEnabled(false);
		cmwImageField.setEnabled(false);
		cmwPositionLabel.setForeground(INACTIVE_COLOR);
		cmwImageLabel.setForeground(INACTIVE_COLOR);
	}

	private void enableEquipmentButtons() {
		modifyButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}

	private void enableCmuFields() {
		cmuProfileLabel.setForeground(ACTIVE_COLOR);
		cmuPositionField.setEnabled(true);
		cmuImageField.setEnabled(true);
		cmuPositionLabel.setForeground(ACTIVE_COLOR);
		cmuImageLabel.setForeground(ACTIVE_COLOR);
	}

	private void disableCmuFields() {
		cmuProfileLabel.setForeground(INACTIVE_COLOR);
		cmuPositionField.setEnabled(false);
		cmuImageField.setEnabled(false);
		cmuPositionLabel.setForeground(INACTIVE_COLOR);
		cmuImageLabel.setForeground(INACTIVE_COLOR);
	}

	private void updateEquipmentInfo() {
		if (!selectedEquipment.getCmuBox().isBlank()) {
			cmuProfileLabel.setText("Shield Box: " + selectedEquipment.getCmuBox() + " - Pozíció: " + selectedEquipment.getCmuPosition());
			enableCmuFields();
		} else {
			cmuProfileLabel.setText("CMU profil nem elérhető");
			disableCmuFields();
		}
		if (!selectedEquipment.getCmwBox().isBlank()) {
			cmwProfileLabel.setText("Shield Box: " + selectedEquipment.getCmwBox() + " - Pozíció: " + selectedEquipment.getCmwPosition());
			enableCmwFields();
		} else {
			cmwProfileLabel.setText("CMW profil nem elérhető");
			disableCmwFields();
		}
		cmuPositionField.setText(selectedEquipment.getCmuPositionDetail());
		cmuImageField.setText(selectedEquipment.getCmuPositionImage());
		cmuInfoField.setText(selectedEquipment.getCmuInfo());
		cmwPositionField.setText(selectedEquipment.getCmwPositionDetail());
		cmwImageField.setText(selectedEquipment.getCmwPositionImage());
		cmwInfoField.setText(selectedEquipment.getCmwInfo());
		supportedNetworksField.setText(TextHelper.stringArrayToCommaSeparatedString(selectedEquipment.getSupportedNetworks()));
		genericInfoField.setText(selectedEquipment.getGenericInfo());
		usageBox.setSelectedItem(selectedEquipment.getUsage());
	}

	private void clearEquipmentInfo() {
		selectedEquipment = null;
		cmuProfileLabel.setText("");
		cmwProfileLabel.setText("");
		cmuPositionField.setText("");
		cmuImageField.setText("");
		cmuInfoField.setText("");
		cmwPositionField.setText("");
		cmwImageField.setText("");
		cmwInfoField.setText("");
		supportedNetworksField.setText("");
		genericInfoField.setText("");
		disableCmuFields();
		disableCmwFields();
		modifyButton.setEnabled(false);
		deleteButton.setEnabled(false);
		cmuInfoField.setEnabled(false);
		cmwInfoField.setEnabled(false);
		supportedNetworksField.setEnabled(false);
		genericInfoField.setEnabled(false);
		usageBox.setEnabled(false);
	}

	public void filterList() {
		clearEquipmentInfo();
		equipmentList.changeModel(EquipmentFilter.getEquipmentsNameLike(equipments, searchField.getText()), false);
		if (equipmentList.getModelSize() > 0) {
			equipmentList.setSelectedIndex(0);
			setSelectedEquipment();
		}
	}

	public void prepareEquipments(boolean checked) {
		saveButton.setEnabled(false);
		removedEquipments.clear();
		searchField.setText("");
		clearEquipmentInfo();
		placer.disableComponents();
		equipmentList.clearModel();
		if (checked) {
			equipments = assistant.readCheckedEquipments();
			saveButton.setEnabled(isAnyEquipmentModified(equipments));
		} else {
			equipments = assistant.readEquipments();
		}
		equipmentList.changeModel(equipments, false);
		placer.enableComponents();
	}

	private boolean isAnyEquipmentModified(List<Equipment> equipments) {
		for (Equipment equipment : equipments) {
			if (equipment.isModified()) {
				return true;
			}
		}
		return false;
	}
}
