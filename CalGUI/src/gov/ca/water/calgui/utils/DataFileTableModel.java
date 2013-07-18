package gov.ca.water.calgui.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

public class DataFileTableModel extends AbstractTableModel {

	protected Vector<Object> data;
	protected Vector<String> columnNames;
	protected Vector<String> columnNames1;
	protected String datafile;
	public String[] datafiles;
	protected StringBuffer header;
	protected StringBuffer[] headers;
	protected String columnTitle;
	public int tID;
	final String NL = System.getProperty("line.separator");
	protected EventListenerList listenerList = new EventListenerList();
	private static Logger log = Logger.getLogger(DataFileTableModel.class.getName());

	String wyts[] = { "Wet", "Above Normal", "Below Normal", "Dry", "Critical" };

	public DataFileTableModel(String f, int ID) {
		tID = ID;
		// check if multiple file names included
		datafiles = f.split("[|]");
		int size = datafiles.length;

		if (size == 1) {
			// CASE 1: 1 file specified
			header = new StringBuffer();
			datafile = f;
			columnTitle = "NOT HANDLED";
			initVectors();
		} else if (size == 2) {
			// CASE 2: 2 files specified
			headers = new StringBuffer[2];
			initVectors2();
		}

	}

	public DataFileTableModel(String f, int ID, int iOpt) {
		tID = ID;

		header = new StringBuffer();
		datafile = f;
		columnTitle = "NOT HANDLED";
		initVectorsReg(iOpt);

	}

	public void initVectors2() {

		String aLine;
		data = new Vector<Object>();
		columnNames = new Vector<String>();
		String firstColumnName = "";
		String secondColumnName = "";
		ArrayList<String> allValues = new ArrayList<String>();
		ArrayList<String> allValues1 = new ArrayList<String>();

		for (int i = 0; i < datafiles.length; i++) {

			try {

				FileInputStream fin = new FileInputStream(datafiles[i]);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));

				// Read until first non-comment line

				if (headers[i] != null)
					headers[i].delete(0, headers[i].length());

				aLine = br.readLine();
				while (aLine.startsWith("!") && aLine != null) {
					headers[i].append(aLine + NL);
					aLine = br.readLine();
				}

				aLine = br.readLine();// Skip title line;
				columnTitle = aLine;
				if (aLine != null) {

					// Extract column names from second line

					StringTokenizer st1 = new StringTokenizer(aLine, "\t| ");
					if (st1.countTokens() < 3) {
						while (st1.hasMoreTokens()) {
							columnNames.addElement(st1.nextToken());
						}
					} else {
						firstColumnName = st1.nextToken();
						secondColumnName = st1.nextToken();
						st1.nextToken();

					}
					// Extract data - first pass. Assumes we are reading in
					// column-major order

					aLine = br.readLine();
					st1 = new StringTokenizer(aLine, "\t| ");
					if (st1.countTokens() < 3) {

						// CASE 1: TWO COLUMNS (month, value)

						while (aLine != null) {
							StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");

							// data.addElement(st2.nextToken());
							// data.addElement(st2.nextToken());
							allValues1.add(st2.nextToken());
							allValues1.add(st2.nextToken());
							aLine = br.readLine();
						}

					}

					else {

						// CASE 2: THREE COLUMNS (year type, month, value)
						// colct=columnNames.size();
						// String firstColumnName = (String)
						// columnNames.get(colct-3);
						// String secondColumnName = (String)
						// columnNames.get(colct-2);

						// columnNames.clear();

						// columnNames.addElement(firstColumnName);

						String lastColID = "-1";
						int rowCount = 0;

						while (aLine != null) {

							StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
							if (st2.countTokens() >= 3) {

								st2.nextToken();
								String aColID = st2.nextToken();
								String aValue = st2.nextToken();
								// System.out.println(Boolean.toString(lastColID
								// == aColID)+" " + lastColID + ":" + aColID +
								// ":" + aRowID + " " + aValue+ " " +
								// Integer.toString(rowCount)+ " " +
								// Integer.toString(columnNames.size()));
								if (Integer.parseInt(lastColID) < Integer.parseInt(aColID)) {
									if (secondColumnName.toLowerCase().startsWith("wyt"))
										columnNames.addElement(wyts[Integer.parseInt(aColID) - 1]);
									else
										columnNames.addElement(secondColumnName + aColID);
									lastColID = aColID;
									rowCount = 0;
								}

								rowCount++;
								allValues.add(aValue);
							}
							aLine = br.readLine();
						}
						for (int r = 0; r < rowCount; r++) {
							// Special handling for "active" flag.
							String colName = columnNames.elementAt(0);
							if (colName.trim().endsWith("active")) {
								Boolean val;
								if (allValues1.get(r * 2).trim().equals("1")) {
									val = true;
								} else {
									val = false;
								}
								data.addElement(val);
							} else {
								data.addElement(allValues1.get(r * 2));
							}
							colName = columnNames.elementAt(1);
							if (colName.trim().endsWith("active")) {
								Boolean val;
								if (allValues1.get(r * 2 + 1).trim().equals("1")) {
									val = true;
								} else {
									val = false;
								}
								data.addElement(val);
							} else {
								data.addElement(allValues1.get(r * 2 + 1));
							}
							// data.addElement(Integer.toString(r+1));
							for (int c = 0; c < columnNames.size() - 2; c++) {

								// System.out.println(Integer.toString(r)+":"+Integer.toString(c)+":"+Integer.toString(r)+":"+Integer.toString(c*rowCount)+"="+Integer.toString(allValues.size()));

								data.addElement(allValues.get(c * rowCount + r));
							}
						}

					}

					br.close();

				}
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
	}

