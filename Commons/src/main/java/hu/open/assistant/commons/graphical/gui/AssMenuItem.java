package hu.open.assistant.commons.graphical.gui;

import hu.open.assistant.commons.graphical.AssImage;

import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * An extended menu item GUI component which can be configured on creation.
 */
public class AssMenuItem extends JMenuItem {

    private boolean gradientBackground;

    /**
     * Create the menu item.
     *
     * @param name     of the menu item
     * @param text     displayed on the menu item
     * @param imageUrl image resource displayed on the menu item
     * @param listener responsible for event handling
     * @param enabled  default state of the menu item
     */
    public AssMenuItem(String name, String text, URL imageUrl, ActionListener listener, boolean enabled) {
        super(text);
        addActionListener(listener);
        setName(name);
        if (imageUrl != null) {
            setIcon(new AssImage(imageUrl));
        }
        setEnabled(enabled);
    }

    /**
     * Enabled a gradient background similar to the menubar.
     */
    public void enableGradientBackground() {
        setBackground(new Color(0, 0, 0, 0));
        gradientBackground = true;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        if (gradientBackground) {
            Graphics2D gradientGraphics = (Graphics2D) graphics;
            GradientPaint gradientPaint = new GradientPaint(0, 0, Color.white, 0, this.getHeight(), new Color(222, 222, 222));
            gradientGraphics.setPaint(gradientPaint);
            gradientGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        super.paintComponent(graphics);
    }
}

