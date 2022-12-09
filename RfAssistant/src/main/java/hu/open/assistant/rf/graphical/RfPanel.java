package hu.open.assistant.rf.graphical;

import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.AssPanel;
import hu.open.assistant.rf.RfAssistant;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Defines tha basic panel layout and some common objects for all the panels in the application.
 */
public class RfPanel extends AssPanel {

    protected static final Dimension SIDE_BUTTON_DIMENSION = new Dimension(250, 50);
    protected static final Dimension TITLE_LABEL_DIMENSION = new Dimension(650, 50);
    protected static final int SIDE_BUTTON_TEXT_SIZE = 14;
    protected static final int TITLE_LABEL_TEXT_SIZE = 24;

    protected RfAssistant assistant;
    protected AssImage logoImage;

    public RfPanel(RfWindow window, RfAssistant assistant, String name) {
        super(name, window);
        this.assistant = assistant;
        this.setBackground(Color.WHITE);
        placer.calculateGrid(4, 10);
        logoImage = new AssImage(getClass().getResource("/images/logo.png"));
    }
}
