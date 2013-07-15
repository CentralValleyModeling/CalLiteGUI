package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.swixml.SwingEngine;

public class ReportListener implements ItemListener {
	private final SwingEngine swix;

	public ReportListener(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		JComponent component = (JComponent) e.getItem();
		String cName = component.getName();
		if (cName != null) {

			if (cName.startsWith("Repckb")) {
				// Checkbox in Reporting page changed
				if (cName.startsWith("RepckbExceedancePlot") || cName.startsWith("RepckbBAWPlot")) {
					// Month controls should be turned on if *either* exceedance or B&W plots are asked for;

					JPanel controls2 = (JPanel) swix.find("controls2");
					GUIUtils.toggleEnComponentAndChildren(controls2,
					        (e.getStateChange() == ItemEvent.SELECTED) || ((JCheckBox) swix.find("RepckbBAWPlot")).isSelected()
					                || ((JCheckBox) swix.find("RepckbExceedancePlot")).isSelected());

				} else if (cName.startsWith("RepckbSummaryTable")) {
					JPanel controls3 = (JPanel) swix.find("controls3");
					GUIUtils.toggleEnComponentAndChildren(controls3, e.getStateChange() == ItemEvent.SELECTED);
				}
			}
		}

	}
}