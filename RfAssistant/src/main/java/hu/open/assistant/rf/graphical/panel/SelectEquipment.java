package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.renderer.EquipmentListRenderer;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.AssColor;
import hu.open.assistant.commons.graphical.gui.AssComboBox;
import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssList;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.filter.EquipmentFilter;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.Equipment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for viewing equipment information. A list is filled with equipments and is not refreshed until the refresh button
 * is pressed (even after switching between panels). The list can be narrowed with a search field and a dropdown menu
 * which contains the manufacturers. Between the search field and the list elements key navigation is available. By
 * selecting an equipment the equipment information is shown with labels and information in the text area. Depending on
 * what kind of RF profiles exists for the equipment, the CMU or CMW position information (with illustrations) becomes
 * visible. The illustrations react to mouse input and will show an enlarged image in a notification.
 */
public class SelectEquipment extends RfPanel {

    private static final Dimension LABEL_DIMENSION = new Dimension(150, 50);
    private static final Dimension NAME_LABEL_DIMENSION = new Dimension(350, 50);
    private static final Dimension IMAGE_LABEL_DIMENSION = new Dimension(120, 120);
    private static final Dimension INFO_AREA_DIMENSION = new Dimension(350, 190);
    private static final Dimension EQUIPMENT_LIST_DIMENSION = new Dimension(250, 480);
    private static final Dimension MANUFACTURER_BOX_DIMENSION = new Dimension(250, 50);
    private static final Dimension SEARCH_FIELD_DIMENSION = new Dimension(250, 50);
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int MEDIUM_TEXT_SIZE = 16;
    private static final int LARGE_TEXT_SIZE = 18;
    private static final int HUGE_TEXT_SIZE = 24;
    private static final Color AREA_NORMAL_COLOR = Color.white;
    private static final Color AREA_HIGHLIGHT_COLOR = new Color(250, 250, 190);

    private final AssButton graphButton;
    private final AssButton searchButton;
    private final AssLabel nameLabel;
    private final AssLabel cmuPositionLabel;
    private final AssLabel cmuPositionValueLabel;
    private final AssLabel cmwPositionLabel;
    private final AssLabel cmwPositionValueLabel;
    private final AssLabel handlingValueLabel;
    private final AssTextField searchField;
    private final AssTextArea infoArea;
    private final AssList<Equipment> equipmentList;
    private final AssLabel cmuPositionImageLabel;
    private final AssLabel cmwPositionImageLabel;
    private final String imageFolder;
    private List<Equipment> equipments = new ArrayList<>();
    private AssComboBox manufacturerBox;
    private Equipment selectedEquipment;
    private boolean refreshNeeded;

