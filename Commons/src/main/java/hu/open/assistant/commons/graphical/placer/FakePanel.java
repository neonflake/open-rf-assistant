package hu.open.assistant.commons.graphical.placer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Filler component used by ComponentPlacer. Fills out empty places in panels and stretches out cells to the
 * needed size. It is used in combination with other components to maintain a specific grid layout.
 */
public class FakePanel extends JPanel {

	/**
	 * Create the fake panel component.
	 *
	 * @param name      component's name (needed if you want to replace the component in runtime)
	 * @param dimension components x and y size in pixels
	 * @param debug     flag to color the component in debug mode
	 */

	public FakePanel(String name, Dimension dimension, boolean debug) {
		setName(name);
		if (!debug) {
            setOpaque(false);
		} else {
            setBackground(Color.red.darker());
		}
        setPreferredSize(dimension);
	}
}
