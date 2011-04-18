package com.limno.calgui;

import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.swixml.SwingEngine;

public class MainMenu7Mar2011 implements ActionListener, ItemListener, MouseListener {
	private SwingEngine swix;

	// Declare public Objects
	JPanel mainmenu;
	JPanel demands;
	JPanel hydroclimate;
	JPanel regulations;
	JPanel Reporting;
	JPanel reportdialog;
	JPanel facilities;
	JPanel Display;
	JPanel controls2;
	JPanel controls3;
	JPanel presets;
	JPanel hyd_CCOpt;
	JPanel hyd_CC;
	JPanel hyd_CC1;
	JPanel hyd_CC2;
	JPanel dem_SWP;
	JPanel dem_CVP;
	JPanel dem_UDSWP;
	JPanel dem_UDCVP;
	JFrame dialog;
	GUILinks gl;

	JMenuBar menu;
	private JList lstScenarios;
	private DSS_Grabber dss_Grabber;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainMenu7Mar2011() throws Exception {

		// Read GUI configuration

		swix = new SwingEngine(this);
		swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		swix.render(new File("Config_and_Lookup\\Config\\MainMenu.xml"))
				.setVisible(true);

		// Set ActionListeners (Regulations Page)

		swix.setActionListener(menu, this);
		swix.setActionListener(regulations, this);
		swix.setActionListener(Reporting, this);
		swix.setActionListener(hydroclimate, this);
		swix.setActionListener(demands, this);
		swix.setActionListener(facilities, this);

		// Set ItemListeners (Regulations Page)

		/*
		Component[] components = regulations.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JRadioButton) {
				JRadioButton r = (JRadioButton) components[i];
				r.addItemListener(this);
			} else if (components[i] instanceof JCheckBox) {
				JCheckBox ckb = (JCheckBox) components[i];
				ckb.addItemListener(this);
			}
		}
		*/
		Component[] components = regulations.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JCheckBox) {
				JCheckBox r = (JCheckBox) components[i];
				r.addItemListener(this);
			} else if (components[i] instanceof JPanel) {
				JPanel p = (JPanel) components[i];
				Component[] components1 = p.getComponents();
				for (int j = 0; j < components1.length; j++) {
					if (components1[j] instanceof JCheckBox) {
						JCheckBox r = (JCheckBox) components1[j];
						r.addItemListener(this);
					} else if (components1[j] instanceof JPanel) {
						JPanel p1 = (JPanel) components1[j];
						Component[] components2 = p1.getComponents();
						for (int k = 0; k < components2.length; k++) {
							if (components2[k] instanceof JCheckBox) {
								JCheckBox r = (JCheckBox) components2[k];
								r.addItemListener(this);
							}

						}
					}
				}
			}
		}

		// Set ItemListeners (Demands Page)

		components = dem_SWP.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JRadioButton) {
				JRadioButton r = (JRadioButton) components[i];
				r.addItemListener(this);
			}
		}
		components = dem_CVP.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JRadioButton) {
				JRadioButton r = (JRadioButton) components[i];
				r.addItemListener(this);
			}
		}

		// Set ItemListeners (Hydroclimate Page)

		components = hyd_CCOpt.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JRadioButton) {
				JRadioButton r = (JRadioButton) components[i];
				r.addItemListener(this);
			}
		}
		components = hyd_CC1.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JCheckBox) {
				JCheckBox r = (JCheckBox) components[i];
				r.addItemListener(this);
			}
		}
		components = hyd_CC2.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JCheckBox) {
				JCheckBox r = (JCheckBox) components[i];
				r.addItemListener(this);
			}
		}

		// Set ItemListeners (Facilities Page)

		components = facilities.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JCheckBox) {
				JCheckBox r = (JCheckBox) components[i];
				r.addItemListener(this);
			} else if (components[i] instanceof JPanel) {
				JPanel p = (JPanel) components[i];
				Component[] components1 = p.getComponents();
				for (int j = 0; j < components1.length; j++) {
					if (components1[j] instanceof JCheckBox) {
						JCheckBox r = (JCheckBox) components1[j];
						r.addItemListener(this);
					} else if (components1[j] instanceof JPanel) {
						JPanel p1 = (JPanel) components1[j];
						Component[] components2 = p1.getComponents();
						for (int k = 0; k < components2.length; k++) {
							if (components2[k] instanceof JCheckBox) {
								JCheckBox r = (JCheckBox) components2[k];
								r.addItemListener(this);
							}

						}
					}
				}
			}
		}

		// Set ItemListeners (Reporting Page)

		components = Display.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JRadioButton) {
				JRadioButton r = (JRadioButton) components[i];
				r.addItemListener(this);
			} else if (components[i] instanceof JCheckBox) {
				JCheckBox ckb = (JCheckBox) components[i];
				ckb.addItemListener(this);
			}
		}

		components = presets.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JCheckBox) {
				JCheckBox ckb = (JCheckBox) components[i];
				ckb.addItemListener(this);
				ckb.addMouseListener(this);
			}
		}

		// Set current directory (Run Settings Page)

		// Set Up Run Settings Page

		JLabel label = (JLabel) swix.find("map");
		java.net.URL imgURL = getClass().getResource("/images/map.jpg");
		if (imgURL != null) {
			ImageIcon image = new ImageIcon(imgURL, null);
			label.setIcon(image);
		}
		JSpinner spnSM1 = (JSpinner) swix.find("spnRunStartMonth");
		JSpinner spnEM1 = (JSpinner) swix.find("spnRunEndMonth");
		String[] monthNames = { " Jan ", " Feb ", " Mar ", " Apr ", " May ",
				" Jun ", " Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec " };
		SpinnerListModel monthModel = new SpinnerListModel(monthNames);
		spnSM1.setModel(monthModel);
		spnEM1.setModel(monthModel);
		// Set up year spinners
		// TODO - Get years from scenarios rather than fixed
		// TODO (?) - Control spinner so end year >= start year
		JSpinner spnSY1 = (JSpinner) swix.find("spnRunStartYear");
		JSpinner spnEY1 = (JSpinner) swix.find("spnRunEndYear");
		SpinnerModel yearModel1 = new SpinnerNumberModel(1921, 1921, 2003, 1);
		SpinnerModel yearModel2 = new SpinnerNumberModel(2003, 1921, 2003, 1);
		spnSY1.setModel(yearModel1);
		spnEY1.setModel(yearModel2);
		spnSY1.setEditor(new JSpinner.NumberEditor(spnSY1, "####"));
		spnEY1.setEditor(new JSpinner.NumberEditor(spnEY1, "####"));

		// Set Up Facilities Page
		JTable table = (JTable) swix.find("tblBanks");
		String[][] data = { { "Jan", "10300" }, { "Feb", "10300" },
				{ "Mar", "10300" }, { "Apr", "10300" }, { "May", "10300" },
				{ "Jun", "10300" }, { "Jul", "10300" }, { "Aug", "10300" },
				{ "Sep", "10300" }, { "Oct", "10300" }, { "Nov", "10300" },
				{ "Dec", "10300" } };
		String[] headers = { "Month", "cfs" };
		DefaultTableModel model = new DefaultTableModel(data, headers);
		table.setModel(model);

		table = (JTable) swix.find("tblIF1");
		String[][] data1 = { { "Jan", "15000" }, { "Feb", "15000" },
				{ "Mar", "15000" }, { "Apr", "15000" }, { "May", "15000" },
				{ "Jun", "15000" }, { "Jul", "15000" }, { "Aug", "15000" },
				{ "Sep", "15000" }, { "Oct", "15000" }, { "Nov", "15000" },
				{ "Dec", "15000" } };
		String[] headers1 = { "Month", "cfs" };
		model = new DefaultTableModel(data1, headers1);
		table.setModel(model);
		JTable table2 = (JTable) swix.find("tblIF2");
		String[][] data2 = { { "Jan", "0", "0" }, { "Feb", "0", "0" },
				{ "Mar", "0", "0" }, { "Apr", "0", "0" }, { "May", "0", "0" },
				{ "Jun", "0", "0" }, { "Jul", "0", "0" }, { "Aug", "0", "0" },
				{ "Sep", "0", "0" }, { "Oct", "0", "0" }, { "Nov", "0", "0" },
				{ "Dec", "0", "0" } };
		String[] headers2 = { "Month", "min cfs", "max cfs" };
		DefaultTableModel model2 = new DefaultTableModel(data2, headers2);
		table2.setModel(model2);

		// String currentDir = System.getProperty("user.dir");
		// JTextField tb = (JTextField) swix.find("tbRSdir");
		// tb.setText(currentDir);

		// Read switch lookup

		gl = new GUILinks();
		gl.readIn("Config_and_Lookup\\Config\\GUI_Links.table");

		// Setup for Reporting page

		// Set up scenario list

		lstScenarios = (JList) swix.find("SelectedList");
		dss_Grabber = new DSS_Grabber(lstScenarios);
		GetDSSFilename getDSSFilename = new GetDSSFilename(lstScenarios,
				(JLabel) swix.find("lblBase"));
		lstScenarios.setModel(getDSSFilename.lmScenNames);
		lstScenarios.setBorder(new LineBorder(Color.gray, 1));
		lstScenarios.setPreferredSize(new Dimension(285, 100));

		JButton btnScenario = (JButton) swix.find("btnAddScenario");
		btnScenario.addActionListener((ActionListener) getDSSFilename);

		JButton btnSetBase = (JButton) swix.find("btnSetBase");
		btnSetBase.addActionListener(this);

		// Set up month spinners
		// TODO (?) - cycling spinner?
		JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
		JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
		// String[] monthNames = { " Jan ", " Feb ", " Mar ", " Apr ", " May ",
		// " Jun ", " Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec " };
		// SpinnerListModel monthModel = new SpinnerListModel(monthNames);
		spnSM.setModel(monthModel);
		spnEM.setModel(monthModel);
		// Set up year spinners
		// TODO - Get years from scenarios rather than fixed
		// TODO (?) - Control spinner so end year >= start year
		JSpinner spnSY = (JSpinner) swix.find("spnStartYear");
		JSpinner spnEY = (JSpinner) swix.find("spnEndYear");
		// SpinnerModel yearModel1 = new SpinnerNumberModel(1921, 1921, 2003,
		// 1);
		// SpinnerModel yearModel2 = new SpinnerNumberModel(2003, 1921, 2003,
		// 1);
		spnSY.setModel(yearModel1);
		spnEY.setModel(yearModel2);
		spnSY.setEditor(new JSpinner.NumberEditor(spnSY, "####"));
		spnEY.setEditor(new JSpinner.NumberEditor(spnEY, "####"));
		// Set up report list
		JList lstReports = (JList) swix.find("lstReports");
		// String[] reportNamesEG = { " " };
		// lstReports.setListData(reportNamesEG);
		lstReports.setBorder(new LineBorder(Color.gray, 1));
		lstReports.setFixedCellWidth(400);
		lstReports.setPreferredSize(new Dimension(600, 100));
		lstReports.setVisible(true);

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) throws Exception {

		new MainMenu7Mar2011();

	}

	// Respond to selection of a check box or radiobox.
	public void itemStateChanged(ItemEvent e) {
		JComponent component = (JComponent) e.getItem();
		// was "e.getItemSelected"
		String cName = component.getName();
		if (cName != null) {
			if (cName.startsWith("ckbp")) {
				// CheckBox in presets panel changed
				// String cID = cName.substring(3);
			}

			else if (cName.startsWith("ckbReg")) {
				// CheckBox in Regulations panel changed
				component = (JComponent) swix.find("scrRegValues");
				JTable table = (JTable) swix.find("tblRegValues");
				if (component != null)
					component
							.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				
				String cID = cName.substring(6);
				populateDTable(cID, table);
				
				//createDTableFrame(cID);
				
				/*
			else if (cName.startsWith("ckb")) {
				// CheckBox in Regulations panel changed
				String cID = cName.substring(3);
				component = (JComponent) swix.find("rdbD" + cID);
				if (component != null)
					component
							.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				component = (JComponent) swix.find("rdbB" + cID);
				if (component != null)
					component
							.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				component = (JComponent) swix.find("rdbU" + cID);
				if (component != null)
					component
							.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				component = (JComponent) swix.find("btnU" + cID);
				if (component != null)
					component
							.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			} else if (cName.startsWith("rdb")) {
				// RadioButton in Regulations panel changed
				JButton button = (JButton) swix.find("btnU"
						+ cName.substring(4));
				if (cName.startsWith("rdbU")) {
					button.setText("Edit");
				} else {
					button.setText("View");
				}
				
				*/
				
			} else if (cName.startsWith("fac_ckb")) {
				// checkbox in facilities panel changed
				JPanel panel = (JPanel) swix.find("fac_pan"
						+ cName.substring(7));

				panel.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				if (e.getStateChange() == ItemEvent.SELECTED) {
					panel.setVisible((e.getStateChange() == ItemEvent.SELECTED));
					Component[] components = panel.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i].setEnabled(true);
					}
				} else {
					Component[] components = panel.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i].setEnabled(false);
					}
				}

				// set all other panels to invisible
				Component[] components = facilities.getComponents();
				for (int i = 0; i < components.length; i++) {
					if (components[i] instanceof JPanel) {
						JPanel p = (JPanel) components[i];
						String pName = p.getName();
						if (pName != null) {
							if (pName.startsWith("fac_pan")) {
								if (pName.startsWith("fac_pan"
										+ cName.substring(7))) {
								} else {
									p.setVisible(false);
								}
							}
						}
					}
				}

			} else if (cName.startsWith("dem_rdbUD")) {
				// Checkbox in Demands page changed

				if (cName.startsWith("dem_rdbUD1")) {
					Component[] components = dem_UDSWP.getComponents();
					for (int i = 0; i < components.length; i++) {
						if (components[i] instanceof JTextField) {
							JTextField c = (JTextField) components[i];
							c.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
						}
					}
				} else if (cName.startsWith("dem_rdbUD2")) {
					Component[] components = dem_UDCVP.getComponents();
					for (int i = 0; i < components.length; i++) {
						if (components[i] instanceof JTextField) {
							JTextField c = (JTextField) components[i];
							c.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
						}
					}
				}
			} else if (cName.startsWith("Repckb")) {
				// Checkbox in Reporting page changed

				if (cName.startsWith("RepckbExceedancePlot")) {
					Component[] components = controls2.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					}
				} else if (cName.startsWith("RepckbSummaryTable")) {
					Component[] components = controls3.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					}
				}
			} else if (cName.startsWith("hyd_ckb")) {
				// Checkbox in Cliamte Scenarios page changed
				Component[] components = hyd_CC1.getComponents();
				int selct = 0;
				for (int i = 0; i < components.length; i++) {
					if (components[i] instanceof JCheckBox) {
						JCheckBox c = (JCheckBox) components[i];
						boolean b = c.isSelected();
						if (b == true) {
							selct = selct + 1;
						}
					}
				}
				components = hyd_CC2.getComponents();
				for (int i = 0; i < components.length; i++) {
					if (components[i] instanceof JCheckBox) {
						JCheckBox c = (JCheckBox) components[i];
						boolean b = c.isSelected();
						if (b == true) {
							selct = selct + 1;
						}
					}
				}
				JLabel lab = (JLabel) swix.find("hydlab_selected");
				if (selct == 0) {
					lab.setText("0 realizations selected");
				} else if (selct == 1) {
					lab.setText("1 realization selected - Deterministic mode required");
				} else {
					lab.setText(selct
							+ " realizations selected - Probabilistic mode required");
				}

			} else if (cName.startsWith("hyd_rdb")) {
				// Radio in Hydroclimate

				if (cName.startsWith("hyd_rdbHis")) {
					Component[] components = hyd_CC.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() != ItemEvent.SELECTED));
					}
					components = hyd_CC1.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() != ItemEvent.SELECTED));
						if (components[i] instanceof JCheckBox) {
							JCheckBox c = (JCheckBox) components[i];
							c.setSelected(false);
						}
					}
					components = hyd_CC2.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() != ItemEvent.SELECTED));
						if (components[i] instanceof JCheckBox) {
							JCheckBox c = (JCheckBox) components[i];
							c.setSelected(false);
						}
					}

				} else {
					Component[] components = hyd_CC.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					}
					components = hyd_CC1.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					}
					components = hyd_CC2.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i]
								.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					}
				}

			}
		}

	}

	// React to menu selections.
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("UD_Table")) {

			// Figure out calling button and look up table
			//String cID = e.getActionCommand().substring(8);
			//createDTableFrame(cID);

			// System.out.println(cID);
			// System.out.println(gl.tableNameForCtrl(cID));

		} else if ("AC_RUN".equals(e.getActionCommand())) {
			/*
			 *
			 * 
			 */

			// Write DLTREGULATION file

			OutputStream outputStream;
			try {
				outputStream = new FileOutputStream(
						"Config_and_Lookup\\Lookup\\DLTREGULATION.table");
			} catch (FileNotFoundException e2) {
				System.out.println("Cannot open DLTRegulation file");
				return;
			}

			try {

				PrintStream output = new PrintStream(outputStream);

				output.println("gui_DLTREGULATION");
				output.println("SWITCHID OPTION");

				for (int switchIdx = 1; switchIdx <= 14; switchIdx++) {
					String switchID = Integer.toString(switchIdx);
					String cID = gl.ctrlForSwitch(switchID);
					int option = 0;
					JCheckBox cb = (JCheckBox) swix.find("ckb" + cID);
					if (cb == null) {
						option = 0;
					} else if (!cb.isSelected()) {
						option = 0;
					} else {
						JRadioButton rb = (JRadioButton) swix
								.find("rdbU" + cID);
						JButton b = (JButton) swix.find("btnU" + cID);
						if (b == null) {
							option = 1;
						} else if (!b.isVisible()) {
							option = 1;
						} else if (rb.isSelected()) {
							option = 2;
						} else {
							option = 1;
						}
					}

					output.println(switchID + " " + option);

					// Output table if needed
					if ((option == 2)
							|| ((option == 1) && (swix.find("btnU" + cID))
									.isVisible())) {

						System.out.println("Output to "
								+ gl.tableNameForCtrl(cID));
						int tID = Integer.parseInt(cID);
						if (dTableModels[tID] == null) {
							System.out.println("Table not initialized");
						} else {
							dTableModels[tID].writeToFile(gl
									.tableNameForCtrl(cID));
						}
					}
				}

				output.close();
				outputStream.close();
			} catch (IOException ioe) {
				System.out.println("IOException");
			}

			// "Run" model

			try {
				Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("cmd /c start "
						+ System.getProperty("user.dir") + "\\CalLite.BAT");
				int exitVal = proc.waitFor();
				System.out.println("Process exitValue: " + exitVal);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			/*
			 * try { Runtime.getRuntime().exec("cmd /c start " +
			 * System.getProperty("user.dir") + "\\	CalLite.BAT"); } catch
			 * (IOException e1) { // TODO Auto-generated catch block
			 * e1.printStackTrace(); }
			 */
		} else if (e.getActionCommand().startsWith("HYD_Clear")) {
			Component[] components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}
			components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}

		} else if (e.getActionCommand().startsWith("HYD_All")) {
			Component[] components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
			components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
		} else if (e.getActionCommand().startsWith("HYD_B1")) {
			Component[] components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
			components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}
		} else if (e.getActionCommand().startsWith("HYD_A2")) {
			Component[] components = hyd_CC2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(true);
				}
			}
			components = hyd_CC1.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}

		} else if (e.getActionCommand().startsWith("AC_Report")) {
			swix = new SwingEngine(this);
			try {
				swix.render(new File(
						"Config_and_Lookup\\Config\\ReportDialog.xml"));
				dialog.setVisible(true);
				swix.setActionListener(reportdialog, this);
				GetDSSFilename getDSSFilename1 = new GetDSSFilename(null,
						(JTextField) swix.find("tfReportFILE1"));
				JButton btnFile1 = (JButton) swix.find("btnGetReportFile1");
				btnFile1.addActionListener((ActionListener) getDSSFilename1);
				
				GetDSSFilename getDSSFilename2 = new GetDSSFilename(null,
						(JTextField) swix.find("tfReportFILE2"));
				JButton btnFile2 = (JButton) swix.find("btnGetReportFile2");
				btnFile2.addActionListener((ActionListener) getDSSFilename2);
				
				GetDSSFilename getDSSFilename3 = new GetDSSFilename(null,
						(JTextField) swix.find("tfReportFILE3"),"PDF");
				JButton btnFile3 = (JButton) swix.find("btnGetReportFile3");
				btnFile3.addActionListener((ActionListener) getDSSFilename3);
						
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (e.getActionCommand().startsWith("AC_GenReport")) {

			Report report = null;
			try {
				report = new Report(
						"Config_and_Lookup\\Config\\calsim_callite_template.inp");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			report.getOutputFile();

		} else if (e.getActionCommand().startsWith("Rep_AllMonths")) {
			Component[] components = controls2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					String cName = c.getName();
					if (cName != null) {
						if (cName.startsWith("RepchkMon")) {
							c.setSelected(true);
						}
					}
				}
			}
		} else if (e.getActionCommand().startsWith("Rep_ClearMonths")) {
			Component[] components = controls2.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					String cName = components[i].getName();
					if (cName != null) {
						if (cName.startsWith("RepchkMon")) {
							c.setSelected(false);
						}
					}
				}
			}
		}

		else if (e.getActionCommand().startsWith("Rep_AddList")) {
			String cAdd;
			cAdd = "Comp;";

			// Store previous list items
			JList lstReports = (JList) swix.find("lstReports");
			int size = lstReports.getModel().getSize(); // 4
			int n;
			n = 0;
			String[] lstArray = new String[size];
			for (int i = 0; i < size; i++) {
				Object item = lstReports.getModel().getElementAt(i);
				if (item.toString() != " ") {
					lstArray[n] = item.toString();
					n = n + 1;
				}
			}

			String[] lstArray1 = new String[n + 1];
			for (int i = 0; i < n; i++) {
				lstArray1[i] = lstArray[i];
			}

			// Date
			JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
			JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
			JSpinner spnSY = (JSpinner) swix.find("spnStartYear");
			JSpinner spnEY = (JSpinner) swix.find("spnEndYear");
			String cDate = spnSM.getValue().toString()
					+ spnSY.getValue().toString();
			cDate = cDate + "-" + spnEM.getValue().toString()
					+ spnEY.getValue().toString();
			cAdd = cAdd + cDate;

			Component[] components = controls3.getComponents();
			JCheckBox ckb = (JCheckBox) swix.find("chbSummaryTable");
			if (ckb.isSelected()) {
				String cST;
				cST = ",ST-";
				for (int i = 0; i < components.length; i++) {
					if (components[i] instanceof JCheckBox) {
						JCheckBox c = (JCheckBox) components[i];
						boolean b = c.isSelected();
						if (b == true) {
							String cName = c.getText();
							// TODO Need different naming convention.
							cST = cST + cName;
						}
					}
				}
				cAdd = cAdd + cST;
			}

			String cSTOR;
			cSTOR = "";
			components = presets.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					String cName = c.getName();
					if (cName.startsWith("ckbp")) {
						boolean b = c.isSelected();
						if (b == true) {
							cSTOR = cSTOR + "," + c.getText();
						}
					}
				}
			}
			cAdd = cAdd + cSTOR;

			lstArray1[n] = cAdd;

			// String[] reportNamesEG = {cDate};
			lstReports.setListData(lstArray1);

		}

		else if (e.getActionCommand().startsWith("Rep_ClearList")) {

			JList lstReports = (JList) swix.find("lstReports");
			String[] lstArray1 = new String[0];
			lstReports.setListData(lstArray1);

		}

		else if (e.getActionCommand().startsWith("Rep_LoadList")) {

			String aLine;
			Integer n = 0;
			Vector<String> data = new Vector<String>();

			try {

				FileInputStream fin = new FileInputStream(
						"Config_and_Lookup\\Config\\reportlist.cgr");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fin));

				// Read until first non-comment line
				aLine = br.readLine();
				while (aLine.startsWith("!") && aLine != null) {
					aLine = br.readLine();
				}
				// aLine = br.readLine();// Skip title line;
				while (aLine != null) {
					data.add(aLine);
					aLine = br.readLine();
				}
				br.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			JList lstReports = (JList) swix.find("lstReports");
			lstReports.setListData(data);

		}

		else if (e.getActionCommand().startsWith("Rep_SaveList")) {

			OutputStream outputStream;
			try {
				outputStream = new FileOutputStream(
						"Config_and_Lookup\\Config\\reportlist.cgr");
			} catch (FileNotFoundException e2) {
				System.out.println("Cannot open reportlist.cgr");
				return;
			}

			// Store previous list items
			JList lstReports = (JList) swix.find("lstReports");
			int size = lstReports.getModel().getSize(); // 4
			int n;
			n = 0;
			String[] lstArray = new String[size];
			for (int i = 0; i < size; i++) {
				Object item = lstReports.getModel().getElementAt(i);
				if (item.toString() != " ") {
					lstArray[n] = item.toString();
					n = n + 1;
				}
			}
			try {

				PrintStream output = new PrintStream(outputStream);
				for (int i = 0; i < n; i++) {
					output.println(lstArray[i]);
				}

				output.close();
				outputStream.close();
			} catch (IOException ioe) {
				System.out.println("IOException");
			}

		}

		else if (e.getActionCommand().startsWith("Rep_DispCur")) {
			HecDss hD;
			// TODO Auto-generated method stub
			try {

				HecTime ht = new HecTime();
				hD = HecDss.open("CL_2020D09E_CONV_092710_DV");
				TimeSeriesContainer dC = (TimeSeriesContainer) hD.get(
						"/CALSIM/S_TRNTY/STORAGE/01JAN2010/1MON/2020D09E/",
						true);

				ht.set(dC.times[0]);
				ht.set(dC.times[984]);

				MonthlyTableModel mTM = new MonthlyTableModel(dC);
				MonthlyTableDialog mTD = new MonthlyTableDialog(
						"S_TRNTY/STORAGE", mTM);
				mTD.setVisible(true);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else {
			JComponent component = (JComponent) e.getSource();
			String cName = component.getName();
			if (cName != null) {
				if (component.getName().equals("btnSetBase")) {
					if (lstScenarios.getSelectedIndex() >= 0) {
						JLabel label = (JLabel) swix.find("lblBase");
						label.setText((String) lstScenarios.getSelectedValue());
					}
				} else if (component.getName().equals("btnDisplayCurrent")) {

				}
			}

		}

	}

	// This method returns the selected radio button in a button group
	/**
	 * @wbp.parser.entryPoint
	 */
	public static JRadioButton getSelection(ButtonGroup group) {
		for (Enumeration e = group.getElements(); e.hasMoreElements();) {
			JRadioButton b = (JRadioButton) e.nextElement();
			if (b.getModel() == group.getSelection()) {
				return b;
			}
		}
		return null;
	}

	private DataFileTableModel[] dTableModels;

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createDTableFrame(String cID) {

		String fileName = gl.tableNameForCtrl(cID);

		int tID = Integer.parseInt(cID);
		if (dTableModels == null) {
			dTableModels = new DataFileTableModel[20];
		}
		if (dTableModels[tID] == null) {
			dTableModels[tID] = new DataFileTableModel(
					"Config_and_Lookup\\Lookup\\" + fileName + ".table");
		}

		TableDialog td = new TableDialog(fileName, dTableModels[tID]);
		td.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		td.setVisible(true);
	}
	
	protected void populateDTable(String cID, final JTable t ) {

		String fileName = gl.tableNameForCtrl(cID);

		int tID = Integer.parseInt(cID);
		if (dTableModels == null) {
			dTableModels = new DataFileTableModel[20];
		}
		if (dTableModels[tID] == null) {
			dTableModels[tID] = new DataFileTableModel(
					"Config_and_Lookup\\Lookup\\" + fileName + ".table");
		}



		t.setModel(dTableModels[tID]);
		t.createDefaultColumnsFromModel();

		t.setRowHeight(20);
		for (int col = 0; col < t.getColumnCount(); col++) {
			t.getColumnModel().getColumn(col).setWidth(50);
		}

		t.setPreferredScrollableViewportSize(new Dimension(t.getColumnCount()*60+60,t.getRowCount()*20));
		
		t.setCellSelectionEnabled(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		t.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				int l = t.getSelectedRow();
				int c = t.getSelectedColumn();
				t.setValueAt(Character.toString(ke.getKeyChar()),l,c);
				t.editCellAt(l, c);
			}
		});

		ExcelAdapter myAd = new ExcelAdapter(t);

	}

	@Override

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// Double Click
		Integer iClickCount = e.getClickCount();
		if (iClickCount == 2) {
			JComponent component = (JComponent) e.getComponent();

			String cName = component.getName();
			if (cName != null) {
				if (cName.startsWith("ckbp")) {

					// ----- HANDLE DISPLAY OF SINGLE VARIABLE -----

					if (lstScenarios.getModel().getSize() == 0) {
						JOptionPane.showMessageDialog(null,
								"No scenarios loaded", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {

						JCheckBox chk = (JCheckBox) component;
						chk.setFont(new Font("Tahoma", Font.ITALIC, 12));
						chk.repaint();

						JLabel label = (JLabel) swix.find("lblBase");
						dss_Grabber.setBase(label.getText());
						dss_Grabber.setLocation(cName);
						// TODO: Set location based on sender

						TimeSeriesContainer[] primary_Results = dss_Grabber
								.getPrimarySeries();
						TimeSeriesContainer[] diff_Results = dss_Grabber
								.getDifferenceSeries(primary_Results);
						TimeSeriesContainer[] exc_Results = dss_Grabber
								.getExceedanceSeries(primary_Results);
						TimeSeriesContainer[] secondary_Results = dss_Grabber
								.getSecondarySeries();

						DoAChart dc = new DoAChart();
						dc.main(primary_Results[0]);

						DoAChart2 dc2 = new DoAChart2();
						DoAChart2 dc2a = new DoAChart2();
						DoAChart2 dc2b = new DoAChart2();

						if (primary_Results.length > 1) {
							dc2.main(
									"Comparison " + dss_Grabber.primaryDSSName,
									primary_Results, secondary_Results);
							dc2a.main("Difference "
									+ dss_Grabber.primaryDSSName, diff_Results,
									null);
						} else {
							dc2.main(dss_Grabber.primaryDSSName,
									primary_Results, secondary_Results);

						}
						dc2b.main("Exceedance " + dss_Grabber.primaryDSSName,
								exc_Results, null);

						chk.setFont(new Font("Tahoma", Font.BOLD, 12));
						chk.repaint();
					}
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}