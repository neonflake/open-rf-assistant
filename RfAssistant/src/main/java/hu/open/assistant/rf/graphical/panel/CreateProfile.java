package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssComboBox;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.rf.Config;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.filter.ShortcutFilter;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.ShieldBox;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.log.ProfileLog;
import hu.open.assistant.rf.model.log.batch.ProfileLogBatch;
import hu.open.assistant.rf.model.log.event.ProfileLogEvent;
import hu.open.assistant.rf.model.profile.CmuProfile;
import hu.open.assistant.rf.model.profile.values.CmuProfileValues;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.values.CmwProfileValues;
import hu.open.assistant.rf.graphical.RfNotice;

import java.awt.Dimension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for creating and modifying RF profiles. Depending on the profile type (CMU or CMW) different fields are available.
 * On creation drop down boxes are set to the default state and on modification some of them are disabled and are set
 * according to the created profile. For CMU profiles the profiles script can be modified without affecting the
 * attenuation database (only the shortcut is modified). Changing the profiles position resets the profiles values
 * (same as deletion and creation). There is navigation option to quickly edit the profile parts listed in the dropdown
 * boxes.
 */
public class CreateProfile extends RfPanel {

    private static final Dimension LABEL_DIMENSION = new Dimension(200, 50);
    private static final Dimension BOX_AND_FIELD_DIMENSION = new Dimension(400, 50);
    private static final int SMALL_TEXT_SIZE = 16;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssButton saveButton;
    private final AssButton manufacturerButton;
    private final AssButton boxButton;

    private final AssButton positionButton;
    private final AssButton backButton;
    private final AssButton scriptButton;
    private final AssTextField typeField;
    private final AssTextField commentField;
    private final AssTextField tacField;
    private final String[] selections;
    private final String[] defaultSelections;
    private final Config config;
    private final AssLabel scriptLabel;
    private final AssLabel titleLabel;
    private AssComboBox manufacturerBox;
    private AssComboBox boxBox;
    private AssComboBox scriptBox;
    private AssComboBox positionBox;
    private ProfileParts parts;
    private List<Database> databases;
    private Database targetDatabase;
    private boolean editMode;

