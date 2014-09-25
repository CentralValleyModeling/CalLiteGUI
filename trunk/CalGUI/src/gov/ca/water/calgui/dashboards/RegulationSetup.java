package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.ExcelAdapter;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.swixml.SwingEngine;

public class RegulationSetup {
	public static void SetRegCheckBoxes(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        ButtonGroup reg_btng1, String cName, Boolean isSelect, String sReset, int[] RegFlags) {

		JPanel pan = (JPanel) swix.find("reg_panTab");
		TitledBorder title;
		JComponent scr = (JComponent) swix.find("scrRegValues");
		JTable table = (JTable) swix.find("tblRegValues");
		if (scr != null)
			scr.setEnabled((isSelect));

		JCheckBox selcomp = (JCheckBox) swix.find(cName);

		// Togle radio buttons above tables
		String D1641 = gl.D1641ForCtrl(cName);
		String D1485 = gl.D1485ForCtrl(cName);
		String UD = gl.UDForCtrl(cName);
		boolean b1;
		boolean b2;
		boolean b3;
		if (D1641.equals("1")) {
			b1 = false;
		} else {
			b1 = true;
		}
		if (D1485.equals("1")) {
			b2 = false;
		} else {
			b2 = true;
		}

		// Special Handling for Vernalis.
		if (cName.equals("ckbReg_VAMP")) {
			b2 = true;
			String sLab = "If D1485 is selected, take VAMP D1641 hydrology with a D1485 run.";
			JLabel lab = (JLabel) swix.find("labReg");
			lab.setText(sLab);
		} else {
			String sLab = "Access regulation table by selecting or right-clicking on item at left";
			JLabel lab = (JLabel) swix.find("labReg");
			lab.setText(sLab);
		}

		if (UD.equals("1")) {
			b3 = false;
		} else {
			b3 = true;
		}

		((JRadioButton) swix.find("btnReg1641")).setVisible(b1);
		((JRadioButton) swix.find("btnReg1485")).setVisible(b2);
		((JRadioButton) swix.find("btnRegUD")).setVisible(b3);

		// Select which button based on RegFlag
		int rID = Integer.parseInt(gl.RIDForCtrl(cName));
		int iFlag = RegFlags[rID];
		if (iFlag == 1) {
			((JRadioButton) swix.find("btnReg1641")).setSelected(true);
		} else if (iFlag == 3) {
			((JRadioButton) swix.find("btnReg1485")).setSelected(true);
		} else {
			((JRadioButton) swix.find("btnRegUD")).setSelected(true);
		}

		if (isSelect) {

			JRadioButton btn = (JRadioButton) swix.find("rdbRegQS_UD");
			Boolean b = btn.isSelected();
			if (b == true) {
				GUIUtils.toggleEnComponentAndChildren(pan, true);
				// GUIUtils.toggleEnComponentAndChildren(swix.find("regpan1"), true);
				// GUIUtils.toggleEnComponentAndChildren(swix.find("regpan2"), true);
				// GUIUtils.toggleEnComponentAndChildren(swix.find("regpan3"), true);
			} else {
				GUIUtils.toggleEnComponentAndChildren(pan, false);
				// GUIUtils.toggleEnComponentAndChildren(swix.find("regpan1"), false);
				// GUIUtils.toggleEnComponentAndChildren(swix.find("regpan2"), false);
				// GUIUtils.toggleEnComponentAndChildren(swix.find("regpan3"), false);
			}

			// GUIUtils.toggleEnComponentAndChildren(pan, true);
			scr.setVisible(true);
			scr.setEnabled(true);
			String cID = cName;
			dTableModels = populateRegDTable(cID, table, scr, swix, RegUserEdits, dTableModels, gl, sReset, RegFlags);

			// JButton btn = (JButton) swix.find("btnRegDef");
			// btn.setEnabled(false);

			if (scr.isVisible()) {
				/*
				 * JRadioButton rdb = (JRadioButton) swix.find("reg_rdbD1641"); if (rdb.isVisible()) { if (RegUserEdits != null &&
				 * dTableModels != null) { DataFileTableModel tm = (DataFileTableModel) table.getModel(); int tID = tm.tID; if
				 * (RegUserEdits[tID] != null) { reg_btng1.clearSelection(); if (RegUserEdits[tID] == true) { rdb = (JRadioButton)
				 * swix.find("reg_rdbUD"); rdb.setSelected(true); } else { rdb = (JRadioButton) swix.find("reg_rdbD1641");
				 * rdb.setSelected(true); } } else { reg_btng1.clearSelection(); rdb = (JRadioButton) swix.find("reg_rdbD1641");
				 * rdb.setSelected(true); } } else { reg_btng1.clearSelection(); rdb = (JRadioButton) swix.find("reg_rdbD1641");
				 * rdb.setSelected(true); } } else {
				 */
				if (RegUserEdits == null) {
					RegUserEdits = new Boolean[20];
				}
				String fileName = gl.tableNameForCtrl(cID);
				if (!fileName.trim().equals("")) {
					int tID = Integer.parseInt(gl.tableIDForCtrl(cID));
					RegUserEdits[tID] = true;
				}

				table.setCellSelectionEnabled(true);
				table.setEnabled(true);
				// }
			}

			String ckbtext = selcomp.getText();
			String[] ckbtext1 = ckbtext.split(" - ");
			ckbtext = ckbtext1[0];
			title = BorderFactory.createTitledBorder(ckbtext);
			String sNote = cName;

			pan.setBorder(title);
			pan.setToolTipText(sNote);
			pan.setEnabled(true);
			scr.setEnabled(true);
			table.setVisible(true);
			pan.revalidate();

		} else {

			GUIUtils.toggleEnComponentAndChildren(pan, false);

			JCheckBox ckb = (JCheckBox) swix.find(cName);
			String ckbtext = ckb.getText();
			String[] ckbtext1 = ckbtext.split(" - ");
			ckbtext = ckbtext1[0];
			ckb.setText(ckbtext);

			pan.setEnabled(false);
			title = BorderFactory.createTitledBorder(selcomp.getText() + " (not selected)");
			pan.setBorder(title);
			scr.setEnabled(false);
			table.setVisible(false);
			pan.revalidate();

		}

	}

