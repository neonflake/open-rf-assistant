package hu.open.assistant.rf.filter;

import hu.open.assistant.rf.model.Shortcut;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that filters Shortcuts from the provided list.
 */
public class ShortcutFilter {

    private ShortcutFilter() {

    }

    public static Shortcut getShortcutByName(List<Shortcut> shortcuts, String name) {
        for (Shortcut shortcut : shortcuts) {
            if (shortcut.getName().equals(name)) {
                return shortcut;
            }
        }
        return null;
    }

    public static List<Shortcut> getShortcutsByListNumber(List<Shortcut> shortcuts, int listNumber) {
        List<Shortcut> filteredShortcuts = new ArrayList<>();
        for (Shortcut shortcut : shortcuts) {
            if (shortcut.getListNumber() == listNumber) {
                filteredShortcuts.add(shortcut);
            }
        }
        return filteredShortcuts;
    }
}
