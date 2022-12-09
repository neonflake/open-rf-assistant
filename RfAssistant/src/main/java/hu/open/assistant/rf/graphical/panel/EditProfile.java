package hu.open.assistant.rf.graphical.panel;

import hu.open.assistant.rf.graphical.RfNotice;
import hu.open.assistant.commons.util.NumberHelper;
import hu.open.assistant.commons.graphical.gui.AssButton;
import hu.open.assistant.commons.graphical.gui.AssLabel;
import hu.open.assistant.commons.graphical.gui.AssTextField;
import hu.open.assistant.rf.RfAssistant;
import hu.open.assistant.rf.graphical.RfPanel;
import hu.open.assistant.rf.graphical.RfWindow;
import hu.open.assistant.rf.model.TesterType;
import hu.open.assistant.rf.model.compensation.CmwCompensation;
import hu.open.assistant.rf.model.compensation.Compensation;
import hu.open.assistant.rf.model.profile.CmwProfile;
import hu.open.assistant.rf.model.compensation.CmuCompensation;
import hu.open.assistant.rf.model.profile.Profile;

import java.awt.Dimension;

/**
 * GUI for editing values inside RF profiles. By selecting the bands from the navigation the text fields are filled with
 * values which can be edited. On CMW type profiles a band switcher is available to switch between 2G-3G or 4G bands.
 * The profiles entire value set can be reset to a default value or replaced with values imported from another profile.
 * Saving the modification creates a compensation on the RF profile and applies it.
 */
public class EditProfile extends RfPanel {

	private static final Dimension SMALL_LABEL_DIMENSION = new Dimension(200, 50);
	private static final Dimension LARGE_LABEL_DIMENSION = new Dimension(400, 50);
	private static final Dimension FIELD_DIMENSION = new Dimension(200, 50);
	private static final int SMALL_TEXT_SIZE = 16;
	private static final int LARGE_TEXT_SIZE = 20;

	private final AssButton bandButton;
	private final AssButton fixButton;
	private final AssButton wcdma1Button;
	private final AssButton wcdma8Button;
	private final AssButton gsm900Button;
	private final AssButton gsm1800Button;
	private final AssLabel profileContentLabel;
	private final AssLabel bandContentLabel;
	private final AssTextField lowInField;
	private final AssTextField lowOutField;
	private final AssTextField midInField;
	private final AssTextField midOutField;
	private final AssTextField highInField;
	private final AssTextField highOutField;
	private Profile profile;
	private Compensation compensation;
	private double[] inValues;
	private double[] outValues;
	private double[] inComp;
	private double[] outComp;
	private boolean valuesOk;
	private String selectedBand;
	private boolean showLte;
	boolean freshOpen;

