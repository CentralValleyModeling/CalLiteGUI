package com.limno.calgui;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

public class SummaryTablePanel extends JPanel {
	int n[][];
	double x[][];
	double xx[][];
	double avg[][];
	double sdev[][];
	double min[][];
	double max[][];
	double med[][];
	double medx[][][];

	private static int ylt[][] = { { 1920, 2, 2, 1, 1, 0, 3, 2, 0, }, { 1921, 2, 2, 1, 1, 0, 3, 2, 0, },
			{ 1922, 2, 1, 1, 1, 0, 4, 2, 0, }, { 1923, 3, 2, 3, 1, 0, 4, 3, 0, }, { 1924, 5, 5, 4, 2, 1, 5, 6, 0, },
			{ 1925, 4, 3, 1, 1, 0, 2, 5, 0, }, { 1926, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1927, 1, 2, 1, 1, 0, 2, 1, 0, },
			{ 1928, 2, 3, 1, 1, 0, 3, 2, 1, }, { 1929, 5, 5, 3, 1, 0, 5, 6, 1, }, { 1930, 4, 5, 2, 1, 0, 4, 5, 1, },
			{ 1931, 5, 5, 4, 2, 1, 5, 6, 1, }, { 1932, 4, 2, 4, 1, 0, 4, 5, 1, }, { 1933, 5, 4, 4, 1, 0, 4, 6, 1, },
			{ 1934, 5, 5, 4, 2, 1, 5, 6, 1, }, { 1935, 3, 2, 1, 1, 0, 4, 3, 0, }, { 1936, 3, 2, 1, 1, 0, 3, 3, 0, },
			{ 1937, 3, 1, 2, 1, 0, 4, 3, 0, }, { 1938, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1939, 4, 4, 3, 2, 0, 5, 5, 0, },
			{ 1940, 2, 2, 1, 1, 0, 2, 2, 0, }, { 1941, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1942, 1, 1, 1, 1, 0, 2, 1, 0, },
			{ 1943, 1, 1, 1, 1, 0, 3, 1, 0, }, { 1944, 4, 3, 3, 1, 0, 5, 5, 0, }, { 1945, 3, 2, 1, 1, 0, 3, 3, 0, },
			{ 1946, 3, 2, 1, 1, 0, 2, 3, 0, }, { 1947, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1948, 3, 3, 1, 1, 0, 3, 3, 0, },
			{ 1949, 4, 3, 2, 1, 0, 3, 5, 0, }, { 1950, 3, 3, 2, 1, 0, 4, 3, 0, }, { 1951, 2, 2, 1, 1, 0, 2, 2, 0, },
			{ 1952, 1, 1, 1, 1, 0, 2, 1, 0, }, { 1953, 1, 3, 1, 1, 0, 2, 1, 0, }, { 1954, 2, 3, 1, 1, 0, 2, 2, 0, },
			{ 1955, 4, 4, 2, 1, 0, 4, 5, 0, }, { 1956, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1957, 2, 3, 1, 1, 0, 3, 2, 0, },
			{ 1958, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1959, 3, 4, 1, 1, 0, 3, 3, 0, }, { 1960, 4, 5, 1, 1, 0, 3, 5, 0, },
			{ 1961, 4, 5, 1, 1, 0, 3, 5, 0, }, { 1962, 3, 3, 1, 1, 0, 3, 3, 0, }, { 1963, 1, 2, 1, 1, 0, 2, 1, 0, },
			{ 1964, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1965, 1, 1, 1, 1, 0, 2, 1, 0, }, { 1966, 3, 3, 1, 1, 0, 3, 3, 0, },
			{ 1967, 1, 1, 1, 1, 0, 2, 1, 0, }, { 1968, 3, 4, 1, 1, 0, 3, 3, 0, }, { 1969, 1, 1, 1, 1, 0, 1, 1, 0, },
			{ 1970, 1, 2, 1, 1, 0, 2, 1, 0, }, { 1971, 1, 3, 1, 1, 0, 2, 1, 0, }, { 1972, 3, 4, 1, 1, 0, 3, 3, 0, },
			{ 1973, 2, 2, 1, 1, 0, 2, 2, 0, }, { 1974, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1975, 1, 1, 1, 1, 0, 2, 1, 0, },
			{ 1976, 5, 5, 3, 2, 0, 4, 6, 2, }, { 1977, 5, 5, 4, 2, 1, 5, 7, 2, }, { 1978, 2, 1, 1, 1, 0, 1, 2, 0, },
			{ 1979, 3, 2, 2, 1, 0, 4, 3, 0, }, { 1980, 2, 1, 1, 1, 0, 2, 2, 0, }, { 1981, 4, 4, 2, 2, 0, 4, 5, 0, },
			{ 1982, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1983, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1984, 1, 2, 1, 1, 0, 2, 1, 0, },
			{ 1985, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1986, 1, 1, 1, 1, 0, 2, 1, 3, }, { 1987, 4, 5, 3, 2, 0, 4, 5, 3, },
			{ 1988, 5, 5, 3, 2, 1, 4, 6, 3, }, { 1989, 4, 5, 1, 1, 0, 3, 5, 3, }, { 1990, 5, 5, 3, 2, 0, 4, 6, 3, },
			{ 1991, 5, 5, 4, 1, 1, 5, 6, 3, }, { 1992, 5, 5, 4, 2, 0, 4, 6, 3, }, { 1993, 2, 1, 1, 1, 0, 2, 2, 0, },
			{ 1994, 5, 5, 4, 2, 0, 5, 6, 0, }, { 1995, 1, 1, 1, 1, 0, 1, 0, 0, }, { 1996, 1, 1, 1, 1, 0, 2, 0, 0, },
			{ 1997, 1, 1, 1, 1, 0, 2, 0, 0, }, { 1998, 1, 1, 1, 1, 0, 1, 0, 0, }, { 1999, 1, 2, 1, 1, 0, 2, 0, 0, },
			{ 2000, 2, 2, 1, 1, 0, 2, 0, 0, }, { 2001, 4, 4, 1, 2, 0, 4, 0, 0, }, { 2002, 4, 4, 1, 1, 0, 3, 0, 0, },
			{ 2003, 2, 3, 1, 1, 0, 2, 0, 0, } };

