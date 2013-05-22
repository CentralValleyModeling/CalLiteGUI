package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import org.swixml.SwingEngine;

public class SchematicAction implements ActionListener {
	private final SwingEngine SWIX;

	public SchematicAction(SwingEngine swix) {
		this.SWIX = swix;

	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getActionCommand().equals("AC_Help")) {

			JTabbedPane jtp = (JTabbedPane) SWIX.find("tabbedPane1");
			String label = jtp.getTitleAt(jtp.getSelectedIndex());
			CalLiteHelp calLiteHelp = new CalLiteHelp();
			calLiteHelp.showHelp(label);

		}

	}

}
