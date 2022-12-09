package hu.open.assistant.update.graphical.panel;

import hu.open.assistant.update.graphical.UpdateNotice;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.update.UpdateAssistant;
import hu.open.assistant.update.graphical.UpdatePanel;
import hu.open.assistant.update.graphical.UpdateWindow;
import hu.open.assistant.update.graphical.renderer.UpdatePackageListRenderer;
import hu.open.assistant.update.model.UpdatePackage;

import java.awt.Dimension;

/**
 * GUI for version control. The list is filled with update packages (for the detected target). When an element is
 * selected text area is updated with version information. After an update the panel will also show the update packages
 * changelog in the same text area.
 */
public class ShowUpdate extends UpdatePanel {

	private static final Dimension BUTTON_DIMENSION = new Dimension(250, 50);
	private static final Dimension LABEL_DIMENSION = new Dimension(600, 50);
	private static final Dimension TEXT_AREA_DIMENSION = new Dimension(600, 280);
	private static final Dimension LIST_DIMENSION = new Dimension(600, 200);
	private static final int LARGE_TEXT_SIZE = 24;
	private static final int MEDIUM_TEXT_SIZE = 20;
	private static final int SMALL_TEXT_SIZE = 14;

	private final AssTextArea infoArea;
	private final AssList<UpdatePackage> packageList;
	private final AssButton changeButton;
	private UpdatePackage selectedPackage;

	public ShowUpdate(UpdateWindow window, UpdateAssistant assistant) {
		super(window, assistant, "ShowUpdate");
		//placer.enableDebug();
		AssLabel titleLabel = new AssLabel("Frissítések keresése és kezelése", LARGE_TEXT_SIZE, LABEL_DIMENSION);
		AssLabel versionLabel = new AssLabel("Elérhető program verziók:", MEDIUM_TEXT_SIZE, LABEL_DIMENSION);
		versionLabel.alignLeft();
		AssLabel infoLabel = new AssLabel("Információ:", MEDIUM_TEXT_SIZE, LABEL_DIMENSION);
		infoLabel.alignLeft();
		infoArea = new AssTextArea("ShowUpdate logArea", SMALL_TEXT_SIZE, TEXT_AREA_DIMENSION, true);
		packageList = new AssList<>("ShowUpdate packageList", SMALL_TEXT_SIZE, LIST_DIMENSION, window, new UpdatePackageListRenderer());
		changeButton = new AssButton("ShowUpdate changeButton", "Váltás", SMALL_TEXT_SIZE, BUTTON_DIMENSION, window, false);
		AssButton backButton = new AssButton("ShowUpdate backButton", "Vissza a főprogramba", SMALL_TEXT_SIZE, BUTTON_DIMENSION, window, true);
		AssButton exitButton = new AssButton("ShowUpdate exitButton", "Kilépés", SMALL_TEXT_SIZE, BUTTON_DIMENSION, window, true);
		infoArea.setEditable(false);
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addComponent(changeButton, 4, 1, 1, 1);
		placer.addComponent(versionLabel, 1, 2, 3, 1);
		placer.addEmptyComponent(BUTTON_DIMENSION, 4, 2, 1, 1);
		placer.addComponent(packageList, 1, 3, 3, 3);
		placer.addEmptyComponent(BUTTON_DIMENSION, 4, 3, 1, 1);
		placer.addEmptyComponent(BUTTON_DIMENSION, 4, 4, 1, 1);
		placer.addEmptyComponent(BUTTON_DIMENSION, 4, 5, 1, 1);
		placer.addComponent(infoLabel, 1, 6, 3, 1);
		placer.addComponent(infoArea, 1, 7, 3, 4);
		placer.addEmptyComponent(BUTTON_DIMENSION, 4, 6, 1, 1);
		placer.addEmptyComponent(BUTTON_DIMENSION, 4, 7, 1, 1);
		placer.addComponent(backButton, 4, 8, 1, 1);
		placer.addComponent(exitButton, 4, 9, 1, 1);
		placer.addImageComponent(logoImage, 4, 10, 1, 1);
	}

	private void refreshLog() {
		infoArea.setText(TextHelper.stringListToLineBrokenString(assistant.readChangeLog()));
		infoArea.setCaretPosition(0);
	}

	private void refreshVersionInfo() {
		double currentVersion = assistant.getCurrentVersion();
		if (currentVersion > 0) {
			String text = "Kezelendő alkalmazás: "
					.concat(assistant.getApplicationName())
					.concat("\n\nJelenlegi verziószám: ")
					.concat(String.valueOf(currentVersion)
							.concat("\n"));
			infoArea.setText(text);
		}
	}

	public void searchForUpdate() {
		refreshVersionInfo();
		assistant.searchForUpdate();
		updateVersionList();
		if (assistant.isUpdateAvailable()) {
			if (assistant.isForceUpdate()) {
				startUpdate();
			} else {
                window.showNotification(UpdateNotice.UPDATE_FOUND);
			}
		}
	}

	public void setSelectedPackage() {
		selectedPackage = packageList.getSelectedValue();
		double currentVersion = assistant.getCurrentVersion();
		double targetVersion = selectedPackage.getVersion();
		boolean versionsMatched = currentVersion == targetVersion;
		String text = "Kezelendő alkalmazás: " + assistant.getApplicationName();
		if (versionsMatched) {
			text = text + "\n\nA kijelölt verzió egyezik a jelenlegivel!";
		} else {
			text = text + "\n\nVerzió váltása: " + currentVersion + " -> " + targetVersion;
		}
		infoArea.setText(text);
		changeButton.setEnabled(!versionsMatched);
	}

	public void startUpdate() {
		if (assistant.startUpdate(selectedPackage)) {
            refreshLog();
            window.showNotification(UpdateNotice.UPDATE_SUCCESS);
		}
	}

	private void updateVersionList() {
		packageList.changeModel(assistant.getAvailablePackages(), false);
		if (packageList.getModelSize() > 0) {
			selectedPackage = packageList.getElement(0);
		}
	}
}
