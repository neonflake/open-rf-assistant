package hu.open.assistant.rf.graphical;

import hu.open.assistant.commons.graphical.notification.Notice;
import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.commons.graphical.AssWindow;
import hu.open.assistant.commons.graphical.Notifier;
import hu.open.assistant.commons.graphical.notification.AssNotification;
import hu.open.assistant.commons.graphical.notification.BasicNotification;
import hu.open.assistant.commons.graphical.notification.NotificationMode;
import hu.open.assistant.commons.graphical.notification.NotificationSize;
import hu.open.assistant.commons.util.DateHelper;
import hu.open.assistant.commons.util.ValidationHelper;

import java.time.LocalDate;

/**
 * Responsible for creating notifications for the entire application. It is a central storage of many pre-defined
 * notifications setting the mode, size, picture and text values.
 */
public class RfNotifier extends Notifier {

    private final AssImage infoImage = new AssImage(getClass().getResource("/images/info.png"));
    private final AssImage questionImage = new AssImage(getClass().getResource("/images/question.png"));
    private final AssImage processImage = new AssImage(getClass().getResource("/images/process.png"));
    private final AssImage clipboardImage = new AssImage(getClass().getResource("/images/clipboard.png"));
    private final AssImage lockImage = new AssImage(getClass().getResource("/images/lock.png"));
    private final AssImage binImage = new AssImage(getClass().getResource("/images/bin.png"));
    private final AssImage saveImage = new AssImage(getClass().getResource("/images/save.png"));
    private final AssImage stationImage = new AssImage(getClass().getResource("/images/station.png"));
    private final AssImage networkImage = new AssImage(getClass().getResource("/images/network.png"));
    private final AssImage equipmentImage = new AssImage(getClass().getResource("/images/equipment.png"));
    private final AssImage searchImage = new AssImage(getClass().getResource("/images/search.png"));

    private int notificationId = 0;

    public RfNotifier(AssWindow window) {
        super(window);
    }

