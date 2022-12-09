package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.RfNotice;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.commons.util.ValidationHelper;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.Profile;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

/**
 * GUI for editing TAC numbers associated with a CMW type RF profile. It displays the associated TAC numbers in a list
 * and allows adding and deleting. Only unique TAC numbers can be added and the TAC used for storage can't be deleted.
 */
public class SelectTac extends RfPanel {

    private static final Dimension SMALL_LABEL_DIMENSION = new Dimension(200, 50);
    private static final Dimension LARGE_LABEL_DIMENSION = new Dimension(400, 50);
    private static final Dimension TAC_LIST_DIMENSION = new Dimension(600, 420);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int MEDIUM_TEXT_SIZE = 18;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssButton deleteButton;
    private final AssButton fixButton;
    private final AssLabel profileContentLabel;
    private final AssLabel storeContentLabel;
    private final AssTextField tacField;
    private final AssList<Long> tacList;
    private List<Long> defaultList;
    private long selectedTac;
    private long storeTac;

    public SelectTac(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectTac");
        //placer.enableDebug();
        tacList = new AssList<>("SelectTac tacList", SMALL_TEXT_SIZE, TAC_LIST_DIMENSION, listener, null);
        AssLabel titleLabel = new AssLabel("Profilhoz társított TAC számok", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        AssLabel profileLabel = new AssLabel("Profil:", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
        AssLabel storeLabel = new AssLabel("Tároló TAC:", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
        AssLabel tacLabel = new AssLabel("TAC szám: ", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
        profileContentLabel = new AssLabel("", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
        storeContentLabel = new AssLabel("", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
        tacField = new AssTextField("SelectTac tacField", LARGE_LABEL_DIMENSION, MEDIUM_TEXT_SIZE, listener, "", true);
        AssButton newButton = new AssButton("SelectTac newButton", "Új", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        deleteButton = new AssButton("SelectTac deleteButton", "Törlés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("SelectTac backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        fixButton = new AssButton("SelectTac fixButton", "Módosít", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(newButton, 4, 1, 1, 1);
        placer.addComponent(profileLabel, 1, 2, 1, 1);
        placer.addComponent(profileContentLabel, 2, 2, 2, 1);
        placer.addComponent(deleteButton, 4, 2, 1, 1);
        placer.addComponent(storeLabel, 1, 3, 1, 1);
        placer.addComponent(storeContentLabel, 2, 3, 2, 1);
        placer.addComponent(fixButton, 4, 3, 1, 1);
        placer.addComponent(tacLabel, 1, 4, 1, 1);
        placer.addComponent(tacField, 2, 4, 2, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addComponent(tacList, 1, 5, 3, 6);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    private String getProfileNameWithSameTac(long tac, String name) {
        for (Database database : assistant.readDatabases(TesterType.CMW)) {
            for (Profile profile : database.getProfiles()) {
                if (!profile.getName().equals(name)) {
                    if (((CmwProfile) profile).getTacList().contains(tac)) {
                        return profile.getName();
                    }
                }
            }
        }
        return null;
    }

    private boolean isInputValid() {
        String tacText = tacField.getText();
        if (tacText.isBlank()) {
            window.showNotification(RfNotice.SELECT_TAC_INPUT_EMPTY);
            return false;
        }
        if (ValidationHelper.isInvalidLong(tacText)) {
            window.showNotification(RfNotice.SELECT_TAC_FORMAT);
            return false;
        }
        if (tacText.length() != 8) {
            window.showNotification(RfNotice.SELECT_TAC_NUMBER_LENGTH);
            return false;
        }
        long tac = Long.parseLong(tacText);
        if (tac <= 0) {
            window.showNotification(RfNotice.SELECT_TAC_FORMAT);
            return false;
        }
        if (tacList.getModelAsArrayList().contains(tac)) {
            window.showNotification(RfNotice.SELECT_TAC_MATCH);
            return false;
        }
        String matchingProfileName = getProfileNameWithSameTac(tac, profileContentLabel.getText());
        if (matchingProfileName != null) {
            window.showNotification(RfNotice.EMPTY);
            window.changeNotificationText("A TAC más profilnál használatban!\nÉrintett profil: " + matchingProfileName);
            return false;
        }
        return true;
    }

    public void addTac() {
        if (isInputValid()) {
            tacList.addElement(0, Long.parseLong(tacField.getText()));
            fixButton.setEnabled(true);
        }
    }

    public boolean isModified() {
        List<Long> tacList = getTacList();
        Collections.sort(tacList);
        Collections.sort(defaultList);
        return !tacList.equals(defaultList);
    }

    public List<Long> getTacList() {
        return tacList.getModelAsArrayList();
    }

    public void setSelectedTac() {
        selectedTac = tacList.getSelectedValue();
        tacField.setText(String.valueOf(selectedTac));
        deleteButton.setEnabled(true);
    }

    public void deleteTac() {
        if (storeTac != selectedTac) {
            tacList.removeElement(selectedTac);
            fixButton.setEnabled(true);
            deleteButton.setEnabled(false);
            tacField.setText("");
        } else {
            window.showNotification(RfNotice.SELECT_TAC_STORE_TAC);
        }
    }

    public void openTacList(List<Long> tacs, long storeTac, String name) {
        this.storeTac = storeTac;
        defaultList = tacs;
        tacField.setText("");
        tacList.changeModel(tacs, false);
        profileContentLabel.setText(name);
        storeContentLabel.setText("" + storeTac);
        deleteButton.setEnabled(false);
        fixButton.setEnabled(false);
    }
}
