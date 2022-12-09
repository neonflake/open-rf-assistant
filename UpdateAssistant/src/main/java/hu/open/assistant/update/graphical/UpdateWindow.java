package hu.open.assistant.update.graphical;

import hu.open.assistant.update.UpdateAssistant;
import hu.open.assistant.update.graphical.panel.ShowUpdate;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.AssWindow;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.task.AssTask;
import hu.open.assistant.commons.util.TextHelper;

import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Graphical controller of the application as well as the actual program window. It holds a panel which contains the
 * GUI elements. Most of the user inputs are handled here and are distributed back to the panel. The panel and data
 * controller have access to the window to show notifications.
 */
public class UpdateWindow extends AssWindow {

	private static final String WINDOW_TITLE = "Open Update Assistant v";
	private static final int WINDOW_WIDTH = 960;
	private static final int WINDOW_HEIGHT = 800;

	private final ShowUpdate showUpdate;
	private final UpdateAssistant assistant;

	public UpdateWindow(UpdateAssistant assistant) {
		super(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE + assistant.getVersion());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.assistant = assistant;
		showUpdate = new ShowUpdate(this, assistant);
		notifier = new UpdateNotifier(this);
	}

	public void startWindow() {
		showUpdate();
		setVisible(true);
		if (notification != null) {
			notification.setVisible(true);
		}
	}

	// Notifications

	public void notificationButtonPress(String sourceName) {
		closeNotification();

		// Yes button

		if (sourceName.equals("AppNotification yesButton")) {
			if (notification.getNotice() == UpdateNotice.UPDATE_FOUND) {
                showUpdate.startUpdate();
            }

			// Ok button

		} else if (sourceName.equals("AppNotification okButton")) {
            if (notification.getNotice() == UpdateNotice.UPDATE_SUCCESS) {
                assistant.launchTarget();
                System.exit(0);
            } else if (notification.getNotice() == UpdateNotice.NETWORK_FOLDER_PROBLEM ||
                    notification.getNotice() == UpdateNotice.DETECTION_PROBLEM) {
                System.exit(0);
            }
		}
	}

	// Panel methods

	// ShowUpdate

	private void showUpdate() {
		changePanel(showUpdate);
		showUpdate.searchForUpdate();
	}

	private void showUpdateButtonPress(String name) {
		switch (name) {
			case ("ShowUpdate exitButton"):
				System.exit(0);
				break;
			case ("ShowUpdate backButton"):
				assistant.launchTarget();
				System.exit(0);
			case ("ShowUpdate changeButton"):
				showUpdate.startUpdate();
				break;
		}
	}

	// Event handling

	@Override
	public void actionPerformed(ActionEvent event) {

		Component sourceComponent = (Component) event.getSource();
		String componentName = sourceComponent.getName();

		// AppButton handling

		if (sourceComponent instanceof AssButton) {
			switch (TextHelper.getFirstWord(componentName)) {
				case "AppNotification":
					notificationButtonPress(componentName);
					break;
				case "ShowUpdate":
					showUpdateButtonPress(componentName);
					break;
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent listEvent) {
		if (!listEvent.getValueIsAdjusting()) {
			AssList<?> sourceComponent = (AssList<?>) listEvent.getSource();
			if (sourceComponent.getName().equals("ShowUpdate packageList")) {
				showUpdate.setSelectedPackage();
			}
		}
	}

	@Override
	public void runTask(AssTask task) {
		// Implement if needed
	}
}