	protected static DataFileTableModel[] populateRegDTable(String cID, final JTable t, JComponent container,
	        final SwingEngine swix, final Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, final GUILinks gl,
	        String sReset, final int[] RegFlags) {

		boolean exists = false;
		String fileName = gl.tableNameForCtrl(cID);
		String iDReg = gl.dRegForCtrl(cID);

		if (fileName != null && fileName.length() != 0) {

			// Revert to base file if different values for D1641 and D1485 exist
			if (!iDReg.equals("n/a")) {
				fileName = fileName.replace("gui_", "");
			}

			String[] files = fileName.split("[|]");
			int size = files.length;

			if (size == 1) {
				// CASE 1: 1 file specified
				fileName = GUIUtils.defaultLookupDirectoryString() + "\\" + fileName + ".table";
				File f = new File(fileName);
				exists = f.exists();
			} else if (size == 2) {
				// CASE 2: 2 files specified
				fileName = GUIUtils.defaultLookupDirectoryString() + "\\" + files[0] + ".table";
				File f = new File(fileName);
				exists = f.exists();
				if (exists) {
					fileName = GUIUtils.defaultLookupDirectoryString() + "\\" + files[1] + ".table";
					f = new File(fileName);
					exists = f.exists();
					fileName = GUIUtils.defaultLookupDirectoryString() + "\\" + files[0] + ".table" + "|"
					        + GUIUtils.defaultLookupDirectoryString() + "\\" + files[1] + ".table";
				}
			}

		}

		// Special Handling for Delta Outflows.
		JRadioButton rdb = (JRadioButton) swix.find("btnReg1485");
		boolean isSel = rdb.isSelected();
		if (cID.equals("ckbReg_NDO") && isSel == true) {
			exists = false;
		}

		// File f = new File("Default\\Lookup\\" + fileName + ".table");
		// boolean exists = f.exists();
		if (!exists) {
			t.setVisible(false);
			container.setVisible(false);
			// ((JRadioButton) swix.find("btnRegUD")).setVisible(false);
		} else {

			// JRadioButton btn1 = (JRadioButton) swix.find("rdbRegQS_UD");
			// boolean enabled = btn1.isEnabled();

			/*
			 * ((JRadioButton) swix.find("btnReg1641")).setEnabled(enabled); ((JRadioButton)
			 * swix.find("btnReg1485")).setEnabled(enabled); ((JRadioButton) swix.find("btnRegUD")).setEnabled(enabled);
			 * ((JRadioButton) swix.find("btnReg1641")).setVisible(enabled); ((JRadioButton)
			 * swix.find("btnReg1485")).setVisible(enabled); ((JRadioButton) swix.find("btnRegUD")).setVisible(enabled);
			 */

			// int tID = Integer.parseInt(cID);
			// t.setVisible(true);
			JRadioButton rdbUD = (JRadioButton) swix.find("rdbRegQS_UD");
			boolean editable = rdbUD.isSelected();
			rdbUD = (JRadioButton) swix.find("btnRegUD");
			if (editable == true) {
				editable = rdbUD.isSelected();
			}

			final int tID = Integer.parseInt(gl.tableIDForCtrl(cID));
			if (dTableModels == null) {
				dTableModels = new DataFileTableModel[20];
			}

			if (iDReg.equals("n/a")) {
				// if (dTableModels[tID] == null) {
				dTableModels[tID] = new DataFileTableModel(fileName, tID, editable);
				// }
			} else {
				int iOpt;
				// String sRegPlan;

				if (sReset.equals("null")) {

					// DKR 16Sept2014 comment out block
					/*
					 * JRadioButton btn = (JRadioButton) swix.find("rdbRegQS_D1485"); Boolean b = btn.isSelected(); if (b == true) {
					 * iOpt = 2; } else { iOpt = 1; }
					 */
					// DKR 16Sept2014 revert display table back to previous quick select
					if (RegFlags[tID] == 2) {
						// iOpt = 0;
						iOpt = dTableModels[tID].iPrevOpt;
					} else if (RegFlags[tID] == 3) {
						iOpt = 2;
					} else {
						iOpt = 1;
					}

				} else if (sReset.equals("1641")) {
					iOpt = 1;
				} else {
					iOpt = 2;
				}

				// DKR 10Set2014 - commented out if criterion to make table cahnge values
				// if (dTableModels[tID] == null) {
				if (iOpt == 0) {
					t.setModel(dTableModels[tID]);
				} else {
					dTableModels[tID] = new DataFileTableModel(fileName, tID, iOpt, editable);
				}
				// }
			}

			// dTableModels[tID].addTableModelListener(this);

			t.setModel(dTableModels[tID]);
			t.createDefaultColumnsFromModel();

			t.setRowHeight(20);
			for (int col = 0; col < t.getColumnCount(); col++) {
				t.getColumnModel().getColumn(col).setWidth(50);
			}

			t.setPreferredScrollableViewportSize(new Dimension(t.getColumnCount() * 60 + 60, t.getRowCount() * 20));

			t.setCellSelectionEnabled(true);
			t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

			t.getModel().addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {

					/*
					 * if (RegUserEdits == null) { RegUserEdits = new Boolean[20]; }
					 * 
					 * RegUserEdits[tID] = true;
					 */
					RegUserEdits[tID] = true;

					String stID = String.valueOf(tID);
					String comp = gl.ctrlFortableID(stID);
					int rID = Integer.parseInt(gl.RIDForCtrl(comp));
					RegFlags[rID] = 2;
					// JButton btn = (JButton) swix.find("btnRegDef");
					// btn.setEnabled(true);

				}
			});

