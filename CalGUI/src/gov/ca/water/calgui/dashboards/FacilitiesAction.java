package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;
import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.PopulateDTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.swixml.SwingEngine;

public class FacilitiesAction implements ActionListener {

	private final SwingEngine swix;
	private final Boolean[] RegUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private final int[] RegFlags;

	public FacilitiesAction(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl, int[] RegFlags) {
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.RegFlags = RegFlags;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getActionCommand().equals("AC_Help")) {

			JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
			String label = jtp.getTitleAt(jtp.getSelectedIndex());
			CalLiteHelp calLiteHelp = new CalLiteHelp();
			calLiteHelp.showHelp(label);

		} else if (actionEvent.getActionCommand().startsWith("Fac_TableEdit")) {
			TitledBorder title = null;
			JComponent component = (JComponent) actionEvent.getSource();
			if (actionEvent.getSource() instanceof JButton) {
				JButton btn = (JButton) actionEvent.getSource();
				String titlestr = btn.getText();
				titlestr = titlestr.substring(5);
				title = BorderFactory.createTitledBorder(titlestr);
			}
			String cName = component.getName();
			JPanel pan = (JPanel) swix.find("fac_pan6");
			pan.setBorder(title);
			component = (JComponent) swix.find("fac_scr1");
			JTable table = (JTable) swix.find("tblIF1");

			component.setVisible(true);
			component.setEnabled(true);
			// String cID = cName.substring(6);
			PopulateDTable.populate(cName, table, component, swix, RegUserEdits, dTableModels, gl, RegFlags);

			// pan.setBorder(title);
			// JComponent box = (JComponent) swix.find("Op_Box");
			// box.setVisible(true);
			component.setEnabled(true);
			table.setVisible(true);
			// JPanel mainmenu = (JPanel) swix.find("mainmenu");
			// mainmenu.revalidate();
		}

	}

}
