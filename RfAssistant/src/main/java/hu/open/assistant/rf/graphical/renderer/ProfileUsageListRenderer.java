package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.profile.usage.ProfileUsage;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a profile usage on a graphical list.
 */
public class ProfileUsageListRenderer extends AssListRenderer<ProfileUsage> {

    public ProfileUsageListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ProfileUsage> list, ProfileUsage profileUsage, int index, boolean isSelected, boolean cellHasFocus) {
        setText(profileUsage.getName() + " (" + profileUsage.getUnitCount() + ")");
        return super.getListCellRendererComponent(list, profileUsage, index, isSelected, cellHasFocus);
    }
}
