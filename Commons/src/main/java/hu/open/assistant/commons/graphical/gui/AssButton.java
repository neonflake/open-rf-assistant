package hu.open.assistant.commons.graphical.gui;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * An extended button GUI component which can be configured on creation.
 */
public class AssButton extends JButton {

	/**
	 * Create the button.
	 *
	 * @param name          of the button
	 * @param text          displayed on the button
	 * @param fontSize      of the text displayed on the button
	 * @param preferredSize width and height in pixels
	 * @param listener      responsible for event handling
	 * @param enabled       default state of the button
	 */
	public AssButton(String name, String text, int fontSize, Dimension preferredSize, ActionListener listener, boolean enabled) {
		setText(text);
		setName(name);
		setFont(new Font("Arial", Font.BOLD, fontSize));
		addActionListener(listener);
		setEnabled(enabled);
		setPreferredSize(preferredSize);
	}
}