	public void initVectors() {

		String aLine;
		data = new Vector<Object>();
		columnNames = new Vector<String>();

		try {

			FileInputStream fin = new FileInputStream(datafile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));

			// Read until first non-comment line
			if (header != null)
				header.delete(0, header.length());
			aLine = br.readLine();
			while (aLine.startsWith("!") && aLine != null) {
				header.append(aLine + NL);
				aLine = br.readLine();
			}

			aLine = br.readLine();// Skip title line;
			columnTitle = aLine;

			if (aLine != null) {

				// Extract column names from second line

				StringTokenizer st1 = new StringTokenizer(aLine, "\t| ");
				while (st1.hasMoreTokens())
					columnNames.addElement(st1.nextToken());

				// Extract data - first pass. Assumes we are reading in
				// column-major order

				aLine = br.readLine();
				st1 = new StringTokenizer(aLine, "\t| ");
				if (st1.countTokens() < 3) {

					// CASE 1: TWO COLUMNS (month, value)

					while (aLine != null) {
						StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
						data.addElement(st2.nextToken());
						data.addElement(st2.nextToken());
						aLine = br.readLine();
					}
				}

				else if (st1.countTokens() == 3) {

					// CASE 2: THREE COLUMNS (year type, month, value)

					String firstColumnName = columnNames.get(0);
					String secondColumnName = columnNames.get(1);
					columnNames.clear();
					columnNames.addElement(firstColumnName);

					String lastColID = "-1";
					int rowCount = 0;

					ArrayList<String> allValues = new ArrayList<String>();
					while (aLine != null) {

						StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
						if (st2.countTokens() >= 3) {

							st2.nextToken();
							String aColID = st2.nextToken();
							String aValue = st2.nextToken();
							// System.out.println(Boolean.toString(lastColID ==
							// aColID)+" " + lastColID + ":" + aColID + ":" +
							// aRowID + " " + aValue+ " " +
							// Integer.toString(rowCount)+ " " +
							// Integer.toString(columnNames.size()));
							if (Integer.parseInt(lastColID) < Integer.parseInt(aColID)) {
								if (secondColumnName.toLowerCase().startsWith("wyt"))
									columnNames.addElement(wyts[Integer.parseInt(aColID) - 1]);
								else
									columnNames.addElement(secondColumnName + aColID);
								lastColID = aColID;
								rowCount = 0;
							}

							rowCount++;
							allValues.add(aValue);
						}
						aLine = br.readLine();
					}
					for (int r = 0; r < rowCount; r++) {

						data.addElement(Integer.toString(r + 1));
						for (int c = 0; c < columnNames.size() - 1; c++) {
							// System.out.println(Integer.toString(r)+":"+Integer.toString(c)+":"+Integer.toString(r)+":"+Integer.toString(c*rowCount)+"="+Integer.toString(allValues.size()));

							data.addElement(allValues.get(c * rowCount + r));
						}
					}

				} else {
					// CASE 3: FOUR COLUMNS (year type, month, value1, value2)
					// EISJR Multiplier + Offset
					String firstColumnName = columnNames.get(0);
					String secondColumnName = columnNames.get(1);
					String thirdColumnName = columnNames.get(2);
					String fourthColumnName = columnNames.get(3);

					if (secondColumnName.toLowerCase().startsWith("wyt")) {

						String wyts1[] = { "W-", "AN-", "BN-", "D-", "CD-" };
						String prefix = "";

						columnNames.clear();
						columnNames.addElement(firstColumnName);

						String lastColID = "-1";
						int rowCount = 0;

						ArrayList<String> allValues = new ArrayList<String>();
						while (aLine != null) {

							StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
							if (st2.countTokens() > 3) {

								st2.nextToken();
								String aColID = st2.nextToken();
								String aValue = st2.nextToken();
								String aValue1 = st2.nextToken();
								if (Integer.parseInt(aColID) > Integer.parseInt(lastColID)) {
									lastColID = aColID;
									rowCount = 0;
									if (secondColumnName.toLowerCase().startsWith("wyt")) {
										// columnNames.addElement(wyts[Integer.parseInt(aColID)-1]);
										prefix = wyts1[Integer.parseInt(aColID) - 1];
										columnNames.addElement(prefix + fourthColumnName); // Multiplier
										columnNames.addElement(prefix + thirdColumnName); // Offset
									} else {
										// columnNames.addElement(secondColumnName +
										// aColID);
									}
								} else {

								}
								rowCount++;
								allValues.add(aValue1); // Multiplier
								allValues.add(aValue); // Offset
							}
							aLine = br.readLine();
						}

						int colct = columnNames.size() / 2;
						int idx = 0;
						for (int r = 0; r < rowCount; r++) {

							data.addElement(Integer.toString(r + 1)); // month
							for (int c = 0; c < colct; c++) {
								for (int c1 = 0; c1 < 2; c1++) {
									idx = c * rowCount * 2 + r * 2 + c1;
									data.addElement(allValues.get(idx));
								}
							}
						}
					} else {
						columnNames.clear();
						columnNames.addElement(firstColumnName);
						columnNames.addElement(secondColumnName);
						columnNames.addElement(thirdColumnName);
						columnNames.addElement(fourthColumnName);
						String lastColID = "-1";
						int rowCount = 0;

						ArrayList<String> allValues = new ArrayList<String>();
						while (aLine != null) {

							StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
							if (st2.countTokens() > 3) {

								String aMon = st2.nextToken();
								String aValue1 = st2.nextToken();
								String aValue2 = st2.nextToken();
								String aValue3 = st2.nextToken();

								rowCount++;
								allValues.add(aMon);
								allValues.add(aValue1);
								allValues.add(aValue2);
								allValues.add(aValue3);
							}
							aLine = br.readLine();
						}
						int colct = columnNames.size();
						int idx = 0;
						for (int r = 0; r < rowCount; r++) {

							// data.addElement(allValues.get(idx)); // month
							for (int c = 0; c < colct; c++) {
								data.addElement(allValues.get(idx));
								idx++;
							}
						}

					}
				}

				br.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initVectorsReg(int iOpt) {

		String aLine;
		data = new Vector<Object>();
		Vector<Object> data1 = new Vector<Object>();
		columnNames = new Vector<String>();
		columnNames1 = new Vector<String>();

		try {

			FileInputStream fin = new FileInputStream(datafile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));

			// Read until first non-comment line
			if (header != null)
				header.delete(0, header.length());
			aLine = br.readLine();
			while (aLine.startsWith("!") && aLine != null) {
				header.append(aLine + NL);
				aLine = br.readLine();
			}

			aLine = br.readLine();// Skip title line;
			columnTitle = aLine;

			if (aLine != null) {
				// Parse only data from selected reg option.
				aLine = br.readLine();
				StringTokenizer st1 = new StringTokenizer(aLine, "\t| ");
				int iColCt = st1.countTokens();
				int iSkipCol;
				if (iOpt == 1) {
					iSkipCol = iColCt;
				} else {
					iSkipCol = iColCt - 1;
				}

				// Extract column names from second line

				st1 = new StringTokenizer(columnTitle, "\t ");
				columnTitle = "";
				for (int j = 0; j < iColCt; j++) {
					if (j + 1 != iSkipCol) {

						// parse option out of column title
						String sCol = st1.nextToken();
						int left = sCol.indexOf("_D");
						if (left > -1) {
							sCol = sCol.substring(0, left);
						}
						columnNames1.addElement(sCol);
						if (j < iColCt - 1) {
							columnTitle = columnTitle + sCol + "\t ";
						} else {
							columnTitle = columnTitle + sCol;
						}
					}
				}

				columnNames = columnNames1;
				// while (st1.hasMoreTokens())
				// columnNames.addElement(st1.nextToken());

				// Extract data - first pass. Assumes we are reading in
				// column-major order
				if (columnNames.size() < 3) {

					// CASE 1: TWO COLUMNS (month, value)

					String dum;
					while (aLine != null) {
						st1 = new StringTokenizer(aLine, "\t| ");
						for (int j = 0; j < iColCt; j++) {
							dum = st1.nextToken();
							if (j + 1 != iSkipCol) {
								data1.addElement(dum);
							}
						}
						aLine = br.readLine();
					}

					data = data1;

				} else if (columnNames.size() == 3) {

					// CASE 2: THREE COLUMNS (year type, month, value)

					String firstColumnName = columnNames.get(0);
					String secondColumnName = columnNames.get(1);
					columnNames.clear();
					columnNames.addElement(firstColumnName);

					String lastColID = "-1";
					int rowCount = 0;

					ArrayList<String> allValues = new ArrayList<String>();
					while (aLine != null) {

						StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
						if (st2.countTokens() >= 3) {

							st2.nextToken();

							String aColID = st2.nextToken();
							String aValue;
							if (iSkipCol == 3) {
								st2.nextToken();
								aValue = st2.nextToken();
							} else {
								aValue = st2.nextToken();
							}

							if (Integer.parseInt(lastColID) < Integer.parseInt(aColID)) {
								if (secondColumnName.toLowerCase().startsWith("wyt"))
									columnNames.addElement(wyts[Integer.parseInt(aColID) - 1]);
								else
									columnNames.addElement(secondColumnName + aColID);
								lastColID = aColID;
								rowCount = 0;
							}

							rowCount++;
							allValues.add(aValue);
						}
						aLine = br.readLine();
					}
					for (int r = 0; r < rowCount; r++) {

						data.addElement(Integer.toString(r + 1));
						for (int c = 0; c < columnNames.size() - 1; c++) {
							// System.out.println(Integer.toString(r)+":"+Integer.toString(c)+":"+Integer.toString(r)+":"+Integer.toString(c*rowCount)+"="+Integer.toString(allValues.size()));

							data.addElement(allValues.get(c * rowCount + r));
						}
					}
				}

				br.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVectors(String aLine) {

		if (aLine != null) {
			// data = new Vector<String>();
			int rowid = 0;
			int colid = 0;

			StringTokenizer st1 = new StringTokenizer(aLine, ";");
			while (st1.hasMoreTokens()) {

				StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
				while (st2.hasMoreTokens()) {
					String string = st2.nextToken();
					if (string.equals("true"))
						setValueAt(true, rowid, colid);
					else if (string.equals("false"))
						setValueAt(false, rowid, colid);
					else
						setValueAt(string, rowid, colid);
					colid++;
					// data.addElement(st2.nextToken());
					// data.addElement(st2.nextToken());
				}
				rowid++;
				colid = 0;
			}
		}
	}

	@Override
	public int getRowCount() {
		// System.out.println(tID);
		if (getColumnCount() > 0) {
			return data.size() / getColumnCount();
		} else {
			return 0;
		}

	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		String colName = "";

		if (columnIndex <= getColumnCount())
			colName = columnNames.elementAt(columnIndex);

		return colName;
	}

	@Override
	public Class getColumnClass(int columnIndex) {
		// return String.class;
		return (getValueAt(0, columnIndex).getClass());
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex != 0);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.elementAt((rowIndex * getColumnCount()) + columnIndex);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}

	@Override
	public TableModelListener[] getTableModelListeners() {
		return listenerList.getListeners(TableModelListener.class);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.setElementAt(aValue, ((rowIndex * getColumnCount()) + columnIndex));
		fireTableCellUpdated(rowIndex, columnIndex);
		// return;
	}

	@Override
	public void fireTableDataChanged() {
		fireTableChanged(new TableModelEvent(this));
	}

	@Override
	public void fireTableCellUpdated(int row, int column) {
		fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	@Override
	public void fireTableChanged(TableModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableModelListener.class) {
				((TableModelListener) listeners[i + 1]).tableChanged(e);
			}
		}
	}

	/**
	 * Writes table data to a single .table file
	 * 
	 * @param runDir
	 * @param outputFileName
	 */
	public void writeToFile(final String runDir, String outputFileName) {

		OutputStream outputStream;
		try {
			String OFileName = new File(runDir, outputFileName + ".table").getAbsolutePath();
			outputStream = new FileOutputStream(OFileName);
		} catch (FileNotFoundException e2) {
			log.debug(e2.getMessage());
			return;
		}

		try {

			PrintStream output = new PrintStream(outputStream);

			// write header
			if (header != null) {
				output.print(header.toString());
			}

			output.println(outputFileName);
			output.println(columnTitle);

			if (columnNames.size() == 2) {
				for (int i = 1; i <= data.size() / 2; i++) {
					output.println(data.elementAt(i * 2 - 2) + " " + data.elementAt(i * 2 - 1));
				}
			} else if (columnNames.size() == 3) {
				for (int i = 1; i <= data.size() / 3; i++) {
					output.println(data.elementAt(i * 3 - 3) + " " + data.elementAt(i * 3 - 2) + " " + data.elementAt(i * 3 - 1));
				}
			} else if (columnNames.size() == 4) {
				// 12 Months x 3 values
				for (int j = 0; j < 12; j++) {
					output.println(data.elementAt(j * 4) + " " + data.elementAt(j * 4 + 1) + " " + data.elementAt(j * 4 + 2) + " "
					        + data.elementAt(j * 4 + 3));
				}
			} else if (columnNames.size() == 6) {
				// 5 WYT x 12 Months x 1 value
				for (int i = 1; i <= 5; i++)
					for (int j = 0; j < 12; j++) {
						output.println(Integer.toString(j + 1) + " " + Integer.toString(i) + " " + data.elementAt(j * 6 + i));
					}
			} else if (columnNames.size() == 11) {
				// EIS-SJR: 5 WYT x 12 Months x 2 values
				for (int i = 1; i <= 5; i++)
					for (int j = 0; j < 12; j++) {
						output.println(Integer.toString(j + 1) + " " + Integer.toString(i) + " " + data.elementAt(j * 11 + i * 2)
						        + " " + data.elementAt(j * 11 + i * 2 - 1));
					}
			}
			output.close();
			outputStream.close();

		} catch (IOException e) {
			log.debug(e.getMessage());
		}

	}

	public void writeToFile2(final String runDir, String outputFileName1, String outputFileName2) {

		OutputStream outputStream1;
		OutputStream outputStream2;
		try {
			// outputStream = new
			String OFileName1 = new File(runDir, outputFileName1 + ".table").getAbsolutePath();
			String OFileName2 = new File(runDir, outputFileName2 + ".table").getAbsolutePath();
			outputStream1 = new FileOutputStream(OFileName1);
			outputStream2 = new FileOutputStream(OFileName2);
		} catch (FileNotFoundException e2) {
			// System.out.println("Cannot open output file");
			return;
		}

		try {

			PrintStream output1 = new PrintStream(outputStream1);
			PrintStream output2 = new PrintStream(outputStream2);

			// write header
			if (headers[0] != null) {
				output1.print(headers[0].toString());
			}
			if (headers[1] != null) {
				output2.print(headers[1].toString());
			}

			output1.println(outputFileName1);
			output2.println(outputFileName2);
			String data1, data2;
			output1.println(columnNames.elementAt(0) + " " + columnNames.elementAt(1));
			// System.out.println(columnNames.elementAt(0) + " " + columnNames.elementAt(1) + " " + data.size());
			for (int i = 1; i <= data.size() / 7; i++) {

				String colName = columnNames.elementAt(0);
				if (colName.trim().endsWith("active")) {
					Boolean val = (Boolean) data.elementAt(i * 7 - 7);
					if (val == true) {
						data1 = "1";
					} else {
						data1 = "0";
					}
				} else {
					data1 = (String) data.elementAt(i * 7 - 7);
				}
				colName = columnNames.elementAt(1);
				if (colName.trim().endsWith("active")) {
					Boolean val;
					try { // Handle possible String element as read from saved
						  // scenario
						val = (Boolean) data.elementAt(i * 7 - 6);
					} catch (ClassCastException ce) {
						val = ((String) data.elementAt(i * 7 - 6) == "true");
					}

					if (val == true) {
						data2 = "1";
					} else {
						data2 = "0";
					}
				} else {
					data2 = (String) data.elementAt(i * 7 - 7);
				}

				// System.out.println(data1 + " " + data2);
				output1.println(data1 + " " + data2);
			}

			output2.println("month	wyT_Sac	x2km");
			for (int i = 2; i <= 6; i++)
				for (int j = 0; j < data.size() / 7; j++) {
					output2.println(Integer.toString(j + 1) + " " + Integer.toString(i - 1) + " " + data.elementAt(j * 7 + i));
				}

			output1.close();
			outputStream1.close();
			output2.close();
			outputStream2.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "DataFileTableModel", JOptionPane.ERROR_MESSAGE);
			log.debug(e.getMessage());
		}

	}

	public Object[][] getTableData() {

		int nCol = columnNames.size();
		int nRow = data.size() / nCol;
		Object[][] tableData = new Object[nRow][nCol];
		for (int i = 0; i < nRow; i++)
			for (int j = 0; j < nCol; j++)
				tableData[i][j] = data.elementAt(i * nCol + j);
		return tableData;
	}

}
