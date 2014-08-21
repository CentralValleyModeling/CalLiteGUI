package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.utils.GUIUtils;
import gov.ca.water.calgui.utils.NumericTextField;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.swixml.SwingEngine;

public class OpListener implements ItemListener {
	private final SwingEngine swix;

	public OpListener(SwingEngine swix) {
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

			if (cName.startsWith("op_")) {
				// Checkbox in Operation page changed
				if (cName.startsWith("op_rdb1")) {
					JButton btn = (JButton) swix.find("op_btn1");
					btn.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					btn = (JButton) swix.find("op_btn2");
					btn.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				} else if (cName.startsWith("op_rdb2")) {
					JPanel pan2 = (JPanel) swix.find("op_pan2");
					GUIUtils.toggleEnComponentAndChildren(pan2, e.getStateChange() == ItemEvent.SELECTED, JRadioButton.class);
					GUIUtils.toggleEnComponentAndChildren(pan2, e.getStateChange() == ItemEvent.SELECTED, JCheckBox.class);

					// Toggle run_rdbD1485
					((JRadioButton) swix.find("run_rdbD1485")).setEnabled(e.getStateChange() != ItemEvent.SELECTED);

				} else if (cName.startsWith("op_rdb3")) {
					JButton btn = (JButton) swix.find("op_btn3");
					btn.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				} else if (cName.startsWith("op_op_ntfSWP_t")) {
					JLabel lab = (JLabel) swix.find("op_ntfSWP_t");
					lab.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					NumericTextField ntf = (NumericTextField) swix.find("op_ntfSWP_t");
					ntf.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				} else if (cName.startsWith("op_ckbCWP")) {
					JLabel lab = (JLabel) swix.find("op_ntfCWP1_t");
					lab.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					NumericTextField ntf = (NumericTextField) swix.find("op_ntfCWP1");
					ntf.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					lab = (JLabel) swix.find("op_ntfCWP2_t");
					lab.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					ntf = (NumericTextField) swix.find("op_ntfCWP2");
					ntf.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				}

			}
		}

	}
}