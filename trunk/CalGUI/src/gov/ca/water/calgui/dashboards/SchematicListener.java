package gov.ca.water.calgui.dashboards;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import org.swixml.SwingEngine;

/**
 * Listener for Schematic View
 * 
 * @author tslawecki
 * 
 */
public class SchematicListener implements ItemListener {
	private final SwingEngine swix;

	public SchematicListener(SwingEngine swix) {
		this.swix = swix;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		JComponent component = (JComponent) e.getItem();
		String cName = component.getName();
		System.out.println(cName);
		if (cName != null) {
			System.out.println(((JRadioButton) component).isSelected());

			if (cName.equals("rdbSchemNormal")) {
				if (((JRadioButton) component).isSelected()) {
					swix.find("schematic_holder2").setVisible(false);
					swix.find("schematic_holder").setVisible(true);
				}
			} else if (cName.equals("rdbSchemMB")) {
				if (((JRadioButton) component).isSelected()) {
					swix.find("schematic_holder").setVisible(false);
					swix.find("schematic_holder2").setVisible(true);
				}

			}
		}
	}
}
