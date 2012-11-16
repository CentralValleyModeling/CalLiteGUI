package gov.ca.water.calgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.swixml.SwingEngine;

public class OpAction implements ActionListener {
	private final SwingEngine swix;
	private final Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
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
			PopulateDTable.populate(cName, table, component, swix, RegUserEdits, dTableModels, gl);

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

}
