package hu.open.assistant.commons.graphical;

import hu.open.assistant.commons.graphical.placer.ComponentPlacer;
import hu.open.assistant.commons.graphical.task.AssTask;
import hu.open.assistant.commons.graphical.task.RunComponent;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

/**
 * Base panel for inheritance used by all Assistant applications. The panel has a built-in layout manager and has
 * methods for virtual keyboard input and background task running on another thread.
 */
public abstract class AssPanel extends JPanel implements RunComponent {

    protected ComponentPlacer placer;
    protected ActionListener listener;
    protected AssWindow window;

    /**
     * Create a panel with the given name and initialize the layout manager. In child constructor you can add
     * application specific look and built in components. Don't forget to specify the application specific grid size
     * in the child constructor and to call the layout managers calculateGrid() method.
     *
     * @param name   of the panel
     * @param window for handling events
     */
    public AssPanel(String name, AssWindow window) {
        this.listener = window;
        this.window = window;
        setName(name);
        setLayout(new GridBagLayout());
        placer = new ComponentPlacer(this);
    }

    /**
     * Revalidate and repaint the component in one go.
     */
    public void reDraw() {
        this.revalidate();
        this.repaint();
    }

    /**
     * Receive a task running on a separate thread. Methods that are called after analyzing the task will run independent
     * of the main thread. To run other methods after completion on the main thread, use the window's actionPerformed()
     * method to create a custom action event and handle it.
     *
     * @param task name that was executed
     */
    public void runTask(AssTask task) {
        // Override if needed
    }
}
