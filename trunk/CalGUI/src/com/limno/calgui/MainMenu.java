package com.limno.calgui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.io.*;

import javax.swing.*;

import org.swixml.*;

public class MainMenu implements ActionListener, ItemListener {
	private SwingEngine swix;

	//Declare public Objects
	JPanel mainmenu;
	JPanel regulations;
	GUILinks gl;

	JMenuBar menu;

	public MainMenu() throws Exception {


		// Read GUI configuration

		swix = new SwingEngine( this );
		swix.render( new File("Config_and_Lookup\\Config\\MainMenu.xml") ).setVisible(true);


		//Set ActionListeners (Regulations Page)

		swix.setActionListener( menu, this );
		swix.setActionListener( regulations, this );		


		//Set ItemListeners (Regulations Page)

		Component[] components = regulations.getComponents( );
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JRadioButton) {
				JRadioButton r = (JRadioButton) components[i]; 
				r.addItemListener(this);
			}
			else if (components[i] instanceof JCheckBox) {
				JCheckBox ckb = (JCheckBox) components[i];
				ckb.addItemListener(this);
			}
		}


		// Set current directory (Run Settings Page)

		String currentDir =  System.getProperty("user.dir");
		JTextField tb = (JTextField) swix.find("tbRSdir");
//		tb.setText(currentDir);


		// Read switch lookup

		gl = new GUILinks();
		gl.readIn("Config_and_Lookup\\Config\\GUI_Links.table");

	}

	public static void main(String [] args) throws Exception {

		new MainMenu();

	}

	//Respond to selection of a check box or radiobox.
	public void itemStateChanged(ItemEvent e) {
		JComponent component = (JComponent) e.getItem();
		//was "e.getItemSelected"
		String cName = component.getName();
		if (cName.startsWith("ckb")) {
			// CheckBox in Regulations panel changed
			String cID = cName.substring(3);
			component = (JComponent) swix.find("rdbD"+cID); if (component != null) component.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			component = (JComponent) swix.find("rdbB"+cID); if (component != null) component.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			component = (JComponent) swix.find("rdbU"+cID); if (component != null) component.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			component = (JComponent) swix.find("btnU"+cID); if (component != null) component.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
		}
		else if (cName.startsWith("rdb")) {
			// RadioButton in Regulations panel changed
			JButton button = (JButton) swix.find("btnU" + cName.substring(4));
			if (cName.startsWith("rdbU")) {
				button.setText("Edit");
			} 
			else {
				button.setText("View");
			}
		}
	}


	//React to menu selections.
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("UD_Table")) { 

			// Figure out calling button and look up table
			String cID = e.getActionCommand().substring(8);
			createDTableFrame(cID);
			
			//System.out.println(cID);
			//System.out.println(gl.tableNameForCtrl(cID));

			
		} else if ("AC_RUN".equals(e.getActionCommand()))  {
			/*
			 *
			 * 
			 */

			// Write DLTREGULATION file

			OutputStream outputStream;
			try {
				outputStream = new FileOutputStream("Config_and_Lookup\\Lookup\\DLTREGULATION.table");
			}
			catch (FileNotFoundException e2) {
				System.out.println("Cannot open DLTRegulation file");
				return;
			} 

			try {

				PrintStream output = new PrintStream(outputStream);

				output.println("gui_DLTREGULATION");
				output.println("SWITCHID OPTION");

				for (int switchIdx = 1; switchIdx <= 14; switchIdx++) {
					String switchID = Integer.toString(switchIdx);
					String cID = gl.ctrlForSwitch(switchID);
					int option = 0;
					JCheckBox cb = (JCheckBox) swix.find("ckb"+cID);
					if (cb == null) {
						option = 0;
					} else if (!cb.isSelected()) {
						option = 0;
					} else {
						JRadioButton rb = (JRadioButton) swix.find("rdbU"+cID);
						JButton b = (JButton) swix.find("btnU"+cID);
						if (b == null) {
							option = 1; 
						} else if (!b.isVisible()) {
							option = 1;
						} else if (rb.isSelected()) {
							option = 2;
						} else {
							option = 1;
						}
					}

					output.println(switchID + " " + option);

					//  Output table if needed
					if ((option == 2) || 
							((option == 1) && (swix.find("btnU"+cID)).isVisible())) {

						System.out.println("Output to " + gl.tableNameForCtrl(cID));
						int tID = Integer.parseInt(cID);
						if (dTableModels[tID] == null) {
							System.out.println("Table not initialized");
						}
						else {
							dTableModels[tID].writeToFile(gl.tableNameForCtrl(cID));
						}
					}
				}

				output.close();
				outputStream.close();
			}
			catch (IOException ioe) {
				System.out.println("IOException");
			}

			// "Run" model 

			try
			{            
				Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\CalLite.BAT");
				int exitVal = proc.waitFor();
				System.out.println("Process exitValue: " + exitVal);
			} catch (Throwable t)
			{
				t.printStackTrace();
			}
			/*			try {
				Runtime.getRuntime().exec("cmd /c start " + System.getProperty("user.dir") + "\\	CalLite.BAT");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
			 */
		}


	}


	// This method returns the selected radio button in a button group
	public static JRadioButton getSelection(ButtonGroup group) {
		for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
			JRadioButton b = (JRadioButton)e.nextElement();
			if (b.getModel() == group.getSelection()) {
				return b;
			}
		}
		return null;
	}

	private DataFileTableModel[] dTableModels;

	protected void createDTableFrame(String cID) {

		String fileName = gl.tableNameForCtrl(cID);


		int tID = Integer.parseInt(cID);
		if (dTableModels == null) {
			dTableModels = new DataFileTableModel[20];
		}
		if (dTableModels[tID] == null){
			dTableModels[tID] = new DataFileTableModel("Config_and_Lookup\\Lookup\\"+fileName+".table");
		}

		/*JFrame frame = new JFrame(fileName);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		frame.setVisible(true); //necessary as of 1.3
		frame.setResizable(true);
		frame.setSize(512,768);

		//DataFileTable dTable = new DataFileTable(dTableModels[tID]);

		frame.getContentPane().add(dTable);

		frame.pack();
		frame.setVisible(true);

		 */
		TableDialog td = new TableDialog(fileName,dTableModels[tID]);
		td.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		td.setVisible(true);
	}



}