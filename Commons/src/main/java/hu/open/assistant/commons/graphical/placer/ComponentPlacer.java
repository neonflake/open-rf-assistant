package hu.open.assistant.commons.graphical.placer;

import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.commons.util.NumberHelper;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple layout manager that operates on a grid. You can place components on the grid by setting their position
 * (cell) and the amount of vertical and horizontal cells they should occupy. The component's actual size in pixels
 * will affect the grid layout (cell sizes). You can also use invisible filler components for cell stretching, to
 * provide a universal look across your application. Component sizes added together should not exceed tha panels size,
 * otherwise the grid (layout) will collapse.
 */
public class ComponentPlacer {
	private final JPanel panel;
	private final GridBagConstraints gridBagConstrains;
	private final List<JPanel> containerPanels;
	private final List<String> disabledComponents;
	private final List<JComponent> hiddenComponents;
	private boolean debug;
	private Cell[][] grid;

	/**
     * Initialize the layout manager for use. If you don't want to use custom weight for your cells, you should also
     * call the calculateGrid() method with the needed grid size.
     *
     * @param panel to place components on
     */
	public ComponentPlacer(JPanel panel) {
		this.panel = panel;
		gridBagConstrains = new GridBagConstraints();
		containerPanels = new ArrayList<>();
		disabledComponents = new ArrayList<>();
		hiddenComponents = new ArrayList<>();
	}

	/**
	 * This method ensures that weight calculation of cells is done automatically. Weight affects mainly empty cells or
	 * large cells containing small components. Large components will stretch out cells ignoring their weight value.
	 * Using the method the horizontal and vertical weight is split evenly across all cells. You can use normal or filler
	 * components to stretch out the user interface, this way weight values can be omitted when adding components to the grid.
	 * Cells should be stretched out with components both vertically and horizontally to the edge of the panel as much as possible.
	 *
	 * @param xCells number of horizontal cells on the grid
	 * @param yCells number of vertical cells on the grid
	 */
	public void calculateGrid(int xCells, int yCells) {
		if (yCells < 1) {
			yCells = 1;
			System.out.println("Rows must be greater than zero!");
		}
		if (xCells < 1) {
			xCells = 1;
			System.out.println("Columns must be greater than zero!");
		}
		grid = new Cell[xCells][yCells];
		double width = 1.0 / xCells;
		double height = 1.0 / yCells;
		double roundedWidth = NumberHelper.twoDecimalPlaceOf(width);
		double roundedHeight = NumberHelper.twoDecimalPlaceOf(height);
		double lastWidth = NumberHelper.twoDecimalPlaceOf(1 - ((xCells - 1) * roundedWidth));
		double lastHeight = NumberHelper.twoDecimalPlaceOf(1 - ((yCells - 1) * roundedHeight));
		for (int x = 0; x < xCells; x++) {
			for (int y = 0; y < yCells; y++) {
				if (y < yCells - 1) {
					height = roundedHeight;
				} else {
					height = lastHeight;
				}
				if (x < xCells - 1) {
					width = roundedWidth;
				} else {
					width = lastWidth;
				}
				grid[x][y] = new Cell(width, height);
            }
        }
    }

    /**
     * Fill the cells background with red and filler elements with dark red color. This helps with fixing component size
     * related problems when the grid collapses.
	 */
	public void enableDebug() {
		debug = true;
	}

	/**
	 * Disable all interactive components on the panel which are currently enabled. Filler components and AppLabel
	 * components are not going to be affected.
	 */
	public void disableComponents() {
		for (JPanel container : containerPanels) {
			if (container.getComponentCount() > 0) {
				JComponent component = (JComponent) container.getComponent(0);
				if (!(component instanceof AssLabel) && !(component instanceof FakePanel)) {
					if (component.isEnabled()) {
						component.setEnabled(false);
						disabledComponents.add(component.getName());
					}
				}
			}
		}
	}

	/**
	 * Re-enable interactive components that where disabled using the disableComponents() method.
	 */
	public void enableComponents() {
		for (JPanel container : containerPanels) {
			if (container.getComponentCount() > 0) {
				JComponent component = (JComponent) container.getComponent(0);
				if (!(component instanceof AssLabel) && !(component instanceof FakePanel)) {
					if (disabledComponents.contains(component.getName())) {
						component.setEnabled(true);
					}
				}
			}
		}
		disabledComponents.clear();
    }

