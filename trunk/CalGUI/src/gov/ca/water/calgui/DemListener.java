package gov.ca.water.calgui;

import gov.ca.water.calgui.GUIUtils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.swixml.SwingEngine;

public class DemListener implements ItemListener {
	private final SwingEngine swix;

	public DemListener(SwingEngine swix) {
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
}