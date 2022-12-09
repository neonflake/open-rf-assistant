package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssTextArea;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.filter.ProfileLogBatchFilter;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.log.batch.LogBatch;
import hu.open.assistant.rf.model.log.batch.ProfileLogBatch;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for showing the profile and database log history in a text area. Allows to switch between the two log types and
 * to show history with compensation values when admin mode is enabled.
 */
public class ShowLog extends RfPanel {

    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(600, 630);
    private static final int TEXT_SIZE = 14;

    private final AssButton modeButton;
    private final AssButton valueButton;
    private final AssTextArea logTextArea;
    private final AssLabel titleLabel;
    private String mode;
    private boolean showValues;
    private final List<LogBatch> sortedLogs = new ArrayList<>();
    private boolean supportEnabled;

    public ShowLog(RfWindow window, RfAssistant assistant) {
        super(window, assistant, "ShowLog");
        //placer.enableDebug();
        titleLabel = new AssLabel("Profil történet", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
        logTextArea = new AssTextArea("ShowLog logTextArea", TEXT_SIZE, TEXT_AREA_DIMENSION, true);
        modeButton = new AssButton("ShowLog modeButton", "Adatbázis történet", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        valueButton = new AssButton("ShowLog valueButton", "Értékek mutatása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
        AssButton backButton = new AssButton("ShowLog backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
        placer.addComponent(titleLabel, 1, 1, 3, 1);
        placer.addComponent(modeButton, 4, 1, 1, 1);
        placer.addComponent(logTextArea, 1, 2, 3, 9);
        placer.addComponent(valueButton, 4, 2, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 3, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 4, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 5, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 6, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 7, 1, 1);
        placer.addEmptyComponent(SIDE_BUTTON_DIMENSION, 4, 8, 1, 1);
        placer.addComponent(backButton, 4, 9, 1, 1);
        placer.addImageComponent(logoImage, 4, 10, 1, 1);
    }

    public void toggleMode() {
        if (mode.equals("profile")) {
            mode = "database";
            refreshLog("database");
        } else {
            mode = "profile";
            refreshLog("profile");
        }
        refreshTitleLabel();
        this.repaint();
    }

    public void toggleShowValues() {
        showValues = !showValues;
        valueButton.setText(showValues ? "Értékek elrejtése" : "Értékek mutatása");
        refreshTextArea();
    }

    public void refreshLog(String mode) {
        this.mode = mode;
        refreshTitleLabel();
        showValues = false;
        sortedLogs.clear();
        if (mode.equals("profile")) {
            modeButton.setText("Adatbázis történet");
            modeButton.setEnabled(supportEnabled);
            placer.showComponent("ShowLog valueButton");
            valueButton.setText("Értékek mutatása");
            valueButton.setEnabled(supportEnabled);
            List<ProfileLogBatch> logs = assistant.readProfileLogs();
            List<ProfileLogBatch> testLogs = ProfileLogBatchFilter.getProfileLogBatchesByNameLike(logs, "Test");
            for (ProfileLogBatch testLog : testLogs) {
                logs.remove(testLog);
            }
            sortedLogs.addAll(logs);
        } else {
            modeButton.setText("Profil történet");
            modeButton.setEnabled(supportEnabled);
            placer.hideComponent("ShowLog valueButton");
            valueButton.setEnabled(supportEnabled);
            sortedLogs.addAll(assistant.readDatabaseLogs());
        }
        refreshTextArea();
    }

    private void refreshTitleLabel() {
        if (mode.equals("profile")) {
            titleLabel.setText("Profil történet");
        } else {
            titleLabel.setText("Adatbázis történet");
        }
    }

    private void refreshTextArea() {
        StringBuilder builder = new StringBuilder();
        for (LogBatch batch : sortedLogs) {
            if (showValues) {
                builder.append(TextHelper.stringListToLineBrokenString(batch.getExtendedInfo()));
            } else {
                builder.append(TextHelper.stringListToLineBrokenString(batch.getInfo()));
            }
        }
        logTextArea.setText(builder.toString());
        logTextArea.setCaretPosition(0);
    }

    public void enableSupport() {
        supportEnabled = true;
        modeButton.setEnabled(true);
        valueButton.setEnabled(true);
    }
}
