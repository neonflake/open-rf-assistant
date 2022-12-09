package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.rf.model.database.backup.DatabaseBackup;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a database backup on a graphical list.
 */
public class BackupListRenderer extends AssListRenderer<DatabaseBackup> {

    public BackupListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DatabaseBackup> list, DatabaseBackup backup, int index, boolean isSelected, boolean cellHasFocus) {
        setText(DateHelper.localDateTimeToTextDateTime(backup.getDateTime()) + " (" + backup.getSerial() + ")");
        return super.getListCellRendererComponent(list, backup, index, isSelected, cellHasFocus);
    }
}
