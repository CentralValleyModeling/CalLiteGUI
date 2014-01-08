package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.FileUtils;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

public class HydListener implements ItemListener {
	private final JFrame desktop;
	private final SwingEngine swix;
	private final Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private int action_WSIDI;

	public HydListener(JFrame desktop, SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
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

			if (cName.startsWith("hyd_rdb20")) {

				// Handling for update of DSS files

				// Confirm CWP/SWP overwrite;

				if (((JRadioButton) component).isSelected() && action_WSIDI != 2) {

					// Actions are only performed if selection is being set to true

					GUIUtils.toggleEnComponentAndChildren(swix.find("hyd_CC"), false);

					int option = JOptionPane.YES_OPTION; // default is to overwrite everything
					boolean isntDefault = false;
					if (RegUserEdits != null) {
						if (RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn1"))] != null)
							isntDefault = isntDefault || RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn1"))];
						if (RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn2"))] != null)
							isntDefault = isntDefault || RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn2"))];

					}
					if ((action_WSIDI == 1) && (isntDefault))

						// Prompt is needed only in "regular" processing (action_WSIDI = 1) with non-default table

						option = JOptionPane
						        .showConfirmDialog(
						                desktop,
						                "You have previously made changes to the SWP and/or CVP WSI/DI curves. \n"
						                        + "Do you want to overwrite these changes with the default values for the configuration ("
						                        + ((JRadioButton) component).getText()
						                        + " ) you have selected?\n\n"
						                        + "Press YES to overwrite, NO to use these values in the selected configuration, or Cancel to revert");

					// Once option is determined, process ...

					if (option == JOptionPane.CANCEL_OPTION) {

						// Cancel: undo change by selecting "other" radio button
						// TODO: ISSUE 116 - this logic won't quite be right.

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

						updateHydrology(option);
					}
				}
			} else if (cName.startsWith("hyd_rdbCC")) {

				if (((JRadioButton) component).isSelected()) {
					GUIUtils.toggleEnComponentAndChildren(swix.find("hyd_CC"), true);
					updateHydrology(JOptionPane.NO_OPTION);

				}

			} else if (cName.startsWith("hyd_ckb")) {

				// Checkbox in Climate Scenarios page changed

				int selct = 0;
				JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC");
				selct = GUIUtils.countSelectedButtons(hyd_CC1, JCheckBox.class, selct);

				JLabel lab = (JLabel) swix.find("hydlab_selected");
				if (selct == 0) {
					lab.setText("0 realizations selected");
				} else if (selct == 1) {
					lab.setText("1 realization selected - Deterministic mode required");
				} else {
					lab.setText(selct + " realizations selected - Probabilistic mode required");
				}

				updateHydrology(JOptionPane.NO_OPTION);

			} else if (cName.equals("btnDSS_Manual")) {

				boolean b = ((JRadioButton) swix.find("btnDSS_Manual")).isSelected();

				swix.find("hyd_DSS_SV_t").setEnabled(b);
				swix.find("hyd_DSS_SV_F_t").setEnabled(b);
				swix.find("hyd_DSS_Select").setEnabled(b);

				swix.find("hyd_DSS_Init_t").setEnabled(b);
				swix.find("hyd_DSS_Init_F_t").setEnabled(b);
				swix.find("hyd_DSS_Init_Select").setEnabled(b);

				if (b) {

					// When moving to manual hydrology, retrieve previous values, if any

					if (((JTextField) swix.find("txf_Manual_SV")).getText().length() > 0) {

						((JTextField) swix.find("hyd_DSS_SV")).setText(((JTextField) swix.find("txf_Manual_SV")).getText());
						((JTextField) swix.find("hyd_DSS_SV_F")).setText(((JTextField) swix.find("txf_Manual_SV_F")).getText());
						((JTextField) swix.find("hyd_DSS_Init")).setText(((JTextField) swix.find("txf_Manual_Init")).getText());
						((JTextField) swix.find("hyd_DSS_Init_F")).setText(((JTextField) swix.find("txf_Manual_Init_F")).getText());
					}
				} else {

					// / When moving to default, store manual choices

					((JTextField) swix.find("txf_Manual_SV")).setText(((JTextField) swix.find("hyd_DSS_SV")).getText());
					((JTextField) swix.find("txf_Manual_SV_F")).setText(((JTextField) swix.find("hyd_DSS_SV_F")).getText());
					((JTextField) swix.find("txf_Manual_Init")).setText(((JTextField) swix.find("hyd_DSS_Init")).getText());
					((JTextField) swix.find("txf_Manual_Init_F")).setText(((JTextField) swix.find("hyd_DSS_Init_F")).getText());

					// Then reset according to run type, LOD and CC
					updateHydrology(JOptionPane.NO_OPTION);
				}

			} else
				System.out.println(cName + "! HydListener unhandled ItemState change");

		}

	}

	/**
	 * Updates hydrology inputs and CVP/SWP files according to hydrology lookup settings.
	 * 
	 * @param option
	 *            JOPtionPane.YES_Option - force CVP and SWP tables to be reset from defaults for hydrology
	 */
	private void updateHydrology(int option) {

		// Yes or no: first determine which GUI_link4.table row to use

		String hydDSSStrings[] = GUIUtils.getHydDSSStrings(swix);

		if (hydDSSStrings[1] != null) {

			// Then update GUI values, files in Default\Lookup\directory

			((JTextField) swix.find("hyd_DSS_SV")).setText(hydDSSStrings[1]);
			((JTextField) swix.find("hyd_DSS_SV_F")).setText(hydDSSStrings[2]);
			((JTextField) swix.find("hyd_DSS_Init")).setText(hydDSSStrings[3]);
			((JTextField) swix.find("hyd_DSS_Init_F")).setText(hydDSSStrings[4]);

			// JTextField tf = (JTextField) swix.find("hyd_DSS_Index");
			// tf.setText(hydDSSStrings[0]);
			// ((JTextField) swix.find("hyd_DSS_Index")).setText(hydDSSStrings[0]);

			if ((action_WSIDI == 1) && (option == JOptionPane.YES_OPTION)) {

				// Force CVP and SWP tables to be reset from files

				FileUtils.copyWSIDItoLookup(hydDSSStrings[7], GUIUtils.defaultLookupDirectoryString()); // TODO: CONFIRM THIS IS NOW
				                                                                                        // SUPERFLUOUS

				String fileName = GUIUtils.defaultLookupDirectoryString() + "\\WSIDI\\wsi_di_swp_" + hydDSSStrings[7] + ".table";
				int tID = Integer.parseInt(gl.tableIDForCtrl("op_btn1"));
				dTableModels[tID] = new DataFileTableModel(fileName, tID);

				fileName = GUIUtils.defaultLookupDirectoryString() + "\\WSIDI\\wsi_di_cvp_sys_" + hydDSSStrings[7] + ".table";
				tID = Integer.parseInt(gl.tableIDForCtrl("op_btn2"));
				dTableModels[tID] = new DataFileTableModel(fileName, tID);

				JTable table = (JTable) swix.find("tblOpValues");
				table.setModel(dTableModels[tID]);

			}
		}
	}

}
