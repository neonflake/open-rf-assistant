package hu.open.assistant.commons.graphical.notification;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssComboBox;
import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.gui.AssPasswordField;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.commons.graphical.AssWindow;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Basic notification used by all Assistant applications. There is selection of notification modes and predefined sizes
 * which can be set on creation. Notifications can display simple a text, image or both and provide user input in form
 * if an input field or combo box selection.
 */
public class BasicNotification extends AssNotification implements KeyListener {

    private static final Dimension FIELD_DIMENSION = new Dimension(300, 50);
    private static final int REDUCED_IMAGE_SIZE = 150;
    private static final int BUTTON_TEXT_SIZE = 16;
    private static final int INPUT_TEXT_SIZE = 16;

    private final AssTextArea textArea;
    private final NotificationMode mode;
    private AssButton okButton;
    private AssButton noButton;
    private AssPasswordField passwordField;
    private AssTextField inputField;
    private AssComboBox selectBox;
    private final String value;

    /**
     * Initialise and display the notification.
     *
     * @param notice       type of the notification
     * @param text         text to display on the notification
     * @param image        image to display in notification
     * @param mode         selected mode of the notification (see NotificationMode)
     * @param size         selected size of the notification (see NotificationSize)
     * @param parentWindow application window for input related notification event handling
     */
    public BasicNotification(Notice notice, String text, AssImage image, NotificationMode mode, NotificationSize size, AssWindow parentWindow) {
        this(notice, text, "", new String[0], image, mode, size, parentWindow);
    }

    /**
     * Initialise and display the notification.
     *
     * @param notice       type of the notification
     * @param text         text to display on the notification
     * @param value        value to show in the notifications text field, default combo box value, or an additional parameter
     * @param image        image to display in notification
     * @param mode         selected mode of the notification (see NotificationMode)
     * @param size         selected size of the notification (see NotificationSize)
     * @param parentWindow application window for input related notification event handling
     */
    public BasicNotification(Notice notice, String text, String value, AssImage image, NotificationMode mode, NotificationSize size, AssWindow parentWindow) {
        this(notice, text, value, new String[0], image, mode, size, parentWindow);
    }

    /**
     * Initialise and display the notification.
     *
     * @param notice       type of the notification
     * @param text         text to display on the notification
     * @param options      options to display in the notifications combo box
     * @param image        image to display in notification
     * @param mode         selected mode of the notification (see NotificationMode)
     * @param size         selected size of the notification (see NotificationSize)
     * @param parentWindow application window for input related notification event handling
     */
    public BasicNotification(Notice notice, String text, String[] options, AssImage image, NotificationMode mode, NotificationSize size, AssWindow parentWindow) {
        this(notice, text, "", options, image, mode, size, parentWindow);
    }