	private void update(int i1, int i2, double value) {
		x[i1][i2] += value;
		xx[i1][i2] += (value * value);
		if (min[i1][i2] > value)
			min[i1][i2] = value;
		if (max[i1][i2] < value)
			max[i1][i2] = value;
		medx[i1][i2][n[i1][i2]] = value;
		n[i1][i2]++;
	}

	Vector<String> columns;

	SummaryTablePanel(TimeSeriesContainer tscs[], String tagString) {

		super();

		// Determine target rows and columns

		Vector<String> data[] = new Vector[tscs.length];
		columns = new Vector<String>(6);
		columns.addElement("Type");
		columns.addElement("N");
		int cols = 0;
		int rows = 0;

		// loop over all Primary datasets

		for (int t = 0; t < tscs.length; t++) {

			// Initialize accumulators

			n = new int[6][6];
			x = new double[6][6];
			xx = new double[6][6];
			min = new double[6][6];
			max = new double[6][6];
			for (int i1 = 0; i1 < 6; i1++)
				for (int i2 = 0; i2 < 6; i2++) {
					n[i1][i2] = 0;
					x[i1][i2] = 0;
					xx[i1][i2] = 0;
					min[i1][i2] = 1e20;
					max[i1][i2] = 0;
				}

			med = new double[6][6];
			medx = new double[6][6][tscs[t].numberValues]; // TODO - adjust for
															// subset of date

			// Loop through timeseries

			HecTime ht = new HecTime();

			for (int i = 0; i < tscs[t].numberValues; i++) {

				ht.set(tscs[t].times[i]);
				int y = ht.year();
				int m = ht.month();
				int wy = (m < 10) ? y : y - 1;
				int ySac403030 = (m < 2) ? y - 1 : y;
				int ySHASTAindex = (m < 3) ? y - 1 : y;
				int yFEATHERindex = (m < 2) ? y - 1 : y;
				int ySJRindex = (m < 2) ? y - 1 : y;

				update(0, 0, tscs[t].values[i]);
				update(1, ylt[ySac403030 - 1920][1], tscs[t].values[i]);
				update(2, ylt[ySHASTAindex - 1920][3], tscs[t].values[i]);
				update(3, ylt[yFEATHERindex - 1920][5], tscs[t].values[i]);
				update(4, ylt[ySJRindex - 1920][2], tscs[t].values[i]);
				if (ylt[wy - 1920][8] != 0) {
					update(5, ylt[wy - 1920][8], tscs[t].values[i]);
					update(5, 0, tscs[t].values[i]);
				}

			}

			avg = new double[6][6];
			sdev = new double[6][6];
			data[t] = new Vector<String>();
			String[] leftPart = { "All", "Sac 40-30-30", "Shasta", "Feather", "SJR", "Dry" };
			String[] rightPartsclimate = { "", "Wet", "Above", "Normal", "Dry", "Extreme" };
			String[] rightPartsDry = { "All dry periods", "1928-1934", "1976-1977", "1986-1992" };
			DecimalFormat df1 = new DecimalFormat("#.#");
			DecimalFormat df2 = new DecimalFormat("#.##");

			// Calculate results
			for (int i1 = 0; i1 < 6; i1++)
				for (int i2 = 0; i2 < 6; i2++)

					if ((((i1 == 0) && tagString.contains("All years"))
							|| ((i1 == 1) && tagString.contains("40-30-30"))
							|| ((i1 == 2) && tagString.contains("Shasta"))
							|| ((i1 == 3) && tagString.contains("Feather"))
							|| ((i1 == 4) && tagString.contains("SJR Index"))
							|| ((i1 == 5) && tagString.contains("All dry"))
							|| ((i1 == 5) && (i2 == 1) && tagString.contains("1928"))
							|| ((i1 == 5) && (i2 == 2) && tagString.contains("1976")) || ((i1 == 5) && (i2 == 3) && tagString
							.contains("1986"))) && (n[i1][i2] != 0)) {

						avg[i1][i2] = x[i1][i2] / n[i1][i2];
						sdev[i1][i2] = Math.sqrt(Math.abs(xx[i1][i2] / n[i1][i2] - avg[i1][2] * avg[i1][2]));

						int nmed = n[i1][i2];
						double[] medx2 = new double[nmed];
						for (int i3 = 0; i3 < nmed; i3++)
							medx2[i3] = medx[i1][i2][i3];
						Arrays.sort(medx2);
						// TODO fix logic for even sizes
						nmed = (int) nmed / 2;
						med[i1][i2] = medx2[nmed];

						String rightPart;
						if (i1 == 0) {
							rightPart = "";
						} else if (i1 == 1 || i1 == 4) {
							rightPart = " (" + rightPartsclimate[i2] + ")";
						} else if (i1 <= 4)
							rightPart = " " + Integer.toString(i2);
						else
							rightPart = " (" + rightPartsDry[i2] + ")";

						System.out.print(leftPart[i1] + rightPart);
						System.out.print("\t");
						System.out.print(n[i1][i2]);
						System.out.print(" ");
						System.out.print(df1.format(avg[i1][i2]));
						System.out.print(" ");
						System.out.print(df2.format(sdev[i1][i2]));
						System.out.print(" ");
						System.out.print(df1.format(min[i1][i2]));
						System.out.print(" ");
						System.out.println(df1.format(max[i1][i2]));

						data[t].addElement(leftPart[i1] + rightPart);
						data[t].addElement(Integer.toString(n[i1][i2]));
						if (tagString.contains("Avg"))
							data[t].addElement(df1.format(avg[i1][i2]));
						if (tagString.contains("StdDev"))
							data[t].addElement(df1.format(sdev[i1][i2]));
						if (tagString.contains("Min"))
							data[t].addElement(df1.format(min[i1][i2]));
						if (tagString.contains("Median"))
							data[t].addElement(df1.format(med[i1][i2]));
						if (tagString.contains("Max"))
							data[t].addElement(df1.format(max[i1][i2]));
						rows = rows + 1;
					}

			cols = 0;
			if (tagString.contains("Avg")) {
				cols++;
				columns.addElement("Average");
			}
			if (tagString.contains("StdDev")) {
				cols++;
				columns.addElement("Std Dev");
			}
			if (tagString.contains("Min")) {
				cols++;
				columns.addElement("Min");
			}
			if (tagString.contains("Median")) {
				cols++;
				columns.addElement("Med");
			}
			if (tagString.contains("Max")) {
				cols++;
				columns.addElement("Max");
			}
		}

		Vector<String> data2 = new Vector<String>();
		rows = data[0].size() / (cols + 2);
		for (int r = 0; r < rows; r++) {
			data2.add(data[0].get(r * (cols + 2)));
			data2.add(data[0].get(r * (cols + 2) + 1));
			for (int t = 0; t < tscs.length; t++)
				for (int c = 0; c < cols; c++)
					data2.add(data[t].get(r * (cols + 2) + 2 + c));
		}

		SimpleTableModel model = new SimpleTableModel(data2, columns);
		JTable table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setPreferredWidth(250);

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

	}
}

