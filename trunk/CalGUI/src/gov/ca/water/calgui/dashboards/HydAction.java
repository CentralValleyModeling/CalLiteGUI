package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.FileDialog;
import hec.heclib.dss.HecDss;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class HydAction implements ActionListener {
	private final SwingEngine swix;
	static Logger log = Logger.getLogger(HydAction.class.getName());

	public HydAction(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getActionCommand().startsWith("AC_Select_DSS")) {

			// Select a DSS file and assign F-part when manually specifiying SV and INIT files.

			JTextField txfDSSFilename;
			JTextField txfDSSFPart;
			FileDialog fileDialog;

			if (ae.getActionCommand().endsWith("SV")) {
				txfDSSFilename = (JTextField) swix.find("hyd_DSS_SV");
				txfDSSFPart = (JTextField) swix.find("hyd_DSS_SV_F");
			} else {
				txfDSSFilename = (JTextField) swix.find("hyd_DSS_Init");
				txfDSSFPart = (JTextField) swix.find("hyd_DSS_Init_F");
			}

			fileDialog = new FileDialog(null, txfDSSFilename, "DSS2");
			fileDialog.actionPerformed(ae);

			String fPartResult = "NOT FOUND";
			try {

				// Read all pathnames from the DSS file and set the F-PART textfield as
				// "NOT FOUND","MULTIPLE F-PARTS", or the first F-PART found.

				HecDss hD = HecDss.open(txfDSSFilename.getToolTipText());
				Vector<String> pathNames = hD.getCatalogedPathnames();
				String lastFPart = "";
				for (int i = 0; i < pathNames.size(); i++) {
					String[] parts = pathNames.elementAt(0).split("/");
					String newFPart = ((parts.length < 7) || (parts[6] == null)) ? "NOT FOUND" : parts[6];
					if (i == 0) {
						lastFPart = newFPart;
						fPartResult = newFPart;
					} else if (!lastFPart.equals(newFPart) && !newFPart.equals("NOT FOUND")) {
						fPartResult = "MULTIPLE F-PARTS";
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				log.debug(e.getMessage());
			}
			txfDSSFPart.setText(fPartResult);

		}
	}
}
