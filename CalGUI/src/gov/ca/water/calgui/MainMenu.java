package gov.ca.water.calgui;

import gov.ca.water.calgui.results.DSSGrabber;
import gov.ca.water.calgui.results.SchematicMain;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.varia.NullAppender;
import org.swixml.SwingEngine;

public class MainMenu implements ActionListener, ItemListener, MouseListener, TableModelListener, MenuListener, ChangeListener,
        ListDataListener, KeyEventDispatcher {
	private final SwingEngine swix;

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
	JPanel externalPDF;
	JPanel reportdialog;
	JPanel facilities;
	JPanel operations;
	JPanel Display;
	JPanel controls2;
	JPanel controls3;
	JPanel presets;
	JPanel shortage;
	JPanel delta_flow_criteria;
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
	static String table4[][]; // Holds GUI_links4.table values that control
	                          // selection of SV and Init DSS as well as
	                          // WSI_DI
	// files
	static String table5[][]; // Holds DSS Schematic link values
	// files

	int action_WSIDI = 0; // 0 = NO PROMPT, 1 = NORMAL, 2 = UNDO

	// configuration. Set False initially

	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	JMenuBar menu;
	ProgressMonitor pMon;
	public JList lstScenarios;

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

		// Set GUI visuals
		desktopTitle = desktop.getTitle() + ";  Scenario";
		desktop.setResizable(false);

		// Set Icon
		java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		desktop.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

		// Title
		scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
		desktop.setTitle(desktopTitle + " - " + scenFilename);
		getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");

		// Help hotkey
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);

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

		// Code added to disable logger
		System.setProperty("log4j.defaultInitOverride", "true");
		LogManager.resetConfiguration();
		LogManager.getRootLogger().addAppender(new NullAppender());

		// Read Schematic_DSS_link4.table and place in Table5
		ArrayList GUILinks5 = new ArrayList();
		GUILinks5 = GUIUtils.GetGUILinks("Config\\Schematic_DSS_link4.table");
		table5 = new String[GUILinks5.size()][6];
		for (int i = 0; i < GUILinks5.size(); i++) {
			String tokens[] = ((String) GUILinks5.get(i)).split("\t");
			table5[i][0] = tokens[0];
			table5[i][1] = tokens[1];
			table5[i][2] = tokens[2];
			table5[i][3] = tokens[3];
			table5[i][4] = tokens[4];
			table5[i][5] = tokens[5];
		}

		// Read Schematic_DSS_link4.table and place in Table4 (for assigning SV,
		// init file, etc.)
		ArrayList GUILinks4 = new ArrayList();
		GUILinks4 = GUIUtils.GetGUILinks("Config\\GUI_Links4.table");
		table4 = new String[GUILinks4.size()][5];
		for (int i = 0; i < GUILinks4.size(); i++) {
			String tokens[] = ((String) GUILinks4.get(i)).split("\t");
			table4[i][0] = tokens[0] + tokens[1] + tokens[2] + tokens[3];
			table4[i][1] = tokens[4];
			table4[i][2] = tokens[5];
			table4[i][3] = tokens[6];
			table4[i][4] = tokens[7];
		}

		// Set up month spinners
		JSpinner spnSM1 = (JSpinner) swix.find("spnRunStartMonth");
		SpinnerSetup.SetMonthModelAndIndex(spnSM1, 9, this, true);
		JSpinner spnEM1 = (JSpinner) swix.find("spnRunEndMonth");
		SpinnerSetup.SetMonthModelAndIndex(spnEM1, 8, this, true);

		// Set up year spinners
		JSpinner spnSY1 = (JSpinner) swix.find("spnRunStartYear");
		SpinnerSetup.SetNumberModelAndIndex(spnSY1, 1921, 1921, 2003, 1, "####", this, true);
		JSpinner spnEY1 = (JSpinner) swix.find("spnRunEndYear");
		SpinnerSetup.SetNumberModelAndIndex(spnEY1, 2003, 1921, 2003, 1, "####", this, true);

		// Read switch lookup
		gl = new GUILinks();
		gl.readIn("Config\\GUI_Links2.table");

		readInLookups(); // Temporary access to quick reports info from
		                 // gui_Links3.table

		// Setup for Reporting page

		// Set up scenario list
		lstScenarios = (JList) swix.find("SelectedList");

		JRadioButton rdb1 = (JRadioButton) swix.find("rdbp001");
		JRadioButton rdb2 = (JRadioButton) swix.find("rdbp002");

		GetDSSFilename getDSSFilename = new GetDSSFilename(lstScenarios, (JLabel) swix.find("lblBase"), rdb1, rdb2);
		lstScenarios.setModel(getDSSFilename.lmScenNames);
		lstScenarios.setBorder(new LineBorder(Color.gray, 1));

		JButton btnScenario = (JButton) swix.find("btnAddScenario");
		btnScenario.addActionListener(getDSSFilename);

		JButton btnScenarioDel = (JButton) swix.find("btnDelScenario");
		btnScenarioDel.addActionListener(getDSSFilename);

		JButton btnClearAll = (JButton) swix.find("btnClearScenario");
		btnClearAll.addActionListener(this);

		JButton btnReport = (JButton) swix.find("btnReport");
		// btnReport.addActionListener(this);

		// Set up month spinners
		JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
		SpinnerSetup.SetMonthModelAndIndex(spnSM, 9, this, true);
		JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
		SpinnerSetup.SetMonthModelAndIndex(spnEM, 8, this, true);

		// Set up year spinners
		// TODO - Get years from scenarios rather than fixed
		// TODO (?) - Control spinner so end year >= start year
		JSpinner spnSY = (JSpinner) swix.find("spnStartYear");
		SpinnerSetup.SetNumberModelAndIndex(spnSY, 1921, 1921, 2003, 1, "####", this, true);
		JSpinner spnEY = (JSpinner) swix.find("spnEndYear");
		SpinnerSetup.SetNumberModelAndIndex(spnEY, 2003, 1921, 2003, 1, "####", this, true);

		// Set up report list
		JList lstReports = (JList) swix.find("lstReports");
		lstReports.setBorder(new LineBorder(Color.gray, 1));
		lstReports.setVisible(true);

		// Load Default Scenario
		JTextField tf = (JTextField) swix.find("run_txfScen");
		String scen = tf.getText();

		File file = new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen);
		action_WSIDI = 0;
		RegUserEdits = GUIUtils.SetControlValues(file, swix, dTableModels, gl);
		action_WSIDI = 1;

		JPanel pan = (JPanel) swix.find("op_panTab");
		JComponent component1 = (JComponent) swix.find("scrOpValues");
		JTable table1 = (JTable) swix.find("tblOpValues");

		component1.setVisible(true);
		component1.setEnabled(true);
		PopulateDTable.populate("op_btn1", table1, component1, swix, RegUserEdits, dTableModels, gl);
		PopulateDTable.populate("op_btn2", table1, component1, swix, RegUserEdits, dTableModels, gl);

		// pan.setBorder(title);
		component1.setVisible(false);
		table1.setVisible(false);

		// Refresh checkbox labels
		for (int i = 0; i < RegUserEdits.length; i++) {
			if (RegUserEdits[i] != null) {

				String cID = Integer.toString(i);
				String cName = gl.CtrlFortableID(cID);

				JComponent c = (JComponent) swix.find(cName);

				if (c instanceof JCheckBox) {
					JCheckBox ckb = (JCheckBox) c;
					String ckbtext = ckb.getText();
					String[] ckbtext1 = ckbtext.split(" - ");
					ckbtext = ckbtext1[0];
					if (ckbtext1.length > 0) {
						if (RegUserEdits[i] == true)
							ckb.setText(ckbtext + " - User Def.");
						else
							ckb.setText(ckbtext + " -  Default");

					} else
						ckb.setText(ckbtext);
				}
			}
		}

		/*
		 * String cName = gl.CtrlFortableID(tokens[0]); JCheckBox comp1 = (JCheckBox) swix.find("cName"); comp1.setSelected(true);
		 * RegUserEdits=GUI_Utils.SetControlValues(file, swix, dTableModels, gl);
		 */

		// Schematic view
		JPanel schematicPanel = (JPanel) swix.find("schematic_holder");
		SchematicMain schemView = new SchematicMain(schematicPanel, "file:///" + System.getProperty("user.dir")
		        + "/Config/callite.svg", this, swix);
		// schemView.setAffineTransform(0.5716912122078099, 0.0, 0.0,
		// 0.5716912122078099, -114.55489341333396,
		// 0.5477924346923828);
		// schemView.setAffineTransform(9.1666667, 0.0, 0.0, 0.1666667, 320.0,
		// 0.0);

		// // Load in WRIMS functionality

		// java.net.URL imgURL =
		// getClass().getResource("/images/CalLITE_08_30corrected10-21-10.jpg");
		// if (imgURL != null) {
		// // ImageIcon image = new ImageIcon(imgURL, null);
		// // System.out.println(image.getIconHeight());
		//
		// BufferedImage img;
		// img = ImageIO.read(imgURL);
		// Font defaultFont = new Font("Serif", Font.PLAIN, 20);
		// SymbolCanvas symbols = new SymbolCanvas(defaultFont, 0x2500, 207,
		// img);
		// symbols.paint(img.getGraphics());
		//
		// ImageIcon image = new ImageIcon(img, null);
		//
		// ScrollablePicture picture = new ScrollablePicture(image, 100);
		// picture.setName("schem_map");
		// JScrollPane scrollpane = (JScrollPane) swix.find("schem_scr");
		// scrollpane.add(picture);
		// scrollpane.setViewportView(picture);
		// scrollpane.setPreferredSize(new Dimension(800, 700));
		// scrollpane.setOpaque(true);
		// scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//
		// GUI_Utils.SetMouseListener(scrollpane, this);
		//
		// }

		// PDF Report
		GetDSSFilename getDSSFilename0 = new GetDSSFilename(null, (JTextField) swix.find("tfTemplateFILE"), "inp");
		JButton btnFile0 = (JButton) swix.find("btnGetTemplateFile");
		btnFile0.addActionListener(getDSSFilename0);
		GetDSSFilename getDSSFilename1 = new GetDSSFilename(null, (JTextField) swix.find("tfReportFILE1"));
		JButton btnFile1 = (JButton) swix.find("btnGetReportFile1");
		btnFile1.addActionListener(getDSSFilename1);
		GetDSSFilename getDSSFilename2 = new GetDSSFilename(null, (JTextField) swix.find("tfReportFILE2"));
		JButton btnFile2 = (JButton) swix.find("btnGetReportFile2");
		btnFile2.addActionListener(getDSSFilename2);
		GetDSSFilename getDSSFilename3 = new GetDSSFilename(null, (JTextField) swix.find("tfReportFILE3"), "PDF");
		JButton btnFile3 = (JButton) swix.find("btnGetReportFile3");
		btnFile3.addActionListener(getDSSFilename3);

		// Set Listeners
		swix.setActionListener(menu, new FileAction(desktop, swix, RegUserEdits, dTableModels, gl, action_WSIDI));
		GUIUtils.SetMenuListener(menu, this);

		swix.setActionListener(regulations, new RegAction(swix, RegUserEdits));
		GUIUtils.SetCheckBoxorRadioButtonItemListener(regulations, this);
		GUIUtils.SetMouseListener(regulations, this);
		GUIUtils.SetChangeListener(regulations, this);

		swix.setActionListener(Reporting, new ReportAction(desktop, swix));

		swix.setActionListener(hydroclimate, new HydAction(swix));
		GUIUtils.SetCheckBoxorRadioButtonItemListener(hydroclimate, this);

		swix.setActionListener(demands, this);

		swix.setActionListener(operations, new OpAction(swix, RegUserEdits, dTableModels, gl));
		GUIUtils.SetCheckBoxorRadioButtonItemListener(operations, this);
		GUIUtils.SetRadioButtonItemListener(dem_SWP, this);
		GUIUtils.SetRadioButtonItemListener(dem_CVP, this);

		swix.setActionListener(facilities, this);
		FacilitiesSetup.SetFacilitiesTables(swix);
		GUIUtils.SetCheckBoxorRadioButtonItemListener(facilities, this);
		GUIUtils.SetCheckBoxorRadioButtonItemListener(Display, this);
		GUIUtils.SetCheckBoxorRadioButtonItemListener(presets, this);
		GUIUtils.SetCheckBoxorRadioButtonItemListener(shortage, this);
		// GUI_Utils.SetCheckBoxorRadioButtonItemListener(delta_flow_criteria,
		// this);
		GUIUtils.SetMouseListener(presets, this);
		GUIUtils.SetMouseListener(shortage, this);
		// GUI_Utils.SetMouseListener(delta_flow_criteria, this);
		GUIUtils.SetMouseListener(facilities, this);

		swix.setActionListener(runsettings, new FileAction(desktop, swix, RegUserEdits, dTableModels, gl, action_WSIDI));
		GUIUtils.SetCheckBoxorRadioButtonItemListener(runsettings, this);

		swix.setActionListener(schematics, this);

		swix.setActionListener(externalPDF, this);

		// Check for scenario changes on Exit.
		desktop.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				System.out.println("Exiting");
				ScenarioSetup.CheckForScenarioChange(swix, dTableModels, RegUserEdits, gl);
				System.exit(0);
			}
		});

		lstScenarios = (JList) swix.find("SelectedList");
		DSSGrabber dss_Grabber;
		dss_Grabber = new DSSGrabber(lstScenarios);
		new WebData(jtp, this, swix, dss_Grabber, lstReports, desktop, 0);

	}

	private void setIconImage(java.awt.Image img) {
		// TODO Auto-generated method stub

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
	@Override
	public void itemStateChanged(ItemEvent e) {
		JComponent component = (JComponent) e.getItem();
		// TODO: EXTERNALIZE

		// was "e.getItemSelected"
		String cName = component.getName();
		if (cName != null) {

			if (cName.startsWith("hyd_rdb20") || cName.startsWith("run_rdb")) {

				// Handling for update of DSS files

				// Confirm CWP/SWP overwrite;
				if (((JRadioButton) component).isSelected() && action_WSIDI != 2) {

					// Actions are only performed if selection is being set to
					// true

					int option = JOptionPane.YES_OPTION; // default is to
					                                     // overwrite
					                                     // everything
					boolean isntDefault = false;
					if (RegUserEdits != null) {
						if (RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn1"))] != null)
							isntDefault = isntDefault || RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn1"))];
						if (RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn2"))] != null)
							isntDefault = isntDefault || RegUserEdits[Integer.parseInt(gl.tableIDForCtrl("op_btn2"))];

					}
					if ((action_WSIDI == 1) && (isntDefault))

						// Prompt is needed only in "regular" processing
						// (action_WSIDI = 1) with non-default table

						option = JOptionPane
						        .showConfirmDialog(
						                desktop,
						                "You have made changes to the SWP and/or CVP WSI/DI curves. \n"
						                        + "Do you want to overwrite these changes with the default values for the configuration ("
						                        + ((JRadioButton) component).getText()
						                        + " ) you have selected?\n\n"
						                        + "Press YES to overwrite, NO to use these values in the selected configuration, or Cancel to revert");

					// Once option is determined, process ...

					if (option == JOptionPane.CANCEL_OPTION) {

						// Cancel: undo change by selecting "other" radio button

						String newcName = null;
						if (cName.equals("hyd_rdb2005"))
							newcName = "hyd_rdb2030";
						else if (cName.equals("hyd_rdb2030"))
							newcName = "hyd_rdb2005";
						else if (cName.equals("run_rdbD1641"))
							newcName = "run_rdbBO";
						else if (cName.equals("run_rdbBO"))
							newcName = "run_rdbD1641";

						action_WSIDI = 2; // Skip all actions on update

						System.out.println(cName + ":-" + newcName);
						((JRadioButton) swix.find(newcName)).setSelected(true);

						action_WSIDI = 1;

					} else {

						// Yes or no: first determine which GUI_link4.table row
						// to use

						String lookup = ((JRadioButton) swix.find("run_rdbD1641")).isSelected() ? "1" : "2";
						lookup = lookup + (((JRadioButton) swix.find("hyd_rdb2005")).isSelected() ? "1" : "2");
						if (((JRadioButton) swix.find("hyd_rdbHis")).isSelected())
							lookup = lookup + "10";
						else {
							lookup = lookup + (((JRadioButton) swix.find("hyd_rdbMid")).isSelected() ? "2" : "3");

							// TODO: Finish off with CCModelID? Move to batch
							// processing?

						}

						int l = -1;
						for (int i = 0; i < table4.length; i++) {
							if (lookup.equals(table4[i][0]))
								l = i;
						}

						if (l != -1) {

							// Then update GUI values, files in Default\Lookup
							// directory

							((JTextField) swix.find("hyd_DSS_Index")).setText(lookup);
							((JTextField) swix.find("hyd_DSS_SV")).setText(table4[l][1]);
							((JTextField) swix.find("hyd_DSS_SV_F")).setText(table4[l][2]);
							((JTextField) swix.find("hyd_DSS_Init")).setText(table4[l][3]);
							((JTextField) swix.find("hyd_DSS_Init_F")).setText(table4[l][4]);

							GUIUtils.copyWSIDItoLookup(((JTextField) swix.find("hyd_DSS_Index")).getText(), "\\Default\\Lookup");

							if ((action_WSIDI == 1) && (option == JOptionPane.YES_OPTION)) {

								// Force CVP and SWP tables to be reset from
								// files

								String fileName = "Default\\Lookup\\" + gl.tableNameForCtrl("op_btn1") + ".table";
								int tID = Integer.parseInt(gl.tableIDForCtrl("op_btn1"));
								dTableModels[tID] = new DataFileTableModel(fileName, tID);

								fileName = "Default\\Lookup\\" + gl.tableNameForCtrl("op_btn2") + ".table";
								tID = Integer.parseInt(gl.tableIDForCtrl("op_btn2"));
								dTableModels[tID] = new DataFileTableModel(fileName, tID);

								JTable table = (JTable) swix.find("tblOpValues");
								table.setModel(dTableModels[tID]);

							}
						}
					}
				}

			} else if (cName.startsWith("ckbp")) {
				// CheckBox in presets panel changed
				// String cID = cName.substring(3);
			}

			else if (cName.startsWith("ckbReg")) {
				// CheckBox in Regulations panel changed
				Boolean isSelect = e.getStateChange() == ItemEvent.SELECTED;
				RegulationSetup.SetRegCheckBoxes(swix, RegUserEdits, dTableModels, gl, reg_btng1, cName, isSelect);

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

						JComponent scr = (JComponent) swix.find("scrRegValues");
						if (scr.isVisible()) {
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
						} else {
							// JOptionPane.showMessageDialog(mainmenu,
							// "There is currently a simulation running at this time.");
						}
					}

				}

			} else if (cName.startsWith("reg_rdbUD")) {
				// do not allow user edits to tables
				JTable table = (JTable) swix.find("tblRegValues");
				JRadioButton rdb = (JRadioButton) e.getItem();

				if (e.getStateChange() == ItemEvent.SELECTED) {
					JComponent scr = (JComponent) swix.find("scrRegValues");
					if (scr.isVisible()) {
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
					} else {
						JPanel pan = (JPanel) swix.find("reg_panTab");
						TitledBorder b = (TitledBorder) pan.getBorder();
						String title = b.getTitle();
						JOptionPane.showMessageDialog(mainmenu, title + " inputs are not user-specifiable at this time.");
						JRadioButton rdb1 = (JRadioButton) swix.find("reg_rdbD1641");
						rdb1.setSelected(true);
						rdb1.revalidate();

					}
				}

			} else if (cName.startsWith("fac_ckb")) {
				// checkbox in facilities panel changed
				JPanel panel = (JPanel) swix.find("fac_pan" + cName.substring(7));

				if (panel != null) {
					// set all "data" panels to invisible
					GUIUtils.ToggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUIUtils.ToggleVisComponent(panel, true);
					GUIUtils.ToggleEnComponentAndChildren(panel, e.getStateChange() == ItemEvent.SELECTED);

				}

			} else if (cName.startsWith("dem_rdbUD")) {
				// Checkbox in Demands page changed

				if (cName.startsWith("dem_rdbUD1")) {
					GUIUtils.ToggleEnComponentAndChildren(dem_UDSWP, e.getStateChange() == ItemEvent.SELECTED,
					        NumericTextField.class);
				} else if (cName.startsWith("dem_rdbUD2")) {
					GUIUtils.ToggleEnComponentAndChildren(dem_UDCVP, e.getStateChange() == ItemEvent.SELECTED,
					        NumericTextField.class);
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
					GUIUtils.ToggleEnComponentAndChildren(pan2, e.getStateChange() == ItemEvent.SELECTED, JRadioButton.class);
					GUIUtils.ToggleEnComponentAndChildren(pan2, e.getStateChange() == ItemEvent.SELECTED, JCheckBox.class);
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
			} else if (cName.startsWith("Repckb")) {
				// Checkbox in Reporting page changed
				if (cName.startsWith("RepckbExceedancePlot")) {
					GUIUtils.ToggleEnComponentAndChildren(controls2, e.getStateChange() == ItemEvent.SELECTED);
				} else if (cName.startsWith("RepckbSummaryTable")) {
					GUIUtils.ToggleEnComponentAndChildren(controls3, e.getStateChange() == ItemEvent.SELECTED);
				}
			} else if (cName.startsWith("hyd_ckb")) {
				// Checkbox in Climate Scenarios page changed
				int selct = 0;
				selct = GUIUtils.CountSelectedButtons(hyd_CC1, JCheckBox.class, selct);
				selct = GUIUtils.CountSelectedButtons(hyd_CC2, JCheckBox.class, selct);

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
					GUIUtils.ToggleEnComponentAndChildren(hyd_CC, e.getStateChange() != ItemEvent.SELECTED);
					GUIUtils.ToggleEnComponentAndChildren(hyd_CC1, e.getStateChange() != ItemEvent.SELECTED);
					GUIUtils.ToggleSelComponentAndChildren(hyd_CC1, false, JCheckBox.class);
					GUIUtils.ToggleSelComponentAndChildren(hyd_CC2, false, JCheckBox.class);
				} else if (cName.startsWith("hyd_rdbMid") || cName.startsWith("hyd_rdbEnd")) {
					GUIUtils.ToggleEnComponentAndChildren(hyd_CC, e.getStateChange() == ItemEvent.SELECTED);
					GUIUtils.ToggleEnComponentAndChildren(hyd_CC1, e.getStateChange() == ItemEvent.SELECTED);
					GUIUtils.ToggleEnComponentAndChildren(hyd_CC2, e.getStateChange() == ItemEvent.SELECTED);
				}
			}
		}
	}

	// React to menu selections.
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: EXTERNALIZE

		if (e.getActionCommand().startsWith("UD_Table")) {

			// Actions.UD_Table(e);

		} else if (e.getActionCommand().startsWith("Sch_Load")) {

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

		} else if (e.getActionCommand().startsWith("Sch_NOD")) {
			// JScrollPane scr = (JScrollPane) swix.find("schem_scr");
			// JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
			// verticalScrollBar.setValue(verticalScrollBar.getMinimum());

		} else if (e.getActionCommand().startsWith("Sch_Delta")) {
			// JScrollPane scr = (JScrollPane) swix.find("schem_scr");
			// JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
			// verticalScrollBar.setValue((int) ((verticalScrollBar.getMaximum()
			// - verticalScrollBar.getMinimum()) *
			// 0.25));

		} else if (e.getActionCommand().startsWith("Sch_SOD")) {
			// JScrollPane scr = (JScrollPane) swix.find("schem_scr");
			// JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
			// verticalScrollBar.setValue(verticalScrollBar.getMaximum());

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
					/*
					 * } else if (component.getName().equals("btnCompareScenarios")) { Object scenlist[] = new
					 * Object[lstScenarios.getModel().getSize()]; int j=0; for (int i = 0; i < lstScenarios.getModel().getSize();
					 * i++) { RBListItem item = (RBListItem) lstScenarios.getModel().getElementAt(i); if (item.isSelected())
					 * scenlist[0] = item.toString2().replace("_DV.DSS", ".cls"); else { j++; scenlist[j] =
					 * item.toString2().replace("_DV.DSS",".cls"); }
					 * 
					 * } // Show frame ScenarioTable sTableFrame = new ScenarioTable(scenlist, swix); java.net.URL imgURL =
					 * getClass().getResource("/images/CalLiteIcon.png"); sTableFrame
					 * .setIconImage(Toolkit.getDefaultToolkit().getImage (imgURL)); sTableFrame.setVisible(true);
					 */

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

	/**
	 * @wbp.parser.entryPoint
	 */

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
					RegulationSetup.SetRegCheckBoxes(swix, RegUserEdits, dTableModels, gl, reg_btng1, cName, isSelect);
				}
			} else if (cName.startsWith("fac_ckb")) {
				// Right Click only
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPanel panel = (JPanel) swix.find("fac_pan" + cName.substring(7));
					// set all "data" panels to invisible
					GUIUtils.ToggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUIUtils.ToggleVisComponent(panel, true);

					JCheckBox ckb = (JCheckBox) component;
					GUIUtils.ToggleEnComponentAndChildren(panel, ckb.isSelected());
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

	@Override
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

					lstScenarios = (JList) swix.find("SelectedList");
					DSSGrabber dss_Grabber;
					dss_Grabber = new DSSGrabber(lstScenarios);

					JCheckBox chk = (JCheckBox) component;
					DisplayFrame.displayFrame(DisplayFrame.QuickState(swix) + ";Locs-" + chk.getText() + ";Index-" + chk.getName(),
					        swix, dss_Grabber, lstScenarios, desktop, 0);
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

							if (((x - 111) * (x - 111) + (y - 150) * (y - 150) < 100)
							        || ((x - 339) * (x - 339) + (y - 157) * (y - 157) < 100)
							        || ((x - 339) * (x - 339) + (y - 250) * (y - 250) < 100)) {

								lstScenarios = (JList) swix.find("SelectedList");
								DSSGrabber dss_Grabber;
								dss_Grabber = new DSSGrabber(lstScenarios);

								if (y < 200)
									DisplayFrame.displayFrame(DisplayFrame.QuickState(swix)
									        + ";Locs-Shasta Storage,;Index-ckbp104,", swix, dss_Grabber, lstScenarios, desktop, 0);

								else
									DisplayFrame.displayFrame(
									        DisplayFrame.QuickState(swix) + ";Locs-Keswick Flows,;Index-ckbp206,", swix,
									        dss_Grabber, lstScenarios, desktop, 0);
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
			int smo = GUIUtils.monthToInt(((String) ((JSpinner) swix.find("spnRunStartMonth")).getValue()).trim());
			int emo = GUIUtils.monthToInt(((String) ((JSpinner) swix.find("spnRunEndMonth")).getValue()).trim());
			if ((syr == 1921) && (smo < 10))
				((JSpinner) swix.find("spnRunStartMonth")).setValue("Oct");
			if ((eyr == 2003) && (emo > 9))
				((JSpinner) swix.find("spnRunEndMonth")).setValue("Sep");

			// Constrain display times the same way [inefficient?]

			syr = (Integer) ((JSpinner) swix.find("spnStartYear")).getValue();
			eyr = (Integer) ((JSpinner) swix.find("spnEndYear")).getValue();
			smo = GUIUtils.monthToInt(((String) ((JSpinner) swix.find("spnStartMonth")).getValue()).trim());
			emo = GUIUtils.monthToInt(((String) ((JSpinner) swix.find("spnEndMonth")).getValue()).trim());
			if ((syr == 1921) && (smo < 10))
				((JSpinner) swix.find("spnStartMonth")).setValue("Oct");
			if ((eyr == 2003) && (emo > 9))
				((JSpinner) swix.find("spnEndMonth")).setValue("Sep");

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
			if (lookups[i][1].equals("") && !lookups[i][0].startsWith("0")) { // additional
				                                                              // condition
				                                                              // added
				                                                              // to
				                                                              // handle
				                                                              // "0xx"
				                                                              // -
				                                                              // these
				                                                              // have
				                                                              // no
				                                                              // checkbox
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

	public static String getLookups5(int i, int j) {
		return table5[i][j];
	}

	public static int getLookups5Length() {
		return table5.length;
	}

	/*
	 * setupAndRun - method to do setup in alternate thread
	 */

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