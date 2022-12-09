package hu.open.assistant.commons.graphical.notification;

/**
 * A list of available notification types used by BasicNotification. The PLAIN is for program state information, has no
 * user input, and is closable only from code. The OK and OK_CHECKED can display simple information text and an optional
 * image. They both have an OK button and the CHECKED version notifies the window when the button is pressed. IMAGE is
 * for displaying an image only without a text, OK button is included. CONFIRM and CONFIRM_CHECKED display text, an
 * optional image, a YES and a NO button. Both notify the window when the YES button was pressed and the CHECKED version
 * also notifies when the NO button was pressed. INPUT displays a text, an optional image and provides a text field for
 * user input beside an OK button (notifies the window when OK was pressed). PASSWORD is like INPUT but the content
 * of the input field is not visible. SELECT displays text, an optional image and provides a combo box for option
 * selection beside an OK button (also notifies the window when OK was pressed).
 */
public enum NotificationMode {
    PLAIN,
    OK,
    OK_CHECKED,
    IMAGE,
    CONFIRM,
    CONFIRM_CHECKED,
    INPUT,
    PASSWORD,
    SELECT
}