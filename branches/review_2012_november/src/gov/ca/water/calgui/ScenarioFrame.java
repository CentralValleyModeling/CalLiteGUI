package gov.ca.water.calgui;

/*
 * ProgressFrame - simple frame to show progress passed by method setText();
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.*;

import org.swixml.SwingEngine;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import rma.awt.JButtonGroup;

public class ScenarioFrame extends JFrame {
	private JLabel lblscen1;
	private JButtonGroup btngrp;
	private JRadioButton rdbAll;
	private JRadioButton rdbDiff;
	private JButton btnComp;
	private JList lstScen;


	public ScenarioFrame(String title, final SwingEngine swix) {

		super();

		// setUndecorated(true);
		// getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setPreferredSize(new Dimension(400, 300));
		setMinimumSize(new Dimension(400, 300));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		setTitle(title);

		lblscen1 = new JLabel("Select Scenarios to Compare", SwingConstants.HORIZONTAL);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(lblscen1, c);

		lstScen = new JList();
		lstScen.setVisibleRowCount(7);
		// populate with file list.
		File dir = new File(".//Scenarios");
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".cls");
			}
		};
		Vector filelist = new Vector(Arrays.asList(dir.list(filter)));
		lstScen.setListData(filelist);
		JScrollPane scrollingList = new JScrollPane(lstScen);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 200;
		c.anchor = GridBagConstraints.WEST;
		add(scrollingList, c);
		scrollingList.setMinimumSize(new Dimension(100, 200));

		btnComp = new JButton("Compare");
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 00;
		c.anchor = GridBagConstraints.WEST;
		btnComp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object[] selected = lstScen.getSelectedValues();
				if (selected.length < 1) {
					JOptionPane.showMessageDialog(null, "You must select at least one scenario file to display.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					Object list[] = new Object[1+selected.length];
					list[0] = "Current_Scenario";
					for (int i = 0; i < selected.length; i++)
						list[i+1] = selected[i];
					ScenarioTable sTable = new ScenarioTable(list, swix);
					sTable.setVisible(true);
					
				}
			}
		});
		add(btnComp, c);

		pack();
		// setAlwaysOnTop(true);

		// TODO - Add close/cancel button

	}

}