    public SelectEquipment(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "SelectEquipment");
        //placer.enableDebug();
        AssLabel label = new AssLabel("Készülék információ", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        nameLabel = new AssLabel("", LARGE_TEXT_SIZE, NAME_LABEL_DIMENSION);
        cmuPositionLabel = new AssLabel("", MEDIUM_TEXT_SIZE, LABEL_DIMENSION);
        cmuPositionValueLabel = new AssLabel("", HUGE_TEXT_SIZE, LABEL_DIMENSION);
        cmuPositionValueLabel.setName("cmuPositionValueLabel");
        cmwPositionLabel = new AssLabel("", MEDIUM_TEXT_SIZE, LABEL_DIMENSION);
        cmwPositionValueLabel = new AssLabel("", HUGE_TEXT_SIZE, LABEL_DIMENSION);
        cmwPositionValueLabel.setName("cmwPositionValueLabel");
        cmuPositionValueLabel.addMouseListener((MouseListener) listener);
        cmwPositionValueLabel.addMouseListener((MouseListener) listener);
        handlingValueLabel = new AssLabel("", MEDIUM_TEXT_SIZE, LABEL_DIMENSION);
        AssLabel handlingLabel = new AssLabel("Készülék kezelése:", MEDIUM_TEXT_SIZE, LABEL_DIMENSION);
        graphButton = new AssButton("SelectEquipment graphButton", "Pontgrafikon", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        searchButton = new AssButton("SelectEquipment searchButton", "Riportok", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton refreshButton = new AssButton("SelectEquipment refreshButton", "Adatok frissítése", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        AssButton backButton = new AssButton("SelectEquipment backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        searchField = new AssTextField("SelectEquipment searchField", SEARCH_FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
        searchField.addKeyListener(window);
        searchField.addFocusListener(window);
        infoArea = new AssTextArea("SelectEquipment infoArea", SMALL_TEXT_SIZE, INFO_AREA_DIMENSION, true);
        equipmentList = new AssList<>("SelectEquipment equipmentList", SMALL_TEXT_SIZE, EQUIPMENT_LIST_DIMENSION, listener, new EquipmentListRenderer());
        equipmentList.addKeyListener(window);
        cmuPositionImageLabel = new AssLabel("cmuPositionImageLabel", "", HUGE_TEXT_SIZE, IMAGE_LABEL_DIMENSION);
        cmuPositionImageLabel.addMouseListener((MouseListener) listener);
        cmwPositionImageLabel = new AssLabel("cmwPositionImageLabel", "", HUGE_TEXT_SIZE, IMAGE_LABEL_DIMENSION);
        cmwPositionImageLabel.addMouseListener((MouseListener) listener);
        manufacturerBox = new AssComboBox("SelectEquipment manufacturerBox", new String[0], SMALL_TEXT_SIZE, MANUFACTURER_BOX_DIMENSION, listener);
        placer.addComponent(label, 1, 1, 3, 1);
        placer.addComponent(graphButton, 4, 1, 1, 1);
        placer.addComponent(searchField, 1, 2, 1, 1);
        placer.addComponent(nameLabel, 2, 2, 2, 1);
        placer.addComponent(searchButton, 4, 2, 1, 1);
        placer.addComponent(manufacturerBox, 1, 3, 1, 1);
        placer.addComponent(cmuPositionLabel, 2, 3, 1, 1);
        placer.addComponent(cmuPositionImageLabel, 3, 3, 1, 2);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 3, 1, 1);
        placer.addComponent(equipmentList, 1, 4, 1, 7);
        placer.addComponent(cmuPositionValueLabel, 2, 4, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addComponent(cmwPositionLabel, 2, 5, 1, 1);
        placer.addComponent(cmwPositionImageLabel, 3, 5, 1, 2);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addComponent(cmwPositionValueLabel, 2, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addComponent(handlingLabel, 2, 7, 1, 1);
        placer.addComponent(handlingValueLabel, 3, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addComponent(infoArea, 2, 8, 2, 3);
        placer.addComponent(refreshButton, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
        imageFolder = assistant.getLocalConfig().getNetworkFolder() + "\\Data\\Positions";
        refreshNeeded = true;
    }

    public boolean isRefreshNeeded() {
        return refreshNeeded;
    }

    public void refreshEquipments() {
        refreshNeeded = true;
        placer.removeComponent("SelectEquipment manufacturerBox");
        manufacturerBox = new AssComboBox("SelectEquipment manufacturerBox", new String[0], SMALL_TEXT_SIZE, MANUFACTURER_BOX_DIMENSION, listener);
        placer.addComponent(manufacturerBox, 1, 3, 1, 1);
    }

    public void focusSearchField() {
        searchField.requestFocus();
    }

    public void focusEquipmentList() {
        equipmentList.requestFocus();
    }

    public void selectSearchText() {
        String line = searchField.getText();
        if (!line.isEmpty()) {
            searchField.select(0, line.length());
        }
    }

    public String[] getFilterOptions() {
        return assistant.readDatabaseNames(null).toArray(new String[0]);
    }

    public Equipment getSelectedEquipment() {
        return selectedEquipment;
    }

    public String getCmuImage() {
        String image;
        if (selectedEquipment.getCmuPositionImage().equals("auto")) {
            image = selectedEquipment.getCmuPositionAutoImage();
        } else {
            image = selectedEquipment.getCmuPositionImage();
        }
        return imageFolder + "\\" + image;
    }

    public String getCmwImage() {
        String image;
        if (selectedEquipment.getCmwPositionImage().contains("auto")) {
            image = selectedEquipment.getCmwPositionAutoImage();
        } else {
            image = selectedEquipment.getCmwPositionImage();
        }
        return imageFolder + "\\" + image;
    }

    private void selectFirstEquipment() {
        if (equipmentList.getModelSize() > 0) {
            equipmentList.setSelectedIndex(0);
            setSelectedEquipment(equipmentList.getElement(0));
        }
    }

    public void setSelectedEquipment(Equipment equipment) {
        if (equipment == null) {
            selectedEquipment = equipmentList.getSelectedValue();
        } else {
            selectedEquipment = equipment;
        }
        if (selectedEquipment != null) {
            enableEquipmentControls();
            nameLabel.setText(selectedEquipment.getName());
            if (!selectedEquipment.getCmuBox().isBlank()) {
                cmuPositionLabel.setText("CMU pozíció:");
                cmuPositionValueLabel.setText(selectedEquipment.getCmuPosition() + " " + selectedEquipment.getCmuPositionDetail());
                if (assistant.getFileHandler().fileExists(getCmuImage() + ".png")) {
                    cmuPositionImageLabel.setIcon(new AssImage(getCmuImage() + ".png").getResizedImage(IMAGE_LABEL_DIMENSION.width, IMAGE_LABEL_DIMENSION.height));
                } else {
                    cmuPositionImageLabel.setIcon(null);
                }

            } else {
                cmuPositionLabel.setText("");
                cmuPositionValueLabel.setText("");
                cmuPositionImageLabel.setIcon(null);
            }
            if (!selectedEquipment.getCmwBox().isBlank()) {
                cmwPositionLabel.setText("CMW pozíció:");
                cmwPositionValueLabel.setText(selectedEquipment.getCmwPosition() + " " + selectedEquipment.getCmwPositionDetail());
                if (assistant.getFileHandler().fileExists(getCmwImage() + ".png")) {
                    cmwPositionImageLabel.setIcon(new AssImage(getCmwImage() + ".png").getResizedImage(IMAGE_LABEL_DIMENSION.width, IMAGE_LABEL_DIMENSION.height));
                } else {
                    cmwPositionImageLabel.setIcon(null);
                }
            } else {
                cmwPositionLabel.setText("");
                cmwPositionValueLabel.setText("");
                cmwPositionImageLabel.setIcon(null);
            }
            infoArea.setText(selectedEquipment.getInfo());
            infoArea.setCaretPosition(0);
            if (selectedEquipment.getUsage().equals("normális")) {
                handlingValueLabel.setText(selectedEquipment.getUsage() + "                  ");
                handlingValueLabel.changeColor(AssColor.LABEL_DARK_GREEN);
                infoArea.setBackground(AREA_NORMAL_COLOR);
            } else if (selectedEquipment.getUsage().equals("problémás")) {
                handlingValueLabel.setText(selectedEquipment.getUsage() + "                 ");
                handlingValueLabel.changeColor(AssColor.LABEL_DARK_RED);
                infoArea.setBackground(AREA_HIGHLIGHT_COLOR);
            }
        }
    }

    public void disableControlsForGraph() {
        placer.disableComponents();
    }

    public void enableControlsForGraph(){
        placer.enableComponents();
    }

    private void disableEquipmentControls() {
        searchButton.setEnabled(false);
        graphButton.setEnabled(false);
    }

    public void enableEquipmentControls() {
        searchButton.setEnabled(true);
        graphButton.setEnabled(true);
    }

    private void clearEquipment() {
        handlingValueLabel.setText("");
        infoArea.setText("");
        nameLabel.setText("");
        cmuPositionLabel.setText("");
        cmuPositionValueLabel.setText("");
        cmuPositionImageLabel.setIcon(null);
        cmwPositionLabel.setText("");
        cmwPositionValueLabel.setText("");
        cmwPositionImageLabel.setIcon(null);
        disableEquipmentControls();
        infoArea.setBackground(Color.white);
    }

    public void filterList() {
        clearEquipment();
        String expression = searchField.getText().toLowerCase();
        String manufacturer = manufacturerBox.getSelectedItemAsString().equals("Mind") ? "" : manufacturerBox.getSelectedItemAsString();
        equipmentList.changeModel(EquipmentFilter.getEquipmentsByManufacturerAndNameLike(equipments, manufacturer, expression), false);
        selectFirstEquipment();
    }

    public void updateFilterBox() {
        List<String> manufacturers = new ArrayList<>();
        for (Equipment equipment : equipments) {
            if (!manufacturers.contains(equipment.getManufacturer())) {
                manufacturers.add(equipment.getManufacturer());
            }
        }
        manufacturers.add("Mind");
        String[] manufacturerArray = new String[manufacturers.size()];
        for (int i = 0; i < manufacturers.size(); i++) {
            manufacturerArray[i] = manufacturers.get(i);
        }
        placer.removeComponent("SelectEquipment manufacturerBox");
        manufacturerBox = new AssComboBox("SelectEquipment manufacturerBox", manufacturerArray, SMALL_TEXT_SIZE, MANUFACTURER_BOX_DIMENSION, listener);
        manufacturerBox.setSelectedItem("Mind");
        placer.addComponent(manufacturerBox, 1, 3, 1, 1);
    }

    public void prepareEquipments() {
        clearEquipment();
        placer.disableComponents();
        equipmentList.clearModel();
        searchField.setText("");
        equipments = assistant.readEquipments();
        refreshNeeded = false;
        placer.enableComponents();
        searchField.requestFocus();
        listener.actionPerformed(new ActionEvent(this, 0, "prepare_equipments_done"));
    }
}
