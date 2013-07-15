package gov.ca.water.calgui;

import gov.ca.water.calgui.dashboards.DemAction;
import gov.ca.water.calgui.dashboards.DemListener;
import gov.ca.water.calgui.dashboards.FacListener;
import gov.ca.water.calgui.dashboards.FacilitiesAction;
import gov.ca.water.calgui.dashboards.FacilitiesSetup;
import gov.ca.water.calgui.dashboards.HydAction;
import gov.ca.water.calgui.dashboards.HydListener;
import gov.ca.water.calgui.dashboards.OpAction;
import gov.ca.water.calgui.dashboards.OpListener;
import gov.ca.water.calgui.dashboards.RegAction;
import gov.ca.water.calgui.dashboards.RegListener;
import gov.ca.water.calgui.dashboards.RegulationSetup;
import gov.ca.water.calgui.dashboards.ReportAction;
import gov.ca.water.calgui.dashboards.ReportListener;
import gov.ca.water.calgui.dashboards.RunListener;
import gov.ca.water.calgui.dashboards.SchematicAction;
import gov.ca.water.calgui.dashboards.SchematicListener;
import gov.ca.water.calgui.results.DisplayFrame;
import gov.ca.water.calgui.results.SchematicMain;
import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;
import gov.ca.water.calgui.utils.NumericTextField;
import gov.ca.water.calgui.utils.PopulateDTable;
import gov.ca.water.calgui.utils.SimpleFileFilter;
import gov.ca.water.calgui.utils.Utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

