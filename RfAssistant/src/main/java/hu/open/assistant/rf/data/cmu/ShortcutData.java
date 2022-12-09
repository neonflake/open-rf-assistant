package hu.open.assistant.rf.data.cmu;

import hu.open.assistant.rf.filter.ShortcutFilter;
import hu.open.assistant.rf.model.Contraction;
import hu.open.assistant.rf.model.ShieldBox;
import hu.open.assistant.rf.model.Shortcut;
import hu.open.assistant.rf.model.profile.parts.ProfileParts;
import hu.open.assistant.commons.data.FileHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data class which reads and writes raw CMU type shortcuts (related to RF profiles) from or to the disk. Shortcut
 * information is stored in a custom text format compatible with CMUgo application. The logical shortcuts are organised
 * and handled into a logical shortcut container. Data files written to disk are fully generated from code.
 */
public class ShortcutData {

    private static final String DATAFILE = "Shortcut.ini";

    private static final String LIST_1_HEADER = "[PHONELIST1]";
    private static final String LIST_2_HEADER = "[PHONELIST2]";
    private static final String LIST_CLOSE = "- - -=UNKNOWN";

    private final String shortcutFolder;
    private final FileHandler fileHandler;
    private ProfileParts profileParts;

    public ShortcutData(String shortcutFolder, FileHandler fileHandler, ProfileParts profileParts) {
        this.shortcutFolder = shortcutFolder;
        this.fileHandler = fileHandler;
        this.profileParts = profileParts;
    }

    public void setProfileParts(ProfileParts profileParts) {
        this.profileParts = profileParts;
    }

    public void writeShortcuts(List<Shortcut> shortcuts) {
        Collections.sort(shortcuts);
        List<String> text = new ArrayList<>();
        text.add(LIST_1_HEADER);
        for (Shortcut shortcut : ShortcutFilter.getShortcutsByListNumber(shortcuts, 1)) {
            text.add(combinePhoneParts(shortcut));
        }
        text.add(LIST_CLOSE);
        text.add("");
        text.add(LIST_2_HEADER);
        for (Shortcut shortcut : ShortcutFilter.getShortcutsByListNumber(shortcuts, 2)) {
            text.add(combinePhoneParts(shortcut));
        }
        text.add(LIST_CLOSE);
        fileHandler.writeUtf8Text(shortcutFolder + "\\" + DATAFILE, text, false);
    }

    public List<Shortcut> readShortcuts() {
        List<Shortcut> shortcuts = new ArrayList<>();
        List<String> shortcutData = fileHandler.readUtf8TextToList(shortcutFolder + "\\" + DATAFILE, false);
        int listCounter = 0;
        if (shortcutData != null) {
            for (String line : shortcutData) {
                if (line.contains("PHONELIST")) {
                    listCounter++;
                } else if (line.contains("=")) {
                    String[] parts = line.split("=");
                    if (!parts[1].equals("UNKNOWN")) {
                        String[] phoneParts = parts[0].substring(0, parts[0].length() - 1).split(" ");
                        String script = parts[1].substring(1);
                        String manufacturer = phoneParts[0];
                        String shortBox = phoneParts[phoneParts.length - 2];
                        String box;
                        String position;
                        int typeEnd;
                        if (!manufacturer.equals("Generic")) {
                            box = profileParts.shortToLongBox(shortBox);
                            position = phoneParts[phoneParts.length - 1];
                            typeEnd = phoneParts.length - 2;
                        } else {
                            box = "";
                            position = "";
                            typeEnd = phoneParts.length;
                        }
                        String type = "";
                        for (int p = 1; p < typeEnd; p++) {
                            if (p != 1) {
                                type = type.concat(" ");
                            }
                            type = type.concat(phoneParts[p]);
                        }
                        Shortcut shortcut = new Shortcut(listCounter, manufacturer, type, new ShieldBox(new Contraction(box, shortBox)), position, script);
                        shortcuts.add(shortcut);
                    }
                }
            }
        }
        return shortcuts;
    }

    private String combinePhoneParts(Shortcut shortcut) {
        if (!shortcut.getManufacturer().equals("Generic")) {
            return shortcut.getName() + " " + shortcut.getBox().getShortName() + " " + shortcut.getPosition() + " = " + shortcut.getScript();
        } else {
            return shortcut.getName() + " = " + shortcut.getScript();
        }
    }
}
