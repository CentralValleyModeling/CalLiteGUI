package gov.ca.water.calgui;

import gov.ca.water.calgui.GUIUtils.GUIUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.swixml.SwingEngine;

public class HydAction implements ActionListener {
	private final SwingEngine swix;

	public HydAction(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if (ae.getActionCommand().startsWith("HYD_Clear")) {
			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			/*
			 * Component[] components = hyd_CC1.getComponents(); for (int i = 0; i < components.length; i++) { if (components[i]
			 * instanceof JCheckBox) { JCheckBox c = (JCheckBox) components[i]; c.setSelected(false); } }
			 */
			GUIUtils.toggleSelComponentAndChildren(hyd_CC1, false, JCheckBox.class);

			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC2, false, JCheckBox.class);

		} else if (ae.getActionCommand().startsWith("HYD_All")) {
			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC1, true, JCheckBox.class);

			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC2, true, JCheckBox.class);

		} else if (ae.getActionCommand().startsWith("HYD_B1")) {
			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC1, false, JCheckBox.class);

			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC2, false, JCheckBox.class);

		} else if (ae.getActionCommand().startsWith("HYD_A2")) {
			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC2, true, JCheckBox.class);

			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			GUIUtils.toggleSelComponentAndChildren(hyd_CC1, false, JCheckBox.class);
		}
	}

}
