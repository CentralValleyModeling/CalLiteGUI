package gov.ca.water.calgui;

import gov.ca.water.calgui.dashboards.ScenarioFrame;
import gov.ca.water.calgui.dashboards.ScenarioTable;
import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.FileUtils;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

/**
 * The FileAction class is the action listener for menubar "menu" and panel "runsettings" in the GUI.
 * 
 * @author drucinski, tslawecki
 * 
 */
public class FileAction implements ActionListener {

	private final JFrame desktop;
	private final SwingEngine swix;
	private Boolean[] regUserEdits;
	private final DataFileTableModel[] dTableModels;
	private final GUILinks gl;
	private int[] RegFlags;
	private static Logger log = Logger.getLogger(FileAction.class.getName());
	private static Properties properties = new Properties();

	/**
	 * Constructor for FileAction
	 * 
	 * @param desktop
	 * @param swix
	 * @param regUserEdits
	 * @param dTableModels
	 * @param gl
	 * @param action_WSIDI
	 */
	public FileAction(JFrame desktop, SwingEngine swix, Boolean[] regUserEdits, DataFileTableModel[] dTableModels, GUILinks gl,
	        int action_WSIDI, int[] regFlags) {
		this.desktop = desktop;
		this.swix = swix;
		this.regUserEdits = regUserEdits;
		this.dTableModels = dTableModels;
		this.gl = gl;
		this.RegFlags = regFlags;

		// create and load default properties
		try {

			properties.load(FileAction.class.getClassLoader().getResourceAsStream("callite-gui.properties"));

		} catch (Exception e) {

			log.debug("Problem loading properties. " + e.getMessage());
		}

	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		JPanel mainmenu = (JPanel) swix.find("mainmenu");
		String scen = ((JTextField) swix.find("run_txfScen")).getText();

		if ("AC_BATCH".equals(ae.getActionCommand())) {

			RunUtils.doBatch(ae);

		} else if ("AC_RUN".equals(ae.getActionCommand())) {

			RunUtils.doSingle(ae, mainmenu, scen);

		} else if (ae.getActionCommand().startsWith("AC_SaveScen")) {

			boolean proceed = true;

			if (ae.getActionCommand().equals("AC_SaveScenAs")) {

				// Save scenario as ...
				FileDialog scenFileDialog;
				scenFileDialog = new FileDialog(null, (JTextField) swix.find("run_txfScen"), "CLS");
				scenFileDialog.actionPerformed(ae);
				if (scenFileDialog.dialogRC != 0)
					proceed = false;
				else {
					RunUtils.setFilenameTooltips();
				}
			}
			if (proceed) {
				scen = ((JTextField) swix.find("run_txfScen")).getText();
				if ((new File(System.getProperty("user.dir") + "\\Scenarios\\" + scen)).exists())
					proceed = (JOptionPane.showConfirmDialog(mainmenu, "The scenario file '" + System.getProperty("user.dir")
					        + "\\Scenarios\\" + scen + "' already exists. Press OK to overwrite.", "CalLite GUI - " + scen,
					        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);

				if (proceed) {
					RunUtils.saveFile(scen, swix, regUserEdits, dTableModels, gl);
					RunUtils.setupScenario(scen, "", desktop, swix, regUserEdits, dTableModels, gl, RegFlags);
				} else {
					JFrame frame = new JFrame("Error");
					JOptionPane.showMessageDialog(frame, "You must specify a scenario name.");
				}
			}

		} else if (ae.getActionCommand().startsWith("AC_LoadScen")) {

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("CalLite Scenario Files *.cls", "cls"));
			fc.setCurrentDirectory(new File(".//Scenarios"));
			int retval = fc.showOpenDialog(mainmenu);
			if (retval == JFileChooser.APPROVE_OPTION) {

				// ... The user selected a file, get it, use it.

				File file = fc.getSelectedFile();
				FileDialog scenFileDialog;
				scenFileDialog = new FileDialog(null, (JTextField) swix.find("run_txfScen"), "CLS");
				scenFileDialog.fc.setSelectedFile(file); // Use this name for next Save As

				regUserEdits = GUIUtils.setControlValues(file, swix, dTableModels, gl, regUserEdits);
				// regUserEdits = GUIUtils.setControlValues(file, swix, dTableModels, gl);
				RegFlags = GUIUtils.setControlValues(file, swix, gl, RegFlags);
				RunUtils.setFilenameTooltips();
			}

		} else if (ae.getActionCommand().startsWith("AC_CompScen")) {

			// Save current settings to temp file
			RunUtils.saveFile("Current_Scenario", swix, regUserEdits, dTableModels, gl);
			ScenarioFrame ScenFrame = new ScenarioFrame("CalLite 2.0 GUI - Scenario Comparison", swix);
			ScenFrame.setVisible(true);

		} else if (ae.getActionCommand().startsWith("AC_ViewScen")) {

			// Save current settings to temp file
			RunUtils.saveFile("Current_Scenario", swix, regUserEdits, dTableModels, gl);
			// Build list
			Object scenlist[] = { "Current_Scenario" };
			// Show frame
			ScenarioTable sTableFrame = new ScenarioTable(scenlist, swix);
			// java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
			// sTableFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
			sTableFrame.setVisible(true);
			// Delete temp file
			File ft = new File(System.getProperty("user.dir") + "\\Scenarios\\Current_Scenario");
			FileUtils.deleteDir(ft);
		}

		else if (ae.getActionCommand().equals("AC_Help")) {

			try {

				JTabbedPane jtp = (JTabbedPane) swix.find("tabbedPane1");
				String label = jtp.getTitleAt(jtp.getSelectedIndex());
				CalLiteHelp calLiteHelp = new CalLiteHelp();
				calLiteHelp.showHelp(label);

			} catch (Exception e) {

				log.debug("Problem with CalLite Help " + e.getMessage());

			}

		} else if (ae.getActionCommand().equals("AC_About")) {

			Long longTime = new File("Config/GUI.xml").lastModified();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("PST"));
			calendar.setTimeInMillis(longTime);
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
			String guiXmlDate = sdf.format(calendar.getTime());

			JOptionPane.showMessageDialog(null, "CalLite v. " + properties.getProperty("version.id") + "\nBuild date: "
			        + properties.getProperty("build.date") + "\nYour last GUI xml revision date: " + guiXmlDate, "About CalLite",
			        JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