	public EditProfile(RfWindow window, RfAssistant assistant) {
		super(window, assistant, "EditProfile");
		//placer.enableDebug();
		AssLabel titleLabel = new AssLabel("Profil csillapítás szerkesztése", TITLE_LABEL_TEXT_SIZE, TITLE_LABEL_DIMENSION);
		AssLabel rangeLabel = new AssLabel("Tartomány", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel inLabel = new AssLabel("IN (TX)", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel outLabel = new AssLabel("OUT (RX)", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel lowLabel = new AssLabel("LOW", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel midLabel = new AssLabel("MID", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel highLabel = new AssLabel("HIGH", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel profileLabel = new AssLabel("Profil:", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		AssLabel bandLabel = new AssLabel("Szabvány:", LARGE_TEXT_SIZE, SMALL_LABEL_DIMENSION);
		profileContentLabel = new AssLabel("", LARGE_TEXT_SIZE, LARGE_LABEL_DIMENSION);
		bandContentLabel = new AssLabel("", LARGE_TEXT_SIZE, LARGE_LABEL_DIMENSION);
		lowInField = new AssTextField("EditProfile lowInField", FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		midInField = new AssTextField("EditProfile midInField", FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		highInField = new AssTextField("EditProfile highInField", FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		lowOutField = new AssTextField("EditProfile lowOutField", FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		midOutField = new AssTextField("EditProfile midOutField", FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		highOutField = new AssTextField("EditProfile highOutField", FIELD_DIMENSION, SMALL_TEXT_SIZE, listener, "", true);
		wcdma1Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		wcdma8Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		gsm900Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		gsm1800Button = new AssButton("", "", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton defaultButton = new AssButton("EditProfile defaultButton", "Értékek alaphelyzetbe", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		AssButton backButton = new AssButton("EditProfile backButton", "Vissza", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		bandButton = new AssButton("EditProfile bandButton", "2G-3G / 4G váltás", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		fixButton = new AssButton("EditProfile fixButton", "Módosít", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, false);
		AssButton importButton = new AssButton("EditProfile importButton", "Értékek importálása", SIDE_BUTTON_TEXT_SIZE, SIDE_BUTTON_DIMENSION, listener, true);
		placer.addComponent(titleLabel, 1, 1, 3, 1);
		placer.addComponent(wcdma1Button, 4, 1, 1, 1);
		placer.addComponent(profileLabel, 1, 2, 1, 1);
		placer.addComponent(profileContentLabel, 2, 2, 2, 1);
		placer.addComponent(wcdma8Button, 4, 2, 1, 1);
		placer.addComponent(bandLabel, 1, 3, 1, 1);
		placer.addComponent(bandContentLabel, 2, 3, 2, 1);
		placer.addComponent(gsm900Button, 4, 3, 1, 1);
		placer.addComponent(rangeLabel, 1, 4, 1, 1);
		placer.addComponent(inLabel, 2, 4, 1, 1);
		placer.addComponent(outLabel, 3, 4, 1, 1);
		placer.addComponent(gsm1800Button, 4, 4, 1, 1);
		placer.addComponent(lowLabel, 1, 5, 1, 1);
		placer.addComponent(lowInField, 2, 5, 1, 1);
		placer.addComponent(lowOutField, 3, 5, 1, 1);
		placer.addComponent(bandButton, 4, 5, 1, 1);
		placer.addComponent(midLabel, 1, 6, 1, 1);
		placer.addComponent(midInField, 2, 6, 1, 1);
		placer.addComponent(midOutField, 3, 6, 1, 1);
		placer.addComponent(importButton, 4, 6, 1, 1);
		placer.addComponent(highLabel,1,7,1,1);
		placer.addComponent(highInField,2,7,1,1);
		placer.addComponent(highOutField,3,7,1,1);
		placer.addComponent(fixButton, 4, 7, 1, 1);
		placer.addComponent(defaultButton, 4, 8, 1, 1);
		placer.addComponent(backButton, 4, 9, 1, 1);
		placer.addImageComponent(logoImage,4,10,1,1);
	}

	private double[] checkFields() {
		double[] values = new double[6];
		try {
			values[0] = Double.parseDouble(lowInField.getText());
			values[1] = Double.parseDouble(midInField.getText());
			values[2] = Double.parseDouble(highInField.getText());
			values[3] = Double.parseDouble(lowOutField.getText());
			values[4] = Double.parseDouble(midOutField.getText());
			values[5] = Double.parseDouble(highOutField.getText());
			valuesOk = true;
		} catch (NumberFormatException exception) {
			window.showNotification(RfNotice.GENERIC_NOT_NUMBER);
			valuesOk = false;
		}
		for (int i = 0; i < 6; i++) {
			values[0] = NumberHelper.oneDecimalPlaceOf(values[0]);
		}
		return values;
	}

	public boolean saveCompensation() {
		updateValues();
		if (valuesOk) {
			if (!compensation.isEmpty()) {
				profile.addCompensation(compensation);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isModified() {
		return !compensation.isEmpty();
	}

	public void importValues(Profile importProfile) {
		calculateImportCompensation("wcdma1", importProfile.getWcdma1InValues(), importProfile.getWcdma1OutValues());
		calculateImportCompensation("wcdma8", importProfile.getWcdma8InValues(), importProfile.getWcdma8OutValues());
		calculateImportCompensation("gsm900", importProfile.getGsm900InValues(), importProfile.getGsm900OutValues());
		calculateImportCompensation("gsm1800", importProfile.getGsm1800InValues(), importProfile.getGsm1800OutValues());
		if (profile.getTesterType() == TesterType.CMW) {
			CmwProfile importCmwProfile = (CmwProfile) importProfile;
			calculateImportCompensation("lte1", importCmwProfile.getLte1InValues(), importCmwProfile.getLte1OutValues());
			calculateImportCompensation("lte3", importCmwProfile.getLte3InValues(), importCmwProfile.getLte3OutValues());
			calculateImportCompensation("lte7", importCmwProfile.getLte7InValues(), importCmwProfile.getLte7OutValues());
			calculateImportCompensation("lte20", importCmwProfile.getLte20InValues(), importCmwProfile.getLte20OutValues());
		}
		freshOpen = true;
		setSelectedBand(selectedBand);
	}

	public void setDefaultValues() {
		profile.createCenterCompensation();
		compensation.copyCompensation(profile.getCompensation());
		profile.getCompensation().resetWholeCompensation();
		freshOpen = true;
		setSelectedBand(selectedBand);
	}

	private void calculateImportCompensation(String band, double[] givenInValues, double[] givenOutValues) {
		selectedBand = band;
		setValues(selectedBand);
		calculateCompensation(givenInValues, givenOutValues);
	}

	public void updateValues() {
		double[] values = checkFields();
		double[] givenInValues = new double[3];
		double[] givenOutValues = new double[3];
		for (int i = 0; i < 6; i++) {
			if (i < 3) {
				givenInValues[i] = values[i];
			} else {
				givenOutValues[i - 3] = values[i];
			}
		}
		if (valuesOk) {
			calculateCompensation(givenInValues, givenOutValues);
		}
	}

	private void calculateCompensation(double[] givenInValues, double[] givenOutValues){
		for (int i = 0; i < 3; i++) {
			double diff = givenInValues[i] - (inValues[i] + inComp[i]);
			compensation.addCompensation(selectedBand + "_tx", i, diff);
			diff = givenOutValues[i] - (outValues[i] + outComp[i]);
			compensation.addCompensation(selectedBand + "_rx", i, diff);
		}
	}

	private void setValues(String band) {
		switch (band) {
			case "wcdma1":
				bandContentLabel.setText("WCDMA Band 1");
				inValues = profile.getWcdma1InValues();
				outValues = profile.getWcdma1OutValues();
				inComp = compensation.getWcdma1TxValues();
				outComp = compensation.getWcdma1RxValues();
				break;
			case "wcdma8":
				bandContentLabel.setText("WCDMA Band 8");
				inValues = profile.getWcdma8InValues();
				outValues = profile.getWcdma8OutValues();
				inComp = compensation.getWcdma8TxValues();
				outComp = compensation.getWcdma8RxValues();
				break;
			case "gsm900":
				bandContentLabel.setText("GSM 900");
				inValues = profile.getGsm900InValues();
				outValues = profile.getGsm900OutValues();
				inComp = compensation.getGsm900TxValues();
				outComp = compensation.getGsm900RxValues();
				break;
			case "gsm1800":
				bandContentLabel.setText("GSM 1800");
				inValues = profile.getGsm1800InValues();
				outValues = profile.getGsm1800OutValues();
				inComp = compensation.getGsm1800TxValues();
				outComp = compensation.getGsm1800RxValues();
				break;
			default:
				CmwCompensation cmwCompensation = (CmwCompensation) compensation;
				CmwProfile cmwProfile = (CmwProfile) profile;
				switch (band) {
					case "lte1":
						bandContentLabel.setText("LTE Band 1");
						inValues = cmwProfile.getLte1InValues();
						outValues = cmwProfile.getLte1OutValues();
						inComp = cmwCompensation.getLte1TxValues();
						outComp = cmwCompensation.getLte1RxValues();
						break;
					case "lte3":
						bandContentLabel.setText("LTE Band 3");
						inValues = cmwProfile.getLte3InValues();
						outValues = cmwProfile.getLte3OutValues();
						inComp = cmwCompensation.getLte3TxValues();
						outComp = cmwCompensation.getLte3RxValues();
						break;
					case "lte7":
						bandContentLabel.setText("LTE Band 7");
						inValues = cmwProfile.getLte7InValues();
						outValues = cmwProfile.getLte7OutValues();
						inComp = cmwCompensation.getLte7TxValues();
						outComp = cmwCompensation.getLte7RxValues();
						break;
					case "lte20":
						bandContentLabel.setText("LTE Band 20");
						inValues = cmwProfile.getLte20InValues();
						outValues = cmwProfile.getLte20OutValues();
						inComp = cmwCompensation.getLte20TxValues();
						outComp = cmwCompensation.getLte20RxValues();
						break;
				}
				break;
		}
	}

	public void setSelectedBand(String band) {
		if (freshOpen) {
			freshOpen = false;
		} else {
			updateValues();
		}
		selectedBand = band;
		if (valuesOk) {
			setValues(band);
			lowInField.setText("" + NumberHelper.oneDecimalPlaceOf(inValues[0] + inComp[0]));
			midInField.setText("" + NumberHelper.oneDecimalPlaceOf(inValues[1] + inComp[1]));
			highInField.setText("" + NumberHelper.oneDecimalPlaceOf(inValues[2] + inComp[2]));
			lowOutField.setText("" + NumberHelper.oneDecimalPlaceOf(outValues[0] + outComp[0]));
			midOutField.setText("" + NumberHelper.oneDecimalPlaceOf(outValues[1] + outComp[1]));
			highOutField.setText("" + NumberHelper.oneDecimalPlaceOf(outValues[2] + outComp[2]));
		}
	}

	public void switchBands() {
		showLte = !showLte;
		changeBands();
	}

	public void changeBands () {
		if (!showLte) {
			wcdma1Button.setName("EditProfile wcdma1Button");
			wcdma1Button.setText("WCDMA Band 1");
			wcdma8Button.setName("EditProfile wcdma8Button");
			wcdma8Button.setText("WCDMA Band 8");
			gsm900Button.setName("EditProfile gsm900Button");
			gsm900Button.setText("GSM 900");
			gsm1800Button.setName("EditProfile gsm1800Button");
			gsm1800Button.setText("GSM 1800");
			setSelectedBand("wcdma1");
		} else {
			wcdma1Button.setName("EditProfile lte1Button");
			wcdma1Button.setText("LTE Band 1");
			wcdma8Button.setName("EditProfile lte3Button");
			wcdma8Button.setText("LTE Band 3");
			gsm900Button.setName("EditProfile lte7Button");
			gsm900Button.setText("LTE Band 7");
			gsm1800Button.setName("EditProfile lte20Button");
			gsm1800Button.setText("LTE Band 20");
			setSelectedBand("lte1");
		}
	}

	public void openProfile(Profile profile) {
		this.profile = profile;
		freshOpen = true;
		profileContentLabel.setText(profile.getName());
		if (profile.getTesterType() == TesterType.CMW) {
			compensation = new CmwCompensation(profile.getSerial(), profile.getName());
			bandButton.setEnabled(true);
		} else {
			compensation = new CmuCompensation(profile.getSerial(), profile.getName());
			bandButton.setEnabled(false);
		}
		wcdma1Button.setEnabled(true);
		wcdma8Button.setEnabled(true);
		gsm900Button.setEnabled(true);
		gsm1800Button.setEnabled(true);
		fixButton.setEnabled(true);
		valuesOk = true;
		showLte = false;
		changeBands();
	}
}