    /**
     * Hide a component on the panel by replacing it with a same size filler component.
     *
     * @param name of the component to hide
	 */
	public void hideComponent(String name) {
		JPanel container = null;
		for (JPanel panel : containerPanels) {
			if (panel.getName().equals(name)) {
				container = panel;
			}
		}
		if (container != null) {
			JComponent component = (JComponent) container.getComponent(0);
			if (!(component instanceof FakePanel)) {
				hiddenComponents.add(component);
				container.remove(0);
				container.add(new FakePanel(component.getName(), component.getPreferredSize(), debug));
            }
        }
    }

    /**
     * Show a component on the panel, that was previously hidden with the hideComponent() method.
     *
     * @param name of the component to show
	 */
	public void showComponent(String name) {
		JPanel container = (JPanel) getMatchingComponent(containerPanels, name);
		if (container != null) {
			JComponent hiddenComponent = getMatchingComponent(hiddenComponents, name);
			if (hiddenComponent != null) {
				container.remove(0);
				container.add(hiddenComponent);
				hiddenComponents.remove(hiddenComponent);
            }
        }
    }

    /**
     * Remove a component permanently from the panel.
     *
     * @param name of the component to remove
	 */
	public void removeComponent(String name) {
		JPanel match = null;
		for (JPanel panel : containerPanels) {
			if (panel.getName().equals(name)) {
				match = panel;
			}
		}
		if (match != null) {
			containerPanels.remove(match);
			panel.remove(match);
        }
    }

    /**
     * Add a component to the panel. The cell will be stretched by the components size when needed.
     *
     * @param component to add
     * @param xPosition horizontal cell of the component on the grid
     * @param yPosition vertical cell of the component on the grid
     * @param xSize     how many cells should the component occupy horizontally
     * @param ySize     how many cells should the component occupy vertically
	 */
	public void addComponent(JComponent component, int xPosition, int yPosition, int xSize, int ySize) {
		addComponent(component, xPosition, yPosition, xSize, ySize, 0, 0);
    }

    /**
     * Add a component to the panel with custom weight. The cell will be stretched by the components size when needed.
     * Weights added together both horizontally and vertically should be 1. For example horizontally 0.7, 0.3 and
     * vertically 0.25, 0.25, 0.25, 0.25 (eight cell grid fo left side content and right side navigation).
     *
     * @param component to add
     * @param xPosition horizontal cell of the component on the grid
     * @param yPosition vertical cell of the component on the grid
     * @param xSize     how many cells should the component occupy horizontally
     * @param ySize     how many cells should the component occupy vertically
     * @param xWeight   how much weight the component's cell has horizontally against other cells (1.0 max.)
     * @param yWeight   how much weight the component's cell has vertically against other cells (1.0 max.)
	 */
	public void addComponent(JComponent component, int xPosition, int yPosition, int xSize, int ySize, double xWeight, double yWeight) {
		prepareGridBagConstrains(xPosition, yPosition, xSize, ySize, xWeight, yWeight);
		if (component instanceof AssList) {
			if (((AssList<?>) component).getScrollPane() != null) {
				component = ((AssList<?>) component).getScrollPane();
			}
		} else if (component instanceof AssTextArea) {
			if (((AssTextArea) component).getScrollPane() != null) {
				component = ((AssTextArea) component).getScrollPane();
			}
		}
		JPanel containerPanel = new JPanel();
		if (component.getName() != null) {
			containerPanel.setName(component.getName());
		} else {
			containerPanel.setName("");
		}
		if (!debug) {
			containerPanel.setOpaque(false);
		} else {
			containerPanel.setBackground(Color.red);
		}
		containerPanel.add(component);
		addPanel(containerPanel);
	}

	/**
	 * Add an image the panel embedded in a frame component.
	 *
	 * @param image     to add
	 * @param xPosition horizontal cell of the image on the grid
	 * @param yPosition vertical cell of the image on the grid
	 * @param xSize     how many cells should the image occupy horizontally
	 * @param ySize     how many cells should the image occupy vertically
	 */
	public void addImageComponent(AssImage image, int xPosition, int yPosition, int xSize, int ySize) {
		addImageComponent(image, xPosition, yPosition, xSize, ySize, 0, 0);
	}

