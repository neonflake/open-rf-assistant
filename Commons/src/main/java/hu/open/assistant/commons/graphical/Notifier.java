package hu.open.assistant.commons.graphical;

import hu.open.assistant.commons.graphical.notification.AssNotification;
import hu.open.assistant.commons.graphical.notification.Notice;

/**
 * Base class responsible for creating notifications for all Assistant applications with the createNotification method.
 * All applications must implement their own set of notifications.
 */
public abstract class Notifier {

    protected final AssWindow window;

    /**
     * Create the notifier.
     *
     * @param window parent window
     */
    public Notifier(AssWindow window) {
        this.window = window;
    }

    /**
     * Create a custom notification based on the name given.
     *
     * @param notice type of the notification to create
     * @return created notification
     */
    public AssNotification createNotification(Notice notice) {
        return createNotification(notice, "", new String[0]);
    }

    /**
     * Create a custom notification based on the name given, with an additional parameter.
     *
     * @param notice type of the notification to create
     * @param value  for text field or as an additional parameter
     * @return created notification
     */
    public AssNotification createNotification(Notice notice, String value) {
        return createNotification(notice, value, new String[0]);
    }

    /**
     * Create a custom notification based on the name given, with additional options.
     *
     * @param notice  type of the notification to create
     * @param options for combo box
     * @return created notification
     */
    public AssNotification createNotification(Notice notice, String[] options) {
        return createNotification(notice, "", options);
    }

    /**
     * Create a custom notification based on the name given, with an additional parameter and options.
     *
     * @param notice  type of the notification to create
     * @param value   for text field or as an additional parameter
     * @param options for combo box
     * @return created notification
     */
    public abstract AssNotification createNotification(Notice notice, String value, String[] options);
}
