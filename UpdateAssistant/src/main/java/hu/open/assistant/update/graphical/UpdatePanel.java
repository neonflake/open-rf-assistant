package hu.open.assistant.update.graphical;

import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.AssPanel;
import hu.open.assistant.commons.graphical.placer.ComponentPlacer;
import hu.open.assistant.update.UpdateAssistant;

import java.awt.Color;

/**
 * Defines tha basic panel layout and some common objects for all the panels in the application.
 */
public class UpdatePanel extends AssPanel {

    protected UpdateAssistant assistant;
    protected AssImage logoImage;
    protected ComponentPlacer placer;

    public UpdatePanel(UpdateWindow window, UpdateAssistant assistant, String name) {
        super(name, window);
        this.assistant = assistant;
        this.setBackground(Color.WHITE);
        placer = new ComponentPlacer(this);
        placer.calculateGrid(4, 10);
        logoImage = new AssImage(getClass().getResource("/images/logo.png"));
    }
}
