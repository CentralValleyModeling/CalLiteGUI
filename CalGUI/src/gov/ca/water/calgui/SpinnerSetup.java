package gov.ca.water.calgui;

import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

public class SpinnerSetup {

	private static Logger log = Logger.getLogger(MainMenu.class.getName());

	public static void SetNumberModelAndIndex(JSpinner jspn, int val, int min, int max, int step, String format, Object obj,
	        boolean changelistener) {

		SpinnerModel spnmod = new SpinnerNumberModel(val, min, max, step);
		jspn.setModel(spnmod);
		jspn.setEditor(new JSpinner.NumberEditor(jspn, format));
		if (changelistener == true) {
			jspn.addChangeListener((ChangeListener) obj);
		}
	}

	public static void SetMonthModelAndIndex(JSpinner jspn, int idx, Object obj, boolean changelistener) {
		String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		try {
			SpinnerListModel monthModel = new SpinnerListModel(monthNames);
			jspn.setModel(monthModel);
			jspn.setValue(monthNames[idx]);
			if (changelistener == true) {
				jspn.addChangeListener((ChangeListener) obj);
			}
		}

		catch (Exception e) {
			log.debug("Problem reading table files. " + e);
		}
	}

}
