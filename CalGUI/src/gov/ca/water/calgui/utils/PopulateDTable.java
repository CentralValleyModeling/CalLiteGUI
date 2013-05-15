package gov.ca.water.calgui.utils;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.swixml.SwingEngine;

public class PopulateDTable {
	public static DataFileTableModel[] populate(String cID, final JTable t, JComponent container, final SwingEngine swix,
	        final Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, final GUILinks gl, final int[] RegFlags) {

		boolean exists = false;
		String fileName = gl.tableNameForCtrl(cID);

		if (fileName != null && fileName.length() != 0) {

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

		// File f = new File("Default\\Lookup\\" + fileName + ".table");
		// boolean exists = f.exists();
		if (!exists) {
			// t.setVisible(false);
			container.setVisible(false);
		} else {

			// int tID = Integer.parseInt(cID);
			// t.setVisible(true);
			final int tID = Integer.parseInt(gl.tableIDForCtrl(cID));
			if (dTableModels == null) {
				dTableModels = new DataFileTableModel[20];
			}
			if (dTableModels[tID] == null) {
				dTableModels[tID] = new DataFileTableModel(fileName, tID);
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
					JButton btn = (JButton) swix.find("btnRegDef");
					btn.setEnabled(true);

					String stID = String.valueOf(tID);
					String comp = gl.tableIDForCtrl(stID);
					int rID = Integer.parseInt(gl.RIDForCtrl(comp));
					RegFlags[rID] = 2;

				}
			});

			t.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent ke) {

					int l = t.getSelectedRow();
					int c = t.getSelectedColumn();

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

			t.revalidate();

			ExcelAdapter myAd = new ExcelAdapter(t);

		}
		return dTableModels;
	}
}
