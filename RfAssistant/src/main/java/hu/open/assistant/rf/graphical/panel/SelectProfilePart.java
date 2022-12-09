package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.RfNotice;
import hu.open.assistant.rf.graphical.renderer.ProfilePartListRenderer;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.TesterType;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GUI for managing the available profile parts. The available profile parts (string, contraction) are shown on a list.
 * CMU and CMW profile parts are managed and stored separately. Selecting an element shows the name and short name
 * (when available) in text fields. New parts can be added and deleted when the part is not used in any of the RF
 * profiles.
 */
public class SelectProfilePart extends RfPanel {

    private static final Dimension LABEL_DIMENSION = new Dimension(200, 50);
    private static final Dimension BOX_AND_FIELD_DIMENSION = new Dimension(400, 50);
    private static final Dimension PART_LIST_DIMENSION = new Dimension(650, 490);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int MEDIUM_TEXT_SIZE = 16;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssButton deleteButton;
    private final AssButton saveButton;
    private final AssLabel titleLabel;
    private final AssLabel shortLabel;
    private final AssTextField shortField;
    private final AssTextField longField;
    private final AssList<Object> partList;
    private List<Shortcut> shortcuts;
    private List<Database> databases;
    private List<String> defaultList;
    private Object selectedPart;
    private String partType;
    private ProfileParts parts;
    private String selectedBox;
    private TesterType testerType;

