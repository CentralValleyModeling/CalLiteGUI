package com.limno.calgui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ScenarioTable extends JFrame {

	private JTable scentable;
	private JScrollPane scrollingtable;

	public ScenarioTable(String title, String[][] data, String[] headers) {

		super();

		// setUndecorated(true);
		// getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setPreferredSize(new Dimension(600, 700));
		setMinimumSize(new Dimension(600, 700));
		setLayout(new FlowLayout());
		// GridBagConstraints c = new GridBagConstraints();

		setTitle(title);

		scentable = new JTable();
		DefaultTableModel model = new DefaultTableModel(data, headers);
		scentable.setModel(model);
		scrollingtable = new JScrollPane(scentable);
		scrollingtable.setPreferredSize(new Dimension(480, 600));
		add(scrollingtable);
		// Set Icon
		java.net.URL imgURL = getClass().getResource("/images/Cal-lite-label-_no_tex08_KF.jpg");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

		pack();
		this.addComponentListener((new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				Dimension dim = ((Component) e.getSource()).getSize();
				int width = (int) (dim.width * 0.95);
				int height = (int) (dim.height * 0.90);
				scrollingtable.setPreferredSize(new Dimension(width, height));
				scrollingtable.revalidate();
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}
		}));

		// TODO - Add close/cancel button

	};

}
