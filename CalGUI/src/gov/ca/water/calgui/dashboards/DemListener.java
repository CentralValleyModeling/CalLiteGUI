package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;
import gov.ca.water.calgui.utils.GUIUtils;
import gov.ca.water.calgui.utils.NumericTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class DemListener implements ItemListener, ActionListener {

	private final SwingEngine swix;
	private static Logger log = Logger.getLogger(DemListener.class.getName());

	public DemListener(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		JComponent component = (JComponent) e.getItem();
		// TODO: EXTERNALIZE

		// was "e.getItemSelected"
		String cName = component.getName();
		if (cName != null) {

			if (cName.startsWith("dem_rdbUD")) {
				// Checkbox in Demands page changed

				if (cName.startsWith("dem_rdbUD1")) {
					JPanel dem_UDSWP = (JPanel) swix.find("dem_UDSWP");
					GUIUtils.toggleEnComponentAndChildren(dem_UDSWP, e.getStateChange() == ItemEvent.SELECTED,
					        NumericTextField.class);
				} else if (cName.startsWith("dem_rdbUD2")) {
					JPanel dem_UDCVP = (JPanel) swix.find("dem_UDCVP");
					GUIUtils.toggleEnComponentAndChildren(dem_UDCVP, e.getStateChange() == ItemEvent.SELECTED,
					        NumericTextField.class);
				}
			}

		}

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
				log.debug("Could not find help dialog for Demands screen. " + e);
			}

		}

	}
}