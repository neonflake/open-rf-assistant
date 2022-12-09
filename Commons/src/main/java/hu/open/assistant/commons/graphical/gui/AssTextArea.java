package hu.open.assistant.commons.graphical.gui;

import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Font;

/**
 * An extended text area GUI component which can be configured on creation and has more functionality. It can contain an
 * optional scroll pane if necessary.
 */
public class AssTextArea extends JTextArea {

    private final AssScrollPane scrollPane;

    /**
     * Create the text area.
     *
     * @param name          of the text area
     * @param fontSize      of the text displayed inside the text area
     * @param preferredSize width and height in pixels
     * @param scrollable    make the text area scrollable
     */
    public AssTextArea(String name, int fontSize, Dimension preferredSize, boolean scrollable) {
        setLineWrap(true);
        setEditable(false);
        setWrapStyleWord(true);
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setName(name);
        if (scrollable) {
            setPreferredSize(null);
            scrollPane = new AssScrollPane(preferredSize, this);
        } else {
            scrollPane = null;
            setPreferredSize(preferredSize);
        }
    }

    /**
     * Return the scroll pane bound to the text area.
     */
    public AssScrollPane getScrollPane() {
        return scrollPane;
    }

}
