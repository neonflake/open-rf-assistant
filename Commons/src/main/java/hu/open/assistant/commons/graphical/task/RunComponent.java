package hu.open.assistant.commons.graphical.task;

/**
 * Interface that signals that a graphical component can run tasks on a separate thread.
 */
public interface RunComponent {

    /**
     * Receive a task running on a separate thread. Methods that are called after analyzing the task parameters will run
     * independent of the main thread. To return execution to the main thread, use the window's actionPerformed() method
     * to create a custom action event and handle it.
     *
     * @param task running on another thread
     */
    void runTask(AssTask task);
}
