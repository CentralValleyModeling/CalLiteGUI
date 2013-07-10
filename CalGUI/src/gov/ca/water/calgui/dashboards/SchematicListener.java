package gov.ca.water.calgui.dashboards;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
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
		if (cName != null) {

			if (cName.equals("rdbSchemNormal")) {
				if (((JRadioButton) component).isSelected()) {
					swix.find("schematic_holder2").setVisible(false);
					swix.find("schematic_holder").setVisible(true);
					updateSchematicLayout();
				}
			} else if (cName.equals("rdbSchemMB")) {
				if (((JRadioButton) component).isSelected()) {
					swix.find("schematic_holder").setVisible(false);
					swix.find("schematic_holder2").setVisible(true);
					updateSchematicLayout();
				}

			}
		}
	}

	/**
	 * @param d
	 */
	private void updateSchematicLayout() {
		Dimension d = ((JFrame) swix.find("desktop")).getSize();
		((JFrame) swix.find("desktop")).setSize(d);

	}

}