class SimpleTableModel extends AbstractTableModel {

	protected Vector<String> data;
	protected Vector<String> columnNames;

	public SimpleTableModel(Vector<String> datain, Vector<String> columnin) {
		data = datain;
		columnNames = columnin;
	}

	public int getRowCount() {
		return data.size() / getColumnCount();
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public String getColumnName(int columnIndex) {
		String colName = "";

		if (columnIndex <= getColumnCount())
			colName = (String) columnNames.elementAt(columnIndex);

		return colName;
	}

	public Class getColumnClass(int columnIndex) {
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return (String) data.elementAt((rowIndex * getColumnCount()) + columnIndex);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.setElementAt((String) aValue, ((rowIndex * getColumnCount()) + columnIndex));
		// return;

	}

	// http://www.codeguru.com/java/articles/124.shtml

	// File: ColumnGroup.java
	/*
	 * (swing1.1beta3)
	 */

	/**
	 * ColumnGroup
	 * 
	 * @version 1.0 10/20/98
	 * @author Nobuo Tamemasa
	 */

	public class ColumnGroup {
		protected TableCellRenderer renderer;
		protected Vector v;
		protected String text;
		protected int margin = 0;

		public ColumnGroup(String text) {
			this(null, text);
		}

		public ColumnGroup(TableCellRenderer renderer, String text) {
			if (renderer == null) {
				this.renderer = new DefaultTableCellRenderer() {
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						JTableHeader header = table.getTableHeader();
						if (header != null) {
							setForeground(header.getForeground());
							setBackground(header.getBackground());
							setFont(header.getFont());
						}
						setHorizontalAlignment(JLabel.CENTER);
						setText((value == null) ? "" : value.toString());
						setBorder(UIManager.getBorder("TableHeader.cellBorder"));
						return this;
					}
				};
			} else {
				this.renderer = renderer;
			}
			this.text = text;
			v = new Vector();
		}

		/**
		 * @param obj
		 *            TableColumn or ColumnGroup
		 */
		public void add(Object obj) {
			if (obj == null) {
				return;
			}
			v.addElement(obj);
		}

		/**
		 * @param c
		 *            TableColumn
		 * @param v
		 *            ColumnGroups
		 */
		public Vector getColumnGroups(TableColumn c, Vector g) {
			g.addElement(this);
			if (v.contains(c))
				return g;
			Enumeration enumeration = v.elements();
			while (enumeration.hasMoreElements()) {
				Object obj = enumeration.nextElement();
				if (obj instanceof ColumnGroup) {
					Vector groups = (Vector) ((ColumnGroup) obj).getColumnGroups(c, (Vector) g.clone());
					if (groups != null)
						return groups;
				}
			}
			return null;
		}

		public TableCellRenderer getHeaderRenderer() {
			return renderer;
		}

		public void setHeaderRenderer(TableCellRenderer renderer) {
			if (renderer != null) {
				this.renderer = renderer;
			}
		}

		public Object getHeaderValue() {
			return text;
		}

		public Dimension getSize(JTable table) {
			Component comp = renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
			int height = comp.getPreferredSize().height;
			int width = 0;
			Enumeration enumeration = v.elements();
			while (enumeration.hasMoreElements()) {
				Object obj = enumeration.nextElement();
				if (obj instanceof TableColumn) {
					TableColumn aColumn = (TableColumn) obj;
					width += aColumn.getWidth();
					width += margin;
				} else {
					width += ((ColumnGroup) obj).getSize(table).width;
				}
			}
			return new Dimension(width, height);
		}

		public void setColumnMargin(int margin) {
			this.margin = margin;
			Enumeration enumeration = v.elements();
			while (enumeration.hasMoreElements()) {
				Object obj = enumeration.nextElement();
				if (obj instanceof ColumnGroup) {
					((ColumnGroup) obj).setColumnMargin(margin);
				}
			}
		}
	}

