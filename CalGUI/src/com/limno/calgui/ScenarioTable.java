package com.limno.calgui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ScenarioTable extends JFrame {
	
	private JTable scentable;

	public ScenarioTable(String title,String[][]data, String[] headers) {

		super();

		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setPreferredSize(new Dimension(500, 700));
		setMinimumSize(new Dimension(500, 700));
		setLayout(new FlowLayout());
		//GridBagConstraints c = new GridBagConstraints();

		setTitle(title);
		
		scentable = new JTable();
		DefaultTableModel model = new DefaultTableModel(data, headers);
		scentable.setModel(model);
		scentable.setSize(450, 600);
		JScrollPane scrollingtable = new JScrollPane(scentable);
		add(scrollingtable);
		//Set Icon
		java.net.URL imgURL = getClass().getResource("/images/Cal-lite-label-_no_tex08_KF.jpg");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
		
		pack();
		//setAlwaysOnTop(true);
		
		

		// TODO - Add close/cancel button
		
		
		
		

	}
	

}
