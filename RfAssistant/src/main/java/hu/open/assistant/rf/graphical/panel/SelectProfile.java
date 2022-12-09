package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.DatabaseListRenderer;
import hu.open.assistant.rf.graphical.renderer.ProfileListRenderer;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.graphical.RfNotice;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for viewing and managing RF profiles. The first list is filled with attenuation databases (CMU or CMW type).
 * When selecting a database a summary is shown in the text area and the second list is filled with RF profiles
 * (profile creation option becomes active). When clicking on an RF profile the text area is updated with profile
 * related information and value editing, deleting and modify options become active (TAC edit with CMW profile only).
 * Depending on the profiles log or compensated state of the profile compensation removal and revert options can be
 * available. Once a profile is compensated editing is not available until the compensation is removed from the profile.
 * When the profiles are modified the save button becomes active, which can be used to write the modifications to disk.
 */
public class SelectProfile extends RfPanel {

    private static final Dimension LABEL_DIMENSION = new Dimension(300, 50);
    private static final Dimension DATABASE_LIST_DIMENSION = new Dimension(300, 120);
    private static final Dimension PROFILE_LIST_DIMENSION = new Dimension(300, 560);
    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 420);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssList<Database> databaseList;
    private final AssList<Profile> profileList;
    private final AssTextArea textArea;
    private final AssButton deleteButton;
    private final AssButton editButton;
    private final AssButton saveButton;
    private final AssButton revertOrRemoveButton;
    private final AssButton createButton;
    private final AssButton tacButton;
    private final AssButton repositionButton;
    private final AssLabel stationLabel;
    private Database selectedDatabase;
    private Profile selectedProfile;
    private boolean jumped;
    private TesterType testerType;

    public SelectProfile(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectProfile");
        //placer.enableDebug();
        AssLabel titleLabel = new AssLabel("Profilok áttekintése", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        stationLabel = new AssLabel("", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        databaseList = new AssList<>("SelectProfile databaseList", SMALL_TEXT_SIZE, DATABASE_LIST_DIMENSION, listener, new DatabaseListRenderer());
        databaseList.enableMouseListening();
        textArea = new AssTextArea("Adatbázisok:", SMALL_TEXT_SIZE, TEXT_AREA_DIMENSION, true);
        deleteButton = new AssButton("SelectProfile deleteButton", "Eltávolítás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        editButton = new AssButton("SelectProfile editButton", "Csillapítás szerkesztése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        tacButton = new AssButton("SelectProfile tacButton", "TAC társítások", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        revertOrRemoveButton = new AssButton("SelectProfile revertButton", "Kompenzálás visszavonása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        saveButton = new AssButton("SelectProfile saveButton", "Mentés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton reloadButton = new AssButton("SelectProfile reloadButton", "Adatok frissítése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        createButton = new AssButton("SelectProfile createButton", "Profil létrehozása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        repositionButton = new AssButton("SelectProfile repositionButton", "Profil módosítása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        AssLabel profileLabel = new AssLabel("RF profilok: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        profileList = new AssList<>("SelectProfile profileList", SIDE_BUTTON_TEXT_SIZE, PROFILE_LIST_DIMENSION, listener, new ProfileListRenderer());
        AssButton backButton = new AssButton("SelectProfile backButton", "Vissza ", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(deleteButton, 4, 1, 1, 1);
        placer.addComponent(stationLabel, 1, 2, 1, 1);
        placer.addComponent(profileLabel, 2, 2, 2, 1);
        placer.addComponent(editButton, 4, 2, 1, 1);
        placer.addComponent(databaseList, 1, 3, 1, 2);
        placer.addComponent(profileList, 2, 3, 2, 8);
        placer.addComponent(revertOrRemoveButton, 4, 3, 1, 1);
        placer.addComponent(textArea, 1, 5, 1, 6);
        placer.addComponent(tacButton, 4, 4, 1, 1);
        placer.addComponent(saveButton, 4, 5, 1, 1);
        placer.addComponent(reloadButton, 4, 6, 1, 1);
        placer.addComponent(createButton, 4, 7, 1, 1);
        placer.addComponent(repositionButton, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public boolean isJumped() {
        return jumped;
    }

    public TesterType getTesterType() {
        return testerType;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    public CmwProfile getSelectedCmwProfile() {
        return (CmwProfile) selectedProfile;
    }

    public void initReload() {
        if (testerType == TesterType.CMW) {
            assistant.cmwDatabaseRefresh();
        } else {
            assistant.cmuDatabaseRefresh();
        }
    }

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }

    public List<Database> getDatabases() {
        return databaseList.getModelAsArrayList();
    }

    public String[] getImportOptions() {
        List<String> options = new ArrayList<>();
        for (Profile profile : selectedDatabase.getProfiles()) {
            if (!profile.getName().equals(selectedProfile.getName())) {
                options.add(profile.getName());
            }
        }
        String[] array = new String[options.size()];
        return options.toArray(array);
    }

    public void setSelectedProfile(Profile profile) {
        if (profile == null) {
            selectedProfile = profileList.getSelectedValue();
        } else {
            selectedProfile = profile;
        }
        deleteButton.setEnabled(true);
        repositionButton.setEnabled(true);
        createButton.setEnabled(false);
        if (selectedProfile.getTesterType() == TesterType.CMW) {
            tacButton.setEnabled(true);
        }
        if (selectedProfile.isCompensated()) {
            revertOrRemoveButton.setEnabled(true);
            revertOrRemoveButton.setText("Kompenzálás eltávolítása");
            revertOrRemoveButton.setName("SelectProfile removeButton");
        } else {
            revertOrRemoveButton.setText("Kompenzálás visszavonása");
            revertOrRemoveButton.setName("SelectProfile revertButton");
            revertOrRemoveButton.setEnabled(selectedProfile.isRevertPossible());
        }
        editButton.setEnabled(!selectedProfile.isCompensated());
        updateValues();
    }

    public void setSelectedDatabase(Database database) {
        if (database == null) {
            selectedDatabase = databaseList.getSelectedValue();
        } else {
            selectedDatabase = database;
        }
        deleteButton.setEnabled(false);
        createButton.setEnabled(true);
        revertOrRemoveButton.setEnabled(false);
        editButton.setEnabled(false);
        repositionButton.setEnabled(false);
        tacButton.setEnabled(false);
        updateProfiles();
    }

    public void revertCompensation() {
        selectedProfile.revertCompensation();
        setSelectedProfile(selectedProfile);
        databaseList.repaint();
        profileList.repaint();
        updateSaveButton();
    }

    public void deleteSelectedProfile() {
        selectedDatabase.removeProfile(selectedProfile);
        updateProfiles();
        deleteButton.setEnabled(false);
        updateSaveButton();
    }

    public boolean isDatabaseModified() {
        for (Database database : databaseList.getModelAsArrayList()) {
            if (database.isModified()) {
                return true;
            }
        }
        return false;
    }

    public void updateSaveButton() {
        saveButton.setEnabled(isDatabaseModified());
    }

    protected void jumpToProfile(int serial, String name) {
        setSelectedDatabase(getMatchingDatabase(databaseList.getModelAsArrayList(), serial));
        databaseList.setSelectedValue(selectedDatabase, true);
        setSelectedProfile(selectedDatabase.getProfileByName(name));
        profileList.setSelectedValue(selectedProfile, true);
        profileList.ensureIndexIsVisible(profileList.getSelectedIndex());
    }

    public void saveDatabases() {
        placer.disableComponents();
        assistant.writeDatabases(databaseList.getModelAsArrayList());
        window.showNotification(RfNotice.SELECT_PROFILE_SAVE_SUCCESS);
        placer.enableComponents();
    }

    private Database getMatchingDatabase(List<Database> databases, int serial) {
        for (Database database : databases) {
            if (database.getSerial() == serial) {
                return database;
            }
        }
        return null;
    }

    public void clearLists() {
        stationLabel.setText("Adatbázisok:");
        textArea.setText("");
        databaseList.clearModel();
        profileList.clearModel();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        revertOrRemoveButton.setEnabled(false);
        createButton.setEnabled(false);
        saveButton.setEnabled(false);
        tacButton.setEnabled(false);
        repositionButton.setEnabled(false);
    }

    public void prepareDatabases(TesterType testerType, List<Compensation> compensations, Profile profile) {
        this.testerType = testerType;
        clearLists();
        placer.disableComponents();
        List<Database> databases = assistant.readDatabases(testerType);
        int shortcutCount = assistant.readShortcuts().size();
        for (Database database : databases) {
            database.resetProfiles();
        }
        if (testerType == TesterType.CMU) {
            stationLabel.setText("CMU 200 adatbázisok:");
            if (shortcutCount > 0) {
                databaseList.changeModel(databases, false);
            }
            if (shortcutCount == 0 && databases.size() == 0) {
                window.showNotification(RfNotice.CMU_COMBINED_ERROR);
            } else if (databases.size() == 0) {
                window.showNotification(RfNotice.CMU_PROFILE_ERROR);
            } else if (shortcutCount == 0) {
                window.showNotification(RfNotice.CMU_SCRIPT_ERROR);
            }
        } else {
            stationLabel.setText("CMW 290 adatbázisok:");
            databaseList.changeModel(databases, false);
            if (databases.size() == 0) {
                window.showNotification(RfNotice.CMW_PROFILE_ERROR);
            } else if (!assistant.getNotificationBuffer().isEmpty()) {
                window.showNotification(RfNotice.EMPTY);
                window.changeNotificationText(assistant.getNotificationBuffer());
                saveButton.setEnabled(true);
            }
        }
        if (compensations != null) {
            transferCompensations(compensations);
            jumped = true;
        } else if (profile != null) {
            jumpToProfile(profile.getSerial(), profile.getName());
            jumped = true;
        } else {
            jumped = false;
        }
        placer.enableComponents();
    }

    private void transferCompensations(List<Compensation> compensations) {
        for (Compensation compensation : compensations) {
            for (Database database : databaseList.getModelAsArrayList()) {
                if (compensation.getSerial() == database.getSerial()) {
                    database.getProfileByName(compensation.getName()).addCompensation(compensation);
                }
            }
        }
        saveButton.setEnabled(true);
    }

    private void updateValues() {
        textArea.setText(TextHelper.stringListToLineBrokenString(selectedProfile.getInfo()));
        textArea.setCaretPosition(0);
    }

    public void updateProfiles() {
        textArea.setText(TextHelper.stringListToLineBrokenString(selectedDatabase.getInfo()));
        profileList.changeModel(selectedDatabase.getProfiles(), false);
    }
}
