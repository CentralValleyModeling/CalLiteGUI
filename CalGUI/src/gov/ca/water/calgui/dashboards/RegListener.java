package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

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

			if (cName.startsWith("rdbRegQS")) {
				// Quick Select Radio Button Selected
				Boolean isSelect = ie.getStateChange() == ItemEvent.SELECTED;
				if (isSelect) {

					int iSel;

					// Find which option is selected.
					if (cName.startsWith("rdbRegQS_D1641")) {
						iSel = 1;
					} else if (cName.startsWith("rdbRegQS_1641BO")) {
						iSel = 2;
					} else if (cName.startsWith("rdbRegQS_D1485")) {
						iSel = 3;
					} else {
						iSel = 0;
					}

					if (iSel > 0) {
						// D1485 or D1641 options
						JPanel pan = (JPanel) swix.find("regpan1");
						GUIUtils.toggleEnComponentAndChildren(pan, false);
						pan = (JPanel) swix.find("regpan2");
						GUIUtils.toggleEnComponentAndChildren(pan, false);
						pan = (JPanel) swix.find("regpan3");
						GUIUtils.toggleEnComponentAndChildren(pan, false);

						Scanner input = null;
						try {
							input = new Scanner(new FileReader("Config\\GUI_LinksReg.table"));
						} catch (FileNotFoundException e) {
							System.out.println("Cannot open input file Config\\GUI_LinksReg.table");
						}

						Vector<String> allLookups = new Vector<String>();

						int lineCount = 0;
						input.nextLine(); // Skip header line
						while (input.hasNextLine()) {
							String line = input.nextLine();
							allLookups.add(line);
							lineCount++;
						}
						input.close();
						String lookups[][];
						lookups = new String[lineCount][5];
						for (int i = 0; i < lineCount; i++) {
							String[] parts = allLookups.get(i).split("[\t]+");
							for (int j = 0; j < 5; j++) {
								if (parts[j].equals("null"))
									parts[j] = "";
								lookups[i][j] = parts[j];
							}
						}

						String ckbName;
						Boolean b;
						for (int i = 0; i < lineCount; i++) {
							ckbName = lookups[i][0];
							JCheckBox ckb = (JCheckBox) swix.find(ckbName);

							b = Boolean.valueOf(lookups[i][iSel + 1]);
							ckb.setSelected(b);
						}

						// JTable tbl = (JTable) swix.find("tblRegValues");

					} else {
						// User Defined Options
						JPanel pan = (JPanel) swix.find("regpan1");
						GUIUtils.toggleEnComponentAndChildren(pan, true);
						pan = (JPanel) swix.find("regpan2");
						GUIUtils.toggleEnComponentAndChildren(pan, true);
						pan = (JPanel) swix.find("regpan3");
						GUIUtils.toggleEnComponentAndChildren(pan, true);
						// JTable tbl = (JTable) swix.find("tblRegValues");

					}
				}

			} else if (cName.startsWith("ckbReg")) {
				// CheckBox in Regulations panel changed
				Boolean isSelect = ie.getStateChange() == ItemEvent.SELECTED;

				RegulationSetup.SetRegCheckBoxes(swix, RegUserEdits, dTableModels, gl, reg_btng1, cName, isSelect);

				/*
				 * } else if (cName.startsWith("reg_rdbD1641")) { // do not allow user edits to tables JTable table = (JTable)
				 * swix.find("tblRegValues"); JRadioButton rdb = (JRadioButton) ie.getItem(); if (ie.getStateChange() ==
				 * ItemEvent.SELECTED) { if (dTableModels != null) {
				 * 
				 * DataFileTableModel tm = (DataFileTableModel) table.getModel(); int size = tm.datafiles.length; if (size == 1) {
				 * tm.initVectors(); } else if (size == 2) { tm.initVectors2(); } table.repaint();
				 * 
				 * table.setCellSelectionEnabled(false); table.setEnabled(false); if (table.isEditing()) {
				 * table.getCellEditor().stopCellEditing(); }
				 * 
				 * JComponent scr = (JComponent) swix.find("scrRegValues"); if (scr.isVisible()) { int tID = tm.tID; if
				 * (RegUserEdits == null) { RegUserEdits = new Boolean[20]; } RegUserEdits[tID] = false;
				 * 
				 * String cName1 = gl.CtrlFortableID(Integer.toString(tID)); JCheckBox ckb = (JCheckBox) swix.find(cName1); String
				 * ckbtext = ckb.getText(); String[] ckbtext1 = ckbtext.split(" - "); ckbtext = ckbtext1[0]; ckb.setText(ckbtext +
				 * " -  Default"); } else { // JOptionPane.showMessageDialog(mainmenu, //
				 * "There is currently a simulation running at this time."); } }
				 * 
				 * }
				 */

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
