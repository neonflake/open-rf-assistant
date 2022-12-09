package hu.open.assistant.commons.graphical.notification;

import java.awt.Dimension;

/**
 * Size definitions for components used by BasicNotification. The normal and reduced size is recommended by default, the
 * large option is for displaying very large images.
 */
public enum NotificationSize {
    NORMAL(520, 520, 475, 150, 75, 50, 18),
    LARGE(950, 950, 475, 150, 75, 50, 18),
    REDUCED(400, 400, 355, 100, 75, 50, 16);

    private final Dimension dialogDimension;
    private final Dimension textAreaDimension;
    private final Dimension buttonDimension;
    private final int textSize;

    NotificationSize(int dialogWidth, int dialogHeight, int textAreaWidth, int textAreaHeight,
                     int buttonWidth, int buttonHeight, int textSize) {
        dialogDimension = new Dimension(dialogWidth, dialogHeight);
        textAreaDimension = new Dimension(textAreaWidth, textAreaHeight);
        buttonDimension = new Dimension(buttonWidth, buttonHeight);
        this.textSize = textSize;
    }

    public Dimension getDialogDimension() {
        return dialogDimension;
    }

    public Dimension getTextAreaDimension() {
        return textAreaDimension;
    }

    public Dimension getButtonDimension() {
        return buttonDimension;
    }

    public int getTextSize() {
        return textSize;
    }
}
