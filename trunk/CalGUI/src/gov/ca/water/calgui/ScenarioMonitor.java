package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.ProgressFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

/**
 * Tracks and displays scenario status during save and run
 * 
 * @author tslawecki
 * 
 */
public class ScenarioMonitor {

	private static Properties properties = new Properties();
	private static Logger log = Logger.getLogger(FileAction.class.getName());
	private static String runRecordFolderName;
	private static ProgressFrame pFrame;

	private static final HashMap<String, String> scenarioList = new HashMap<String, String>();
	private static final SwingWorker<Void, String> workerScenarioMonitor = new SwingWorker<Void, String>() {

		@Override
		protected Void doInBackground() throws InterruptedException {

			boolean loop = true;
			while (loop) {
				Thread.sleep(1000);
				publish("");
			}
			return null;
		}

		@Override
		protected void process(List<String> stuff) {
			String[] listData = new String[scenarioList.size()];
			Iterator<Entry<String, String>> it = scenarioList.entrySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				entry.setValue(getStatus(entry.getKey()));
				listData[i] = entry.getKey() + " - " + entry.getValue();
				i++;
			}
			pFrame.setList(listData);

			return;
		}

		@Override
		protected void done() {
			return;

		}

	};

	/**
	 * Starts monitoring thread
	 */
	public static void start() {
		try {
			properties.load(FileAction.class.getClassLoader().getResourceAsStream("callite-gui.properties"));
			runRecordFolderName = properties.getProperty("runrecord.dir", "Run_Records");
		} catch (Exception e) {
			log.debug("Problem loading properties. " + e.getMessage());
		}
		pFrame = new ProgressFrame("Status Monitor");
		workerScenarioMonitor.execute();
	}

	/**
	 * Adds a scenario to be monitored
	 * 
	 * @param scenarioName
	 */
	public static void add(String scenarioName) {
		// TODO: Check for duplicate and handle appropriately
		// TODO: Logging
		scenarioList.put(scenarioName, "Unknown");
		if (!pFrame.isVisible())
			pFrame.setVisible(true);

	}

	/**
	 * Returns status of scenario - Saved, Parsing, Running, Done
	 * 
	 * @param scenarioName
	 * @return
	 */
	public static String getStatus(String scenarioName) {

		String text;

		String scenDir_absPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName + "\\"
		        + scenarioName).getAbsolutePath();

		File scenDir = new File(scenDir_absPath);
		if (!scenDir.isDirectory())
			return "ERROR - No directory"; // Scenario directory doesn't exist

		File scenSavingFile = new File(scenDir_absPath + "\\save.txt");
		File scenWRESLCHECKFile = new File(scenDir_absPath + "\\RUN\\=WreslCheck_main=.log");
		File scenPROGRESSFile = new File(scenDir_absPath + "\\Run\\PROGRESS.txt");

		if (!scenSavingFile.exists())
			return "ERROR - No save.txt file"; // Scenario save in progress

		text = lastLine(scenSavingFile);
		if (text.contains("unopenable!"))
			return "WORKING - unable to read save.txt";

		if (!text.contains("Save complete"))
			return "WORKING - " + text;

		if (!scenWRESLCHECKFile.exists())
			return "SAVED";

		if (!scenPROGRESSFile.exists()) {

			text = lastLine(scenWRESLCHECKFile);
			if (text.contains("unopenable!"))
				return "WORKING - unable to read parsing log";
			if (text.contains("Empty!"))
				return ("WORKING - parsing started");
			if (!text.contains("Total errors:"))
				return "WORKING - Parsing - " + text;
			else
				return "WORKING - Parsing complete - " + text;

		} else {

			text = lastLine(scenPROGRESSFile);
			if (text.contains("unopenable!"))
				return "WORKING - unable to read progress.txt";
			if (text.contains("Empty!"))
				return "WORKING - run starting";
			if (!text.contains("Run completed."))
				return "WORKING - " + text;
			else
				return "DONE";
		}
	}

	/**
	 * Reads a text file and returns last line
	 * 
	 * @param file
	 * @return - String containing last line of file or the messages "Empty!" if no lines or "unopenable!".
	 */

	private static String lastLine(File file) {
		String text = "Empty!";
		try {
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(file.getAbsolutePath()));
			while (scanner.hasNextLine()) {
				text = scanner.nextLine();
			}
			scanner.close();
			return text;

		} catch (IOException e) {
			log.info(file.getName() + " not openable");
			e.printStackTrace();
			return file.getName() + " unopenable!";
		}
	}
}
