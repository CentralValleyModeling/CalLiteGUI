package gov.ca.water.calgui;

import gov.ca.water.calgui.dashboards.DemListener;
import gov.ca.water.calgui.dashboards.FacListener;
import gov.ca.water.calgui.dashboards.FacilitiesSetup;
import gov.ca.water.calgui.dashboards.HydAction;
import gov.ca.water.calgui.dashboards.HydListener;
import gov.ca.water.calgui.dashboards.OpAction;
import gov.ca.water.calgui.dashboards.OpListener;
import gov.ca.water.calgui.dashboards.RegAction;
import gov.ca.water.calgui.dashboards.RegListener;
import gov.ca.water.calgui.dashboards.RegulationSetup;
import gov.ca.water.calgui.dashboards.ReportAction;
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
import gov.ca.water.calgui.utils.UnitsUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        KeyEventDispatcher {

	static Logger log = Logger.getLogger(MainMenu.class.getName());

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
	GetDSSFilename getScenFilename;
	DataFileTableModel[] dTableModels;
	Boolean[] RegUserEdits;

	static public String lookups[][];
	static String table4[][]; // Holds GUI_links4.table values that control selection of SV and Init DSS as well as WSI_DI files
	static String table5[][]; // Holds DSS Schematic link values

	int action_WSIDI = 0; // 0 = NO PROMPT, 1 = NORMAL, 2 = UNDO

	// configuration. Set False initially

	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	JMenuBar menu;
	ProgressMonitor pMon;

	public JList lstScenarios;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainMenu() throws Exception {

		long startSetupTime = System.currentTimeMillis();

		// Read GUI configuration
		swix = new SwingEngine(this);
		swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		swix.render(new File(System.getProperty("user.dir") + "/Config/GUI.xml"));
		desktop.setVisible(false);

		// Set GUI visuals
		desktopTitle = desktop.getTitle() + ";  Scenario";
		desktop.setResizable(false);

		// Set Icon
		URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		desktop.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

		// Title
		scenFilename = ((JTextField) swix.find("run_txfScen")).getText();
		desktop.setTitle(desktopTitle + " - " + scenFilename);
		getScenFilename = new GetDSSFilename(null, (JTextField) swix.find("run_txfScen"), "CLS");

		// Set initial Tooltips with scenario directory
		((JTextField) swix.find("run_txfoDSS")).setToolTipText(System.getProperty("user.dir") + "\\Scenarios\\"
		        + ((JTextField) swix.find("run_txfoDSS")).getText());

		((JTextField) swix.find("run_txfScen")).setToolTipText(System.getProperty("user.dir") + "\\Scenarios\\"
		        + ((JTextField) swix.find("run_txfScen")).getText());

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

		// Read Schematic_DSS_link4.table and place in Table5
		ArrayList<String> guiLinks5 = new ArrayList<String>();
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
		action_WSIDI = 0;
		RegUserEdits = GUIUtils.setControlValues(file, swix, dTableModels, gl);
		action_WSIDI = 1;

		JComponent component1 = (JComponent) swix.find("scrOpValues");
		JTable table1 = (JTable) swix.find("tblOpValues");

		component1.setVisible(true);
		component1.setEnabled(true);
		dTableModels = PopulateDTable.populate("op_btn1", table1, component1, swix, RegUserEdits, dTableModels, gl);
		dTableModels = PopulateDTable.populate("op_btn2", table1, component1, swix, RegUserEdits, dTableModels, gl);

		// pan.setBorder(title);
		component1.setVisible(false);
		table1.setVisible(false);

		// Refresh checkbox labels
		for (int i = 0; i < RegUserEdits.length; i++) {
			if (RegUserEdits[i] != null) {

				String cID = Integer.toString(i);
				String cName = gl.ctrlFortableID(cID);

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

		// Schematic views
		SchematicMain schemView;
		schemView = new SchematicMain((JPanel) swix.find("schematic_holder"), "file:///" + System.getProperty("user.dir")
		        + "/Config/callite_merged.svg", this, swix, 4.0, 0.0, 0.0, 4.0, -1400.0, -200.0);
		schemView = new SchematicMain((JPanel) swix.find("schematic_holder2"), "file:///" + System.getProperty("user.dir")
		        + "/Config/callite-massbalance_working.svg", this, swix, 3.0, 0, 0.0, 3.0, -950.0, -520.0);

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
		GUIUtils.setMenuListener(menu, this);

		swix.setActionListener(regulations, new RegAction(swix, RegUserEdits));
		GUIUtils.setCheckBoxorRadioButtonItemListener(regulations, new RegListener(swix, RegUserEdits, dTableModels, gl, reg_btng1));
		GUIUtils.setMouseListener(regulations, this);
		GUIUtils.setChangeListener(regulations, this);

		swix.setActionListener(Reporting, new ReportAction(desktop, swix));

		swix.setActionListener(hydroclimate, new HydAction(swix));
		GUIUtils.setCheckBoxorRadioButtonItemListener(hydroclimate, new HydListener(desktop, swix, RegUserEdits, dTableModels, gl,
		        action_WSIDI));

		swix.setActionListener(demands, this);

		swix.setActionListener(operations, new OpAction(swix, RegUserEdits, dTableModels, gl));
		GUIUtils.setCheckBoxorRadioButtonItemListener(operations, new OpListener(swix));
		GUIUtils.setRadioButtonItemListener(dem_SWP, new DemListener(swix));
		GUIUtils.setRadioButtonItemListener(dem_CVP, new DemListener(swix));

		swix.setActionListener(facilities, this);
		FacilitiesSetup.SetFacilitiesTables(swix);
		GUIUtils.setCheckBoxorRadioButtonItemListener(facilities, new FacListener(swix));
		GUIUtils.setCheckBoxorRadioButtonItemListener(Display, new FacListener(swix));
		GUIUtils.setCheckBoxorRadioButtonItemListener(presets, new FacListener(swix));
		GUIUtils.setCheckBoxorRadioButtonItemListener(shortage, new FacListener(swix));
		GUIUtils.setMouseListener(presets, this);
		GUIUtils.setMouseListener(shortage, this);
		GUIUtils.setMouseListener(facilities, this);

		swix.setActionListener(runsettings, new FileAction(desktop, swix, RegUserEdits, dTableModels, gl, action_WSIDI));
		GUIUtils.setCheckBoxorRadioButtonItemListener(runsettings, new RunListener(desktop, swix, RegUserEdits, dTableModels, gl,
		        action_WSIDI));

		swix.setActionListener(schematics, new SchematicAction(swix));
		GUIUtils.setCheckBoxorRadioButtonItemListener(schematics, new SchematicListener(swix));

		swix.setActionListener(externalPDF, this);

		// Check for scenario changes on Exit.
		desktop.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		desktop.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				if (FileAction.checkForScenarioChange(swix, dTableModels, RegUserEdits, gl) == true) {
					System.exit(0);
				}

			}
		});

		lstScenarios = (JList) swix.find("SelectedList");

		long totalSetupTime = System.currentTimeMillis() - startSetupTime;
		if (totalSetupTime < 3000)
			Thread.sleep(3000 - totalSetupTime);

		desktop.setVisible(true);

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) {

		// Load menu
		try {
			new MainMenu();
		} catch (Exception e) {

			log.debug("Problem loading main menu: " + e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(), "Main menu error", JOptionPane.ERROR_MESSAGE);
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
			if (cName.startsWith("ckbp")) {

				// ----- Quick Results: HANDLE DISPLAY OF SINGLE VARIABLE -----

				menu.setCursor(hourglassCursor);

				if (lstScenarios.getModel().getSize() == 0) {
					JOptionPane.showMessageDialog(null, "No scenarios loaded", "Error", JOptionPane.ERROR_MESSAGE);
				} else {

					lstScenarios = (JList) swix.find("SelectedList");
					JCheckBox chk = (JCheckBox) component;
					DisplayFrame.displayFrame(DisplayFrame.quickState(swix) + ";Locs-" + chk.getText() + ";Index-" + chk.getName(),
					        swix, lstScenarios, desktop, 0);
					menu.setCursor(normalCursor);
				}
			}
		} else {

			// Double Click

			if (iClickCount == 2) {
				if (cName != null) {

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

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		Component c = (Component) changeEvent.getSource();
		if (c.getName().toLowerCase().equals("reg_tabbedpane")) {
			// Hide table on Regulations dashboard when moving between tabbed panes on Regulation dashboard
			((JComponent) swix.find("scrRegValues")).setVisible(false);
			((JPanel) swix.find("reg_panTab")).setBorder(BorderFactory.createTitledBorder("Values"));
		}
		if (c.getName().toLowerCase().substring(0, 3).equals("spn")) {

			// Constrain run times to [10/1921,9/2003]
			int syr = (Integer) ((JSpinner) swix.find("spnRunStartYear")).getValue();
			int eyr = (Integer) ((JSpinner) swix.find("spnRunEndYear")).getValue();
			int smo = UnitsUtils.monthToInt(((String) ((JSpinner) swix.find("spnRunStartMonth")).getValue()).trim());
			int emo = UnitsUtils.monthToInt(((String) ((JSpinner) swix.find("spnRunEndMonth")).getValue()).trim());
			if ((syr == 1921) && (smo < 10))
				((JSpinner) swix.find("spnRunStartMonth")).setValue("Oct");
			if ((eyr == 2003) && (emo > 9))
				((JSpinner) swix.find("spnRunEndMonth")).setValue("Sep");

			// Constrain display times the same way [inefficient?]

			syr = (Integer) ((JSpinner) swix.find("spnStartYear")).getValue();
			eyr = (Integer) ((JSpinner) swix.find("spnEndYear")).getValue();
			smo = UnitsUtils.monthToInt(((String) ((JSpinner) swix.find("spnStartMonth")).getValue()).trim());
			emo = UnitsUtils.monthToInt(((String) ((JSpinner) swix.find("spnEndMonth")).getValue()).trim());
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

}