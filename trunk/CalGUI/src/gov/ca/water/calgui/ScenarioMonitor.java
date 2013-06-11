package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.ProgressFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
				publish(" ");
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
			pFrame.setVisible(true);
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
	}

	public static String getStatus(String scenarioName) {

		String scenDir_absPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName + "\\"
		        + scenarioName).getAbsolutePath();

		File scenDir = new File(scenDir_absPath);
		if (!scenDir.isDirectory())
			return "No directory"; // Scenario directory doesn't exist

		File scenSavingFile = new File(scenDir_absPath + "\\saving.txt");
		if (scenSavingFile.exists())
			return "Saving"; // Scenario save in progress

		File scenSavedFile = new File(scenDir_absPath + "\\saved.txt");
		File scenWRESLCHECKFile = new File(scenDir_absPath + "\\RUN\\=WreslCheck_main=.log");

		if (scenSavedFile.exists() && !scenWRESLCHECKFile.exists())
			return "Saved";

		List<String> text = new ArrayList<String>();

		File scenPROGRESSFile = new File(scenDir_absPath + "\\Run\\PROGRESS.txt");
		if (!scenPROGRESSFile.exists()) {
			try {
				Scanner scanner;
				scanner = new Scanner(new FileInputStream(scenWRESLCHECKFile.getAbsolutePath()));
				while (scanner.hasNextLine()) {
					text.add(scanner.nextLine());
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				log.info("WRESLCheck file not openable for " + scenarioName);
				e.printStackTrace();
			}
			if (text.size() == 0) {
				return ("Parsing");
			} else {
				boolean stillParsing = false;
				for (int i = 0; i <= text.size(); i++) {
					if (text.get(i).contains("Total errors: 0"))
						stillParsing = true;
					else if (text.get(i).contains("Total errors:"))
						return "Done parsing - " + text.get(i);
				}
				if (stillParsing)
					return "Parsing";
			}

		}

		text.clear();
		try {
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(scenPROGRESSFile.getAbsolutePath()));
			while (scanner.hasNextLine()) {
				text.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			log.info("Progress file not openable for " + scenarioName);
		}

		if (text.size() == 0)
			return "Running?";
		else if (text.get(text.size() - 1).contains("Run completed."))
			return "Run completed";
		else
			return "Running - " + text.get(text.size() - 1);
	}
}
