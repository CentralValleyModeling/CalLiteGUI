package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;
import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class RegAction implements ActionListener {
	private final SwingEngine swix;
	private Boolean[] RegUserEdits;
	private static Logger log = Logger.getLogger(RegAction.class.getName());
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private final ButtonGroup reg_btng1;
	private int[] regFlags;

	public RegAction(SwingEngine swix, Boolean[] RegUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        ButtonGroup reg_btng1, int[] RegFlags) {
		this.swix = swix;
		this.RegUserEdits = RegUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.reg_btng1 = reg_btng1;
		this.regFlags = RegFlags;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if (ae.getActionCommand().startsWith("Reg_Copy")) {

			JTable table = (JTable) swix.find("tblRegValues");
			GUIUtils.copyTableValues(table);

		} else if (ae.getActionCommand().startsWith("Reg_Paste")) {

			JTable table = (JTable) swix.find("tblRegValues");
			GUIUtils.pasteTableValues(table);

		} else if (ae.getActionCommand().startsWith("Reg_Default")) {

			JTable table = (JTable) swix.find("tblRegValues");
			DataFileTableModel tm = (DataFileTableModel) table.getModel();
			int size = tm.datafiles.length;
			if (size == 1) {
				tm.initVectors();
			} else if (size == 2) {
				tm.initVectors2();
			}
			table.repaint();
			int tID = tm.tID;
			if (RegUserEdits == null) {
				RegUserEdits = new Boolean[20];
			}
			RegUserEdits[tID] = false;

			// JButton btn = (JButton) swix.find("btnRegDef");
			// btn.setEnabled(false);
		} else if (ae.getActionCommand().startsWith("Reg_1641")) {
			String cName = "";
			JComponent scr = (JComponent) swix.find("scrRegValues");
			if (scr.isVisible() == true) {

				JTable table = (JTable) swix.find("tblRegValues");
				DataFileTableModel tm = (DataFileTableModel) table.getModel();
				int tID = tm.tID;
				String strI = String.valueOf(tID);
				cName = gl.ctrlFortableID(strI);
			} else {
				JPanel pan = (JPanel) swix.find("reg_panTab");
				cName = pan.getToolTipText();
			}
			if (regFlags == null) {
				regFlags = new int[40];
			}
			// String stID = String.valueOf(tID);
			int rID = Integer.parseInt(gl.RIDForCtrl(cName));
			regFlags[rID] = 1;

			RegulationSetup.SetRegCheckBoxes(swix, RegUserEdits, dTableModels, gl, reg_btng1, cName, true, "1641", regFlags);

		} else if (ae.getActionCommand().startsWith("Reg_1485")) {

			JComponent scr = (JComponent) swix.find("scrRegValues");
			String cName = "";
			if (scr.isVisible() == true) {
				JTable table = (JTable) swix.find("tblRegValues");
				DataFileTableModel tm = (DataFileTableModel) table.getModel();

				int tID = tm.tID;
				String strI = String.valueOf(tID);
				cName = gl.ctrlFortableID(strI);
			} else {
				JPanel pan = (JPanel) swix.find("reg_panTab");
				cName = pan.getToolTipText();

			}

			if (regFlags == null) {
				regFlags = new int[20];
			}
			// String stID = String.valueOf(tID);
			int rID = Integer.parseInt(gl.RIDForCtrl(cName));
			regFlags[rID] = 3;

			RegulationSetup.SetRegCheckBoxes(swix, RegUserEdits, dTableModels, gl, reg_btng1, cName, true, "1485", regFlags);

		} else if (ae.getActionCommand().equals("AC_Help")) {

			JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
			String label = jtp.getTitleAt(jtp.getSelectedIndex());
			CalLiteHelp calLiteHelp = new CalLiteHelp();
			calLiteHelp.showHelp(label);

		}
	}
}