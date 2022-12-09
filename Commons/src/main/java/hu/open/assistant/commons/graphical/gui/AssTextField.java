package hu.open.assistant.commons.graphical.gui;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * An extended text field GUI component which can be configured on creation and has more functionality.
 */
public class AssTextField extends JTextField {

	/**
	 * Create the text field
	 *
	 * @param name          of the text field
	 * @param preferredSize width and height in pixels
	 * @param fontSize      of the text displayed inside the text field
	 * @param listener      responsible for event handling
	 * @param text          default text to display inside the text field
	 * @param enabled       default state of the text field
	 */
	public AssTextField(String name, Dimension preferredSize, int fontSize, ActionListener listener, String text, boolean enabled) {
		setFont(new Font("Arial", Font.BOLD, fontSize));
		setName(name);
		setText(text);
		addActionListener(listener);
		setEnabled(enabled);
		setPreferredSize(preferredSize);
	}

	/**
	 * Align the text to center inside the password field.
	 */
	public void centerText() {
        setHorizontalAlignment(SwingConstants.CENTER);
	}

	/**
	 * Display only static information and disable editing.
	 *
	 * @param enabled true for static, false for default behavior
	 */
	public void setStatic(boolean enabled) {
		if (enabled) {
            setForeground(Color.darkGray);
            setEditable(false);
            setFocusable(false);
        } else {
            setForeground(Color.black);
            setEditable(true);
            setFocusable(true);
        }
	}

	public int getTextAsInteger() throws NumberFormatException {
		return Integer.parseInt(getText());
	}

	public long getTextAsLong() throws NumberFormatException {
		return Long.parseLong(getText());
	}
}
