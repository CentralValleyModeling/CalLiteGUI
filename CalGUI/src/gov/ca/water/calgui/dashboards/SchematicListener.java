package gov.ca.water.calgui.dashboards;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

			JPanel p = (JPanel) swix.find("schematic_card_layout");
			CardLayout cl = (CardLayout) p.getLayout();
			if (cName.equals("rdbSchemNormal")) {
				if (((JRadioButton) component).isSelected()) {

					cl.show(p, "First");
					updateSchematicLayout();
				}
			} else if (cName.equals("rdbSchemMB")) {
				if (((JRadioButton) component).isSelected()) {
					cl.show(p, "Second");
					updateSchematicLayout();
				}

			}
		}
	}

	/**
	 * @param d
	 */
	private void updateSchematicLayout() {
		JFrame f = (JFrame) swix.find("desktop");
		Dimension d = f.getSize();
		f.setSize(new Dimension(d.width + 1, d.height));
		f.setSize(d);

	}

}
