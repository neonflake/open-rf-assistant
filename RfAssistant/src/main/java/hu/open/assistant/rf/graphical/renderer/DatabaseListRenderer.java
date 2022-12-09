package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.database.Database;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a database on a graphical list.
 */
public class DatabaseListRenderer extends AssListRenderer<Database> {

    public DatabaseListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Database> list, Database database, int index, boolean isSelected, boolean cellHasFocus) {
        String text = String.valueOf(database.getSerial());
        if (database.isModified()) {
            text = text + " (módosítva)";
        }
        setText(text);
        return super.getListCellRendererComponent(list, database, index, isSelected, cellHasFocus);
    }
}
