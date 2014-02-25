/**
 * 
 */
package gov.ca.water.calgui.utils;

import gov.ca.water.calgui.MainMenu;
import gov.ca.water.calgui.results.RBListItem;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import calsim.app.AppUtils;
import calsim.app.Project;
import calsim.gui.CalLiteGUIPanelWrapper;
import calsim.gui.GuiUtils;

/**
 * 
 * Methods for updating Project values in the WRIMS GUI object space from CalLite GUI
 * 
 * @author tslawecki
 * 
 */
public class WRIMSGUILinks {

	static Logger log = Logger.getLogger(WRIMSGUILinks.class.getName());

	private static JLabel statusLabel;

	public static void setStatus(String text) {
		statusLabel.setText(text);
	}

	public static void buildWRIMSGUI(JPanel p) {

		p.setSize(900, 650);

		CalLiteGUIPanelWrapper pw = new CalLiteGUIPanelWrapper(MainMenu.desktop);
		pw.getPanel().setSize(900, 650);
		p.add(pw.getPanel(), BorderLayout.NORTH);
		JPanel statusPanel = GuiUtils.getStatusPanel();
		p.add(statusPanel, BorderLayout.CENTER);
		GuiUtils.setStatus("Initialized.");

		statusLabel = (JLabel) statusPanel.getComponent(2);
	}

	/**
	 * Update WRIMS GUI project file names from file list
	 */
	public static void updateProjectFiles(JList theList) {

		// Get project and clear

		Project project = MainMenu.getProject();
		project.setDVFile("");
		project.setDV2File("");
		project.setDV3File("");
		project.setDV4File("");
		AppUtils.baseOn = false;

		// Find and set files

		if (theList.getModel().getSize() == 1) {

			RBListItem item = (RBListItem) theList.getModel().getElementAt(0);
			String dvFileName = item.toString();
			project.setDVFile(dvFileName);

			String svFileName = item.getSVFilename();
			if (svFileName.equals("")) {
				svFileName = findSVFileName(dvFileName);
				item.setSVFilename(svFileName);
			}
			project.setSVFile(svFileName);
			System.out.println("List of 1 - " + svFileName + " - " + dvFileName);

			AppUtils.baseOn = true;

		} else {

			int dssCount = 1;
			for (int i = 0; i < theList.getModel().getSize(); i++) {

				RBListItem item = (RBListItem) theList.getModel().getElementAt(i);
				String dvFileName = item.toString();
				String svFileName = item.getSVFilename();
				if (svFileName.equals("")) {
					svFileName = findSVFileName(dvFileName);
					item.setSVFilename(svFileName);
				}
				if (item.isSelected()) {
					project.setDVFile(dvFileName);
					project.setSVFile(svFileName);
					AppUtils.baseOn = true;
				} else {
					dssCount++;
					switch (dssCount) {
					case 2:
						project.setDV2File(dvFileName);
						project.setSV2File(svFileName);
						break;
					case 3:
						project.setDV3File(dvFileName);
						project.setSV3File(svFileName);
						break;
					case 4:
						project.setDV4File(dvFileName);
						project.setSV4File(svFileName);
						break;
					}
				}
			}
		}
	}

	/**
	 * Finds scenario's SV file by looking up SV file name in scenario (.cls) file
	 * 
	 * @param dvFileName
	 * @return Full name of SV file including path
	 */

	private static String findSVFileName(String dvFileName) {

		// CalLite scenario file is in the same directory as the DV file

		String clsFileName = dvFileName.substring(0, dvFileName.length() - 7) + ".cls";
		File clsF = new File(clsFileName);
		String svFileName = "";
		try {
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(clsF.getAbsolutePath()));
			while (scanner.hasNextLine() && svFileName.equals("")) {
				String text = scanner.nextLine();
				if (text.startsWith("hyd_DSS_SV|")) {

					String[] texts = text.split("[|]");
					svFileName = texts[1];
				}
			}
			scanner.close();

		} catch (IOException e) {
			log.info(clsF.getName() + " not openable - checking for like-named SV file");
		}

		if (!svFileName.equals("")) {

			// Found in CLS - Build string pointing to "Scenarios/Run_Details/scenarioname/Run/DSS/svfilename"

			String svPathString = dvFileName.substring(0, dvFileName.length() - 7); // Strip out "_DV.DSS"
			int i = svPathString.lastIndexOf("\\"); // find rightmost "/"
			svFileName = svPathString.substring(0, i) + "\\Run_Details" + svPathString.substring(i) + "\\Run\\DSS\\" + svFileName;
		} else {

			// Not found in CLS: first, check if there's a corresponding SV.DSS
			if (dvFileName.substring(dvFileName.length() - 6, dvFileName.length()).toUpperCase().equals("DV.DSS")) {
				svFileName = dvFileName.substring(0, dvFileName.length() - 6) + "SV.dss";
				File svF = new File(svFileName);
				if (svF.exists() && !svF.isDirectory())
					log.info("Found like-named SV file - " + svFileName);
				else
					svFileName = "";

				if (svFileName.equals("")) {

					// No corresponding SV.DSS - get file from file dialog!

					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(dvFileName));
					fc.setDialogTitle("Set SV file for " + dvFileName);
					fc.setFileFilter(new FileNameExtensionFilter("DSS File *.dss", "dss"));
					if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						svFileName = fc.getSelectedFile().getAbsolutePath();
						log.info("SV file set - " + svFileName);
					} else
						log.info("No SV file set for " + dvFileName + "!");
				}
			}
		}

		return svFileName;
	}
}
