package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.profile.Profile;
import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.report.batch.CmwReportBatch;
import hu.open.assistant.rf.model.compensation.CmuCompensation;
import hu.open.assistant.rf.model.report.batch.ReportBatch;

import java.awt.Color;
import java.awt.Dimension;

/**
 * GUI for displaying a point graph with measurements from a report batch. The logic for the graph display is inside the
 * panel of the graphs panel. This panel has controls which can change the graphs type and the number of options
 * that are available is determined by the given report batches content and type. With admin mode enabled there are
 * controls to alter the points vertical position. By altering the vertical position a compensation is calculated. The
 * amount of compensation alters the color of the navigation buttons.
 */
public class ShowGraph extends RfPanel {

	private static final Dimension BUTTON_DIMENSION = new Dimension(150, 50);
	private static final int SMALL_TEXT_SIZE = 14;
	private static final int LARGE_TEXT_SIZE = 16;
	private static final int NORMAL_LIMIT = 25;
	private static final int ACCEPTABLE_LIMIT = 30;
	private static final Color PROBLEMATIC_COLOR = new Color(200, 0, 30);
	private static final Color ACCEPTABLE_COLOR = new Color(176, 129, 0);
	private static final Color NORMAL_COLOR = new Color(0, 0, 0);

	private final AssLabel titleLabel;
	private final AssButton lowUpButton;
	private final AssButton midUpButton;
	private final AssButton highUpButton;
	private final AssButton lowDownButton;
	private final AssButton midDownButton;
	private final AssButton highDownButton;
	private final AssButton wcdma1Button;
	private final AssButton wcdma8Button;
	private final AssButton gsm900Button;
	private final AssButton gsm1800Button;
	private final AssButton resetButton;
	private final AssButton bandButton;
	private final AssButton fixButton;
	private PointGraph board;
	private Compensation defaultCompensation;
	private Compensation compensation;
	private String graphType;
	private String name;
	private boolean rxGraph;
	private boolean lteGraph;
	private Profile profile;
	private double[] profileValues;
	private ReportBatch selectedBatch;
	private boolean operatorMode;

