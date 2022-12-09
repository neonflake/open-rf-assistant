package hu.open.assistant.rf.model;

import java.awt.Color;

/**
 * A logical representation of a point placed on a graph. Besides the position, size and color information, it
 * determines the points group and helps with mouse interactivity.
 */
public class Point {
    private final int xPosition;
    private final int yPosition;
    private int xSize;
    private int ySize;
    private final int defaultXSize;
    private final int defaultYSize;
    private final int groupId;
    private final Color defaultColor;
    private Color color;
    private boolean selected;
    private final String source;

    public Point(int groupId, int xPosition, int yPosition, int xSize, int ySize, Color color, String source) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.xSize = xSize;
        this.ySize = ySize;
        this.defaultXSize = xSize;
        this.defaultYSize = ySize;
        this.groupId = groupId;
        this.color = color;
        this.defaultColor = color;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getGroupId() {
        return groupId;
    }

    public Color getColor() {
        return color;
    }

    public boolean isSelected() {
        return selected;
    }

    public void unSelectPoint() {
        selected = false;
        color = defaultColor;
        xSize = defaultXSize;
        ySize = defaultYSize;
    }

    public void selectPoint() {
        if (!selected) {
            selected = true;
            this.color = Color.red;
            this.xSize = xSize * 2;
            this.ySize = ySize * 2;
        }
    }

    public boolean isInside(int x, int y) {
        if (x >= xPosition && x < xPosition + defaultXSize) {
            return y >= yPosition && y < yPosition + defaultYSize;
        }
        return false;
    }
}
