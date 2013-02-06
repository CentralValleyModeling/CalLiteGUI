package gov.ca.water.calgui.results;

public class RBListItem {
	private final String label;
	private final String fullname;
	private boolean isSelected = false;

	public RBListItem(String label, String label2) {
		this.label = label2;
		this.fullname = label;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public String toString() {
		return fullname;
	}

	public String toString2() {
		return label;
	}
}