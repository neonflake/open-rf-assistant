package hu.open.assistant.commons.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A class responsible for handling update tasks for all Assistant applications.
 */
public class UpdateHandler {

    private final FileHandler fileHandler;

    /**
     * Initialise the UpdateHandler with the given FileHandler which provides disk access.
     */
    public UpdateHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    /**
     * Check the program version inside the provided update (.zip) file. Version information is checked in the manifest
     * information file.
     *
     * @param filePath  target update (.zip) file
     * @param jarName   target .jar file inside the update file
     * @param mainClass target application class in manifest information
     * @return program version when update file is valid, 0 otherwise
     */
    public double checkZipVersion(String filePath, String jarName, String mainClass) {
        unzipFile(filePath, "temp.jar", jarName);
        if (fileHandler.fileExists("temp.jar")) {
            double version = checkJarVersion("temp.jar", mainClass);
            fileHandler.deleteFile("temp.jar");
            return version;
        }
        return 0;
    }

    /**
     * Check the program version of the provided .jar file. Version information is checked in the manifest information file.
     *
     * @param filePath  target .jar file
     * @param mainClass target application class in manifest information
     * @return program version when .jar file is valid, 0 otherwise
     */
    public double checkJarVersion(String filePath, String mainClass) {
        unzipFile(filePath, "MANIFEST.MF", "META-INF/MANIFEST.MF");
        boolean classMatched = false;
        if (fileHandler.fileExists("MANIFEST.MF")) {
            List<String> manifest = fileHandler.readUtf8TextToList("MANIFEST.MF", false);
            fileHandler.deleteFile("MANIFEST.MF");
            for (String line : manifest) {
                if (line.contains("Main-Class")) {
                    String[] lineParts = line.split("\\.");
                    classMatched = lineParts[lineParts.length - 1].equals(mainClass);
                }
                if (line.contains("Specification-Version")) {
                    if (classMatched) {
                        return (Double.parseDouble(line.split(" ")[1]));
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Update the updater (UpdateAssistant) application from the main application. When a new version is available in
     * the network folder it automatically updates the updater (copies new version locally). The updaters update files
     * are located in the root Updater folder.
     *
     * @param networkFolder main applications network folder
     */
    public void updateUpdater(String networkFolder) {
        double updaterVersion = checkJarVersion("Updater.jar", "UpdateAssistant");
        String updateFolder = fileHandler.getParentDirectory(networkFolder) + "\\Updater";
        String latestUpdater = "";
        for (String filename : fileHandler.listFiles(updateFolder)) {
            double archivedUpdaterVersion = checkZipVersion(updateFolder + "\\" + filename, "Updater.jar", "UpdateAssistant");
            if (archivedUpdaterVersion > updaterVersion) {
                latestUpdater = filename;
            }
        }
        if (!latestUpdater.isEmpty()) {
            unzipFile(updateFolder + "\\" + latestUpdater, "Updater.jar", "Updater.jar");
        }
    }

    /**
     * Unzip a whole .zip archive to the given location, or only one given file from it when needed.
     *
     * @param filePath     target .zip file
     * @param targetPath   folder to extract the archives content
     * @param specificFile filename when only a specific file needs to be extracted (null extracts the entire archive)
     * @return true of extraction was successful, false otherwise
     */
    public boolean unzipFile(String filePath, String targetPath, String specificFile) {
        Path target = Paths.get(targetPath);
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (specificFile == null) {
                    Path entryTarget = target.resolve(zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        if (!fileHandler.directoryExists(entryTarget.toString())) {
                            Files.createDirectory(entryTarget);
                        }
                    } else {
                        Files.copy(zipInputStream, entryTarget, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zipEntry = zipInputStream.getNextEntry();
                } else {
                    if (!zipEntry.isDirectory() && zipEntry.getName().equals(specificFile)) {
                        Files.copy(zipInputStream, target, StandardCopyOption.REPLACE_EXISTING);
                        zipEntry = null;
                    } else {
                        zipEntry = zipInputStream.getNextEntry();
                    }
                }
            }
            zipInputStream.close();
        } catch (IOException exception) {
            System.out.println("Error while unzipping: " + filePath);
            return false;
        }
        return true;
    }
}
