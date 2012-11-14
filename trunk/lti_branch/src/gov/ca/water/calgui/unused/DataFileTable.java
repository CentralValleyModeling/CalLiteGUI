package gov.ca.water.calgui.unused;

import gov.ca.water.calgui.DataFileTableModel;
import gov.ca.water.calgui.ExcelAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class DataFileTable extends JPanel {

	public DataFileTable(String dataFilePath, int tID) {
		JTable table;
		DataFileTableModel model;
		Font f;

		f = new Font("SanSerif", Font.PLAIN, 24);
		setFont(f);
		setLayout(new BorderLayout());

		model = new DataFileTableModel(dataFilePath, tID);

		table = new JTable();
		table.setModel(model);
		table.createDefaultColumnsFromModel();
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		ExcelAdapter myAd = new ExcelAdapter(table);

		JScrollPane scrollpane = new JScrollPane(table);
		add(scrollpane);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(2, 2);
	}

	public static void main(String dataFilePath, int tID) {
		JFrame frame = new JFrame("Data File Table");
		DataFileTable panel;

		panel = new DataFileTable(dataFilePath, tID);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setForeground(Color.black);
		frame.setBackground(Color.lightGray);
		frame.getContentPane().add(panel, "Center");

		frame.setSize(panel.getPreferredSize());
		frame.setResizable(true);
		frame.setVisible(true);
		frame.addWindowListener(new WindowCloser());

	}
}

class WindowCloser extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent e) {
		Window win = e.getWindow();
		win.setVisible(false);
		System.exit(0);
	}
}
