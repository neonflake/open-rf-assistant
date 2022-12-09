package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.Contraction;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a profile parts component (string, contraction) on a graphical list.
 */
public class ProfilePartListRenderer extends AssListRenderer<Object> {

    public ProfilePartListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
        if (object instanceof Contraction) {
            setText(((Contraction) object).getName());
        } else {
            setText(object.toString());
        }
        return super.getListCellRendererComponent(list, object, index, isSelected, cellHasFocus);
    }
}