			t.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent ke) {

					// int l = t.getSelectedRow();
					// int c = t.getSelectedColumn();

					// t.setValueAt(Character.toString(ke.getKeyChar()),l,c);
					// t.editCellAt(l, c);
				}
			});

			// Special handling for tiered header tables (11 columns)
			if (dTableModels[tID].getColumnCount() == 11) {

				// JFrame frame1 = new JFrame();

				/*
				 * JTable table = new JTable(); table.setColumnModel(new GroupableTableColumnModel()); table.setTableHeader(new
				 * GroupableTableHeader((GroupableTableColumnModel) table.getColumnModel())); table.setModel(dTableModels[tID]);
				 * table.createDefaultColumnsFromModel();
				 * 
				 * 
				 * GroupableTableColumnModel cm = (GroupableTableColumnModel)table.getColumnModel();
				 * 
				 * ColumnGroup g_Wet = new ColumnGroup("Wet"); g_Wet.add(cm.getColumn(1)); g_Wet.add(cm.getColumn(2)); ColumnGroup
				 * g_AN = new ColumnGroup("Above Normal"); g_AN.add(cm.getColumn(3)); g_AN.add(cm.getColumn(4)); ColumnGroup g_BN =
				 * new ColumnGroup("Below Normal"); g_BN.add(cm.getColumn(5)); g_BN.add(cm.getColumn(6)); ColumnGroup g_DRY = new
				 * ColumnGroup("Dry"); g_DRY.add(cm.getColumn(7)); g_DRY.add(cm.getColumn(8)); ColumnGroup g_CD = new
				 * ColumnGroup("Critical Dry"); g_CD.add(cm.getColumn(9)); g_CD.add(cm.getColumn(10));
				 * 
				 * 
				 * GroupableTableHeader h = (GroupableTableHeader)table.getTableHeader(); h.addColumnGroup(g_Wet);
				 * h.addColumnGroup(g_AN); h.addColumnGroup(g_BN); h.addColumnGroup(g_DRY); h.addColumnGroup(g_CD); JScrollPane
				 * scroll = new JScrollPane( table );
				 * 
				 * //frame1.add(scroll); //frame1.pack(); //frame1.setVisible(true);
				 */

			}
			ExcelAdapter myAd = new ExcelAdapter(t);

			t.revalidate();

		}
		return dTableModels;

	}

}
