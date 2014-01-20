package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.Pair;
import gov.ca.water.calgui.utils.ProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

;
/**
 * Tracks and displays scenario status during save and run
 * 
 * @author tslawecki
 * 
 */
public class ScenarioMonitor {

	private static Properties properties = new Properties();
	private static Logger log = Logger.getLogger(ScenarioMonitor.class.getName());
	private static String runRecordFolderName;
	private static ProgressDialog progressDialog;
	private static int wsdiIterations;

	private static boolean saving = false;
	private static final HashMap<String, Pair<String, Date>> scenarioList = new HashMap<String, Pair<String, Date>>();
	private static final SwingWorker<Void, String> workerScenarioMonitor = new SwingWorker<Void, String>() {

		@Override
		protected Void doInBackground() throws InterruptedException {

			boolean loop = true;
			while (loop) {
				publish("");
				Thread.sleep(1000);
			}
			return null;
		}

		@Override
		protected void process(List<String> stuff) {
			String[] listData;
			saving = false;
			if (scenarioList.size() == 0) {
				listData = new String[1];
				listData[0] = "No active scenarios";
			} else {
				listData = new String[scenarioList.size()];
				Iterator<Entry<String, Pair<String, Date>>> it = scenarioList.entrySet().iterator();
				int j = 0;
				Date now = new Date();
				while (it.hasNext()) {

					Map.Entry<String, Pair<String, Date>> entry = it.next();
					String status = getStatus(entry.getKey());

					// Check for change in status
					if (!status.equals(entry.getValue().status)) {
						entry.setValue(new Pair<String, Date>(status, now));
						if (status.contains("DONE - run completed")) {
							MainMenu.fdDSSFiles.addFileToList(new File(System.getProperty("user.dir") + "\\Scenarios\\"
							        + entry.getKey() + "_DV.DSS"));
						}
					}

					// Check for timeout
					if (now.getTime() - entry.getValue().timestamp.getTime() < 1000 * 60) {
						listData[j] = entry.getKey() + " - " + status;
						j++;
						saving = saving || status.contains("SAVING -");
					} else {
						scenarioList.remove(entry.getKey());
					}
				}
			}
			progressDialog.setList(listData);
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
			wsdiIterations = Integer.parseInt(properties.getProperty("wsidi.iterations"));
		} catch (Exception e) {
			log.debug("Problem loading properties. " + e.getMessage());
		}
		progressDialog = new ProgressDialog("Status Monitor");
		workerScenarioMonitor.execute();
	}

	/**
	 * Adds a scenario to be monitored
	 * 
	 * @param scenarioName
	 */
	public static void add(String scenarioName) {
		// TODO: Check for duplicate and handle appropriately
		if (!scenarioList.containsKey(scenarioName)) {
			Date now = new Date();
			Pair<String, Date> pair = new Pair<String, Date>("Unknown", now);
			scenarioList.put(scenarioName, pair);
		}

		if (!progressDialog.isVisible())
			progressDialog.setVisible(true);
	}

	public boolean isSaving() {
		return saving;
	}

	private static String pdfStatus;

	public static void setPDFStatus(String status) {
		pdfStatus = status;
	}

	/**
	 * Returns status of scenario - Saved, Parsing, Running, Done
	 * 
	 * @param scenarioName
	 * @return
	 */
	public static String getStatus(String scenarioName) {

		if (scenarioName.equals("Creating PDF"))
			return pdfStatus;

		String text;

		String scenDir_absPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName + "\\"
		        + scenarioName).getAbsolutePath();

		File scenDir = new File(scenDir_absPath);
		if (!scenDir.isDirectory())
			return "WHILE SAVING - No directory"; // Scenario directory doesn't exist

		File scenSavingFile = new File(scenDir_absPath + "\\save.txt");
		File scenWRESLCHECKFile = new File(scenDir_absPath + "\\RUN\\=WreslCheck_main=.log");
		File scenWRESLCHECK_WSIDIFile = new File(scenDir_absPath + "\\RUN\\=WreslCheck_main_wsidi=.log");
		File scenPROGRESSFile = new File(scenDir_absPath + "\\Run\\PROGRESS.txt");

		if (!scenSavingFile.exists())
			return "WHILE SAVING - No save.txt file"; // Scenario save in progress

		text = lastLine(scenSavingFile);
		if (text.contains("unopenable!"))
			return "SAVING - unable to read save.txt";

		if (!text.contains("Save complete"))
			return "SAVING - " + text;

		if (!scenWRESLCHECKFile.exists() && !scenWRESLCHECK_WSIDIFile.exists())
			return "SAVED";

		String infoWSIDI = "";
		if (scenWRESLCHECK_WSIDIFile.exists()) {
			File scenWSIDIIterationFile = new File(scenDir_absPath + "\\RUN\\wsidi_iteration.log");
			if (!scenWSIDIIterationFile.exists())
				infoWSIDI = "(WSIDI) ";
			else
				infoWSIDI = "(WSIDI " + lastLine(scenWSIDIIterationFile) + ") ";
		}

		if (!scenPROGRESSFile.exists()) {

			if (scenWRESLCHECK_WSIDIFile.exists())
				text = lastLine(scenWRESLCHECK_WSIDIFile);
			else
				text = lastLine(scenWRESLCHECKFile);
			if (text.contains("unopenable!"))
				return (infoWSIDI + "PARSING - unable to read parsing log");
			if (text.contains("Empty!"))
				return (infoWSIDI + "PARSING - parsing started");
			if (!text.contains("Total errors:"))
				return (infoWSIDI + "PARSING - " + text);
			else
				return (infoWSIDI + "PARSING - Parsing complete - " + text);

		} else {

			text = lastLine(scenPROGRESSFile);
			if (text.contains("unopenable!"))
				return (infoWSIDI + "RUNNING - unable to read progress.txt");
			if (text.contains("Empty!"))
				return (infoWSIDI + "RUNNING - run starting");
			if (text.contains("Run completed.")) {
				File scenWSIDIIterationFile = new File(scenDir_absPath + "\\RUN\\wsidi_iteration.log");
				if (scenWSIDIIterationFile.exists()) {
					String strIter = lastLine(scenWSIDIIterationFile);
					if (strIter.endsWith(Integer.toString(wsdiIterations) + "/" + Integer.toString(wsdiIterations))) {
						RunUtils.loadGeneratedWSIDI(scenDir_absPath);
					}
				}
				return (infoWSIDI + "DONE - run completed");
			}
			if (text.contains("Run failed."))
				return (infoWSIDI + "DONE - run failed.");
			else {
				String parts[] = text.split(" ");
				if (parts.length == 4) {
					try {
						int totalMonths = 12 * (Integer.parseInt(parts[1]) - Integer.parseInt(parts[0]));
						int months = Math.min(totalMonths,
						        Integer.parseInt(parts[3]) + 12 * (Integer.parseInt(parts[2]) - Integer.parseInt(parts[0])));
						text = parts[3] + "/" + parts[2] + " (" + (100 * months / totalMonths) + "%)";
					} catch (NumberFormatException e) {
						// System.out.println("Error");
					}
				}
				return (infoWSIDI + "RUNNING - " + text);
			}
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
		String text2 = "";
		try {
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(file.getAbsolutePath()));
			while (scanner.hasNextLine()) {
				text2 = text;
				text = scanner.nextLine();
			}
			scanner.close();
			if (text2.equals("Empty!"))
				return text;
			else

				return text2 + text;

		} catch (IOException e) {
			log.info(file.getName() + " not openable");
			e.printStackTrace();
			return file.getName() + " unopenable!";
		}
	}

}
