package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.TextTransfer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class RegAction implements ActionListener {
	private final SwingEngine swix;
	private Boolean[] RegUserEdits;
	private Logger log;

	public RegAction(SwingEngine swix, Boolean[] RegUserEdits) {
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if (ae.getActionCommand().startsWith("Reg_Copy")) {

			JTable table = (JTable) swix.find("tblRegValues");
			ActionEvent ae1 = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
			// table.selectAll();
			table.getActionMap().get(ae1.getActionCommand()).actionPerformed(ae);

		} else if (ae.getActionCommand().startsWith("Reg_Paste")) {

			// System.out.println("Clipboard contains:" +
			// TextTransfer.getClipboardContents() );
			JTable table = (JTable) swix.find("tblRegValues");
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
				log.debug(ex.getMessage());
			}

		} else if (ae.getActionCommand().startsWith("Reg_Default")) {

			JTable table = (JTable) swix.find("tblRegValues");
			DataFileTableModel tm = (DataFileTableModel) table.getModel();
			int size = tm.datafiles.length;
			if (size == 1) {
				tm.initVectors();
			} else if (size == 2) {
				tm.initVectors2();
			}
			table.repaint();
			int tID = tm.tID;
			if (RegUserEdits == null) {
				RegUserEdits = new Boolean[20];
			}
			RegUserEdits[tID] = false;

			JButton btn = (JButton) swix.find("btnRegDef");
			btn.setEnabled(false);
		}
	}
}