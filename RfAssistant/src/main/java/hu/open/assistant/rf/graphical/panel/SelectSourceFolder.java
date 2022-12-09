package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.TesterType;

import java.awt.Dimension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GUI for selecting source folders for processing. It fills the first list with folders detected in the report folder
 * from the current and previous year. There is an option to switch between CMU and CMW report folders. Folders can be
 * moved to the other list so that they can be marked for processing (multi selection is allowed). There is an option
 * to change the processing limit value, re-read the detected folders or clear the entire selection.
 */
public class SelectSourceFolder extends RfPanel {

	private static final Dimension LABEL_DIMENSION = new Dimension(300, 50);
	private static final Dimension LIST_DIMENSION = new Dimension(300, 560);
	private static final int SMALL_TEXT_SIZE = 14;
	private static final int LARGE_TEXT_SIZE = 20;
	private static final int PASSABLE_LIMIT_MAX = 20;
	private static final int PASSABLE_LIMIT_STEP = 5;

	private final AssList<String> sourceFolderList;
	private final AssList<String> selectedFolderList;
	private final AssButton addButton;
	private final AssButton removeButton;
	private final AssButton limitButton;
	private final AssButton clearButton;
	private final AssButton useButton;
	private final AssButton cmuButton;
	private final AssButton cmwButton;
	private final AssLabel selectedLabel;
	private final AssLabel availableLabel;
	private List<String> sourceFolders;
	private List<String> selectedFolders;
	private boolean rebuildNeeded;
	private int passableLimit;
	private TesterType testerType;

