package gov.ca.water.calgui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.swixml.SwingEngine;

public class OpAction implements ActionListener {
	private final SwingEngine swix;
	private final Boolean[] RegUserEdits;
	private DataFileTableModel[] dTableModels;
	private final GUILinks gl;

	public OpAction(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl) {
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if (ae.getActionCommand().startsWith("Op_TableEdit")) {
			TitledBorder title = null;
			JComponent component = (JComponent) ae.getSource();
			if (ae.getSource() instanceof JButton) {
				JButton btn = (JButton) ae.getSource();
				String titlestr = btn.getText();
				titlestr = titlestr.substring(5);
				title = BorderFactory.createTitledBorder(titlestr);
			}
			String cName = component.getName();
			// CheckBox in Regulations panel changed
			JPanel pan = (JPanel) swix.find("op_panTab");
			pan.setBorder(title);
			component = (JComponent) swix.find("scrOpValues");
			JTable table = (JTable) swix.find("tblOpValues");

			component.setVisible(true);
			component.setEnabled(true);
			// String cID = cName.substring(6);
			populateDTable(cName, table, component);

			// pan.setBorder(title);
			// JComponent box = (JComponent) swix.find("Op_Box");
			// box.setVisible(true);
			component.setEnabled(true);
			table.setVisible(true);
			JPanel mainmenu = (JPanel) swix.find("mainmenu");
			mainmenu.revalidate();

		} else if (ae.getActionCommand().startsWith("Op_Copy")) {

			JTable table = (JTable) swix.find("tblOpValues");
			ActionEvent ae1 = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
			// table.selectAll();
			table.getActionMap().get(ae1.getActionCommand()).actionPerformed(ae);

		} else if (ae.getActionCommand().startsWith("Op_Paste")) {

			// System.out.println("Clipboard contains:" +
			// TextTransfer.getClipboardContents() );
			JTable table = (JTable) swix.find("tblOpValues");
			int startRow = (table.getSelectedRows())[0];
			int startCol = (table.getSelectedColumns())[0];
			// int RowCt=table.getSelectedRows().length;
			// int ColCt=table.getSelectedColumns().length;
			try {
				String trstring = (TextTransfer.getClipboardContents());
				trstring = trstring.replaceAll("(?sm)\t\t", "\t \t");
				trstring = trstring.replaceAll("(?sm)\t\n", "\t \n");
				System.out.println("String is:" + trstring);
				StringTokenizer st1 = new StringTokenizer(trstring, "\n");
				for (int i = 0; st1.hasMoreTokens(); i++)
				// for(int i=0; i < RowCt; i++)
				{
					String rowstring = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
					for (int j = 0; st2.hasMoreTokens(); j++)
					// for(int j=0;j < ColCt;j++)
					{
						String value = st2.nextToken();
						if (startRow + i < table.getRowCount() && startCol + j < table.getColumnCount())
							table.setValueAt(value, startRow + i, startCol + j);
						table.repaint();
						System.out.println("Putting " + value + " at row = " + startRow + i + ", column = " + startCol + j);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	protected void populateDTable(String cID, final JTable t, JComponent container) {

		boolean exists = false;
		String fileName = gl.tableNameForCtrl(cID);

		if (fileName != null && fileName.length() != 0) {

			String[] files = fileName.split("[|]");
			int size = files.length;

			if (size == 1) {
				// CASE 1: 1 file specified
				fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + fileName + ".table";
				File f = new File(fileName);
				exists = f.exists();
			} else if (size == 2) {
				// CASE 2: 2 files specified
				fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[0] + ".table";
				File f = new File(fileName);
				exists = f.exists();
				if (exists) {
					fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[1] + ".table";
					f = new File(fileName);
					exists = f.exists();
					fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[0] + ".table" + "|"
					        + System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[1] + ".table";
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

	}

}
