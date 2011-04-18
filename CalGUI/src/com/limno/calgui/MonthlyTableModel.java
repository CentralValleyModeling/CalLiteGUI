package com.limno.calgui;

import javax.swing.table.*;

import java.util.*;
import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

	public class MonthlyTableModel extends AbstractTableModel {

	protected TimeSeriesContainer tsc;
	protected int rows;
	protected int cols;

	protected Vector<String> data;
	protected Vector<String> columnNames = new Vector<String>(Arrays.asList(
			"Year", " Jan ", " Feb ", " Mar ", " Apr ", " May ", " Jun ",
			" Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec ", "Total"));

	public MonthlyTableModel(TimeSeriesContainer tscIn) {
		tsc = tscIn;
		initVectors();
	}

	private void initVectors() {

		HecTime ht = new HecTime();

		ht.set(tsc.startTime);
		int startYear = ht.year();
		int startMonth = ht.month();

		ht.set(tsc.endTime);
		int endYear = ht.year();

		rows = endYear - startYear + 2;
		cols = 14;

		data = new Vector<String>();
		// first year
		data.addElement(Integer.toString(startYear));

		for (int i = 1; i < startMonth; i++)
			data.addElement("");
		double subtotal = 0;
		int col = startMonth;

		for (int i = 0; i < tsc.numberValues; i++) {
			subtotal = subtotal + tsc.values[i];
			data.addElement(Double.toString(((int)(100*tsc.values[i]))/100));
			//data.addElement(Double.toString(tsc.values[i]));
			if (col == 12) {
				data.addElement(Double.toString(((int)(100*subtotal))/100));
				startYear = startYear + 1;
				data.addElement(Integer.toString(startYear));
				subtotal = 0;
				col = 0;
			}
			col ++;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3718212331885573022L;

	@Override
	public String getColumnName(int columnIndex) {
		String colName = "";

		if (columnIndex <= getColumnCount())
			colName = (String) columnNames.elementAt(columnIndex);

		return colName;
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return cols;
	}

	@Override
	public int getRowCount()	 {
		// TODO Auto-generated method stub
		return rows;
	}

	@Override
	public Object getValueAt(final int arg0, final int arg1) {
		// TODO Auto-generated method stub
		return data.elementAt(14 * arg0 + arg1);
	}

}
