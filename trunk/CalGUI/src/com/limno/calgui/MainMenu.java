package com.limno.calgui;

import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ProgressMonitor;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jfree.data.time.Month;
import org.swixml.SwingEngine;

import com.limno.calgui.GetDSSFilename.RBListItem;
import com.limno.calgui.GetDSSFilename.JFileChooser2;
import com.limno.calgui.results.ChartPanel1;
import com.limno.calgui.results.DSS_Grabber;
import com.limno.calgui.results.MonthlyTablePanel;
import com.limno.calgui.results.Report;
import com.limno.calgui.results.ScrollablePicture;
import com.limno.calgui.results.SummaryTablePanel;
import com.limno.calgui.SymbolCanvas;

public class MainMenu implements ActionListener, ItemListener, MouseListener, TableModelListener, MenuListener, ChangeListener, ListDataListener,
		KeyEventDispatcher {
	private SwingEngine swix;

	// Declare public Objects
	static JHelp helpViewer = null;

	JFrame desktop;
	static JFrame help;
	JPanel runsettings;
	JPanel mainmenu;
	JPanel demands;
	JPanel schematics;
	JPanel hydroclimate;
	JPanel regulations;
	JPanel Reporting;
	JPanel reportdialog;
	JPanel facilities;
	JPanel operations;
	JPanel Display;
	JPanel controls2;
	JPanel controls3;
	JPanel presets;
	JPanel shortage_flow;
	JPanel WMA;
	JPanel hyd_CCOpt;
	JPanel hyd_CC;
	JPanel hyd_CC1;
	JPanel hyd_CC2;
	JPanel dem_SWP;
	JPanel dem_CVP;
	JPanel dem_UDSWP;
	JPanel dem_UDCVP;
	JFrame dialog;
	ButtonGroup reg_btng1;
	GUILinks gl;

	String desktopTitle;
	String scenFilename;
	GetDSSFilename getScenFilename;

	static public String lookups[][];

	String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	JMenuBar menu;
	ProgressMonitor pMon;
	private JList lstScenarios;
	private DSS_Grabber dss_Grabber;

	public DataFileTableModel[] dTableModels = null;
	public Boolean[] RegUserEdits = null;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainMenu() throws Exception {

		// Read GUI configuration

		swix = new SwingEngine(this);

		swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		swix.render(new File(System.getProperty("user.dir") + "\\Config\\GUI.xml")).setVisible(true);

		desktopTitle = desktop.getTitle() + ".196";
		desktop.setResizable(false);

		// Help hotkey
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);

		scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
		desktop.setTitle(desktopTitle + " - " + scenFilename);
		getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");

		// Set ActionListeners (Regulations Page)

		swix.setActionListener(menu, this);
		swix.setActionListener(regulations, this);
		swix.setActionListener(Reporting, this);
		swix.setActionListener(hydroclimate, this);
		swix.setActionListener(demands, this);
		swix.setActionListener(operations, this);
		swix.setActionListener(facilities, this);
		swix.setActionListener(runsettings, this);
		swix.setActionListener(schematics, this);

		// Set ItemListeners
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(regulations, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(operations, this);
		GUI_Utils.SetRadioButtonItemListener(dem_SWP, this);
		GUI_Utils.SetRadioButtonItemListener(dem_CVP, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(hydroclimate, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(facilities, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(Display, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(presets, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(shortage_flow, this);
		GUI_Utils.SetMouseListener(presets, this);
		GUI_Utils.SetMouseListener(shortage_flow, this);
		GUI_Utils.SetMouseListener(facilities, this);
		GUI_Utils.SetMenuListener(menu, this);
		GUI_Utils.SetMouseListener(regulations, this);
		GUI_Utils.SetChangeListener(regulations, this);

		// Recolor results tabs

		JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
		jtp.setForegroundAt(6, Color.blue);

		jtp.setForegroundAt(7, Color.blue);
		jtp.setForegroundAt(8, Color.blue);
		jtp.setForegroundAt(9, Color.blue);

		jtp.setBackgroundAt(6, Color.WHITE);
		jtp.setBackgroundAt(7, Color.WHITE);
		jtp.setBackgroundAt(8, Color.WHITE);
		jtp.setBackgroundAt(9, Color.WHITE);

		// Set Up Run Settings Page
		JLabel label = (JLabel) swix.find("map");
		java.net.URL imgURL = getClass().getResource("/images/CA_map_and_Delta.jpg");
		if (imgURL != null) {

			BufferedImage img;
			img = ImageIO.read(imgURL);
			ImageIcon image = new ImageIcon(img, null);
			// label.setIcon(image);

			// ImageIcon image = new ImageIcon(imgURL, null);
			// label.setIcon(image);
		}

		imgURL = getClass().getResource("/images/CalLITE_08_30corrected10-21-10.jpg");
		if (imgURL != null) {
			// ImageIcon image = new ImageIcon(imgURL, null);
			// System.out.println(image.getIconHeight());

			BufferedImage img;
			img = ImageIO.read(imgURL);
			Font defaultFont = new Font("Serif", Font.PLAIN, 20);
			SymbolCanvas symbols = new SymbolCanvas(defaultFont, 0x2500, 207, img);
			symbols.paint(img.getGraphics());

			ImageIcon image = new ImageIcon(img, null);

			ScrollablePicture picture = new ScrollablePicture(image, 100);
			picture.setName("schem_map");
			JScrollPane scrollpane = (JScrollPane) swix.find("schem_scr");
			scrollpane.add(picture);
			scrollpane.setViewportView(picture);
			scrollpane.setPreferredSize(new Dimension(800, 700));
			scrollpane.setOpaque(true);
			scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			GUI_Utils.SetMouseListener(scrollpane, this);

		}

		JSpinner spnSM1 = (JSpinner) swix.find("spnRunStartMonth");
		JSpinner spnEM1 = (JSpinner) swix.find("spnRunEndMonth");
		SpinnerListModel monthModel = new SpinnerListModel(monthNames);
		SpinnerListModel monthModel2 = new SpinnerListModel(monthNames);
		spnSM1.setModel(monthModel);
		spnSM1.setValue(monthNames[9]);
		spnEM1.setModel(monthModel2);
		spnEM1.setValue(monthNames[8]);

		// Set up year spinners

		JSpinner spnSY1 = (JSpinner) swix.find("spnRunStartYear");
		JSpinner spnEY1 = (JSpinner) swix.find("spnRunEndYear");
		SpinnerModel yearModel1 = new SpinnerNumberModel(1921, 1921, 2003, 1);
		SpinnerModel yearModel2 = new SpinnerNumberModel(2003, 1921, 2003, 1);
		spnSY1.setModel(yearModel1);
		spnEY1.setModel(yearModel2);
		spnSY1.setEditor(new JSpinner.NumberEditor(spnSY1, "####"));
		spnEY1.setEditor(new JSpinner.NumberEditor(spnEY1, "####"));
		spnSY1.addChangeListener(this);
		spnEY1.addChangeListener(this);
		spnSM1.addChangeListener(this);
		spnEM1.addChangeListener(this);

		// Set Up Facilities Page
		// TODO: Externalize these tables

		JTable table = (JTable) swix.find("tblBanks");
		String[][] data = { { "Jan", "10300" }, { "Feb", "10300" }, { "Mar", "10300" }, { "Apr", "10300" }, { "May", "10300" }, { "Jun", "10300" },
				{ "Jul", "10300" }, { "Aug", "10300" }, { "Sep", "10300" }, { "Oct", "10300" }, { "Nov", "10300" }, { "Dec", "10300" } };
		String[] headers = { "Month", "cfs" };
		DefaultTableModel model = new DefaultTableModel(data, headers);
		table.setModel(model);

		table = (JTable) swix.find("tblIF1");
		String[][] data1 = { { "Jan", "15000" }, { "Feb", "15000" }, { "Mar", "15000" }, { "Apr", "15000" }, { "May", "15000" }, { "Jun", "15000" },
				{ "Jul", "15000" }, { "Aug", "15000" }, { "Sep", "15000" }, { "Oct", "15000" }, { "Nov", "15000" }, { "Dec", "15000" } };
		String[] headers1 = { "Month", "cfs" };
		model = new DefaultTableModel(data1, headers1);
		table.setModel(model);
		JTable table2 = (JTable) swix.find("tblIF2");
		String[][] data2 = { { "Jan", "0", "0" }, { "Feb", "0", "0" }, { "Mar", "0", "0" }, { "Apr", "0", "0" }, { "May", "0", "0" },
				{ "Jun", "0", "0" }, { "Jul", "0", "0" }, { "Aug", "0", "0" }, { "Sep", "0", "0" }, { "Oct", "0", "0" }, { "Nov", "0", "0" },
				{ "Dec", "0", "0" } };
		String[] headers2 = { "Month", "min cfs", "max cfs" };
		DefaultTableModel model2 = new DefaultTableModel(data2, headers2);
		table2.setModel(model2);

		// String currentDir = System.getProperty("user.dir");
		// JTextField tb = (JTextField) swix.find("tbRSdir");
		// tb.setText(currentDir);

		// Read switch lookup

		gl = new GUILinks();
		gl.readIn("Config\\GUI_Links2.table");

		readInLookups(); // Temporary access to quick reports info from
							// gui_Links3.table

		// Setup for Reporting page

		// Set up scenario list

		lstScenarios = (JList) swix.find("SelectedList");

		dss_Grabber = new DSS_Grabber(lstScenarios);
		JRadioButton rdb1 = (JRadioButton) swix.find("rdbp001");
		JRadioButton rdb2 = (JRadioButton) swix.find("rdbp002");

		GetDSSFilename getDSSFilename = new GetDSSFilename(lstScenarios, (JLabel) swix.find("lblBase"), rdb1, rdb2);
		lstScenarios.setModel(getDSSFilename.lmScenNames);
		lstScenarios.setBorder(new LineBorder(Color.gray, 1));

		JButton btnScenario = (JButton) swix.find("btnAddScenario");
		btnScenario.addActionListener((ActionListener) getDSSFilename);

		JButton btnScenarioDel = (JButton) swix.find("btnDelScenario");
		btnScenarioDel.addActionListener((ActionListener) getDSSFilename);

		JButton btnClearAll = (JButton) swix.find("btnClearScenario");
		btnClearAll.addActionListener(this);

		JButton btnReport = (JButton) swix.find("btnReport");
		btnReport.addActionListener(this);

		// Set up month spinners
		// TODO (?) - cycling spinner?
		JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
		JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
		spnSM.setModel(monthModel);
		spnEM.setModel(monthModel2);
		// Set up year spinners
		// TODO - Get years from scenarios rather than fixed
		// TODO (?) - Control spinner so end year >= start year
		JSpinner spnSY = (JSpinner) swix.find("spnStartYear");
		JSpinner spnEY = (JSpinner) swix.find("spnEndYear");
		spnSY.setModel(yearModel1);
		spnEY.setModel(yearModel2);
		spnSY.setEditor(new JSpinner.NumberEditor(spnSY, "####"));
		spnEY.setEditor(new JSpinner.NumberEditor(spnEY, "####"));

		// Set up report list
		JList lstReports = (JList) swix.find("lstReports");
		lstReports.setBorder(new LineBorder(Color.gray, 1));
		lstReports.setVisible(true);

		// Load Default Scenario
		JTextField tf = (JTextField) swix.find("run_txfScen");
		String scen = tf.getText();
		File file = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
		RegUserEdits = GUI_Utils.SetControlValues(file, swix, dTableModels, gl);
		// Refresh checkbox labels
		for (int i = 0; i < RegUserEdits.length; i++) {
			if (RegUserEdits[i] != null) {
				String cID = Integer.toString(i);
				String cName = gl.CtrlFortableID(cID);
				JCheckBox ckb = (JCheckBox) swix.find(cName);
				String ckbtext = ckb.getText();
				String[] ckbtext1 = ckbtext.split(" - ");
				ckbtext = ckbtext1[0];
				if (ckbtext1.length > 0) {
					if (RegUserEdits[i] == true) {
						ckb.setText(ckbtext + " - User Def.");
					} else {
						ckb.setText(ckbtext + " -  Default");
					}
				} else {
					ckb.setText(ckbtext);
				}

			}
		}

		/*
		 * String cName = gl.CtrlFortableID(tokens[0]); JCheckBox comp1 = (JCheckBox) swix.find("cName");
		 * comp1.setSelected(true); RegUserEdits=GUI_Utils.SetControlValues(file, swix, dTableModels, gl);
		 */

		// PDF Report
		GetDSSFilename getDSSFilename0 = new GetDSSFilename(null, (JTextField) swix.find("tfTemplateFILE"), "inp");
		JButton btnFile0 = (JButton) swix.find("btnGetTemplateFile");
		btnFile0.addActionListener((ActionListener) getDSSFilename0);
		GetDSSFilename getDSSFilename1 = new GetDSSFilename(null, (JTextField) swix.find("tfReportFILE1"));
		JButton btnFile1 = (JButton) swix.find("btnGetReportFile1");
		btnFile1.addActionListener((ActionListener) getDSSFilename1);
		GetDSSFilename getDSSFilename2 = new GetDSSFilename(null, (JTextField) swix.find("tfReportFILE2"));
		JButton btnFile2 = (JButton) swix.find("btnGetReportFile2");
		btnFile2.addActionListener((ActionListener) getDSSFilename2);
		GetDSSFilename getDSSFilename3 = new GetDSSFilename(null, (JTextField) swix.find("tfReportFILE3"), "PDF");
		JButton btnFile3 = (JButton) swix.find("btnGetReportFile3");
		btnFile3.addActionListener((ActionListener) getDSSFilename3);

		// Check for scenario changes on Exit.
		desktop.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.out.println("Exiting");

				// *** Determine if scenario has changed.

				// Store selections
				StringBuffer sb = new StringBuffer();
				sb = GUI_Utils.GetControlValues(runsettings, sb);
				sb = GUI_Utils.GetControlValues(regulations, sb);
				sb = GUI_Utils.GetControlValues(hydroclimate, sb);
				sb = GUI_Utils.GetControlValues(demands, sb);
				sb = GUI_Utils.GetControlValues(operations, sb);
				sb = GUI_Utils.GetControlValues(facilities, sb);

				// get table values.
				final String NL = System.getProperty("line.separator");
				sb.append("DATATABLEMODELS" + NL);
				ArrayList GUILinks = new ArrayList();
				ArrayList GUITables = new ArrayList();
				GUILinks = GUI_Utils.GetGUILinks("Config\\GUI_Links2.table");
				GUITables = GUI_Utils.GetGUITables(GUILinks, "Regulations");
				sb = GUI_Utils.GetTableModelData(dTableModels, GUITables, gl, sb);
				sb.append("END DATATABLEMODELS" + NL);
				sb.append("USERDEFINEDFLAGS" + NL);
				for (int i = 0; i < RegUserEdits.length; i++) {
					if (RegUserEdits[i] != null) {
						sb.append(i + "|" + RegUserEdits[i] + NL);
					}
				}
				sb.append("END USERDEFINEDFLAGS" + NL);

				// Read existing file
				JTextField tf = (JTextField) swix.find("run_txfScen");
				String scen = tf.getText();
				File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
				StringBuffer sbExisting = GUI_Utils.ReadScenarioFile(f);

				Boolean scensave = false;

				if (!sb.toString().equals(sbExisting.toString())) {

					/*
					 * int n = JOptionPane.showConfirmDialog(mainmenu,
					 * "Would you like to save the scenario definition? \nScenario information " + "will be saved to '"
					 * + System.getProperty("user.dir") + "\\Scenarios\\" + scen + "'", "CalLite Gui",
					 * JOptionPane.YES_NO_OPTION);
					 */
					int n = JOptionPane.showConfirmDialog(mainmenu, "Scenario selections have changed. Would you like to save the the changes?",
							"CalLite Gui", JOptionPane.YES_NO_OPTION);

					if (n == JOptionPane.YES_OPTION) {

						JFileChooser fc = new JFileChooser();
						fc.setFileFilter(new CLSFileFilter());
						fc.setCurrentDirectory(new File(".//Scenarios"));

						String dirname = ".//Scenarios";
						File file = null;
						String filename = null;
						int retval = fc.showSaveDialog(mainmenu);
						if (retval == JFileChooser.APPROVE_OPTION) {
							file = fc.getSelectedFile();
							filename = file.toString();
						}

						if (filename != null) {

							if (new File(filename).exists())
								scensave = (JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + filename
										+ "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

							if (scensave = true) {

								GUI_Utils.CreateNewFile(filename);
								f = new File(filename);
								try {
									FileWriter fstream = new FileWriter(f);
									BufferedWriter outobj = new BufferedWriter(fstream);
									outobj.write(sb.toString());
									outobj.close();

								} catch (Exception e1) {
									System.err.println("Error: " + e1.getMessage());
								}

							}
						}
					}

				}

				System.exit(0);
			}
		});

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) {

		// Load help
		try {
			ClassLoader cl = MainMenu.class.getClassLoader();
			URL url = HelpSet.findHelpSet(null, "helpset.hs");
			helpViewer = new JHelp(new HelpSet(cl, url));
			helpViewer.setCurrentID("Introduction");
		} catch (Exception e) {
			System.err.println("API Help Set not found");
		}
		help = new JFrame("CalLite 2.0 GUI Help");
		help.getContentPane().add(helpViewer);
		help.pack();
		help.setVisible(false);

		// Load menu
		try {
			new MainMenu();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}

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
				Boolean isSelect = e.getStateChange() == ItemEvent.SELECTED;
				SetRegCheckBoxes(cName, isSelect);

			} else if (cName.startsWith("reg_rdbD1641")) {
				// do not allow user edits to tables
				JTable table = (JTable) swix.find("tblRegValues");
				JRadioButton rdb = (JRadioButton) e.getItem();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (dTableModels != null) {

						DataFileTableModel tm = (DataFileTableModel) table.getModel();
						int size = tm.datafiles.length;
						if (size == 1) {
							tm.initVectors();
						} else if (size == 2) {
							tm.initVectors2();
						}
						table.repaint();

						table.setCellSelectionEnabled(false);
						table.setEnabled(false);
						if (table.isEditing()) {
							table.getCellEditor().stopCellEditing();
						}
						int tID = tm.tID;
						if (RegUserEdits == null) {
							RegUserEdits = new Boolean[20];
						}
						RegUserEdits[tID] = false;

						String cName1 = gl.CtrlFortableID(Integer.toString(tID));
						JCheckBox ckb = (JCheckBox) swix.find(cName1);
						String ckbtext = ckb.getText();
						String[] ckbtext1 = ckbtext.split(" - ");
						ckbtext = ckbtext1[0];
						ckb.setText(ckbtext + " -  Default");
					}

				}

			} else if (cName.startsWith("reg_rdbUD")) {
				// do not allow user edits to tables
				JTable table = (JTable) swix.find("tblRegValues");
				JRadioButton rdb = (JRadioButton) e.getItem();

				if (e.getStateChange() == ItemEvent.SELECTED) {

					table.setCellSelectionEnabled(true);
					table.setEnabled(true);

					DataFileTableModel tm = (DataFileTableModel) table.getModel();
					int tID = tm.tID;
					if (RegUserEdits == null) {
						RegUserEdits = new Boolean[20];
					}
					RegUserEdits[tID] = true;

					String cName1 = gl.CtrlFortableID(Integer.toString(tID));
					JCheckBox ckb = (JCheckBox) swix.find(cName1);
					String ckbtext = ckb.getText();
					String[] ckbtext1 = ckbtext.split(" - ");
					ckbtext = ckbtext1[0];
					ckb.setText(ckbtext + " - User Def.");
				}

			} else if (cName.startsWith("fac_ckb")) {
				// checkbox in facilities panel changed
				JPanel panel = (JPanel) swix.find("fac_pan" + cName.substring(7));

				if (panel != null) {
					// set all "data" panels to invisible
					GUI_Utils.ToggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUI_Utils.ToggleVisComponent(panel, true);
					GUI_Utils.ToggleEnComponentAndChildren(panel, e.getStateChange() == ItemEvent.SELECTED);

				}

			} else if (cName.startsWith("dem_rdbUD")) {
				// Checkbox in Demands page changed

				if (cName.startsWith("dem_rdbUD1")) {
					GUI_Utils.ToggleEnComponentAndChildren(dem_UDSWP, e.getStateChange() == ItemEvent.SELECTED, NumericTextField.class);
				} else if (cName.startsWith("dem_rdbUD2")) {
					GUI_Utils.ToggleEnComponentAndChildren(dem_UDCVP, e.getStateChange() == ItemEvent.SELECTED, NumericTextField.class);
				}
			} else if (cName.startsWith("op_")) {
				// Checkbox in Operation page changed
				if (cName.startsWith("op_rdb1")) {
					JButton btn = (JButton) swix.find("op_btn1");
					btn.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					btn = (JButton) swix.find("op_btn2");
					btn.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				} else if (cName.startsWith("op_rdb2")) {
					JPanel pan2 = (JPanel) swix.find("op_pan2");
					GUI_Utils.ToggleEnComponentAndChildren(pan2, e.getStateChange() == ItemEvent.SELECTED, JRadioButton.class);
					GUI_Utils.ToggleEnComponentAndChildren(pan2, e.getStateChange() == ItemEvent.SELECTED, JCheckBox.class);
				} else if (cName.startsWith("op_rdb3")) {
					JButton btn = (JButton) swix.find("op_btn3");
					btn.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				} else if (cName.startsWith("op_ckbSWP")) {
					JLabel lab = (JLabel) swix.find("op_labSWP");
					lab.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					NumericTextField ntf = (NumericTextField) swix.find("op_ntfSWP");
					ntf.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				} else if (cName.startsWith("op_ckbCWP")) {
					JLabel lab = (JLabel) swix.find("op_labCWP1");
					lab.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					NumericTextField ntf = (NumericTextField) swix.find("op_ntfCWP1");
					ntf.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					lab = (JLabel) swix.find("op_labCWP2");
					lab.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
					ntf = (NumericTextField) swix.find("op_ntfCWP2");
					ntf.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				}
			} else if (cName.startsWith("Repckb")) {
				// Checkbox in Reporting page changed
				if (cName.startsWith("RepckbExceedancePlot")) {
					GUI_Utils.ToggleEnComponentAndChildren(controls2, e.getStateChange() == ItemEvent.SELECTED);
				} else if (cName.startsWith("RepckbSummaryTable")) {
					GUI_Utils.ToggleEnComponentAndChildren(controls3, e.getStateChange() == ItemEvent.SELECTED);
				}
			} else if (cName.startsWith("hyd_ckb")) {
				// Checkbox in Climate Scenarios page changed
				int selct = 0;
				selct = GUI_Utils.CountSelectedButtons(hyd_CC1, JCheckBox.class, selct);
				selct = GUI_Utils.CountSelectedButtons(hyd_CC2, JCheckBox.class, selct);

				JLabel lab = (JLabel) swix.find("hydlab_selected");
				if (selct == 0) {
					lab.setText("0 realizations selected");
				} else if (selct == 1) {
					lab.setText("1 realization selected - Deterministic mode required");
				} else {
					lab.setText(selct + " realizations selected - Probabilistic mode required");
				}

			} else if (cName.startsWith("hyd_rdb")) {
				// Radio in Hydroclimate

				if (cName.startsWith("hyd_rdbHis")) {
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC, e.getStateChange() != ItemEvent.SELECTED);
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC1, e.getStateChange() != ItemEvent.SELECTED);
					GUI_Utils.ToggleSelComponentAndChildren(hyd_CC1, false, JCheckBox.class);
					GUI_Utils.ToggleSelComponentAndChildren(hyd_CC2, false, JCheckBox.class);
				} else if (cName.startsWith("hyd_rdbMid") || cName.startsWith("hyd_rdbEnd")) {
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC, e.getStateChange() == ItemEvent.SELECTED);
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC1, e.getStateChange() == ItemEvent.SELECTED);
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC2, e.getStateChange() == ItemEvent.SELECTED);
				}
			}
		}
	}

	// React to menu selections.
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("UD_Table")) {

			// Figure out calling button and look up table
			// String cID = e.getActionCommand().substring(8);
			// createDTableFrame(cID);

			// System.out.println(cID);
			// System.out.println(gl.tableNameForCtrl(cID));

		} else if ("AC_RUN".equals(e.getActionCommand())) {

			// Check if selections are valid
			JTextField tf = (JTextField) swix.find("run_txfScen");
			String scen = tf.getText();

			JSpinner spn = (JSpinner) swix.find("spnRunStartMonth");
			String StartMon = (String) spn.getValue();
			StartMon = StartMon.trim();
			spn = (JSpinner) swix.find("spnRunEndMonth");
			String EndMon = (String) spn.getValue();
			EndMon = EndMon.trim();
			spn = (JSpinner) swix.find("spnRunStartYear");
			Integer StartYr = (Integer) spn.getValue();
			spn = (JSpinner) swix.find("spnRunEndYear");
			Integer EndYr = (Integer) spn.getValue();

			// Determine Month/Count
			Integer iSMon = GUI_Utils.MonthStr2int(StartMon);
			Integer iEMon = GUI_Utils.MonthStr2int(EndMon);

			Integer numMon;
			numMon = (EndYr - StartYr) * 12 + (iEMon - iSMon) + 1;

			double startdate = 1.0;
			double enddate = 1.0;

			if (!scen.equals("")) {

				// Make sure current run isnt in background.
				if ((new File(System.getProperty("user.dir") + "\\Run\\running.txt")).exists()) {
					JOptionPane.showMessageDialog(mainmenu, "There is currently a simulation running at this time.");

				} else if (numMon < 1) {
					JOptionPane.showMessageDialog(mainmenu, "The specified start date must be before then end date.");
				} else {

					// Disable run button
					JButton btn = (JButton) e.getSource();
					btn.setEnabled(false);
					mainmenu.revalidate();

					// *** Determine if scenario has changed.

					// Store selections
					StringBuffer sb = new StringBuffer();
					sb = GUI_Utils.GetControlValues(runsettings, sb);
					sb = GUI_Utils.GetControlValues(regulations, sb);
					sb = GUI_Utils.GetControlValues(hydroclimate, sb);
					sb = GUI_Utils.GetControlValues(demands, sb);
					sb = GUI_Utils.GetControlValues(operations, sb);
					sb = GUI_Utils.GetControlValues(facilities, sb);

					// get table values.
					final String NL = System.getProperty("line.separator");
					sb.append("DATATABLEMODELS" + NL);
					ArrayList GUILinks = new ArrayList();
					ArrayList GUITables = new ArrayList();
					GUILinks = GUI_Utils.GetGUILinks("Config\\GUI_Links2.table");
					GUITables = GUI_Utils.GetGUITables(GUILinks, "Regulations");
					sb = GUI_Utils.GetTableModelData(dTableModels, GUITables, gl, sb);
					sb.append("END DATATABLEMODELS" + NL);
					sb.append("USERDEFINEDFLAGS" + NL);
					for (int i = 0; i < RegUserEdits.length; i++) {
						if (RegUserEdits[i] != null) {
							sb.append(i + "|" + RegUserEdits[i] + NL);
						}
					}
					sb.append("END USERDEFINEDFLAGS" + NL);

					// Read existing file
					File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
					StringBuffer sbExisting = GUI_Utils.ReadScenarioFile(f);

					Boolean scensave = false;

					if (!sb.toString().equals(sbExisting.toString())) {

						/*
						 * int n = JOptionPane.showConfirmDialog(mainmenu,
						 * "Would you like to save the scenario definition? \nScenario information " +
						 * "will be saved to '" + System.getProperty("user.dir") + "\\Scenarios\\" + scen + "'",
						 * "CalLite Gui", JOptionPane.YES_NO_OPTION);
						 */
						int n = JOptionPane.showConfirmDialog(mainmenu, "Scenario selections have changed. Would you like to save the the changes?",
								"CalLite Gui", JOptionPane.YES_NO_OPTION);

						if (n == JOptionPane.YES_OPTION) {

							getScenFilename.actionPerformed(e);
							if (getScenFilename.dialogRC != 0)
								scensave = false;
							else {
								tf = (JTextField) swix.find("run_txfScen");
								scen = tf.getText();

								if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen)).exists())
									scensave = (JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + System.getProperty("user.dir")
											+ "\\Scenarios\\" + scen + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
											JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

								if (scensave = true) {
									scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
									desktop.setTitle(desktopTitle + " - " + scenFilename);
									((JTextField) swix.find("run_txfoDSS")).setText(scenFilename.substring(0, scenFilename.length() - 4) + "_DV.DSS");

									GUI_Utils.CreateNewFile(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
									f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
									try {
										FileWriter fstream = new FileWriter(f);
										BufferedWriter outobj = new BufferedWriter(fstream);
										outobj.write(sb.toString());
										outobj.close();

									} catch (Exception e1) {
										System.err.println("Error: " + e1.getMessage());
									}
								}

							}
						}

					}
					setupAndRun(scen);
					btn.setEnabled(true);
					mainmenu.revalidate();
				}

			} else {
				JFrame frame = new JFrame("Error");

				// show a joptionpane dialog using showMessageDialog
				JOptionPane.showMessageDialog(frame, "You must specify a scenario name.");

			}
		} else if (e.getActionCommand().startsWith("AC_SaveScen")) {

			boolean proceed = true;

			if (e.getActionCommand().equals("AC_SaveScenAs")) {

				// Save scenario as ...

				getScenFilename.actionPerformed(e);
				if (getScenFilename.dialogRC != 0)
					proceed = false;
				else {
					scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
					desktop.setTitle(desktopTitle + " - " + scenFilename);
					((JTextField) swix.find("run_txfoDSS")).setText(scenFilename.substring(0, scenFilename.length() - 4) + "_DV.DSS");

				}
			}
			if (proceed) {
				JTextField tf = (JTextField) swix.find("run_txfScen");
				String scen = tf.getText();
				if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen)).exists())
					proceed = (JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + System.getProperty("user.dir") + "\\Scenarios\\"
							+ scen + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

				if (proceed) {

					StringBuffer sb = new StringBuffer();
					sb = GUI_Utils.GetControlValues(runsettings, sb);
					sb = GUI_Utils.GetControlValues(regulations, sb);
					sb = GUI_Utils.GetControlValues(hydroclimate, sb);
					sb = GUI_Utils.GetControlValues(demands, sb);
					sb = GUI_Utils.GetControlValues(operations, sb);
					sb = GUI_Utils.GetControlValues(facilities, sb);

					// get table values.
					final String NL = System.getProperty("line.separator");
					sb.append("DATATABLEMODELS" + NL);
					ArrayList GUITables = new ArrayList();
					ArrayList GUILinks = new ArrayList();
					GUILinks = GUI_Utils.GetGUILinks("Config\\GUI_Links2.table");
					GUITables = GUI_Utils.GetGUITables(GUILinks, "Regulations");
					sb = GUI_Utils.GetTableModelData(dTableModels, GUITables, gl, sb);
					sb.append("END DATATABLEMODELS" + NL);
					sb.append("USERDEFINEDFLAGS" + NL);
					for (int i = 0; i < RegUserEdits.length; i++) {
						if (RegUserEdits[i] != null) {
							sb.append(i + "|" + RegUserEdits[i] + NL);
						}
					}
					sb.append("END USERDEFINEDFLAGS" + NL);

					GUI_Utils.CreateNewFile(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
					File f = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);

					try {
						FileWriter fstream = new FileWriter(f);
						BufferedWriter outobj = new BufferedWriter(fstream);
						outobj.write(sb.toString());
						outobj.close();

					} catch (Exception e1) {
						System.err.println("Error: " + e1.getMessage());
					}
				} else {
					JFrame frame = new JFrame("Error");

					// show a joptionpane dialog using showMessageDialog
					JOptionPane.showMessageDialog(frame, "You must specify a scenario name.");

				}
			}

		} else if (e.getActionCommand().startsWith("AC_LoadScen")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new ScenarioFileFilter());
			fc.setCurrentDirectory(new File(".//Scenarios"));

			String dirname = ".//Scenarios";

			int retval = fc.showOpenDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				// ... The user selected a file, get it, use it.
				File file = fc.getSelectedFile();

				RegUserEdits = GUI_Utils.SetControlValues(file, swix, dTableModels, gl);
				RegUserEdits = GUI_Utils.SetControlValues(file, swix, dTableModels, gl);

			}

		} else if (e.getActionCommand().startsWith("Reg_Copy")) {

			JTable table = (JTable) swix.find("tblRegValues");
			ActionEvent ae = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
			// table.selectAll();
			table.getActionMap().get(ae.getActionCommand()).actionPerformed(ae);

		} else if (e.getActionCommand().startsWith("Reg_Paste")) {

			// System.out.println("Clipboard contains:" +
			// TextTransfer.getClipboardContents() );
			JTable table = (JTable) swix.find("tblRegValues");
			int startRow = (table.getSelectedRows())[0];
			int startCol = (table.getSelectedColumns())[0];
			// int RowCt=table.getSelectedRows().length;
			// int ColCt=table.getSelectedColumns().length;
			try {
				String trstring = (String) (TextTransfer.getClipboardContents());
				trstring = trstring.replaceAll("(?sm)\t\t", "\t \t");
				trstring = trstring.replaceAll("(?sm)\t\n", "\t \n");
				System.out.println("String is:" + trstring);
				StringTokenizer st1 = new StringTokenizer(trstring, "\n");
				for (int i = 0; st1.hasMoreTokens(); i++)
				// for(int i=0; i < RowCt; i++)
				{
					String rowstring = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
					for (int j = 0; st2.hasMoreTokens(); j++)
					// for(int j=0;j < ColCt;j++)
					{
						String value = (String) st2.nextToken();
						if (startRow + i < table.getRowCount() && startCol + j < table.getColumnCount())
							table.setValueAt(value, startRow + i, startCol + j);
						table.repaint();
						System.out.println("Putting " + value + " at row = " + startRow + i + ", column = " + startCol + j);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} else if (e.getActionCommand().startsWith("Reg_Default")) {

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

			JButton btn = (JButton) swix.find("btnRegDef");
			btn.setEnabled(false);

		} else if (e.getActionCommand().startsWith("Op_TableEdit")) {
			TitledBorder title = null;
			JComponent component = (JComponent) e.getSource();
			if (e.getSource() instanceof JButton) {
				JButton btn = (JButton) e.getSource();
				String titlestr = btn.getText();
				titlestr = titlestr.substring(5);
				title = BorderFactory.createTitledBorder(titlestr);
			}
			String cName = component.getName();
			// CheckBox in Regulations panel changed
			JPanel pan = (JPanel) swix.find("op_panTab");
			pan.setBorder(title);
			component = (JComponent) swix.find("scrOpValues");
			JTable table = (JTable) swix.find("tblOpValues");

			component.setVisible(true);
			component.setEnabled(true);
			// String cID = cName.substring(6);
			populateDTable(cName, table, component);

			// pan.setBorder(title);
			component.setEnabled(true);
			table.setVisible(true);

		} else if (e.getActionCommand().startsWith("Op_Copy")) {

			JTable table = (JTable) swix.find("tblOpValues");
			ActionEvent ae = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
			// table.selectAll();
			table.getActionMap().get(ae.getActionCommand()).actionPerformed(ae);

		} else if (e.getActionCommand().startsWith("Op_Paste")) {

			// System.out.println("Clipboard contains:" +
			// TextTransfer.getClipboardContents() );
			JTable table = (JTable) swix.find("tblOpValues");
			int startRow = (table.getSelectedRows())[0];
			int startCol = (table.getSelectedColumns())[0];
			// int RowCt=table.getSelectedRows().length;
			// int ColCt=table.getSelectedColumns().length;
			try {
				String trstring = (String) (TextTransfer.getClipboardContents());
				trstring = trstring.replaceAll("(?sm)\t\t", "\t \t");
				trstring = trstring.replaceAll("(?sm)\t\n", "\t \n");
				System.out.println("String is:" + trstring);
				StringTokenizer st1 = new StringTokenizer(trstring, "\n");
				for (int i = 0; st1.hasMoreTokens(); i++)
				// for(int i=0; i < RowCt; i++)
				{
					String rowstring = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
					for (int j = 0; st2.hasMoreTokens(); j++)
					// for(int j=0;j < ColCt;j++)
					{
						String value = (String) st2.nextToken();
						if (startRow + i < table.getRowCount() && startCol + j < table.getColumnCount())
							table.setValueAt(value, startRow + i, startCol + j);
						table.repaint();
						System.out.println("Putting " + value + " at row = " + startRow + i + ", column = " + startCol + j);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

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
			/*
			 * } else if (e.getActionCommand().startsWith("AC_Report")) { try { dialog.setVisible(true);
			 * swix2.setActionListener(reportdialog, this); GetDSSFilename getDSSFilename0 = new GetDSSFilename(null,
			 * (JTextField) swix2.find("tfTemplateFILE"), "inp"); JButton btnFile0 = (JButton)
			 * swix2.find("btnGetTemplateFile"); btnFile0.addActionListener((ActionListener) getDSSFilename0);
			 * GetDSSFilename getDSSFilename1 = new GetDSSFilename(null, (JTextField) swix2.find("tfReportFILE1"));
			 * JButton btnFile1 = (JButton) swix2.find("btnGetReportFile1"); btnFile1.addActionListener((ActionListener)
			 * getDSSFilename1); GetDSSFilename getDSSFilename2 = new GetDSSFilename(null, (JTextField)
			 * swix2.find("tfReportFILE2")); JButton btnFile2 = (JButton) swix2.find("btnGetReportFile2");
			 * btnFile2.addActionListener((ActionListener) getDSSFilename2); GetDSSFilename getDSSFilename3 = new
			 * GetDSSFilename(null, (JTextField) swix2.find("tfReportFILE3"), "PDF"); JButton btnFile3 = (JButton)
			 * swix2.find("btnGetReportFile3"); btnFile3.addActionListener((ActionListener) getDSSFilename3); } catch
			 * (Exception e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
			 */

		} else if (e.getActionCommand().startsWith("AC_PresetClear")) {

			Component[] components = presets.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}

			components = WMA.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}

		} else if (e.getActionCommand().startsWith("AC_ShortageClear")) {

			Component[] components = shortage_flow.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					c.setSelected(false);
				}
			}		
			
		} else if (e.getActionCommand().startsWith("AC_GenReport")) {
			if (((JTextField) swix.find("tfReportFILE1")).getText().isEmpty() || ((JTextField) swix.find("tfReportFILE2")).getText().isEmpty()
					|| ((JTextField) swix.find("tfReportFILE3")).getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "You must specify the source DSS files and the output PDF file", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				Report report = null;
				// dialog.setVisible(false);
				try {
					// Create an inputstream from template file;
					FileInputStream fin = new FileInputStream(((JTextField) swix.find("tfTemplateFILE")).getToolTipText());
					BufferedReader br = new BufferedReader(new InputStreamReader(fin));
					// Open the template file
					System.out.println(((JTextField) swix.find("tfTemplateFILE")).getToolTipText());
					String theText = br.readLine() + "\n";
					theText = theText + br.readLine() + "\n";
					theText = theText + br.readLine() + "\n";
					br.readLine();
					theText = theText + "FILE_BASE\t" + ((JTextField) swix.find("tfReportFILE1")).getToolTipText() + "\n";
					br.readLine();
					theText = theText + "NAME_BASE\t" + ((JTextField) swix.find("tfReportNAME1")).getText() + "\n";
					br.readLine();
					theText = theText + "FILE_ALT\t" + ((JTextField) swix.find("tfReportFILE2")).getToolTipText() + "\n";
					br.readLine();
					theText = theText + "NAME_ALT\t" + ((JTextField) swix.find("tfReportNAME2")).getText() + "\n";
					br.readLine();
					theText = theText + "OUTFILE\t" + ((JTextField) swix.find("tfReportFILE3")).getToolTipText() + "\n";
					br.readLine();
					theText = theText + "NOTE\t\"" + ((JTextArea) swix.find("taReportNOTES")).getText() + "\"\n";
					br.readLine();
					theText = theText + "ASSUMPTIONS\t\"" + ((JTextArea) swix.find("taReportASSUMPTIONS")).getText() + "\"\n";
					br.readLine();
					theText = theText + "MODELER\t\"" + ((JTextField) swix.find("tfReportMODELER")).getText() + "\"\n";

					theText = theText + "TABLE_FONT_SIZE\t" + ((JTextField) swix.find("tfFontSize")).getText() + "\n";

					System.out.println(theText);
					String aLine = br.readLine();
					while (aLine != null) {
						theText = theText + aLine + "\n";
						aLine = br.readLine();
					}
					theText = theText + "\n";
					ByteArrayInputStream bs = new ByteArrayInputStream(theText.getBytes());
					try {
						System.out.println(theText);
						report = new Report(bs, ((JTextField) swix.find("tfReportFILE3")).getToolTipText(), desktop);
						report.execute();
						// report = new Report(bs);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		} else if (e.getActionCommand().startsWith("Rep_All")) {
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

			String cSTOR = ";Locs-";
			String cSTORIdx = ";Index-";
			Component[] components = presets.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					String cName = c.getName();
					if (cName.startsWith("ckbp")) {
						boolean b = c.isSelected();
						if (b == true) {
							cSTOR = cSTOR + c.getText().trim() + ",";
							cSTORIdx = cSTORIdx + cName + ",";
						}
					}
				}

				lstArray1[n] = QuickState() + cSTOR + cSTORIdx;

				// String[] reportNamesEG = {cDate};
				lstReports.setListData(lstArray1);
			}
			components = shortage_flow.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					String cName = c.getName();
					if (cName.startsWith("ckbp")) {
						boolean b = c.isSelected();
						if (b == true) {
							cSTOR = cSTOR + c.getText().trim() + ",";
							cSTORIdx = cSTORIdx + cName + ",";
						}
					}
				}

				lstArray1[n] = QuickState() + cSTOR + cSTORIdx;

				// String[] reportNamesEG = {cDate};
				lstReports.setListData(lstArray1);
			}
			
		}

		else if (e.getActionCommand().startsWith("Sch_Load")) {

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new DSSFileFilter());
			fc.setCurrentDirectory(new File(".//Scenarios"));

			String dirname = ".//Scenarios";

			int retval = fc.showOpenDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				// ... The user selected a file, get it, use it.
				File file = fc.getSelectedFile();

				JTextField tf = (JTextField) swix.find("schem_tfload");
				tf.setText(file.toString());

			}

		}

		else if (e.getActionCommand().startsWith("Rep_ClearList")) {

			JList lstReports = (JList) swix.find("lstReports");
			String[] lstArray1 = new String[0];
			lstReports.setListData(lstArray1);

		}

		else if (e.getActionCommand().startsWith("Rep_LoadList")) {

			String aLine;
			Vector<String> data = new Vector<String>();

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new CGRFileFilter());
			fc.setCurrentDirectory(new File(".//Config"));

			String dirname = ".//Config";
			File file = null;
			String filename = null;
			int retval = fc.showOpenDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				// ... The user selected a file, get it, use it.
				file = fc.getSelectedFile();
				filename = file.toString();

			}

			try {

				// FileInputStream fin = new FileInputStream(System.getProperty("user.dir") +
				// "\\Config\\reportlist.cgr");
				FileInputStream fin = new FileInputStream(filename);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));

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

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new CGRFileFilter());
			fc.setCurrentDirectory(new File(".//Config"));

			String dirname = ".//Config";
			File file = null;
			String filename = null;
			int retval = fc.showSaveDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				// ... The user selected a file, get it, use it.
				file = fc.getSelectedFile();
				filename = file.toString();
			}

			/*
			 * Delete existing reportlist File f = new File(System.getProperty("user.dir") +
			 * "\\Config\\reportlist.cgr"); GUI_Utils.deleteDir(f);
			 * GUI_Utils.CreateNewFile(System.getProperty("user.dir") + "\\Config\\reportlist.cgr");
			 */

			OutputStream outputStream;
			try {
				// outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\Config\\reportlist.cgr");
				outputStream = new FileOutputStream(filename);
			} catch (FileNotFoundException e2) {
				System.out.println("Cannot open " + filename);
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

		else if (e.getActionCommand().startsWith("Rep_DispAll")) {

			if (lstScenarios.getModel().getSize() == 0) {
				JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				displayCount = 0;
				JList list = (JList) swix.find("lstReports");
				for (int i = 0; i < list.getModel().getSize(); i++)
					DisplayFrame((String) (list.getModel().getElementAt(i)));

			}
		}

		else if (e.getActionCommand().startsWith("Rep_DispCur")) {

			if (lstScenarios.getModel().getSize() == 0) {
				JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
			} else if (((JList) swix.find("lstReports")).getSelectedValue() == null) {
				JOptionPane.showMessageDialog(null, "No display group selected", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				displayCount = 0;
				DisplayFrame((String) ((JList) swix.find("lstReports")).getSelectedValue());
			}

		} else if (e.getActionCommand().startsWith("Sch_NOD")) {
			JScrollPane scr = (JScrollPane) swix.find("schem_scr");
			JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
			verticalScrollBar.setValue(verticalScrollBar.getMinimum());

		} else if (e.getActionCommand().startsWith("Sch_Delta")) {
			JScrollPane scr = (JScrollPane) swix.find("schem_scr");
			JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
			verticalScrollBar.setValue((int) ((verticalScrollBar.getMaximum() - verticalScrollBar.getMinimum()) * 0.25));

		} else if (e.getActionCommand().startsWith("Sch_SOD")) {
			JScrollPane scr = (JScrollPane) swix.find("schem_scr");
			JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
			verticalScrollBar.setValue(verticalScrollBar.getMaximum());

		} else if (e.getActionCommand().startsWith("AC_Help")) {
			JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
			int selIndex = jtp.getSelectedIndex();
			String label = jtp.getTitleAt(selIndex);
			helpViewer.setCurrentID(label);
			help.setVisible(true);
		} else {
			JComponent component = (JComponent) e.getSource();
			String cName = component.getName();
			if (cName != null) {
				if (component.getName().equals("btnSetBase")) {
					if (lstScenarios.getSelectedIndex() >= 0) {
						JLabel label = (JLabel) swix.find("lblBase");
						label.setText(new File((String) lstScenarios.getSelectedValue()).getName());
						label.setToolTipText((String) lstScenarios.getSelectedValue());
					}

				} else if (component.getName().equals("btnClearScenario")) {
					((DefaultListModel) lstScenarios.getModel()).clear();
					lstScenarios.repaint();

				} else if (component.getName().equals("btnDisplayCurrent")) {

				}
			}

		}

	}

	private String QuickState() {

		String cAdd;
		cAdd = "";
		// Base, Comparison and Difference
		JRadioButton rdb = (JRadioButton) swix.find("rdbp000");
		if (rdb.isSelected()) {
			cAdd = cAdd + "Base";
		}

		rdb = (JRadioButton) swix.find("rdbp001");
		if (rdb.isSelected()) {
			cAdd = cAdd + "Comp";
		}

		rdb = (JRadioButton) swix.find("rdbp002");
		if (rdb.isSelected()) {
			cAdd = cAdd + "Diff";
		}
		// Units
		rdb = (JRadioButton) swix.find("rdbCFS");
		if (rdb.isSelected()) {
			cAdd = cAdd + ";CFS";
		} else {
			cAdd = cAdd + ";TAF";
		}

		// Date
		JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
		JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
		JSpinner spnSY = (JSpinner) swix.find("spnStartYear");
		JSpinner spnEY = (JSpinner) swix.find("spnEndYear");
		String cDate = spnSM.getValue().toString() + spnSY.getValue().toString();
		cDate = cDate + "-" + spnEM.getValue().toString() + spnEY.getValue().toString();
		cAdd = cAdd + ";" + cDate;

		// Time Series
		JCheckBox ckb = (JCheckBox) swix.find("RepckbTimeSeriesPlot");
		if (ckb.isSelected()) {
			cAdd = cAdd + ";TS";
		}

		// Exceedance Plot
		Component[] components = controls2.getComponents();
		ckb = (JCheckBox) swix.find("RepckbExceedancePlot");
		if (ckb.isSelected()) {
			String cST;
			cST = ";EX-";
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					boolean b = c.isSelected();
					if (b == true) {
						String cName = c.getText();
						// TODO Need different naming convention.
						cST = cST + "," + cName;
					}
				}
			}
			cAdd = cAdd + cST;
		}

		// Monthly Table
		ckb = (JCheckBox) swix.find("RepckbMonthlyTable");
		if (ckb.isSelected()) {
			cAdd = cAdd + ";Monthly";
		}

		// Summary Table
		components = controls3.getComponents();
		ckb = (JCheckBox) swix.find("RepckbSummaryTable");
		if (ckb.isSelected()) {
			String cST;
			cST = ";ST-";
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					boolean b = c.isSelected();
					if (b == true) {
						String cName = c.getText();
						// TODO Need different naming convention.
						cST = cST + "," + cName;
					}
				}
			}
			cAdd = cAdd + cST;
		}

		return cAdd;
	}

	public int displayCount;

	public void DisplayFrame(String displayGroup) {

		boolean doComparison = false;
		boolean doDifference = false;
		boolean doTimeSeries = false;
		boolean doBase = false;
		boolean doExceedance = false;
		boolean isCFS = false;
		boolean doMonthlyTable = false;
		boolean doSummaryTable = false;
		String exceedMonths = "";
		String summaryTags = "";
		String names = "";
		String locations = "";
		String dateRange = "";

		String[] groupParts = displayGroup.split(";");

		for (int i = 0; i < groupParts.length; i++) {
			if (groupParts[i].equals("Base"))
				doBase = true;
			if (groupParts[i].equals("Comp"))
				doComparison = true;
			else if (groupParts[i].equals("Diff"))
				doDifference = true;
			else if (groupParts[i].equals("TS"))
				doTimeSeries = true;
			else if (groupParts[i].startsWith("EX-")) {
				doExceedance = true;
				exceedMonths = groupParts[i].substring(3);
			} else if (groupParts[i].equals("CFS"))
				isCFS = true;
			else if (groupParts[i].equals("TAF"))
				isCFS = false;
			else if (groupParts[i].equals("Monthly"))
				doMonthlyTable = true;
			else if (groupParts[i].startsWith("ST-")) {
				doSummaryTable = true;
				summaryTags = groupParts[i].substring(4);
			} else if (groupParts[i].startsWith("Locs-"))
				names = groupParts[i].substring(5);
			else if (groupParts[i].startsWith("Index-"))
				locations = groupParts[i].substring(6);
			else {
				// Check to see if the groupPart parses as mmmyyyy-mmmyyy
				Pattern p = Pattern.compile("\\w\\w\\w\\d\\d\\d\\d-\\w\\w\\w\\d\\d\\d\\d");
				Matcher m = p.matcher(groupParts[i]);
				if (m.find())
					dateRange = groupParts[i];
				else
					System.out.println("Unparsed display list component - " + groupParts[i]);
			}
		}

		dss_Grabber.setIsCFS(isCFS);

		for (int i = 0; i < lstScenarios.getModel().getSize(); i++) {
			RBListItem item = (RBListItem) lstScenarios.getModel().getElementAt(i);
			if (item.isSelected())
				dss_Grabber.setBase(item.toString());
		}

		String locationNames[] = locations.split(",");
		String namesText[] = names.split(",");

		for (int i = 0; i < locationNames.length; i++) {

			dss_Grabber.setLocation(locationNames[i]);
			dss_Grabber.setDateRange(dateRange);

			TimeSeriesContainer[] primary_Results = dss_Grabber.getPrimarySeries();
			TimeSeriesContainer[] secondary_Results = dss_Grabber.getSecondarySeries();

			dss_Grabber.calcTAFforCFS(primary_Results, secondary_Results);

			TimeSeriesContainer[] diff_Results = dss_Grabber.getDifferenceSeries(primary_Results);
			TimeSeriesContainer[][] exc_Results = dss_Grabber.getExceedanceSeries(primary_Results);
			TimeSeriesContainer[][] sexc_Results = dss_Grabber.getExceedanceSeries(secondary_Results);

			JTabbedPane tabbedpane = new JTabbedPane();

			if (doSummaryTable) {
				SummaryTablePanel stp;
				if (doDifference)
					stp = new SummaryTablePanel(dss_Grabber.getTitle() + " - Difference from " + primary_Results[0].fileName, diff_Results, null,
							summaryTags, "");
				else
					stp = new SummaryTablePanel(dss_Grabber.getTitle(), primary_Results, secondary_Results, summaryTags, dss_Grabber.getSLabel());
				tabbedpane.insertTab("Summary - " + dss_Grabber.getBase(), null, stp, null, 0);
			}

			if (doMonthlyTable) {
				MonthlyTablePanel mtp;
				if (doDifference) {
					mtp = new MonthlyTablePanel(dss_Grabber.getTitle() + " - Difference from " + primary_Results[0].fileName, diff_Results, null,
							dss_Grabber, "");
				} else
					mtp = new MonthlyTablePanel(dss_Grabber.getTitle(), primary_Results, secondary_Results, dss_Grabber, dss_Grabber.getSLabel());
				tabbedpane.insertTab("Monthly - " + dss_Grabber.getBase(), null, mtp, null, 0);
			}

			Date lower = new Date();
			JSpinner m = (JSpinner) swix.find("spnStartMonth");
			JSpinner y = (JSpinner) swix.find("spnStartYear");
			lower.setTime((new Month(monthToInt((String) m.getValue()), (Integer) y.getValue())).getFirstMillisecond());

			Date upper = new Date();
			m = (JSpinner) swix.find("spnEndMonth");
			y = (JSpinner) swix.find("spnEndYear");
			upper.setTime((new Month(monthToInt((String) m.getValue()), (Integer) y.getValue()).getLastMillisecond()));

			ChartPanel1 cp3;
			if (doExceedance) {
				boolean plottedOne = false; // Check if any monthly plots were
											// done
				for (int m1 = 0; m1 < 12; m1++)
					if (exceedMonths.contains(monthNames[m1])) {
						cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (" + monthNames[m1] + ")", dss_Grabber.getYLabel(),
								exc_Results[m1], sexc_Results == null ? null : sexc_Results[m1], true, upper, lower, dss_Grabber.getSLabel());
						plottedOne = true;
						tabbedpane.insertTab("Exceedance (" + monthNames[m1] + ")", null, cp3, null, 0);
					}
				if (exceedMonths.contains("ALL") || !plottedOne) {
					cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (All months)", dss_Grabber.getYLabel(), exc_Results[13],
							sexc_Results == null ? null : sexc_Results[13], true, upper, lower, dss_Grabber.getSLabel());
					tabbedpane.insertTab("Exceedance (all)", null, cp3, null, 0);
				}
				if (exceedMonths.contains("Annual")) {
					if (dss_Grabber.originalUnits.equals("CFS")) {
						cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (Annual Total)", "Annual Total Volume (TAF)", exc_Results[12],
								sexc_Results == null ? null : sexc_Results[12], true, upper, lower, dss_Grabber.getSLabel());
						tabbedpane.insertTab("Exceedance (Annual Total)", null, cp3, null, 0);
					} else {
						JPanel panel = new JPanel();
						panel.add(new JLabel("No chart - annual totals are only calculated for flows."));
						tabbedpane.insertTab("Exceedance (Annual Total)", null, panel, null, 0);
					}
				}
			}

			ChartPanel1 cp1;
			ChartPanel1 cp2;

			if (doTimeSeries) {
				if (doBase) {
					cp2 = new ChartPanel1(dss_Grabber.getTitle(), dss_Grabber.getYLabel(), primary_Results, secondary_Results, false, upper, lower,
							dss_Grabber.getSLabel());
					tabbedpane.insertTab("Time Series", null, cp2, null, 0);

				} else if (primary_Results.length < 2) {
					JPanel panel = new JPanel();
					panel.add(new JLabel("No chart - need two or more time series."));
					tabbedpane.insertTab(doDifference ? "Difference" : "Comparison", null, panel, null, 0);
				} else {
					if (doDifference) {
						cp2 = new ChartPanel1(dss_Grabber.getTitle() + " - Difference from " + primary_Results[0].fileName, dss_Grabber.getYLabel(),
								diff_Results, null, false, upper, lower, dss_Grabber.getSLabel());
						tabbedpane.insertTab("Difference", null, cp2, null, 0);
					} else if (doComparison) {
						cp1 = new ChartPanel1(dss_Grabber.getTitle() + " - Comparison ", dss_Grabber.getYLabel(), primary_Results, secondary_Results,
								false, upper, lower, dss_Grabber.getSLabel());
						tabbedpane.insertTab("Comparison", null, cp1, null, 0);
					}
				}
			}

			// Show the frame
			JFrame frame = new JFrame();

			Container container = frame.getContentPane();
			container.add(tabbedpane);
			frame.pack();
			frame.setTitle("CalLite Results - " + namesText[i]);
			if (!(doTimeSeries || doExceedance || doMonthlyTable || doSummaryTable))
				container.add(new JLabel("Nothing to show!"));
			else
				tabbedpane.setSelectedIndex(0);
			frame.setVisible(true);
			frame.setSize(980, 700);
			frame.setLocation(displayCount * 20, displayCount * 20);
			displayCount++;

		}

		return;
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

	/**
	 * @wbp.parser.entryPoint
	 */

	@SuppressWarnings("unused")
	protected void populateDTable(String cID, final JTable t, JComponent container) {

		boolean exists = false;
		String fileName = gl.tableNameForCtrl(cID);

		if (fileName != null && fileName.length() != 0) {

			String[] files = fileName.split("[|]");
			int size = files.length;

			if (size == 1) {
				// CASE 1: 1 file specified
				fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + fileName + ".table";
				File f = new File(fileName);
				exists = f.exists();
			} else if (size == 2) {
				// CASE 2: 2 files specified
				fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[0] + ".table";
				File f = new File(fileName);
				exists = f.exists();
				if (exists) {
					fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[1] + ".table";
					f = new File(fileName);
					exists = f.exists();
					fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[0] + ".table" + "|" + System.getProperty("user.dir")
							+ "\\Default\\Lookup\\" + files[1] + ".table";
				}
			}

		}

		// File f = new File("Default\\Lookup\\" + fileName + ".table");
		// boolean exists = f.exists();
		if (!exists) {
			container.setVisible(false);
		} else {

			// int tID = Integer.parseInt(cID);
			final int tID = Integer.parseInt(gl.tableIDForCtrl(cID));
			if (dTableModels == null) {
				dTableModels = new DataFileTableModel[20];
			}
			if (dTableModels[tID] == null) {
				dTableModels[tID] = new DataFileTableModel(fileName, tID);
			}
			// dTableModels[tID].addTableModelListener(this);

			t.setModel(dTableModels[tID]);
			t.createDefaultColumnsFromModel();

			t.setRowHeight(20);
			for (int col = 0; col < t.getColumnCount(); col++) {
				t.getColumnModel().getColumn(col).setWidth(50);
			}

			t.setPreferredScrollableViewportSize(new Dimension(t.getColumnCount() * 60 + 60, t.getRowCount() * 20));

			t.setCellSelectionEnabled(true);
			t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

			t.getModel().addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e) {

					/*
					 * if (RegUserEdits == null) { RegUserEdits = new Boolean[20]; }
					 * 
					 * RegUserEdits[tID] = true;
					 */
					JButton btn = (JButton) swix.find("btnRegDef");
					btn.setEnabled(true);

				}
			});

			t.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent ke) {

					int l = t.getSelectedRow();
					int c = t.getSelectedColumn();

					// t.setValueAt(Character.toString(ke.getKeyChar()),l,c);
					// t.editCellAt(l, c);
				}
			});

			// Special handling for tiered header tables (11 columns)
			if (dTableModels[tID].getColumnCount() == 11) {

				// JFrame frame1 = new JFrame();

				/*
				 * JTable table = new JTable(); table.setColumnModel(new GroupableTableColumnModel());
				 * table.setTableHeader(new GroupableTableHeader((GroupableTableColumnModel) table.getColumnModel()));
				 * table.setModel(dTableModels[tID]); table.createDefaultColumnsFromModel();
				 * 
				 * 
				 * GroupableTableColumnModel cm = (GroupableTableColumnModel)table.getColumnModel();
				 * 
				 * ColumnGroup g_Wet = new ColumnGroup("Wet"); g_Wet.add(cm.getColumn(1)); g_Wet.add(cm.getColumn(2));
				 * ColumnGroup g_AN = new ColumnGroup("Above Normal"); g_AN.add(cm.getColumn(3));
				 * g_AN.add(cm.getColumn(4)); ColumnGroup g_BN = new ColumnGroup("Below Normal");
				 * g_BN.add(cm.getColumn(5)); g_BN.add(cm.getColumn(6)); ColumnGroup g_DRY = new ColumnGroup("Dry");
				 * g_DRY.add(cm.getColumn(7)); g_DRY.add(cm.getColumn(8)); ColumnGroup g_CD = new
				 * ColumnGroup("Critical Dry"); g_CD.add(cm.getColumn(9)); g_CD.add(cm.getColumn(10));
				 * 
				 * 
				 * GroupableTableHeader h = (GroupableTableHeader)table.getTableHeader(); h.addColumnGroup(g_Wet);
				 * h.addColumnGroup(g_AN); h.addColumnGroup(g_BN); h.addColumnGroup(g_DRY); h.addColumnGroup(g_CD);
				 * JScrollPane scroll = new JScrollPane( table );
				 * 
				 * //frame1.add(scroll); //frame1.pack(); //frame1.setVisible(true);
				 */

			}

			t.revalidate();

			ExcelAdapter myAd = new ExcelAdapter(t);

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		JComponent component = (JComponent) e.getComponent();
		String cName = component.getName();
		if (cName != null) {
			if (cName.startsWith("ckbReg")) {
				// Right Click only
				if (e.getButton() == MouseEvent.BUTTON3) {

					JCheckBox selcomp = (JCheckBox) e.getComponent();
					Boolean isSelect = selcomp.isSelected();

					SetRegCheckBoxes(cName, isSelect);
				}
			} else if (cName.startsWith("fac_ckb")) {
				// Right Click only
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPanel panel = (JPanel) swix.find("fac_pan" + cName.substring(7));
					// set all "data" panels to invisible
					GUI_Utils.ToggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUI_Utils.ToggleVisComponent(panel, true);

					JCheckBox ckb = (JCheckBox) component;
					GUI_Utils.ToggleEnComponentAndChildren(panel, ckb.isSelected());
				}
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {

		// Handles mouse presses for results tabs
		
		JComponent component = (JComponent) e.getComponent();
		String cName = component.getName();

		int button = e.getButton();
		Integer iClickCount = e.getClickCount();

		// Right click

		if (button != MouseEvent.NOBUTTON && button != MouseEvent.BUTTON1) {
			if (cName.startsWith("ckbp")) {

				// ----- Quick Results: HANDLE DISPLAY OF SINGLE VARIABLE -----

				menu.setCursor(hourglassCursor);

				if (lstScenarios.getModel().getSize() == 0) {
					JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
				} else {

					JCheckBox chk = (JCheckBox) component;
					DisplayFrame(QuickState() + ";Locs-" + chk.getText() + ";Index-" + chk.getName());
					menu.setCursor(normalCursor);
				}
			}
		} else {

			// Double Click

			if (iClickCount == 2) {
				if (cName != null) {

					if (cName.startsWith("schem_map")) {
						if (((JTextField) swix.find("schem_tfload")).getText().equals("")) {
							JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
						} else {
							Point b = e.getPoint();
							int x = (int) b.getX();
							int y = (int) b.getY();

							if ((x - 111) * (x - 111) + (y - 150) * (y - 150) < 100) {
								JFrame mapPlotFrame = new JFrame("Example map plot");
								dss_Grabber.setLocation("102");

								TimeSeriesContainer[] primary_Results = new TimeSeriesContainer[1];
								primary_Results[0] = dss_Grabber.getOneSeries(((JTextField) swix.find("schem_tfload")).getText(),
										dss_Grabber.primaryDSSName);

								Date lower = new Date();
								JSpinner m = (JSpinner) swix.find("spnStartMonth");
								JSpinner y1 = (JSpinner) swix.find("spnStartYear");
								lower.setTime((new Month(monthToInt((String) m.getValue()), (Integer) y1.getValue())).getFirstMillisecond());

								Date upper = new Date();
								m = (JSpinner) swix.find("spnEndMonth");
								y1 = (JSpinner) swix.find("spnEndYear");
								upper.setTime((new Month(monthToInt((String) m.getValue()), (Integer) y1.getValue()).getLastMillisecond()));

								ChartPanel1 cp1 = new ChartPanel1(dss_Grabber.getTitle(), dss_Grabber.getYLabel(), primary_Results, null, false,
										upper, lower, dss_Grabber.getSLabel());
								mapPlotFrame.add(cp1);
								mapPlotFrame.pack();
								mapPlotFrame.setVisible(true);

							}

							else
								JOptionPane.showMessageDialog(null, "X = " + x + "; Y = " + y);
						}
					}
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void menuCanceled(MenuEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void menuDeselected(MenuEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void menuSelected(MenuEvent e) {
		String cName = ((JMenu) e.getSource()).getName();
		if (cName != null) {
			if (cName.startsWith("menuR")) {
				JPanel p = (JPanel) swix.find("settings");
				p.setVisible(false);

				p = (JPanel) swix.find("results");
				p.setVisible(true);

			} else if (cName.startsWith("menuS")) {
				JPanel p = (JPanel) swix.find("results");
				p.setVisible(false);

				p = (JPanel) swix.find("settings");
				p.setVisible(true);
			}
		}
		// System.out.print("Clicked! " + ((JMenu) e.getSource()).getText());
		// //action depending on text/name ...
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		Component c = (Component) changeEvent.getSource();
		if (c.getName().toLowerCase().substring(0, 3).equals("spn")) {

			// Constrain run times to [10/1921,9/2003]
			int syr = (Integer) ((JSpinner) swix.find("spnRunStartYear")).getValue();
			int eyr = (Integer) ((JSpinner) swix.find("spnRunEndYear")).getValue();
			int smo = monthToInt(((String) ((JSpinner) swix.find("spnRunStartMonth")).getValue()).trim());
			int emo = monthToInt(((String) ((JSpinner) swix.find("spnRunEndMonth")).getValue()).trim());
			if ((syr == 1921) && (smo < 10))
				((JSpinner) swix.find("spnRunStartMonth")).setValue("Oct");
			if ((eyr == 2003) && (emo > 9))
				((JSpinner) swix.find("spnRunEndMonth")).setValue("Sep");

		} else {

			JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
			int index = sourceTabbedPane.getSelectedIndex();
			String cName = sourceTabbedPane.getTitleAt(index);
			if (cName.startsWith("D-1641")) {
				JButton btn = (JButton) swix.find("btnRegDef");
				btn.setVisible(false);
				JRadioButton rdb = (JRadioButton) swix.find("reg_rdbD1641");
				rdb.setVisible(true);
				rdb = (JRadioButton) swix.find("reg_rdbUD");
				rdb.setVisible(true);
			} else {
				JButton btn = (JButton) swix.find("btnRegDef");
				btn.setVisible(true);
				JRadioButton rdb = (JRadioButton) swix.find("reg_rdbD1641");
				rdb.setVisible(false);
				rdb = (JRadioButton) swix.find("reg_rdbUD");
				rdb.setVisible(false);
			}
		}

	}

	private int readInLookups() {

		// Open input file

		Scanner input;
		try {
			input = new Scanner(new FileReader("Config\\GUI_Links3.table"));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot open input file Config\\GUI_Links3.table");
			return -1;
		}

		Vector<String> allLookups = new Vector<String>();

		int lineCount = 0;
		input.nextLine(); // Skip header line
		while (input.hasNextLine()) {
			String line = input.nextLine();
			allLookups.add(line);
			lineCount++;
		}
		input.close();
		lookups = new String[lineCount][6];
		for (int i = 0; i < lineCount; i++) {
			String[] parts = allLookups.get(i).split("[\t]+");
			for (int j = 0; j < 6; j++) {
				if (parts[j].equals("null"))
					parts[j] = "";
				lookups[i][j] = parts[j];
			}
			if (lookups[i][1].equals("")) {
				JCheckBox cb = (JCheckBox) swix.find("ckbp" + lookups[i][0]);
				cb.setEnabled(false);
			}
		}

		return 0;
	}

	public static String getLookups(int i, int j) {
		return lookups[i][j];
	}

	public static int getLookupsLength() {
		return lookups.length;
	}

	public void SetRegCheckBoxes(String cName, Boolean isSelect) {

		JPanel pan = (JPanel) swix.find("reg_panTab");
		TitledBorder title;
		JComponent scr = (JComponent) swix.find("scrRegValues");
		JTable table = (JTable) swix.find("tblRegValues");
		if (scr != null)
			scr.setEnabled((isSelect));

		JCheckBox selcomp = (JCheckBox) swix.find(cName);
		if (isSelect) {

			GUI_Utils.ToggleEnComponentAndChildren(pan, true);
			scr.setVisible(true);
			scr.setEnabled(true);
			String cID = cName;
			populateDTable(cID, table, scr);

			JButton btn = (JButton) swix.find("btnRegDef");
			btn.setEnabled(false);

			JRadioButton rdb = (JRadioButton) swix.find("reg_rdbD1641");
			if (rdb.isVisible()) {
				if (RegUserEdits != null && dTableModels != null) {
					DataFileTableModel tm = (DataFileTableModel) table.getModel();
					int tID = tm.tID;
					if (RegUserEdits[tID] != null) {
						reg_btng1.clearSelection();
						if (RegUserEdits[tID] == true) {
							rdb = (JRadioButton) swix.find("reg_rdbUD");
							rdb.setSelected(true);
						} else {
							rdb = (JRadioButton) swix.find("reg_rdbD1641");
							rdb.setSelected(true);
						}
					} else {
						reg_btng1.clearSelection();
						rdb = (JRadioButton) swix.find("reg_rdbD1641");
						rdb.setSelected(true);
					}
				} else {
					reg_btng1.clearSelection();
					rdb = (JRadioButton) swix.find("reg_rdbD1641");
					rdb.setSelected(true);
				}
			} else {
				if (RegUserEdits == null) {
					RegUserEdits = new Boolean[20];
				}
				String fileName = gl.tableNameForCtrl(cID);
				if (!fileName.trim().equals("")) {
					int tID = Integer.parseInt(gl.tableIDForCtrl(cID));
					RegUserEdits[tID] = true;
				}

				table.setCellSelectionEnabled(true);
				table.setEnabled(true);
			}

			String ckbtext = selcomp.getText();
			String[] ckbtext1 = ckbtext.split(" - ");
			ckbtext = ckbtext1[0];
			title = BorderFactory.createTitledBorder(ckbtext);

			pan.setBorder(title);
			pan.setEnabled(true);
			scr.setEnabled(true);
			table.setVisible(true);
			pan.revalidate();

		} else {

			GUI_Utils.ToggleEnComponentAndChildren(pan, false);

			JCheckBox ckb = (JCheckBox) (JCheckBox) swix.find(cName);
			String ckbtext = ckb.getText();
			String[] ckbtext1 = ckbtext.split(" - ");
			ckbtext = ckbtext1[0];
			ckb.setText(ckbtext);

			pan.setEnabled(false);
			title = BorderFactory.createTitledBorder(selcomp.getText() + " (not selected)");
			pan.setBorder(title);
			scr.setEnabled(false);
			table.setVisible(false);
			pan.revalidate();

		}

	}

	public int monthToInt(String EndMon) {
		int iEMon = 0;

		if (EndMon.equals("Apr")) {
			iEMon = 4;
		} else if (EndMon.equals("Jun")) {
			iEMon = 6;
		} else if (EndMon.equals("Sep")) {
			iEMon = 9;
		} else if (EndMon.equals("Nov")) {
			iEMon = 11;
		} else if (EndMon.equals("Feb")) {
			iEMon = 2;
		} else if (EndMon.equals("Jan")) {
			iEMon = 1;
		} else if (EndMon.equals("Mar")) {
			iEMon = 3;
		} else if (EndMon.equals("May")) {
			iEMon = 5;
		} else if (EndMon.equals("Jul")) {
			iEMon = 7;
		} else if (EndMon.equals("Aug")) {
			iEMon = 8;
		} else if (EndMon.equals("Oct")) {
			iEMon = 10;
		} else if (EndMon.equals("Dec")) {
			iEMon = 12;
		}
		return iEMon;
	}

	/*
	 * setupAndRun - method to do setup in alternate thread
	 */
	ProgressFrame pFrame;

	public void setupAndRun(final String scen) {

		pFrame = new ProgressFrame("CalLite 2.0 GUI - Setting Up Run");

		SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

			protected void done() {

				if (pFrame != null) {
					pFrame.setCursor(null);
					pFrame.dispose();
				}

				desktop.setEnabled(true);
				desktop.setVisible(true);
				return;

			}

			protected void process(List<String> status) {

				pFrame.setText(status.get(status.size() - 1));
				return;
			}

			@Override
			protected Void doInBackground() throws Exception {

				// Copy Run directory

				pFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				desktop.setEnabled(false);

				publish("Creating new Run directory.");

				File fs = new File(System.getProperty("user.dir") + "\\Default");
				File ft = new File(System.getProperty("user.dir") + "\\Run");
				// First delete existing Run directory.
				GUI_Utils.deleteDir(ft);

				try {
					GUI_Utils.copyDirectory(fs, ft, false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Copy lookup files.
				fs = new File(System.getProperty("user.dir") + "\\Default\\Lookup");
				ft = new File(System.getProperty("user.dir") + "\\Run\\Lookup");
				try {
					GUI_Utils.copyDirectory(fs, ft, false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Development flag
				Integer LODFlag = 0;
				JRadioButton rdb = (JRadioButton) swix.find("hyd_rdb2005");
				if (rdb.isSelected()) {
					LODFlag = 0;
				} else {
					LODFlag = 1;
				}

				// Copy DSS files.
				ft = new File(System.getProperty("user.dir") + "\\Run\\DSS");
				ft.mkdir();
				if (LODFlag == 0) {
					fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\CL2005A01A021411_SV.DSS");
					ft = new File(System.getProperty("user.dir") + "\\Run\\DSS\\CL2005A01A021411_SV.DSS");
					try {
						GUI_Utils.copyDirectory(fs, ft, false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\CalLite2005A01AINIT.DSS");
					ft = new File(System.getProperty("user.dir") + "\\Run\\DSS\\CalLite2005A01AINIT.DSS");
					try {
						GUI_Utils.copyDirectory(fs, ft, false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\CL_FUTURE_WHL021111_SV.DSS");
					ft = new File(System.getProperty("user.dir") + "\\Run\\DSS\\CL_FUTURE_WHL021111_SV.DSS");
					try {
						GUI_Utils.copyDirectory(fs, ft, false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					fs = new File(System.getProperty("user.dir") + "\\Default\\DSS\\CALLITE2020D09EINIT.DSS");
					ft = new File(System.getProperty("user.dir") + "\\Run\\DSS\\CALLITE2020D09EINIT.DSS");
					try {
						GUI_Utils.copyDirectory(fs, ft, false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				publish("Writing GUI tables.");

				// Get GUI Link Array
				ArrayList GUILinks = new ArrayList();
				GUILinks = GUI_Utils.GetGUILinks("Config\\GUI_Links2.table");

				// Write GUI Tables

				try {
					GUI_Utils.WriteGUITables(GUILinks, RegUserEdits, swix);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				publish("Copying demand tables.");

				/*
				 * GUI_Utils .ReplaceLineInFile(System.getProperty("user.dir") + "\\Run\\Lookup\\options.table", 13,
				 * "9       " + LODFlag + "   !Level of Development, LOD_Future = 1 for future and 0 for existing" );
				 */

				// Copy 2005/2030 lookup tables
				// pMon.setNote("Copying lookup tables...");
				// pMon.setProgress(30);
				File fsDem;
				/*
				 * rdb = (JRadioButton) swix.find("dem_rdbCurSWP"); if (rdb.isSelected()) { fsDem = new
				 * File(System.getProperty("user.dir") + "\\Run\\Lookup\\FutureDemand"); } else { fsDem = new
				 * File(System.getProperty("user.dir") + "\\Run\\Lookup\\VariableDemand"); } GUI_Utils.deleteDir(fsDem);
				 */

				rdb = (JRadioButton) swix.find("dem_rdbCurSWP");

				if (rdb.isSelected()) {
					fsDem = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\VariableDemand");
				} else {
					fsDem = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\FutureDemand");
				}

				File fsLookup = new File(System.getProperty("user.dir") + "\\Run\\Lookup");

				GUI_Utils.copyDirectory(fsDem, fsLookup, true);

				publish("Creating study.sty.");

				// Write study.sty
				Calendar cal = Calendar.getInstance();

				JSpinner spn = (JSpinner) swix.find("spnRunStartMonth");
				String StartMon = (String) spn.getValue();
				StartMon = StartMon.trim();
				spn = (JSpinner) swix.find("spnRunEndMonth");
				String EndMon = (String) spn.getValue();
				EndMon = EndMon.trim();
				spn = (JSpinner) swix.find("spnRunStartYear");
				Integer StartYr = (Integer) spn.getValue();
				spn = (JSpinner) swix.find("spnRunEndYear");
				Integer EndYr = (Integer) spn.getValue();

				// Determine Month/Count
				Integer dayct = GUI_Utils.DaysinMonth(StartMon);
				Integer iSMon = GUI_Utils.MonthStr2int(StartMon);
				Integer iEMon = GUI_Utils.MonthStr2int(EndMon);

				Integer numMon;
				numMon = (EndYr - StartYr) * 12 + (iEMon - iSMon) + 1;
				StartMon = StartMon.toUpperCase();

				JTextField tf = (JTextField) swix.find("run_txfoDSS");
				String oDSS = tf.getText().trim();

				String[] newtext = new String[20];
				Integer[] LineNum = new Integer[20];

				newtext[0] = scen;
				LineNum[0] = 2;
				newtext[1] = cal.getTime().toString();
				LineNum[1] = 4;
				newtext[2] = System.getProperty("user.dir") + "\\Run";
				LineNum[2] = 7;
				newtext[3] = System.getProperty("user.dir") + "\\Run\\CALLITE_BO_FUTURE.STY";
				LineNum[3] = 8;
				newtext[4] = System.getProperty("user.dir") + "\\Run\\MAIN_BO.WRESL";
				LineNum[4] = 9;
				if (oDSS.toUpperCase().endsWith(".DSS")) {
					newtext[6] = System.getProperty("user.dir") + "\\Scenarios\\" + oDSS;
					LineNum[6] = 11;
				} else {
					newtext[6] = System.getProperty("user.dir") + "\\Scenarios\\" + oDSS + ".DSS";
					LineNum[6] = 11;
				}

				if (LODFlag == 0) {
					newtext[5] = System.getProperty("user.dir") + "\\Run\\DSS\\CL2005A01A021411_SV.DSS";
					LineNum[5] = 10;
					newtext[7] = System.getProperty("user.dir") + "\\Run\\DSS\\CalLite2005A01AINIT.DSS";
					LineNum[7] = 12;
				} else {
					newtext[5] = System.getProperty("user.dir") + "\\Run\\DSS\\CL_FUTURE_WHL021111_SV.DSS";
					LineNum[5] = 10;
					newtext[7] = System.getProperty("user.dir") + "\\Run\\DSS\\CALLITE2020D09EINIT.DSS";
					LineNum[7] = 12;
				}

				newtext[8] = numMon.toString();
				LineNum[8] = 14;
				newtext[9] = dayct.toString();
				LineNum[9] = 15;
				newtext[10] = StartMon;
				LineNum[10] = 16;
				newtext[11] = StartYr.toString();
				LineNum[11] = 17;

				LineNum[12] = 33;
				LineNum[13] = 34;
				if (LODFlag == 0) {
					newtext[12] = "2005A01A";
					newtext[13] = "2005A01A";
				} else {
					newtext[12] = "2020D09E";
					newtext[13] = "2020D09E";
				}

				GUI_Utils.ReplaceLinesInFile(System.getProperty("user.dir") + "\\Run\\study.sty", LineNum, newtext);

				pFrame.setText("Copying DLL.");

				// Sea Level Selections
				File fsAnnO;
				File fsAnnS;
				JRadioButton rdbSLR45 = (JRadioButton) swix.find("hyd_rdb1");
				JRadioButton rdbSLR15 = (JRadioButton) swix.find("hyd_rdb2");
				if (rdbSLR45.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BDCP_LLT_45cm.dll");
					fsAnnO = new File(System.getProperty("user.dir") + "\\Model\\Ann.dll");
				} else if (rdbSLR15.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BDCP_ELT_15cm.dll");
					fsAnnO = new File(System.getProperty("user.dir") + "\\Model\\Ann.dll");
				} else {
					fsAnnS = new File(System.getProperty("user.dir") + "\\Default\\External\\Ann7inp_BST_noSLR_111709.dll");
					fsAnnO = new File(System.getProperty("user.dir") + "\\Model\\Ann.dll");
				}
				try {
					GUI_Utils.copyDirectory(fsAnnS, fsAnnO, true);
				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
				}

				publish("Writing GUI option tables.");

				// Write table files
				ArrayList GUITables = new ArrayList();
				GUITables = GUI_Utils.GetGUITables(GUILinks, "Regulations");

				for (int i = 0; i < GUITables.size(); i++) {
					System.out.println(i);
					String line = GUITables.get(i).toString();
					String[] parts = line.split("[|]");
					String cName = parts[0].trim(); // Get name of controlling
													// checkbox;
					String tableName = gl.tableNameForCtrl(cName); // Find the
																	// corresponding
																	// table
					String switchID = gl.switchIDForCtrl(cName); // Get the
																	// switchID
																	// (index in
																	// .table
																	// file)

					int tID = Integer.parseInt(gl.tableIDForCtrl(cName));

					int option = 0;
					JCheckBox cb = (JCheckBox) swix.find(cName);
					if (cb == null) {
						option = 0;
					} else if (!cb.isSelected()) {
						option = 0;
					} else {
						option = 1;
					}
					System.out.println(switchID + " " + option);

					if ((option == 2) || (option == 1)) {

						String[] files = tableName.split("[|]");
						int size = files.length;
						if (size == 1) {
							// CASE 1: 1 file specified
							System.out.println("Output to " + tableName);
							String fo = System.getProperty("user.dir") + "\\Run\\Lookup\\" + tableName + ".table";

							if (dTableModels[tID] == null) {
								System.out.println("Table not initialized - " + tableName);
							} else {
								dTableModels[tID].writeToFile(tableName);
							}
						} else if (size == 2) {
							// CASE 2: 2 files specified
							System.out.println("Output to " + files[0]);
							String fo1 = System.getProperty("user.dir") + "\\Run\\Lookup\\" + files[0] + ".table";
							String fo2 = System.getProperty("user.dir") + "\\Run\\Lookup\\" + files[1] + ".table";

							if (dTableModels[tID] == null) {
								System.out.println("Table not initialized");
							} else {
								dTableModels[tID].writeToFile2(files[0], files[1]);
							}

						}

					}
				}

				desktop.setVisible(false);

				// "Run" model

				try {

					pFrame.setCursor(null);
					pFrame.dispose();

					Runtime rt = Runtime.getRuntime();
					Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\CalLite.BAT");
					int exitVal = proc.waitFor();
					System.out.println("Process exitValue: " + exitVal);
				} catch (Throwable t) {
					JOptionPane.showMessageDialog(null, t.getMessage());
					t.printStackTrace();
				}
				return null;
			}
		};

		worker.execute();
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Changed");
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Added");
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Removed");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent arg0) {
		if (arg0.getID() == KeyEvent.KEY_PRESSED) {
			if (arg0.getKeyCode() == KeyEvent.VK_F1) {
				JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
				int selIndex = jtp.getSelectedIndex();
				String label = jtp.getTitleAt(selIndex);
				helpViewer.setCurrentID(label);
				help.setVisible(true);
			}
		}
		return false;
	}

}