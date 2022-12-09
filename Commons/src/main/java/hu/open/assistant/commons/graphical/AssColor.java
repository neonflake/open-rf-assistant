package hu.open.assistant.commons.graphical;

import java.awt.Color;

public enum AssColor {
    GENERIC_BLACK(Color.black),
    GENERIC_BORDER_GREY(new Color(122, 138, 153)),
    LABEL_DARK_GREEN(Color.green.darker().darker()),
    LABEL_DARK_RED(Color.red.darker()),
    LABEL_DARK_BLUE(Color.blue.darker().darker()),
    LIST_CELL_BORDER_BLUE(new Color(99, 130, 191)),
    LIST_BACKGROUND_GREY(new Color(224, 234, 244));

    private final Color color;

    AssColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
