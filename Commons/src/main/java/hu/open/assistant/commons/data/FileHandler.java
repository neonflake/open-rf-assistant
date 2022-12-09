package hu.open.assistant.commons.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible for disk handling for all Assistant applications. By convention the FileHandler must be instanced
 * only once per application and injected to other objects when needed.
 */
public class FileHandler {

	private final List<String> failedFilenames = new ArrayList<>();

	/**
	 * Create the FileHandler.
	 */
	public FileHandler() {

	}


	/**
	 * Read the file content from disk in UTF-8 format.
	 *
	 * @param filePath   target file
	 * @param checkInUse check if target file is currently used by another application
	 * @return a list of Strings (lines) as raw text or null if file doesn't exist (or is in use)
	 */
	public List<String> readUtf8TextToList(String filePath, boolean checkInUse) {
		return readTextToList(filePath, checkInUse, StandardCharsets.UTF_8);
	}

	/**
	 * Read the file content from disk in ISO format.
	 *
	 * @param filePath   target file
	 * @param checkInUse check if target file is currently used by another application
	 * @return a list of Strings (lines) as raw text or null if the file doesn't exist (or is in use)
	 */
	public List<String> readIsoTextToList(String filePath, boolean checkInUse) {
		return readTextToList(filePath, checkInUse, StandardCharsets.ISO_8859_1);
	}

	private List<String> readTextToList(String filePath, boolean checkInUse, Charset charset) {
		List<String> fileContent = null;
		if (!checkInUse || fileNotInUse(filePath)) {
			fileContent = new ArrayList<>();
			try (FileInputStream inputStream = new FileInputStream(filePath);
				 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
				 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
				String line = "";
				while (line != null) {
					line = bufferedReader.readLine();
					if (line != null) {
						fileContent.add(line);
					}
				}
			} catch (IOException exception) {
				System.out.println("File read error: " + filePath);
				fileContent = null;
			}
		}
		return fileContent;
	}

	/**
	 * Read the file content from disk in UTF-8 format.
	 *
	 * @param filePath target file
	 * @return a StringBuilder containing the raw text or null if the file doesn't exist
	 */
	public StringBuilder readUtf8TextToBuilder(String filePath) {
		StringBuilder fileContent = new StringBuilder();
		try (FileInputStream inputStream = new FileInputStream(filePath);
			 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			String line = "";
			while (line != null) {
				line = bufferedReader.readLine();
				if (line != null) {
					fileContent.append(line);
				}
			}
		} catch (IOException exception) {
			System.out.println("File read error: " + filePath);
			fileContent = null;
		}
		return fileContent;
	}

	/**
	 * Write raw text content to disk in UTF-8 format.
	 *
	 * @param filePath    target file
	 * @param textContent raw text
	 * @param append      append content to the end of an existing file (do not overwrite whole file)
	 */
	public void writeUtf8Text(String filePath, List<String> textContent, boolean append) {
		try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8, append);
			 BufferedWriter writer = new BufferedWriter(fileWriter)) {
			for (String line : textContent) {
				writer.append(line);
				writer.newLine();
			}
		} catch (IOException exception) {
			System.out.println("File write error: " + filePath);
		}
	}

	/**
	 * Create a directory on disk at the given path.
	 *
	 * @param path of the new directory
	 */
	public void createDirectory(String path) {
		try {
			Files.createDirectory(Paths.get(path));
		} catch (IOException exception) {
			System.out.println("Create directory error: " + path);
		}
	}

	/**
	 * Return the parent directory path of the given directory.
	 *
	 * @param path directory for reference
	 * @return parent directory path
	 */
	public String getParentDirectory(String path) {
		if (directoryExists(path)) {
			return Paths.get(path).getParent().toString();
		}
		return "";
	}

	/**
	 * Check if the file exists and return the results.
	 *
	 * @param path file to check
	 * @return true if file exists false otherwise
	 */
	public boolean fileExists(String path) {
		if (path != null) {
			File file = new File(path);
			return file.exists() && file.isFile();
		}
		return false;
	}

	/**
	 * Check if the directory exists and return the results.
	 *
	 * @param path directory to check
	 * @return true if file exists false otherwise
	 */
	public boolean directoryExists(String path) {
		if (path != null) {
			File directory = new File(path);
			return directory.exists() && directory.isDirectory();
		}
		return false;
	}

	/**
	 * Check if a file is used by another application and return the results.
	 *
	 * @param path file to check
	 * @return return false if file exists and is used by another application, true otherwise
	 */
	public boolean fileNotInUse(String path) {
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				File sameFile = new File(path);
				return file.renameTo(sameFile);
			}
		}
		return true;
	}

	/**
	 * Delete a file from disk.
	 *
	 * @param filePath target file
	 * @return false if file exists and cannot be deleted, true otherwise
	 */
	public boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			if (!file.delete()) {
				System.out.println("Delete file error: " + filePath);
				return false;
			}
		} else {
			System.out.println("File to delete not found: " + filePath);
		}
		return true;
	}

	/**
	 * Delete a whole directory including the files and subdirectories within.
	 *
	 * @param path of the directory to delete
	 * @return list of filenames that were not possible to delete (directories are not included)
	 */
	public List<String> deleteWholeDirectory(String path) {
		failedFilenames.clear();
		deleteDirectory(path);
		return failedFilenames;
	}

	private void deleteDirectory(String path) {
		List<String> directoryContent = listFilesAndDirectories(path);
		for (String filename : directoryContent) {
			String filepath = path + "\\" + filename;
			File file = new File(filepath);
			if (file.isDirectory()) {
				deleteDirectory(filepath);
			} else if (!deleteFile(filepath)) {
				failedFilenames.add(filename);
			}
		}
		deleteFile(path);
	}

	/**
	 * List detected files and directories on the given path.
	 *
	 * @param path to check for files and directories
	 * @return a list containing detected file and directory names
	 */
	public List<String> listFilesAndDirectories(String path) {
		List<String> filesList = new ArrayList<>();
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				filesList.add(file.getName());
			}
		}
		return filesList;
	}

	/**
	 * List detected files on the given path (directories will be omitted).
	 *
	 * @param path to check for files
	 * @return a list containing detected filenames
	 */
	public List<String> listFiles(String path) {
		List<String> filesList = new ArrayList<>();
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					filesList.add(file.getName());
				}
			}
		}
		return filesList;
	}

	/**
	 * List detected directories on the given path (files will be omitted).
	 *
	 * @param path to check for directories
	 * @return a list containing detected directory names.
	 */
	public List<String> listDirectories(String path) {
		List<String> filesList = new ArrayList<>();
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					String name = file.getName();
					filesList.add(name);
				}
			}
		}
		return filesList;
	}
}
