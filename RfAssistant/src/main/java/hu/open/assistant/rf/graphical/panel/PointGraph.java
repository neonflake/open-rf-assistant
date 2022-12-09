package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.commons.graphical.AssImage;
import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.report.Report;
import hu.open.assistant.rf.model.report.batch.ReportBatch;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;
import hu.open.assistant.rf.model.report.CmwReport;
import hu.open.assistant.rf.model.Point;
import hu.open.assistant.rf.model.report.limits.ReportLimits;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel that draws a point graph from the given report batch. It contains logic to choose the correct background image
 * depending on the graph type and to create and draw the points on it. Drawing is done in three different colors
 * (actual, previous, irrelevant measurements) and the points are stored. The panel can react to mouse inputs which
 * selects the points belonging to the same test report. The points are in three columns (LOW, MID and HIGH channels)
 * and drawing starts horizontally in the middle and moves between the two sides of the column. The points vertical
 * position represents the measured value.
 */
public class PointGraph extends JPanel {

	private static final Dimension BOARD_DIMENSION = new Dimension(600, 490);
	private static final Color BOARD_BACKGROUND_COLOR = Color.WHITE;
	private static final Color IRRELEVANT_POINT_COLOR = new Color(255, 255, 255);
	private static final Color INVALID_POINT_COLOR = new Color(144, 144, 144);
	private static final Color VALID_POINT_COLOR = new Color(0, 0, 0);
	private static final int BOARD_OFFSET = 10;
	public static final int NARROW_GRAPH_INTERVAL = 5;
	public static final int WIDE_GRAPH_INTERVAL = 7;
	private static final int POINT_WIDTH = 3;
	private static final int POINT_HEIGHT = 3;
	private static final int HORIZONTAL_POINT_START = 149;
	private static final int VERTICAL_POINT_START = 240;
	private static final int CHANNEL_HORIZONTAL_OFFSET = 150;
	private static final int UNIT_HEIGHT = 20;
	private static final int MIN_DRAW_HEIGHT = 40;
	private static final int MAX_DRAW_HEIGHT = 440;
	public static final int REVERT_POINT_COUNT = 37;
	public static final int HORIZONTAL_POINT_STEP = 4;

	private final ReportLimits limits;
	private final ReportBatch reportBatch;
	private final List<Point> points;
	private final List<Integer> selectedGroups;
	private final boolean operatorMode;
	private Compensation compensation;
	private String graphType;
	private AssImage graphImage;
	private int lastXPosition;
	private int lastYPosition;

	public PointGraph(ReportBatch reportBatch, Compensation compensation, boolean operatorMode) {
		this.reportBatch = reportBatch;
		this.compensation = compensation;
		this.operatorMode = operatorMode;
		limits = reportBatch.getLimits();
		setBackground(BOARD_BACKGROUND_COLOR);
		setPreferredSize(BOARD_DIMENSION);
		setMinimumSize(BOARD_DIMENSION);
		points = new ArrayList<>();
		selectedGroups = new ArrayList<>();
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
		initGraph();
		initPoints();
	}

	public void setCompensation(Compensation compensation) {
		this.compensation = compensation;
		initPoints();
	}

	public void selectPoints(int x, int y) {
		this.setToolTipText(null);
		int selectedGroup = 0;
		if (lastXPosition != x || lastYPosition != y) {
			selectedGroups.clear();
		}
		if (selectedGroups.isEmpty()) {
			for (Point point : points) {
				if (point.isInside(x, y) && y >= MIN_DRAW_HEIGHT && y <= MAX_DRAW_HEIGHT) {
					selectedGroups.add(point.getGroupId());
					if (selectedGroup == 0) {
						selectedGroup = point.getGroupId();
					}
				}
			}
		} else {
			selectedGroups.remove(0);
			if (!selectedGroups.isEmpty()) {
				selectedGroup = selectedGroups.get(0);
			}
		}
		for (Point point : points) {
			if (point.getGroupId() == selectedGroup) {
				point.selectPoint();
				this.setToolTipText(point.getSource());
			} else {
				point.unSelectPoint();
			}
		}
		lastXPosition = x;
		lastYPosition = y;
		repaint();
	}

