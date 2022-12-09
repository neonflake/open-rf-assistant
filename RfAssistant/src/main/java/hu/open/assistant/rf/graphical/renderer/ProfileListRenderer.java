package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.profile.Profile;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a profile on a graphical list.
 */
public class ProfileListRenderer extends AssListRenderer<Profile> {

    public ProfileListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Profile> list, Profile profile, int index, boolean isSelected, boolean cellHasFocus) {
        setIcon(new AssImage(getClass().getResource("/images/cond_" + profile.getCondition() + ".png")));
        String text = profile.getName();
        if (profile.isReverted()) {
            text = text.concat(" (visszaállítva)");
        } else if (profile.isCompensated()) {
            text = text.concat(" (kompenzálva)");
        }
        if (profile.getTesterType() == TesterType.CMW && ((CmwProfile) profile).isTacModified()) {
            text = text.concat(" (módosítva)");
        }
        setText(text);
        return super.getListCellRendererComponent(list, profile, index, isSelected, cellHasFocus);
    }
}
