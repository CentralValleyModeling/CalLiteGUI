package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.GetDSSFilename;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import org.swixml.SwingEngine;

public class HydAction implements ActionListener {
	private final SwingEngine swix;

	public HydAction(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getActionCommand().startsWith("AC_Select_DSS")) {

			// Select a DSS file and assign F-part when manually specifiying SV and INIT files.

			JTextField txfDSSFilename;
			JTextField txfDSSFPart;
			GetDSSFilename getDSSFilename;

			if (ae.getActionCommand().endsWith("SV")) {
				txfDSSFilename = (JTextField) swix.find("hyd_DSS_SV");
				txfDSSFPart = (JTextField) swix.find("hyd_DSS_SV_F");
			} else {
				txfDSSFilename = (JTextField) swix.find("hyd_DSS_Init");
				txfDSSFPart = (JTextField) swix.find("hyd_DSS_Init_F");
			}
			getDSSFilename = new GetDSSFilename(null, txfDSSFilename, "DSS2");
			getDSSFilename.actionPerformed(ae);

			// TODO: Take the DSS file and extract F-part, then place in txfDSSFPart
			// OK for now to assume only one F-Part, could extend to allow user to select

		}
	}
}
