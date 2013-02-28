package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.FileUtils;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

public class RunListener implements ItemListener {
	private final JFrame desktop;
	private final SwingEngine swix;
	private JComponent component;
	private final Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private int action_WSIDI;

	public RunListener(JFrame desktop, SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        int action_WSIDI) {
		this.desktop = desktop;
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.action_WSIDI = action_WSIDI;

	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		JComponent component = (JComponent) ie.getItem();

		String cName = component.getName();
		if (cName != null) {

			if (cName.startsWith("run_rdb")) {

				// Handling for update of DSS files

				// Confirm CWP/SWP overwrite;
				if (((JRadioButton) component).isSelected() && action_WSIDI != 2) {

					// Actions are only performed if selection is being set to
					// true

					int option = JOptionPane.YES_OPTION; // default is to
					                                     // overwrite
					                                     // everything
					boolean isntDefault = false;
					if (RegUserEdits != null) {
						if (RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn1"))] != null)
							isntDefault = isntDefault || RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn1"))];
						if (RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn2"))] != null)
							isntDefault = isntDefault || RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn2"))];

					}
					if ((action_WSIDI == 1) && (isntDefault))

						// Prompt is needed only in "regular" processing
						// (action_WSIDI = 1) with non-default table

						option = JOptionPane
						        .showConfirmDialog(
						                desktop,
						                "You have made changes to the SWP and/or CVP WSI/DI curves. \n"
						                        + "Do you want to overwrite these changes with the default values for the configuration ("
						                        + ((JRadioButton) component).getText()
						                        + " ) you have selected?\n\n"
						                        + "Press YES to overwrite, NO to use these values in the selected configuration, or Cancel to revert");

					// Once option is determined, process ...

					if (option == JOptionPane.CANCEL_OPTION) {

						// Cancel: undo change by selecting "other" radio button

						String newcName = null;
						if (cName.equals("hyd_rdb2005"))
							newcName = "hyd_rdb2030";
						else if (cName.equals("hyd_rdb2030"))
							newcName = "hyd_rdb2005";
						else if (cName.equals("run_rdbD1641"))
							newcName = "run_rdbBO";
						else if (cName.equals("run_rdbBO"))
							newcName = "run_rdbD1641";

						action_WSIDI = 2; // Skip all actions on update

						System.out.println(cName + ":-" + newcName);
						((JRadioButton) swix.find(newcName)).setSelected(true);

						action_WSIDI = 1;

					} else {

						// Yes or no: first determine which GUI_link4.table row
						// to use

						String lookup = ((JRadioButton) swix.find("run_rdbD1641")).isSelected() ? "1" : "2";
						lookup = lookup + (((JRadioButton) swix.find("hyd_rdb2005")).isSelected() ? "1" : "2");
						if (((JRadioButton) swix.find("hyd_rdbHis")).isSelected())
							lookup = lookup + "10";
						else {
							lookup = lookup + (((JRadioButton) swix.find("hyd_rdbMid")).isSelected() ? "2" : "3");

							// TODO: Finish off with CCModelID? Move to batch
							// processing?

						}

						String table4[][]; // Holds GUI_links4.table values that control
						// selection of SV and Init DSS as well as
						// WSI_DI
						// Read Schematic_DSS_link4.table and place in Table4 (for assigning SV,
						// init file, etc.)
						ArrayList GUILinks4 = new ArrayList();
						GUILinks4 = GUIUtils.getGUILinks("Config\\GUI_Links4.table");
						table4 = new String[GUILinks4.size()][5];
						for (int i = 0; i < GUILinks4.size(); i++) {
							String tokens[] = ((String) GUILinks4.get(i)).split("\t");
							table4[i][0] = tokens[0] + tokens[1] + tokens[2] + tokens[3];
							table4[i][1] = tokens[4];
							table4[i][2] = tokens[5];
							table4[i][3] = tokens[6];
							table4[i][4] = tokens[7];
						}

						int l = -1;
						for (int i = 0; i < table4.length; i++) {
							if (lookup.equals(table4[i][0]))
								l = i;
						}

						if (l != -1) {

							// Then update GUI values, files in Default\Lookup
							// directory

							((JTextField) swix.find("hyd_DSS_Index")).setText(lookup);
							((JTextField) swix.find("hyd_DSS_SV")).setText(table4[l][1]);
							((JTextField) swix.find("hyd_DSS_SV_F")).setText(table4[l][2]);
							((JTextField) swix.find("hyd_DSS_Init")).setText(table4[l][3]);
							((JTextField) swix.find("hyd_DSS_Init_F")).setText(table4[l][4]);

							// TODO: ? Is this call needed?
							FileUtils.copyWSIDItoLookup(((JTextField) swix.find("hyd_DSS_Index")).getText(),
							        System.getProperty("user.dir") + "\\Default\\Lookup");

							if ((action_WSIDI == 1) && (option == JOptionPane.YES_OPTION)) {

								// Force CVP and SWP tables to be reset from
								// files

								String fileName = "Default\\Lookup\\" + gl.tableNameForCtrl("op_btn1") + ".table";
								int tID = Integer.parseInt(gl.tableIDForCtrl("op_btn1"));
								dTableModels[tID] = new DataFileTableModel(fileName, tID);

								fileName = "Default\\Lookup\\" + gl.tableNameForCtrl("op_btn2") + ".table";
								tID = Integer.parseInt(gl.tableIDForCtrl("op_btn2"));
								dTableModels[tID] = new DataFileTableModel(fileName, tID);

								JTable table = (JTable) swix.find("tblOpValues");
								table.setModel(dTableModels[tID]);

							}
						}
					}
				}
			}
		}

	}

}