	public ShowGraph(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "ShowGraph");
		//placer.enableDebug();
		titleLabel = new AssLabel("", LARGE_TEXT_SIZE, TITLE_LABEL_DIMENSION);
		AssButton backButton = new AssButton("ShowGraph backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		resetButton = new AssButton("ShowGraph resetButton", "Alaphelyzet", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		fixButton = new AssButton("ShowGraph fixButton", "Módosít", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		lowUpButton = new AssButton("ShowGraph lowUpButton", "+ Low (0.0)", SMALL_TEXT_SIZE, BUTTON_DIMENSION, listener, false);
		lowDownButton = new AssButton("ShowGraph lowDownButton", "- Low (0.0)", SMALL_TEXT_SIZE, BUTTON_DIMENSION, listener, false);
		midUpButton = new AssButton("ShowGraph midUpButton", "+ Mid (0.0)", SMALL_TEXT_SIZE, BUTTON_DIMENSION, listener, false);
		midDownButton = new AssButton("ShowGraph midDownButton", "- Mid (0.0)", SMALL_TEXT_SIZE, BUTTON_DIMENSION, listener, false);
		highUpButton = new AssButton("ShowGraph highUpButton", "+ High (0.0)", SMALL_TEXT_SIZE, BUTTON_DIMENSION, listener, false);
		highDownButton = new AssButton("ShowGraph highDownButton", "- High (0.0)", SMALL_TEXT_SIZE, BUTTON_DIMENSION, listener, false);
		wcdma1Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		wcdma8Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		gsm900Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		gsm1800Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		bandButton = new AssButton("ShowGraph bandButton", "2G-3G / 4G váltás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton directionButton = new AssButton("ShowGraph directionButton", "TX / RX váltás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addComponent(wcdma1Button, 4, 1, 1, 1);
		placer.addComponent(wcdma8Button, 4, 2, 1, 1);
		placer.addComponent(gsm900Button, 4, 3, 1, 1);
		placer.addComponent(gsm1800Button, 4, 4, 1, 1);
		placer.addComponent(directionButton, 4, 5, 1, 1);
		placer.addComponent(bandButton, 4, 6, 1, 1);
		placer.addComponent(resetButton, 4, 7, 1, 1);
		placer.addComponent(fixButton, 4, 8, 1, 1);
		placer.addComponent(lowUpButton, 1, 9, 1, 1);
		placer.addComponent(midUpButton, 2, 9, 1, 1);
		placer.addComponent(highUpButton, 3, 9, 1, 1);
		placer.addComponent(lowDownButton, 1, 10, 1, 1);
		placer.addComponent(midDownButton, 2, 10, 1, 1);
		placer.addComponent(highDownButton, 3, 10, 1, 1);
		placer.addComponent(backButton, 4, 9, 1, 1);
		placer.addImageComponent(logoImage, 4, 10, 1, 1);
	}

	public void displayGraph(ReportBatch reportBatch, boolean operatorMode) {
		selectedBatch = reportBatch;
		this.operatorMode = operatorMode;
		profile = reportBatch.getProfile();
		name = reportBatch.getName();
		rxGraph = false;
		if (!operatorMode) {
			placer.showComponent("ShowGraph resetButton");
			placer.showComponent("ShowGraph fixButton");
			placer.showComponent("ShowGraph lowUpButton");
			placer.showComponent("ShowGraph lowDownButton");
			placer.showComponent("ShowGraph midUpButton");
			placer.showComponent("ShowGraph midDownButton");
			placer.showComponent("ShowGraph highUpButton");
			placer.showComponent("ShowGraph highDownButton");
		} else {
			placer.hideComponent("ShowGraph resetButton");
			placer.hideComponent("ShowGraph fixButton");
			placer.hideComponent("ShowGraph lowUpButton");
			placer.hideComponent("ShowGraph lowDownButton");
			placer.hideComponent("ShowGraph midUpButton");
			placer.hideComponent("ShowGraph midDownButton");
			placer.hideComponent("ShowGraph highUpButton");
			placer.hideComponent("ShowGraph highDownButton");
		}
		if (reportBatch.getTesterType() == TesterType.CMW) {
			compensation = new CmwCompensation(reportBatch.getSerial(), reportBatch.getName());
			defaultCompensation = new CmwCompensation(reportBatch.getSerial(), reportBatch.getName());
			lteGraph = ((CmwReportBatch) reportBatch).getUsableNonLteCount() <= 0 && (((CmwReportBatch) reportBatch).getOldUsableNonLteCount() <= 0 || operatorMode);
		} else {
			lteGraph = false;
			compensation = new CmuCompensation(reportBatch.getSerial(), reportBatch.getName());
			defaultCompensation = new CmuCompensation(reportBatch.getSerial(), reportBatch.getName());
		}
		if (!reportBatch.getCompensation().isEmpty()) {
			compensation.copyCompensation(reportBatch.getCompensation());
		}
		defaultCompensation.copyCompensation(compensation);
		placer.removeComponent("ShowGraph pointGraph");
		board = new PointGraph(reportBatch, compensation, operatorMode);
		board.addMouseListener(window);
		board.setName("ShowGraph pointGraph");
		placer.addComponent(board, 1, 2, 3, 7);
		fixButton.setEnabled(false);
		updateGraph();
		updateCompButtons();
	}

	public boolean isOperatorMode() {
		return operatorMode;
	}

	public Compensation getCompensation() {
		return compensation;
	}

	public void switchDirection() {
		rxGraph = !rxGraph;
		setGraphType(graphType.split("_")[0]);
	}

	protected void enableOrientationButtons() {
		lowUpButton.setEnabled(true);
		lowDownButton.setEnabled(true);
		midUpButton.setEnabled(true);
		midDownButton.setEnabled(true);
		highUpButton.setEnabled(true);
		highDownButton.setEnabled(true);
	}

	protected void disableOrientationButtons(){
		lowUpButton.setEnabled(false);
		lowDownButton.setEnabled(false);
		midUpButton.setEnabled(false);
		midDownButton.setEnabled(false);
		highUpButton.setEnabled(false);
		highDownButton.setEnabled(false);
	}

	public void updateGraph() {
		wcdma1Button.setEnabled(false);
		wcdma8Button.setEnabled(false);
		gsm900Button.setEnabled(false);
		gsm1800Button.setEnabled(false);
		bandButton.setEnabled(false);
		disableOrientationButtons();
		graphType = "";
		if (!lteGraph) {
			showNonLteBands();
			if (selectedBatch.getTesterType() == TesterType.CMU && selectedBatch.getUsableCount() > 0 ||
					selectedBatch.getTesterType() == TesterType.CMW && ((CmwReportBatch) selectedBatch).getUsableNonLteCount() > 0) {
				enableOrientationButtons();
			}
			if (selectedBatch.getUsableWcdma1Count() > 0 || selectedBatch.getOldUsableWcdma1Count() > 0 && !operatorMode) {
				wcdma1Button.setEnabled(true);
				if (!lteGraph) {
					setGraphType("wcdma1");
				}
			}
			if (selectedBatch.getUsableWcdma8Count() > 0 || selectedBatch.getOldUsableWcdma8Count() > 0 && !operatorMode) {
				wcdma8Button.setEnabled(true);
				if (graphType.isEmpty()) {
					setGraphType("wcdma8");
				}
			}
			if (selectedBatch.getUsableGsmCount() > 0 || selectedBatch.getOldUsableGsmCount() > 0 && !operatorMode) {
				gsm900Button.setEnabled(true);
				gsm1800Button.setEnabled(true);
				if (graphType.isEmpty()) {
					setGraphType("gsm900");
				}
			}
		} else {
			showLteBands();
			if (((CmwReportBatch) selectedBatch).getUsableLteCount() > 0 || ((CmwReportBatch) selectedBatch).getOldUsableLteCount() > 0 && !operatorMode) {
				wcdma1Button.setEnabled(true);
				wcdma8Button.setEnabled(true);
				gsm900Button.setEnabled(true);
				gsm1800Button.setEnabled(true);
				setGraphType("lte1");
				if (((CmwReportBatch) selectedBatch).getUsableLteCount() > 0) {
					enableOrientationButtons();
				}
			}
		}
		if (selectedBatch.getTesterType() == TesterType.CMW) {
			if ((((CmwReportBatch) selectedBatch).getUsableNonLteCount() > 0 || ((CmwReportBatch) selectedBatch).getOldUsableNonLteCount() > 0 && !operatorMode) &&
					(((CmwReportBatch) selectedBatch).getUsableLteCount() > 0 || ((CmwReportBatch) selectedBatch).getOldUsableLteCount() > 0 && !operatorMode)) {
				bandButton.setEnabled(true);
			}
		}
	}

	protected void showNonLteBands() {
		wcdma1Button.setName("ShowGraph wcdma1Button");
		wcdma1Button.setText("WCDMA Band 1");
		wcdma8Button.setName("ShowGraph wcdma8Button");
		wcdma8Button.setText("WCDMA Band 8");
		gsm900Button.setName("ShowGraph gsm900Button");
		gsm900Button.setText("GSM 900");
		gsm1800Button.setName("ShowGraph gsm1800Button");
		gsm1800Button.setText("GSM 1800");
	}

	protected void showLteBands() {
		wcdma1Button.setName("ShowGraph lte1Button");
		wcdma1Button.setText("LTE Band 1");
		wcdma8Button.setName("ShowGraph lte3Button");
		wcdma8Button.setText("LTE Band 3");
		gsm900Button.setName("ShowGraph lte7Button");
		gsm900Button.setText("LTE Band 7");
		gsm1800Button.setName("ShowGraph lte20Button");
		gsm1800Button.setText("LTE Band 20");
	}

	public void switchBands() {
		lteGraph = !lteGraph;
		updateGraph();
		updateResetButton();
	}

	public void resetCompensation() {
		compensation.resetCompensation(graphType);
		board.setCompensation(compensation);
		updateCompButtons();
		updateResetButton();
		updateFixButton();
	}

	public void addCompensation(int i, double value) {
		if (graphType.split("_")[1].equals("rx")) {
			value = 1 * value;
		} else {
			value = NumberHelper.oneDecimalPlaceOf(0.5 * value);
		}
		compensation.addCompensation(graphType, i, value);
		board.setCompensation(compensation);
		updateCompButtons();
		updateResetButton();
		updateFixButton();
	}

	public boolean isModified() {
		return compensation.differsFrom(defaultCompensation);
	}

	public void selectPoints(int x, int y) {
		board.selectPoints(x, y);
	}

	private void updateFixButton() {
		fixButton.setEnabled(compensation.differsFrom(defaultCompensation));
	}

	private void updateResetButton() {
		resetButton.setEnabled(NumberHelper.arrayHasValues(getBandCompensation()));
	}

	public double[] getBandCompensation() {
		double[] values = null;
		switch (graphType) {
			case "wcdma1_tx":
				values = compensation.getWcdma1TxValues();
				break;
			case "wcdma1_rx":
				values = compensation.getWcdma1RxValues();
				break;
			case "wcdma8_tx":
				values = compensation.getWcdma8TxValues();
				break;
			case "wcdma8_rx":
				values = compensation.getWcdma8RxValues();
				break;
			case "gsm900_tx":
				values = compensation.getGsm900TxValues();
				break;
			case "gsm900_rx":
				values = compensation.getGsm900RxValues();
				break;
			case "gsm1800_tx":
				values = compensation.getGsm1800TxValues();
				break;
			case "gsm1800_rx":
				values = compensation.getGsm1800RxValues();
				break;
			case "lte1_tx":
				values = ((CmwCompensation) compensation).getLte1TxValues();
				break;
			case "lte1_rx":
				values = ((CmwCompensation) compensation).getLte1RxValues();
				break;
			case "lte3_tx":
				values = ((CmwCompensation) compensation).getLte3TxValues();
				break;
			case "lte3_rx":
				values = ((CmwCompensation) compensation).getLte3RxValues();
				break;
			case "lte7_tx":
				values = ((CmwCompensation) compensation).getLte7TxValues();
				break;
			case "lte7_rx":
				values = ((CmwCompensation) compensation).getLte7RxValues();
				break;
			case "lte20_tx":
				values = ((CmwCompensation) compensation).getLte20TxValues();
				break;
			case "lte20_rx":
				values = ((CmwCompensation) compensation).getLte20RxValues();
				break;
		}
		return values;
	}

	public void setGraphType(String type) {
		if (!rxGraph) {
			graphType = type.concat("_tx");
		} else {
			graphType = type.concat("_rx");
		}
		board.setGraphType(graphType);
		switch (graphType) {
			case "wcdma1_tx":
				titleLabel.setText(name + " - WCDMA Band 1 Max RMS Power");
				profileValues = profile.getWcdma1InValues();
				break;
			case "wcdma1_rx":
				titleLabel.setText(name + " - WCDMA Band 1 RSCP Min");
				profileValues = profile.getWcdma1OutValues();
				break;
			case "wcdma8_tx":
				titleLabel.setText(name + " - WCDMA Band 8 Max RMS Power");
				profileValues = profile.getWcdma8InValues();
				break;
			case "wcdma8_rx":
				titleLabel.setText(name + " - WCDMA Band 8 RSCP Min");
				profileValues = profile.getWcdma8OutValues();
				break;
			case "gsm900_tx":
				titleLabel.setText(name + " - GSM 900 Average Power");
				profileValues = profile.getGsm900InValues();
				break;
			case "gsm900_rx":
				titleLabel.setText(name + " - GSM 900 RX Level");
				profileValues = profile.getGsm900OutValues();
				break;
			case "gsm1800_tx":
				titleLabel.setText(name + " - GSM 1800 Average Power");
				profileValues = profile.getGsm1800InValues();
				break;
			case "gsm1800_rx":
				titleLabel.setText(name + " - GSM 1800 RX Level");
				profileValues = profile.getGsm1800OutValues();
				break;
			case "lte1_tx":
				titleLabel.setText(name + " - LTE Band 1  Max. Output Power");
				profileValues = ((CmwProfile) profile).getLte1InValues();
				break;
			case "lte1_rx":
				titleLabel.setText(name + " - LTE Band 1 RSRP");
				profileValues = ((CmwProfile) profile).getLte1OutValues();
				break;
			case "lte3_tx":
				titleLabel.setText(name + " - LTE Band 3  Max. Output Power");
				profileValues = ((CmwProfile) profile).getLte3OutValues();
				break;
			case "lte3_rx":
				titleLabel.setText(name + " - LTE Band 3 RSRP");
				profileValues = ((CmwProfile) profile).getLte3InValues();
				break;
			case "lte7_tx":
				titleLabel.setText(name + " - LTE Band 7  Max. Output Power");
				profileValues = ((CmwProfile) profile).getLte7OutValues();
				break;
			case "lte7_rx":
				titleLabel.setText(name + " - LTE Band 7 RSRP");
				profileValues = ((CmwProfile) profile).getLte7InValues();
				break;
			case "lte20_tx":
				titleLabel.setText(name + " - LTE Band 20  Max. Output Power");
				profileValues = ((CmwProfile) profile).getLte20OutValues();
				break;
			case "lte20_rx":
				titleLabel.setText(name + " - LTE Band 20 RSRP");
				profileValues = ((CmwProfile) profile).getLte20InValues();
				break;
		}
		updateCompButtons();
		updateResetButton();
	}

	private void checkOverload(double[] values) {
		if (profileValues[0] + values[0] > ACCEPTABLE_LIMIT) {
			lowUpButton.setForeground(PROBLEMATIC_COLOR);
			lowDownButton.setForeground(PROBLEMATIC_COLOR);
			lowUpButton.setText("+ Low = (" + (profileValues[0] + values[0]) + ")");
			lowDownButton.setText("- Low = (" + (profileValues[0] + values[0]) + ")");
		} else if (profileValues[0] + values[0] > NORMAL_LIMIT) {
			lowUpButton.setForeground(ACCEPTABLE_COLOR);
			lowDownButton.setForeground(ACCEPTABLE_COLOR);
		} else {
			lowUpButton.setForeground(NORMAL_COLOR);
			lowDownButton.setForeground(NORMAL_COLOR);
		}
		if (profileValues[1] + values[1] > ACCEPTABLE_LIMIT) {
			midUpButton.setForeground(PROBLEMATIC_COLOR);
			midDownButton.setForeground(PROBLEMATIC_COLOR);
			midUpButton.setText("+ Mid = (" + (profileValues[1] + values[1]) + ")");
			midDownButton.setText("- Mid = (" + (profileValues[1] + values[1]) + ")");
		} else if (profileValues[1] + values[1] > NORMAL_LIMIT) {
			midUpButton.setForeground(ACCEPTABLE_COLOR);
			midDownButton.setForeground(ACCEPTABLE_COLOR);
		} else {
			midUpButton.setForeground(NORMAL_COLOR);
			midDownButton.setForeground(NORMAL_COLOR);
		}
		if (profileValues[2] + values[2] > ACCEPTABLE_LIMIT) {
			highUpButton.setForeground(PROBLEMATIC_COLOR);
			highDownButton.setForeground(PROBLEMATIC_COLOR);
			highUpButton.setText("+ High = (" + (profileValues[2] + values[2]) + ")");
			highDownButton.setText("- High = (" + (profileValues[2] + values[2]) + ")");
		} else if (profileValues[2] + values[2] > NORMAL_LIMIT) {
			highUpButton.setForeground(ACCEPTABLE_COLOR);
			highDownButton.setForeground(ACCEPTABLE_COLOR);
		} else {
			highUpButton.setForeground(NORMAL_COLOR);
			highDownButton.setForeground(NORMAL_COLOR);
		}
	}

	private void updateCompButtons() {
		double[] values = getBandCompensation();
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				if (values[i] > 0) {
					lowUpButton.setText("+ Low (" + values[i] + ")");
					lowDownButton.setText("- Low (0.0)");
				} else if (values[i] < 0) {
					lowUpButton.setText("+ Low (0.0)");
					lowDownButton.setText("- Low (" + values[i] + ")");
				} else {
					lowUpButton.setText(" + Low (0.0)");
					lowDownButton.setText("- Low (0.0)");
				}
			} else if (i == 1) {
				if (values[i] > 0) {
					midUpButton.setText("+ Mid (" + values[i] + ")");
					midDownButton.setText("- Mid (0.0)");
				} else if (values[i] < 0) {
					midUpButton.setText("+ Mid (0.0)");
					midDownButton.setText("- Mid (" + values[i] + ")");
				} else {
					midUpButton.setText("+ Mid (0.0)");
					midDownButton.setText("- Mid (0.0)");
				}
			} else {
				if (values[i] > 0) {
					highUpButton.setText("+ High (" + values[i] + ")");
					highDownButton.setText("- High (0.0)");
				} else if (values[i] < 0) {
					highUpButton.setText("+ High (0.0)");
					highDownButton.setText("- High (" + values[i] + ")");
				} else {
					highUpButton.setText("+ High (0.0)");
					highDownButton.setText("- High (0.0)");
				}

			}
		}
		checkOverload(values);
	}
}
