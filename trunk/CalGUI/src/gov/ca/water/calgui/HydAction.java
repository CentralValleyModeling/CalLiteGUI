package gov.ca.water.calgui;

import java.awt.Component;
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
			Component[] components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}
			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}

		} else if (ae.getActionCommand().startsWith("HYD_All")) {
			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			Component[] components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
		} else if (ae.getActionCommand().startsWith("HYD_B1")) {
			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			Component[] components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}
		} else if (ae.getActionCommand().startsWith("HYD_A2")) {
			JPanel hyd_CC2 = (JPanel) swix.find("hyd_CC2");
			Component[] components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
			JPanel hyd_CC1 = (JPanel) swix.find("hyd_CC1");
			components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}
		}
	}

}
