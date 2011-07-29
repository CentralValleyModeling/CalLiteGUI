package com.limno.calgui;

/*
 * ProgressFrame - simple frame to show progress passed by method setText();
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.*;

import org.swixml.SwingEngine;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import rma.awt.JButtonGroup;

public class ScenarioFrame extends JFrame {
	private JLabel lblscen1;
	private JButtonGroup btngrp;
	private JRadioButton rdbAll;
	private JRadioButton rdbDiff;
	private JButton btnComp;
	private JList lstScen;

	public ScenarioFrame(String title, final SwingEngine swix) {

		super();

		// setUndecorated(true);
		// getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setPreferredSize(new Dimension(400, 300));
		setMinimumSize(new Dimension(400, 300));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		setTitle(title);

		lblscen1 = new JLabel("Select Scenarios to Compare", SwingConstants.HORIZONTAL);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(lblscen1, c);

		lstScen = new JList();
		lstScen.setVisibleRowCount(7);
		// populate with file list.
		File dir = new File(".//Scenarios");
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".cls");
			}
		};
		Vector filelist = new Vector(Arrays.asList(dir.list(filter)));
		lstScen.setListData(filelist);
		JScrollPane scrollingList = new JScrollPane(lstScen);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 200;
		c.anchor = GridBagConstraints.WEST;
		add(scrollingList, c);
		scrollingList.setMinimumSize(new Dimension(100, 100));

		rdbAll = new JRadioButton("List All");
		rdbAll.setSelected(true);
		c.gridx = 0;
		c.gridy = 2;
		c.ipadx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.EAST;
		add(rdbAll, c);

		rdbDiff = new JRadioButton("List Differences");
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		add(rdbDiff, c);

		btngrp = new JButtonGroup();
		btngrp.add(rdbAll);
		btngrp.add(rdbDiff);

		btnComp = new JButton("Compare");
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 00;
		c.anchor = GridBagConstraints.WEST;
		btnComp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object[] selected = lstScen.getSelectedValues();
				String[] controls;
				String[] conttypes = null;
				String[] headers = null;
				String[][] scenmatrix = null;
				String[][] scenmatrixdiff = null;
				GUILinks gl;

				int option = 0;
				if (selected.length < 1) {
					JOptionPane.showMessageDialog(null, "You must select at least one scenario file to display.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					if (rdbAll.isSelected()) {
						option = 1;
					} else {
						option = 2;
					}
					StringBuffer[] sbScen = new StringBuffer[selected.length];
					for (int i = 0; i < selected.length; i++) {
						File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + selected[i].toString());
						sbScen[i] = GUI_Utils.ReadScenarioFile(f);
					}

					// Populate Cross Tab Matrix (control info columns first)
					@SuppressWarnings("unused")
					String delims = "[|]";
					String NL = System.getProperty("line.separator");
					String sFull = sbScen[0].toString();
					String[] lines = sFull.split(NL);
					String[] temp = sFull.split("[;]");
					int totlines = lines.length + temp.length;

					controls = new String[totlines];
					conttypes = new String[totlines];
					scenmatrix = new String[totlines][selected.length + 2];
					headers = new String[selected.length + 2];
					headers[0] = "Control";
					headers[1] = "Location";
					headers[2] = selected[0].toString();

					int i = 0;
					while (true) {
						String textinLine = lines[i];
						String comptext = "";
						if (textinLine == null | textinLine.equals("DATATABLEMODELS"))
							break;
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

							//if (jab.isSelected())
							if (value.equals("true")){
								value = jab.getText();
							}else{
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
								StringBuffer sbparents=new StringBuffer();
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

								//if (jab.isSelected())
								if (value.equals("true")){
									value = jab.getText();
								}else{
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
				}
				// String[] headersa = new String[selected.length + 2];
				// headersa[0] = "Location";
				// headersa[1] = "more";
				// for (int j = 1; j <= selected.length; j++) {
				// headersa[j + 1] = selected[j - 1].toString();
				// }
				// // ScenarioTable ScenTable2 = new ScenarioTable("CalLite 2.0 GUI - Scenario Comparison", scenmatrix,
				// headersa);
				// ScenTable2.setVisible(true);

				// Second pass - process date and radiobuttons

				int ito = 0;
				for (int i = 0; i < scenmatrix.length; i++) {

					if (conttypes[i] == null || scenmatrix[i][1] == null) {
						break;
					} else {
						scenmatrix[ito] = scenmatrix[i];
						if (scenmatrix[i][0].equals("spnRunStartMonth")) {
							scenmatrix[ito][0] = "  Run Start";
							for (int j = 1; j <= selected.length; j++)
								scenmatrix[ito][j + 1] = scenmatrix[i][j + 1] + " " + scenmatrix[i + 1][j + 1];
							i++;
						} else if (scenmatrix[i][0].equals("spnRunEndMonth")) {
							scenmatrix[ito][0] = "  Run End";
							for (int j = 1; j <= selected.length; j++)
								scenmatrix[ito][j + 1] = scenmatrix[i][j + 1] + " " + scenmatrix[i + 1][j + 1];
							i++;
						} else if (conttypes[i].equals("rdb")) {
							scenmatrix[ito][0] = "RDB";
							scenmatrix[ito][1] = scenmatrix[i][1];
							int irdb = i;
							for (; conttypes[irdb].equals("rdb") && scenmatrix[irdb][1].equals(scenmatrix[i][1]); irdb++) {
								for (int j = 1; j <= selected.length; j++)
									if (!scenmatrix[irdb][j + 1].equals(""))
										scenmatrix[ito][j + 1] = scenmatrix[irdb][j + 1];
							}
							i = irdb - 1;
						}
					}
					ito++;
					System.out.println(i + " " + ito);
				}

				// Process scenario matrix for hierarchical results
				String[] headers1 = new String[selected.length + 1];
				headers1[0] = "Location";
				for (int j = 1; j <= selected.length; j++) {
					headers1[j] = selected[j - 1].toString();
				}
				String[][] procscenmatrix = new String[scenmatrix.length * 2][selected.length + 1];
				int ii = 0;
				String CurLoc = "";
				String PrevLoc = "";
				String CurDash = "";
				String PrevDash = "";

				for (int i = 0; i < ito; i++) {
					System.out.println(i + " " + ii);
					CurLoc = scenmatrix[i][1];
					String[] parArr = scenmatrix[i][1].split("[|]");

					// Whenever the dashboard changes, always add a new row
					CurDash = parArr[0].toUpperCase();
					if (!CurDash.equals(PrevDash)) {
						procscenmatrix[ii][0] = parArr[0].toUpperCase();
						ii++;
						System.out.println(i + " " + scenmatrix[i][1]);
					}

					// Start by always copying exactly

					procscenmatrix[ii][0] = scenmatrix[i][0];
					for (int j = 1; j <= selected.length; j++)
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

				if (option == 1) {
					// All scenario infor
					ScenarioTable ScenTable = new ScenarioTable("CalLite 2.0 GUI - Scenario Comparison", procscenmatrix, headers1);
					ScenTable.setVisible(true);
				} else {
					// Only differing scenario info
					scenmatrixdiff = new String[procscenmatrix.length][selected.length + 1];
					int k = 0;
					for (int i = 0; i < procscenmatrix.length; i++) {
						if (procscenmatrix[i][0] == null) {
							break;
						}
						;
						String[] scenariovals = new String[selected.length];
						for (int j = 0; j < selected.length; j++) {
							scenariovals[j] = procscenmatrix[i][j + 1];
						}
						for (int j = 0; j < scenariovals.length - 1; j++) {
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

					ScenarioTable ScenTable = new ScenarioTable("CalLite 2.0 GUI - Scenario Comparison", scenmatrixdiff, headers1);
					ScenTable.setVisible(true);
				}

			}
		});
		add(btnComp, c);

		pack();
		// setAlwaysOnTop(true);

		// TODO - Add close/cancel button

	}
}
