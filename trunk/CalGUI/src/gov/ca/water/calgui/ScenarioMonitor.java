package gov.ca.water.calgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private String runRecordFolderName;

	private final HashMap<String, String> scenarioList = new HashMap<String, String>();
	private final SwingWorker<Void, String> workerScenarioMonitor = new SwingWorker<Void, String>() {

		@Override
		protected Void doInBackground() throws InterruptedException {

			boolean loop = true;
			while (loop) {
				Thread.sleep(100);
				publish(" ");
			}
			return null;
		}

		@Override
		protected void process(List<String> stuff) {
			// TODO: replace console output with Swing GUI
			System.out.println("ScenarioMonitor: " + scenarioList.size() + " scenarios.");
			for (int i = 0; i <= scenarioList.size(); i++)
				System.out.println(scenarioList.get(i));

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
	public void start() {
		try {
			properties.load(FileAction.class.getClassLoader().getResourceAsStream("callite-gui.properties"));
			runRecordFolderName = properties.getProperty("runrecord.dir", "Run_Records");
		} catch (Exception e) {
			log.debug("Problem loading properties. " + e.getMessage());
		}
		workerScenarioMonitor.execute();
	}

	/**
	 * Adds a scenario to be monitored
	 * 
	 * @param scenarioName
	 */
	public void add(String scenarioName) {
		// TODO: Check for duplicate and handle appropriately
		// TODO: Logging
		scenarioList.put(scenarioName, "Unknown");
	}

	private String getStatus(String scenarioName) {

		String scenDir_absPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName + "\\"
		        + scenarioName).getAbsolutePath();

		File scenDir = new File(scenDir_absPath);
		if (!scenDir.isDirectory())
			return "No directory"; // Scenario directory doesn't exist

		File scenSavingFile = new File(scenDir_absPath + "\\saving.txt");
		if (scenSavingFile.exists())
			return "Saving"; // Scenario save in progress

		File scenSavedFile = new File(scenDir_absPath + "\\saved.txt");
		File scenWRESLCHECKFile = new File(scenDir_absPath + "\\RUN\\=WreslCheck_main=.txt");

		if (scenSavedFile.exists() && !scenWRESLCHECKFile.exists())
			return "Saved";

		List<String> text = new ArrayList<String>();
		try {
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(scenWRESLCHECKFile.getAbsolutePath()));
			while (scanner.hasNextLine()) {
				text.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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

		File scenPROGRESSFile = new File(scenDir_absPath + "\\Run\\PROGRESS.txt");

		if (!scenPROGRESSFile.exists())
			return "Parsed, not running";

		text.clear();
		try {
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(scenPROGRESSFile.getAbsolutePath()));
			while (scanner.hasNextLine()) {
				text.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (text.size() == 0)
			return "Running?";
		else
			return "Running - " + text.get(text.size() - 1);
	}
}
