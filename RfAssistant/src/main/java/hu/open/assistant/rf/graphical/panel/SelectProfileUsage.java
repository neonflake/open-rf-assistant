package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.ProfileUsageListRenderer;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.filter.ShortcutFilter;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.database.Database;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.profile.usage.ProfileUsage;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.report.batch.ReportBatch;
import hu.open.assistant.rf.model.station.Station;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GUI for displaying CMU type RF profile usage statistics. Fills two lists with profile usages (most used
 * profile first) separated by the associated shortcuts list number. The elements between the lists can be moved,
 * effectively changing the list number for the given shortcut. The panel also creates a global statistic for profile
 * usage which is displayed in the text area.
 */
public class SelectProfileUsage extends RfPanel {

    private static final Dimension LABEL_DIMENSION = new Dimension(300, 50);
    private static final Dimension USAGE_LABEL_DIMENSION = new Dimension(650, 50);
    private static final Dimension PROFILE_LIST_DIMENSION = new Dimension(300, 270);
    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(600, 190);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int LARGE_TEXT_SIZE = 20;

    private final AssList<ProfileUsage> primaryProfileList;
    private final AssList<ProfileUsage> legacyProfileList;
    private final AssTextArea textArea;
    private final AssButton primaryButton;
    private final AssButton legacyButton;
    private final AssButton saveButton;
    private List<Shortcut> shortcuts;
    private ProfileUsage selectedProfileUsage;