    /**
     * Initialise and display the notification.
     *
     * @param notice       type of the notification
     * @param text         text to display on the notification
     * @param options      options to display in the notifications combo box
     * @param value        value to show in the notifications text field, default combo box value, or an additional parameter
     * @param image        image to display in notification
     * @param mode         selected mode of the notification (see NotificationMode)
     * @param size         selected size of the notification (see NotificationSize)
     * @param parentWindow application window for input related notification event handling
     */
    public BasicNotification(Notice notice, String text, String value, String[] options, AssImage image, NotificationMode mode, NotificationSize size, AssWindow parentWindow) {
        super(notice, size.getDialogDimension(), parentWindow);
        this.mode = mode;
        this.value = value;
        if (size.equals(NotificationSize.REDUCED)) {
            image = image.getResizedImage(REDUCED_IMAGE_SIZE, REDUCED_IMAGE_SIZE);
        }
        textArea = new AssTextArea("AppNotification textArea", size.getTextSize(), size.getTextAreaDimension(), false);
        textArea.setText(text);
        switch (mode) {
            case OK:
            case OK_CHECKED:
                okButton = new AssButton("AppNotification okButton", "OK", BUTTON_TEXT_SIZE, size.getButtonDimension(), null, true);
                okButton.addKeyListener(this);
                if (mode.equals(NotificationMode.OK_CHECKED)) {
                    okButton.addActionListener(parentWindow);
                } else {
                    okButton.addActionListener(this);
                }
                if (image == null) {
                    placer.addComponent(textArea, 1, 1, 1, 1, 1.0, 0.9);
                    placer.addComponent(okButton, 1, 2, 1, 1, 1.0, 0.1);
                } else {
                    double imageYWeight = size == NotificationSize.NORMAL ? 0.3 : 0.6;
                    double textAreaYWeight = size == NotificationSize.NORMAL ? 0.6 : 0.3;
                    placer.addImageComponent(image, 1, 1, 1, 1, 1.0, imageYWeight);
                    placer.addComponent(textArea, 1, 2, 1, 1, 1.0, textAreaYWeight);
                    placer.addComponent(okButton, 1, 3, 1, 1, 1.0, 0.1);
                }
                break;
            case IMAGE:
                okButton = new AssButton("AppNotification okButton", "OK", BUTTON_TEXT_SIZE, size.getButtonDimension(), this, true);
                okButton.addKeyListener(this);
                placer.addImageComponent(image, 1, 1, 1, 1, 1.0, 0.95);
                placer.addComponent(okButton, 1, 2, 1, 1, 1.0, 0.05);
                break;
            case CONFIRM:
            case CONFIRM_CHECKED:
                AssButton yesButton = new AssButton("AppNotification yesButton", "Igen", BUTTON_TEXT_SIZE, size.getButtonDimension(), parentWindow, true);
                noButton = new AssButton("AppNotification noButton", "Nem", size.getTextSize(), size.getButtonDimension(), null, true);
                noButton.addKeyListener(this);
                if (mode.equals(NotificationMode.CONFIRM_CHECKED)) {
                    noButton.addActionListener(parentWindow);
                } else {
                    noButton.addActionListener(this);
                }
                if (image == null) {
                    placer.addComponent(textArea, 1, 1, 2, 1, 1.0, 0.9);
                    placer.addComponent(yesButton, 1, 2, 1, 1, 1.0, 0.1);
                    placer.addComponent(noButton, 1, 2, 1, 1, 1.0, 0.1);
                } else {
                    placer.addImageComponent(image, 1, 1, 2, 1, 1.0, 0.3);
                    placer.addComponent(textArea, 1, 2, 2, 1, 1.0, 0.5);
                    placer.addComponent(yesButton, 1, 3, 1, 1, 0.5, 0.1);
                    placer.addComponent(noButton, 2, 3, 1, 1, 0.5, 0.1);
                }
                break;
            case PASSWORD:
            case INPUT:
                okButton = new AssButton("AppNotification okButton", "OK", BUTTON_TEXT_SIZE, size.getButtonDimension(), parentWindow, true);
                okButton.addKeyListener(this);
                textArea.setPreferredSize(new Dimension(size.getTextAreaDimension().width, 75));
                if (mode.equals(NotificationMode.PASSWORD)) {
                    passwordField = new AssPasswordField("AppNotification passwordField", INPUT_TEXT_SIZE, FIELD_DIMENSION, parentWindow);
                    passwordField.addKeyListener(this);
                    placer.addComponent(passwordField, 1, 3, 2, 1, 1.0, 0.5);
                } else {
                    inputField = new AssTextField("AppNotification inputField", FIELD_DIMENSION, INPUT_TEXT_SIZE, parentWindow, "", true);
                    inputField.setText(value);
                    inputField.addKeyListener(this);
                    placer.addComponent(inputField, 1, 3, 2, 1, 1.0, 0.5);
                }
                placer.addImageComponent(image, 1, 1, 1, 1, 1.0, 0.3);
                placer.addComponent(textArea, 1, 2, 1, 1, 1.0, 0.1);
                placer.addComponent(okButton, 1, 4, 1, 1, 1.0, 0.1);
                break;
            case SELECT:
                okButton = new AssButton("AppNotification okButton", "OK", BUTTON_TEXT_SIZE, size.getButtonDimension(), parentWindow, true);
                textArea.setPreferredSize(new Dimension(size.getTextAreaDimension().width, 75));
                selectBox = new AssComboBox("AppNotification selectBox", options, INPUT_TEXT_SIZE, FIELD_DIMENSION, parentWindow);
                selectBox.setSelectedItem(value);
                placer.addImageComponent(image, 1, 1, 1, 1, 1.0, 0.3);
                placer.addComponent(textArea, 1, 2, 1, 1, 1.0, 0.1);
                placer.addComponent(selectBox, 1, 3, 2, 1, 1.0, 0.5);
                placer.addComponent(okButton, 1, 4, 1, 1, 1.0, 0.1);
                break;
            case PLAIN:
                this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                if (image == null) {
                    placer.addComponent(textArea, 1, 1, 1, 1, 1.0, 1);
                } else {
                    placer.addImageComponent(image, 1, 1, 1, 1, 1.0, 0.3);
                    placer.addComponent(textArea, 1, 2, 1, 1, 1.0, 0.7);
                }
                break;
        }
        parentWindow.setEnabled(false);
        setVisible(true);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getInput() {
        if (mode.equals(NotificationMode.INPUT)) {
            return inputField.getText();
        } else if (mode.equals(NotificationMode.PASSWORD)) {
            return String.valueOf(passwordField.getPassword());
        }
        return "";
    }

    @Override
    public String getSelectedOption() {
        return (String) selectBox.getSelectedItem();
    }

    @Override
    public void changeText(String text) {
        textArea.setText(text);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            switch (mode) {
                case OK:
                case OK_CHECKED:
                    okButton.requestFocus();
                    break;
                case INPUT:
                    inputField.requestFocus();
                    break;
                case PASSWORD:
                    passwordField.requestFocus();
                    break;
                case CONFIRM:
                    noButton.requestFocus();
                    break;
                case SELECT:
                    selectBox.requestFocus();
                    break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() instanceof JComponent) {
            switch (((JComponent) event.getSource()).getName()) {
                case "AppNotification okButton":
                case "AppNotification noButton":
                    this.dispose();
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // implement if needed
    }

    @Override
    public void keyPressed(KeyEvent event) {
        // implement if needed
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getSource() instanceof JComponent) {
            String sourceName = ((JComponent) event.getSource()).getName();
            if (sourceName.contains("AppNotification okButton")) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    okButton.doClick();
                }
            } else if (sourceName.equals("AppNotification noButton")) {
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    noButton.doClick();
                }
            } else if (sourceName.equals("AppNotification passwordField")) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    okButton.doClick();
                }
            } else if (sourceName.equals("AppNotification inputField")) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    okButton.doClick();
                }
            }
        }
    }
}
