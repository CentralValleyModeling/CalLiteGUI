/*
 * GUILinks: creates a dictionary that relates GUI elements to switchtable order and daat table file/DSS location
 * 
 * Version 1.0
 * 
 * 2010-12-02
 *  
 */

package gov.ca.water.calgui.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class GUILinks {

	private Map<String,String> mapCtrlToTable;
	private Map<String,String> mapCtrlToTID;
	private Map<String,String> mapCtrlToswitchID;
	private Map<String,String> mapTIDToCtrl;

	/* 
	 * GUILinks.tableNameForCtrl: Method to look up table name to use for a given control
	 * 
	 * 12/3/2010
	 * 
	 */
	
	public String tableNameForCtrl(String ctrlID) {
		return mapCtrlToTable.get(ctrlID);
	}
		
	public String tableIDForCtrl(String ctrlID) {
		return mapCtrlToTID.get(ctrlID);
	}
	
	public String switchIDForCtrl(String ctrlID) {
		return mapCtrlToswitchID.get(ctrlID);
	}
	
	public String CtrlFortableID(String tID) {
		return mapTIDToCtrl.get(tID);
	}
	
	
	/* 
	 * GUILinks.readIn: Method to look read control-to-switch, control-to-table information
	 * 
	 * 12/3/2010
	 * 
	 */
	
	public int readIn (String inputFileName) {

		// Open input file

		Scanner input;
		try 
		{
			input = new Scanner(new FileReader(inputFileName));
		}
		catch (FileNotFoundException e) {
			System.out.println("Cannot open input file " + inputFileName);
			return -1;
		}

		mapCtrlToTable = new HashMap<String,String>();
		mapCtrlToTID = new HashMap<String,String>();
		mapCtrlToswitchID = new HashMap<String,String>();
		mapTIDToCtrl = new HashMap<String,String>();
		
		int lineCount = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lineCount++;
			if (lineCount > 1) {
				// Parse, assuming space and/or tab-delimited
				String[] parts = line.split("[\t]+");
				if (parts.length > 0) {

					String ctrlName = parts[0];
					String switchID  = "";
					String TableID  = "";
					String tableName = "";
					if (parts.length > 6) {
						tableName = parts[6];
						if (parts.length > 7) {
							switchID = parts[7];
						}
						if (parts.length > 8) {
							TableID = parts[8];
						}
					}
					mapCtrlToTable.put(ctrlName,tableName);
					if (switchID != "" ) {
						mapCtrlToswitchID.put(ctrlName,switchID);
					}
					if (TableID != "" ) {
						mapCtrlToTID.put(ctrlName,TableID);
						mapTIDToCtrl.put(TableID, ctrlName);
					}
				}
			}
		}

		input.close();

/*		Set<Map.Entry<String,String>> s = mapCtrlToTable.entrySet();

		//Move next key and value of Map by iterator
		Iterator<Map.Entry<String,String>> it=s.iterator();

		while(it.hasNext())
		{
			// key=value separator this by Map.Entry to get key and value
			Map.Entry<String,String> m =(Map.Entry<String,String>)it.next();

			// getKey is used to get key of Map
			String key = (String) m.getKey();

				System.out.println("Key :"+key+"  Value : " + mapCtrlToTable.get(key));
		}

		for (integer i = 1; i < 15; i++) {
			System.out.println("Switch: " + Integer.toString(i) + " - " + mapSwitchToCtrl.get(Integer.toString(i)));
		}
*/
		return 0;
	}


}