	/**
	 * Add an image the panel embedded in a frame component with custom weight.
	 *
	 * @param image     to add
	 * @param xPosition horizontal cell of the image on the grid
	 * @param yPosition vertical cell of the image on the grid
	 * @param xSize     how many cells should the image occupy horizontally
	 * @param ySize     how many cells should the image occupy vertically
	 * @param xWeight   how much weight the image's cell has horizontally against other cells (1.0 max.)
	 * @param yWeight   how much weight the image's cell has vertically against other cells (1.0 max.)
	 */
	public void addImageComponent(AssImage image, int xPosition, int yPosition, int xSize, int ySize, double xWeight, double yWeight) {
		AssLabel imageLabel = new AssLabel(image.getName(), "", 8, new Dimension(image.getIconWidth(), image.getIconHeight()));
		imageLabel.setIcon(image);
		addComponent(imageLabel, xPosition, yPosition, xSize, ySize, xWeight, yWeight);
	}

    /**
     * Add an empty filler component that is not visible on the panel. Use it to stretch out the grid's cells to maintain
     * a specific layout.
     *
     * @param componentSize x and y size of the component in pixels
     * @param xPosition     horizontal cell of the component on the grid
     * @param yPosition     vertical cell of the component on the grid
     * @param xSize         how many cells should the component occupy horizontally
     * @param ySize         how many cells should the component occupy vertically
	 */
	public void addEmptyComponent(Dimension componentSize, int xPosition, int yPosition, int xSize, int ySize) {
		addEmptyComponent("", componentSize, xPosition, yPosition, xSize, ySize);
    }

    /**
     * Add an empty filler component that is not visible on the panel. Use it to stretch out the grid's cells to maintain
     * a specific layout. With the given name the component is replaceable in runtime, with another component.
     *
     * @param name          of the filler component
     * @param componentSize x and y size of the component in pixels
     * @param xPosition     horizontal cell of the component on the grid
     * @param yPosition     vertical cell of the component on the grid
     * @param xSize         how many cells should the component occupy horizontally
     * @param ySize         how many cells should the component occupy vertically
	 */
	public void addEmptyComponent(String name, Dimension componentSize, int xPosition, int yPosition, int xSize, int ySize) {
		addEmptyComponent(name, componentSize, xPosition, yPosition, xSize, ySize, 0, 0);
    }

    /**
     * Add an empty filler component with custom weight that is not visible on the panel. Use it to stretch out the
     * grid's cells to maintain a specific layout. With the given name the component is replaceable in runtime, with
     * another component.
     *
     * @param name          of the filler component
     * @param componentSize x and y size of the component in pixels
     * @param xPosition     horizontal cell of the component on the grid
     * @param yPosition     vertical cell of the component on the grid
     * @param xSize         how many cells should the component occupy horizontally
     * @param ySize         how many cells should the component occupy vertically
     * @param xWeight       how much weight the component's cell has horizontally against other cells (1.0 max.)
	 * @param yWeight       how much weight the component's cell has vertically against other cells (1.0 max.)
	 */
	public void addEmptyComponent(String name, Dimension componentSize, int xPosition, int yPosition, int xSize, int ySize, double xWeight, double yWeight) {
		addComponent(new FakePanel(name, componentSize, debug), xPosition, yPosition, xSize, ySize, xWeight, yWeight);
	}

	private JComponent getMatchingComponent(List<? extends JComponent> components, String name) {
		for (JComponent component : components) {
			if (component.getName().equals(name)) {
				return component;
			}
		}
		return null;
	}

	private void addPanel(JPanel containerPanel) {
		containerPanels.add(containerPanel);
		panel.add(containerPanel, gridBagConstrains);
	}

	private void prepareGridBagConstrains(int gridX, int gridY, int gridWidth, int gridHeight, double weightX, double weightY) {
		gridBagConstrains.insets = new Insets(5, 5, 5, 5);
		gridBagConstrains.gridx = gridX;
		gridBagConstrains.gridy = gridY;
		gridBagConstrains.gridwidth = gridWidth;
		gridBagConstrains.gridheight = gridHeight;
		if (weightX == 0 && weightY == 0) {
			for (int x = 0; x < gridWidth; x++) {
				weightX += grid[gridX - 1 + x][gridY - 1].getWidth();
			}
			for (int y = 0; y < gridHeight; y++) {
				weightY += grid[gridX - 1][gridY - 1 + y].getHeight();
			}
		}
		gridBagConstrains.weightx = NumberHelper.twoDecimalPlaceOf(weightX);
		gridBagConstrains.weighty = NumberHelper.twoDecimalPlaceOf(weightY);
		gridBagConstrains.fill = GridBagConstraints.BOTH;
	}
}
