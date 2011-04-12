package com.limno.calgui;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.io.*;
import java.util.*;

public class DataFileTableModel extends AbstractTableModel {

	protected Vector<String> data;
	protected Vector<String> columnNames ;  
	protected String datafile;
	protected EventListenerList listenerList = new EventListenerList();


	public DataFileTableModel(String f){
		datafile = f;
		initVectors();  
	}


	public void initVectors() {

		String aLine ;
		data = new Vector<String>();
		columnNames = new Vector<String>();	

		try {

			FileInputStream fin =  new FileInputStream(datafile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));


			// Read until first non-comment line

			aLine = br.readLine();
			while (aLine.startsWith("!") && aLine != null ) {
				aLine = br.readLine(); 
			}

			aLine = br.readLine();// Skip title line;

			if (aLine != null){
				
				// Extract column names from second line

				StringTokenizer st1 = new StringTokenizer(aLine, "\t| ");
				while(st1.hasMoreTokens())
					columnNames.addElement(st1.nextToken());

				// Extract data - first pass. Assumes we are reading in column-major order


				aLine = br.readLine();
				st1 = 	new StringTokenizer(aLine, "\t| ");
				if (st1.countTokens() < 3) {

					// CASE 1: TWO COLUMNS (month, value)
					
					while (aLine != null) {  
						StringTokenizer st2 = new StringTokenizer(aLine, "\t| ");
						data.addElement(st2.nextToken());
						data.addElement(st2.nextToken());
						aLine = br.readLine();
					}
				}

				else { 

					// CASE 2: THREE COLUMNS (year type, month, value) 
					
					String firstColumnName = (String) columnNames.get(0);
					String secondColumnName = (String) columnNames.get(1);
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
							//System.out.println(Boolean.toString(lastColID == aColID)+" " + lastColID + ":" + aColID + ":" + aRowID + " " + aValue+ " " + Integer.toString(rowCount)+ " " + Integer.toString(columnNames.size()));
							if (Integer.parseInt(lastColID) != Integer.parseInt(aColID)) {
								columnNames.addElement(secondColumnName + aColID);
								lastColID = aColID;
								rowCount = 0;
							}

							rowCount++;
							allValues.add(aValue);
						}
						aLine = br.readLine();
					}
					for (int r = 0; r < rowCount; r ++) {

						data.addElement(Integer.toString(r+1));
						for (int c = 0; c < columnNames.size() - 1 ; c++) {
							//System.out.println(Integer.toString(r)+":"+Integer.toString(c)+":"+Integer.toString(r)+":"+Integer.toString(c*rowCount)+"="+Integer.toString(allValues.size()));

							data.addElement(allValues.get(c*rowCount+r));
						}}

				}

				br.close();  

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getRowCount() {
		return data.size() / getColumnCount();
	}

	public int getColumnCount(){
		return columnNames.size();
	}

	public String getColumnName(int columnIndex) {
		String colName = "";

		if (columnIndex <= getColumnCount())
			colName = (String)columnNames.elementAt(columnIndex);

		return colName;
	}

	public Class getColumnClass(int columnIndex){
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex != 0);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return (String)data.elementAt
		( (rowIndex * getColumnCount()) + columnIndex);
	}
	
    public void addTableModelListener(TableModelListener l) {
     listenerList.add(TableModelListener.class, l);
    }
    
    public void removeTableModelListener(TableModelListener l) {
    	listenerList.remove(TableModelListener.class, l);
    }

    public TableModelListener[] getTableModelListeners() {
    	return (TableModelListener[])listenerList.getListeners(
    			TableModelListener.class);
    }

	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.setElementAt((String) aValue, ( (rowIndex * getColumnCount()) + columnIndex));        
		fireTableCellUpdated(rowIndex, columnIndex);
		//return;
	}
	
	public void fireTableDataChanged() {
		fireTableChanged(new TableModelEvent(this));
	}
	public void fireTableCellUpdated(int row, int column) {
		fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	public void fireTableChanged(TableModelEvent e) {
		// Guaranteed to return a non-null array
		Object  [] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TableModelListener.class) {
				((TableModelListener)listeners[i+1]).tableChanged(e);
			}
		}
	}


	public void writeToFile(String outputFileName) {

		OutputStream outputStream;
		try {
			//outputStream = new FileOutputStream("Config_and_Lookup\\Lookup\\"+outputFileName+".table2");
			outputStream = new FileOutputStream(outputFileName);
		}
		catch (FileNotFoundException e2) {
			System.out.println("Cannot open output file");
			return;
		} 

		try {

			PrintStream output = new PrintStream(outputStream);

			output.println(outputFileName);
			if (columnNames.size() == 2) {
				output.println(columnNames.elementAt(0)+" "+columnNames.elementAt(1));
				for (int i = 1; i <= data.size() / 2; i++) {
					output.println(data.elementAt(i*2-2)+ " " + data.elementAt(i*2-1));
				}

			} else if (columnNames.size() == 6){
				output.println("year_type month day");
				for (int i = 1; i <= 5; i++) 
					for (int j = 0; j < data.size()/6; j++){
						output.println(Integer.toString(i) + " " + Integer.toString(j+1)+ " " + data.elementAt(j*6+i));
					}

				output.close();
				outputStream.close();
			}
		}
		catch (IOException ioe) {
			System.out.println("IOException");
		}

	}

	
	
}



