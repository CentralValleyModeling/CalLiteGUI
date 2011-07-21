package com.limno.calgui;

/*
 * ProgressFrame - simple frame to show progress passed by method setText();
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import rma.awt.JButtonGroup;

public class ScenarioFrame extends JFrame {
	private JLabel lblscen1;
	private JLabel lblscen2;
	private JTextField tfscen1;
	private JTextField tfscen2;
	private JButtonGroup btngrp;
	private JRadioButton rdbAll;
	private JRadioButton rdbDiff;
	private JButton btn1;
	private JButton btn2;
	private JButton btnComp;
	private StringBuffer sbScen1;
	private StringBuffer sbScen2;

	public ScenarioFrame(String title) {

		super();

		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setPreferredSize(new Dimension(400, 400));
		setMinimumSize(new Dimension(400, 400));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		setTitle(title);
		
		lblscen1 = new JLabel("Scenario 1: ",SwingConstants.HORIZONTAL);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(lblscen1,c);
		
		tfscen1 = new JTextField("",SwingConstants.LEFT);
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 160;
		c.anchor = GridBagConstraints.WEST;
		add(tfscen1,c);
		
		btn1 = new JButton("Select");
		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 00;
		c.anchor = GridBagConstraints.WEST;
		btn1.addActionListener(new ActionListener() {
			 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ScenarioFileFilter());
				fc.setCurrentDirectory(new File(".//Scenarios"));

				int retval = fc.showOpenDialog(null);

				if (retval == JFileChooser.APPROVE_OPTION) {
					// ... The user selected a file, get it, use it.
					File file1 = fc.getSelectedFile();
					sbScen1 = GUI_Utils.ReadScenarioFile(file1);
					tfscen1.setText(file1.toString());
				}	
				
			}
        });
		add(btn1,c);
		
		lblscen2 = new JLabel("Scenario 2: ",SwingConstants.HORIZONTAL);
		c.gridx = 0;
		c.gridy = 1;
		c.ipadx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(lblscen2,c);
		
		tfscen2 = new JTextField("",SwingConstants.LEFT);
		c.gridx = 1;
		c.gridy = 1;
		c.ipadx = 160;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(tfscen2,c);
		
		btn2 = new JButton("Select");
		c.gridx = 2;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.anchor = GridBagConstraints.WEST;
		btn2.addActionListener(new ActionListener() {
			 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ScenarioFileFilter());
				fc.setCurrentDirectory(new File(".//Scenarios"));

				int retval = fc.showOpenDialog(null);

				if (retval == JFileChooser.APPROVE_OPTION) {
					// ... The user selected a file, get it, use it.
					File file1 = fc.getSelectedFile();
					sbScen2 = GUI_Utils.ReadScenarioFile(file1);
					tfscen2.setText(file1.toString());
				}	
				
			}
        });
		add(btn2,c);
		
		rdbAll = new JRadioButton("List All");
		rdbAll.setSelected(true);
		c.gridx = 0;
		c.gridy = 2;
		c.ipadx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth=3;
		c.anchor = GridBagConstraints.EAST;
		add(rdbAll,c);
		
		rdbDiff = new JRadioButton("List Differences");
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth=3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		add(rdbDiff,c);
		
		btngrp = new JButtonGroup();
		btngrp.add(rdbAll);
		btngrp.add(rdbDiff);

		btnComp = new JButton("Compare");
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 00;
		c.anchor = GridBagConstraints.WEST;
		btnComp.addActionListener(new ActionListener() {
			 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String strFile1=(String) tfscen1.getText();
				String strFile2=(String) tfscen2.getText();
				
				if ((strFile1.isEmpty()) || (strFile2.isEmpty())) {
					JOptionPane.showMessageDialog(null, "You must specify two scenario files to compare.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
										
					
					
					
					
				}
				
			}
        });
		add(btnComp,c);		
		
		
		
		
		
		
		
		
		pack();
		//setAlwaysOnTop(true);
		
		

		// TODO - Add close/cancel button
		
		
		
		

	}

}
