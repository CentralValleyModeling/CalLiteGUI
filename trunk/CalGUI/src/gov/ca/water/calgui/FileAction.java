package gov.ca.water.calgui;

import gov.ca.water.calgui.dashboards.ScenarioFrame;
import gov.ca.water.calgui.dashboards.ScenarioTable;
import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.FileUtils;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;
import gov.ca.water.calgui.utils.NumericTextField;
import gov.ca.water.calgui.utils.ProgressFrame;
import gov.ca.water.calgui.utils.SimpleFileFilter;
import gov.ca.water.calgui.utils.UnitsUtils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

import wrimsv2.evaluator.TimeOperation;

/**
 * The FileAction class is the action listener for menubar "menu" and panel "runsettings" in the GUI.
 * 
 * @author drucinski, tslawecki
 * 
 */
public class FileAction implements ActionListener {

	private final JFrame desktop;
	private final SwingEngine swix;
	private Boolean[] regUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private int action_WSIDI;
	private static String runRecordFolderName; // Name for subfolder under scenarios directory to contain generated files for each
	                                           // run

	static Logger log = Logger.getLogger(FileAction.class.getName());
	private static SwingWorker<Void, String> worker_setupScenario = null;

	/**
	 * Constructor for FileAction
	 * 
	 * @param desktop
	 * @param swix
	 * @param regUserEdits
	 * @param dTableModels
	 * @param gl
	 * @param action_WSIDI
	 */
	public FileAction(JFrame desktop, SwingEngine swix, Boolean[] regUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        int action_WSIDI) {
		this.desktop = desktop;
		this.swix = swix;
		this.regUserEdits = regUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.action_WSIDI = action_WSIDI;

		// create and load default properties
		runRecordFolderName = "Run_Records";
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("CalLiteGUI.properties");
			defaultProps.load(in);
			in.close();
			runRecordFolderName = defaultProps.getProperty("RunRecordFolderName", "Run_Records");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		JPanel mainmenu = (JPanel) swix.find("mainmenu");
		String scen = ((JTextField) swix.find("run_txfScen")).getText();

		if ("AC_RUN".equals(ae.getActionCommand())) {

			// Check if selections are valid

			String startMon = ((String) ((JSpinner) swix.find("spnRunStartMonth")).getValue()).trim();
			String endMon = ((String) ((JSpinner) swix.find("spnRunEndMonth")).getValue()).trim();
			Integer startYr = (Integer) ((JSpinner) swix.find("spnRunStartYear")).getValue();
			Integer endYr = (Integer) ((JSpinner) swix.find("spnRunEndYear")).getValue();

			// Determine Month/Count
			Integer iSMon = UnitsUtils.monthToInt(startMon);
			Integer iEMon = UnitsUtils.monthToInt(endMon);
			Integer numMon = (endYr - startYr) * 12 + (iEMon - iSMon) + 1;

			if (!scen.equals("")) {

				// Make sure current run isnt in background.
				if ((new File(System.getProperty("user.dir") + "\\Run\\running.txt")).exists()) {
					JOptionPane.showMessageDialog(mainmenu, "There is currently a simulation running at this time.");

				} else if (numMon < 1) {
					JOptionPane.showMessageDialog(mainmenu, "The specified start date must be before the end date.");
				} else {

					// Disable run button
					JButton btn = (JButton) swix.find("run_btnRun");
					btn.setEnabled(false);
					mainmenu.revalidate();

					// *** Determine if scenario has changed.

					// Get current scenario settings
					StringBuffer sb = buildScenarioString(swix, regUserEdits, dTableModels, gl);

					// Read existing file

					File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
					StringBuffer sbExisting = FileUtils.readScenarioFile(f);

					Boolean okToRun = sb.toString().equals(sbExisting.toString());
					if (!okToRun) {

						// Scenario settings have changed - check if they should be saved before running

						int n = JOptionPane.showConfirmDialog(mainmenu,
						        "Scenario selections have changed. Would you like to save the changes?", "CalLite GUI",
						        JOptionPane.YES_NO_CANCEL_OPTION);

						switch (n) {

						case JOptionPane.CANCEL_OPTION:

							// CANCEL - do not save changes to disk, do not run, return

							okToRun = false;
							break;

						case JOptionPane.NO_OPTION:

							// NO - do not save changes to disk, check if OK to revert and run

							if (JOptionPane.showConfirmDialog(mainmenu,
							        "Press OK to run with the last saved version of " + f.getPath()
							                + "; your changes will be lost.", "CalLite GUI", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
								okToRun = true;

								action_WSIDI = 0;
								regUserEdits = GUIUtils.setControlValues(f, swix, dTableModels, gl);
								regUserEdits = GUIUtils.setControlValues(f, swix, dTableModels, gl);
								action_WSIDI = 1;

							} else {
								okToRun = false;
							}
							break;

						case JOptionPane.YES_OPTION:

							// YES - get file name

							GetDSSFilename getScenFilename;
							getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");
							getScenFilename.actionPerformed(ae);
							if (getScenFilename.dialogRC != 0) {
								// Cancel?
								okToRun = false;
							} else {

								okToRun = true;
								String scen2 = ((JTextField) swix.find("run_txfScen")).getText();
								if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen2)).exists()) {
									if (JOptionPane.showConfirmDialog(mainmenu,
									        "The scenario file '" + System.getProperty("user.dir") + "\\Scenarios\\" + scen
									                + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
									        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
										// Existing file, do overwrite
										okToRun = true;

									} else {
										// Existing file, do not overwrite -> cancel run
										okToRun = false;
									}
								}
								if (okToRun) {
									((JTextField) swix.find("run_txfScen")).setText(scen2);
									setFilenameTooltips();
									sb = buildScenarioString(swix, regUserEdits, dTableModels, gl);
									saveScenarioFile(sb, System.getProperty("user.dir") + "\\Scenarios\\" + scen2);
									scen = scen2;
								} else {
									((JTextField) swix.find("run_txfScen")).setText(scen);
									setFilenameTooltips();
								}
							}
							break;
						}
					}
					if (okToRun) {
						// setupAndRun(scen, desktop, swix, regUserEdits, dTableModels, gl);
						setupScenario(scen, desktop, swix, regUserEdits, dTableModels, gl);
						setupBatchFile(scen, false);
						runBatch();
					}
					btn.setEnabled(true);
					mainmenu.revalidate();
				}

			} else {
				JFrame frame = new JFrame("Error");

				// show a joptionpane dialog using showMessageDialog
				JOptionPane.showMessageDialog(frame, "You must specify a scenario name.");

			}
		} else if (ae.getActionCommand().startsWith("AC_SaveScen")) {

			boolean proceed = true;

			if (ae.getActionCommand().equals("AC_SaveScenAs")) {

				// Save scenario as ...
				GetDSSFilename getScenFilename;
				getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");
				getScenFilename.actionPerformed(ae);
				if (getScenFilename.dialogRC != 0)
					proceed = false;
				else {
					setFilenameTooltips();
				}
			}
			if (proceed) {
				scen = ((JTextField) swix.find("run_txfScen")).getText();
				if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen)).exists())
					proceed = (JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + System.getProperty("user.dir")
					        + "\\Scenarios\\" + scen + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
					        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

				if (proceed) {
					saveFile(scen, swix, regUserEdits, dTableModels, gl);
					setupScenario(scen, desktop, swix, regUserEdits, dTableModels, gl);
				} else {
					JFrame frame = new JFrame("Error");
					JOptionPane.showMessageDialog(frame, "You must specify a scenario name.");
				}
			}

		} else if (ae.getActionCommand().startsWith("AC_LoadScen")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("CalLite Scenario Files *.cls", "cls"));
			fc.setCurrentDirectory(new File(".//Scenarios"));
			int retval = fc.showOpenDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {

				// ... The user selected a file, get it, use it.

				File file = fc.getSelectedFile();
				GetDSSFilename getScenFilename;
				getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");
				getScenFilename.fc.setSelectedFile(file); // Use this name for next Save As

				action_WSIDI = 0;
				regUserEdits = GUIUtils.setControlValues(file, swix, dTableModels, gl);
				regUserEdits = GUIUtils.setControlValues(file, swix, dTableModels, gl);
				action_WSIDI = 1;
				setFilenameTooltips();
			}

		} else if (ae.getActionCommand().startsWith("AC_CompScen")) {

			// Save current settings to temp file
			saveFile("Current_Scenario", swix, regUserEdits, dTableModels, gl);
			ScenarioFrame ScenFrame = new ScenarioFrame("CalLite 2.0 GUI - Scenario Comparison", swix);
			ScenFrame.setVisible(true);
			ScenFrame.setVisible(true);

		} else if (ae.getActionCommand().startsWith("AC_ViewScen")) {

			// Save current settings to temp file
			saveFile("Current_Scenario", swix, regUserEdits, dTableModels, gl);
			// Build list
			Object scenlist[] = { "Current_Scenario" };
			// Show frame
			ScenarioTable sTableFrame = new ScenarioTable(scenlist, swix);
			// java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
			// sTableFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
			sTableFrame.setVisible(true);
			// Delete temp file
			File ft = new File(System.getProperty("user.dir") + "\\Scenarios\\Current_Scenario");
			FileUtils.deleteDir(ft);
		}

		else if (ae.getActionCommand().equals("AC_Help")) {

			JHelp helpViewer;
			JFrame help;
			try {
				ClassLoader classLoader = FileAction.class.getClassLoader();
				URL url = HelpSet.findHelpSet(classLoader, "../docs/helpset.hs");
				helpViewer = new JHelp(new HelpSet(classLoader, url));

				help = new JFrame("CalLite 2.0 GUI Help");
				help.getContentPane().add(helpViewer);

				JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
				int selIndex = jtp.getSelectedIndex();
				String label = jtp.getTitleAt(selIndex);
				helpViewer.setCurrentID(label);
				help.pack();
				help.setVisible(true);

			} catch (Exception e) {

				log.debug("helpset not found: " + e.getMessage());

			}

		}
	}

	/**
	 * Sets value of DV DSS file name and tooltips for scenario and DSS textfields
	 */
	private void setFilenameTooltips() {

		String scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
		((JTextField) swix.find("run_txfScen")).setToolTipText(System.getProperty("user.dir") + "\\Scenarios\\" + scenFilename);

		String dvDSSFilename = scenFilename.substring(0, scenFilename.length() - 4) + "_DV.DSS";
		((JTextField) swix.find("run_txfoDSS")).setText(dvDSSFilename);
		((JTextField) swix.find("run_txfoDSS")).setToolTipText(System.getProperty("user.dir") + "\\Scenarios\\" + dvDSSFilename);

		desktop.setTitle(desktop.getTitle().split(";")[0] + ";  Scenario - " + scenFilename);

	}

	/**
	 * Creates a scenario directory, then copies contents of Default and Default\Lookup to new directory
	 * 
	 * @param runDirName
	 * @return true if successful, false if not
	 */
	public static boolean setupScenarioDirectory(String runDir) {

		boolean success = true;

		File ft = new File(runDir);
		// First delete existing Run directory.
		FileUtils.deleteDir(ft);
		ft.mkdirs();

		// Copy wrims2 wresl directory to Run directory
		File wreslDir = new File(System.getProperty("user.dir") + "\\Model_w2\\wresl");

		try {
			FileUtils.copyDirectory(wreslDir, ft, true);
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}

		// Copy Default dir to Run dir. This may overwrite wrims2 wresl's copy
		File fs = new File(GUIUtils.defaultDirectoryString());
		try {
			FileUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			success = false;
		}

		// Copy lookup files.
		fs = new File(GUIUtils.defaultLookupDirectoryString());
		ft = new File(runDir, "Lookup");
		try {
			FileUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			success = false;
		}
		return success;
	}

	/**
	 * Copies indicated DSS file from default DSS directory to run directory
	 * 
	 * @param runDirName
	 * 
	 * @param dssFileName
	 * @return true if successful, false if not
	 */
	private static boolean copyDSSFileToScenarioDirectory(String runDir, String dssFileName) {

		boolean success = true;
		File ft = new File(runDir, "\\DSS");
		ft.mkdirs();

		File fs = new File(GUIUtils.defaultDSSDirectoryString() + "\\" + dssFileName);

		ft = new File(runDir, "\\DSS\\" + dssFileName);

		try {
			FileUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			success = false;
		}
		return success;
	}

	/**
	 * Writes .table files to Run directory with values set in GUI
	 * 
	 * @param links
	 *            Array containing strings read from GUI_Links2.table - one string for each line
	 * @param UDFlags
	 *            Array of boolean values that indicate whether user-defined values have been entered for certain data tables
	 * @param swix
	 *            Pointer to UI for retrieval of GUI selections made by user
	 * @throws IOException
	 */
	public static void writeScenarioTables(final String runDir, ArrayList<String> links, Boolean[] UDFlags, SwingEngine swix)
	        throws IOException {

		String openFileName = "";
		File f = null;
		BufferedWriter tableFile_BufferedWriter_ = null;
		String line = "", outstring = "";
		String swixControlName = "", tableFileName = "", descr = "", value = "", option = "", cID = "";
		Boolean val;
		int tID;
		int index = 0;

		final String NL = System.getProperty("line.separator");

		// For each line in GUI_Links2.table ...

		for (int i = 0; i < links.size(); i++) {

			line = links.get(i).toString();
			String[] linkParts = line.split("[\t]+");

			// ... Get name of associated control in GUI, and name of CalLite table file where option is set
			// Links are assumed to be ordered by table file name, and then by index (line in table file)

			swixControlName = linkParts[0].trim();
			tableFileName = linkParts[1].trim();

			// If there is a table file for the current link ...

			if (!tableFileName.equals("n/a")) {

				// ... Get the index (line in table file where the link result is written),
				// option (0..n for choice indicated through radiobuttons, -1 for values entered as text, -2 for bollean from
				// checkbox)
				// and descriptive text for link.

				index = Integer.parseInt(linkParts[2].trim());
				option = linkParts[3].trim();
				descr = "!" + linkParts[4].trim();

				// If the target table file isn't currently open ...

				if (!tableFileName.equals(openFileName)) {

					// Close the open file, if any

					if (tableFile_BufferedWriter_ != null) {
						tableFile_BufferedWriter_.close();
					}

					// TODO: Handle multiple scenarios running simultaneously - probably by passing scenario directory

					// Open existing table file and read in all header comments (lines that start with a "!")

					f = new File(GUIUtils.defaultLookupDirectoryString(), tableFileName);
					FileInputStream fin = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fin));
					StringBuffer header = new StringBuffer();
					String aLine = br.readLine();
					while (aLine.startsWith("!") && aLine != null) {
						header.append(aLine + NL);
						aLine = br.readLine();
					}

					// Close table file in "Default" folder

					br.close();
					// FileUtils.deleteDir(f);

					// Create a new file with the same name and write the header comments

					File ft = new File(runDir, tableFileName);
					FileWriter fstream = new FileWriter(ft);
					tableFile_BufferedWriter_ = new BufferedWriter(fstream);
					if (header != null) {
						tableFile_BufferedWriter_.write(header.toString());
						openFileName = tableFileName;
					}

					// Write column headers for table files

					outstring = openFileName.substring(0, openFileName.length() - 6) + NL;
					tableFile_BufferedWriter_.write(outstring);
					outstring = "Index" + "\t" + "Option" + NL;
					tableFile_BufferedWriter_.write(outstring);

					// There is now a file ready to have options written to.
				}

				// Retrieve user entry from GUI

				Component c = swix.find(swixControlName);

				if (c instanceof JTextField || c instanceof NumericTextField || c instanceof JTextArea) {

					// Linked component is a text field or variant: set "option" (in second column) to text

					value = ((JTextComponent) c).getText();
					option = value;
					outstring = (index + "\t" + option + "\t" + descr + NL);
					tableFile_BufferedWriter_.write(outstring);

				} else if (c instanceof JCheckBox) {

					// Linked component is a checkbox

					val = ((AbstractButton) c).isSelected(); // TODO: Check if we can just use "val" instead of converting to
					                                         // string?
					value = val.toString();
					if (!value.startsWith("true")) {

						// If it's not selected, set option to "0" - false
						// Check for N/A Flag
						String NAFlag = linkParts[11].trim();
						if (NAFlag == "1") {
							option = "NA";
						} else {
							option = "0";
						}

					} else {

						// If it is selected, set option to "1" - true pr check d1485/D1641 options
						if (swixControlName.startsWith("ckbReg")) {
							JRadioButton c1 = ((JRadioButton) swix.find("rdbRegQS_D1485"));
							if (c1.isSelected()) {
								// Check for N/A Flag
								String NAFlag = linkParts[13].trim();
								if (NAFlag == "1") {
									option = "NA";
								} else {
									option = "3";
								}
							} else {
								// Check for N/A Flag
								String NAFlag = linkParts[10].trim();
								if (NAFlag == "1") {
									option = "NA";
								} else {
									option = "1";
								}
							}
						} else {
							option = "1";
						}

						// ... but check if "user defined" flag is turned on - only for inputs that will be stored as separate data
						// tables. Those separate tables are written elsewhere

						// if (linkParts.length > 8) {
						if (!linkParts[8].trim().equals("n/a")) {
							cID = linkParts[8];
							tID = Integer.parseInt(cID);
							if (UDFlags != null) {
								if (UDFlags[tID] != null) {
									if (UDFlags[tID] == true) {
										// Check for N/A Flag
										String NAFlag = linkParts[12].trim();
										if (NAFlag == "1") {
											option = "NA";
										} else {
											option = "2";
										}
									}
								}
							}

						} else {
							// option = "1"; // TODO: Check if this is removable
						}
					}

					// Finally write the checkbox status to the file

					outstring = (index + "\t" + option + "\t" + descr + NL);
					tableFile_BufferedWriter_.write(outstring);

				} else if (c instanceof JRadioButton) {

					// Component is a Radiobutton. The GUI_Links2.table file is assumed to have a link entry for *each* radiobutton
					// in a radio group, and should write out only one line in the new .table file corresponding to the button
					// selected in the UI.

					val = ((AbstractButton) c).isSelected();
					value = val.toString();

					if (value.startsWith("true")) {
						outstring = (index + "\t" + option + "\t" + descr + NL);
						tableFile_BufferedWriter_.write(outstring);
					}
				} else if (c == null) { // control not found

					// TODDO: Action TBD if there is no matching control - we should raise an alert of some sort.
					option = "0";
					outstring = (index + "\t" + option + "\t" + descr + NL);
					tableFile_BufferedWriter_.write(outstring);
				}
			}
		}

		tableFile_BufferedWriter_.close();
	}

	static ProgressFrame pFrame;

	// TODO: Modify to read/set swix state from scenario file "scen", possibly saving current state of swix in temporary string
	// buffer.

	/**
	 * Sets up scenario directory and files, then executes
	 * 
	 * @param scen
	 * @param desktop
	 * @param swix
	 * @param regUserEdits
	 * @param dTableModels
	 * @param gl
	 */

	public static void runBatch() {

		// "Run" model

		try {

			pFrame.setCursor(null);
			pFrame.dispose();

			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\CalLite_w2.bat");
			int exitVal = proc.waitFor();
			System.out.println("Process exitValue: " + exitVal);
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.getMessage(), "Run failure!", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}

	}

	public static void setupBatchFile(final String scen, final boolean isAppend) {

		// Wait for Swing worker to complete

		try {
			worker_setupScenario.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// find config file path

		String scenarioName = FilenameUtils.removeExtension(scen);

		String scenarioPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName + "\\" + scenarioName)
		        .getAbsolutePath();

		String configFilePath = new File(scenarioPath, scenarioName + ".config").getAbsolutePath();

		// replace vars in batch file

		String batchText_template = wrimsv2.wreslparser.elements.Tools.readFileAsString(System.getProperty("user.dir")
		        + "\\Model_w2\\CalLite_w2.bat.template");

		String batchText = batchText_template.replace("{ConfigFilePath}", configFilePath);

		File f = new File(System.getProperty("user.dir"), "CalLite_w2.bat");

		PrintWriter batchFile;
		try {
			batchFile = new PrintWriter(new BufferedWriter(new FileWriter(f, isAppend)));
			batchFile.print(batchText);
			batchFile.flush();
			batchFile.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void setupScenario(final String scen, final JFrame desktop, final SwingEngine swix, final Boolean[] regUserEdits,
	        final DataFileTableModel[] dTableModels, final GUILinks gl) {

		pFrame = new ProgressFrame("CalLite 2.0 GUI - Setting Up Run");

		// SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
		worker_setupScenario = new SwingWorker<Void, String>() {

			@Override
			protected void done() {

				if (pFrame != null) {
					pFrame.setCursor(null);
					pFrame.dispose();
				}

				desktop.setEnabled(true);
				desktop.setVisible(true);
				return;

			}

			@Override
			protected void process(List<String> status) {

				pFrame.setText(status.get(status.size() - 1));
				return;
			}

			@Override
			protected Void doInBackground() throws Exception {

				pFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				desktop.setEnabled(false);

				boolean success = true;
				String scenWithoutExt = FilenameUtils.removeExtension(scen);

				// ========== Prepare "Generated" folder
				publish("Creating new Generated directory.");

				String scenGeneratedDir_absPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName
				        + "\\" + scenWithoutExt + "\\Generated").getAbsolutePath();

				// delete "Generated" folder to cleanup files from previous actions

				File ft = new File(scenGeneratedDir_absPath);

				if (ft.listFiles() != null)
					for (File file : ft.listFiles()) {
						FileDeleteStrategy.FORCE.delete(file);
					}

				// create DSS, Lookup, and external folders

				ft = new File(scenGeneratedDir_absPath, "DSS");
				ft.mkdirs();
				ft = new File(scenGeneratedDir_absPath, "Lookup");
				ft.mkdirs();
				ft = new File(scenGeneratedDir_absPath, "External");
				ft.mkdirs();

				// Copy DSS files to "Generated" folder

				success = success
				        & copyDSSFileToScenarioDirectory(scenGeneratedDir_absPath, ((JTextField) swix.find("hyd_DSS_SV")).getText());
				success = success
				        & copyDSSFileToScenarioDirectory(scenGeneratedDir_absPath,
				                ((JTextField) swix.find("hyd_DSS_Init")).getText());

				// ========== Copy Run directory

				publish("Creating new Run directory.");

				String scenRunDir_absPath = new File(System.getProperty("user.dir") + "\\Scenarios\\" + runRecordFolderName + "\\"
				        + scenWithoutExt + "\\Run").getAbsolutePath();

				success = success & setupScenarioDirectory(scenRunDir_absPath);

				// ========== Copy DSS files to "Run" folder

				success = success
				        & copyDSSFileToScenarioDirectory(scenRunDir_absPath, ((JTextField) swix.find("hyd_DSS_SV")).getText());
				success = success
				        & copyDSSFileToScenarioDirectory(scenRunDir_absPath, ((JTextField) swix.find("hyd_DSS_Init")).getText());

				// ==========

				File checkFile = new File(scenRunDir_absPath, "check.text");
				if (checkFile.exists())
					checkFile.delete();

				// ==========

				publish("Writing GUI tables.");
				ArrayList<String> links2Lines = new ArrayList<String>();
				links2Lines = GUIUtils.getGUILinks("Config\\GUI_Links2.table");

				try {
					// write to "Generated" folder
					writeScenarioTables(scenGeneratedDir_absPath + "\\Lookup", links2Lines, regUserEdits, swix);
					// write to "Run" folder
					writeScenarioTables(scenRunDir_absPath + "\\Lookup", links2Lines, regUserEdits, swix);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					success = false;
				}

				// ==========

				publish("Copying demand tables.");

				File fsDem;
				if (((JRadioButton) swix.find("dem_rdbCurSWP")).isSelected()) {
					fsDem = new File(GUIUtils.defaultLookupDirectoryString() + "\\VariableDemand");
				} else {
					fsDem = new File(GUIUtils.defaultLookupDirectoryString() + "\\FutureDemand");
				}

				// Copy proper WSIDI table AFTER future/variable demand copy
				// TODO: WHY IS THIS ORDER NECESSARY?? IT DOESN'T LOOK LIKE WE'RE DOING THAT NOW?
				// TODO: This kludge (forcing the value of hyd_DSS_Index to be calculated before accessing it) is a temporary fix to
				// issues 98/99. THIS MAY NOT BE NECESSARY - CHECK LOGIC!!

				String result[] = GUIUtils.getHydDSSStrings(swix);

				// copy either variableDemand or futureDemand lookup tables to "Generated" folder
				File fsLookup = new File(scenGeneratedDir_absPath, "Lookup");
				FileUtils.copyDirectory(fsDem, fsLookup, true);

				// copy either variableDemand or futureDemand lookup tables to "Run" folder
				fsLookup = new File(scenRunDir_absPath, "Lookup");
				FileUtils.copyDirectory(fsDem, fsLookup, true);

				// copy WSIDI to "Generated" folder
				FileUtils.copyWSIDItoLookup(result[7], scenGeneratedDir_absPath + "\\Lookup");
				// copy WSIDI to "Run" folder
				FileUtils.copyWSIDItoLookup(result[7], scenRunDir_absPath + "\\Lookup");

				// ==========

				publish("Creating study.sty.");

				Calendar cal = Calendar.getInstance();

				String startMon = ((String) ((JSpinner) swix.find("spnRunStartMonth")).getValue()).trim().toUpperCase();
				String endMon = ((String) ((JSpinner) swix.find("spnRunEndMonth")).getValue()).trim().toUpperCase();
				Integer startYr = (Integer) ((JSpinner) swix.find("spnRunStartYear")).getValue();
				Integer endYr = (Integer) ((JSpinner) swix.find("spnRunEndYear")).getValue();

				// Determine Month/Count
				Integer dayct = FileUtils.getDaysinMonth(startMon);
				Integer iSMon = UnitsUtils.monthToInt(startMon);
				Integer iEMon = UnitsUtils.monthToInt(endMon);
				Integer numMon = (endYr - startYr) * 12 + (iEMon - iSMon) + 1;

				String oDSS = ((JTextField) swix.find("run_txfoDSS")).getText().trim();

				String[] newtext = new String[20];
				Integer[] LineNum = new Integer[20];

				newtext[0] = scen;
				LineNum[0] = 2;
				newtext[1] = cal.getTime().toString();
				LineNum[1] = 4;
				newtext[2] = scenRunDir_absPath;
				LineNum[2] = 7;
				newtext[3] = scenRunDir_absPath + "\\CALLITE_BO_FUTURE.STY";
				LineNum[3] = 8;
				newtext[4] = scenRunDir_absPath + "\\MAIN.WRESL";
				LineNum[4] = 9;
				if (oDSS.toUpperCase().endsWith(".DSS")) {
					newtext[6] = System.getProperty("user.dir") + "\\Scenarios\\" + oDSS;
					LineNum[6] = 11;
				} else {
					newtext[6] = System.getProperty("user.dir") + "\\Scenarios\\" + oDSS + ".DSS";
					LineNum[6] = 11;
				}

				LineNum[5] = 10;
				newtext[5] = scenRunDir_absPath + "\\DSS\\" + ((JTextField) swix.find("hyd_DSS_SV")).getText();
				LineNum[7] = 12;
				newtext[7] = scenRunDir_absPath + "\\DSS\\" + ((JTextField) swix.find("hyd_DSS_Init")).getText();

				newtext[8] = numMon.toString();
				LineNum[8] = 14;
				newtext[9] = dayct.toString();
				LineNum[9] = 15;
				newtext[10] = startMon;
				LineNum[10] = 16;
				newtext[11] = startYr.toString();
				LineNum[11] = 17;

				LineNum[12] = 33;
				newtext[12] = ((JTextField) swix.find("hyd_DSS_SV_F")).getText();
				LineNum[13] = 34;
				newtext[13] = ((JTextField) swix.find("hyd_DSS_Init_F")).getText();

				FileUtils.replaceLinesInFile(scenRunDir_absPath + "\\study.sty", LineNum, newtext);

				// ==========

				pFrame.setText("Writing WRIMSv2 Batchfile.");

				// configuration file for wrims v2
				Integer iStartMonth = TimeOperation.monthValue(startMon.toLowerCase());
				Integer iEndMonth = TimeOperation.monthValue(endMon.toLowerCase());
				Integer iStartDay = TimeOperation.numberOfDays(iStartMonth, startYr);
				Integer iEndDay = TimeOperation.numberOfDays(iEndMonth, endYr);

				Map<String, String> configMap = new HashMap<String, String>();
				configMap.put("MainFile", scenRunDir_absPath + "\\main.wresl");
				configMap.put("DvarFile", newtext[6]);
				configMap.put("SvarFile", newtext[5]);
				configMap.put("SvarFPart", newtext[12]);
				configMap.put("InitFile", newtext[7]);
				configMap.put("InitFPart", newtext[13]);
				configMap.put("StartYear", startYr.toString());
				configMap.put("StartMonth", iStartMonth.toString());
				configMap.put("StartDay", iStartDay.toString());
				configMap.put("EndYear", endYr.toString());
				configMap.put("EndMonth", iEndMonth.toString());
				configMap.put("EndDay", iEndDay.toString());
				configMap.put("UserPath", System.getProperty("user.dir"));
				configMap.put("ScenarioName", scenWithoutExt);
				configMap.put("ScenarioPath", new File(scenRunDir_absPath).getParentFile().getAbsolutePath());
				configMap.put("RunPath", scenRunDir_absPath);
				configMap.put("ConfigFilePath",
				        new File(configMap.get("ScenarioPath"), configMap.get("ScenarioName") + ".config").getAbsolutePath());

				// replace vars in batch file

				// String batchText = wrimsv2.wreslparser.elements.Tools.readFileAsString(System.getProperty("user.dir")
				// + "\\Model_w2\\CalLite_w2.bat.template");
				//
				// batchText = batchText.replace("{ConfigFilePath}", configMap.get("ConfigFilePath"));
				//
				// File f = new File(System.getProperty("user.dir"), "CalLite_w2.bat");
				// PrintWriter cfgFile = new PrintWriter(new BufferedWriter(new FileWriter(f)));
				//
				// cfgFile.print(batchText);
				// cfgFile.flush();
				// cfgFile.close();

				// write scenario config file

				pFrame.setText("Writing Scenario Config.");
				// replace vars in config template file

				String configText = wrimsv2.wreslparser.elements.Tools.readFileAsString(System.getProperty("user.dir")
				        + "\\Model_w2\\config.template");

				configText = configText.replace("{MainFile}", "run\\main.wresl");
				configText = configText.replace("{SvarFile}", configMap.get("SvarFile"));
				configText = configText.replace("{SvarFPart}", configMap.get("SvarFPart"));
				configText = configText.replace("{InitFile}", configMap.get("InitFile"));
				configText = configText.replace("{InitFPart}", configMap.get("InitFPart"));
				configText = configText.replace("{DvarFile}", configMap.get("DvarFile"));
				configText = configText.replace("{StartYear}", configMap.get("StartYear"));
				configText = configText.replace("{StartMonth}", configMap.get("StartMonth"));
				configText = configText.replace("{EndYear}", configMap.get("EndYear"));
				configText = configText.replace("{EndMonth}", configMap.get("EndMonth"));
				configText = configText.replace("{StartDay}", configMap.get("StartDay"));
				configText = configText.replace("{EndDay}", configMap.get("EndDay"));

				File configFile = new File(configMap.get("ConfigFilePath"));

				PrintWriter configFilePW = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

				configFilePW.print(configText);
				configFilePW.flush();
				configFilePW.close();

				// ==========

				pFrame.setText("Copying WRIMSv2 DLL.");

				// wrims2 ANN file name is different from wrims1
				File fsAnnS;
				File fsAnnO_wrims2;
				File fsAnnO_wrims2_generated;

				JRadioButton rdbSLR45 = (JRadioButton) swix.find("hyd_rdb1");
				JRadioButton rdbSLR15 = (JRadioButton) swix.find("hyd_rdb2");

				if (rdbSLR45.isSelected()) {
					fsAnnS = new File(GUIUtils.defaultDirectoryString() + "\\External\\Ann7inp_BDCP_LLT_45cm.dll");
					fsAnnO_wrims2 = new File(scenRunDir_absPath, "External\\Ann7inp_CA.dll");
					fsAnnO_wrims2_generated = new File(scenGeneratedDir_absPath, "External\\Ann7inp_BDCP_LLT_45cm.dll");
				} else if (rdbSLR15.isSelected()) {
					fsAnnS = new File(GUIUtils.defaultDirectoryString() + "\\External\\Ann7inp_BDCP_ELT_15cm.dll");
					fsAnnO_wrims2 = new File(scenRunDir_absPath, "External\\Ann7inp_CA.dll");
					fsAnnO_wrims2_generated = new File(scenGeneratedDir_absPath, "External\\Ann7inp_BDCP_ELT_15cm.dll");
				} else {
					fsAnnS = new File(GUIUtils.defaultDirectoryString() + "\\External\\Ann7inp_BST_noSLR_111709.dll");
					fsAnnO_wrims2 = new File(scenRunDir_absPath, "External\\Ann7inp_CA.dll");
					fsAnnO_wrims2_generated = new File(scenGeneratedDir_absPath, "External\\Ann7inp_BST_noSLR_111709.dll");
				}
				try {
					// copy dll to "Run" folder
					FileUtils.copyDirectory(fsAnnS, fsAnnO_wrims2, true);
					// copy dll to "Generated" folder
					FileUtils.copyDirectory(fsAnnS, fsAnnO_wrims2_generated, true);

				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
					success = false;
				}

				// ==========

				publish("Writing GUI regulations table files.");

				// Write regulations table files
				ArrayList<String> GUITables = new ArrayList<String>();
				GUITables = GUIUtils.getGUITables(links2Lines, "Regulations");

				// TODO: investigate similar code inGUIUtils.java:getTableModelData

				for (int i = 0; i < GUITables.size(); i++) {

					String[] parts = GUITables.get(i).toString().split("[|]");
					String cName = parts[0].trim(); // Get name of controlling checkbox;
					String tableName = gl.tableNameForCtrl(cName); // Find the corresponding table

					// System.out.println("GUI Option Table " + i + ": " + cName);

					if (!tableName.equals("n/a")) {

						// Is the controlling checkbox selected?
						AbstractButton buttonC = (AbstractButton) swix.find(cName);
						if (buttonC == null || !(buttonC instanceof JToggleButton)) {
						} else {
							if (!((JToggleButton) buttonC).isSelected()) {
							} else {
								int tID = Integer.parseInt(gl.tableIDForCtrl(cName));
								String[] files = tableName.split("[|]");
								int size = files.length;
								if (size == 1) {
									if (dTableModels[tID] == null) {
										// System.out.println("Table not initialized - " + tableName);
									} else {
										// Write regulations table files to "Generated" Folder
										dTableModels[tID].writeToFile(scenGeneratedDir_absPath + "\\Lookup", tableName);
										// Write regulations table files to "Run" Folder
										dTableModels[tID].writeToFile(scenRunDir_absPath + "\\Lookup", tableName);
									}
								} else if (size == 2) {
									if (dTableModels[tID] == null) {
										// System.out.println("Table not initialized - nothing written");
									} else {
										// Write regulations table files to "Generated" Folder
										dTableModels[tID].writeToFile2(scenGeneratedDir_absPath + "\\Lookup", files[0], files[1]);
										// Write regulations table files to "Run" Folder
										dTableModels[tID].writeToFile2(scenRunDir_absPath + "\\Lookup", files[0], files[1]);
									}
								} else {
									System.out.println("Unhandled files.length case in FileAction.setupAndRun");
								}

							}
						}

					}
				}

				System.out.println("Done");

				// =====

				publish("Writing GUI operations table files.");

				GUITables = new ArrayList<String>();
				GUITables = GUIUtils.getGUITables(links2Lines, "Operations");

				for (int i = 0; i < GUITables.size(); i++) {
					String[] parts = GUITables.get(i).toString().split("[|]");
					String cName = parts[0].trim();
					String tableName = gl.tableNameForCtrl(cName);
					if (!tableName.equals("n/a")) {
						System.out.println("Output to " + tableName);
						int tID = Integer.parseInt(gl.tableIDForCtrl(cName));
						if (dTableModels[tID] == null) {
							// System.out.println("Table not initialized - " + tableName);
						} else {
							// WriteGUI operations table files to "Generated" Folder
							dTableModels[tID].writeToFile(scenGeneratedDir_absPath + "\\Lookup\\", tableName);
							// WriteGUI operations table files to "Run" Folder
							dTableModels[tID].writeToFile(scenRunDir_absPath + "\\Lookup\\", tableName);
						}

					}

				}

				if (success)
					checkFile.createNewFile();

				desktop.setVisible(false);

				// "Run" model

				// try {
				//
				// pFrame.setCursor(null);
				// pFrame.dispose();
				//
				// Runtime rt = Runtime.getRuntime();
				// Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\CalLite_w2.bat");
				// int exitVal = proc.waitFor();
				// System.out.println("Process exitValue: " + exitVal);
				// } catch (Throwable t) {
				// JOptionPane.showMessageDialog(null, t.getMessage(), "Run failure!", JOptionPane.ERROR_MESSAGE);
				// t.printStackTrace();
				// }
				return null;
			}
		};

		worker_setupScenario.execute();
	}

	public static void saveFile(String scen, SwingEngine swix, Boolean[] regUserEdits, DataFileTableModel[] dTableModels,
	        GUILinks gl) {

		StringBuffer sb = buildScenarioString(swix, regUserEdits, dTableModels, gl);

		FileUtils.createNewFile(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
		File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);

		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e1) {
			System.err.println("Error: " + e1.getMessage());
		}

	}

	/**
	 * Creates a StringBuffer that represents the current settings for the scenario in memory.
	 * 
	 * @param swix
	 * @param RegUserEdits
	 * @param dTableModels
	 * @param gl
	 * @return
	 */
	private static StringBuffer buildScenarioString(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels,
	        GUILinks gl) {

		StringBuffer sb = new StringBuffer();

		// Get control values from UI

		sb = GUIUtils.setControlValues(swix.find("runsettings"), sb);
		sb = GUIUtils.setControlValues(swix.find("hydroclimate"), sb);
		sb = GUIUtils.setControlValues(swix.find("demands"), sb);
		sb = GUIUtils.setControlValues(swix.find("facilities"), sb);
		sb = GUIUtils.setControlValues(swix.find("regulations"), sb);
		sb = GUIUtils.setControlValues(swix.find("operations"), sb);

		// Get table values

		final String NL = System.getProperty("line.separator");
		sb.append("DATATABLEMODELS" + NL);

		ArrayList<String> guiLinks = new ArrayList<String>();
		guiLinks = GUIUtils.getGUILinks("Config\\GUI_Links2.table");

		ArrayList<String> guiTables = new ArrayList<String>();
		guiTables = GUIUtils.getGUITables(guiLinks, "Regulations");
		sb = GUIUtils.getTableModelData(dTableModels, guiTables, gl, sb, swix);

		guiTables = GUIUtils.getGUITables(guiLinks, "Operations");
		sb = GUIUtils.getTableModelData(dTableModels, guiTables, gl, sb, swix);

		sb.append("END DATATABLEMODELS" + NL);

		// Get flags marking user-defined tables

		sb.append("USERDEFINEDFLAGS" + NL);
		for (int i = 0; i < RegUserEdits.length; i++) {
			if (RegUserEdits[i] != null) {
				sb.append(i + "|" + RegUserEdits[i] + NL);
			}
		}
		sb.append("END USERDEFINEDFLAGS" + NL);
		return sb;
	}

	/**
	 * Checks if there are differences between in-memory scenario settings and the settings in the currently referenced scenario
	 * file.
	 * 
	 * @param swix
	 * @param dTableModels
	 * @param regUserEdits
	 * @param gl
	 */
	public static Boolean checkForScenarioChange(SwingEngine swix, DataFileTableModel[] dTableModels, Boolean[] regUserEdits,
	        GUILinks gl) {

		Boolean result = true;
		JPanel mainmenu = (JPanel) swix.find("mainmenu");

		StringBuffer sbInMemory = buildScenarioString(swix, regUserEdits, dTableModels, gl);

		// Read existing file specified in UI

		String scen = ((JTextField) swix.find("run_txfScen")).getText();
		File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
		StringBuffer sbExisting = FileUtils.readScenarioFile(f);

		if (sbInMemory.toString().equals(sbExisting.toString())) {
			result = JOptionPane.showConfirmDialog(mainmenu, "Are you sure you want to exit?", "CalLite Gui",
			        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		} else {

			int n = JOptionPane.showConfirmDialog(mainmenu,
			        "Scenario selections have changed. Would you like to save the changes before exiting?", "CalLite Gui",
			        JOptionPane.YES_NO_CANCEL_OPTION);

			switch (n) {
			case JOptionPane.CANCEL_OPTION:
				result = false;
				break;

			case JOptionPane.NO_OPTION:
				result = true;
				break;

			case JOptionPane.YES_OPTION:

				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new SimpleFileFilter("cls", "CalLite Scenario File (*.cls)"));
				fc.setCurrentDirectory(new File(".//Scenarios"));

				File file = null;
				String filename = null;
				int retval = fc.showSaveDialog(mainmenu);
				if (retval == JFileChooser.CANCEL_OPTION) {
					result = false;
				}

				else {
					if (retval == JFileChooser.APPROVE_OPTION) {

						file = fc.getSelectedFile();
						filename = file.toString();
					}

					if (filename != null) {

						boolean scensave = false;
						if (new File(filename).exists()) {
							n = JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + filename
							        + "' already exists. Press OK to overwrite and then exit.", "CalLite GUI - " + scen,
							        JOptionPane.OK_CANCEL_OPTION);
							scensave = (n == JOptionPane.OK_OPTION);
							result = scensave;
						}
						if (scensave == true) {
							saveScenarioFile(sbInMemory, filename);
						}
					}
				}
				break;
			}

		}
		return result;
	}

	/**
	 * Saves scenario as stored in sb into a scenario file
	 * 
	 * @param sb
	 *            StringBuffer contains all control settings from GUI along with ancillary data.
	 * @param filename
	 */
	private static void saveScenarioFile(StringBuffer sb, String filename) {
		File f;
		FileUtils.createNewFile(filename);
		f = new File(filename);
		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e1) {
			log.debug(e1.getMessage());
		}
	}

}
