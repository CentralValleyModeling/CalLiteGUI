package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.FileAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class DemAction implements ActionListener {
	private final SwingEngine swix;
	static Logger log = Logger.getLogger(DemAction.class.getName());

	public DemAction(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getActionCommand().equals("AC_Help")) {

			JHelp helpViewer;
			JFrame help;
			try {
				ClassLoader classLoader = FileAction.class.getClassLoader();
				URL url = HelpSet.findHelpSet(classLoader, "../docs/helpset.hs");
				helpViewer = new JHelp(new HelpSet(classLoader, url));

				help = new JFrame("CalLite 2.0 GUI Help");
				help.getContentPane().add(helpViewer);

				JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
				int selIndex = jtp.getSelectedIndex();
				String label = jtp.getTitleAt(selIndex);
				helpViewer.setCurrentID(label);
				help.pack();
				help.setVisible(true);

			} catch (Exception e) {

				log.debug("helpset not found: " + e.getMessage());

			}
		}
	}
}
