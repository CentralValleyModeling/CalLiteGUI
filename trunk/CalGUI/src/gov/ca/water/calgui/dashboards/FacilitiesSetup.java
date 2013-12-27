package gov.ca.water.calgui.dashboards;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.swixml.SwingEngine;

public class FacilitiesSetup {

	public static void SetFacilitiesTables(SwingEngine swix) {

		/*
		 * JScrollPane scr = (JScrollPane) swix.find("fac_scr1"); scr.setVisible(false); scr = (JScrollPane) swix.find("fac_scr2");
		 * scr.setVisible(false); scr = (JScrollPane) swix.find("fac_scr3"); scr.setVisible(false);
		 */

		/*
		 * JTable table = (JTable) swix.find("tblIF1"); String[][] data1 = { { "Jan", "15000", "0", "1", "999999" }, { "Feb",
		 * "15000", "0", "1", "999999" }, { "Mar", "15000", "0", "1", "999999" }, { "Apr", "15000", "0", "1", "999999" }, { "May",
		 * "15000", "0", "1", "999999" }, { "Jun", "15000", "0", "1", "999999" }, { "Jul", "15000", "0", "1", "999999" }, { "Aug",
		 * "15000", "0", "1", "999999" }, { "Sep", "15000", "0", "1", "999999" }, { "Oct", "15000", "0", "1", "999999" }, { "Nov",
		 * "15000", "0", "1", "999999" }, { "Dec", "15000", "0", "1", "999999" } }; String[] headers1 = { "Month",
		 * "Max Capacity (cfs)", "Min Bypass (fraction)", "Min Bypass (fraction)", "Reserved" }; DefaultTableModel model = new
		 * DefaultTableModel(data1, headers1); table.setModel(model); table.setVisible(true);
		 * 
		 * 
		 * table = (JTable) swix.find("tblIF2"); String[][] data2 = { { "Oct", "7000", "0", "7000", "0", "7000", "0", "7000", "0" },
		 * { "Nov", "7000", "0", "7000", "0", "7000", "0", "7000", "0" }, { "Dec", "0", "1", "11000", "0.4", "12600", "0.2",
		 * "13600", "0.2" }, { "Jan", "0", "1", "11000", "0.5", "13000", "0.35", "14750", "0.2" }, { "Feb", "0", "1", "11000",
		 * "0.6", "13400", "0.5", "15900", "0.2" }, { "Mar", "0", "1", "11000", "0.6", "13400", "0.5", "15900", "0.2" }, { "Apr",
		 * "0", "1", "11000", "0.6", "13400", "0.5", "15900", "0.2" }, { "May", "0", "1", "11000", "0.5", "13000", "0.35", "14750",
		 * "0.2" }, { "Jun", "0", "1", "11000", "0.4", "12600", "0.2", "13600", "0.2" }, { "Jul", "5000", "0", "5000", "0", "5000",
		 * "0", "5000", "0" }, { "Aug", "5000", "0", "5000", "0", "5000", "0", "5000", "0" }, { "Sep", "5000", "0", "5000", "0",
		 * "5000", "0", "5000", "0" } }; String[] headers2 = { "Month", "Min Bypass", "Coeff", "Min Bypass", "Coeff", "Min Bypass",
		 * "Coeff", "Min Bypass", "Coeff" }; model = new DefaultTableModel(data2, headers2); table.setModel(model);
		 * 
		 * TableColumnModel columnModel = table.getColumnModel(); GroupableTableHeader groupableTableHeader = new
		 * GroupableTableHeader(columnModel);
		 * 
		 * ColumnGroup g_qRange = new ColumnGroup("qRange:"); ColumnGroup g_minv = new ColumnGroup("minv:"); ColumnGroup g_maxv =
		 * new ColumnGroup("maxv:"); g_maxv.add(columnModel.getColumn(0)); g_minv.add(g_maxv); g_qRange.add(g_minv);
		 * groupableTableHeader.addColumnGroup(g_qRange);
		 * 
		 * ColumnGroup g_qRange1 = new ColumnGroup("Range #1"); ColumnGroup g_minv1 = new ColumnGroup("0"); ColumnGroup g_maxv1 =
		 * new ColumnGroup("11000"); g_maxv1.add(columnModel.getColumn(1)); g_maxv1.add(columnModel.getColumn(2));
		 * g_minv1.add(g_maxv1); g_qRange1.add(g_minv1); groupableTableHeader.addColumnGroup(g_qRange1);
		 * 
		 * ColumnGroup g_qRange2 = new ColumnGroup("Range #1"); ColumnGroup g_minv2 = new ColumnGroup("11000"); ColumnGroup g_maxv2
		 * = new ColumnGroup("15000"); g_maxv2.add(columnModel.getColumn(3)); g_maxv2.add(columnModel.getColumn(4));
		 * g_minv2.add(g_maxv2); g_qRange2.add(g_minv2); groupableTableHeader.addColumnGroup(g_qRange2);
		 * 
		 * ColumnGroup g_qRange3 = new ColumnGroup("Range #3"); ColumnGroup g_minv3 = new ColumnGroup("15000"); ColumnGroup g_maxv3
		 * = new ColumnGroup("20000"); g_maxv3.add(columnModel.getColumn(5)); g_maxv3.add(columnModel.getColumn(6));
		 * g_minv3.add(g_maxv3); g_qRange3.add(g_minv3); groupableTableHeader.addColumnGroup(g_qRange3);
		 * 
		 * ColumnGroup g_qRange4 = new ColumnGroup("Range #4"); ColumnGroup g_minv4 = new ColumnGroup("20000"); ColumnGroup g_maxv4
		 * = new ColumnGroup("999999"); g_maxv4.add(columnModel.getColumn(7)); g_maxv4.add(columnModel.getColumn(8));
		 * g_minv4.add(g_maxv4); g_qRange4.add(g_minv4); groupableTableHeader.addColumnGroup(g_qRange4);
		 * 
		 * table.setTableHeader(groupableTableHeader); table.setVisible(false);
		 */

		JTable table = (JTable) swix.find("tblBanks");
		String[][] data3 = { { "Jan", "6680", "8500", "10300", "0", "0", "0" }, { "Feb", "6680", "8500", "10300", "0", "0", "0" },
		        { "Mar", "6680", "8500", "10300", "17", "0", "0" }, { "Apr", "6680", "8500", "10300", "31", "0", "0" },
		        { "May", "6680", "8500", "10300", "28", "0", "0" }, { "Jun", "6680", "8500", "10300", "15", "0", "0" },
		        { "Jul", "6680", "8500", "10300", "0", "0", "0" }, { "Aug", "6680", "8500", "10300", "0", "0", "0" },
		        { "Sep", "6680", "8500", "10300", "0", "0", "0" }, { "Oct", "6680", "8500", "10300", "0", "0", "500" },
		        { "Nov", "6680", "8500", "10300", "0", "0", "500" }, { "Dec", "6680", "8500", "10300", "0", "0", "500" } };
		String[] headers3 = { "Month", "Perm Cap1", "Perm Cap2", "Phys Cap", "DaysIncr", "CVPDedCap", "EWACap" };
		DefaultTableModel model = new DefaultTableModel(data3, headers3);
		table.setModel(model);

	}
}
