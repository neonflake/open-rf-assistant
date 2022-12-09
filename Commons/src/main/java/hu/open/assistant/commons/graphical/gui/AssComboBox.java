package hu.open.assistant.commons.graphical.gui;

import javax.swing.JComboBox;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * An extended combo box GUI component which can be configured on creation and has more functionality.
 */
public class AssComboBox extends JComboBox<String> {

    /**
     * Create the combo box.
     *
     * @param name          of the combo box
     * @param options       to select from
     * @param fontSize      of the selectable options
     * @param preferredSize width and height in pixels
     * @param listener      responsible for event handling
     */
    public AssComboBox(String name, String[] options, int fontSize, Dimension preferredSize, ActionListener listener) {
        super(options);
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setName(name);
        addActionListener(listener);
        setPreferredSize(preferredSize);
    }

    /**
     * Return the selected item (option) as a String or an empty String when nothing is selected.
     */
	public String getSelectedItemAsString() {
		String selectedItem = (String) getSelectedItem();
        return selectedItem != null ? selectedItem : "";
	}
}
