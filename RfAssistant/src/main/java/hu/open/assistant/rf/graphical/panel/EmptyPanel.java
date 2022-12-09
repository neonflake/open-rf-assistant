package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;

/**
 * GUI template for creating new interfaces. Includes the label, back button, logo and side button fillers.
 */
@SuppressWarnings("unused")
public class EmptyPanel extends RfPanel {

    public EmptyPanel(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "EmptyPanel");
        placer.enableDebug();
        AssLabel titleLabel = new AssLabel("Üres", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        AssButton backButton = new AssButton("EmptyPanel backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 1, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 2, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 3, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }
}
