package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.UnitsUtils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

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
	private Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private int action_WSIDI;

	public FileAction(JFrame desktop, SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        int action_WSIDI) {
		this.desktop = desktop;
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.action_WSIDI = action_WSIDI;

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		// TODO: Break out individual actions for readability?

		if ("AC_RUN".equals(ae.getActionCommand())) {

			// Check if selections are valid
			JTextField tf = (JTextField) swix.find("run_txfScen");
			String scen = tf.getText();

			JSpinner spn = (JSpinner) swix.find("spnRunStartMonth");
			String StartMon = (String) spn.getValue();
			StartMon = StartMon.trim();
			spn = (JSpinner) swix.find("spnRunEndMonth");
			String EndMon = (String) spn.getValue();
			EndMon = EndMon.trim();
			spn = (JSpinner) swix.find("spnRunStartYear");
			Integer StartYr = (Integer) spn.getValue();
			spn = (JSpinner) swix.find("spnRunEndYear");
			Integer EndYr = (Integer) spn.getValue();

			// Determine Month/Count
			Integer iSMon = UnitsUtils.monthToInt(StartMon);
			Integer iEMon = UnitsUtils.monthToInt(EndMon);

			// Find Main Panels
			JPanel runsettings = (JPanel) swix.find("runsettings");
			JPanel regulations = (JPanel) swix.find("regulations");
			JPanel hydroclimate = (JPanel) swix.find("hydroclimate");
			JPanel demands = (JPanel) swix.find("demands");
			JPanel operations = (JPanel) swix.find("operations");
			JPanel facilities = (JPanel) swix.find("facilities");
			JPanel mainmenu = (JPanel) swix.find("mainmenu");

			Integer numMon;
			numMon = (EndYr - StartYr) * 12 + (iEMon - iSMon) + 1;

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

					// Store selections
					StringBuffer sb = new StringBuffer();
					sb = GUIUtils.GetControlValues(runsettings, sb);
					sb = GUIUtils.GetControlValues(regulations, sb);
					sb = GUIUtils.GetControlValues(hydroclimate, sb);
					sb = GUIUtils.GetControlValues(demands, sb);
					sb = GUIUtils.GetControlValues(operations, sb);
					sb = GUIUtils.GetControlValues(facilities, sb);

					// get table values.
					final String NL = System.getProperty("line.separator");
					sb.append("DATATABLEMODELS" + NL);
					ArrayList GUILinks = new ArrayList();
					ArrayList GUITables = new ArrayList();
					GUILinks = GUIUtils.GetGUILinks("Config\\GUI_Links2.table");
					GUITables = GUIUtils.GetGUITables(GUILinks, "Regulations");
					sb = GUIUtils.GetTableModelData(dTableModels, GUITables, gl, sb, swix);
					GUITables = GUIUtils.GetGUITables(GUILinks, "Operations");
					sb = GUIUtils.GetTableModelData(dTableModels, GUITables, gl, sb, swix);
					sb.append("END DATATABLEMODELS" + NL);
					sb.append("USERDEFINEDFLAGS" + NL);
					for (int i = 0; i < RegUserEdits.length; i++) {
						if (RegUserEdits[i] != null) {
							sb.append(i + "|" + RegUserEdits[i] + NL);
						}
					}
					sb.append("END USERDEFINEDFLAGS" + NL);

					// Read existing file
					File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
					StringBuffer sbExisting = GUIUtils.ReadScenarioFile(f);

					Boolean scensave = false;

					if (!sb.toString().equals(sbExisting.toString())) {

						/*
						 * int n = JOptionPane.showConfirmDialog(mainmenu,
						 * "Would you like to save the scenario definition? \nScenario information " + "will be saved to '" +
						 * System.getProperty("user.dir") + "\\Scenarios\\" + scen + "'", "CalLite Gui", JOptionPane.YES_NO_OPTION);
						 */
						int n = JOptionPane.showConfirmDialog(mainmenu,
						        "Scenario selections have changed. Would you like to save the changes?", "CalLite Gui",
						        JOptionPane.YES_NO_OPTION);

						if (n == JOptionPane.YES_OPTION) {
							GetDSSFilename getScenFilename;
							getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");
							getScenFilename.actionPerformed(ae);
							if (getScenFilename.dialogRC != 0)
								scensave = false;
							else {
								tf = (JTextField) swix.find("run_txfScen");
								scen = tf.getText();

								if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen)).exists())
									scensave = (JOptionPane.showConfirmDialog(mainmenu,
									        "The scenario file '" + System.getProperty("user.dir") + "\\Scenarios\\" + scen
									                + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
									        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

								if (scensave = true) {
									String scenFilename;
									scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
									String desktopTitle;
									desktopTitle = desktop.getTitle() + ";  Scenario";
									desktop.setTitle(desktopTitle + " - " + scenFilename);
									((JTextField) swix.find("run_txfoDSS")).setText(scenFilename.substring(0,
									        scenFilename.length() - 4) + "_DV.DSS");

									GUIUtils.createNewFile(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
									f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
									try {
										FileWriter fstream = new FileWriter(f);
										BufferedWriter outobj = new BufferedWriter(fstream);
										outobj.write(sb.toString());
										outobj.close();

									} catch (Exception e1) {
										System.err.println("Error: " + e1.getMessage());
									}
								}

							}
						}

					}
					setupAndRun(scen, desktop, swix, RegUserEdits, dTableModels, gl);
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
					String scenFilename;
					scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
					String desktopTitle;
					desktopTitle = desktop.getTitle() + ";  Scenario";
					desktop.setTitle(desktopTitle + " - " + scenFilename);
					((JTextField) swix.find("run_txfoDSS")).setText(scenFilename.substring(0, scenFilename.length() - 4)
					        + "_DV.DSS");
				}
			}
			if (proceed) {
				JPanel mainmenu = (JPanel) swix.find("mainmenu");
				JTextField tf = (JTextField) swix.find("run_txfScen");
				String scen = tf.getText();
				if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen)).exists())
					proceed = (JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + System.getProperty("user.dir")
					        + "\\Scenarios\\" + scen + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
					        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

				if (proceed) {
					saveFile(scen, swix, RegUserEdits, dTableModels, gl);
				} else {
					JFrame frame = new JFrame("Error");

					// show a joptionpane dialog using showMessageDialog
					JOptionPane.showMessageDialog(frame, "You must specify a scenario name.");

				}
			}

		} else if (ae.getActionCommand().startsWith("AC_LoadScen")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new ScenarioFileFilter());
			fc.setCurrentDirectory(new File(".//Scenarios"));
			JPanel mainmenu = (JPanel) swix.find("mainmenu");
			int retval = fc.showOpenDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				// ... The user selected a file, get it, use it.

				File file = fc.getSelectedFile();
				GetDSSFilename getScenFilename;
				getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");
				getScenFilename.fc.setSelectedFile(file); // Use this name for
				                                          // next Save As

				action_WSIDI = 0;
				RegUserEdits = GUIUtils.SetControlValues(file, swix, dTableModels, gl);
				RegUserEdits = GUIUtils.SetControlValues(file, swix, dTableModels, gl);
				action_WSIDI = 1;
				String scenFilename;
				scenFilename = file.getName();
				String desktopTitle;
				desktopTitle = desktop.getTitle() + ";  Scenario";
				desktop.setTitle(desktopTitle + " - " + scenFilename);
				JTextField tf = (JTextField) swix.find("run_txfScen");
				tf.setText(scenFilename);

			}

		} else if (ae.getActionCommand().startsWith("AC_CompScen")) {

			// Save current settings to temp file
			saveFile("Current_Scenario", swix, RegUserEdits, dTableModels, gl);
			ScenarioFrame ScenFrame = new ScenarioFrame("CalLite 2.0 GUI - Scenario Comparison", swix);
			ScenFrame.setVisible(true);
			// Set Icon
			// java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
			// ScenFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
			ScenFrame.setVisible(true);
			// Delete temp file
			// File ft = new File(System.getProperty("user.dir") +
			// "\\Scenarios\\Current_Scenario.cls");
			// GUI_Utils.deleteDir(ft);

		} else if (ae.getActionCommand().startsWith("AC_ViewScen")) {

			// Save current settings to temp file
			saveFile("Current_Scenario", swix, RegUserEdits, dTableModels, gl);
			// Build list
			Object scenlist[] = { "Current_Scenario" };
			// Show frame
			ScenarioTable sTableFrame = new ScenarioTable(scenlist, swix);
			// java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
			// sTableFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
			sTableFrame.setVisible(true);
			// Delete temp file
			File ft = new File(System.getProperty("user.dir") + "\\Scenarios\\Current_Scenario");
			GUIUtils.deleteDir(ft);
		}
	}

	/**
	 * Creates a scenario directory, then copies contents of Default and Default\Lookup to new directory
	 * 
	 * @param runDirName
	 */
	public static void setupScenarioDirectory(String runDirName) {

		File ft = new File(System.getProperty("user.dir") + runDirName);
		// First delete existing Run directory.
		GUIUtils.deleteDir(ft);
		ft.mkdirs();

		// Copy wrims2 wresl directory to Run directory
		File wreslDir = new File(System.getProperty("user.dir") + "\\Model_w2\\wresl");

		try {
			GUIUtils.copyDirectory(wreslDir, ft, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Copy Default dir to Run dir. This may overwrite wrims2
		// wresl's copy
		File fs = new File(System.getProperty("user.dir") + "\\Default");
		try {
			GUIUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Copy lookup files.
		fs = new File(System.getProperty("user.dir") + "\\Default\\Lookup");
		ft = new File(System.getProperty("user.dir") + runDirName + "\\Lookup");
		try {
			GUIUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Copies indicates DSS file from \Default\DSS to run directory
	 * 
	 * @param runDirName
	 * @param dssFileName
	 */
	private static void copyDSSFileToScenarioDirectory(String runDirName, String dssFileName) {

		File ft = new File(System.getProperty("user.dir") + runDirName + "\\DSS");
		ft.mkdir();

		// TODO: Files are assumed to be in Default\DSS
		File fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\" + dssFileName);
		ft = new File(System.getProperty("user.dir") + runDirName + "\\DSS\\" + dssFileName);

		try {
			GUIUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
	public static void writeScenarioTables(ArrayList<String> links, Boolean[] UDFlags, SwingEngine swix) throws IOException {

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

					f = new File(System.getProperty("user.dir") + "\\Run\\Lookup\\" + openFileName);
					FileInputStream fin = new FileInputStream(f);
					System.out.println(tableFileName + ":" + openFileName);
					BufferedReader br = new BufferedReader(new InputStreamReader(fin));
					StringBuffer header = new StringBuffer();
					String aLine = br.readLine();
					while (aLine.startsWith("!") && aLine != null) {
						header.append(aLine + NL);
						aLine = br.readLine();
					}

					// Close existing table file and delete

					br.close();
					GUIUtils.deleteDir(f);

					// Create a new file with the same name and write the header comments

					FileWriter fstream = new FileWriter(f);
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

						option = "0";

					} else {

						// If it is selected, set option to "1" - true ...
						option = "1";

						// ... but check if "user defined" flag is turned on - only for inputs that will be stored as separate data
						// tables. Those separate tables are written elsewhere

						if (linkParts.length > 8) {
							cID = linkParts[8];
							tID = Integer.parseInt(cID);
							if (UDFlags != null) {
								if (UDFlags[tID] != null) {
									if (UDFlags[tID] == true) {
										option = "2";
									}
								}
							}

						} else {
							option = "1"; // TODO: Check if this is removable
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
	 * @param RegUserEdits
	 * @param dTableModels
	 * @param gl
	 */
	public static void setupAndRun(final String scen, final JFrame desktop, final SwingEngine swix, final Boolean[] RegUserEdits,
	        final DataFileTableModel[] dTableModels, final GUILinks gl) {

		pFrame = new ProgressFrame("CalLite 2.0 GUI - Setting Up Run");

		SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

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

				// Extract "model" values from "view"

				String runDirName = "\\Run";

				String hydDSSSVName = ((JTextField) swix.find("hyd_DSS_SV")).getText();
				String hydDSSInitName = ((JTextField) swix.find("hyd_DSS_Init")).getText();

				// Copy Run directory

				publish("Creating new Run directory.");

				setupScenarioDirectory(runDirName);
				copyDSSFileToScenarioDirectory(runDirName, hydDSSSVName);
				copyDSSFileToScenarioDirectory(runDirName, ((JTextField) swix.find("hyd_DSS_SV")).getText());

				publish("Writing GUI tables.");
				ArrayList<String> links2Lines = new ArrayList<String>();
				links2Lines = GUIUtils.GetGUILinks("Config\\GUI_Links2.table");

				try {
					writeScenarioTables(links2Lines, RegUserEdits, swix);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				publish("Copying demand tables.");

				File fsDem;
				/*
				 * rdb = (JRadioButton) swix.find("dem_rdbCurSWP"); if (rdb.isSelected()) { fsDem = new
				 * File(System.getProperty("user.dir") + "\\Run\\Lookup\\FutureDemand"); } else { fsDem = new
				 * File(System.getProperty("user.dir") + "\\Run\\Lookup\\VariableDemand"); } GUI_Utils.deleteDir(fsDem);
				 */

				JRadioButton rdb = (JRadioButton) swix.find("dem_rdbCurSWP");

				if (rdb.isSelected()) {
					fsDem = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\VariableDemand");
				} else {
					fsDem = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\FutureDemand");
				}

				// Copy proper WSIDI table AFTER future/variable demand copy

				GUIUtils.copyWSIDItoLookup(((JTextField) swix.find("hyd_DSS_Index")).getText(), "\\Run\\Lookup");

				File fsLookup = new File(System.getProperty("user.dir") + "\\Run\\Lookup");

				GUIUtils.copyDirectory(fsDem, fsLookup, true);

				publish("Creating study.sty.");

				// Write study.sty
				Calendar cal = Calendar.getInstance();

				JSpinner spn = (JSpinner) swix.find("spnRunStartMonth");
				String StartMon = (String) spn.getValue();
				StartMon = StartMon.trim();
				spn = (JSpinner) swix.find("spnRunEndMonth");
				String EndMon = (String) spn.getValue();
				EndMon = EndMon.trim();
				spn = (JSpinner) swix.find("spnRunStartYear");
				Integer StartYr = (Integer) spn.getValue();
				spn = (JSpinner) swix.find("spnRunEndYear");
				Integer EndYr = (Integer) spn.getValue();

				// Determine Month/Count
				Integer dayct = GUIUtils.DaysinMonth(StartMon);
				Integer iSMon = UnitsUtils.monthToInt(StartMon);
				Integer iEMon = UnitsUtils.monthToInt(EndMon);

				Integer numMon;
				numMon = (EndYr - StartYr) * 12 + (iEMon - iSMon) + 1;
				StartMon = StartMon.toUpperCase();

				JTextField tf = (JTextField) swix.find("run_txfoDSS");
				String oDSS = tf.getText().trim();

				String[] newtext = new String[20];
				Integer[] LineNum = new Integer[20];

				newtext[0] = scen;
				LineNum[0] = 2;
				newtext[1] = cal.getTime().toString();
				LineNum[1] = 4;
				newtext[2] = System.getProperty("user.dir") + "\\Run";
				LineNum[2] = 7;
				newtext[3] = System.getProperty("user.dir") + "\\Run\\CALLITE_BO_FUTURE.STY";
				LineNum[3] = 8;
				newtext[4] = System.getProperty("user.dir") + "\\Run\\MAIN_BO.WRESL";
				LineNum[4] = 9;
				if (oDSS.toUpperCase().endsWith(".DSS")) {
					newtext[6] = System.getProperty("user.dir") + "\\Scenarios\\" + oDSS;
					LineNum[6] = 11;
				} else {
					newtext[6] = System.getProperty("user.dir") + "\\Scenarios\\" + oDSS + ".DSS";
					LineNum[6] = 11;
				}

				LineNum[5] = 10;
				newtext[5] = System.getProperty("user.dir") + "\\Run\\DSS\\" + ((JTextField) swix.find("hyd_DSS_SV")).getText();
				LineNum[7] = 12;
				newtext[7] = System.getProperty("user.dir") + "\\Run\\DSS\\" + ((JTextField) swix.find("hyd_DSS_Init")).getText();

				newtext[8] = numMon.toString();
				LineNum[8] = 14;
				newtext[9] = dayct.toString();
				LineNum[9] = 15;
				newtext[10] = StartMon;
				LineNum[10] = 16;
				newtext[11] = StartYr.toString();
				LineNum[11] = 17;

				LineNum[12] = 33;
				newtext[12] = ((JTextField) swix.find("hyd_DSS_SV_F")).getText();
				LineNum[13] = 34;
				newtext[13] = ((JTextField) swix.find("hyd_DSS_Init_F")).getText();

				GUIUtils.replaceLinesInFile(System.getProperty("user.dir") + "\\Run\\study.sty", LineNum, newtext);

				pFrame.setText("Writing WRIMSv2 Batchfile.");

				// wrims2 configuration

				// configuration file for wrims v2
				Integer iStartMonth = TimeOperation.monthValue(StartMon.toLowerCase());
				Integer iEndMonth = TimeOperation.monthValue(EndMon.toLowerCase());
				Integer iStartDay = TimeOperation.numberOfDays(iStartMonth, StartYr);
				Integer iEndDay = TimeOperation.numberOfDays(iEndMonth, EndYr);

				Map<String, String> configMap = new HashMap<String, String>();
				configMap.put("MainFile", System.getProperty("user.dir") + "\\Run\\main_bo.wresl");
				configMap.put("DvarFile", newtext[6]);
				configMap.put("SvarFile", newtext[5]);
				configMap.put("SvarFPart", newtext[12]);
				configMap.put("InitFile", newtext[7]);
				configMap.put("InitFPart", newtext[13]);
				configMap.put("StartYear", StartYr.toString());
				configMap.put("StartMonth", iStartMonth.toString());
				configMap.put("StartDay", iStartDay.toString());
				configMap.put("EndYear", EndYr.toString());
				configMap.put("EndMonth", iEndMonth.toString());
				configMap.put("EndDay", iEndDay.toString());

				// replace vars in batch file

				String batchText = wrimsv2.wreslparser.elements.Tools.readFileAsString(System.getProperty("user.dir")
				        + "\\Model_w2\\CalLite_w2.bat.template");

				batchText = batchText.replace("{MainFile}", configMap.get("MainFile"));
				batchText = batchText.replace("{SvarFile}", configMap.get("SvarFile"));
				batchText = batchText.replace("{SvarFPart}", configMap.get("SvarFPart"));
				batchText = batchText.replace("{InitFile}", configMap.get("InitFile"));
				batchText = batchText.replace("{InitFPart}", configMap.get("InitFPart"));
				batchText = batchText.replace("{DvarFile}", configMap.get("DvarFile"));
				batchText = batchText.replace("{StartYear}", configMap.get("StartYear"));
				batchText = batchText.replace("{StartMonth}", configMap.get("StartMonth"));
				batchText = batchText.replace("{EndYear}", configMap.get("EndYear"));
				batchText = batchText.replace("{EndMonth}", configMap.get("EndMonth"));
				batchText = batchText.replace("{StartDay}", configMap.get("StartDay"));
				batchText = batchText.replace("{EndDay}", configMap.get("EndDay"));

				// System.out.println(batchText);

				// write WRIMSv2 batch file
				File f = new File(System.getProperty("user.dir"), "CalLite_w2.bat");

				PrintWriter cfgFile = new PrintWriter(new BufferedWriter(new FileWriter(f)));

				cfgFile.print(batchText);
				cfgFile.flush();
				cfgFile.close();

				pFrame.setText("Copying WRIMSv1 DLL.");

				// Sea Level Selections
				File fsAnnO;
				File fsAnnS;
				JRadioButton rdbSLR45 = (JRadioButton) swix.find("hyd_rdb1");
				JRadioButton rdbSLR15 = (JRadioButton) swix.find("hyd_rdb2");
				if (rdbSLR45.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BDCP_LLT_45cm.dll");
					fsAnnO = new File(System.getProperty("user.dir") + "\\Model\\Ann.dll");
				} else if (rdbSLR15.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BDCP_ELT_15cm.dll");
					fsAnnO = new File(System.getProperty("user.dir") + "\\Model\\Ann.dll");
				} else {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BST_noSLR_111709.dll");
					fsAnnO = new File(System.getProperty("user.dir") + "\\Model\\Ann.dll");
				}
				try {
					GUIUtils.copyDirectory(fsAnnS, fsAnnO, true);
				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
				}

				pFrame.setText("Copying WRIMSv2 DLL.");

				// wrims2 ANN file name is different from wrims1
				File fsAnnO_wrims2;

				if (rdbSLR45.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BDCP_LLT_45cm.dll");
					fsAnnO_wrims2 = new File(System.getProperty("user.dir") + "\\Run\\External\\Ann7inp_CA.dll");
				} else if (rdbSLR15.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BDCP_ELT_15cm.dll");
					fsAnnO_wrims2 = new File(System.getProperty("user.dir") + "\\Run\\External\\Ann7inp_CA.dll");
				} else {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BST_noSLR_111709.dll");
					fsAnnO_wrims2 = new File(System.getProperty("user.dir") + "\\Run\\External\\Ann7inp_CA.dll");
				}
				try {
					GUIUtils.copyDirectory(fsAnnS, fsAnnO_wrims2, true);
				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
				}

				publish("Writing GUI option tables.");

				// Write regulations table files
				ArrayList<String> GUITables = new ArrayList<String>();
				GUITables = GUIUtils.GetGUITables(links2Lines, "Regulations");

				for (int i = 0; i < GUITables.size(); i++) {
					System.out.println(i);
					String line = GUITables.get(i).toString();
					String[] parts = line.split("[|]");
					String cName = parts[0].trim(); // Get name of controlling
					                                // checkbox;
					String tableName = gl.tableNameForCtrl(cName); // Find the
					                                               // corresponding
					                                               // table
					String switchID = gl.switchIDForCtrl(cName); // Get the
					                                             // switchID
					                                             // (index in
					                                             // .table
					                                             // file)

					int tID = Integer.parseInt(gl.tableIDForCtrl(cName));

					int option = 0;
					AbstractButton buttonC = (AbstractButton) swix.find(cName);
					if (buttonC == null || !(buttonC instanceof JToggleButton)) {
						option = 0;
					} else {
						JToggleButton cb = (JToggleButton) swix.find(cName);
						if (!cb.isSelected()) {
							option = 0;
						} else {
							option = 1;
						}
					}
					System.out.println(switchID + " " + option);

					if ((option == 2) || (option == 1)) {

						String[] files = tableName.split("[|]");
						int size = files.length;
						if (size == 1) {
							// CASE 1: 1 file specified
							System.out.println("Output to " + tableName);
							String fo = System.getProperty("user.dir") + "\\Run\\Lookup\\" + tableName + ".table";

							if (dTableModels[tID] == null) {
								System.out.println("Table not initialized - " + tableName);
							} else {
								dTableModels[tID].writeToFile(tableName);
							}
						} else if (size == 2) {
							// CASE 2: 2 files specified
							System.out.println("Output to " + files[0]);
							String fo1 = System.getProperty("user.dir") + "\\Run\\Lookup\\" + files[0] + ".table";
							String fo2 = System.getProperty("user.dir") + "\\Run\\Lookup\\" + files[1] + ".table";

							if (dTableModels[tID] == null) {
								System.out.println("Table not initialized");
							} else {
								dTableModels[tID].writeToFile2(files[0], files[1]);
							}

						}

					}
				}

				// Write operations table files
				GUITables = new ArrayList();
				GUITables = GUIUtils.GetGUITables(links2Lines, "Operations");

				for (int i = 0; i < GUITables.size(); i++) {
					String line = GUITables.get(i).toString();
					String[] parts = line.split("[|]");
					String cName = parts[0].trim();
					String tableName = gl.tableNameForCtrl(cName);
					String switchID = gl.switchIDForCtrl(cName);

					int tID = Integer.parseInt(gl.tableIDForCtrl(cName));

					System.out.println("Output to " + tableName);
					String fo = System.getProperty("user.dir") + "\\Run\\Lookup\\" + tableName + ".table";

					if (dTableModels[tID] == null) {
						System.out.println("Table not initialized - " + tableName);
					} else {
						dTableModels[tID].writeToFile(tableName);
					}

				}

				desktop.setVisible(false);

				// "Run" model

				try {

					pFrame.setCursor(null);
					pFrame.dispose();

					Runtime rt = Runtime.getRuntime();
					Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\CalLite_w2.bat");
					int exitVal = proc.waitFor();
					System.out.println("Process exitValue: " + exitVal);
				} catch (Throwable t) {
					JOptionPane.showMessageDialog(null, t.getMessage());
					t.printStackTrace();
				}
				return null;
			}
		};

		worker.execute();
	}

	public static void saveFile(String scen, SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels,
	        GUILinks gl) {

		JPanel runsettings = (JPanel) swix.find("runsettings");
		JPanel regulations = (JPanel) swix.find("regulations");
		JPanel hydroclimate = (JPanel) swix.find("hydroclimate");
		JPanel demands = (JPanel) swix.find("demands");
		JPanel operations = (JPanel) swix.find("operations");
		JPanel facilities = (JPanel) swix.find("facilities");

		StringBuffer sb = new StringBuffer();
		sb = GUIUtils.GetControlValues(runsettings, sb);
		sb = GUIUtils.GetControlValues(hydroclimate, sb);
		sb = GUIUtils.GetControlValues(demands, sb);
		sb = GUIUtils.GetControlValues(facilities, sb);
		sb = GUIUtils.GetControlValues(regulations, sb);
		sb = GUIUtils.GetControlValues(operations, sb);

		// get table values.
		final String NL = System.getProperty("line.separator");
		sb.append("DATATABLEMODELS" + NL);
		ArrayList GUITables = new ArrayList();
		ArrayList GUILinks = new ArrayList();
		GUILinks = GUIUtils.GetGUILinks("Config\\GUI_Links2.table");
		GUITables = GUIUtils.GetGUITables(GUILinks, "Regulations");
		sb = GUIUtils.GetTableModelData(dTableModels, GUITables, gl, sb, swix);
		GUITables = GUIUtils.GetGUITables(GUILinks, "Operations");
		sb = GUIUtils.GetTableModelData(dTableModels, GUITables, gl, sb, swix);
		sb.append("END DATATABLEMODELS" + NL);
		sb.append("USERDEFINEDFLAGS" + NL);
		for (int i = 0; i < RegUserEdits.length; i++) {
			if (RegUserEdits[i] != null) {
				sb.append(i + "|" + RegUserEdits[i] + NL);
			}
		}
		sb.append("END USERDEFINEDFLAGS" + NL);

		GUIUtils.createNewFile(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
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

}
