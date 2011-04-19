package com.limno.calgui;

import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
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
import javax.swing.ProgressMonitor;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.swixml.SwingEngine;

import com.limno.calgui.GetDSSFilename.JFileChooser2;


public class MainMenu implements ActionListener, ItemListener, MouseListener,
		TableModelListener, MenuListener {
	private SwingEngine swix;

	// Declare public Objects
	JFrame desktop;
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
	ProgressMonitor pMon;
	private JList lstScenarios;
	private DSS_Grabber dss_Grabber;

	private SwingEngine swix2;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainMenu() throws Exception {

		// Read GUI configuration

		swix = new SwingEngine(this);
		swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		swix.render(new File("Config\\MainMenu.xml")).setVisible(true);

		//swix2 = new SwingEngine(this);
		//swix2.render(new File("Config\\ReportDialog.xml"));

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
		GUI_Utils.SetCheckBoxItemListener(regulations,this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(operations, this);
		GUI_Utils.SetRadioButtonItemListener(dem_SWP, this);
		GUI_Utils.SetRadioButtonItemListener(dem_CVP, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(hydroclimate, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(facilities, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(Display, this);
		GUI_Utils.SetCheckBoxorRadioButtonItemListener(presets, this);
		GUI_Utils.SetMouseListener(presets, this);
		GUI_Utils.SetMenuListener(menu, this);

		
		
		// Set current directory (Run Settings Page)

		// Set Up Run Settings Page

		JLabel label = (JLabel) swix.find("map");
		java.net.URL imgURL = getClass().getResource("/images/CA_map_and_Delta.jpg");
		if (imgURL != null) {

			BufferedImage img;
			img = ImageIO.read(imgURL);
			ImageIcon image = new ImageIcon(img, null);
			label.setIcon(image);

			//ImageIcon image = new ImageIcon(imgURL, null);
			//label.setIcon(image);
		}
		
		imgURL = getClass().getResource("/images/CalLITE_08_30corrected10-21-10.jpg");
		if (imgURL != null) {
			//ImageIcon image = new ImageIcon(imgURL, null);
			//System.out.println(image.getIconHeight());

			BufferedImage img;
			img = ImageIO.read(imgURL);
			Font defaultFont = new Font("Serif", Font.PLAIN, 20);
			SymbolCanvas symbols = new SymbolCanvas(defaultFont, 0x2500, 207, img);
			symbols.paint(img.getGraphics());

			ImageIcon image = new ImageIcon(img, null);

			ScrollablePicture picture = new ScrollablePicture(image,100);
			picture.setName("schem_map");
			JScrollPane scrollpane = (JScrollPane) swix.find("schem_scr");
			scrollpane.add(picture);
			scrollpane.setViewportView(picture);
			scrollpane.setPreferredSize(new Dimension(800,700));
			scrollpane.setOpaque(true);
			scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			
			GUI_Utils.SetMouseListener(scrollpane, this);

		}

		JSpinner spnSM1 = (JSpinner) swix.find("spnRunStartMonth");
		JSpinner spnEM1 = (JSpinner) swix.find("spnRunEndMonth");
		String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
				"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		SpinnerListModel monthModel = new SpinnerListModel(monthNames);
		SpinnerListModel monthModel2 = new SpinnerListModel(monthNames);
		spnSM1.setModel(monthModel);
		spnSM1.setValue(monthNames[9]);
		spnEM1.setModel(monthModel2);
		spnEM1.setValue(monthNames[9]);

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
		gl.readIn("Config\\GUI_Links.table");

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

		JButton btnClearAll = (JButton) swix.find("btnClearScenario");
		btnClearAll.addActionListener(this);

		JButton btnSetBase = (JButton) swix.find("btnSetBase");
		btnSetBase.addActionListener(this);

		// Set up month spinners
		// TODO (?) - cycling spinner?
		JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
		JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
		spnSM.setModel(monthModel);
		spnEM.setModel(monthModel);
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
		lstReports.setFixedCellWidth(400);
		lstReports.setPreferredSize(new Dimension(600, 100));
		lstReports.setVisible(true);

		
		//PDF Report
		GetDSSFilename getDSSFilename0 = new GetDSSFilename(null,
				(JTextField) swix.find("tfTemplateFILE"), "inp");
		JButton btnFile0 = (JButton) swix.find("btnGetTemplateFile");
		btnFile0.addActionListener((ActionListener) getDSSFilename0);
		GetDSSFilename getDSSFilename1 = new GetDSSFilename(null,
				(JTextField) swix.find("tfReportFILE1"));
		JButton btnFile1 = (JButton) swix.find("btnGetReportFile1");
		btnFile1.addActionListener((ActionListener) getDSSFilename1);
		GetDSSFilename getDSSFilename2 = new GetDSSFilename(null,
				(JTextField) swix.find("tfReportFILE2"));
		JButton btnFile2 = (JButton) swix.find("btnGetReportFile2");
		btnFile2.addActionListener((ActionListener) getDSSFilename2);
		GetDSSFilename getDSSFilename3 = new GetDSSFilename(null,
				(JTextField) swix.find("tfReportFILE3"), "PDF");
		JButton btnFile3 = (JButton) swix.find("btnGetReportFile3");
		btnFile3.addActionListener((ActionListener) getDSSFilename3);
		
		
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) throws Exception {

		new MainMenu();

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
				JPanel pan = (JPanel) swix.find("reg_panTab");
				TitledBorder title;
				component = (JComponent) swix.find("scrRegValues");
				JTable table = (JTable) swix.find("tblRegValues");
				if (component != null)
					component
							.setEnabled((e.getStateChange() == ItemEvent.SELECTED));

				JCheckBox selcomp = (JCheckBox) e.getItem();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					component.setVisible(true);
					component.setEnabled(true);
					String cID = cName.substring(6);
					populateDTable(cID, table, component);

					title = BorderFactory.createTitledBorder(selcomp.getText());
					pan.setBorder(title);
					pan.setEnabled(true);
					component.setEnabled(true);
					table.setVisible(true);

					JButton btn = (JButton) swix.find("btnRegDef");
					btn.setEnabled(false);

				} else {
					pan.setEnabled(false);
					title = BorderFactory.createTitledBorder(selcomp.getText()
							+ " (not selected)");
					pan.setBorder(title);
					component.setEnabled(false);
					table.setVisible(false);

				}

			} else if (cName.startsWith("fac_ckb")) {
				// checkbox in facilities panel changed
				JPanel panel = (JPanel) swix.find("fac_pan"+ cName.substring(7));

				if (panel !=null) {
					// set all "data" panels to invisible
					GUI_Utils.ToggleVisComponentAndChildrenCrit(facilities, "fac_pan", false);
					// set specified "data" panel to active
					GUI_Utils.ToggleVisComponent(panel,true);
					GUI_Utils.ToggleEnComponentAndChildren(panel,e.getStateChange() == ItemEvent.SELECTED);
				}

			} else if (cName.startsWith("dem_rdbUD")) {
				// Checkbox in Demands page changed

				if (cName.startsWith("dem_rdbUD1")) {
					GUI_Utils.ToggleEnComponentAndChildren(dem_UDSWP,e.getStateChange() == ItemEvent.SELECTED, NumericTextField.class );
				} else if (cName.startsWith("dem_rdbUD2")) {
					GUI_Utils.ToggleEnComponentAndChildren(dem_UDCVP,e.getStateChange() == ItemEvent.SELECTED, NumericTextField.class );
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
					GUI_Utils.ToggleEnComponentAndChildren(pan2,e.getStateChange() == ItemEvent.SELECTED, JRadioButton.class );
					GUI_Utils.ToggleEnComponentAndChildren(pan2,e.getStateChange() == ItemEvent.SELECTED, JCheckBox.class );
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
					GUI_Utils.ToggleEnComponentAndChildren(controls2,e.getStateChange() == ItemEvent.SELECTED);
				} else if (cName.startsWith("RepckbSummaryTable")) {
					GUI_Utils.ToggleEnComponentAndChildren(controls3,e.getStateChange() == ItemEvent.SELECTED);
				}
			} else if (cName.startsWith("hyd_ckb")) {
				// Checkbox in Climate Scenarios page changed
				int selct = 0;
				selct=GUI_Utils.CountSelectedButtons(hyd_CC1, JCheckBox.class, selct );
				selct=GUI_Utils.CountSelectedButtons(hyd_CC2, JCheckBox.class, selct );

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
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC,e.getStateChange() != ItemEvent.SELECTED);
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC1,e.getStateChange() != ItemEvent.SELECTED);
					GUI_Utils.ToggleSelComponentAndChildren(hyd_CC1,false, JCheckBox.class );
					GUI_Utils.ToggleSelComponentAndChildren(hyd_CC2,false, JCheckBox.class );
				} else if (cName.startsWith("hyd_rdbMid") || cName.startsWith("hyd_rdbEnd")) {
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC,e.getStateChange() == ItemEvent.SELECTED);
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC1,e.getStateChange() == ItemEvent.SELECTED);
					GUI_Utils.ToggleEnComponentAndChildren(hyd_CC2,e.getStateChange() == ItemEvent.SELECTED);
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
			if (!scen.equals("")) {
				
				/*
				StatusBar statusBar = new StatusBar();
				desktop.add(statusBar, java.awt.BorderLayout.SOUTH);
				statusBar.setMessage("Initializing...");
				statusBar.revalidate();
				*/
				
				//Ask if User wants to save scenario file
				Boolean scensave=false;
				int n = JOptionPane.showConfirmDialog(
						mainmenu,
						"Would you like to save the scenario information?",
						"CalLite Gui",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
						
					//statusBar.setMessage("Saving CalLite Scenario file...");
					//statusBar.revalidate();
					scensave=true;
					StringBuffer sb = new StringBuffer();
					sb =  GUI_Utils.GetControlValues(runsettings, sb);
					sb =  GUI_Utils.GetControlValues(regulations, sb);
					sb =  GUI_Utils.GetControlValues(hydroclimate, sb);
					sb =  GUI_Utils.GetControlValues(demands, sb);
					sb =  GUI_Utils.GetControlValues(operations, sb);
					sb =  GUI_Utils.GetControlValues(facilities, sb);
					
					
					//get table values.
					final String NL = System.getProperty("line.separator");
					sb.append("DATATABLEMODELS" + NL);
					sb= GUI_Utils.GetTableModelData(dTableModels,gl,sb);
					sb.append("END DATATABLEMODELS" + NL);
					
					

					GUI_Utils.CreateNewFile(System.getProperty("user.dir")
							+ "\\Scenarios\\" + scen + ".cls");
					File f=new File(System.getProperty("user.dir")+"\\Scenarios\\" + scen + ".cls");
					try{
						FileWriter fstream = new FileWriter(f);
						BufferedWriter outobj = new BufferedWriter(fstream);
						outobj.write(sb.toString());
						outobj.close();

					}catch (Exception e1){
						System.err.println("Error: " + e1.getMessage());
					}
				}
				
				
				//menu.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				/*
				statusBar.setMessage("Copying default run files...");
				statusBar.revalidate();
				*/
				// Copy Run directory		
				File fs = new File(System.getProperty("user.dir") + "\\Default");
				File ft = new File(System.getProperty("user.dir") + "\\Run");

				// First delete existing Run directory.
				GUI_Utils.deleteDir(ft);

				try {
					GUI_Utils.copyDirectory(fs, ft);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Write options.table
				//pMon.setNote("Writing 'options.table' file...");
				//pMon.setProgress(20);
				Integer LODFlag = 0;
				JRadioButton rdb = (JRadioButton) swix.find("hyd_rdb2005");
				if (rdb.isSelected()) {
					LODFlag = 0;
				} else {
					LODFlag = 1;
				}

				GUI_Utils.ReplaceLineInFile(
						System.getProperty("user.dir")
								+ "\\Run\\Lookup\\options.table",
						13,
						"9       "
								+ LODFlag
								+ "   !Level of Development, LOD_Future = 1 for future and 0 for existing");

				// Copy 2005/2030 lookup tables
				//pMon.setNote("Copying lookup tables...");
            	//pMon.setProgress(30);
				File fsDem;
				rdb = (JRadioButton) swix.find("dem_rdbCurSWP");
				if (rdb.isSelected()) {
					fsDem = new File(System.getProperty("user.dir")
							+ "\\Run\\Lookup\\FutureDemand");
				} else {
					fsDem = new File(System.getProperty("user.dir")
							+ "\\Run\\Lookup\\VariableDemand");	
				}
				GUI_Utils.deleteDir(fsDem);
						
				
                
				// Write study.sty
				//pMon.setNote("Writing 'study.sty' file...");
            	//pMon.setProgress(30);
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
				Integer dayct = 1;
				Integer iEMon = 1;
				Integer iSMon = 1;
				if (StartMon.equals("Apr")) {
					dayct = 30;
					iSMon = 4;
				} else if (StartMon.equals("Jun")) {
					dayct = 30;
					iSMon = 6;
				} else if (StartMon.equals("Sept")) {
					dayct = 30;
					iSMon = 9;
				} else if (StartMon.equals("Nov")) {
					dayct = 30;
					iSMon = 11;
				} else if (StartMon.equals("Feb")) {
					dayct = 28;
					iSMon = 2;
				} else if (StartMon.equals("Jan")) {
					dayct = 31;
					iSMon = 1;
				} else if (StartMon.equals("Mar")) {
					dayct = 31;
					iSMon = 3;
				} else if (StartMon.equals("May")) {
					dayct = 31;
					iSMon = 5;
				} else if (StartMon.equals("Jul")) {
					dayct = 31;
					iSMon = 7;
				} else if (StartMon.equals("Aug")) {
					dayct = 31;
					iSMon = 8;
				} else if (StartMon.equals("Oct")) {
					dayct = 31;
					iSMon = 10;
				} else if (StartMon.equals("Dec")) {
					dayct = 31;
					iSMon = 12;
				}

				if (EndMon.equals("Apr")) {
					iEMon = 4;
				} else if (StartMon.equals("Jun")) {
					iEMon = 6;
				} else if (StartMon.equals("Sept")) {
					iEMon = 9;
				} else if (StartMon.equals("Nov")) {
					iEMon = 11;
				} else if (StartMon.equals("Feb")) {
					iEMon = 2;
				} else if (StartMon.equals("Jan")) {
					iEMon = 1;
				} else if (StartMon.equals("Mar")) {
					iEMon = 3;
				} else if (StartMon.equals("May")) {
					iEMon = 5;
				} else if (StartMon.equals("Jul")) {
					iEMon = 7;
				} else if (StartMon.equals("Aug")) {
					iEMon = 8;
				} else if (StartMon.equals("Oct")) {
					iEMon = 10;
				} else if (StartMon.equals("Dec")) {
					iEMon = 12;
				}

				Integer numMon;
				numMon = (EndYr - StartYr) * 12 + (iEMon - iSMon);
				StartMon = StartMon.toUpperCase();
				
				tf = (JTextField) swix.find("run_txfoDSS");
				String oDSS = tf.getText().trim();

				String[] newtext = new String[20];
				Integer[] LineNum = new Integer[20];

				newtext[0] = scen;
				LineNum[0] = 2;
				newtext[1] = cal.getTime().toString();
				LineNum[1] = 4;
				newtext[2] = System.getProperty("user.dir") + "\\Run";
				LineNum[2] = 7;
				newtext[3] = System.getProperty("user.dir")
						+ "\\Run\\CALLITE_BO_FUTURE.STY";
				LineNum[3] = 8;
				newtext[4] = System.getProperty("user.dir")
						+ "\\Run\\MAIN_BO.WRESL";
				LineNum[4] = 9;
				if (oDSS.toUpperCase().endsWith(".DSS")) {
					newtext[6] = System.getProperty("user.dir")
							+ "\\Scenarios\\" + oDSS;
					LineNum[6] = 11;
				} else {
					newtext[6] = System.getProperty("user.dir")
							+ "\\Scenarios\\" + oDSS + ".DSS";
					LineNum[6] = 11;
				}

				if (LODFlag == 0) {
					newtext[5] = System.getProperty("user.dir")
							+ "\\Run\\DSS\\CL2005A01A021411_SV.DSS";
					LineNum[5] = 10;
					newtext[7] = System.getProperty("user.dir")
							+ "\\Run\\DSS\\CalLite2005A01AINIT.DSS";
					LineNum[7] = 12;
				} else {
					newtext[5] = System.getProperty("user.dir")
							+ "\\Run\\DSS\\CL_FUTURE_WHL021111_SV.DSS";
					LineNum[5] = 10;
					newtext[7] = System.getProperty("user.dir")
							+ "\\Run\\DSS\\CALLITE2020D09EINIT.DSS";
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

				GUI_Utils.ReplaceLinesInFile(System.getProperty("user.dir")
						+ "\\Run\\study.sty", LineNum, newtext);
				
				/*
				//Sea Level Selections
				File fsAnnO = new File(System.getProperty("user.dir")+ "\\Model\\Ann.dll");
				File fsAnnS;
				JRadioButton rdbSLR45 = (JRadioButton) swix.find("hyd_rdb1");
				JRadioButton rdbSLR15 = (JRadioButton) swix.find("hyd_rdb2");
				if (rdbSLR45.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir")+ "\\Default\\\External\\Ann7inp_BDCP_LLT_45cm.dll");
				} else if (rdbSLR15.isSelected()) {
					fsAnnS = new File(System.getProperty("user.dir")+ "\\Default\\\External\\Ann7inp_BDCP_ELT_15cm.dll");
				} else {
					fsAnnS = new File(System.getProperty("user.dir")+ "\\Default\\\External\\Ann7inp_BST_noSLR_111709.dll");
				}
				try {
					GUI_Utils.copyDirectory(fsAnnS, fsAnnO);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				*/

				// Write SOD_demand_definitions.table
				//pMon.setNote("Writing 'SOD_demand_definitions.table' file...");
            	//pMon.setProgress(80);
				Integer SWPFlag = 0;
				Integer CVPFlag = 0;
				JRadioButton rdb1 = (JRadioButton) swix.find("dem_rdbCurSWP");
				JRadioButton rdb2 = (JRadioButton) swix.find("dem_rdbFutSWP");
				JRadioButton rdb3 = (JRadioButton) swix.find("dem_rdbFullCVP");
				NumericTextField ntf1 = (NumericTextField) swix.find("demtxt1");
				NumericTextField ntf2 = (NumericTextField) swix.find("demtxt2");
				NumericTextField ntf3 = (NumericTextField) swix.find("demtxt3");
				NumericTextField ntf4 = (NumericTextField) swix.find("demtxt4");
				NumericTextField ntf5 = (NumericTextField) swix.find("demtxt5");
				NumericTextField ntf6 = (NumericTextField) swix.find("demtxt6");
				if (rdb1.isSelected()) {
					SWPFlag = 1;
				} else if (rdb2.isSelected()) {
					SWPFlag = 2;
				} else {
					SWPFlag = 3;
				}
				if (rdb3.isSelected()) {
					CVPFlag = 1;
				} else {
					CVPFlag = 2;
				}
				String dem;
				dem = String.format("%-10s", "1")
						+ String.format("%-10s", SWPFlag.toString())
						+ String.format("%-10s", ntf1.getText())
						+ String.format("%-10s", ntf2.getText())
						+ String.format("%-10s", ntf3.getText())
						+ String.format("%-10s", CVPFlag.toString())
						+ String.format("%-10s", ntf4.getText())
						+ String.format("%-10s", ntf5.getText())
						+ String.format("%-10s", ntf6.getText());
				GUI_Utils.ReplaceLineInFile(System.getProperty("user.dir")
						+ "\\Run\\Lookup\\SOD_demand_options.table", 3, dem);

				// Write DLTREGULATION file
				OutputStream outputStream;
				//pMon.setNote("Writing 'DLTREGULATION.table' file...");
            	//pMon.setProgress(90);
				try {
					outputStream = new FileOutputStream(
							System.getProperty("user.dir")
									+ "\\Run\\Lookup\\DLTREGULATION.table");
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
						JCheckBox cb = (JCheckBox) swix.find("ckbReg" + cID);
						if (cb == null) {
							option = 0;
						} else if (!cb.isSelected()) {
							option = 0;
						} else {
							option = 1;
						}

						output.println(switchID + " " + option);

						// Output table if needed
						// if ((option == 2)
						// || ((option == 1) && (swix.find("btnU" + cID))
						// .isVisible())) {
						if ((option == 2) || (option == 1)) {

							String fileName = gl.tableNameForCtrl(cID);
							
							String[] files = fileName.split("[|]");
							int size = files.length;							
							if (size == 1) {
								// CASE 1: 1 file specified
								System.out.println("Output to "
										+ fileName);
								String fo = System.getProperty("user.dir")
										+ "\\Run\\Lookup\\"+ fileName + ".table";
								
								int tID = Integer.parseInt(cID);
								if (dTableModels[tID] == null) {
									System.out.println("Table not initialized");
								} else {
									dTableModels[tID].writeToFile(fo);
								}
							} else if (size == 2) {
								// CASE 2: 2 files specified
								System.out.println("Output to "
										+ files[0]);
								String fo1=System.getProperty("user.dir")
								+ "\\Run\\Lookup\\"+ files[0] + ".table";
								String fo2=System.getProperty("user.dir")
								+ "\\Run\\Lookup\\"+ files[1] + ".table";

								int tID = Integer.parseInt(cID);
								if (dTableModels[tID] == null) {
									System.out.println("Table not initialized");
								} else {
									dTableModels[tID].writeToFile2(fo1, fo2);
								}

							}

						}
					}
					
					output.close();
					outputStream.close();
				} catch (IOException ioe) {
					System.out.println("IOException");
				}

				//menu.setCursor(Cursor.getDefaultCursor());

				// "Run" model
				//pMon.setNote("Running CalLite...");
            	//pMon.setProgress(100);
            	//timer.stop();
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
			} else {
				JFrame frame = new JFrame("Error");

				// show a joptionpane dialog using showMessageDialog
				JOptionPane.showMessageDialog(frame,
						"You must specify a scenario name.");

			}
		} else if (e.getActionCommand().startsWith("AC_SaveScen")) {

			JTextField tf = (JTextField) swix.find("run_txfScen");
			String scen = tf.getText();
			if (!scen.equals("")) {
				
				StringBuffer sb = new StringBuffer();
				sb =  GUI_Utils.GetControlValues(runsettings, sb);
				sb =  GUI_Utils.GetControlValues(regulations, sb);
				sb =  GUI_Utils.GetControlValues(hydroclimate, sb);
				sb =  GUI_Utils.GetControlValues(demands, sb);
				sb =  GUI_Utils.GetControlValues(operations, sb);
				sb =  GUI_Utils.GetControlValues(facilities, sb);
				
				//get table values.
				final String NL = System.getProperty("line.separator");
				sb.append("DATATABLEMODELS" + NL);
				sb= GUI_Utils.GetTableModelData(dTableModels,gl,sb);
				sb.append("END DATATABLEMODELS" + NL);

				GUI_Utils.CreateNewFile(System.getProperty("user.dir")
						+ "\\Scenarios\\" + scen + ".cls");
				File f=new File(System.getProperty("user.dir")+"\\Scenarios\\" + scen + ".cls");


				try{
					FileWriter fstream = new FileWriter(f);
					BufferedWriter outobj = new BufferedWriter(fstream);
					outobj.write(sb.toString());
					outobj.close();

				}catch (Exception e1){
					System.err.println("Error: " + e1.getMessage());
				}
			} else {
				JFrame frame = new JFrame("Error");

				// show a joptionpane dialog using showMessageDialog
				JOptionPane.showMessageDialog(frame,
				"You must specify a scenario name.");

			}			
			
            
		} else if (e.getActionCommand().startsWith("AC_LoadScen")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new ScenarioFileFilter());
			fc.setCurrentDirectory(new File(".//Scenarios"));
			
			String dirname = ".//Scenarios"; 

            int retval = fc.showOpenDialog(mainmenu);
            if (retval == JFileChooser.APPROVE_OPTION) {
                //... The user selected a file, get it, use it.
                File file = fc.getSelectedFile();
                
                GUI_Utils.SetControlValues(file, swix, dTableModels, gl);
			
            }
                
            
		} else if (e.getActionCommand().startsWith("Reg_Copy")) {

			JTable table = (JTable) swix.find("tblRegValues");
			ActionEvent ae = new ActionEvent(table,
					ActionEvent.ACTION_PERFORMED, "copy");
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
						if (startRow + i < table.getRowCount()
								&& startCol + j < table.getColumnCount())
							table.setValueAt(value, startRow + i, startCol + j);
						table.repaint();
						System.out.println("Putting " + value + " at row = "
								+ startRow + i + ", column = " + startCol + j);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/*
			 * JTable table =(JTable) swix.find("tblRegValues"); ActionEvent ae
			 * = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "paste");
			 * //table.selectAll();
			 * table.getActionMap().get(ae.getActionCommand(
			 * )).actionPerformed(ae); table.repaint();
			 */
		} else if (e.getActionCommand().startsWith("Reg_Default")) {

			JTable table = (JTable) swix.find("tblRegValues");
			DataFileTableModel tm = (DataFileTableModel) table.getModel();
			tm.initVectors();
			table.repaint();

			JButton btn = (JButton) swix.find("btnRegDef");
			btn.setEnabled(false);

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
		} else if (e.getActionCommand().startsWith("AC_Report")) {
			try {
				dialog.setVisible(true);
				swix2.setActionListener(reportdialog, this);
				GetDSSFilename getDSSFilename0 = new GetDSSFilename(null,
						(JTextField) swix2.find("tfTemplateFILE"), "inp");
				JButton btnFile0 = (JButton) swix2.find("btnGetTemplateFile");
				btnFile0.addActionListener((ActionListener) getDSSFilename0);
				GetDSSFilename getDSSFilename1 = new GetDSSFilename(null,
						(JTextField) swix2.find("tfReportFILE1"));
				JButton btnFile1 = (JButton) swix2.find("btnGetReportFile1");
				btnFile1.addActionListener((ActionListener) getDSSFilename1);
				GetDSSFilename getDSSFilename2 = new GetDSSFilename(null,
						(JTextField) swix2.find("tfReportFILE2"));
				JButton btnFile2 = (JButton) swix2.find("btnGetReportFile2");
				btnFile2.addActionListener((ActionListener) getDSSFilename2);
				GetDSSFilename getDSSFilename3 = new GetDSSFilename(null,
						(JTextField) swix2.find("tfReportFILE3"), "PDF");
				JButton btnFile3 = (JButton) swix2.find("btnGetReportFile3");
				btnFile3.addActionListener((ActionListener) getDSSFilename3);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
		} else if (e.getActionCommand().startsWith("AC_GenReport")) {
			if (((JTextField) swix2.find("tfReportFILE1")).getText().isEmpty()
					|| ((JTextField) swix2.find("tfReportFILE2")).getText()
							.isEmpty()
					|| ((JTextField) swix2.find("tfReportFILE3")).getText()
							.isEmpty()) {
				JOptionPane
						.showMessageDialog(
								null,
								"You must specify the source DSS files and the output PDF file",
								"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				Report report = null;
				dialog.setVisible(false);
				try {
					// Create an inputstream from template file;
					FileInputStream fin = new FileInputStream(
							((JTextField) swix2.find("tfTemplateFILE"))
									.getToolTipText());
					BufferedReader br = new BufferedReader(
							new InputStreamReader(fin));
					// Open the template file
					String theText = br.readLine() + "\n";
					theText = theText + br.readLine() + "\n";
					theText = theText + br.readLine() + "\n";
					br.readLine();
					theText = theText
							+ "FILE1\t"
							+ ((JTextField) swix2.find("tfReportFILE1"))
									.getToolTipText() + "\n";
					br.readLine();
					theText = theText
							+ "NAME1\t"
							+ ((JTextField) swix2.find("tfReportNAME1"))
									.getText() + "\n";
					br.readLine();
					theText = theText
							+ "FILE2\t"
							+ ((JTextField) swix2.find("tfReportFILE2"))
									.getToolTipText() + "\n";
					br.readLine();
					theText = theText
							+ "NAME2\t"
							+ ((JTextField) swix2.find("tfReportNAME2"))
									.getText() + "\n";
					br.readLine();
					theText = theText
							+ "OUTFILE\t"
							+ ((JTextField) swix2.find("tfReportFILE3"))
									.getToolTipText() + "\n";
					br.readLine();
					theText = theText
							+ "NOTE\t\""
							+ ((JTextArea) swix2.find("taReportNOTES"))
									.getText() + "\"\n";
					br.readLine();
					theText = theText
							+ "ASSUMPTIONS\t\""
							+ ((JTextArea) swix2.find("taReportASSUMPTIONS"))
									.getText() + "\"\n";
					br.readLine();
					theText = theText
							+ "MODELER\t\""
							+ ((JTextField) swix2.find("tfReportMODELER"))
									.getText() + "\"\n";
					System.out.println(theText);
					String aLine = br.readLine();
					while (aLine != null) {
						theText = theText + aLine + "\n";
						aLine = br.readLine();
					}
					ByteArrayInputStream bs = new ByteArrayInputStream(
							theText.getBytes());
					report = new Report((InputStream) bs);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				report.getOutputFile();
			}

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
			
			String cAdd;
			cAdd = "";
			// Comparison and Difference
			JCheckBox ckb = (JCheckBox) swix.find("ckbp001");
			if (ckb.isSelected()) {
				cAdd = cAdd+ "Comp";
			}
			ckb = (JCheckBox) swix.find("ckbp002");
			if (ckb.isSelected()) {
				cAdd = cAdd+";Diff";
			}
			// Units
			JRadioButton rdb = (JRadioButton) swix.find("rdbCFS");
			if (rdb.isSelected()) {
				cAdd = cAdd+";CFS";
			} else {
				cAdd = cAdd+";CFS";
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
			cAdd = cAdd + ";" + cDate;

			// Time Series
			ckb = (JCheckBox) swix.find("RepckbTimeSeriesPlot");
			if (ckb.isSelected()) {
				cAdd = cAdd+";TS";
			}
			
			//Exceedence Plot
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
							cST = cST + ","+ cName;
						}
					}
				}
				cAdd = cAdd + cST;
			}			
			
			// Monthly Table
			ckb = (JCheckBox) swix.find("RepckbMonthlyTable");
			if (ckb.isSelected()) {
				cAdd = cAdd+";Monthly";
			}
			
			//Summary Table
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
							cST = cST + ","+ cName;
						}
					}
				}
				cAdd = cAdd + cST;
			}

			String cSTOR;
			cSTOR = ";";
			components = presets.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					String cName = c.getName();
					if (cName.startsWith("ckbp")) {
						boolean b = c.isSelected();
						if (b == true) {
							cSTOR = cSTOR + ";" + c.getText().trim();
						}
					}
				}
			}
			cAdd = cAdd + cSTOR;

			lstArray1[n] = cAdd;

			// String[] reportNamesEG = {cDate};
			lstReports.setListData(lstArray1);

		}
		

		else if (e.getActionCommand().startsWith("Sch_Load")) {

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new DSSFileFilter());
			fc.setCurrentDirectory(new File(".//Scenarios"));
			
			String dirname = ".//Scenarios"; 

            int retval = fc.showOpenDialog(mainmenu);
            if (retval == JFileChooser.APPROVE_OPTION) {
                //... The user selected a file, get it, use it.
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

			try {

				FileInputStream fin = new FileInputStream(
						"\\Config\\reportlist.cgr");
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

			//Delete existing reportlist
			File f = new File(System.getProperty("user.dir") + "\\Config\\reportlist.cgr");
			GUI_Utils.deleteDir(f);
			GUI_Utils.CreateNewFile(System.getProperty("user.dir") + "\\Config\\reportlist.cgr");
			
			OutputStream outputStream;
			try {
				outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\Config\\reportlist.cgr");
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
		} else if (e.getActionCommand().startsWith("Sch_NOD")) {
			JScrollPane scr = (JScrollPane) swix.find("schem_scr");
		    JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
		    verticalScrollBar.setValue(verticalScrollBar.getMinimum());
			
		} else if (e.getActionCommand().startsWith("Sch_Delta")) {
			JScrollPane scr = (JScrollPane) swix.find("schem_scr");
		    JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
		    verticalScrollBar.setValue((int) ((verticalScrollBar.getMaximum()-verticalScrollBar.getMinimum())*0.25));
			
		} else if (e.getActionCommand().startsWith("Sch_SOD")) {
			JScrollPane scr = (JScrollPane) swix.find("schem_scr");
		    JScrollBar verticalScrollBar = scr.getVerticalScrollBar();
		    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			
		} else {
			JComponent component = (JComponent) e.getSource();
			String cName = component.getName();
			if (cName != null) {
				if (component.getName().equals("btnSetBase")) {
					if (lstScenarios.getSelectedIndex() >= 0) {
						JLabel label = (JLabel) swix.find("lblBase");
						label.setText(new File((String) lstScenarios
								.getSelectedValue()).getName());
						label.setToolTipText((String) lstScenarios
								.getSelectedValue());
					}

				} else if (component.getName().equals("btnClearScenario")) {
					((DefaultListModel) lstScenarios.getModel()).clear();
					lstScenarios.repaint();

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
			dTableModels[tID] = new DataFileTableModel("Default\\Lookup\\"
					+ fileName + ".table");
		}

		TableDialog td = new TableDialog(fileName, dTableModels[tID]);
		td.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		td.setVisible(true);
	}

	@SuppressWarnings("unused")
	protected void populateDTable(String cID, final JTable t,
			JComponent container) {

		boolean exists = false;
		String fileName = gl.tableNameForCtrl(cID);
		
		if (fileName != null) {

			String[] files = fileName.split("[|]");
			int size = files.length;

			if (size == 1) {
				// CASE 1: 1 file specified
				fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" + fileName + ".table";
				File f = new File(fileName);
				exists = f.exists();
			} else if (size == 2) {
				// CASE 2: 2 files specified
				fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[0]+ ".table";
				File f = new File(fileName);
				exists = f.exists();
				if (exists) {
					fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[1]+ ".table";
					f = new File(fileName);
					exists = f.exists();
					fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[0]+ ".table" + "|" + System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[1]+ ".table";
				}
			}

		}
		
		//File f = new File("Default\\Lookup\\" + fileName + ".table");
		//boolean exists = f.exists();
		if (!exists) {
			container.setVisible(false);
		} else {

			int tID = Integer.parseInt(cID);
			if (dTableModels == null) {
				dTableModels = new DataFileTableModel[20];
			}
			if (dTableModels[tID] == null) {
				dTableModels[tID] = new DataFileTableModel(fileName);
			}
			// dTableModels[tID].addTableModelListener(this);

			t.setModel(dTableModels[tID]);
			t.createDefaultColumnsFromModel();

			t.setRowHeight(20);
			for (int col = 0; col < t.getColumnCount(); col++) {
				t.getColumnModel().getColumn(col).setWidth(50);
			}

			t.setPreferredScrollableViewportSize(new Dimension(t
					.getColumnCount() * 60 + 60, t.getRowCount() * 20));

			t.setCellSelectionEnabled(true);
			t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

			t.getModel().addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e) {

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

			ExcelAdapter myAd = new ExcelAdapter(t);

		}

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
						dss_Grabber.setBase(label.getToolTipText());

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

					    JTabbedPane tabbedpane = new JTabbedPane();
						ChartPanel1 cp1;
						ChartPanel1 cp2;
						if (primary_Results.length > 1) {
							cp1 = new ChartPanel1("Comparison " + dss_Grabber.primaryDSSName, primary_Results, secondary_Results);
						    tabbedpane.insertTab("Comparison", null, cp1, null, 0);
							cp2 = new ChartPanel1("Difference " + dss_Grabber.primaryDSSName, diff_Results, null);
						} else {
							cp2 = new ChartPanel1(dss_Grabber.primaryDSSName, primary_Results, secondary_Results);
						}
					    tabbedpane.insertTab("CP2", null, cp2, null, 0);
					    
						ChartPanel1 cp3;
						cp3 = new ChartPanel1("Exceedance " + dss_Grabber.primaryDSSName, exc_Results, null);
					    tabbedpane.insertTab("Exceedance", null, cp3, null, 0);

						SummaryTablePanel stp = new SummaryTablePanel(primary_Results[0]);
					    tabbedpane.insertTab("Summary", null, stp	, null, 0);
					    
					    MonthlyTablePanel mtp = new MonthlyTablePanel(primary_Results[0]);
					    tabbedpane.insertTab("Monthly", null, mtp , null, 0);


						// Show the frame
						JFrame frame = new JFrame();
						
						Container container = frame.getContentPane();
						container.add(tabbedpane);
						frame.pack();
						frame.setTitle("CalLite Results - " + chk.getText());
						frame.setVisible(true);	

						//DoAChart dc = new DoAChart();
						//dc.main(primary_Results[0]);


						chk.setFont(new Font("Tahoma", Font.BOLD, 12));
						chk.repaint();
					}
				} else if (cName.startsWith("schem_map")) {
					JFrame frame = new JFrame("Test");

					Point b = e.getPoint();
					int x = (int) b.getX();
					int y = (int) b.getY();
					
					// show a joptionpane dialog using showMessageDialog
					JOptionPane.showMessageDialog(frame,
							"X = " + x + "; Y = " + y);
					
						
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
		//System.out.print("Clicked! " + ((JMenu) e.getSource()).getText()); //action depending on text/name ...
	}
	
}