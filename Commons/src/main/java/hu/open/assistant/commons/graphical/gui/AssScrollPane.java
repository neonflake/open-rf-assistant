package hu.open.assistant.commons.graphical.gui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;

/**
 * An extended scroll pane GUI component which can be configured on creation and has more functionality.
 */
public class AssScrollPane extends JScrollPane {

	private final JComponent parent;

	/**
	 * Create the scroll pane.
	 *
	 * @param preferredSize width and height in pixels
	 * @param parent        bound to the scroll pane
	 */
	public AssScrollPane(Dimension preferredSize, JComponent parent) {
		super(parent, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setPreferredSize(preferredSize);
		setName(parent.getName());
		this.parent = parent;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		parent.setEnabled(enabled);
	}
}