	// File: GroupableTableHeader.java
	//
	/*
	 * (swing1.1beta3)
	 */

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
			setUI(new GroupableTableHeaderUI());
			setReorderingAllowed(false);
		}

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
				Vector v_ret = (Vector) cGroup.getColumnGroups(col, new Vector());
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

	// File: GroupableTableHeaderUI.java
	//
	/*
	 * (swing1.1beta3)
	 */

	public class GroupableTableHeaderUI extends BasicTableHeaderUI {

		public void paint(Graphics g, JComponent c) {
			Rectangle clipBounds = g.getClipBounds();
			if (header.getColumnModel() == null)
				return;
			((GroupableTableHeader) header).setColumnMargin();
			int column = 0;
			Dimension size = header.getSize();
			Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
			Hashtable h = new Hashtable();
			int columnMargin = header.getColumnModel().getColumnMargin();

			Enumeration enumerationeration = header.getColumnModel().getColumns();
			while (enumerationeration.hasMoreElements()) {
				cellRect.height = size.height;
				cellRect.y = 0;
				TableColumn aColumn = (TableColumn) enumerationeration.nextElement();
				Enumeration cGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);
				if (cGroups != null) {
					int groupHeight = 0;
					while (cGroups.hasMoreElements()) {
						ColumnGroup cGroup = (ColumnGroup) cGroups.nextElement();
						Rectangle groupRect = (Rectangle) h.get(cGroup);
						if (groupRect == null) {
							groupRect = new Rectangle(cellRect);
							Dimension d = cGroup.getSize(header.getTable());
							groupRect.width = d.width;
							groupRect.height = d.height;
							h.put(cGroup, groupRect);
						}
						paintCell(g, groupRect, cGroup);
						groupHeight += groupRect.height;
						cellRect.height = size.height - groupHeight;
						cellRect.y = groupHeight;
					}
				}
				cellRect.width = aColumn.getWidth() + columnMargin;
				if (cellRect.intersects(clipBounds)) {
					paintCell(g, cellRect, column);
				}
				cellRect.x += cellRect.width;
				column++;
			}
		}

		private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
			TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
			TableCellRenderer renderer = aColumn.getHeaderRenderer();
			Component component = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(),
					false, false, -1, columnIndex);
			rendererPane.add(component);
			rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
					true);
		}

		private void paintCell(Graphics g, Rectangle cellRect, ColumnGroup cGroup) {
			TableCellRenderer renderer = cGroup.getHeaderRenderer();
			Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(),
					false, false, -1, -1);
			rendererPane.add(component);
			rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
					true);
		}

		private int getHeaderHeight() {
			int height = 0;
			TableColumnModel columnModel = header.getColumnModel();
			for (int column = 0; column < columnModel.getColumnCount(); column++) {
				TableColumn aColumn = columnModel.getColumn(column);
				TableCellRenderer renderer = aColumn.getHeaderRenderer();
				Component comp = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(),
						false, false, -1, column);
				int cHeight = comp.getPreferredSize().height;
				Enumeration enumeration = ((GroupableTableHeader) header).getColumnGroups(aColumn);
				if (enumeration != null) {
					while (enumeration.hasMoreElements()) {
						ColumnGroup cGroup = (ColumnGroup) enumeration.nextElement();
						cHeight += cGroup.getSize(header.getTable()).height;
					}
				}
				height = Math.max(height, cHeight);
			}
			return height;
		}

		private Dimension createHeaderSize(long width) {
			TableColumnModel columnModel = header.getColumnModel();
			width += columnModel.getColumnMargin() * columnModel.getColumnCount();
			if (width > Integer.MAX_VALUE) {
				width = Integer.MAX_VALUE;
			}
			return new Dimension((int) width, getHeaderHeight());
		}

		public Dimension getPreferredSize(JComponent c) {
			long width = 0;
			Enumeration enumerationeration = header.getColumnModel().getColumns();
			while (enumerationeration.hasMoreElements()) {
				TableColumn aColumn = (TableColumn) enumerationeration.nextElement();
				width = width + aColumn.getPreferredWidth();
			}
			return createHeaderSize(width);
		}
	}

}
