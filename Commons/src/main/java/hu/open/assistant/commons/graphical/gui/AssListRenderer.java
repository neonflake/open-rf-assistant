package hu.open.assistant.commons.graphical.gui;

import hu.open.assistant.commons.graphical.AssColor;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Component;

/**
 * A base list cell renderer implementation used by AppList. It provides basic functionality for displaying list elements
 * graphically correctly and stores the render mode which can affect the rendered text's content.
 *
 * @param <T> class of elements to render on list
 */
public class AssListRenderer<T> extends JLabel implements ListCellRenderer<T> {

    protected String renderMode;

    /**
     * Create the list render.
     */
    public AssListRenderer() {
        setOpaque(true);
    }

    /**
     * Set the current render mode.
     */
    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T object, int index, boolean isSelected, boolean cellHasFocus) {
        setFont(list.getFont());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        if (cellHasFocus) {
            setBorder(new LineBorder(AssColor.LIST_CELL_BORDER_BLUE.getColor()));
        } else {
            setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        return this;
    }
}


