package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.swixml.SwingEngine;

public class FacListener implements ItemListener {
	private final SwingEngine swix;

	public FacListener(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stu
		JComponent component = (JComponent) e.getItem();
		// TODO: EXTERNALIZE

		// was "e.getItemSelected"
		String cName = component.getName();
		if (cName != null) {

			if (cName.startsWith("fac_ckb")) {
				// checkbox in facilities panel changed
				JPanel panel = (JPanel) swix.find("fac_pan" + cName.substring(7));

				if (panel != null) {
					// set all "data" panels to invisible
					JPanel facilities = (JPanel) swix.find("facilities");
					GUIUtils.toggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUIUtils.toggleVisComponent(panel, true);
					GUIUtils.toggleEnComponentAndChildren(panel, e.getStateChange() == ItemEvent.SELECTED);

					// ((JPanel) swix.find("FacPict")).setVisible(false);

				}
			}
		}

	}
}
