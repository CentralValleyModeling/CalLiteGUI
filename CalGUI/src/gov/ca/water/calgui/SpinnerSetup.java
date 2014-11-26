package gov.ca.water.calgui;

import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

/**
 * Sets up Spinner models for number range, months
 * 
 * @author tslawecki
 * 
 */
public class SpinnerSetup {

	private static Logger log = Logger.getLogger(MainMenu.class.getName());

	/**
	 * Sets up a spinner for a numeric range
	 * 
	 * @param jspn
	 *            - Swing spinner component
	 * @param val
	 *            - Initial value
	 * @param min
	 *            - Minimum value
	 * @param max
	 *            - Maximum value
	 * @param step
	 *            - Increment between values
	 * @param format
	 *            - Format for display
	 * @param obj
	 *            - ChangeListener
	 * @param changelistener
	 *            - True is a ChangeListener is to be assigned
	 */
	public static void SetNumberModelAndIndex(JSpinner jspn, int val, int min, int max, int step, String format, Object obj,
	        boolean changelistener) {

		SpinnerModel spnmod = new SpinnerNumberModel(val, min, max, step);
		jspn.setModel(spnmod);
		jspn.setEditor(new JSpinner.NumberEditor(jspn, format));
		if (changelistener == true) {
			jspn.addChangeListener((ChangeListener) obj);
		}
	}

	/**
	 * 
	 * @param jspn
	 *            - Swing spinner component
	 * @param idx
	 * @param obj
	 *            - ChangeListener
	 * @param changelistener
	 *            - True is a ChangeListener is to be assigned
	 * 
	 */
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
