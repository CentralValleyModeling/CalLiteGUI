package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;
import gov.ca.water.calgui.results.ControlFrame;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.swixml.SwingEngine;

public class CustomResultsAction implements ActionListener {
	private final SwingEngine SWIX;

	public CustomResultsAction(SwingEngine swix) {
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
		if (actionEvent.getActionCommand().equals("AC_Controls")) {

			ControlFrame cf = GUIUtils.getControlFrame();
			cf.display();
			if (cf.getExtendedState() == JFrame.ICONIFIED)
				cf.setExtendedState(JFrame.NORMAL);

		}

	}

}
