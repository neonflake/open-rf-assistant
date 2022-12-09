package hu.open.assistant.commons.graphical.notification;

import hu.open.assistant.commons.graphical.AssWindow;
import hu.open.assistant.commons.graphical.placer.ComponentPlacer;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionListener;

/**
 * Base abstract class for notifications used by all Assistant applications. The notification pop's up above the main
 * program window (in the center) and will remain in focus until it is closed. Static notification's event handling is
 * covered (such as closing the notification).
 */
public abstract class AssNotification extends JDialog implements ActionListener {

    protected ComponentPlacer placer;
    private final Notice notice;

    /**
     * Initialise base parameters for notification.
     *
     * @param notice       type of the notification
     * @param size         width and height of notification (pixels)
     * @param parentWindow application window for input related notification event handling
     */
    public AssNotification(Notice notice, Dimension size, AssWindow parentWindow) {
        super(parentWindow);
        this.notice = notice;
        setSize(size);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocation(calculateLocation(parentWindow));
        setResizable(false);
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        setContentPane(panel);
        setLayout(new GridBagLayout());
        placer = new ComponentPlacer(panel);
    }

    /**
     * Returns the type of the notification.
     */
    public Notice getNotice() {
        return notice;
    }

    private Point calculateLocation(AssWindow parentWindow) {
        Point parentLocation = parentWindow.getLocation();
        return new Point((int) (parentLocation.getX() + parentWindow.getSize().getWidth() / 2 - this.getSize().getWidth() / 2),
                (int) (parentLocation.getY() + parentWindow.getSize().getHeight() / 2 - this.getSize().getHeight() / 2));
    }

    /**
     * Return the notifications additional parameter.
     */
    public abstract String getValue();

    /**
     * Return the text typed in the notifications input field.
     */
    public abstract String getInput();

    /**
     * Return the text selected in the notifications combo box.
     */
    public abstract String getSelectedOption();

    /**
     * Change the text displayed on the notification.
     *
     * @param text to display
     */
    public abstract void changeText(String text);

    @Override
    public void dispose() {
        getOwner().setEnabled(true);
        super.dispose();
    }
}