import javax.help.JHelp;
import javax.swing.BorderFactory;
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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class MainMenu implements ActionListener, MouseListener, TableModelListener, MenuListener, ChangeListener, ListDataListener,
        ComponentListener, KeyEventDispatcher {

	private static Logger log = Logger.getLogger(MainMenu.class.getName());

	public final SwingEngine swix;

	// Declare public Objects
	static JHelp helpViewer = null;
	public static JFrame desktop;
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
	JPanel presets;
	JPanel shortage;
	JPanel delta_flow_criteria;
	JPanel WMA;
	JPanel hyd_CCOpt;
	JPanel dem_SWP;
	JPanel dem_CVP;
	JFrame dialog;
	ButtonGroup reg_btng1;
	GUILinks gl;
	String desktopTitle;
	String scenFilename;
	JFrame frameDisplayControls;

	public DataFileTableModel[] dTableModels;
	public Boolean[] regUserEditFlags;
	public int[] regFlags;
	static FileDialog fdDSSFiles;

	static public String lookups[][];
	static String table4[][]; // Holds GUI_links4.table values that control selection of SV and Init DSS as well as WSI_DI files
	static String table5[][]; // Holds DSS Schematic link values

	int action_WSIDI = 0; // 0 = NO PROMPT, 1 = NORMAL, 2 = UNDO

	// configuration. Set False initially

	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	JTabbedPane jtp;
	JMenuBar menu;
	ProgressMonitor pMon;

	public JList lstScenarios;
	public FileAction fileAction; // Listener for FileActions is public to facilitate testing

	/**
	 * Main constructor for CalLite GUI
	 * 
	 * @param makeVisible
	 *            - set to true if UI should be visible, false to keep hidden (for use in unit tests)
	 * @throws Exception
	 */
	public MainMenu(boolean makeVisible) throws Exception {

		long startSetupTime = System.currentTimeMillis();

		try {
			CalLiteSplash.getSplash();
		}

		catch (Exception e) {
			log.debug("Problem loading splash screen: " + e);
		}

		swix = new SwingEngine(this);

		// Read GUI configuration, set UI elements
		try {

			swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
			swix.render(new File(System.getProperty("user.dir") + "/Config/GUI.xml"));
			desktop.setVisible(false);

			// Set GUI visuals
			desktopTitle = desktop.getTitle();
			desktop.setResizable(false);

			// Set Icon
			URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
			desktop.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

			// Title
			scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
			desktop.setTitle(desktopTitle + " - " + scenFilename);

			// Set initial Tooltips with scenario directory
			((JTextField) swix.find("run_txfoDSS")).setToolTipText(System.getProperty("user.dir") + "\\Scenarios\\"
			        + ((JTextField) swix.find("run_txfoDSS")).getText());

			((JTextField) swix.find("run_txfScen")).setToolTipText(System.getProperty("user.dir") + "\\Scenarios\\"
			        + ((JTextField) swix.find("run_txfScen")).getText());

			// Help hotkey
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			manager.addKeyEventDispatcher(this);

			// Recolor results tabs

			jtp = (JTabbedPane) swix.find("tabbedPane1");

			jtp.setForegroundAt(6, Color.blue);
			jtp.setForegroundAt(7, Color.blue);
			jtp.setForegroundAt(8, Color.blue);
			jtp.setForegroundAt(9, Color.blue);

			jtp.setBackgroundAt(6, Color.WHITE);
			jtp.setBackgroundAt(7, Color.WHITE);
			jtp.setBackgroundAt(8, Color.WHITE);
			jtp.setBackgroundAt(9, Color.WHITE);
			// DSSGrabber dss_Grabber = new DSSGrabber(lstScenarios);
			// new WebData(jtp, this, swix, dss_Grabber, lstScenarios, desktop, 0);

		}

		catch (Exception e) {
			log.debug("Could not build UI. " + e);
		}

		// Read Schematic_DSS_link4.table and place in Table5
		ArrayList<String> guiLinks5 = new ArrayList<String>();

		try {
			guiLinks5 = GUIUtils.getGUILinks("Config/Schematic_DSS_link4.table");
			table5 = new String[guiLinks5.size()][6];

			for (int i = 0; i < guiLinks5.size(); i++) {
				String tokens[] = guiLinks5.get(i).split("\t");
				table5[i][0] = tokens[0];
				table5[i][1] = tokens[1];
				table5[i][2] = tokens[2];
				table5[i][3] = tokens[3];
				table5[i][4] = tokens[4];
				table5[i][5] = tokens[5];
			}

			// Read Schematic_DSS_link4.table and place in Table4 (for assigning SV,
			// init file, etc.)
			ArrayList<String> guiLinks4 = new ArrayList<String>();
			guiLinks4 = GUIUtils.getGUILinks("Config/GUI_Links4.table");
			table4 = new String[guiLinks4.size()][5];
			for (int i = 0; i < guiLinks4.size(); i++) {
				String tokens[] = guiLinks4.get(i).split("\t");
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
			gl.readIn("Config/GUI_Links2.table");

			readInLookups(); // Temporary access to quick reports info from gui_Links3.table
		}

		catch (Exception e) {
			log.debug("Problem reading table files. " + e);
		}

		// Setup for Reporting page

		// Set up scenario list
		try {
			lstScenarios = (JList) swix.find("SelectedList");
		} catch (Exception e) {
			log.debug("Problem setting scenario list. " + e);
		}

		// Set up additional UI elements
		try {
			JRadioButton rdb1 = (JRadioButton) swix.find("rdbp001");
			JRadioButton rdb2 = (JRadioButton) swix.find("rdbp002");

			fdDSSFiles = new FileDialog(lstScenarios, (JLabel) swix.find("lblBase"), rdb1, rdb2, true);
			lstScenarios.setModel(fdDSSFiles.lmScenNames);
			lstScenarios.setBorder(new LineBorder(Color.gray, 1));

			JButton btnScenario = (JButton) swix.find("btnAddScenario");
			btnScenario.addActionListener(fdDSSFiles);

			JButton btnScenarioDel = (JButton) swix.find("btnDelScenario");
			btnScenarioDel.addActionListener(fdDSSFiles);

			JButton btnClearAll = (JButton) swix.find("btnClearScenario");
			btnClearAll.addActionListener(this);

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

			File file = new File(System.getProperty("user.dir") + "/Scenarios/" + scen);
			if (!file.exists()) {
				if (JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(null, "Default scenario \"" + scen
				        + "\" not found. Press OK to select a scenario to load or Cancel to exit", "Default Scenario Not Found!",
				        JOptionPane.OK_CANCEL_OPTION)) {
					return;
				}
				FileDialog scenFileDialog = new FileDialog(null, (JTextField) swix.find("run_txfScen"), "CLS");
				scenFileDialog.actionPerformed(null);
				if (scenFileDialog.dialogRC != 0)
					return;

				tf = (JTextField) swix.find("run_txfScen");
				scen = tf.getText();
				file = new File(System.getProperty("user.dir") + "/Scenarios/" + scen);

			}
			action_WSIDI = 0;
			regUserEditFlags = GUIUtils.setControlValues(file, swix, dTableModels, gl);
			regFlags = GUIUtils.setControlValues(file, swix, gl);
			action_WSIDI = 1;

			JComponent component1 = (JComponent) swix.find("scrOpValues");
			JTable table1 = (JTable) swix.find("tblOpValues");

			component1.setVisible(true);
			component1.setEnabled(true);
			dTableModels = PopulateDTable.populate("op_btn1", table1, component1, swix, regUserEditFlags, dTableModels, gl,
			        regFlags);
			dTableModels = PopulateDTable.populate("op_btn2", table1, component1, swix, regUserEditFlags, dTableModels, gl,
			        regFlags);

			// pan.setBorder(title);
			component1.setVisible(false);
			table1.setVisible(false);
		}

		catch (Exception e) {
			log.debug("Exception reading in scenario file in constructor." + e);
		}

		// Refresh checkbox labels
		try {
			for (int i = 0; i < regUserEditFlags.length; i++) {
				if (regUserEditFlags[i] != null) {

					String cID = Integer.toString(i);
					String cName = gl.ctrlFortableID(cID);

					JComponent c = (JComponent) swix.find(cName);

					if (c instanceof JCheckBox) {
						JCheckBox ckb = (JCheckBox) c;
						String ckbtext = ckb.getText();
						String[] ckbtext1 = ckbtext.split(" - ");
						ckbtext = ckbtext1[0];
						if (ckbtext1.length > 0) {
							if (regUserEditFlags[i] == true)
								ckb.setText(ckbtext + " - User Def.");
							else
								ckb.setText(ckbtext + " -  Default");

						} else
							ckb.setText(ckbtext);
					}
				}
			}
		}

		catch (Exception e) {
			log.debug("Problem setting checkbox labels. " + e);
		}

		// Schematic views

		try {

			SchematicMain schemView = new SchematicMain((JPanel) swix.find("schematic_holder"), "file:///"
			        + System.getProperty("user.dir") + "/Config/callite_merged.svg", this, swix, 1.19, 0.0, 0.0, 1.19, -8.0, 5.0);
			SchematicMain schemView2 = new SchematicMain((JPanel) swix.find("schematic_holder2"), "file:///"
			        + System.getProperty("user.dir") + "/Config/callite-massbalance_working.svg", this, swix, 1.2, 0, 0.0, 1.2,
			        21.0, 15.0);
		} catch (Exception e) {
			log.debug("Could not load schematic views. " + e);
		}

		// PDF Report

		try {

			((JButton) swix.find("btnGetTemplateFile")).addActionListener(new FileDialog(null, (JTextField) swix
			        .find("tfTemplateFILE"), "inp"));
			((JButton) swix.find("btnGetReportFile1")).addActionListener(new FileDialog(null, (JTextField) swix
			        .find("tfReportFILE1")));
			((JButton) swix.find("btnGetReportFile2")).addActionListener(new FileDialog(null, (JTextField) swix
			        .find("tfReportFILE2")));
			((JButton) swix.find("btnGetReportFile3")).addActionListener(new FileDialog(null, (JTextField) swix
			        .find("tfReportFILE3"), "PDF"));
		} catch (Exception e) {
			log.debug("Could not load report controls." + e);
		}

		// Set Listeners
		try {

			jtp.addChangeListener(this);
			((JTabbedPane) swix.find("variables")).addChangeListener(this);

			fileAction = new FileAction(desktop, swix, regUserEditFlags, dTableModels, gl, action_WSIDI, regFlags);
			swix.setActionListener(menu, fileAction);
			GUIUtils.setMenuListener(menu, this);

			swix.setActionListener(regulations, new RegAction(swix, regUserEditFlags, dTableModels, gl, reg_btng1, regFlags));
			GUIUtils.setCheckBoxorRadioButtonItemListener(regulations, new RegListener(swix, regUserEditFlags, dTableModels, gl,
			        reg_btng1, regFlags));
			GUIUtils.setMouseListener(regulations, this);
			GUIUtils.setChangeListener(regulations, this);

			swix.setActionListener(Reporting, new ReportAction(desktop, swix));
			GUIUtils.setCheckBoxorRadioButtonItemListener(Reporting, new ReportListener(swix));

			swix.setActionListener(hydroclimate, new HydAction(swix));
			GUIUtils.setCheckBoxorRadioButtonItemListener(hydroclimate, new HydListener(desktop, swix, regUserEditFlags,
			        dTableModels, gl, action_WSIDI));

			swix.setActionListener(operations, new OpAction(swix, regUserEditFlags, dTableModels, gl, regFlags));
			GUIUtils.setCheckBoxorRadioButtonItemListener(operations, new OpListener(swix));

			swix.setActionListener(demands, new DemAction(swix));
			GUIUtils.setMouseListener(demands, this);
			GUIUtils.setRadioButtonItemListener(dem_SWP, new DemListener(swix));
			GUIUtils.setRadioButtonItemListener(dem_CVP, new DemListener(swix));

			FacilitiesSetup.SetFacilitiesTables(swix);
			GUIUtils.setCheckBoxorRadioButtonItemListener(facilities, new FacListener(swix));
			GUIUtils.setCheckBoxorRadioButtonItemListener(Display, new FacListener(swix));
			GUIUtils.setCheckBoxorRadioButtonItemListener(presets, new FacListener(swix));
			GUIUtils.setCheckBoxorRadioButtonItemListener(shortage, new FacListener(swix));
			GUIUtils.setMouseListener(presets, this);
			GUIUtils.setMouseListener(shortage, this);
			GUIUtils.setMouseListener(facilities, this);
			swix.setActionListener(facilities, new FacilitiesAction(swix));

			swix.setActionListener(runsettings, new FileAction(desktop, swix, regUserEditFlags, dTableModels, gl, action_WSIDI,
			        regFlags));
			((JSlider) swix.find("run_sldThreads")).addChangeListener(this);
			GUIUtils.setCheckBoxorRadioButtonItemListener(runsettings, new RunListener(desktop, swix, regUserEditFlags,
			        dTableModels, gl, action_WSIDI));

			swix.setActionListener(schematics, new SchematicAction(swix));
			GUIUtils.setCheckBoxorRadioButtonItemListener(schematics, new SchematicListener(swix));

			swix.setActionListener(externalPDF, new ReportAction(desktop, swix));

			desktop.addComponentListener(this);

			// Check for scenario changes on Exit.
			desktop.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			desktop.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					if (FileAction.checkForScenarioChange(swix, dTableModels, regUserEditFlags, gl) == true) {
						System.exit(0);
					}

				}
			});

			lstScenarios = (JList) swix.find("SelectedList");

			GUIUtils.toggleEnComponentAndChildren(swix.find("regpan1"), false);
			GUIUtils.toggleEnComponentAndChildren(swix.find("regpan2"), false);
			GUIUtils.toggleEnComponentAndChildren(swix.find("regpan3"), false);

			// Count threads and update selector appropriately

			int maxThreads = Math.max(1, Runtime.getRuntime().availableProcessors());
			GUIUtils.simultaneousRuns = maxThreads;

			((JSlider) swix.find("run_sldThreads")).setEnabled(maxThreads > 1);
			((JSlider) swix.find("run_sldThreads")).setMaximum(maxThreads);
			((JLabel) swix.find("run_lblThreads")).setText(" " + maxThreads + ((maxThreads > 1) ? " runs" : " run"));
			((JLabel) swix.find("run_lblThreadsInfo")).setText("Simultaneous runs "
			        + ((maxThreads > 1) ? "(1-" + maxThreads + ")" : "(1)"));

			ScenarioMonitor.start();

			long totalSetupTime = System.currentTimeMillis() - startSetupTime;
			if (totalSetupTime < 3000)
				Thread.sleep(3000 - totalSetupTime);

			desktop.setVisible(makeVisible);

		}

		catch (Exception e) {
			log.debug("Problem with setting listeners " + e);
		}

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) {

		// Load menu

		try {
			new MainMenu(true);
		} catch (Exception e) {
			log.debug(e);

		}

	}

	// React to menu selections.
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().startsWith("UD_Table")) {

			// Actions.UD_Table(e);

		} else if (e.getActionCommand().startsWith("Sch_Load")) {

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new SimpleFileFilter(".dss.", "DSS File (*.dss)"));
			fc.setCurrentDirectory(new File(".//Scenarios"));

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
				} else if (component.getName().equals("btnFacLimits")) {
					JScrollPane scr = (JScrollPane) swix.find("fac_scr1");
					scr.setVisible(true);
					scr = (JScrollPane) swix.find("fac_scr2");
					scr.setVisible(false);
					scr = (JScrollPane) swix.find("fac_scr3");
					scr.setVisible(false);
				} else if (component.getName().equals("btnFacHood")) {
					JScrollPane scr = (JScrollPane) swix.find("fac_scr2");
					scr.setVisible(true);
					scr = (JScrollPane) swix.find("fac_scr1");
					scr.setVisible(false);
					scr = (JScrollPane) swix.find("fac_scr3");
					scr.setVisible(false);
				} else if (component.getName().equals("btnFacPulse")) {
					JScrollPane scr = (JScrollPane) swix.find("fac_scr3");
					scr.setVisible(true);
					scr = (JScrollPane) swix.find("fac_scr1");
					scr.setVisible(false);
					scr = (JScrollPane) swix.find("fac_scr2");
					scr.setVisible(false);
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
					RegulationSetup.SetRegCheckBoxes(swix, regUserEditFlags, dTableModels, gl, reg_btng1, cName, isSelect, "null",
					        regFlags);

					// JRadioButton btn = (JRadioButton) swix.find("rdbRegQS_UD");
					// boolean enabled = btn.isEnabled();

					/*
					 * ((JRadioButton) swix.find("btnReg1641")).setEnabled(enabled); ((JRadioButton)
					 * swix.find("btnReg1485")).setEnabled(enabled); ((JRadioButton) swix.find("btnRegUD")).setEnabled(enabled);
					 * ((JRadioButton) swix.find("btnReg1641")).setVisible(enabled); ((JRadioButton)
					 * swix.find("btnReg1485")).setVisible(enabled); ((JRadioButton) swix.find("btnRegUD")).setVisible(enabled);
					 */
				}
			} else if (cName.startsWith("fac_ckb")) {
				// Right click on a facility checkbox makes a subpanel visible
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPanel panel = (JPanel) swix.find("fac_pan" + cName.substring(7));
					// set all "data" panels to invisible
					GUIUtils.toggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUIUtils.toggleVisComponent(panel, true);

					JCheckBox ckb = (JCheckBox) component;
					GUIUtils.toggleEnComponentAndChildren(panel, ckb.isSelected());
					((JLabel) swix.find("map3")).setVisible(false);
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

			if (cName != null) {
			}
		} else {

			// Double Click

			if (iClickCount == 2) {
				if (cName.startsWith("ckbp")) {

					// ----- Quick Results: HANDLE DISPLAY OF SINGLE VARIABLE -----

					menu.setCursor(hourglassCursor);

					if (lstScenarios.getModel().getSize() == 0) {
						JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
					} else {

						lstScenarios = (JList) swix.find("SelectedList");
						JCheckBox chk = (JCheckBox) component;
						DisplayFrame.displayFrame(
						        DisplayFrame.quickState(swix) + ";Locs-" + chk.getText() + ";Index-" + chk.getName(), swix,
						        lstScenarios, desktop, 0);
						menu.setCursor(normalCursor);
					}
					// Placeholder for future handling of double-clicks

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

	int lastHeight = 768;
	int lastWidth = 1024;

	@Override
	public void stateChanged(ChangeEvent changeEvent) {

		Component c = (Component) changeEvent.getSource();
		String lcName = c.getName().toLowerCase();

		if (lcName.equals("run_sldthreads")) {
			GUIUtils.simultaneousRuns = ((JSlider) c).getValue();
			((JLabel) swix.find("run_lblThreads")).setText(" " + GUIUtils.simultaneousRuns + " run"
			        + ((GUIUtils.simultaneousRuns > 1) ? "s" : ""));
		}
		if (lcName.equals("reg_tabbedpane")) {
			// Hide table on Regulations dashboard when moving between tabbed panes on Regulation dashboard
			((JComponent) swix.find("scrRegValues")).setVisible(false);
			((JPanel) swix.find("reg_panTab")).setBorder(BorderFactory.createTitledBorder("Values"));
		}

		if (lcName.equals("variables")) {
			desktop.setSize(1025, 768);
			desktop.setSize(1024, 768);
		}

		if (lcName.equals("tabbedpane1")) {
			// Allow larger windows when Web Map or Custom View selected
			if (((JTabbedPane) c).getSelectedIndex() == 8 || ((JTabbedPane) c).getSelectedIndex() == 10) {

				// Enable max
				desktop.setResizable(true);
				// Resize to last large size
				desktop.setSize(lastWidth, lastHeight);

			} else {

				// Disable max
				desktop.setResizable(false);
				// Store current size
				lastWidth = (int) desktop.getSize().getWidth();
				lastHeight = (int) desktop.getSize().getHeight();

				if (((JTabbedPane) c).getSelectedIndex() == 6) { // Quick Results
					desktop.setResizable(true);
					if (GUIUtils.controlFrame != null) {
						WindowEvent wev = new WindowEvent(GUIUtils.controlFrame, WindowEvent.WINDOW_CLOSING);
						GUIUtils.controlFrame.dispatchEvent(wev);
						GUIUtils.controlFrame.dispose();
						GUIUtils.controlFrame = null;
					}
					desktop.setSize(1025, 768);
				}
				// Size down if needed
				desktop.setSize(1024, 768);

			}
			mainmenu.setLocation(0, 2);
			((JTabbedPane) c).setLocation(0, 0);
			((JPanel) swix.find("settings")).setLocation(0, 0);
		}

		if (lcName.substring(0, 3).equals("spn")) {

			// Constrain run times to [10/1921,9/2003]
			int syr = (Integer) ((JSpinner) swix.find("spnRunStartYear")).getValue();
			int eyr = (Integer) ((JSpinner) swix.find("spnRunEndYear")).getValue();
			int smo = Utils.monthToInt(((String) ((JSpinner) swix.find("spnRunStartMonth")).getValue()).trim());
			int emo = Utils.monthToInt(((String) ((JSpinner) swix.find("spnRunEndMonth")).getValue()).trim());
			if ((syr == 1921) && (smo < 10))
				((JSpinner) swix.find("spnRunStartMonth")).setValue("Oct");
			if ((eyr == 2003) && (emo > 9))
				((JSpinner) swix.find("spnRunEndMonth")).setValue("Sep");

			// Constrain display times the same way [inefficient?]

			syr = (Integer) ((JSpinner) swix.find("spnStartYear")).getValue();
			eyr = (Integer) ((JSpinner) swix.find("spnEndYear")).getValue();
			smo = Utils.monthToInt(((String) ((JSpinner) swix.find("spnStartMonth")).getValue()).trim());
			emo = Utils.monthToInt(((String) ((JSpinner) swix.find("spnEndMonth")).getValue()).trim());
			if ((syr == 1921) && (smo < 10))
				((JSpinner) swix.find("spnStartMonth")).setValue("Oct");
			if ((eyr == 2003) && (emo > 9))
				((JSpinner) swix.find("spnEndMonth")).setValue("Sep");

		}
	}

	private int readInLookups() {

		// Open input file

		Scanner input;
		try {
			input = new Scanner(new FileReader("Config/GUI_Links3.table"));
		} catch (FileNotFoundException e) {
			log.debug("Cannot open input file Config/GUI_Links3.table: " + e.getMessage());
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

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {

		Component c = (Component) e.getSource();
		if (c.getName().equals("desktop")) {

			// Handle resizing of main frame for app (only allowed in Web and Map views)

			JFrame f = (JFrame) c;
			Dimension d = f.getSize();

			d.width = Math.max(1024, d.width);
			d.height = Math.max(768, d.height);
			if (d != f.getSize()) {

				f.setSize(d);
				f.repaint();
			}

			// Adjust position of schematics components

			updateSchematicLayout(d);
			mainmenu.invalidate();

		}

	}

	/**
	 * @param d
	 */
	private void updateSchematicLayout(Dimension d) {

		mainmenu.setSize(new Dimension(d.width - 8, d.height - 51));

		JPanel p1 = (JPanel) swix.find("settings");
		p1.setMinimumSize(new Dimension(d.width - 4, d.height - 28));
		p1.setSize(new Dimension(d.width - 4, d.height - 28));
		p1.setPreferredSize(new Dimension(d.width - 4, d.height - 28));
		p1.setMaximumSize(new Dimension(d.width - 4, d.height - 28));

		JPanel p2 = (JPanel) swix.find("schematic_holder");
		p2.setSize(new Dimension(d.width - 74, d.height - 148));
		p2.setPreferredSize(new Dimension(d.width - 74, d.height - 148));
		p2.setMaximumSize(new Dimension(d.width - 74, d.height - 148));
		p2.setMinimumSize(new Dimension(d.width - 74, d.height - 148));
		p2.invalidate();

		JPanel p3 = (JPanel) swix.find("schematic_holder2");
		p3.setSize(new Dimension(d.width - 74, d.height - 148));
		p3.setPreferredSize(new Dimension(d.width - 74, d.height - 148));
		p3.setMaximumSize(new Dimension(d.width - 74, d.height - 148));
		p3.setMinimumSize(new Dimension(d.width - 74, d.height - 148));
		p3.invalidate();

		// JPanel p4 = (JPanel) swix.find("schematics");
		// p4.setSize(new Dimension(d.width - 40, d.height - 40));

		JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
		mainmenu.setLocation(0, 2);
		jtp.setLocation(0, 0);
		p1.setLocation(0, 0);
		jtp.setSize(new Dimension(mainmenu.getSize().width - 1, mainmenu.getSize().height - 4));

		// System.out.println(p4.getSize());
		// System.out.println("Desktop " + desktop.getSize());
		// System.out.println("Mainmenu " + mainmenu.getSize() + " " + mainmenu.getLocation());
		// System.out.println("TabbedPane " + jtp.getSize() + " " + jtp.getLocation());
		// System.out.println("Settings " + p1.getSize() + " " + p1.getLocation());
		// System.out.println("Schematic" + p2.getSize() + "  " + (desktop.getSize().height - p2.getSize().height));
		// System.out.println(((JSVGScrollPane) ((JPanel) swix.find("schematic_holder")).getComponent(0)).getCanvas()
		// .getRenderingTransform());
		// System.out.println(((JSVGScrollPane) ((JPanel) swix.find("schematic_holder2")).getComponent(0)).getCanvas()
		// .getRenderingTransform());
		// System.out.println();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

}