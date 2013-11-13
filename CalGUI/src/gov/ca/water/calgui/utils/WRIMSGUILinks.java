/**
 * 
 */
package gov.ca.water.calgui.utils;

import gov.ca.water.calgui.MainMenu;
import gov.ca.water.calgui.results.RBListItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JList;

import org.apache.log4j.Logger;

import calsim.app.Project;

/**
 * 
 * Methods for updating Project values in the WRIMS GUI object space from CalLite GUI
 * 
 * @author tslawecki
 * 
 */
public class WRIMSGUILinks {

	static Logger log = Logger.getLogger(WRIMSGUILinks.class.getName());

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

		// Find and set files

		if (theList.getModel().getSize() == 1) {

			String dvFileName = ((RBListItem) theList.getModel().getElementAt(0)).toString();
			project.setDVFile(dvFileName);

			// Find scenario file
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
				log.info(clsF.getName() + " not openable");
				e.printStackTrace();
			}

			// Build string pointing to "Scenarios/Run_Details/scenarioname/Run/DSS/svfilename"

			String svPathString = dvFileName.substring(0, dvFileName.length() - 7); // Strip out "_DV.DSS"
			int i = svPathString.lastIndexOf("\\"); // find rightmost "/"
			svFileName = svPathString.substring(0, i) + "\\Run_Details" + svPathString.substring(i) + "\\Run\\DSS\\" + svFileName;
			project.setSVFile(svFileName);

		} else {

			int dssCount = 1;
			for (int i = 0; i < theList.getModel().getSize(); i++) {

				RBListItem item = (RBListItem) theList.getModel().getElementAt(i);
				if (item.isSelected()) {
					project.setDVFile(item.toString());
				} else {
					dssCount++;
					switch (dssCount) {
					case 2:
						project.setDV2File(item.toString());
						break;
					case 3:
						project.setDV3File(item.toString());
						break;
					case 4:
						project.setDV4File(item.toString());
						break;
					}
				}
			}
		}
	}
}
