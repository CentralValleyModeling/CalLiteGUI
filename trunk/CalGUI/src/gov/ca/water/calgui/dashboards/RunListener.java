package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.FileUtils;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
						if (cName.equals("run_rdbD1641"))
							newcName = "run_rdbBO";
						else if (cName.equals("run_rdbBO"))
							newcName = "run_rdbD1641";

						action_WSIDI = 2; // Skip all actions on update

						// System.out.println(cName + ":-" + newcName);
						((JRadioButton) swix.find(newcName)).setSelected(true);

						action_WSIDI = 1;

					} else {

						// TODO: REPLACE WITH CALL TO updateHydrology?

						// Yes or no: first determine which GUI_link4.table row
						// to use

						String result[] = GUIUtils.getHydDSSStrings(swix);

						if (!(result[1] == null)) {

							// Then update GUI values, files in Default\Lookup directory

							((JTextField) swix.find("hyd_DSS_Index")).setText(result[0]);
							((JTextField) swix.find("hyd_DSS_SV")).setText(result[1]);
							((JTextField) swix.find("hyd_DSS_SV_F")).setText(result[2]);
							((JTextField) swix.find("hyd_DSS_Init")).setText(result[3]);
							((JTextField) swix.find("hyd_DSS_Init_F")).setText(result[4]);

							// TODO: ? Is this call needed?
							FileUtils.copyWSIDItoLookup(result[7], GUIUtils.defaultLookupDirectoryString());

							if ((action_WSIDI == 1) && (option == JOptionPane.YES_OPTION)) {

								// Force CVP and SWP tables to be reset from files

								String fileName = GUIUtils.defaultLookupDirectoryString() + "\\WSIDI\\wsi_di_swp_" + result[7]
								        + ".table";
								int tID = Integer.parseInt(gl.tableIDForCtrl("op_btn1"));
								dTableModels[tID] = new DataFileTableModel(fileName, tID);

								fileName = GUIUtils.defaultLookupDirectoryString() + "\\WSIDI\\wsi_di_cvp_sys_" + result[7]
								        + ".table";
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
