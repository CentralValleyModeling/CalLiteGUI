package com.limno.calgui;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class MonthlyTablePanel extends JPanel implements ActionListener, ComponentListener {
	JPanel panel;
	JScrollPane scrollPane;

	final String LINE_BREAK = "\n"; 
	final String CELL_BREAK = "\t"; 
	final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard(); 

	MonthlyTablePanel(String title, TimeSeriesContainer[] tscs) {
		super();



		Box box = Box.createVerticalBox(); 
		JButton copy = new JButton("Copy to Clipboard");
		copy.setAlignmentX(LEFT_ALIGNMENT);
		copy.addActionListener((ActionListener) this);


		panel = new JPanel();
		// panel.setPreferredSize(new Dimension(70, 600));
		panel.setLayout((LayoutManager) (new BoxLayout(panel, BoxLayout.PAGE_AXIS)));

		scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(750, 600));


		DecimalFormat df1 = new DecimalFormat("#.#");
		DecimalFormat df2 = new DecimalFormat("#.##");
		HecTime ht = new HecTime();
		// Count forward to right month - hardcoded to 10 for now
		// TODO - match to input
		Vector<String> columns = new Vector<String>();
		columns.addElement("WY");
		columns.addElement("Oct");
		columns.addElement("Nov");
		columns.addElement("Dec");
		columns.addElement("Jan");
		columns.addElement("Feb");
		columns.addElement("Mar");
		columns.addElement("Apr");
		columns.addElement("May");
		columns.addElement("Jun");
		columns.addElement("Jul");
		columns.addElement("Aug");
		columns.addElement("Sep");
		if (tscs[0].units.equals("CFS")) {
			columns.addElement("Ann");
		}

		for (int s = 0; s < tscs.length; s++) {

			JLabel label = new JLabel();
			label.setText(title + " (" + tscs[s].units + ") - " + tscs[s].fileName);
			panel.add(label);

			int first = 0;
			ht.set(tscs[s].times[first]);
			while (ht.month() != 10) {
				first++;
				ht.set(tscs[s].times[first]);
			}
			Vector<String> data = new Vector<String>();
			double sum = 0;
			for (int i = first; i < tscs[s].numberValues; i++) {
				ht.set(tscs[s].times[i]);
				int y = ht.year();
				int m = ht.month();
				int wy = (m < 10) ? y : y + 1;
				if ((i - first) % 12 == 0) {
					if (i != first)
						if (tscs[s].units.equals("CFS"))
							data.addElement(df1.format(sum));
					sum = 0;
					data.addElement(Integer.toString(wy));
				}
				sum = sum + tscs[s].values[i];
				data.addElement(df1.format(tscs[s].values[i]));
			}
			if (tscs[s].units.equals("CFS")) {
				data.addElement(df1.format(sum));
			}

			SimpleTableModel2 model = new SimpleTableModel2(data, columns);
			JTable table = new JTable(model);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			TableColumn col = table.getColumnModel().getColumn(0);
			col.setPreferredWidth(50);
			
			addComponentListener((ComponentListener) this);
			panel.add(table.getTableHeader(), BorderLayout.NORTH);
			panel.add(table);

			box.add(scrollPane);
			box.add(copy);
			add(box);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JComponent component = (JComponent) e.getSource();
		if (component instanceof JButton) {
			JButton btn= (JButton) component;
			String cName = btn.getText();
			if (cName != null) {
				if (cName.startsWith("Copy")) {
					StringBuffer excelStr=new StringBuffer();

					Component[] components =panel.getComponents();

					for (int i = 0; i < components.length; i++) {
						if (components[i] instanceof JTable) {
							JTable table = (JTable) components[i];
							int numCols=table.getColumnCount();
							int numRows=table.getRowCount();


							//get column headers
							for (int k=0; k<numCols; k++) { 
								excelStr.append(table.getColumnModel().getColumn(k).getHeaderValue());
								if (k<numCols-1) { 
									excelStr.append(CELL_BREAK); 
								} 
							} 
							excelStr.append(LINE_BREAK);

							//get cell values
							for (int j=0; j<numRows; j++) { 
								for (int k=0; k<numCols; k++) { 
									excelStr.append(escape(table.getValueAt(j, k))); 
									if (k<numCols-1) { 
										excelStr.append(CELL_BREAK); 
									} 
								} 
								excelStr.append(LINE_BREAK); 
							} 

							StringSelection sel  = new StringSelection(excelStr.toString()); 
							CLIPBOARD.setContents(sel, sel); 
						} else if (components[i] instanceof JLabel) {
							JLabel label = (JLabel) components[i];
							excelStr.append(label.getText());
							excelStr.append(LINE_BREAK);
						}
					}
				}
			}
		}
	}


	private String escape(Object cell) { 
		return cell.toString().replace(LINE_BREAK, " ").replace(CELL_BREAK, " "); 
	} 

	class SimpleTableModel2 extends AbstractTableModel {
		protected Vector<String> data;
		protected Vector<String> columnNames;

		public SimpleTableModel2(Vector<String> datain, Vector<String> columnin) {
			data = datain;
			columnNames = columnin;
		}

		public int getRowCount() {
			return data.size() / getColumnCount();
		}

		public int getColumnCount() {
			return columnNames.size();
		}

		public String getColumnName(int columnIndex) {
			String colName = "";
			if (columnIndex <= getColumnCount())
				colName = (String) columnNames.elementAt(columnIndex);
			return colName;
		}

		public Class getColumnClass(int columnIndex) {
			return String.class;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return (String) data.elementAt((rowIndex * getColumnCount()) + columnIndex);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			data.setElementAt((String) aValue, ((rowIndex * getColumnCount()) + columnIndex));
			// return;
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

		Dimension dim = super.getSize();
		int width=(int) (dim.width*0.99);
		int height=(int) (dim.height*0.90);
		scrollPane.setPreferredSize(new Dimension(width,height));
		scrollPane.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
