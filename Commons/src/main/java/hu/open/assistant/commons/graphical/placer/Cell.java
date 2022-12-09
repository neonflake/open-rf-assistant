package hu.open.assistant.commons.graphical.placer;

/**
 * Used by ComponentPlacer to calculate grid layout.
 */
public class Cell {

    private final double width;
    private final double height;

    /**
     * Create the cell.
     *
     * @param width  fixed horizontal weight value
     * @param height fixed vertical weight value
     */
    public Cell(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Return the cell's horizontal weight value.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Return the cell's vertical weight value.
     */
    public double getHeight() {
        return height;
    }

}
