package gov.ca.water.calgui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.swixml.SwingEngine;

public class RegListener implements ItemListener {
	private final SwingEngine swix;
	private JComponent component;
	private Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private final ButtonGroup reg_btng1;

	public RegListener(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        ButtonGroup reg_btng1) {
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.reg_btng1 = reg_btng1;

	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		// TODO Auto-generated method stub
		JComponent component = (JComponent) ie.getItem();
		// TODO: EXTERNALIZE

		// was "e.getItemSelected"
		String cName = component.getName();
		if (cName != null) {

			if (cName.startsWith("ckbReg")) {
				// CheckBox in Regulations panel changed
				Boolean isSelect = ie.getStateChange() == ItemEvent.SELECTED;

				RegulationSetup.SetRegCheckBoxes(swix, RegUserEdits, dTableModels, gl, reg_btng1, cName, isSelect);

			} else if (cName.startsWith("reg_rdbD1641")) {
				// do not allow user edits to tables
				JTable table = (JTable) swix.find("tblRegValues");
				JRadioButton rdb = (JRadioButton) ie.getItem();
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					if (dTableModels != null) {

						DataFileTableModel tm = (DataFileTableModel) table.getModel();
						int size = tm.datafiles.length;
						if (size == 1) {
							tm.initVectors();
						} else if (size == 2) {
							tm.initVectors2();
						}
						table.repaint();

						table.setCellSelectionEnabled(false);
						table.setEnabled(false);
						if (table.isEditing()) {
							table.getCellEditor().stopCellEditing();
						}

						JComponent scr = (JComponent) swix.find("scrRegValues");
						if (scr.isVisible()) {
							int tID = tm.tID;
							if (RegUserEdits == null) {
								RegUserEdits = new Boolean[20];
							}
							RegUserEdits[tID] = false;

							String cName1 = gl.CtrlFortableID(Integer.toString(tID));
							JCheckBox ckb = (JCheckBox) swix.find(cName1);
							String ckbtext = ckb.getText();
							String[] ckbtext1 = ckbtext.split(" - ");
							ckbtext = ckbtext1[0];
							ckb.setText(ckbtext + " -  Default");
						} else {
							// JOptionPane.showMessageDialog(mainmenu,
							// "There is currently a simulation running at this time.");
						}
					}

				}

			} else if (cName.startsWith("reg_rdbUD")) {
				// do not allow user edits to tables
				JTable table = (JTable) swix.find("tblRegValues");
				JRadioButton rdb = (JRadioButton) ie.getItem();

				if (ie.getStateChange() == ItemEvent.SELECTED) {
					JComponent scr = (JComponent) swix.find("scrRegValues");
					if (scr.isVisible()) {
						table.setCellSelectionEnabled(true);
						table.setEnabled(true);

						DataFileTableModel tm = (DataFileTableModel) table.getModel();
						int tID = tm.tID;
						if (RegUserEdits == null) {
							RegUserEdits = new Boolean[20];
						}
						RegUserEdits[tID] = true;

						String cName1 = gl.CtrlFortableID(Integer.toString(tID));
						JCheckBox ckb = (JCheckBox) swix.find(cName1);
						String ckbtext = ckb.getText();
						String[] ckbtext1 = ckbtext.split(" - ");
						ckbtext = ckbtext1[0];
						ckb.setText(ckbtext + " - User Def.");
					} else {
						JPanel pan = (JPanel) swix.find("reg_panTab");
						TitledBorder b = (TitledBorder) pan.getBorder();
						String title = b.getTitle();
						JPanel mainmenu = (JPanel) swix.find("mainmenu");
						JOptionPane.showMessageDialog(mainmenu, title + " inputs are not user-specifiable at this time.");
						JRadioButton rdb1 = (JRadioButton) swix.find("reg_rdbD1641");
						rdb1.setSelected(true);
						rdb1.revalidate();

					}
				}
			}
		}
	}

}