    public SelectProfilePart(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectProfilePart");
        //placer.enableDebug();
        partList = new AssList<>("SelectProfilePart partList", SMALL_TEXT_SIZE, PART_LIST_DIMENSION, listener, new ProfilePartListRenderer());
        titleLabel = new AssLabel("", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        shortLabel = new AssLabel("Rövidítés:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel longLabel = new AssLabel("Megnevezés:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        shortField = new AssTextField("SelectProfilePart shortField", BOX_AND_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
        longField = new AssTextField("SelectProfilePart longField", BOX_AND_FIELD_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
        AssButton newButton = new AssButton("SelectProfilePart newButton", "Új", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        deleteButton = new AssButton("SelectProfilePart deleteButton", "Törlés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("SelectProfilePart backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        saveButton = new AssButton("SelectProfilePart saveButton", "Mentés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(newButton, 4, 1, 1, 1);
        placer.addComponent(longLabel, 1, 2, 1, 1);
        placer.addComponent(longField, 2, 2, 2, 1);
        placer.addComponent(deleteButton, 4, 2, 1, 1);
        placer.addComponent(shortLabel, 1, 3, 1, 1);
        placer.addComponent(shortField, 2, 3, 2, 1);
        placer.addComponent(saveButton, 4, 3, 1, 1);
        placer.addComponent(partList, 1, 4, 3, 7);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public void writeParts() {
        assistant.writeProfileParts(parts);
    }

    public void setSelectedPart() {
        selectedPart = partList.getSelectedValue();
        if (selectedPart instanceof Contraction) {
            shortField.setText(((Contraction) selectedPart).getShortName());
            longField.setText(((Contraction) selectedPart).getName());
        } else {
            longField.setText((String) selectedPart);
        }
        deleteButton.setEnabled(true);
    }

    public boolean isModified() {
        List<String> currentList;
        if (partType.equals("box")) {
            currentList = partList.getModelAsArrayList().stream().map(object -> ((Contraction) object).getName()).collect(Collectors.toList());
        } else {
            currentList = partList.getModelAsArrayList().stream().map(object -> ((String) object)).collect(Collectors.toList());
        }
        Collections.sort(currentList);
        Collections.sort(defaultList);
        return !currentList.equals(defaultList);
    }

    private boolean isInputValid() {
        String longName = longField.getText();
        String shortName = shortField.getText();
        if (longName.isBlank() || (partType.equals("box") && testerType == TesterType.CMU && shortName.isBlank())) {
            window.showNotification(RfNotice.SELECT_PROFILE_PART_MISSING);
            return false;
        }
        if (ValidationHelper.hasForbiddenCharacter(longName) || ValidationHelper.hasForbiddenCharacter(shortName)) {
            window.showNotification(RfNotice.GENERIC_INVALID_CHARACTER);
            return false;
        }
        if (isNameInUse(longField.getText(), shortField.getText())) {
            window.showNotification(RfNotice.SELECT_PROFILE_PART_MATCH);
            return false;
        }
        if (partType.equals("manufacturer") && longName.contains(" ")) {
            window.showNotification(RfNotice.SELECT_PROFILE_PART_WHITESPACE);
            return false;
        }
        return true;
    }

    public void addPart() {
        if (isInputValid()) {
            saveButton.setEnabled(true);
            if (partType.equals("box")) {
                Contraction contraction = new Contraction(longField.getText(), shortField.getText());
                partList.addElement(0, contraction);
                parts.addEmptyBox(contraction);
            } else {
                partList.addElement(0, longField.getText());
                switch (partType) {
                    case "position":
                        parts.addPosition(selectedBox, longField.getText());
                        break;
                    case "script":
                        parts.addScript(longField.getText());
                        break;
                    case "manufacturer":
                        parts.addManufacturer(longField.getText());
                        break;
                }
            }
        }
    }

    private boolean isNameInUse(String name, String shortName) {
        for (Object part : partList.getModelAsArrayList()) {
            if (!partType.equals("box")) {
                if (part.equals(name)) {
                    return true;
                }
            } else if (((Contraction) part).getName().equals(name) || (testerType == TesterType.CMU && ((Contraction) part).getShortName().equals(shortName))) {
                return true;
            }
        }
        return false;
    }

    private boolean partNotInUse() {
        if (testerType == TesterType.CMU) {
            for (Shortcut shortcut : shortcuts) {
                switch (partType) {
                    case "position":
                        if (shortcut.getPosition().equals(selectedPart) && shortcut.getBox().getName().equals(selectedBox)) {
                            return false;
                        }
                        break;
                    case "script":
                        if (shortcut.getScript().equals(selectedPart) || selectedPart.equals("UNKNOWN")) {
                            return false;
                        }
                        break;
                    case "manufacturer":
                        if (shortcut.getManufacturer().equals(selectedPart)) {
                            return false;
                        }
                        break;
                    case "box":
                        if (shortcut.getBox().getName().equals(((Contraction) selectedPart).getName())) {
                            return false;
                        }
                        break;
                }
            }
        } else {
            for (Database database : databases) {
                for (Profile profile : database.getProfiles()) {
                    switch (partType) {
                        case "manufacturer":
                            if (profile.getManufacturer().equals(selectedPart)) {
                                return false;
                            }
                            break;
                        case "box":
                            if (profile.getBox().equals(((Contraction) selectedPart).getName())) {
                                return false;
                            }
                            break;
                        case "position":
                            if (profile.getBox().equals(selectedBox) && profile.getPosition().equals(selectedPart)) {
                                return false;
                            }
                            break;
                    }
                }
            }
        }
        return true;
    }

    public void deletePart() {
        if (partNotInUse()) {
            saveButton.setEnabled(true);
            deleteButton.setEnabled(false);
            longField.setText("");
            shortField.setText("");
            switch (partType) {
                case "box":
                    parts.removeBox(selectedPart.toString());
                    break;
                case "manufacturer":
                    parts.removeManufacturer(selectedPart.toString());
                    break;
                case "position":
                    parts.removePosition(selectedBox, selectedPart.toString());
                    break;
                case "script":
                    parts.removeScript(selectedPart.toString());
                    break;
            }
        } else {
            window.showNotification(RfNotice.SELECT_PROFILE_PART_IN_USE);
        }
    }

    public void openPart(String partType, String selectedBox, TesterType testerType) {
        this.testerType = testerType;
        this.selectedBox = selectedBox;
        defaultList = new ArrayList<>();
        if (testerType == TesterType.CMU) {
            shortcuts = assistant.readShortcuts();
        } else {
            databases = assistant.readDatabases(TesterType.CMW);
        }
        parts = assistant.readProfileParts(testerType);
        this.partType = partType;
        selectedPart = null;
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        shortField.setText("");
        longField.setText("");
        shortLabel.setText("");
        placer.hideComponent("SelectProfilePart shortField");
        if (!partType.equals("box")) {
            shortLabel.setText("");
            placer.hideComponent("SelectProfilePart shortField");
            String[] elements;
            if (partType.equals("position")) {
                titleLabel.setText("Elérhető pozíciók");
                elements = parts.getPositions(selectedBox);
            } else if (partType.equals("script")) {
                titleLabel.setText("Elérhető RF scriptek");
                elements = parts.getScripts();
            } else {
                titleLabel.setText("Elérhető gyártók");
                elements = parts.getManufacturers();
            }
            partList.changeModel(Arrays.asList(elements), false);
            defaultList = Arrays.asList(elements);
        } else {
            titleLabel.setText("Elérhető Shield Box-ok");
            if (testerType == TesterType.CMU) {
                shortLabel.setText("Rövidítés:");
                placer.showComponent("SelectProfilePart shortField");
            }
            partList.changeModel(parts.getBoxes(), false);
            defaultList = parts.getBoxes().stream().map(object -> ((Contraction) object).getName()).collect(Collectors.toList());
        }
    }
}
