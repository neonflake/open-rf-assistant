package hu.open.assistant.commons.graphical.gui;

import hu.open.assistant.commons.graphical.AssColor;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;

/**
 * An extended label GUI component which can be configured on creation and has more functionality.
 */

public class AssLabel extends JLabel {

	/**
     * Create the label with a name.
     *
     * @param name          of the label
     * @param text          displayed on the label
     * @param fontSize      of the text on the label
     * @param preferredSize width and height in pixels
     */
    public AssLabel(String name, String text, int fontSize, Dimension preferredSize) {
        this(text, fontSize, preferredSize);
        this.setName(name);
    }

    /**
     * Create the label without a name.
     *
     * @param text          displayed on the label
     * @param fontSize      of the text on the label
     * @param preferredSize width and height in pixels
     */
    public AssLabel(String text, int fontSize, Dimension preferredSize) {
        super(text, SwingConstants.CENTER);
        setText(text);
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setPreferredSize(preferredSize);
	}

	/**
	 * Align the displayed text on the label to the left.
	 */
	public void alignLeft() {
		this.setHorizontalAlignment(SwingConstants.LEFT);
	}

	/**
	 * Set the color of the label's text to a predefined color.
	 *
	 * @param color name (dark_green, dark_red, dark_blue, black)
	 */
	public void changeColor(AssColor color) {
		this.setForeground(color.getColor());
	}
}
