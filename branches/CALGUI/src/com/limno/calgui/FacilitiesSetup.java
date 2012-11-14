package com.limno.calgui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.swixml.SwingEngine;

public class FacilitiesSetup {
	public static void SetFacilitiesTables(SwingEngine swix) {

		JTable table = (JTable) swix.find("tblBanks");
		String[][] data = { { "Jan", "10300" }, { "Feb", "10300" }, { "Mar", "10300" }, { "Apr", "10300" }, { "May", "10300" },
		        { "Jun", "10300" }, { "Jul", "10300" }, { "Aug", "10300" }, { "Sep", "10300" }, { "Oct", "10300" },
		        { "Nov", "10300" }, { "Dec", "10300" } };
		String[] headers = { "Month", "cfs" };
		DefaultTableModel model = new DefaultTableModel(data, headers);
		table.setModel(model);

		table = (JTable) swix.find("tblIF1");
		String[][] data1 = { { "Jan", "15000" }, { "Feb", "15000" }, { "Mar", "15000" }, { "Apr", "15000" }, { "May", "15000" },
		        { "Jun", "15000" }, { "Jul", "15000" }, { "Aug", "15000" }, { "Sep", "15000" }, { "Oct", "15000" },
		        { "Nov", "15000" }, { "Dec", "15000" } };
		String[] headers1 = { "Month", "cfs" };
		model = new DefaultTableModel(data1, headers1);
		table.setModel(model);
		JTable table2 = (JTable) swix.find("tblIF2");
		String[][] data2 = { { "Jan", "0", "0" }, { "Feb", "0", "0" }, { "Mar", "0", "0" }, { "Apr", "0", "0" },
		        { "May", "0", "0" }, { "Jun", "0", "0" }, { "Jul", "0", "0" }, { "Aug", "0", "0" }, { "Sep", "0", "0" },
		        { "Oct", "0", "0" }, { "Nov", "0", "0" }, { "Dec", "0", "0" } };
		String[] headers2 = { "Month", "min cfs", "max cfs" };
		DefaultTableModel model2 = new DefaultTableModel(data2, headers2);
		table2.setModel(model2);

	}
}
