package hu.open.assistant.commons.graphical.gui;

import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * An extended password field GUI component which can be configured on creation and has more functionality.
 */
public class AssPasswordField extends JPasswordField {

    /**
     * Create the password field.
     *
     * @param name          of the password field
     * @param fontSize      of the text displayed inside the password field
     * @param preferredSize width and height in pixels
     * @param listener      responsible for event handling
     */
    public AssPasswordField(String name, int fontSize, Dimension preferredSize, ActionListener listener) {
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setName(name);
        addActionListener(listener);
        setPreferredSize(preferredSize);
    }

    /**
     * Align the text to center inside the password field.
     */
    public void centerText() {
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
