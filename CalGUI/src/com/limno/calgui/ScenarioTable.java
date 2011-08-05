package com.limno.calgui;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.io.File;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.swixml.SwingEngine;

public class ScenarioTable extends JFrame implements ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6485279690935542779L;
	private JTable scentable;
	private JScrollPane scrollingtable;
	private ScenarioPanel sPanel1;
	private ScenarioPanel sPanel2;
	private ScenarioPanel sPanel3;

	public ScenarioTable(Object[] selected, SwingEngine swix) {

		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setPreferredSize(new Dimension(600, 700));
		setMinimumSize(new Dimension(600, 700));
		setLayout(new FlowLayout());
		setTitle("CalLite 2.0 GUI - Scenario Comparison");

		String[] controls;
		String[] conttypes = null;
		String[] headers = null;
		String[][] scenmatrix = null;
		String[][] scenmatrix1 = null;
		String[][] scenmatrixdiff = null;
		GUILinks gl;

		StringBuffer[] sbScen = new StringBuffer[selected.length];
		for (int i = 0; i < selected.length; i++) {
			File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + selected[i].toString());
			sbScen[i] = GUI_Utils.ReadScenarioFile(f);
		}

		// Populate Cross Tab Matrix (control info columns first)

		String delims = "[|]";
		String NL = System.getProperty("line.separator");
		String sFull = sbScen[0].toString();
		String[] lines = sFull.split(NL);
		String[] temp = sFull.split("[;]");
		int totlines = lines.length + temp.length;

		controls = new String[totlines];
		conttypes = new String[totlines];
		scenmatrix = new String[totlines][selected.length + 2];
		scenmatrix1 = new String[totlines][selected.length + 2];
		headers = new String[selected.length + 2];
		headers[0] = "Control";
		headers[1] = "Location";
		headers[2] = selected[0].toString();

		int i = 0;
		while (true) {

			String textinLine = lines[i];
			if (textinLine == null | textinLine.equals("DATATABLEMODELS"))
				break;

			String comptext = "";
			String[] tokens = textinLine.split(delims);

			String comp = tokens[0];
			String value = tokens[1];
			JComponent component = (JComponent) swix.find(comp);
			StringBuffer sbparents = new StringBuffer();
			sbparents = GUI_Utils.GetControlParents(component, sbparents);

			sbparents = GUI_Utils.ReverseStringBuffer(sbparents, "[|]");

			controls[i] = comp;

			// if (component instanceof JCheckBox || component instanceof JRadioButton) {
			if (component instanceof JCheckBox) {
				JCheckBox jab = (JCheckBox) component;
				// comptext = comp + "| " + jab.getText();
				if (jab.getText().equals("")) {
					comptext = comp;
				} else {
					comptext = jab.getText();
				}

				if (value.equals("false"))
					value = "Turned off";
				else if (sbparents.toString().contains("D-1641") && jab.getText().contains("Default"))
					value = "Per D-1641";
				else
					value = "Turned on";

				conttypes[i] = "chk";
			} else if (component instanceof JRadioButton) {
				JRadioButton jab = (JRadioButton) component;
				comptext = comp;

				// if (jab.isSelected())
				if (value.equals("true")) {
					value = jab.getText();
				} else {
					value = "";
				}
				conttypes[i] = "rdb";
				
			} else {
				if (component instanceof JTextField || component instanceof NumericTextField) {
					conttypes[i] = "txt";
					JLabel label = (JLabel) swix.find(comp + "_t");
					if (label != null)
						// comptext = comp + "| " + label.getText();
						comptext = label.getText();
					else
						comptext = comp;
				} else {
					conttypes[i] = "oth";
					comptext = comp;
				}
			}
			if (comptext.equals("run_txaScenDesc"))
				comptext = "Scenario Description";

			scenmatrix[i][0] = comptext;
			scenmatrix[i][1] = sbparents.toString();
			scenmatrix[i][2] = value;
			
			i++;
		}
		// Data Table Entries
		int ii = i;
		i++;
		gl = new GUILinks();
		gl.readIn("Config\\GUI_Links2.table");
		while (true) {
			String textinLine = lines[i];
			String comptext = "";
			if (textinLine == null | textinLine.equals("END DATATABLEMODELS"))
				break;
			String[] tokens = textinLine.split(delims);

			String value = tokens[1];
			String cID = tokens[0];
			String cName = gl.CtrlFortableID(cID);
			String fileName = gl.tableNameForCtrl(cName);
			JComponent component = (JComponent) swix.find(cName);
			StringBuffer sbparents = new StringBuffer();
			sbparents = GUI_Utils.GetControlParents(component, sbparents);

			sbparents = GUI_Utils.ReverseStringBuffer(sbparents, "[|]");

			controls[ii] = fileName;
			comptext = fileName;

			String[] values = value.split("[;]");
			scenmatrix[ii][1] = comptext;
			scenmatrix[ii][0] = sbparents.toString();

			for (int j = 0; j < values.length; j++) {
				scenmatrix[ii][2] = values[j];
				conttypes[ii] = "tab";
				ii++;
				controls[ii] = "";
				scenmatrix[ii][0] = "";
				scenmatrix[ii][1] = fileName;
			}
			i++;
		}

		// Populate Cross Tab Matrix (other selected scenarios)
		int j = 0;
		for (i = 1; i < selected.length; i++) {
			sFull = sbScen[i].toString();
			lines = sFull.split(NL);

			j = 0;
			while (true) {
				String textinLine = lines[j];
				if (j == lines.length - 1 || textinLine.equals("DATATABLEMODELS"))
					break;
				String[] tokens = textinLine.split(delims);

				String comp = tokens[0];
				String value = tokens[1];

				JComponent component = (JComponent) swix.find(comp);
				if (component instanceof JCheckBox) {
					JCheckBox jab = (JCheckBox) component;
					StringBuffer sbparents = new StringBuffer();
					sbparents = GUI_Utils.GetControlParents(component, sbparents);
					sbparents = GUI_Utils.ReverseStringBuffer(sbparents, "[|]");

					if (value.equals("false"))
						value = "Turned off";
					else if (sbparents.toString().contains("D-1641") && jab.getText().contains("Default"))
						value = "Per D-1641";
					else
						value = "Turned on";
				} else if (component instanceof JRadioButton) {
					JRadioButton jab = (JRadioButton) component;

					// if (jab.isSelected())
					if (value.equals("true")) {
						value = jab.getText();
					} else {
						value = "";
					}
				}

				// Search for control index
				int index = GUI_Utils.FindInArray(controls, comp);
				// System.out.println(index);

				scenmatrix[index][i + 2] = value;
				
				j++;
			}
			headers[i + 2] = selected[i].toString();

			// Data Table Entries
			j++;
			if (j < lines.length) {
				while (true) {
					String textinLine = lines[j];
					if (j == lines.length - 1 || textinLine.equals("END DATATABLEMODELS"))
						break;
					String[] tokens = textinLine.split(delims);

					String value = tokens[1];
					String cID = tokens[0];
					String cName = gl.CtrlFortableID(cID);
					String fileName = gl.tableNameForCtrl(cName);

					// Search for control index
					int index = GUI_Utils.FindInArray(controls, fileName);
					// System.out.println(index);

					ii = index;
					String[] values = value.split("[;]");

					for (int k = 0; k < values.length; k++) {
						scenmatrix[ii][i + 2] = values[k];
						ii++;
					}
					j++;
				}

			}
		}

		int ito = 0;
		for (i = 0; i < scenmatrix.length; i++) {
			
			if (conttypes[i] == null || scenmatrix[i][1] == null) {
				break;
			} else {
				
				scenmatrix1[ito] = scenmatrix[i];
				if (scenmatrix[i][0].equals("Scenario Directory")) {
					for (j = 1; j <= selected.length; j++)
						scenmatrix1[ito][j + 1] = System.getProperty("user.dir") + "\\Scenarios\\" + scenmatrix[i][j + 1];
				} else if (scenmatrix[i][0].equals("spnRunStartMonth")) {
					scenmatrix1[ito][0] = "  Run Start";
					for (j = 1; j <= selected.length; j++)
						scenmatrix1[ito][j + 1] = scenmatrix[i][j + 1] + " " + scenmatrix[i + 1][j + 1];
					i++;
				} else if (scenmatrix[i][0].equals("spnRunEndMonth")) {
					scenmatrix1[ito][0] = "  Run End";
					for (j = 1; j <= selected.length; j++)
						scenmatrix1[ito][j + 1] = scenmatrix[i][j + 1] + " " + scenmatrix[i + 1][j + 1];
					i++;
				} else if (scenmatrix[i][1].equals("Demands|SWP Demands - South of Delta")) {
					scenmatrix1[ito][0] = "RDB";
					scenmatrix1[ito][1] = scenmatrix[i][1];
					int irdb = i;
					for (; conttypes[irdb].equals("rdb") && scenmatrix[irdb][1].equals(scenmatrix[i][1]); irdb++) {
						for (j = 1; j <= selected.length; j++)
							if  (scenmatrix[irdb][j + 1]!=null) {
								if (!scenmatrix[irdb][j + 1].equals(""))
									scenmatrix1[ito][j + 1] = scenmatrix[irdb][j + 1];
							}
					}
					int isel=ito;
					i = irdb;
					int ictrl = i;
					for (; scenmatrix[ictrl][1].equals(scenmatrix[i][1]); ictrl++) {
						int ct=0;
						for (j = 1; j <= selected.length; j++){
							if  (scenmatrix1[isel][j + 1].equals("User-Defined")) {
								if (ct==0) {ito++;}
								scenmatrix1[ito][0] = scenmatrix[ictrl][0];
								scenmatrix1[ito][1] = scenmatrix[ictrl][1];
								scenmatrix1[ito][j + 1] = scenmatrix[ictrl][j + 1];
								ct++;
							}
						}
					}
					i = ictrl - 1;
				} else if (scenmatrix[i][1].equals("Demands|CVP Demands - South of Delta")) {
					scenmatrix1[ito][0] = "RDB";
					scenmatrix1[ito][1] = scenmatrix[i][1];
					int irdb = i;
					for (; conttypes[irdb].equals("rdb") && scenmatrix[irdb][1].equals(scenmatrix[i][1]); irdb++) {
						for (j = 1; j <= selected.length; j++)
							if  (scenmatrix[irdb][j + 1]!=null) {
								if (!scenmatrix[irdb][j + 1].equals(""))
									scenmatrix1[ito][j + 1] = scenmatrix[irdb][j + 1];
							}
					}
					int isel=ito;
					i = irdb;
					int ictrl = i;
					for (; scenmatrix[ictrl][1].equals(scenmatrix[i][1]); ictrl++) {
						int ct=0;
						for (j = 1; j <= selected.length; j++){
							if  (scenmatrix1[isel][j + 1].equals("User-Defined")) {
								if (ct==0) {ito++;}
								scenmatrix1[ito][0] = scenmatrix[ictrl][0];
								scenmatrix1[ito][1] = scenmatrix[ictrl][1];
								scenmatrix1[ito][j + 1] = scenmatrix[ictrl][j + 1];
								ct++;
							}
						}
					}
					i = ictrl - 1;
					
					
				}else if (scenmatrix[i][0].equals("reg_rdbD1641") || (scenmatrix[i][0].equals("reg_rdbUD"))) {
					ito--;

									
				} else if (conttypes[i].equals("rdb")) {
					scenmatrix1[ito][0] = "RDB";
					scenmatrix1[ito][1] = scenmatrix[i][1];
					int irdb = i;
					for (; conttypes[irdb].equals("rdb") && scenmatrix[irdb][1].equals(scenmatrix[i][1]); irdb++) {
						for (j = 1; j <= selected.length; j++)
							if  (scenmatrix[irdb][j + 1]!=null) {
								if (!scenmatrix[irdb][j + 1].equals(""))
									scenmatrix1[ito][j + 1] = scenmatrix[irdb][j + 1];
							}
					}
					i = irdb - 1;
				}
			}
			//System.out.println(scenmatrix1[ito][0] + " " + scenmatrix1[ito][1]);
			ito++;
			
		}
		scenmatrix=scenmatrix1;
		
		
		// Process scenario matrix for hierarchical results

		String[][] procscenmatrix = new String[scenmatrix.length * 2][selected.length + 1];
		ii = 0;
		String CurLoc = "";
		String PrevLoc = "";
		String CurDash = "";
		String PrevDash = "";

		for (i = 0; i < ito; i++) {

			CurLoc = scenmatrix[i][1];
			String[] parArr = scenmatrix[i][1].split("[|]");

			// Whenever the dashboard changes, always add a new row
			CurDash = parArr[0].toUpperCase();
			if (!CurDash.equals(PrevDash)) {
				procscenmatrix[ii][0] = parArr[0].toUpperCase();
				ii++;
				System.out.println(i + " " + scenmatrix[i][1]);
			}
			
			//Special Handling for Regulation Sub Tabs
			if (CurDash.equals("REGULATIONS")) {
				if (!CurLoc.equals(PrevLoc)) {
					procscenmatrix[ii][0] = " " + parArr[1];
					ii++;
				}
			}

			// Start by always copying exactly

			procscenmatrix[ii][0] = scenmatrix[i][0];
			for (j = 1; j <= selected.length; j++)
				procscenmatrix[ii][j] = scenmatrix[i][j + 1];

			// Apply indentation and "correct" for radiobuttons

			String indent = "              ".substring(0, 2 * parArr.length);
			if (scenmatrix[i][0].equals("RDB"))
				procscenmatrix[ii][0] = indent + parArr[parArr.length - 1];
			else
				procscenmatrix[ii][0] = indent + scenmatrix[i][0];

			ii++;

			PrevDash = CurDash;
			PrevLoc = CurLoc;
		}

		// Create reduced arrays for tables

		boolean isBlank = true;
		for (i = procscenmatrix.length - 1; (i >= 0) && isBlank; i--) {
			isBlank = true;
			for (j = 0; j < procscenmatrix[i].length; j++)
				isBlank = isBlank & (procscenmatrix[i][j] == null);
		}

		String m1[][] = new String[i + 1][2];
		String m2[][] = new String[i + 1][selected.length + 1];
		for (; i >= 0; i--) {
			m2[i] = procscenmatrix[i];
			m1[i][0] = procscenmatrix[i][0];
			m1[i][1] = procscenmatrix[i][1];
		}

		// Headers for tables

		String[] headers1 = new String[2];
		headers1[0] = "Location";
		headers1[1] = selected[0].toString();

		String[] headers2 = new String[selected.length + 1];
		headers2[0] = "Location";
		for (j = 1; j <= selected.length; j++) {
			headers2[j] = selected[j - 1].toString();
		}

		JRadioButton b1 = new JRadioButton("Base");
		JRadioButton b2 = new JRadioButton("Comparison");
		JRadioButton b3 = new JRadioButton("Difference");

		ButtonGroup group = new ButtonGroup();
		group.add(b1);
		group.add(b2);
		group.add(b3);

		Box box = new Box(BoxLayout.LINE_AXIS);
		box.add(b1);
		box.add(b2);
		box.add(b3);

		Box vbox = new Box(BoxLayout.PAGE_AXIS);
		vbox.add(box);

		sPanel1 = new ScenarioPanel(m1, headers1);
		vbox.add(sPanel1);

		if (selected.length == 1) {
			b1.setSelected(true);
			b2.setEnabled(false);
			b3.setEnabled(false);
			sPanel1.setVisible(true);
		} else {
			sPanel2 = new ScenarioPanel(m2, headers2);

			// Only differing scenario info
			scenmatrixdiff = new String[procscenmatrix.length][selected.length + 1];
			int k = 0;
			for (i = 0; i < procscenmatrix.length; i++) {
				if (procscenmatrix[i][0] == null) {
					break;
				}
				;
				String[] scenariovals = new String[selected.length];
				for (j = 0; j < selected.length; j++) {
					scenariovals[j] = procscenmatrix[i][j + 1];
				}
				for (j = 0; j < scenariovals.length - 1; j++) {
					if (scenariovals[j] != null) {
						if (!scenariovals[j].equals(scenariovals[j + 1])) {
							// Differing values
							scenmatrixdiff[k][0] = procscenmatrix[i][0];
							// scenmatrixdiff[k][1]=scenmatrix[i][1];
							for (int jj = 0; jj < scenariovals.length; jj++) {
								scenmatrixdiff[k][jj + 1] = procscenmatrix[i][jj + 1];
							}
							k++;
							break;
						}
					}
				}
			}
			String m3[][] = new String[k + 1][selected.length + 1];
			for (; k >= 0; k--)
				m3[k] = scenmatrixdiff[k];

			sPanel3 = new ScenarioPanel(m3, headers2);

			if (((String) selected[0]).equals("Current_Scenario")) {
				b2.setSelected(true);
				sPanel2.setVisible(true);
				sPanel3.setVisible(false);
			} else {
				b2.setSelected(true);
				sPanel2.setVisible(true);
				sPanel3.setVisible(false);
			}

			vbox.add(sPanel2);
			vbox.add(sPanel3);
			sPanel1.setVisible(false);
			sPanel2.setVisible(true);
			sPanel3.setVisible(false);
		}

		add(vbox);

		b1.addItemListener(this);
		b2.addItemListener(this);
		b3.addItemListener(this);

	}

	private class ScenarioPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2528366027231642526L;

		public ScenarioPanel(String[][] data, String[] headers) {

			super();

			// setUndecorated(true);
			// getRootPane().setWindowDecorationStyle(JRootPane.NONE);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			setPreferredSize(new Dimension(600, 700));
			setMinimumSize(new Dimension(600, 700));
			setLayout(new FlowLayout());
			// GridBagConstraints c = new GridBagConstraints();

			scentable = new JTable();
			DefaultTableModel model = new DefaultTableModel(data, headers);
			scentable.setModel(model);
			scrollingtable = new JScrollPane(scentable);
			scrollingtable.setPreferredSize(new Dimension(480, 600));
			add(scrollingtable);
			// Set Icon
			java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
			setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

			pack();

			// TODO - Add close/cancel button

		}

	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		// TODO Auto-generated method stub

		if (ie.getItem() instanceof JRadioButton) {
			JRadioButton rb = (JRadioButton) ie.getItem();
			if (rb.isSelected()) {
				if (rb.getText().equals("Base")) {
					sPanel1.setVisible(true);
					sPanel2.setVisible(false);
					sPanel3.setVisible(false);

				} else if (rb.getText().equals("Comparison")) {
					sPanel1.setVisible(false);
					sPanel2.setVisible(true);
					sPanel3.setVisible(false);

				} else if (rb.getText().equals("Difference")) {
					sPanel1.setVisible(false);
					sPanel2.setVisible(false);
					sPanel3.setVisible(true);

				}

			}
		}
	}

}
