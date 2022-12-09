package hu.open.assistant.rf.graphical;

import hu.open.assistant.commons.graphical.gui.AssMenuItem;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.Dimension;

/**
 * The menubar of the main program window. Stores the different menu items organised into different menus. Some of its
 * elements can be disabled or enabled depending on the situation.
 */
public class RfMenu extends JMenuBar {

	private final JMenu fileMenu;
	private final JMenu supportTaskMenu;
	private final JMenu operatorTaskMenu;
	private final AssMenuItem reportBatchMenuItem;
	private final AssMenuItem usageMenuItem;
	private final AssMenuItem configMenuItem;
	private final AssMenuItem updateMenuItem;
	private final AssMenuItem jumpMenuItem;
	private final AssMenuItem helpMenuItem;
	private boolean supportEnabled;

	public RfMenu(RfWindow window) {
		fileMenu = new JMenu("Program");
		supportTaskMenu = new JMenu("Karbantartói feladatok");
		supportTaskMenu.setName("supportTaskMenu");
		supportTaskMenu.setEnabled(false);
		supportTaskMenu.addMouseListener(window);
		operatorTaskMenu = new JMenu("Operátori feladatok");
		AssMenuItem sourceMenuItem = new AssMenuItem("sourceMenuItem", "Forrás mappák", getClass().getResource("/images/source.png"), window, true);
		helpMenuItem = new AssMenuItem("helpMenuItem", "Felhasználói dokumentáció", getClass().getResource("/images/book.png"), window, true);
		updateMenuItem = new AssMenuItem("updateMenuItem", "Frissítések keresése", getClass().getResource("/images/updateVersion.png"), window, true);
		AssMenuItem exitMenuItem = new AssMenuItem("exitMenuItem", "Kilépés", getClass().getResource("/images/exit.png"), window, true);
		reportBatchMenuItem = new AssMenuItem("reportBatchMenuItem", "Mérések összesítése", getClass().getResource("/images/report.png"), window, false);
		usageMenuItem = new AssMenuItem("usageMenuItem", "CMU profil kihasználtság", getClass().getResource("/images/stat.png"), window, false);
		AssMenuItem cmuProfileMenuItem = new AssMenuItem("cmuProfileMenuItem", "CMU profilok", getClass().getResource("/images/cmu200.png"), window, true);
		AssMenuItem syncProfileMenuItem = new AssMenuItem("syncProfileMenuItem", "CMU profil szinkron", getClass().getResource("/images/sync.png"), window, true);
		AssMenuItem cmwProfileMenuItem = new AssMenuItem("cmwProfileMenuItem", "CMW profilok", getClass().getResource("/images/cmw290.png"), window, true);
		AssMenuItem logMenuItem = new AssMenuItem("logMenuItem", "Profil és adatbázis történet", getClass().getResource("/images/log.png"), window, true);
		AssMenuItem networkMenuItem = new AssMenuItem("networkMenuItem", "Hálózati mappa", getClass().getResource("/images/networkSmall.png"), window, true);
		configMenuItem = new AssMenuItem("configMenuItem", "Beállítások", getClass().getResource("/images/settings.png"), window, true);
		jumpMenuItem = new AssMenuItem("jumpMenuItem", "", getClass().getResource("/images/back.png"), window, true);
		jumpMenuItem.enableGradientBackground();
		AssMenuItem equipmentInfoMenuItem = new AssMenuItem("equipmentInfoMenuItem", "Készülék információ", getClass().getResource("/images/profile.png"), window, true);
		AssMenuItem equipmentListMenuItem = new AssMenuItem("equipmentListMenuItem", "Készülékek szerkesztése", getClass().getResource("/images/profileEdit.png"), window, true);
		AssMenuItem reportListMenuItem = new AssMenuItem("reportListMenuItem", "Mai riportok", getClass().getResource("/images/reports.png"), window, true);
		AssMenuItem reportSearchMenuItem = new AssMenuItem("reportSearchMenuItem", "Riport keresés", getClass().getResource("/images/searchSmall.png"), window, true);
		AssMenuItem backupMenuItem = new AssMenuItem("backupMenuItem", "Biztonsági mentések", getClass().getResource("/images/database.png"), window, true);
		fileMenu.add(configMenuItem);
		fileMenu.add(networkMenuItem);
		fileMenu.add(helpMenuItem);
		fileMenu.add(updateMenuItem);
		fileMenu.add(exitMenuItem);
		supportTaskMenu.add(sourceMenuItem);
		supportTaskMenu.add(reportBatchMenuItem);
		supportTaskMenu.add(usageMenuItem);
		supportTaskMenu.add(cmuProfileMenuItem);
		supportTaskMenu.add(syncProfileMenuItem);
		supportTaskMenu.add(cmwProfileMenuItem);
		supportTaskMenu.add(equipmentListMenuItem);
		supportTaskMenu.add(backupMenuItem);
		operatorTaskMenu.add(equipmentInfoMenuItem);
		operatorTaskMenu.add(reportListMenuItem);
		operatorTaskMenu.add(reportSearchMenuItem);
		operatorTaskMenu.add(logMenuItem);
		this.add(fileMenu);
		this.add(supportTaskMenu);
		this.add(operatorTaskMenu);
		String distance = "";
		for (int i = 0; i < 115; i++) {
			distance = distance.concat(" ");
		}
		this.setPreferredSize(new Dimension(0,25));
		JMenu blankMenu=new JMenu(distance);
		blankMenu.setEnabled(false);
		this.add(blankMenu);
		this.add(jumpMenuItem);
		supportEnabled = false;
	}

	public void openSupport() {
		supportTaskMenu.doClick();
	}

	public void enableSupport() {
		supportTaskMenu.setEnabled(true);
		supportEnabled = true;
	}

	public void disableOperator() {
		operatorTaskMenu.setEnabled(false);
		configMenuItem.setEnabled(false);
		updateMenuItem.setEnabled(false);
		helpMenuItem.setEnabled(false);
	}

	public void enableMenu() {
		operatorTaskMenu.setEnabled(true);
		if (supportEnabled) {
			supportTaskMenu.setEnabled(true);
		}
		fileMenu.setEnabled(true);
		jumpMenuItem.setEnabled(true);

	}

	public void disableMenu() {
		operatorTaskMenu.setEnabled(false);
		supportTaskMenu.setEnabled(false);
		fileMenu.setEnabled(false);
		jumpMenuItem.setEnabled(false);
	}

	public void disableJumpMenuItem() {
		jumpMenuItem.setVisible(false);
	}

	public void enableJumpMenuItem(String target) {
		jumpMenuItem.setName("jumpMenuItem " + target);
		switch (target) {
			case "SelectReport":
				jumpMenuItem.setText("Ugrás a mai riportokhoz");
				break;
			case "SelectEquipment":
				jumpMenuItem.setText("Ugrás a készülék információhoz");
				break;
		}
		jumpMenuItem.setVisible(true);
	}

	public void enableBatchMenuItem() {
		reportBatchMenuItem.setEnabled(true);
	}

	public void enableUsageMenuItem() {
		usageMenuItem.setEnabled(true);
	}

	public void disableUsageMenuItem() {
		usageMenuItem.setEnabled(false);
	}
}
