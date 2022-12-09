package hu.open.assistant.update.graphical;

import hu.open.assistant.commons.graphical.notification.Notice;

/**
 * Defines the types of notifications used in this application.
 */
public enum UpdateNotice implements Notice {
    DETECTION_PROBLEM,
    NETWORK_FOLDER_PROBLEM,
    UPDATE_FOUND,
    FILE_IN_USE,
    UPDATE_FAIL,
    UPDATE_SUCCESS,
    EMPTY
}