package hu.open.assistant.update.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.update.model.UpdatePackage;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying an update package on a graphical list.
 */
public class UpdatePackageListRenderer extends AssListRenderer<UpdatePackage> {

    public UpdatePackageListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends UpdatePackage> list, UpdatePackage programVersion, int index, boolean isSelected, boolean cellHasFocus) {
        String text = programVersion.getProgram().getName() + " - v" + programVersion.getVersion();
        this.setText(text);
        return super.getListCellRendererComponent(list, programVersion, index, isSelected, cellHasFocus);
    }
}