    public AssNotification createNotification(Notice notice, String value, String[] options) {
        AssNotification notification;
        switch ((RfNotice) notice) {

            // Panel specific notifications

            // CreateProfile notifications
            case CREATE_PROFILE_TAC_FORMAT:
                notification = new BasicNotification(notice, "A tároló TAC nem érvényes szám!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_TAC_LENGTH:
                notification = new BasicNotification(notice, "A tároló TAC nem 8 számjegyű!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_TAC_MISSING:
                notification = new BasicNotification(notice, "A tároló TAC nincs megadva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_PROFILE_MATCH:
                notification = new BasicNotification(notice, "A profil már létezik!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_SELECTION_MISSING:
                notification = new BasicNotification(notice, "Nincs minden elem kiválasztva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_UNDERSCORE:
                notification = new BasicNotification(notice, "A típus alulvonást és tabulátort nem tartalmazhat!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_TYPE_MISSING:
                notification = new BasicNotification(notice, "Nincs típus megadva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_SAVE:
                notification = new BasicNotification(notice, "A feleslegessé vált csillapítás fájlokat a program törölheti?",
                        questionImage, NotificationMode.CONFIRM_CHECKED, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_SAVE_SUCCESS:
                notification = new BasicNotification(notice, "A módosítás mentésre került!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // EditConfig notifications
            case EDIT_CONFIG_VERSION_INVALID:
                notification = new BasicNotification(notice, "A minimum verzióknak számnak és az alaphelyzet értékének egész számnak kell lennie!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_PASSWORD_SHORT:
                notification = new BasicNotification(notice, "A jelszó minimum három karakter!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_SOURCE_INVALID:
                notification = new BasicNotification(notice, "A forrás csak a következő lehet: cmu, cmw",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_FOLDER_PATHS:
                notification = new BasicNotification(notice, "Biztos benne?\nMentés után, a program kilép!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_CMU_LIMITS:
                notification = new BasicNotification(notice, "Biztos benne?\nMentés után, a program kilép!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_CMW_LIMITS:
                notification = new BasicNotification(notice, "Biztos benne?\nMentés után, a program kilép!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_CMW_LTE_LIMITS:
                notification = new BasicNotification(notice, "Biztos benne?\nMentés után, a program kilép!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_PROGRAM_CONFIG:
                notification = new BasicNotification(notice, "Biztos benne?\nMentés után, a program kilép!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_PRELOAD_CONFIG:
                notification = new BasicNotification(notice, "Biztos benne?\nMentés után, a program kilép!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // EditEquipment notifications
            case EDIT_EQUIPMENT_DELETE_FAIL:
                notification = new BasicNotification(notice, "Amíg a profilok léteznek, a készülék nem törölhető!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_DELETE_CONFIRM:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_NETWORK_FORMAT_ERROR:
                notification = new BasicNotification(notice, "Megadott támogatott hálózatok formátuma hibás!\nLehetséges opciók: 2G,3G,4G,5G\nFormátum: szóköz nélkül, vesszővel elválasztva",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_NETWORK_MISSING_ERROR:
                notification = new BasicNotification(notice, "Legalább egy, támogatott hálózat megadása kötelező!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_POSITION_ERROR:
                notification = new BasicNotification(notice, "Megadott pozíció kiegészítés hibás!\nLehetséges opciók: FF,FL,FJ,FB,--",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_CREATE_ERROR:
                notification = new BasicNotification(notice, "A névnek legalább 2 szóból kell állnia: gyártó és típus",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_CREATE_FORMAT:
                notification = new BasicNotification(notice, "A név alulvonást és tabulátort nem tartalmazhat!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_SAVE:
                notification = new BasicNotification(notice, "A módosítás mentésre került!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_CREATE:
                notification = new BasicNotification(notice, "Adja meg a készülék nevét!\n(gyártó és típus)",
                        equipmentImage, NotificationMode.INPUT, NotificationSize.NORMAL, window);
                break;

            // SelectTask notifications
            case SELECT_TASK_CONFIG_ERROR:
                notification = new BasicNotification(notice, "A globális beállítások között érvénytelen bejegyzés található. A beállítások ellenőrzése és mentése szükséges!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TASK_NETWORK_ERROR:
                notification = new BasicNotification(notice, "A hálózati mappa beállítás nem megfelelő, vagy a működéshez szükséges fájlok és mappák hiányoznak!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TASK_SUPPORT_PASSWORD:
                notification = new BasicNotification(notice, "A karbantartói funkciók eléréséhez, jelszó szükséges!",
                        value, lockImage, NotificationMode.PASSWORD, NotificationSize.NORMAL, window);
                break;
            case SELECT_TASK_WRONG_PASSWORD:
                notification = new BasicNotification(notice, "Helytelen jelszó!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // EditProfile notifications
            case EDIT_PROFILE_IMPORT:
                notification = new BasicNotification(notice, "Melyik profil kerüljön importálásra:",
                        value, options, questionImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;
            case EDIT_PROFILE_RESET:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // SelectBackup notifications
            case SELECT_BACKUP_DELETE_CONFIRM:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_RESTORE_CONFIRM:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_CREATE_DONE:
                notification = new BasicNotification(notice, "A kiválasztott adatbázisról biztonsági mentés készült!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_RESTORE_DONE:
                notification = new BasicNotification(notice, "A biztonsági mentés tartalma viszzaállításra került!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_DELETED:
                notification = new BasicNotification(notice, "A kiválasztott biztonsági mentés törlésre került!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_CREATE:
                notification = new BasicNotification(notice, "Választható állomások:",
                        options, stationImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_RESTORE:
                notification = new BasicNotification(notice, "Biztonsági mentés visszaállítása...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // SelectEquipment notifications
            case SELECT_EQUIPMENT_FILTER:
                notification = new BasicNotification(notice, "Választható állomások:",
                        options, stationImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;
            case SELECT_EQUIPMENT_NO_REPORT:
                notification = new BasicNotification(notice, "Nincs felhasználható riport!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_EQUIPMENT_NO_VALID_REPORT:
                notification = new BasicNotification(notice, "Nincs felhasználható mérés!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_EQUIPMENT_CMU_IMAGE:
                notification = new BasicNotification(notice, "",
                        new AssImage(value), NotificationMode.IMAGE, NotificationSize.LARGE, window);
                break;
            case SELECT_EQUIPMENT_CMW_IMAGE:
                notification = new BasicNotification(notice, "",
                        new AssImage(value), NotificationMode.IMAGE, NotificationSize.LARGE, window);
                break;
            case SELECT_EQUIPMENT_POSITION:
                notification = new BasicNotification(notice, "",
                        infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                break;

            // SelectProfile notifications
            case SELECT_PROFILE_SAVE:
                notification = new BasicNotification(notice, "A feleslegessé vált csillapítás fájlokat a program törölheti?",
                        questionImage, NotificationMode.CONFIRM_CHECKED, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_SAVE_SUCCESS:
                notification = new BasicNotification(notice, "A módosítás mentésre került!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_RELOAD:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_DELETE:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_REVERT:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // SelectProfilePart notifications
            case SELECT_PROFILE_PART_SAVE:
                notification = new BasicNotification(notice, "A módosítás mentésre került!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_WHITESPACE:
                notification = new BasicNotification(notice, "A gyártó nem tartalmazhat szóközt!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_MATCH:
                notification = new BasicNotification(notice, "Ütközés létező bejegyzéssel!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_MISSING:
                notification = new BasicNotification(notice, "Hiányzó szövegrész!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_IN_USE:
                notification = new BasicNotification(notice, "Bejegyzés használatban, nem törölhető!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // SelectProfileUsage notifications
            case SELECT_PROFILE_USAGE_CONFIRM:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_USAGE_SAVE:
                notification = new BasicNotification(notice, "A profil társítás mentésre került!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // SelectReport notifications
            case SELECT_REPORT_DELETE_ERROR:
                notificationId++;
                notification = new BasicNotification(notice, "A riport törlése sikertelen!",
                        String.valueOf(notificationId), infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case SELECT_REPORT_DELETE_SUCCESS:
                notificationId++;
                notification = new BasicNotification(notice, "A riport törlésre került!",
                        String.valueOf(notificationId), infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case SELECT_REPORT_DELETE:
                notification = new BasicNotification(notice, "Biztos benne?",
                        binImage, NotificationMode.CONFIRM, NotificationSize.REDUCED, window);
                break;
            case SELECT_REPORT_FILTER:
                notification = new BasicNotification(notice, "Választható állomások:",
                        value, options, stationImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;

            // SelectReportBatch notifications
            case SELECT_REPORT_BATCH_DATABASE_CHECK:
                notification = new BasicNotification(notice,
                        "Biztos benne?\n\nA mai nappal (" + DateHelper.localDateToTextDate(LocalDate.now()) +
                                ") a kiválasztott állomás csillapítás adatbázisára, ellenőrizve bejegyzés fog kerülni!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // SelectSourceFolder notifications
            case SELECT_SOURCE_FOLDER_READ:
                notification = new BasicNotification(notice, "Forrás mappák pásztázása...",
                        searchImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // SelectTac notifications
            case SELECT_TAC_FORMAT:
                notification = new BasicNotification(notice, "A TAC nem érvényes szám!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_MATCH:
                notification = new BasicNotification(notice, "A TAC társítás már létezik!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_NUMBER_LENGTH:
                notification = new BasicNotification(notice, "A szám hossza nem 8 számjegyű!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_INPUT_EMPTY:
                notification = new BasicNotification(notice, "A bevitelő mező üres!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_STORE_TAC:
                notification = new BasicNotification(notice, "A tároláshoz használt TAC, nem törölhető!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // SyncProfile notifications
            case SYNC_PROFILE_SAVE:
                notification = new BasicNotification(notice, "Csillapítások átvitele megtörtént!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // Missing data notifications
            case CMU_SCRIPT_ERROR:
                notification = new BasicNotification(notice, "CMU script társítások nem elérhetők vagy sérültek!\n\nEllenőrizze a mappát és beállítást!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMU_PROFILE_ERROR:
                notification = new BasicNotification(notice, "CMU profilok nem elérhetők vagy sérültek!\n\nEllenőrizze a mappát és beállítást!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMU_COMBINED_ERROR:
                notification = new BasicNotification(notice, "CMU script társítások és profilok nem elérhetők vagy sérültek!\n\nEllenőrizze a mappát és beállítást!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMW_PROFILE_ERROR:
                notification = new BasicNotification(notice, "CMW profilok nem elérhetők vagy sérültek!\n\nEllenőrizze a mappát és beállítást!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMU_CMW_PROFILE_ERROR:
                notification = new BasicNotification(notice, "CMU és CMW profilok nem elérhetők vagy sérültek!\n\nEllenőrizze a mappát és beállítást!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // Network folder notification
            case NETWORK_FOLDER_CONFIG:
                notification = new BasicNotification(notice, "Adja meg a hálózati mappa nevét!\n\n(A beállítás után a program kilép)",
                        value, networkImage, NotificationMode.INPUT, NotificationSize.NORMAL, window);
                break;

            case NETWORK_FOLDER_CREATE:
                notification = new BasicNotification(notice, "A megadott mappa érvénytelen!\n\nBeállításra kerüljön, új hálózati mappaként?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // Database notifications
            case CMU_DATABASE_READ:
                notification = new BasicNotification(notice, "CMU adatbázis beolvasása...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;
            case CMW_DATABASE_READ:
                notification = new BasicNotification(notice, "CMW adatbázis beolvasása...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;
            case CMU_DATABASE_WRITE:
                notification = new BasicNotification(notice, "CMU adatbázis mentése...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;
            case CMW_DATABASE_WRITE:
                notification = new BasicNotification(notice, "CMW adatbázis mentése...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // Report notifications
            case REPORT_PATH_SUCCESS:
                notificationId++;
                notification = new BasicNotification(notice, "Elérési útvonal a vágólapra került!",
                        String.valueOf(notificationId), clipboardImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case REPORT_IMEI_SUCCESS:
                notificationId++;
                notification = new BasicNotification(notice, "IMEI szám a vágólapra került!",
                        String.valueOf(notificationId), clipboardImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case REPORT_MISSING:
                notificationId++;
                notification = new BasicNotification(notice, "A riport már nem létezik!",
                        String.valueOf(notificationId), infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case REPORT_PROCESS:
                notification = new BasicNotification(notice, "", processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // Generic notifications
            case GENERIC_EMPTY_FIELD:
                notification = new BasicNotification(notice, "Nincs minden mező kitöltve!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case GENERIC_NOT_NUMBER:
                notification = new BasicNotification(notice, "Nem szám lett megadva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case GENERIC_INVALID_CHARACTER:
                notification = new BasicNotification(notice, "Tiltott karakter a kifejezésben: " +
                        ValidationHelper.getForbiddenCharacters(), infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case GENERIC_MODIFIED:
                notification = new BasicNotification(notice, "Biztos benne?\nA módosítások elvetésre kerülnek.",
                        value, questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // Default empty notification
            default:
                notification = new BasicNotification(RfNotice.EMPTY, "", infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
        }
        return notification;
    }
}