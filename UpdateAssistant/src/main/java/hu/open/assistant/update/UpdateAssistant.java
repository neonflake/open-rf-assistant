package hu.open.assistant.update;

import hu.open.assistant.update.graphical.UpdateNotice;
import hu.open.assistant.commons.data.FileHandler;
import hu.open.assistant.commons.data.IniParser;
import hu.open.assistant.commons.util.SystemHelper;
import hu.open.assistant.commons.data.UpdateHandler;
import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.update.graphical.UpdateWindow;
import hu.open.assistant.update.model.UpdatePackage;
import hu.open.assistant.update.model.UpdateTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Data controller for the application as well as the main entry point (which can receive an optional -force argument).
 * The applications panel has access to the data controller to request read, write and data related functions. It has
 * target selection and can read the main programs configuration and changelog. The controller can search for available
 * updates, start the update process and create a log about the update.
 */
public class UpdateAssistant {

	private static final double VERSION = 1.31;

	private final FileHandler fileHandler;
	private final UpdateHandler updateHandler;
	private final UpdateWindow window;
	private UpdateTarget updateTarget = UpdateTarget.UNKNOWN;
	private String networkFolder = "";
	private double currentVersion = 0;
	private final List<UpdatePackage> availablePackages = new ArrayList<>();
	private final boolean forceUpdate;

	public static void main(String[] args) {
		boolean forceUpdate = args.length > 0 && args[0].equals("-force");
		new UpdateAssistant(forceUpdate);
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public UpdateAssistant(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
		fileHandler = new FileHandler();
		updateHandler = new UpdateHandler(fileHandler);
		readConfig();
		if (!networkFolder.isEmpty()) {
			selectTargetByApplication();
			currentVersion = updateHandler.checkJarVersion(updateTarget.getLaunchFile(), updateTarget.getMainClass());
		}
		window = new UpdateWindow(this);
		window.startWindow();
	}

	private void readConfig() {
		Map<String, String> settingsMap = new IniParser(fileHandler).readIniFile("config.ini");
		for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
			if (entry.getKey().equals("networkFolder")) {
				networkFolder = fileHandler.getParentDirectory(entry.getValue()) + "\\Updater\\Release";
			}
		}
	}

	private void selectTargetByApplication() {
		List<String> files = fileHandler.listFiles(".");
		for (String file : files) {
			if (file.contains(".jar") && !file.equals("Updater.jar")) {
				updateTarget = selectTarget(file);
			}
		}
	}

	private UpdateTarget selectTarget(String applicationName) {
		switch (applicationName) {
			case "RFAssistant.jar":
				return UpdateTarget.RF;
		}
		return UpdateTarget.UNKNOWN;
	}

	public List<UpdatePackage> getAvailablePackages() {
		return availablePackages;
	}

	public double getVersion() {
		return VERSION;
	}

	public String getApplicationName() {
		return updateTarget.getLaunchFile().split("\\.")[0];
	}

	public double getCurrentVersion() {
		return currentVersion;
	}

	public void launchTarget() {
		SystemHelper.runJar(updateTarget.getLaunchFile());
	}

	public List<String> readChangeLog() {
		return fileHandler.readUtf8TextToList("changelog.txt", false);
	}

	public boolean startUpdate(UpdatePackage updatePackage) {
		if (fileHandler.fileNotInUse(updateTarget.getLaunchFile())) {
			if (updateHandler.unzipFile(networkFolder + "\\" + updatePackage.getFilename(), "", null)) {
				if (fileHandler.fileExists("cleanup.txt")) {
					List<String> filesToDelete = fileHandler.readUtf8TextToList("cleanup.txt", false);
					filesToDelete.forEach(fileHandler::deleteFile);
					fileHandler.deleteFile("cleanup.txt");
				}
				ArrayList<String> logText = new ArrayList<>();
				logText.add(TextHelper.createLogEntry("updated " + updateTarget.getName() + " v" + currentVersion + " to: v" + updatePackage.getVersion()));
				writeLog(logText);
				return true;
			} else {
                window.showNotification(UpdateNotice.UPDATE_FAIL);
			}
		} else {
            window.showNotification(UpdateNotice.FILE_IN_USE);
		}
		return false;
	}

	private void writeLog(ArrayList<String> text) {
		String logFolder = networkFolder + "\\..\\Log";
		if (!fileHandler.directoryExists(logFolder)) {
			fileHandler.createDirectory(logFolder);
		}
		fileHandler.writeUtf8Text(logFolder + "\\log.txt", text, true);
	}

	public boolean isUpdateAvailable() {
		return availablePackages.size() > 0 && availablePackages.get(0).getVersion() > currentVersion;
	}

	public void searchForUpdate() {
		if (updateTarget != UpdateTarget.UNKNOWN) {
			if (fileHandler.directoryExists(networkFolder)) {
				List<String> updateFiles = fileHandler.listFiles(networkFolder);
				for (String filename : updateFiles) {
					double updateVersion = updateHandler.checkZipVersion(networkFolder + "\\" + filename, updateTarget.getLaunchFile(), updateTarget.getMainClass());
					if (updateVersion > 0) {
						availablePackages.add(new UpdatePackage(updateTarget, updateVersion, filename));
					}
				}
				Collections.sort(availablePackages);
			} else {
                window.showNotification(UpdateNotice.NETWORK_FOLDER_PROBLEM);
			}
		} else {
            window.showNotification(UpdateNotice.DETECTION_PROBLEM);
		}
	}
}