    public CreateProfile(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "CreateProfile");
        //placer.enableDebug();
        titleLabel = new AssLabel("Profil létrehozása", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        AssLabel typeLabel = new AssLabel("Típus:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel manufacturerLabel = new AssLabel("Gyártó: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel boxLabel = new AssLabel("Shield Box:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel positionLabel = new AssLabel("Pozíció:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        scriptLabel = new AssLabel("", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel commentLabel = new AssLabel("Megjegyzés: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        typeField = new AssTextField("CreateProfile typeField", BOX_AND_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
        commentField = new AssTextField("CreateProfile typeField", BOX_AND_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
        tacField = new AssTextField("CreateProfile tacField", BOX_AND_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
        manufacturerBox = new AssComboBox("CreateProfile manufacturerBox", new String[0], SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
        boxBox = new AssComboBox("CreateProfile boxBox", new String[0], SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
        scriptBox = new AssComboBox("CreateProfile scriptBox", new String[0], SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
        saveButton = new AssButton("CreateProfile saveButton", "Létrehozás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        manufacturerButton = new AssButton("CreateProfile manufacturerButton", "Gyártók", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        boxButton = new AssButton("CreateProfile boxButton", "Shield Box-ok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        positionButton = new AssButton("CreateProfile positionButton", "Pozíciók", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        scriptButton = new AssButton("CreateProfile scriptButton", "Scriptek", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        backButton = new AssButton("CreateProfile backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(saveButton, 4, 1, 1, 1);
        placer.addComponent(typeLabel, 1, 2, 1, 1);
        placer.addComponent(typeField, 2, 2, 2, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 2, 1, 1);
        placer.addComponent(manufacturerLabel, 1, 3, 1, 1);
        placer.addComponent(manufacturerBox, 2, 3, 2, 1);
        placer.addComponent(manufacturerButton, 4, 3, 1, 1);
        placer.addComponent(boxLabel, 1, 4, 1, 1);
        placer.addComponent(boxBox, 2, 4, 2, 1);
        placer.addComponent(boxButton, 4, 4, 1, 1);
        placer.addComponent(positionLabel, 1, 5, 1, 1);
        placer.addComponent(positionButton, 4, 5, 1, 1);
        placer.addComponent(scriptLabel, 1, 6, 1, 1);
        placer.addComponent(scriptButton, 4, 6, 1, 1);
        placer.addComponent(commentLabel, 1, 7, 1, 1);
        placer.addComponent(commentField, 2, 7, 2, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
        selections = new String[7];
        defaultSelections = new String[5];
        config = assistant.getGlobalConfig();
    }

    private void initSelections() {
        selections[0] = "";
        selections[1] = config.getDefaultManufacturer();
        if (targetDatabase.getTesterType() == TesterType.CMU) {
            selections[2] = "";
            selections[3] = config.getDefaultCmuBox();
            selections[4] = config.getDefaultCmuPosition();
            selections[5] = config.getDefaultCmuScript();
        } else {
            selections[3] = config.getDefaultCmwBox();
            selections[4] = config.getDefaultCmwPosition();
        }
        selections[6] = "";
    }

    public String getSelectedBox(){
        return((String)boxBox.getSelectedItem());
    }

    public void updatePositionBox() {
        String box = getSelectedBox();
        if (box != null) {
            if (targetDatabase.getTesterType() == TesterType.CMU) {
                if (box.equals("CMU-Z11")) {
                    scriptBox.setSelectedItem(config.getDefaultCmuScript());
                } else if (box.equals("Aeroflex_4933")) {
                    scriptBox.setSelectedItem(config.getDefaultAeroflexScript());
                }
            }
        }
        if (positionBox != null) {
            placer.removeComponent(positionBox.getName());
        }
        positionBox = new AssComboBox("CreateProfile positionBox", parts.getPositions(box), SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
        if (!selections[4].isBlank()) {
            positionBox.setSelectedItem(selections[4]);
        }
        placer.addComponent(positionBox, 2, 5, 2, 1);
        this.revalidate();
        this.repaint();
    }

    public void checkSaveButton() {
        if (editMode) {
            saveButton.setEnabled(isModified());
        }
    }

    public boolean isInputValid() {
        String manufacturer = manufacturerBox.getSelectedItemAsString();
        String box = boxBox.getSelectedItemAsString();
        String position = positionBox.getSelectedItemAsString();
        String type = typeField.getText();
        String name = manufacturer + " " + type;
        if (type.isBlank()) {
            window.showNotification(RfNotice.CREATE_PROFILE_TYPE_MISSING);
            return false;
        }
        if (assistant.hasInvalidSeparatorCharacter(type)) {
            window.showNotification(RfNotice.CREATE_PROFILE_UNDERSCORE);
            return false;
        }
        if (ValidationHelper.hasForbiddenCharacter(type) || ValidationHelper.hasForbiddenCharacter(commentField.getText())) {
            window.showNotification(RfNotice.GENERIC_INVALID_CHARACTER);
            return false;
        }
        if (manufacturer.isEmpty() || box.isEmpty() || position.isEmpty() ||
                (targetDatabase.getTesterType() == TesterType.CMU && scriptBox.getSelectedItemAsString().isEmpty())) {
            window.showNotification(RfNotice.CREATE_PROFILE_SELECTION_MISSING);
            return false;
        }
        boolean profileNameMatched = assistant.isProfileNameMatched(databases, name);
        if (!editMode && profileNameMatched) {
            window.showNotification(RfNotice.CREATE_PROFILE_PROFILE_MATCH);
            return false;
        }
        if (targetDatabase.getTesterType() == TesterType.CMW) {
            String tacText = tacField.getText();
            if (tacText.isBlank()) {
                window.showNotification(RfNotice.CREATE_PROFILE_TAC_MISSING);
                return false;
            }
            if (ValidationHelper.isInvalidLong(tacText)) {
                window.showNotification(RfNotice.CREATE_PROFILE_TAC_FORMAT);
                return false;
            }
            if (tacText.length() != 8) {
                window.showNotification(RfNotice.CREATE_PROFILE_TAC_LENGTH);
                return false;
            }
            Long tac = tacField.getTextAsLong();
            Profile profileWithMatchingTac = databases.stream().flatMap(database -> database.getProfiles().stream())
                    .filter(profile -> !profile.getName().equals(name) && ((CmwProfile) profile).getTacList().contains(tac))
                    .findAny().orElse(null);
            if (profileWithMatchingTac != null) {
                window.showNotification(RfNotice.EMPTY);
                window.changeNotificationText("A TAC más profilnál használatban!\nÉrintett profil: " + profileWithMatchingTac.getName());
                return false;
            }
        }
        return true;
    }

    public boolean createProfile() {
        enableControls(false);
        if (isInputValid()) {
            String type = typeField.getText();
            String manufacturer = manufacturerBox.getSelectedItemAsString();
            String box = boxBox.getSelectedItemAsString();
            String position = positionBox.getSelectedItemAsString();
            Profile profile;
            if (targetDatabase.getTesterType() == TesterType.CMU) {
                profile = new CmuProfile(targetDatabase.getSerial(), type, manufacturer, box, position, new CmuProfileValues(), config.getDefaultValue());
                List<Shortcut> shortcuts = assistant.readShortcuts();
                Shortcut shortcut = ShortcutFilter.getShortcutByName(shortcuts, profile.getName());
                if (editMode) {
                    shortcuts.remove(shortcut);
                    shortcut = null;
                }
                if (shortcut == null) {
                    shortcuts.add(new Shortcut(1, manufacturer, type, new ShieldBox(new Contraction(box, assistant.getCmuProfileParts().longToShortBox(box))), position, scriptBox.getSelectedItemAsString()));
                    assistant.writeShortcuts(shortcuts);
                }
            } else {
                List<Long> tacList = new ArrayList<>();
                long storeTac = tacField.getTextAsLong();
                tacList.add(storeTac);
                profile = new CmwProfile(targetDatabase.getSerial(), type, manufacturer, box, position, new CmwProfileValues(), config.getDefaultValue(), storeTac, tacList);
            }
            boolean positionChanged = !positionBox.getSelectedItemAsString().equals(defaultSelections[3]);
            if (!editMode || positionChanged) {
                profile.createCenterCompensation();
                profile.setCreated(true);
                ProfileLogBatch logBatch = new ProfileLogBatch(profile.getSerial(), profile.getName());
                logBatch.addLog(new ProfileLog(LocalDateTime.now(), ProfileLogEvent.CREATION, profile.getSerial(), profile.getTesterType(),
                        profile.getName(), commentField.getText()));
                profile.setLogBatch(logBatch);
                profile.addCompensation(profile.getCompensation());
                if (editMode) {
                    Profile oldProfile = targetDatabase.getProfileByName(profile.getName());
                    targetDatabase.removeProfile(oldProfile);
                    if (oldProfile.getTesterType() == TesterType.CMW) {
                        assistant.deleteProfileData((CmwProfile) oldProfile);
                    }
                }
                assistant.addEquipmentData(profile);
                targetDatabase.addProfile(profile);
                targetDatabase.setProfileCreated(true);
            }
            assistant.writeDatabases(databases);
            window.showNotification(RfNotice.CREATE_PROFILE_SAVE_SUCCESS);
            return true;
        }
        enableControls(true);
        if (editMode) {
            disableNonEditControls();
        }
        return false;
    }


    public void clearFields(List<Database> databases, Database targetDatabase, Profile profile) {
        this.targetDatabase = targetDatabase;
        this.databases = databases;
        editMode = false;
        typeField.setText("");
        commentField.setText("");
        tacField.setText("");
        initSelections();
        updateControls();
        enableControls(true);
        if (profile != null) {
            if (targetDatabase.getTesterType() == TesterType.CMU) {
                titleLabel.setText("Pozíció és script módosítása");
            } else {
                titleLabel.setText("Pozíció módosítása");
            }
            editMode = true;
            saveButton.setText("Módosítás");
            typeField.setText(profile.getType());
            manufacturerBox.setSelectedItem(profile.getManufacturer());
            boxBox.setSelectedItem(profile.getBox());
            updatePositionBox();
            positionBox.setSelectedItem(profile.getPosition());
            boolean profileSynced = isProfileSynced(profile.getName());
            positionBox.setEnabled(!profileSynced);
            positionButton.setEnabled(!profileSynced);
            if (profile.getTesterType() == TesterType.CMU) {
                Shortcut shortcut = ShortcutFilter.getShortcutByName(assistant.readShortcuts(), profile.getName());
                if (shortcut != null) {
                    scriptBox.setSelectedItem(shortcut.getScript());
                } else {
                    scriptBox.setSelectedItem("UNKNOWN");
                }
            } else {
                tacField.setText((String.valueOf(((CmwProfile) profile).getStoreTac())));
                tacField.setEnabled(!profileSynced);
            }
            commentField.setEnabled(!profileSynced);
            disableNonEditControls();
            defaultSelections[3] = (String) positionBox.getSelectedItem();
            defaultSelections[4] = (String) scriptBox.getSelectedItem();
            checkSaveButton();
        } else {
            titleLabel.setText("Profil létrehozás");
            saveButton.setText("Létrehozás");
        }
    }

    private boolean isProfileSynced(String name) {
        int count = 0;
        for (Database database : databases) {
            if (database.getProfileByName(name) != null) {
                count++;
            }
        }
        return count > 1;
    }

    private void disableNonEditControls() {
        manufacturerButton.setEnabled(false);
        boxButton.setEnabled(false);
        manufacturerBox.setEnabled(false);
        boxBox.setEnabled(false);
        typeField.setEnabled(false);
    }

    private void enableControls(boolean enable) {
        saveButton.setEnabled(enable);
        manufacturerButton.setEnabled(enable);
        boxButton.setEnabled(enable);
        positionButton.setEnabled(enable);
        backButton.setEnabled(enable);
        manufacturerBox.setEnabled(enable);
        boxBox.setEnabled(enable);
        positionBox.setEnabled(enable);
        typeField.setEnabled(enable);
        commentField.setEnabled(enable);
        if (targetDatabase.getTesterType() == TesterType.CMU) {
            scriptButton.setEnabled(enable);
            scriptBox.setEnabled(enable);
        } else {
            tacField.setEnabled(enable);
        }
    }

    public boolean isModified() {
        if (!editMode) {
            return !typeField.getText().isBlank() || !commentField.getText().isBlank() ||
                    !manufacturerBox.getSelectedItemAsString().equals(defaultSelections[0]) ||
                    !boxBox.getSelectedItemAsString().equals(defaultSelections[2]) ||
                    (positionBox.getSelectedItem() != null && !positionBox.getSelectedItem().equals(defaultSelections[3])) ||
                    (targetDatabase.getTesterType() == TesterType.CMU && !scriptBox.getSelectedItemAsString().equals(defaultSelections[4])) ||
                    (targetDatabase.getTesterType() == TesterType.CMW && !tacField.getText().isBlank());
        } else {
            return !commentField.getText().isBlank() ||
                    (positionBox.getSelectedItem() != null && !positionBox.getSelectedItem().equals(defaultSelections[3])) ||
                    (targetDatabase.getTesterType() == TesterType.CMU && !scriptBox.getSelectedItemAsString().equals(defaultSelections[4]));

        }
    }

    public void saveSelections() {
        selections[0] = typeField.getText();
        selections[1] = (String)manufacturerBox.getSelectedItem();
        selections[3] = (String)boxBox.getSelectedItem();
        selections[4] = (String) positionBox.getSelectedItem();
        selections[5] = (String) scriptBox.getSelectedItem();
        selections[6] = commentField.getText();
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] == null) {
                selections[i] = "";
            }
        }
    }

    public TesterType getTesterType() {
        return targetDatabase.getTesterType();
    }

    public void updateControls() {
        typeField.setText(selections[0]);
        commentField.setText(selections[6]);
        placer.removeComponent(manufacturerBox.getName());
        placer.removeComponent(boxBox.getName());
        placer.removeComponent(scriptBox.getName());
        placer.removeComponent(tacField.getName());
        parts = assistant.readProfileParts(targetDatabase.getTesterType());
        manufacturerBox = new AssComboBox("CreateProfile manufacturerBox", parts.getManufacturers(), SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
        if (!selections[1].isBlank()) {
            manufacturerBox.setSelectedItem(selections[1]);
        }
        boxBox = new AssComboBox("CreateProfile boxBox", parts.getStringBoxes(), SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
        if (!selections[3].isBlank()) {
            boxBox.setSelectedItem(selections[3]);
        }
        if (targetDatabase.getTesterType() == TesterType.CMU) {
            placer.showComponent("CreateProfile scriptButton");
            scriptLabel.setText("Script:");
            scriptBox = new AssComboBox("CreateProfile scriptBox", parts.getScripts(), SMALL_TEXT_SIZE, BOX_AND_FIELD_DIMENSION, listener);
            placer.addComponent(scriptBox, 2, 6, 2, 1);
            if (!selections[5].isBlank()) {
                scriptBox.setSelectedItem(selections[5]);
            }
            defaultSelections[4] = (String) scriptBox.getSelectedItem();
        } else {
            placer.hideComponent("CreateProfile scriptButton");
            scriptLabel.setText("Tároló TAC:");
            placer.addComponent(tacField, 2, 6, 2, 1);
        }
        placer.addComponent(manufacturerBox, 2, 3, 2, 1);
        placer.addComponent(boxBox, 2, 4, 2, 1);
        updatePositionBox();
        defaultSelections[0] = (String) manufacturerBox.getSelectedItem();
        defaultSelections[2] = (String) boxBox.getSelectedItem();
        defaultSelections[3] = (String) positionBox.getSelectedItem();
        if (editMode) {
            disableNonEditControls();
        }
    }
}
