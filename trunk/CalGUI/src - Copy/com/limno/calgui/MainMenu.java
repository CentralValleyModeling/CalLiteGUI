package com.limno.calgui;

import java.awt.event.*;

import javax.swing.*;

import org.swixml.*;

public class MainMenu implements ActionListener, ItemListener {
	private SwingEngine swix;
	
	//Declare public Objects
	JPanel mainmenu;
	
	JRadioButton rdbD1;
	JRadioButton rdbD2;
	JRadioButton rdbD3;
	JRadioButton rdbD4;
	JRadioButton rdbD5;
	JRadioButton rdbD6;
	JRadioButton rdbD7;
	JRadioButton rdbD8;
	JRadioButton rdbD9;
	JRadioButton rdbD10;
	JRadioButton rdbB1;
	JRadioButton rdbB2;
	JRadioButton rdbB3;
	JRadioButton rdbB4;
	JRadioButton rdbB5;
	JRadioButton rdbB6;
	JRadioButton rdbB7;
	JRadioButton rdbB8;
	JRadioButton rdbB9;
	JRadioButton rdbB10;
	JRadioButton rdbU1;
	JRadioButton rdbU2;
	JRadioButton rdbU3;
	JRadioButton rdbU4;
	JRadioButton rdbU5;
	JRadioButton rdbU6;
	JRadioButton rdbU7;
	JRadioButton rdbU8;
	JRadioButton rdbU9;
	JRadioButton rdbU10;
	
	JButton btnU1;
	JButton btnU2;
	JButton btnU3;
	JButton btnU4;
	JButton btnU5;
	JButton btnU6;
	JButton btnU7;
	JButton btnU8;
	JButton btnU9;
	JButton btnU10;

	JCheckBox ckb1;
	JCheckBox ckb2;
	JCheckBox ckb3;
	JCheckBox ckb4;
	JCheckBox ckb5;
	JCheckBox ckb6;
	JCheckBox ckb7;
	JCheckBox ckb8;
	JCheckBox ckb9;
	JCheckBox ckb10;
    
    
    public MainMenu() throws Exception {
    	swix = new SwingEngine( this );
    	swix.render( "MainMenu.xml" ).setVisible(true);
        //new SwingEngine(this).render("MainMenu.xml")
                //.setVisible(true);

    	rdbU1.addItemListener(this);
    	rdbU2.addItemListener(this);
    	rdbU3.addItemListener(this);
    	rdbU4.addItemListener(this);
    	rdbU5.addItemListener(this);
    	rdbU6.addItemListener(this);
    	rdbU7.addItemListener(this);
    	rdbU8.addItemListener(this);
    	rdbU9.addItemListener(this);
    	rdbU10.addItemListener(this);

    	ckb1.addItemListener(this);
    	ckb2.addItemListener(this);
    	ckb3.addItemListener(this);
    	ckb4.addItemListener(this);
    	ckb5.addItemListener(this);
    	ckb6.addItemListener(this);
    	ckb7.addItemListener(this);
    	ckb8.addItemListener(this);
    	ckb9.addItemListener(this);
    	ckb10.addItemListener(this);

    	btnU1.addActionListener(this);
    	btnU2.addActionListener(this);
    	btnU3.addActionListener(this);
    	btnU4.addActionListener(this);
    	btnU5.addActionListener(this);
    	btnU6.addActionListener(this);
    	btnU7.addActionListener(this);
    	btnU8.addActionListener(this);
    	btnU9.addActionListener(this);
    	btnU10.addActionListener(this);

    }
    
    public static void main(String [] args) throws Exception {
        new MainMenu();
        
        
    }
	//React to check box selections.
	public void itemStateChanged(ItemEvent e) {
	    //int index = 0;
	    //char c = '-';
	    Object source = e.getItemSelectable();

	    if (source == ckb1) {
			  rdbD1.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB1.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU1.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit1.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	    } else if (source == ckb2) {
			  rdbD2.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB2.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU2.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
		  //btnEdit2.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	    } else if (source == ckb3) {
			  rdbD3.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB3.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU3.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
		  //btnEdit3.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	    } else if (source == ckb4) {
			  rdbD4.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB4.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU4.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit4.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	    } else if (source == ckb5) {
			  rdbD5.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB5.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU5.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit5.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == ckb6) {
			  rdbD6.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB6.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU6.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	  	  //btnEdit6.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == ckb7) {
			  rdbD7.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB7.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU7.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit7.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == ckb8) {
			  rdbD8.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB8.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU8.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit8.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == ckb9) {
			  rdbD9.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB9.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU9.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit9.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == ckb10) {
			  rdbD10.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbB10.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			  rdbU10.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      //btnEdit10.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      
	      } else if (source == rdbU1) {
	      btnU1.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU2) {
	          btnU2.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU3) {
	          btnU3.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU4) {
	          btnU4.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU5) {
	          btnU5.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU6) {
	          btnU6.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU7) {
	          btnU7.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU8) {
	          btnU8.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU9) {
	          btnU9.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	      } else if (source == rdbU10) {
	          btnU10.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
	    }
	}

	//React to menu selections.
	public void actionPerformed(ActionEvent e) {
	if ("UD_Table".equals(e.getActionCommand())) { 
		createXMLFrame("UD_Table");
	}

	}
    
	protected void createXMLFrame(String frmstr) {
		MyInternalFrame frame = new MyInternalFrame("User Defined");
		frame.setVisible(true); //necessary as of 1.3

		frame.setResizable(true);
		frame.setClosable(true);
		
		frame.setVisible(true);
		mainmenu.add(frame);
		mainmenu.setComponentZOrder(frame, 0);
		
	}
	
    
}