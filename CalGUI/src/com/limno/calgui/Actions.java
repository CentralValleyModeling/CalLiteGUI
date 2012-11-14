package com.limno.calgui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;

import org.swixml.SwingEngine;

import wrimsv2.evaluator.TimeOperation;

public class Actions {
	public static void AC_Run(ActionEvent e, JFrame desktop, SwingEngine swix, Boolean[] RegUserEdits,
	        DataFileTableModel[] dTableModels, GUILinks gl) {
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
		Integer iSMon = GUI_Utils.MonthStr2int(StartMon);
		Integer iEMon = GUI_Utils.MonthStr2int(EndMon);

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
				sb = GUI_Utils.GetControlValues(runsettings, sb);
				sb = GUI_Utils.GetControlValues(regulations, sb);
				sb = GUI_Utils.GetControlValues(hydroclimate, sb);
				sb = GUI_Utils.GetControlValues(demands, sb);
				sb = GUI_Utils.GetControlValues(operations, sb);
				sb = GUI_Utils.GetControlValues(facilities, sb);

				// get table values.
				final String NL = System.getProperty("line.separator");
				sb.append("DATATABLEMODELS" + NL);
				ArrayList GUILinks = new ArrayList();
				ArrayList GUITables = new ArrayList();
				GUILinks = GUI_Utils.GetGUILinks("Config\\GUI_Links2.table");
				GUITables = GUI_Utils.GetGUITables(GUILinks, "Regulations");
				sb = GUI_Utils.GetTableModelData(dTableModels, GUITables, gl, sb, swix);
				GUITables = GUI_Utils.GetGUITables(GUILinks, "Operations");
				sb = GUI_Utils.GetTableModelData(dTableModels, GUITables, gl, sb, swix);
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
				StringBuffer sbExisting = GUI_Utils.ReadScenarioFile(f);

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
						getScenFilename.actionPerformed(e);
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
								((JTextField) swix.find("run_txfoDSS"))
								        .setText(scenFilename.substring(0, scenFilename.length() - 4) + "_DV.DSS");

								GUI_Utils.CreateNewFile(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
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

	}

	static ProgressFrame pFrame;

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

				// Copy Run directory

				pFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				desktop.setEnabled(false);

				publish("Creating new Run directory.");

				File ft = new File(System.getProperty("user.dir") + "\\Run");
				// First delete existing Run directory.
				GUI_Utils.deleteDir(ft);
				ft.mkdirs();

				// Copy wrims2 wresl directory to Run directory
				File wreslDir = new File(System.getProperty("user.dir") + "\\Model_w2\\wresl");

				try {
					GUI_Utils.copyDirectory(wreslDir, ft, true);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Copy Default dir to Run dir. This may overwrite wrims2
				// wresl's copy
				File fs = new File(System.getProperty("user.dir") + "\\Default");
				try {
					GUI_Utils.copyDirectory(fs, ft, false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Copy lookup files.
				fs = new File(System.getProperty("user.dir") + "\\Default\\Lookup");
				ft = new File(System.getProperty("user.dir") + "\\Run\\Lookup");
				try {
					GUI_Utils.copyDirectory(fs, ft, false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Copy DSS files.
				ft = new File(System.getProperty("user.dir") + "\\Run\\DSS");
				ft.mkdir();

				// TODO: Files are assumed to be in Default\DSS
				fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\"
				        + ((JTextField) swix.find("hyd_DSS_SV")).getText());
				ft = new File(System.getProperty("user.dir") + "\\Run\\DSS\\" + ((JTextField) swix.find("hyd_DSS_SV")).getText());

				try {
					GUI_Utils.copyDirectory(fs, ft, false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\"
				        + ((JTextField) swix.find("hyd_DSS_Init")).getText());
				ft = new File(System.getProperty("user.dir") + "\\Run\\DSS\\" + ((JTextField) swix.find("hyd_DSS_Init")).getText());
				try {
					GUI_Utils.copyDirectory(fs, ft, false);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				publish("Writing GUI tables.");

				// Get GUI Link Array
				ArrayList GUILinks = new ArrayList();
				GUILinks = GUI_Utils.GetGUILinks("Config\\GUI_Links2.table");

				// Write GUI Tables

				try {
					GUI_Utils.WriteGUITables(GUILinks, RegUserEdits, swix);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				publish("Copying demand tables.");

				/*
				 * GUI_Utils .ReplaceLineInFile(System.getProperty("user.dir") + "\\Run\\Lookup\\options.table", 13, "9       " +
				 * LODFlag + "   !Level of Development, LOD_Future = 1 for future and 0 for existing" );
				 */

				// Copy 2005/2030 lookup tables
				// pMon.setNote("Copying lookup tables...");
				// pMon.setProgress(30);
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

				GUI_Utils.copyWSIDItoLookup(((JTextField) swix.find("hyd_DSS_Index")).getText(), "\\Run\\Lookup");

				File fsLookup = new File(System.getProperty("user.dir") + "\\Run\\Lookup");

				GUI_Utils.copyDirectory(fsDem, fsLookup, true);

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
				Integer dayct = GUI_Utils.DaysinMonth(StartMon);
				Integer iSMon = GUI_Utils.MonthStr2int(StartMon);
				Integer iEMon = GUI_Utils.MonthStr2int(EndMon);

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

				GUI_Utils.ReplaceLinesInFile(System.getProperty("user.dir") + "\\Run\\study.sty", LineNum, newtext);

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
					GUI_Utils.copyDirectory(fsAnnS, fsAnnO, true);
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
					GUI_Utils.copyDirectory(fsAnnS, fsAnnO_wrims2, true);
				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
				}

				publish("Writing GUI option tables.");

				// Write regulations table files
				ArrayList GUITables = new ArrayList();
				GUITables = GUI_Utils.GetGUITables(GUILinks, "Regulations");

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
				GUITables = GUI_Utils.GetGUITables(GUILinks, "Operations");

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

}
