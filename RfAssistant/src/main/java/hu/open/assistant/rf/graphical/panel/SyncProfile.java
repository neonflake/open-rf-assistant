package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.DatabaseListRenderer;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.filter.ProfileFilter;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.graphical.RfNotice;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for syncing CMU type RF profiles between different stations. Fills the first list with databases and a second one
 * excluding the one selected in the first. By selecting elements in the different lists the direction and the profiles
 * to be synced can be determined. The information box about profiles to be synced is updated when clicking on the list
 * elements. The panel contains logic for checking for missing profiles in a database.
 */
public class SyncProfile extends RfPanel {

    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(600, 340);
    private static final Dimension LABEL_DIMENSION = new Dimension(300, 50);
    private static final Dimension LIST_DIMENSION = new Dimension(300, 120);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssList<Database> sourceDatabaseList;
    private final AssList<Database> targetDatabaseList;
    private final AssTextArea textArea;
    private final AssButton transferButton;
    private final AssButton transferAllButton;
    private List<Database> databases;
    private Database selectedSourceDatabase;
    private Database selectedTargetDatabase;

    public SyncProfile(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SyncProfile");
        //placer.enableDebug();
        AssLabel titleLabel = new AssLabel("Profilok szinkronizálása", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        AssLabel differenceLabel = new AssLabel("Állomásokból hiányzó profilok:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel sourceLabel = new AssLabel("Forrás állomás:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        sourceDatabaseList = new AssList<>("SyncProfile sourceDatabaseList", SMALL_TEXT_SIZE, LIST_DIMENSION, listener, new DatabaseListRenderer());
        textArea = new AssTextArea("SyncProfile textArea", SMALL_TEXT_SIZE, TEXT_AREA_DIMENSION, true);
        transferButton = new AssButton("SyncProfile transferButton", "Hiányzó profilok a célba", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        transferAllButton = new AssButton("SyncProfile transferAllButton", "Hiányzó profilok az összesbe", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("SyncProfile backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        AssLabel targetLabel = new AssLabel("Cél állomás:", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        targetDatabaseList = new AssList<>("SyncProfile targetDatabaseList", SIDE_BUTTON_TEXT_SIZE, LIST_DIMENSION, listener, new DatabaseListRenderer());
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(transferButton, 4, 1, 1, 1);
        placer.addComponent(sourceLabel, 1, 2, 1, 1);
        placer.addComponent(targetLabel, 2, 2, 2, 1);
        placer.addComponent(sourceDatabaseList, 1, 3, 1, 2);
        placer.addComponent(targetDatabaseList, 2, 3, 2, 2);
        placer.addComponent(transferAllButton, 4, 2, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 3, 1, 1);
        placer.addComponent(textArea, 1, 6, 3, 5);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addComponent(differenceLabel, 1, 5, 3, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public void setSourceDatabase() {
        this.selectedSourceDatabase = sourceDatabaseList.getSelectedValue();
        this.selectedTargetDatabase = null;
        updateSource();
        targetDatabaseList.clearSelection();
    }

    public void setTargetDatabase() {
        this.selectedTargetDatabase = targetDatabaseList.getSelectedValue();
        updateTarget();
    }

    public void writeDatabase() {
        List<Profile> missingProfiles = checkMissingProfiles(selectedTargetDatabase);
        for (Profile profile : missingProfiles) {
            selectedTargetDatabase.addProfile(profile);
        }
        selectedTargetDatabase.sortProfiles();
        assistant.setDatabaseCleanup(false);
        assistant.writeDatabase(selectedTargetDatabase);
    }

    public void writeProfilesToTarget() {
        assistant.writeProfileSyncLog(checkMissingProfiles(selectedTargetDatabase), selectedSourceDatabase.getSerial(), selectedTargetDatabase.getSerial());
        writeDatabase();
    }

    public void writeProfilesToAll() {
        for (int i = 0; i < targetDatabaseList.getModelSize(); i++) {
            if (selectedSourceDatabase != selectedTargetDatabase) {
                selectedTargetDatabase = targetDatabaseList.getElement(i);
                assistant.writeProfileSyncLog(checkMissingProfiles(selectedTargetDatabase), selectedSourceDatabase.getSerial(), selectedTargetDatabase.getSerial());
                writeDatabase();
            }
        }
    }

    public void updateSource() {
        textArea.setText("");
        targetDatabaseList.clearModel();
        for (Database database : databases) {
            if (database.getSerial() != selectedSourceDatabase.getSerial()) {
                targetDatabaseList.addElement(database);
            }
        }
        String wholeText = selectedSourceDatabase.getSerial() + " (forrás) " + " -> ";
        for (int i = 0; i < targetDatabaseList.getModelSize(); i++) {
            wholeText = wholeText.concat(" ").concat(String.valueOf(targetDatabaseList.getElement(i).getSerial()));
            if (i < targetDatabaseList.getModelSize() - 1) {
                wholeText = wholeText + ",";
            }
        }
        wholeText = wholeText + " (cél)\n\n";
        transferAllButton.setEnabled(false);
        for (int i = 0; i < targetDatabaseList.getModelSize(); i++) {
            wholeText = wholeText.concat("(").concat(String.valueOf(targetDatabaseList.getElement(i).getSerial()).concat(")\n"));
            List<Profile> missingProfiles = checkMissingProfiles(targetDatabaseList.getElement(i));
            for (Profile profile : missingProfiles) {
                wholeText = wholeText.concat(profile.getName()).concat(" (profil)\n");
            }
            if (missingProfiles.size() == 0) {
                wholeText = wholeText.concat("Nincsenek hiányzó profilok").concat("\n");
            } else {
                transferAllButton.setEnabled(true);
            }
            if (i < targetDatabaseList.getModelSize() - 1) {
                wholeText = wholeText + "\n";
            }
        }
        transferButton.setEnabled(false);
        textArea.setText(wholeText);
        textArea.setCaretPosition(0);
    }

    public void updateTarget() {
        List<Profile> missingProfiles = checkMissingProfiles(selectedTargetDatabase);
        String wholeText = selectedSourceDatabase.getSerial() + " (forrás) " + " -> " + selectedTargetDatabase.getSerial() + " (cél)\n\n";
        transferButton.setEnabled(false);
        for (Profile profile : missingProfiles) {
            wholeText = wholeText.concat(profile.getName()).concat(" (profil)\n");
        }
        if (missingProfiles.size() == 0) {
            wholeText = wholeText + "Nincsenek hiányzó profilok" + "\n";
        } else {
            transferButton.setEnabled(true);
        }
        textArea.setText(wholeText);
        transferAllButton.setEnabled(false);
    }

    public void updateList() {
        targetDatabaseList.clearModel();
        textArea.setText("");
        databases = assistant.readDatabases(TesterType.CMU);
        if (databases.size() == 0) {
            window.showNotification(RfNotice.CMU_PROFILE_ERROR);
        }
        sourceDatabaseList.changeModel(databases, false);
    }

    private List<Profile> checkMissingProfiles(Database targetDatabase) {
        List<Profile> missingProfiles = new ArrayList<>();
        for (Profile profile : selectedSourceDatabase.getProfiles()) {
            if (ProfileFilter.getProfileByName(targetDatabase.getProfiles(), profile.getName()) == null) {
                missingProfiles.add(profile);
            }
        }
        return missingProfiles;
    }
}
