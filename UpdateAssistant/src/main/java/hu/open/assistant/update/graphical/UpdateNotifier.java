package hu.open.assistant.update.graphical;

import hu.open.assistant.commons.graphical.notification.Notice;
import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.AssWindow;
import hu.open.assistant.commons.graphical.Notifier;
import hu.open.assistant.commons.graphical.notification.AssNotification;
import hu.open.assistant.commons.graphical.notification.BasicNotification;
import hu.open.assistant.commons.graphical.notification.NotificationMode;
import hu.open.assistant.commons.graphical.notification.NotificationSize;

/**
 * Responsible for creating notifications for the entire application. It is a central storage of many pre-defined
 * notifications setting the mode, size, picture and text values.
 */
public class UpdateNotifier extends Notifier {

    private final AssImage infoImage = new AssImage(getClass().getResource("/images/info.png"));
    private final AssImage questionImage = new AssImage(getClass().getResource("/images/question.png"));

    public UpdateNotifier(AssWindow window) {
        super(window);
    }

    @Override
    public AssNotification createNotification(Notice notice, String value, String[] options) {
        AssNotification notification;
        switch ((UpdateNotice) notice) {
            case DETECTION_PROBLEM:
                notification = new BasicNotification(notice, "A frissítendő alkalmazás és verziója nem ismerhető fel!",
                        infoImage, NotificationMode.OK_CHECKED, NotificationSize.REDUCED, window);
                break;
            case NETWORK_FOLDER_PROBLEM:
                notification = new BasicNotification(notice, "A hálózati mappa nem elérhető!",
                        infoImage, NotificationMode.OK_CHECKED, NotificationSize.REDUCED, window);
                break;
            case UPDATE_FOUND:
                notification = new BasicNotification(notice, "Újabb verzió elérhető!\n\nIndítható a frissítés?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.REDUCED, window);
                break;
            case FILE_IN_USE:
                notification = new BasicNotification(notice, "A frissítés nem végezhető el!\n\nA jelenlegi program még fut.",
                        infoImage, NotificationMode.OK_CHECKED, NotificationSize.REDUCED, window);
                break;
            case UPDATE_FAIL:
                notification = new BasicNotification(notice, "A frissítés nem végezhető el!\n\nIsmeretlen probléma másolás közben.",
                        infoImage, NotificationMode.OK_CHECKED, NotificationSize.REDUCED, window);
                break;
            case UPDATE_SUCCESS:
                notification = new BasicNotification(notice, "A program frissítése sikeres volt!",
                        infoImage, NotificationMode.OK_CHECKED, NotificationSize.REDUCED, window);
                break;
            default:
                notification = new BasicNotification(UpdateNotice.EMPTY, "", infoImage, NotificationMode.OK_CHECKED, NotificationSize.REDUCED, window);
        }
        return notification;
    }
}