	private void initPoints() {
		lastXPosition = 0;
		lastYPosition = 0;
		boolean move;
		Color color;
		int low = 0;
		int mid = 0;
		int high = 0;
		int horizontalPosition = HORIZONTAL_POINT_START;
		int verticalPosition = VERTICAL_POINT_START;
		int alingment = 0;
		int direction = 1;
		int step = 0;
		int count = 1;
		boolean createPoint;
		List<Report> reports = reportBatch.getAllUsableReports();
		points.clear();
		for (Report report : reports) {
			move = true;
			createPoint = false;
			color = VALID_POINT_COLOR;
			if (reportBatch.getProfile().getLogBatch() != null) {
				if (!report.getDateTime().isAfter(reportBatch.getProfile().getLogBatch().getLastModificationDate())) {
					color = INVALID_POINT_COLOR;
					move = false;
				}
				if (reportBatch.getProfile().getLogBatch().getSecondModificationDate() != null) {
					if (!report.getDateTime().isAfter(reportBatch.getProfile().getLogBatch().getSecondModificationDate())) {
						color = IRRELEVANT_POINT_COLOR;
					}
				}
			}
			if (graphType.equals("wcdma1_tx") && report.hasWcdma1()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getWcdma1TxValues()[0] + report.getWcdma1TxValues()[0] - limits.getWcdma1TxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getWcdma1TxValues()[1] + report.getWcdma1TxValues()[1] - limits.getWcdma1TxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getWcdma1TxValues()[2] + report.getWcdma1TxValues()[2] - limits.getWcdma1TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getWcdma1TxValues()[0] - limits.getWcdma1TxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getWcdma1TxValues()[1] - limits.getWcdma1TxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getWcdma1TxValues()[2] - limits.getWcdma1TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("wcdma1_rx") && report.hasWcdma1()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getWcdma1RxValues()[0] + report.getWcdma1RxValues()[0] - limits.getWcdma1RxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getWcdma1RxValues()[1] + report.getWcdma1RxValues()[1] - limits.getWcdma1RxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getWcdma1RxValues()[2] + report.getWcdma1RxValues()[2] - limits.getWcdma1RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getWcdma1RxValues()[0] - limits.getWcdma1RxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getWcdma1RxValues()[1] - limits.getWcdma1RxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getWcdma1RxValues()[2] - limits.getWcdma1RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("wcdma8_tx") && report.hasWcdma8()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getWcdma8TxValues()[0] + report.getWcdma8TxValues()[0] - limits.getWcdma8TxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getWcdma8TxValues()[1] + report.getWcdma8TxValues()[1] - limits.getWcdma8TxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getWcdma8TxValues()[2] + report.getWcdma8TxValues()[2] - limits.getWcdma8TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getWcdma8TxValues()[0] - limits.getWcdma8TxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getWcdma8TxValues()[1] - limits.getWcdma8TxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getWcdma8TxValues()[2] - limits.getWcdma8TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("wcdma8_rx") && report.hasWcdma8()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getWcdma8RxValues()[0] + report.getWcdma8RxValues()[0] - limits.getWcdma8RxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getWcdma8RxValues()[1] + report.getWcdma8RxValues()[1] - limits.getWcdma8RxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getWcdma8RxValues()[2] + report.getWcdma8RxValues()[2] - limits.getWcdma8RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getWcdma8RxValues()[0] - limits.getWcdma8RxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getWcdma8RxValues()[1] - limits.getWcdma8RxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getWcdma8RxValues()[2] - limits.getWcdma8RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("gsm900_tx") && report.hasGsm()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getGsm900TxValues()[0] + report.getGsm900TxValues()[0] - limits.getGsm900TxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getGsm900TxValues()[1] + report.getGsm900TxValues()[1] - limits.getGsm900TxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getGsm900TxValues()[2] + report.getGsm900TxValues()[2] - limits.getGsm900TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getGsm900TxValues()[0] - limits.getGsm900TxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getGsm900TxValues()[1] - limits.getGsm900TxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getGsm900TxValues()[2] - limits.getGsm900TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("gsm900_rx") && report.hasGsm()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getGsm900RxValues()[0] + report.getGsm900RxValues()[0] - limits.getGsm900RxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getGsm900RxValues()[1] + report.getGsm900RxValues()[1] - limits.getGsm900RxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getGsm900RxValues()[2] + report.getGsm900RxValues()[2] - limits.getGsm900RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getGsm900RxValues()[0] - limits.getGsm900RxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getGsm900RxValues()[1] - limits.getGsm900RxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getGsm900RxValues()[2] - limits.getGsm900RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("gsm1800_tx") && report.hasGsm()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getGsm1800TxValues()[0] + report.getGsm1800TxValues()[0] - limits.getGsm1800TxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getGsm1800TxValues()[1] + report.getGsm1800TxValues()[1] - limits.getGsm1800TxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getGsm1800TxValues()[2] + report.getGsm1800TxValues()[2] - limits.getGsm1800TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getGsm1800TxValues()[0] - limits.getGsm1800TxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getGsm1800TxValues()[1] - limits.getGsm1800TxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getGsm1800TxValues()[2] - limits.getGsm1800TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("gsm1800_rx") && report.hasGsm()) {
				createPoint = true;
				if (move) {
					low = (int) ((compensation.getGsm1800RxValues()[0] + report.getGSM1800RxValues()[0] - limits.getGsm1800RxExp()) * UNIT_HEIGHT);
					mid = (int) ((compensation.getGsm1800RxValues()[1] + report.getGSM1800RxValues()[1] - limits.getGsm1800RxExp()) * UNIT_HEIGHT);
					high = (int) ((compensation.getGsm1800RxValues()[2] + report.getGSM1800RxValues()[2] - limits.getGsm1800RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((report.getGSM1800RxValues()[0] - limits.getGsm1800RxExp()) * UNIT_HEIGHT);
					mid = (int) ((report.getGSM1800RxValues()[1] - limits.getGsm1800RxExp()) * UNIT_HEIGHT);
					high = (int) ((report.getGSM1800RxValues()[2] - limits.getGsm1800RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte1_tx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte1TxValues()[0] + ((CmwReport) report).getLte1TxValues()[0] - ((CmwReportLimits) limits).getLte1TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte1TxValues()[1] + ((CmwReport) report).getLte1TxValues()[1] - ((CmwReportLimits) limits).getLte1TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte1TxValues()[2] + ((CmwReport) report).getLte1TxValues()[2] - ((CmwReportLimits) limits).getLte1TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte1TxValues()[0] - ((CmwReportLimits) limits).getLte1TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte1TxValues()[1] - ((CmwReportLimits) limits).getLte1TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte1TxValues()[2] - ((CmwReportLimits) limits).getLte1TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte1_rx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte1RxValues()[0] + ((CmwReport) report).getLte1RxValues()[0] - ((CmwReportLimits) limits).getLte1RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte1RxValues()[1] + ((CmwReport) report).getLte1RxValues()[1] - ((CmwReportLimits) limits).getLte1RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte1RxValues()[2] + ((CmwReport) report).getLte1RxValues()[2] - ((CmwReportLimits) limits).getLte1RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte1RxValues()[0] - ((CmwReportLimits) limits).getLte1RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte1RxValues()[1] - ((CmwReportLimits) limits).getLte1RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte1RxValues()[2] - ((CmwReportLimits) limits).getLte1RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte3_tx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte3TxValues()[0] + ((CmwReport) report).getLte3TxValues()[0] - ((CmwReportLimits) limits).getLte3TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte3TxValues()[1] + ((CmwReport) report).getLte3TxValues()[1] - ((CmwReportLimits) limits).getLte3TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte3TxValues()[2] + ((CmwReport) report).getLte3TxValues()[2] - ((CmwReportLimits) limits).getLte3TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte3TxValues()[0] - ((CmwReportLimits) limits).getLte3TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte3TxValues()[1] - ((CmwReportLimits) limits).getLte3TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte3TxValues()[2] - ((CmwReportLimits) limits).getLte3TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte3_rx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte3RxValues()[0] + ((CmwReport) report).getLte3RxValues()[0] - ((CmwReportLimits) limits).getLte3RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte3RxValues()[1] + ((CmwReport) report).getLte3RxValues()[1] - ((CmwReportLimits) limits).getLte3RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte3RxValues()[2] + ((CmwReport) report).getLte3RxValues()[2] - ((CmwReportLimits) limits).getLte3RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte3RxValues()[0] - ((CmwReportLimits) limits).getLte3RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte3RxValues()[1] - ((CmwReportLimits) limits).getLte3RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte3RxValues()[2] - ((CmwReportLimits) limits).getLte3RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte7_tx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte7TxValues()[0] + ((CmwReport) report).getLte7TxValues()[0] - ((CmwReportLimits) limits).getLte7TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte7TxValues()[1] + ((CmwReport) report).getLte7TxValues()[1] - ((CmwReportLimits) limits).getLte7TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte7TxValues()[2] + ((CmwReport) report).getLte7TxValues()[2] - ((CmwReportLimits) limits).getLte7TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte7TxValues()[0] - ((CmwReportLimits) limits).getLte7TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte7TxValues()[1] - ((CmwReportLimits) limits).getLte7TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte7TxValues()[2] - ((CmwReportLimits) limits).getLte7TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte7_rx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte7RxValues()[0] + ((CmwReport) report).getLte7RxValues()[0] - ((CmwReportLimits) limits).getLte7RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte7RxValues()[1] + ((CmwReport) report).getLte7RxValues()[1] - ((CmwReportLimits) limits).getLte7RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte7RxValues()[2] + ((CmwReport) report).getLte7RxValues()[2] - ((CmwReportLimits) limits).getLte7RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte7RxValues()[0] - ((CmwReportLimits) limits).getLte7RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte7RxValues()[1] - ((CmwReportLimits) limits).getLte7RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte7RxValues()[2] - ((CmwReportLimits) limits).getLte7RxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte20_tx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte20TxValues()[0] + ((CmwReport) report).getLte20TxValues()[0] - ((CmwReportLimits) limits).getLte20TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte20TxValues()[1] + ((CmwReport) report).getLte20TxValues()[1] - ((CmwReportLimits) limits).getLte20TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte20TxValues()[2] + ((CmwReport) report).getLte20TxValues()[2] - ((CmwReportLimits) limits).getLte20TxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte20TxValues()[0] - ((CmwReportLimits) limits).getLte20TxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte20TxValues()[1] - ((CmwReportLimits) limits).getLte20TxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte20TxValues()[2] - ((CmwReportLimits) limits).getLte20TxExp()) * UNIT_HEIGHT);
				}
			} else if (graphType.equals("lte20_rx") && ((CmwReport) report).hasLte()) {
				createPoint = true;
				if (move) {
					low = (int) ((((CmwCompensation) compensation).getLte20RxValues()[0] + ((CmwReport) report).getLte20RxValues()[0] - ((CmwReportLimits) limits).getLte20RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwCompensation) compensation).getLte20RxValues()[1] + ((CmwReport) report).getLte20RxValues()[1] - ((CmwReportLimits) limits).getLte20RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwCompensation) compensation).getLte20RxValues()[2] + ((CmwReport) report).getLte20RxValues()[2] - ((CmwReportLimits) limits).getLte20RxExp()) * UNIT_HEIGHT);
				} else {
					low = (int) ((((CmwReport) report).getLte20RxValues()[0] - ((CmwReportLimits) limits).getLte20RxExp()) * UNIT_HEIGHT);
					mid = (int) ((((CmwReport) report).getLte20RxValues()[1] - ((CmwReportLimits) limits).getLte20RxExp()) * UNIT_HEIGHT);
					high = (int) ((((CmwReport) report).getLte20RxValues()[2] - ((CmwReportLimits) limits).getLte20RxExp()) * UNIT_HEIGHT);
				}
			}
			if (createPoint) {
				if (color == VALID_POINT_COLOR || !operatorMode) {
					points.add(new Point(count, horizontalPosition + alingment + step * direction, verticalPosition - low, POINT_WIDTH, POINT_HEIGHT, color, report.getExtendedFilename()));
					points.add(new Point(count, horizontalPosition + CHANNEL_HORIZONTAL_OFFSET + alingment + step * direction, verticalPosition - mid, POINT_WIDTH, POINT_HEIGHT, color, report.getExtendedFilename()));
					points.add(new Point(count, horizontalPosition + CHANNEL_HORIZONTAL_OFFSET * 2 + alingment + step * direction, verticalPosition - high, POINT_WIDTH, POINT_HEIGHT, color, report.getExtendedFilename()));
					if (count % REVERT_POINT_COUNT == 0) {
						step = 0;
						direction = 1;
						if (alingment == 0) {
							alingment = -1;
						} else {
							alingment = 0;
						}
					} else {
						if (direction == 1) {
							step += HORIZONTAL_POINT_STEP;
						}
						direction *= -1;
					}
					count++;
				}
			}
		}
	}

	private void initGraph() {
		int interval = 0;
		switch (graphType) {
			case "wcdma1_tx":
			case "wcdma8_tx":
				if (graphType.equals("wcdma1_tx")) {
					interval = limits.getWcdma1TxInterval();
				} else {
					interval = limits.getWcdma8TxInterval();
				}
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphWcdmaTxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphWcdmaTxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphWcdmaTx.png"));
				}
				break;
			case "wcdma1_rx":
			case "wcdma8_rx":
				if (graphType.equals("wcdma1_rx")) {
					interval = limits.getWcdma1RxInterval();
				} else {
					interval = limits.getWcdma8RxInterval();
				}
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphWcdmaRxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphWcdmaRxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphWcdmaRx.png"));
				}
				break;
			case "gsm900_tx":
				interval = limits.getGsm900TxInterval();
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphGsm900TxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphGsm900TxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphGsm900Tx.png"));
				}
				break;
			case "gsm1800_tx":
				interval = limits.getGsm1800TxInterval();
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphGsm1800TxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphGsm1800TxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphGsm1800Tx.png"));
				}
				break;
			case "gsm900_rx":
			case "gsm1800_rx":
				if (graphType.equals("gsm900_rx")) {
					interval = limits.getGsm900RxInterval();
				} else {
					interval = limits.getGsm1800RxInterval();
				}
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphGsmRxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphGsmRxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphGsmRx.png"));
				}
				break;
			case "lte1_tx":
			case "lte3_tx":
			case "lte7_tx":
			case "lte20_tx":
				switch (graphType) {
					case "lte1_tx":
						interval = ((CmwReportLimits) limits).getLte1TxInterval();
						break;
					case "lte3_tx":
						interval = ((CmwReportLimits) limits).getLte3TxInterval();
						break;
					case "lte7_tx":
						interval = ((CmwReportLimits) limits).getLte7TxInterval();
						break;
					case "lte20_tx":
						interval = ((CmwReportLimits) limits).getLte20TxInterval();
						break;
				}
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphLteTxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphLteTxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphLteTx.png"));
				}
				break;
			case "lte1_rx":
			case "lte3_rx":
			case "lte7_rx":
			case "lte20_rx":
				switch (graphType) {
					case "lte1_rx":
						interval = ((CmwReportLimits) limits).getLte1RxInterval();
						break;
					case "lte3_rx":
						interval = ((CmwReportLimits) limits).getLte3RxInterval();
						break;
					case "lte7_rx":
						interval = ((CmwReportLimits) limits).getLte7RxInterval();
						break;
					case "lte20_rx":
						interval = ((CmwReportLimits) limits).getLte20RxInterval();
						break;
				}
				if (interval == NARROW_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphLteRxNarrow.png"));
				} else if (interval == WIDE_GRAPH_INTERVAL) {
					graphImage = new AssImage(getClass().getResource("/images/graphLteRxWide.png"));
				} else {
					graphImage = new AssImage(getClass().getResource("/images/graphLteRx.png"));
				}
				break;
		}
	}

	@Override public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.drawImage(graphImage.getImage(), BOARD_OFFSET, BOARD_OFFSET, null);
		List<Point> selectedPoints = new ArrayList<>();
		for (Point point : points) {
			if (!point.isSelected()) {
				graphics.setColor(point.getColor());
				if (point.getYPosition() >= MIN_DRAW_HEIGHT && point.getYPosition() <= MAX_DRAW_HEIGHT) {
					graphics.fillRect(point.getXPosition(), point.getYPosition(), point.getXSize(), point.getYSize());
				}
			} else {
				selectedPoints.add(point);
			}
		}
		for (Point point : selectedPoints) {
			graphics.setColor(point.getColor());
			if (point.getYPosition() >= MIN_DRAW_HEIGHT && point.getYPosition() <= MAX_DRAW_HEIGHT) {
				graphics.fillRect(point.getXPosition(), point.getYPosition(), point.getXSize(), point.getYSize());
			}
		}
	}
}
