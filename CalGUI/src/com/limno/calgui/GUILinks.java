/*
 * GUILinks: creates a dictionary that relates GUI elements to switchtable order and daat table file/DSS location
 * 
 * Version 1.0
 * 
 * 2010-12-02
 *  
 */

package com.limno.calgui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class GUILinks {

	private Map<String,String> mapCtrlToTable;
	private Map<String,String> mapSwitchToCtrl;

	/* 
	 * GUILinks.tableNameForCtrl: Method to look up table name to use for a given control
	 * 
	 * 12/3/2010
	 * 
	 */
	
	public String tableNameForCtrl(String ctrlID) {
		return mapCtrlToTable.get(ctrlID);
	}
	
	/* 
	 * GUILinks.ctrlForSwitch: Method to look up control name associated with switch # (position in switch table)
	 * 
	 * 12/3/2010
	 * 
	 */
		
	public String ctrlForSwitch(String switchID) {
		return mapSwitchToCtrl.get(switchID);
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
		mapSwitchToCtrl = new HashMap<String,String>();

		int lineCount = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lineCount++;
			if (lineCount > 1) {
				// Parse, assuming space and/or tab-delimited
				String[] parts = line.split("[ \\t]+");
				if (parts.length > 0) {

					String ctrlID = parts[0];
					String switchID  = "";
					String tableName = "";
					if (parts.length > 1) {
						switchID = parts[1];
						if (parts.length > 2) {
							tableName = parts[2];
						}
					}
					mapCtrlToTable.put(ctrlID,tableName);
					if (switchID != "" ) {
						mapSwitchToCtrl.put(switchID,ctrlID);
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
