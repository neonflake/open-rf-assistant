package hu.open.assistant.commons.graphical;

import hu.open.assistant.commons.graphical.notification.AssNotification;
import hu.open.assistant.commons.graphical.notification.Notice;
import hu.open.assistant.commons.graphical.task.AssTask;
import hu.open.assistant.commons.graphical.task.CommonTaskName;
import hu.open.assistant.commons.graphical.task.RunComponent;
import hu.open.assistant.commons.graphical.task.TaskName;
import hu.open.assistant.commons.graphical.task.TaskType;

import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Timer;

/**
 * Base window for inheritance used by all Assistant applications. Implements all necessary listeners and provides
 * methods for running background threads (tasks). Notification handling and switch between fullscreen and windowed mode
 * is also included.
 */
public abstract class AssWindow extends JFrame implements RunComponent, ActionListener, ListSelectionListener, MouseListener, KeyListener, FocusListener, WindowFocusListener {

	protected Notifier notifier;
	protected AssNotification notification;
	protected Timer timer;
	protected int width;
	protected int height;

	/**
	 * Create the window with the given parameters. You need to call the setVisible() method in child constructor to
	 * display it and also initialise an application specific notifier.
	 *
	 * @param width  of the window in pixels
	 * @param height of the window in pixels
	 * @param title  text displayed by the window
	 */
	public AssWindow(int width, int height, String title) {
		this.width = width;
		this.height = height;
		addWindowFocusListener(this);
        setName("hu.open.assistant_program_window");
        setResizable(false);
        setSize(width, height);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) screenSize.getWidth() / 2) - (int) (this.getSize().getWidth() / 2),
                ((int) screenSize.getHeight() / 2) - (int) (this.getSize().getHeight() / 2));
        setTitle(title);
        timer = new Timer();
    }

    /**
     * Display an application specific notification from the notifier.
     *
     * @param notice type of the notification to display
     */
    public void showNotification(Notice notice) {
        notification = notifier.createNotification(notice);
    }

    /**
     * Display an application specific notification from the notifier, with an additional parameter.
     *
     * @param notice type of the notification to display
     * @param value  for text field or as an additional parameter
     */
    public void showNotification(Notice notice, String value) {
        notification = notifier.createNotification(notice, value);
    }

    /**
	 * Display an application specific notification from the notifier, with additional options.
	 *
	 * @param notice  type of the notification to display
	 * @param options for combo box
	 */
	public void showNotification(Notice notice, String[] options) {
		notification = notifier.createNotification(notice, options);
	}


	/**
	 * Display an application specific notification from the notifier, with an additional parameter and options.
	 *
	 * @param notice  type of the notification to display
	 * @param value   for text field or as an additional parameter
	 * @param options for combo box
	 */
	public void showNotification(Notice notice, String value, String[] options) {
		notification = notifier.createNotification(notice, value, options);
	}

	/**
	 * Close the notification if present.
	 */
	public void closeNotification() {
		if (notification != null) {
			notification.dispose();
        }
    }

    /**
     * Close a specific notification with a 2 sec delay.
     *
     * @param id number of the notification to close
	 */
	public void closeNotificationWithDelay(int id) {
		int delay = 2000;
		startTaskWithDelay(CommonTaskName.CLOSE_NOTIFICATION, TaskType.DELAYED_EVENT, String.valueOf(id), delay);
	}

	/**
	 * Change the text displayed on the active notification.
	 *
	 * @param text to display
	 */
	public void changeNotificationText(String text) {
		notification.changeText(text);
	}

	/**
	 * Switch between panels on the window.
	 *
	 * @param panel to display
	 */
	protected void changePanel(AssPanel panel) {
		setContentPane(panel);
		revalidate();
		repaint();
	}

	/**
	 * Start a task on a separate thread. Ideal for disk data processing when you need to keep the GUI responsive.
	 *
	 * @param taskName name of the task
	 * @param taskType type of the task
	 */
	protected void startTask(TaskName taskName, TaskType taskType) {
		startTaskWithDelay(taskName, taskType, 0);
	}

	/**
	 * Start a task on a separate thread with a stored parameter. Ideal for disk data processing when you need to keep
	 * the GUI responsive.
	 *
	 * @param taskName  name of the task
	 * @param taskType  type of the task
	 * @param parameter to store in task
	 */
	protected void startTask(TaskName taskName, TaskType taskType, String parameter) {
		startTaskWithDelay(taskName, taskType, parameter, 0);
	}

	/**
	 * Start a task on separate thread with delay. Ideal for GUI cleanup and revert tasks.
	 *
	 * @param taskName name of the task
	 * @param taskType type of the task
	 * @param delay    time to delay task in milliseconds
	 */
	protected void startTaskWithDelay(TaskName taskName, TaskType taskType, int delay) {
		AssTask task = new AssTask(this, taskName, taskType);
		timer.schedule(task, delay);
	}

	/**
	 * Start a task on separate thread with delay and with a stored parameter. Ideal for GUI cleanup and revert tasks.
	 *
	 * @param taskName  name of the task
	 * @param taskType  type of the task
	 * @param parameter to store in task
	 * @param delay     time to delay task in milliseconds
	 */
	protected void startTaskWithDelay(TaskName taskName, TaskType taskType, String parameter, int delay) {
		AssTask task = new AssTask(this, taskName, taskType, parameter);
		timer.schedule(task, delay);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			revalidate();
			repaint();
		}
	}

	@Override
	public void windowGainedFocus(WindowEvent event) {
		if (event.getOppositeWindow() == null) {
			if (notification != null) {
				if (notification.isVisible()) {
					notification.setVisible(true);
				}
			}
		}
		getContentPane().requestFocus();
	}

	@Override
	public void windowLostFocus(WindowEvent event) {
		// Override if needed
	}

	@Override
	public void focusLost(FocusEvent event) {
		// Override if needed
	}

	@Override
	public void focusGained(FocusEvent event) {
		// Override if needed
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		// Override if needed
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		// Override if needed
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// Override if needed
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// Override if needed
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		// Override if needed
	}

	@Override
	public void mouseExited(MouseEvent event) {
		// Override if needed
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// Override if needed
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// Override if needed
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// Override if needed
	}
}
