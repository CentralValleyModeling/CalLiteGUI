/**
 * 
 */
package gov.ca.water.calgui.utils;

import gov.ca.water.calgui.MainMenu;
import gov.ca.water.calgui.results.RBListItem;

import javax.swing.JList;

import calsim.app.Project;

/**
 * 
 * Methods for updating Project values in the WRIMS GUI object space from CalLite GUI
 * 
 * @author tslawecki
 * 
 */
public class WRIMSGUILinks {
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
			project.setDVFile(((RBListItem) theList.getModel().getElementAt(0)).toString());
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
