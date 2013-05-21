package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

/**
 * Handles button clicks from Demands dashboard.
 * 
 */

public class DemAction implements ActionListener {

	private final SwingEngine swix;
	private static Logger log = Logger.getLogger(HydAction.class.getName());

	public DemAction(SwingEngine swix) {

		this.swix = swix;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.equals("AC_Help")) {

			try {
				JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
				String label = jtp.getTitleAt(jtp.getSelectedIndex());
				CalLiteHelp calLiteHelp = new CalLiteHelp();
				calLiteHelp.showHelp(label);
			} catch (Exception e) {
				log.debug("could not find help dialog for Demands screen. " + e);
			}

		}

	}
}
