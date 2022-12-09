package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.RfNotice;
import hu.open.assistant.rf.graphical.renderer.BackupListRenderer;
import hu.open.assistant.rf.graphical.renderer.ProfileListRenderer;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.commons.graphical.notification.AssNotification;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.database.backup.DatabaseBackup;
import hu.open.assistant.rf.model.profile.Profile;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for viewing and managing database backups. Two lists are filled with available CMU and CMW type database backups
 * based on the database log (backup files content is loaded only on selection). When selecting a backup in the list
 * the text area shows the backups content (database information) and a third list is filled with the RF profiles
 * contained in the database. Selecting a profile shows the profile information in the text area. There are options to
 * create a CMU or CMW type backup, delete an existing one or to restore its content.
 */
public class SelectBackup extends RfPanel {

    private static final Dimension LABEL_DIMENSION = new Dimension(300, 50);
    private static final Dimension BACKUP_LIST_DIMENSION = new Dimension(300, 120);
    private static final Dimension PROFILE_LIST_DIMENSION = new Dimension(300, 560);
    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 200);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssList<DatabaseBackup> cmuBackupList;
    private final AssList<DatabaseBackup> cmwBackupList;
    private final AssTextArea infoTextArea;
    private final AssList<Profile> profileList;
    private final AssButton deleteButton;
    private final AssButton restoreButton;
    private DatabaseBackup selectedBackup;

    public SelectBackup(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectBackup");
        //placer.enableDebug();
        AssLabel titleLabel = new AssLabel("Biztonsági mentések", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        AssLabel cmuBackupLabel = new AssLabel("CMU 200 mentések", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel cmwBackupLabel = new AssLabel("CMW 290 mentések", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel profileLabel = new AssLabel("RF profilok: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        cmuBackupList = new AssList<>("SelectBackup cmuBackupList", SMALL_TEXT_SIZE, BACKUP_LIST_DIMENSION, listener, new BackupListRenderer());
        cmuBackupList.enableMouseListening();
        cmwBackupList = new AssList<>("SelectBackup cmwBackupList", SMALL_TEXT_SIZE, BACKUP_LIST_DIMENSION, listener, new BackupListRenderer());
        cmwBackupList.enableMouseListening();
        profileList = new AssList<>("SelectBackup profileList", SIDE_BUTTON_TEXT_SIZE, PROFILE_LIST_DIMENSION, listener, new ProfileListRenderer());
        infoTextArea = new AssTextArea("SelectBackup infoTextArea", SMALL_TEXT_SIZE, TEXT_AREA_DIMENSION, true);
        AssButton cmuBackupButton = new AssButton("SelectBackup cmuBackupButton", "CMU mentés készítése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        AssButton cmwBackupButton = new AssButton("SelectBackup cmwBackupButton", "CMW mentés készítése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        deleteButton = new AssButton("SelectBackup deleteButton", "Mentés törlése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        restoreButton = new AssButton("SelectBackup restoreButton", "Mentés visszaállítása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("SelectBackup backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(cmuBackupButton, 4, 1, 1, 1);
        placer.addComponent(cmuBackupLabel, 1, 2, 1, 1);
        placer.addComponent(profileLabel, 2, 2, 2, 1);
        placer.addComponent(cmwBackupButton, 4, 2, 1, 1);
        placer.addComponent(cmuBackupList, 1, 3, 1, 2);
        placer.addComponent(profileList, 2, 3, 2, 8);
        placer.addComponent(deleteButton, 4, 3, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addComponent(cmwBackupLabel, 1, 5, 1, 1);
        placer.addComponent(restoreButton, 4, 5, 1, 1);
        placer.addComponent(cmwBackupList, 1, 6, 1, 2);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addComponent(infoTextArea, 1, 8, 1, 3);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public void deleteBackup() {
        assistant.deleteBackup(selectedBackup);
        window.showNotification(RfNotice.SELECT_BACKUP_DELETED);
    }

    public String[] getCmuFilterOptions() {
        return assistant.readDatabaseNames(TesterType.CMU).toArray(new String[0]);
    }

    public String[] getCmwFilterOptions() {
        return assistant.readDatabaseNames(TesterType.CMW).toArray(new String[0]);
    }

    public void createBackup(String selectedOption) {
        placer.disableComponents();
        TesterType testerType = TesterType.getByName(selectedOption.split(" ")[0].toLowerCase());
        int serial = Integer.parseInt(selectedOption.split(" ")[1]);
        List<Database> databases = assistant.readDatabases(testerType);
        databases.stream().filter(database -> database.getSerial() == serial).findAny()
                .ifPresent(databaseToBackup -> assistant.createDatabaseBackup(databaseToBackup));
        placer.enableComponents();
        listener.actionPerformed(new ActionEvent(this, 0, "backup_create_done"));
    }

    public void restoreBackup() {
        placer.disableComponents();
        window.showNotification(RfNotice.SELECT_BACKUP_RESTORE);
        AssNotification notification = ((RfWindow) window).getNotification();
        boolean success = assistant.restoreDatabaseBackup(selectedBackup);
        notification.dispose();
        placer.enableComponents();
        if (success) {
            listener.actionPerformed(new ActionEvent(this, 0, "backup_restore_done"));
        } else {
            listener.actionPerformed(new ActionEvent(this, 0, "backup_restore_failed"));
        }
    }

    public void updateBackups() {
        profileList.clearModel();
        infoTextArea.setText("");
        List<DatabaseBackup> cmuBackups = new ArrayList<>();
        List<DatabaseBackup> cmwBackups = new ArrayList<>();
        List<DatabaseBackup> backups = assistant.readDatabaseBackupHeaders();
        for (DatabaseBackup backup : backups) {
            if (backup.getTesterType() == TesterType.CMU) {
                cmuBackups.add(backup);
            } else {
                cmwBackups.add(backup);
            }
        }
        cmuBackupList.changeModel(cmuBackups, false);
        cmwBackupList.changeModel(cmwBackups, false);
        restoreButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public void setSelectedBackup(TesterType testerType) {
        if (testerType == TesterType.CMU) {
            selectedBackup = cmuBackupList.getSelectedValue();
            cmwBackupList.clearSelection();
        } else {
            selectedBackup = cmwBackupList.getSelectedValue();
            cmuBackupList.clearSelection();
        }
        if (selectedBackup.getDatabase() == null) {
            assistant.readDatabaseBackupContent(selectedBackup);
        }
        infoTextArea.setText(TextHelper.stringListToLineBrokenString(selectedBackup.getInfo()));
        infoTextArea.setCaretPosition(0);
        profileList.changeModel(selectedBackup.getDatabase().getProfiles(), false);
        restoreButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    public void setSelectedProfile() {
        infoTextArea.setText(TextHelper.stringListToLineBrokenString(profileList.getSelectedValue().getInfo()));
        infoTextArea.setCaretPosition(0);
        restoreButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
}