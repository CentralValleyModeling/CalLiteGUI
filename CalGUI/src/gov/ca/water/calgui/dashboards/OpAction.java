package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;
import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.PopulateDTable;
import gov.ca.water.calgui.utils.TextTransfer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class OpAction implements ActionListener {
	private final SwingEngine swix;
	private final Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private static Logger log = Logger.getLogger(OpAction.class.getName());
	private final int[] RegFlags;

	public OpAction(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl, int[] RegFlags) {
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.RegFlags = RegFlags;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getActionCommand().startsWith("Op_Generate")) {
			try {

				Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\wsidi_generator_test.bat");
				int exitVal = proc.waitFor();
				log.debug("Return from batch run " + exitVal);
			} catch (Throwable t) {
				JOptionPane.showMessageDialog(null, t.getMessage(), "Run failure!", JOptionPane.ERROR_MESSAGE);
				log.debug(t.getStackTrace());
			}

		} else if (ae.getActionCommand().startsWith("Op_TableEdit")) {
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
			PopulateDTable.populate(cName, table, component, swix, RegUserEdits, dTableModels, gl, RegFlags);

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
				log.debug(ex.getMessage());
			}
		}

		else {

			if (ae.getActionCommand().equals("AC_Help")) {

				JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
				String label = jtp.getTitleAt(jtp.getSelectedIndex());
				CalLiteHelp calLiteHelp = new CalLiteHelp();
				calLiteHelp.showHelp(label);
			}
		}
	}

}
