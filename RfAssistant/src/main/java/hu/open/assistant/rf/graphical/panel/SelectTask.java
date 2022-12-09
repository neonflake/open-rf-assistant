package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.TesterType;

/**
 * GUI for selecting different tasks as well as the applications start screen. There are different buttons for operator
 * and support related tasks. The panel switches between different button arrangements.
 */
public class SelectTask extends RfPanel {

	private final AssButton backButton;
	private final AssLabel titleLabel;
	private AssButton reportBatchButton;
	private AssButton profileUsageButton;
	private AssButton supportButton;
	private AssButton operatorButton;
	private AssButton configButton;
	private AssButton updateButton;
	private AssButton manualButton;
	private String mode;

	public SelectTask(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "SelectTask");
		//placer.enableDebug();
		AssImage image = new AssImage(getClass().getResource("/images/background.png"));
		titleLabel = new AssLabel("", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
		backButton = new AssButton("SelectTask backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addImageComponent(image, 1, 3, 3, 8);
		placer.addComponent(backButton, 4, 9, 1, 1);
		placer.addImageComponent(logoImage, 4, 10, 1, 1);
		mode = "";
		setMode("select");
	}

	public void disableTasks() {
		supportButton.setEnabled(false);
		operatorButton.setEnabled(false);
		configButton.setEnabled(false);
		updateButton.setEnabled(false);
		manualButton.setEnabled(false);
	}

	public void disableButtons() {
		placer.disableComponents();
	}

	public void enableButtons() {
		placer.enableComponents();
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String targetMode) {
		switch (targetMode) {
			case "select":
				switch (mode) {
					case "operator":
						removeOperatorButtons();
						break;
					case "support":
						removeSupportButtons();
						break;
				}
				showSelectButtons();
				break;
			case "operator":
				switch (mode) {
					case "select":
						removeSelectButtons();
						break;
					case "support":
						removeSupportButtons();
						break;
				}
				showOperatorButtons();
				break;
			case "support":
				switch (mode) {
					case "select":
						removeSelectButtons();
						break;
					case "operator":
						removeOperatorButtons();
						break;
				}
				showSupportButtons();
				break;
		}
	}

	private void removeSupportButtons() {
		placer.removeComponent("SelectTask sourceButton");
		placer.removeComponent("SelectTask reportBatchButton");
		placer.removeComponent("SelectTask cmuProfileButton");
		placer.removeComponent("SelectTask cmwProfileButton");
		placer.removeComponent("SelectTask syncProfileButton");
		placer.removeComponent("SelectTask profileUsageButton");
		placer.removeComponent("SelectTask equipmentListButton");
		placer.removeComponent("SelectTask backupButton");
	}

	private void removeSelectButtons() {
		placer.removeComponent("SelectTask operatorButton");
		placer.removeComponent("SelectTask supportButton");
		placer.removeComponent("SelectTask manualButton");
		placer.removeComponent("SelectTask configButton");
		placer.removeComponent("SelectTask updateButton");
		placer.removeComponent("filler_1");
		placer.removeComponent("filler_2");
		placer.removeComponent("filler_3");
	}

	private void removeOperatorButtons() {
		placer.removeComponent("SelectTask equipmentInfoButton");
		placer.removeComponent("SelectTask reportListButton");
		placer.removeComponent("SelectTask reportSearchButton");
		placer.removeComponent("SelectTask logButton");
		placer.removeComponent("filler_1");
		placer.removeComponent("filler_2");
		placer.removeComponent("filler_3");
		placer.removeComponent("filler_4");
	}

	private void showSupportButtons() {
		mode = "support";
		titleLabel.setText("Karbantartói feladatok");
		backButton.setText("Vissza");
		AssButton sourceButton = new AssButton("SelectTask sourceButton", "Forrás mappák", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		reportBatchButton = new AssButton("SelectTask reportBatchButton", "Mérések összesítése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton cmuProfileButton = new AssButton("SelectTask cmuProfileButton", "CMU profilok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton cmwProfileButton = new AssButton("SelectTask cmwProfileButton", "CMW profilok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton syncProfileButton = new AssButton("SelectTask syncProfileButton", "CMU profil szinkron", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		profileUsageButton = new AssButton("SelectTask profileUsageButton", "CMU profil kihasználtság", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton equipmentListButton = new AssButton("SelectTask equipmentListButton", "Készülékek szerkesztése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton backupButton = new AssButton("SelectTask backupButton", "Biztonsági mentések", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(sourceButton, 4, 1, 1, 1);
		placer.addComponent(reportBatchButton, 4, 2, 1, 1);
		placer.addComponent(profileUsageButton, 4, 3, 1, 1);
		placer.addComponent(cmuProfileButton, 4, 4, 1, 1);
		placer.addComponent(syncProfileButton, 4, 5, 1, 1);
		placer.addComponent(cmwProfileButton, 4, 6, 1, 1);
		placer.addComponent(equipmentListButton, 4, 7, 1, 1);
		placer.addComponent(backupButton, 4, 8, 1, 1);
	}

	private void showOperatorButtons() {
		mode = "operator";
		titleLabel.setText("Operátori feladatok");
		backButton.setText("Vissza");
		AssButton equipmentInfoButton = new AssButton("SelectTask equipmentInfoButton", "Készülék információ", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton reportListButton = new AssButton("SelectTask reportListButton", "Mai riportok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton reportSearchButton = new AssButton("SelectTask reportSearchButton", "Riport keresés", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton logButton = new AssButton("SelectTask logButton", "Profil és adatbázis történet", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(equipmentInfoButton, 4, 1, 1, 1);
		placer.addComponent(reportListButton, 4, 2, 1, 1);
		placer.addComponent(reportSearchButton, 4, 3, 1, 1);
		placer.addComponent(logButton, 4, 4, 1, 1);
		placer.addEmptyComponent("filler_1", SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
		placer.addEmptyComponent("filler_2", SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
		placer.addEmptyComponent("filler_3", SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
		placer.addEmptyComponent("filler_4", SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
	}

	private void showSelectButtons() {
		mode = "select";
		titleLabel.setText("Feladatok kiválasztása");
		backButton.setText("Kilépés");
		operatorButton = new AssButton("SelectTask operatorButton", "Operátori feladatok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		supportButton = new AssButton("SelectTask supportButton", "Karbantartói feladatok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		manualButton = new AssButton("SelectTask manualButton", "Felhasználói dokumentáció", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		configButton = new AssButton("SelectTask configButton", "Beállítások", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		updateButton = new AssButton("SelectTask updateButton", "Frissítések keresése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(operatorButton, 4, 1, 1, 1);
		placer.addComponent(supportButton, 4, 2, 1, 1);
		placer.addComponent(configButton, 4, 3, 1, 1);
		placer.addEmptyComponent("filler_1", SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
		placer.addComponent(manualButton, 4, 5, 1, 1);
		placer.addComponent(updateButton, 4, 6, 1, 1);
		placer.addEmptyComponent("filler_2", SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
		placer.addEmptyComponent("filler_3", SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
	}

	public void enableReportBatchButton() {
		reportBatchButton.setEnabled(true);
	}

	public void enableProfileUsageButton() {
		profileUsageButton.setEnabled(true);
	}

	public void disableProfileUsageButton() {
		profileUsageButton.setEnabled(false);
	}

	public void setSourceNumber(int number, TesterType testerType) {
		if (number > 0) {
			reportBatchButton.setText("Mérések összesítése: " + number + " " + testerType);
		} else {
			reportBatchButton.setText("Mérések megtekintése");
		}
	}
}