	public SelectSourceFolder(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "SelectSourceFolder");
		//placer.enableDebug();
		selectedFolders = new ArrayList<>();
		sourceFolders = new ArrayList<>();
		rebuildNeeded = false;
		passableLimit = 5;
		AssLabel titleLabel = new AssLabel("Forrás mappák kiválasztása", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
		availableLabel = new AssLabel("", LARGE_TEXT_SIZE, LABEL_DIMENSION);
		sourceFolderList = new AssList<>("SelectSourceFolder sourceFolderList", SMALL_TEXT_SIZE, LIST_DIMENSION, listener, null);
		sourceFolderList.enableMultiSelect();
		sourceFolderList.enableMouseListening();
		addButton = new AssButton("SelectSourceFolder addButton", "Mappa hozzáadása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		removeButton = new AssButton("SelectSourceFolder removeButton", "Mappa eltávolítása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		clearButton = new AssButton("SelectSourceFolder clearButton", "Kiválasztás törlése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		useButton = new AssButton("SelectSourceFolder useButton", "Feldolgozásra kijelöl", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton rescanButton = new AssButton("SelectSourceFolder rescanButton", "Mappák újra pásztázása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		limitButton = new AssButton("SelectSourceFolder limitButton", "Határéték felett limit: " + passableLimit, SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		cmuButton = new AssButton("SelectSourceFolder cmuButton", "CMU források", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		cmwButton = new AssButton("SelectSourceFolder cmwButton", "CMW források", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton backButton = new AssButton("SelectSourceFolder backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		selectedLabel = new AssLabel("Kiválasztott mappák (0): ", LARGE_TEXT_SIZE, LABEL_DIMENSION);
		selectedFolderList = new AssList<>("SelectSourceFolder selectedFolderList", SMALL_TEXT_SIZE, LIST_DIMENSION, listener, null);
		selectedFolderList.enableMultiSelect();
		selectedFolderList.enableMouseListening();
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addComponent(addButton, 4, 1, 1, 1);
		placer.addComponent(availableLabel, 1, 2, 1, 1);
		placer.addComponent(selectedLabel, 2, 2, 2, 1);
		placer.addComponent(removeButton, 4, 2, 1, 1);
		placer.addComponent(sourceFolderList, 1, 3, 1, 8);
		placer.addComponent(selectedFolderList, 2, 3, 2, 8);
		placer.addComponent(useButton, 4, 3, 1, 1);
		placer.addComponent(limitButton, 4, 4, 1, 1);
		placer.addComponent(clearButton, 4, 5, 1, 1);
		placer.addComponent(rescanButton, 4, 6, 1, 1);
		placer.addComponent(cmuButton, 4, 7, 1, 1);
		placer.addComponent(cmwButton, 4, 8, 1, 1);
		placer.addComponent(backButton, 4, 9, 1, 1);
		placer.addImageComponent(logoImage, 4, 10, 1, 1);
		changeTesterType(assistant.getLocalConfig().getDefaultSource().equals("cmu") ? TesterType.CMU : TesterType.CMW);
	}

	public TesterType getTesterType() {
		return testerType;
	}

	public void changeTesterType(TesterType testerType) {
		this.testerType = testerType;
		if (testerType == TesterType.CMW) {
			cmuButton.setEnabled(true);
			cmwButton.setEnabled(false);
			availableLabel.setText("Elérhető CMW mappák:");
		} else {
			cmuButton.setEnabled(false);
			cmwButton.setEnabled(true);
			availableLabel.setText("Elérhető CMU mappák:");
		}
		clearLists();
	}

	public boolean isRebuildNeeded() {
		return rebuildNeeded;
	}

	public void setRebuildNeeded(boolean rebuildNeeded) {
		this.rebuildNeeded = rebuildNeeded;
	}

	public List<String> getSelectedFolders() {
		return selectedFolders;
	}

	public int getPassableLimit() {
		return passableLimit;
	}

	public boolean isScanned() {
		return sourceFolders.size() == 0;
	}

	public void useSelection() {
		selectedFolders = selectedFolderList.getModelAsArrayList();
		this.rebuildNeeded = true;
	}

	public ArrayList<String> getGeneratedSelection(TesterType testerType, int interval) {
		ArrayList<String> folders = new ArrayList<>();
		String folder;
		for (int i = 0; i < interval; i++) {
			if (testerType == TesterType.CMU) {
				folder = assistant.localDateTimeToCmuFolder(LocalDateTime.now().minusDays(i));
			} else {
				folder = DateHelper.localDateTimeToIsoTextDate(LocalDateTime.now().minusDays(i));
			}
			folders.add(folder);
		}
		return folders;
	}

	public void enableAdding() {
		addButton.setEnabled(true);
		removeButton.setEnabled(false);
		selectedFolderList.clearSelection();
	}

	public void enableRemoving() {
		addButton.setEnabled(false);
		removeButton.setEnabled(true);
		sourceFolderList.clearSelection();
	}

	public void changePassableLimit() {
		if (passableLimit < PASSABLE_LIMIT_MAX) {
			passableLimit += PASSABLE_LIMIT_STEP;
		} else {
			passableLimit = 0;
		}
		limitButton.setText("Határérték felett limit: " + passableLimit);
	}

	public void addSelection() {
		List<?> list = sourceFolderList.getSelectedValuesList();
		for (Object object : list) {
			String folder = (String)object;
			selectedFolderList.addElement(0, folder);
			sourceFolderList.removeElement(folder);
		}
		sortSelectedFolderList();
		addButton.setEnabled(false);
		clearButton.setEnabled(true);
		useButton.setEnabled(true);
		selectedLabel.setText("Kiválasztott mappák ("+selectedFolderList.getModelSize()+"): ");
	}

	public void removeSelection() {
		List<?> list = selectedFolderList.getSelectedValuesList();
		for (Object object : list) {
			String folder = (String)object;
			sourceFolderList.addElement(0, folder);
			selectedFolderList.removeElement(folder);
		}
		sortSourceFolderList();
		removeButton.setEnabled(false);
		if (selectedFolderList.getModelSize()==0) {
			clearButton.setEnabled(false);
			useButton.setEnabled(false);
		}
		selectedLabel.setText("Kiválasztott mappák ("+selectedFolderList.getModelSize()+"): ");
	}

	public void clearLists() {
		sourceFolderList.changeModel(sourceFolders, true);
		selectedFolderList.clearModel();
		selectedLabel.setText("Kiválasztott mappák ("+selectedFolderList.getModelSize()+"): ");
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
		clearButton.setEnabled(false);
		useButton.setEnabled(false);
	}

	private void sortSourceFolderList() {
		List<String> tempList = sourceFolderList.getModelAsArrayList();
		Collections.sort(tempList);
		sourceFolderList.changeModel(tempList, true);
	}

	private void sortSelectedFolderList() {
		List<String> tempList = selectedFolderList.getModelAsArrayList();
		Collections.sort(tempList);
		selectedFolderList.changeModel(tempList, true);
	}

	public void prepareSourceFolders(){
		placer.disableComponents();
		selectedFolderList.clearModel();
		sourceFolderList.clearModel();
		selectedLabel.setText("Kiválasztott mappák ("+selectedFolderList.getModelSize()+"): ");
		if (testerType == TesterType.CMW) {
			sourceFolders = assistant.readCmwSourceFolders();
		} else {
			sourceFolders = assistant.readCmuSourceFolders();
		}
		Collections.sort(sourceFolders);
		sourceFolderList.changeModel(sourceFolders,true);
		placer.enableComponents();
	}
}
