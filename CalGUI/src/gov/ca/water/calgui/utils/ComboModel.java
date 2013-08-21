package gov.ca.water.calgui.utils;

import javax.swing.DefaultComboBoxModel;

/**
 * Combobox Model used in the InitClass sample.
 */
public class ComboModel extends DefaultComboBoxModel {
	/**
	 * Constructs a DefaultComboBoxModel object.
	 */
	public ComboModel() {
		super(new Object[] { "Oct 1921 - Sep 2003", "Oct 1921 - Sep 1994", "May 1928 - Oct 1934", "Jun 1986 - Sep 1992",
		        "Oct 1975 - Sep 1977", "Oct 1983 - Sep 1993", "Custom" });

	}
}