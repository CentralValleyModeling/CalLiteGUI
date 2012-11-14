package gov.ca.water.calgui.table;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import rma.awt.table.ColumnGroup;

/**
 * GroupableTableHeader
 * 
 * @version 1.0 10/20/98
 * @author Nobuo Tamemasa
 */

public class GroupableTableHeader extends JTableHeader {
	private static final String uiClassID = "GroupableTableHeaderUI";
	protected Vector columnGroups = null;

	public GroupableTableHeader(TableColumnModel model) {
		super(model);
		// setUI(new GroupableTableHeaderUI());
		setReorderingAllowed(false);
	}

	@Override
	public void setUI(javax.swing.plaf.TableHeaderUI ui) {
		super.setUI(new GroupableTableHeaderUI());
	}

	@Override
	public void setReorderingAllowed(boolean b) {
		reorderingAllowed = false;
	}

	public void addColumnGroup(ColumnGroup g) {
		if (columnGroups == null) {
			columnGroups = new Vector();
		}
		columnGroups.addElement(g);
	}

	public Enumeration getColumnGroups(TableColumn col) {
		if (columnGroups == null)
			return null;
		Enumeration enumeration = columnGroups.elements();
		while (enumeration.hasMoreElements()) {
			ColumnGroup cGroup = (ColumnGroup) enumeration.nextElement();
			Vector v_ret = cGroup.getColumnGroups(col, new Vector());
			if (v_ret != null) {
				return v_ret.elements();
			}
		}
		return null;
	}

	public void setColumnMargin() {
		if (columnGroups == null)
			return;
		int columnMargin = getColumnModel().getColumnMargin();
		Enumeration enumeration = columnGroups.elements();
		while (enumeration.hasMoreElements()) {
			ColumnGroup cGroup = (ColumnGroup) enumeration.nextElement();
			cGroup.setColumnMargin(columnMargin);
		}
	}

}
