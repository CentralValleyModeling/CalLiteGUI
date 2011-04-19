package com.limno.calgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

	
	public class StatusFrame extends JFrame {
		JLabel lblStatus=new JLabel();
		JPanel panel1 = new JPanel();
		BorderLayout borderLayout1 = new BorderLayout();
		
		
	    /** Creates a new instance */
	    public StatusFrame(String title) {
			super(title);
			
	        super.setPreferredSize(new Dimension(300, 200));
			super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			panel1.setLayout(borderLayout1);
			
			this.getContentPane().add(panel1);		
			this.getContentPane().add(lblStatus);
			this.pack();
			this.setVisible(true);
			
	    }

	    public void setMessage(String message) {
	    	lblStatus.setText(" "+message);  
	    	super.invalidate();
	    	
	    }   
		
	}
	