    public SelectProfileUsage(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectProfileUsage");
        //placer.enableDebug();
        AssLabel titleLabel = new AssLabel("Profil kihasználtság és készülék listák: ", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        AssLabel actualLabel = new AssLabel("Aktuális profil lista: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel legacyLabel = new AssLabel("Elévült profil lista: ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel usageLabel = new AssLabel("Kihasználtság összesítő adatai: ", LARGE_TEXT_SIZE, USAGE_LABEL_DIMENSION);
        primaryProfileList = new AssList<>("SelectProfileUsage primaryProfileList", SMALL_TEXT_SIZE, PROFILE_LIST_DIMENSION, listener, new ProfileUsageListRenderer());
        primaryProfileList.enableMouseListening();
        legacyProfileList = new AssList<>("SelectProfileUsage legacyProfileList", SMALL_TEXT_SIZE, PROFILE_LIST_DIMENSION, listener, new ProfileUsageListRenderer());
        legacyProfileList.enableMouseListening();
        textArea = new AssTextArea("SelectProfileUsage textArea", SMALL_TEXT_SIZE, TEXT_AREA_DIMENSION, true);
        primaryButton = new AssButton("SelectProfileUsage primaryButton", "Aktuális listába", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        legacyButton = new AssButton("SelectProfileUsage legacyButton", "Elévült listába", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("SelectProfileUsage backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        saveButton = new AssButton("SelectProfileUsage saveButton", "Mentés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(primaryButton, 4, 1, 1, 1);
        placer.addComponent(actualLabel, 1, 2, 1, 1);
        placer.addComponent(legacyLabel, 2, 2, 2, 1);
        placer.addComponent(legacyButton, 4, 2, 1, 1);
        placer.addComponent(primaryProfileList, 1, 3, 1, 4);
        placer.addComponent(legacyProfileList, 2, 3, 2, 4);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 3, 1, 1);
        placer.addComponent(saveButton, 4, 4, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addComponent(usageLabel, 1, 7, 3, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addComponent(textArea, 1, 8, 3, 3);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public void moveUnusedProfiles() {
        for (ProfileUsage usage : primaryProfileList.getModelAsArrayList()) {
            Shortcut shortcut = ShortcutFilter.getShortcutByName(shortcuts, usage.getName());
            if (shortcut != null) {
                shortcut.setListNumber(1);
            }
        }
        for (ProfileUsage usage : legacyProfileList.getModelAsArrayList()) {
            Shortcut shortcut = ShortcutFilter.getShortcutByName(shortcuts, usage.getName());
            if (shortcut != null) {
                shortcut.setListNumber(2);
            }
        }
        assistant.writeShortcuts(shortcuts);
    }

    private ProfileUsage getMatchingProfileUsage(List<ProfileUsage> usages, String name) {
        for (ProfileUsage usage : usages) {
            if (usage.getName().equals(name)) {
                return usage;
            }
        }
        return null;
    }

    public void setSelectedProfileUsage(boolean primary) {
        if (primary) {
            selectedProfileUsage = primaryProfileList.getSelectedValue();
            primaryButton.setEnabled(false);
            legacyButton.setEnabled(true);
            legacyProfileList.clearSelection();
        } else {
            selectedProfileUsage = legacyProfileList.getSelectedValue();
            primaryButton.setEnabled(true);
            legacyButton.setEnabled(false);
            primaryProfileList.clearSelection();
        }
    }

    public void moveToPrimary() {
        primaryProfileList.addElement(0, selectedProfileUsage);
        legacyProfileList.removeElement(selectedProfileUsage);
        saveButton.setEnabled(true);
        clearSelection();
    }

    public void moveToLegacy() {
        legacyProfileList.addElement(0, selectedProfileUsage);
        primaryProfileList.removeElement(selectedProfileUsage);
        saveButton.setEnabled(true);
        clearSelection();
    }

    public void clearSelection() {
        primaryButton.setEnabled(false);
        legacyButton.setEnabled(false);
        selectedProfileUsage = null;
    }

    public void updateProfileUsages(List<String> selectedFolders, List<Station> stations) {
        String text;
        clearSelection();
        primaryProfileList.clearModel();
        legacyProfileList.clearModel();
        saveButton.setEnabled(false);
        textArea.setCaretPosition(0);
        shortcuts = assistant.readShortcuts();
        List<ProfileUsage> usages = new ArrayList<>();
        List<Database> databases = assistant.readDatabases(TesterType.CMU);
        Collections.sort(selectedFolders);
        text = "Intervallum: " + cmuFolderToTextDate(selectedFolders.get(0)) + " - " + cmuFolderToTextDate(selectedFolders.get(selectedFolders.size() - 1));
        text = text.concat("\n" + "Napok száma: " + selectedFolders.size());
        List<Profile> profiles = new ArrayList<>();
        for (Database database : databases) {
            for (Profile profile : database.getProfiles()) {
                if (!profiles.contains(profile)) {
                    profiles.add(profile);
                }
            }
        }
        for (Profile profile : profiles) {
            if (getMatchingProfileUsage(usages, profile.getName()) == null) {
                ProfileUsage usage = new ProfileUsage(profile.getType(), profile.getManufacturer());
                usages.add(usage);
            }
        }
        for (Station station : stations) {
            List<ReportBatch> batches = station.getReportBatches();
            for (ReportBatch batch : batches) {
                ProfileUsage usage = getMatchingProfileUsage(usages, batch.getName());
                if (usage != null) {
                    for (Report report : batch.getReports()) {
                        usage.addSample(report.getImei());
                    }
                }
            }
        }
        Collections.sort(usages);
        List<String> manufacturers = new ArrayList<>();
        List<Integer> manufacturerCount = new ArrayList<>();
        int unitCount = 0;
        int frequentCount = 0;
        int rareCount = 0;
        int unusedCount = 0;
        for (ProfileUsage usage : usages) {
            String manufacturer = usage.getManufacturer();
            if (manufacturers.contains(manufacturer)) {
                int count = manufacturerCount.get(manufacturers.indexOf(manufacturer));
                manufacturerCount.set(manufacturers.indexOf(manufacturer), count + usage.getUnitCount());
            } else {
                manufacturers.add(usage.getManufacturer());
                manufacturerCount.add(usage.getUnitCount());
            }
            int count = usage.getUnitCount();
            if (count >= 5) {
                frequentCount++;
            } else if (count > 0) {
                rareCount++;
            } else {
                unusedCount++;
            }
            unitCount = unitCount + count;
            Shortcut shortcut = ShortcutFilter.getShortcutByName(shortcuts, usage.getName());
            if (shortcut != null) {
                if (shortcut.getListNumber() == 1) {
                    primaryProfileList.addElement(usage);
                } else {
                    legacyProfileList.addElement(usage);
                }
            }
        }
        text = text.concat("\n" + "\n" + "RF (2G/3G) tesztelt készülék: " + unitCount);
        text = text.concat("\n" + "Napi átlag: " + Math.round((double) unitCount / selectedFolders.size()));
        text = text.concat("\n" + "\n" + "Gyártó szerinti bontás: ");
        for (int i = 0; i < manufacturers.size(); i++) {
            if (manufacturerCount.get(i) > 0) {
                text = text.concat("\n" + manufacturers.get(i) + " : " + manufacturerCount.get(i));
            }
        }
        text = text.concat("\n" + "\n" + "Gyakran használt profilok (5+): " + frequentCount);
        text = text.concat("\n" + "Ritkán használt profilok (4-1): " + rareCount);
        text = text.concat("\n" + "Használatlan profilok (0): " + unusedCount);
        textArea.setText(text);
        textArea.setCaretPosition(0);
        primaryProfileList.scrollToTop();
        legacyProfileList.scrollToTop();
    }

    private String cmuFolderToTextDate(String folder) {
        return folder.substring(0, 4) + "." + folder.substring(4, 6) + "." + folder.substring(6, 8);
    }

}
