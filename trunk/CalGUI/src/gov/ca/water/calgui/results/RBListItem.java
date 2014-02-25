package gov.ca.water.calgui.results;

/**
 * RBListItem is a helper for the FileDialog class. It holds DV and SV path information and selection (base) status of scenarios
 * listed in the results control panel.
 * 
 * @author tslawecki
 * 
 */
public class RBListItem {

	private final String label;
	private final String fullname;
	private boolean isSelected = false;
	private String svFilename;

	public RBListItem(String label, String label2) {
		this.label = label2;
		this.fullname = label;
		this.svFilename = "";
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setSVFilename(String svf) {
		svFilename = svf;
	}

	public String getSVFilename() {
		return svFilename;
	}

	@Override
	public String toString() {
		return fullname;
	}

	public String toString2() {
		return label;
	}
}