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
                notification = new BasicNotification(notice, "A t??rol?? TAC nem ??rv??nyes sz??m!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_TAC_LENGTH:
                notification = new BasicNotification(notice, "A t??rol?? TAC nem 8 sz??mjegy??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_TAC_MISSING:
                notification = new BasicNotification(notice, "A t??rol?? TAC nincs megadva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_PROFILE_MATCH:
                notification = new BasicNotification(notice, "A profil m??r l??tezik!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_SELECTION_MISSING:
                notification = new BasicNotification(notice, "Nincs minden elem kiv??lasztva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_UNDERSCORE:
                notification = new BasicNotification(notice, "A t??pus alulvon??st ??s tabul??tort nem tartalmazhat!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_TYPE_MISSING:
                notification = new BasicNotification(notice, "Nincs t??pus megadva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_SAVE:
                notification = new BasicNotification(notice, "A feleslegess?? v??lt csillap??t??s f??jlokat a program t??r??lheti?",
                        questionImage, NotificationMode.CONFIRM_CHECKED, NotificationSize.NORMAL, window);
                break;
            case CREATE_PROFILE_SAVE_SUCCESS:
                notification = new BasicNotification(notice, "A m??dos??t??s ment??sre ker??lt!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // EditConfig notifications
            case EDIT_CONFIG_VERSION_INVALID:
                notification = new BasicNotification(notice, "A minimum verzi??knak sz??mnak ??s az alaphelyzet ??rt??k??nek eg??sz sz??mnak kell lennie!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_PASSWORD_SHORT:
                notification = new BasicNotification(notice, "A jelsz?? minimum h??rom karakter!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_SOURCE_INVALID:
                notification = new BasicNotification(notice, "A forr??s csak a k??vetkez?? lehet: cmu, cmw",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_FOLDER_PATHS:
                notification = new BasicNotification(notice, "Biztos benne?\nMent??s ut??n, a program kil??p!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_CMU_LIMITS:
                notification = new BasicNotification(notice, "Biztos benne?\nMent??s ut??n, a program kil??p!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_CMW_LIMITS:
                notification = new BasicNotification(notice, "Biztos benne?\nMent??s ut??n, a program kil??p!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_CMW_LTE_LIMITS:
                notification = new BasicNotification(notice, "Biztos benne?\nMent??s ut??n, a program kil??p!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_PROGRAM_CONFIG:
                notification = new BasicNotification(notice, "Biztos benne?\nMent??s ut??n, a program kil??p!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_CONFIG_UPDATE_PRELOAD_CONFIG:
                notification = new BasicNotification(notice, "Biztos benne?\nMent??s ut??n, a program kil??p!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // EditEquipment notifications
            case EDIT_EQUIPMENT_DELETE_FAIL:
                notification = new BasicNotification(notice, "Am??g a profilok l??teznek, a k??sz??l??k nem t??r??lhet??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_DELETE_CONFIRM:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_NETWORK_FORMAT_ERROR:
                notification = new BasicNotification(notice, "Megadott t??mogatott h??l??zatok form??tuma hib??s!\nLehets??ges opci??k: 2G,3G,4G,5G\nForm??tum: sz??k??z n??lk??l, vessz??vel elv??lasztva",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_NETWORK_MISSING_ERROR:
                notification = new BasicNotification(notice, "Legal??bb egy, t??mogatott h??l??zat megad??sa k??telez??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_POSITION_ERROR:
                notification = new BasicNotification(notice, "Megadott poz??ci?? kieg??sz??t??s hib??s!\nLehets??ges opci??k: FF,FL,FJ,FB,--",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_CREATE_ERROR:
                notification = new BasicNotification(notice, "A n??vnek legal??bb 2 sz??b??l kell ??llnia: gy??rt?? ??s t??pus",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_CREATE_FORMAT:
                notification = new BasicNotification(notice, "A n??v alulvon??st ??s tabul??tort nem tartalmazhat!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_SAVE:
                notification = new BasicNotification(notice, "A m??dos??t??s ment??sre ker??lt!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case EDIT_EQUIPMENT_CREATE:
                notification = new BasicNotification(notice, "Adja meg a k??sz??l??k nev??t!\n(gy??rt?? ??s t??pus)",
                        equipmentImage, NotificationMode.INPUT, NotificationSize.NORMAL, window);
                break;

            // SelectTask notifications
            case SELECT_TASK_CONFIG_ERROR:
                notification = new BasicNotification(notice, "A glob??lis be??ll??t??sok k??z??tt ??rv??nytelen bejegyz??s tal??lhat??. A be??ll??t??sok ellen??rz??se ??s ment??se sz??ks??ges!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TASK_NETWORK_ERROR:
                notification = new BasicNotification(notice, "A h??l??zati mappa be??ll??t??s nem megfelel??, vagy a m??k??d??shez sz??ks??ges f??jlok ??s mapp??k hi??nyoznak!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TASK_SUPPORT_PASSWORD:
                notification = new BasicNotification(notice, "A karbantart??i funkci??k el??r??s??hez, jelsz?? sz??ks??ges!",
                        value, lockImage, NotificationMode.PASSWORD, NotificationSize.NORMAL, window);
                break;
            case SELECT_TASK_WRONG_PASSWORD:
                notification = new BasicNotification(notice, "Helytelen jelsz??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // EditProfile notifications
            case EDIT_PROFILE_IMPORT:
                notification = new BasicNotification(notice, "Melyik profil ker??lj??n import??l??sra:",
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
                notification = new BasicNotification(notice, "A kiv??lasztott adatb??zisr??l biztons??gi ment??s k??sz??lt!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_RESTORE_DONE:
                notification = new BasicNotification(notice, "A biztons??gi ment??s tartalma viszza??ll??t??sra ker??lt!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_DELETED:
                notification = new BasicNotification(notice, "A kiv??lasztott biztons??gi ment??s t??rl??sre ker??lt!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_CREATE:
                notification = new BasicNotification(notice, "V??laszthat?? ??llom??sok:",
                        options, stationImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;
            case SELECT_BACKUP_RESTORE:
                notification = new BasicNotification(notice, "Biztons??gi ment??s vissza??ll??t??sa...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // SelectEquipment notifications
            case SELECT_EQUIPMENT_FILTER:
                notification = new BasicNotification(notice, "V??laszthat?? ??llom??sok:",
                        options, stationImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;
            case SELECT_EQUIPMENT_NO_REPORT:
                notification = new BasicNotification(notice, "Nincs felhaszn??lhat?? riport!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_EQUIPMENT_NO_VALID_REPORT:
                notification = new BasicNotification(notice, "Nincs felhaszn??lhat?? m??r??s!",
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
                notification = new BasicNotification(notice, "A feleslegess?? v??lt csillap??t??s f??jlokat a program t??r??lheti?",
                        questionImage, NotificationMode.CONFIRM_CHECKED, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_SAVE_SUCCESS:
                notification = new BasicNotification(notice, "A m??dos??t??s ment??sre ker??lt!",
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
                notification = new BasicNotification(notice, "A m??dos??t??s ment??sre ker??lt!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_WHITESPACE:
                notification = new BasicNotification(notice, "A gy??rt?? nem tartalmazhat sz??k??zt!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_MATCH:
                notification = new BasicNotification(notice, "??tk??z??s l??tez?? bejegyz??ssel!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_MISSING:
                notification = new BasicNotification(notice, "Hi??nyz?? sz??vegr??sz!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_PART_IN_USE:
                notification = new BasicNotification(notice, "Bejegyz??s haszn??latban, nem t??r??lhet??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // SelectProfileUsage notifications
            case SELECT_PROFILE_USAGE_CONFIRM:
                notification = new BasicNotification(notice, "Biztos benne?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;
            case SELECT_PROFILE_USAGE_SAVE:
                notification = new BasicNotification(notice, "A profil t??rs??t??s ment??sre ker??lt!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // SelectReport notifications
            case SELECT_REPORT_DELETE_ERROR:
                notificationId++;
                notification = new BasicNotification(notice, "A riport t??rl??se sikertelen!",
                        String.valueOf(notificationId), infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case SELECT_REPORT_DELETE_SUCCESS:
                notificationId++;
                notification = new BasicNotification(notice, "A riport t??rl??sre ker??lt!",
                        String.valueOf(notificationId), infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case SELECT_REPORT_DELETE:
                notification = new BasicNotification(notice, "Biztos benne?",
                        binImage, NotificationMode.CONFIRM, NotificationSize.REDUCED, window);
                break;
            case SELECT_REPORT_FILTER:
                notification = new BasicNotification(notice, "V??laszthat?? ??llom??sok:",
                        value, options, stationImage, NotificationMode.SELECT, NotificationSize.NORMAL, window);
                break;

            // SelectReportBatch notifications
            case SELECT_REPORT_BATCH_DATABASE_CHECK:
                notification = new BasicNotification(notice,
                        "Biztos benne?\n\nA mai nappal (" + DateHelper.localDateToTextDate(LocalDate.now()) +
                                ") a kiv??lasztott ??llom??s csillap??t??s adatb??zis??ra, ellen??rizve bejegyz??s fog ker??lni!",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // SelectSourceFolder notifications
            case SELECT_SOURCE_FOLDER_READ:
                notification = new BasicNotification(notice, "Forr??s mapp??k p??szt??z??sa...",
                        searchImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // SelectTac notifications
            case SELECT_TAC_FORMAT:
                notification = new BasicNotification(notice, "A TAC nem ??rv??nyes sz??m!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_MATCH:
                notification = new BasicNotification(notice, "A TAC t??rs??t??s m??r l??tezik!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_NUMBER_LENGTH:
                notification = new BasicNotification(notice, "A sz??m hossza nem 8 sz??mjegy??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_INPUT_EMPTY:
                notification = new BasicNotification(notice, "A bevitel?? mez?? ??res!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case SELECT_TAC_STORE_TAC:
                notification = new BasicNotification(notice, "A t??rol??shoz haszn??lt TAC, nem t??r??lhet??!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // SyncProfile notifications
            case SYNC_PROFILE_SAVE:
                notification = new BasicNotification(notice, "Csillap??t??sok ??tvitele megt??rt??nt!",
                        saveImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // Missing data notifications
            case CMU_SCRIPT_ERROR:
                notification = new BasicNotification(notice, "CMU script t??rs??t??sok nem el??rhet??k vagy s??r??ltek!\n\nEllen??rizze a mapp??t ??s be??ll??t??st!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMU_PROFILE_ERROR:
                notification = new BasicNotification(notice, "CMU profilok nem el??rhet??k vagy s??r??ltek!\n\nEllen??rizze a mapp??t ??s be??ll??t??st!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMU_COMBINED_ERROR:
                notification = new BasicNotification(notice, "CMU script t??rs??t??sok ??s profilok nem el??rhet??k vagy s??r??ltek!\n\nEllen??rizze a mapp??t ??s be??ll??t??st!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMW_PROFILE_ERROR:
                notification = new BasicNotification(notice, "CMW profilok nem el??rhet??k vagy s??r??ltek!\n\nEllen??rizze a mapp??t ??s be??ll??t??st!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case CMU_CMW_PROFILE_ERROR:
                notification = new BasicNotification(notice, "CMU ??s CMW profilok nem el??rhet??k vagy s??r??ltek!\n\nEllen??rizze a mapp??t ??s be??ll??t??st!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;

            // Network folder notification
            case NETWORK_FOLDER_CONFIG:
                notification = new BasicNotification(notice, "Adja meg a h??l??zati mappa nev??t!\n\n(A be??ll??t??s ut??n a program kil??p)",
                        value, networkImage, NotificationMode.INPUT, NotificationSize.NORMAL, window);
                break;

            case NETWORK_FOLDER_CREATE:
                notification = new BasicNotification(notice, "A megadott mappa ??rv??nytelen!\n\nBe??ll??t??sra ker??lj??n, ??j h??l??zati mappak??nt?",
                        questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // Database notifications
            case CMU_DATABASE_READ:
                notification = new BasicNotification(notice, "CMU adatb??zis beolvas??sa...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;
            case CMW_DATABASE_READ:
                notification = new BasicNotification(notice, "CMW adatb??zis beolvas??sa...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;
            case CMU_DATABASE_WRITE:
                notification = new BasicNotification(notice, "CMU adatb??zis ment??se...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;
            case CMW_DATABASE_WRITE:
                notification = new BasicNotification(notice, "CMW adatb??zis ment??se...",
                        processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // Report notifications
            case REPORT_PATH_SUCCESS:
                notificationId++;
                notification = new BasicNotification(notice, "El??r??si ??tvonal a v??g??lapra ker??lt!",
                        String.valueOf(notificationId), clipboardImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case REPORT_IMEI_SUCCESS:
                notificationId++;
                notification = new BasicNotification(notice, "IMEI sz??m a v??g??lapra ker??lt!",
                        String.valueOf(notificationId), clipboardImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case REPORT_MISSING:
                notificationId++;
                notification = new BasicNotification(notice, "A riport m??r nem l??tezik!",
                        String.valueOf(notificationId), infoImage, NotificationMode.OK, NotificationSize.REDUCED, window);
                window.closeNotificationWithDelay(notificationId);
                break;
            case REPORT_PROCESS:
                notification = new BasicNotification(notice, "", processImage, NotificationMode.PLAIN, NotificationSize.REDUCED, window);
                break;

            // Generic notifications
            case GENERIC_EMPTY_FIELD:
                notification = new BasicNotification(notice, "Nincs minden mez?? kit??ltve!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case GENERIC_NOT_NUMBER:
                notification = new BasicNotification(notice, "Nem sz??m lett megadva!",
                        infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case GENERIC_INVALID_CHARACTER:
                notification = new BasicNotification(notice, "Tiltott karakter a kifejez??sben: " +
                        ValidationHelper.getForbiddenCharacters(), infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
                break;
            case GENERIC_MODIFIED:
                notification = new BasicNotification(notice, "Biztos benne?\nA m??dos??t??sok elvet??sre ker??lnek.",
                        value, questionImage, NotificationMode.CONFIRM, NotificationSize.NORMAL, window);
                break;

            // Default empty notification
            default:
                notification = new BasicNotification(RfNotice.EMPTY, "", infoImage, NotificationMode.OK, NotificationSize.NORMAL, window);
        }
        return notification;
    }
}