package gov.ca.water.calgui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

public class ScenarioSetup {
	public static void CheckForScenarioChange(SwingEngine swix, DataFileTableModel[] dTableModels, Boolean[] RegUserEdits,
	        GUILinks gl) {

		// *** Determine if scenario has changed.

		// Find Main Panels
		JPanel runsettings = (JPanel) swix.find("runsettings");
		JPanel regulations = (JPanel) swix.find("regulations");
		JPanel hydroclimate = (JPanel) swix.find("hydroclimate");
		JPanel demands = (JPanel) swix.find("demands");
		JPanel operations = (JPanel) swix.find("operations");
		JPanel facilities = (JPanel) swix.find("facilities");
		JPanel mainmenu = (JPanel) swix.find("mainmenu");

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
		JTextField tf = (JTextField) swix.find("run_txfScen");
		String scen = tf.getText();
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

				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new CLSFileFilter());
				fc.setCurrentDirectory(new File(".//Scenarios"));

				String dirname = ".//Scenarios";
				File file = null;
				String filename = null;
				int retval = fc.showSaveDialog(mainmenu);
				if (retval == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					filename = file.toString();
				}

				if (filename != null) {

					if (new File(filename).exists())
						scensave = (JOptionPane
						        .showConfirmDialog(mainmenu, "The scenario file '" + filename
						                + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
						                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

					if (scensave = true) {

						GUIUtils.CreateNewFile(filename);
						f = new File(filename);
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

	}
}
