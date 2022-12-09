package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.Equipment;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying equipments on a graphical list.
 */
public class EquipmentListRenderer extends AssListRenderer<Equipment> {

    public EquipmentListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Equipment> list, Equipment equipment, int index, boolean isSelected, boolean cellHasFocus) {
        String text = equipment.getName();
        if (equipment.isModified()) {
            text = text + " (módosítva)";
        }
        setText(text);
        return super.getListCellRendererComponent(list, equipment, index, isSelected, cellHasFocus);
    }
}
