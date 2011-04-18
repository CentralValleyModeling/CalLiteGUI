package com.limno.calgui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.table.TableCellRenderer;

public class MonthlyTableDialog extends JDialog {
	
	JPanel panel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane theScrollPane = new JScrollPane();
	JTable table;
	
	public MonthlyTableDialog(Frame frame, String title, MonthlyTableModel model)
	{
		super(frame, title);
		try
		{
			jbInit(model);
			pack();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public MonthlyTableDialog()
	{
		this(null, "", null);
	}

	public MonthlyTableDialog(String title, MonthlyTableModel model) {
		this(null,title,model);
	}
	
	@SuppressWarnings("serial")
	void jbInit(MonthlyTableModel model) throws Exception	{
		table = new JTable(){
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				if (vColIndex == 0 && !isCellSelected(rowIndex, vColIndex)) {
					c.setBackground(Color.lightGray);
				} else {
					c.setBackground(getBackground());
				}
				return c;
			}
		};

		table.setModel(model);
		table.createDefaultColumnsFromModel();

		table.setRowHeight(20);
		for (int col = 0; col < table.getColumnCount(); col++) {
			table.getColumnModel().getColumn(col).setWidth(50);
		}

		table.setPreferredScrollableViewportSize(new Dimension(table.getColumnCount()*60+60,table.getRowCount()*20));
		
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		table.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				int l = table.getSelectedRow();
				int c = table.getSelectedColumn();
				table.setValueAt(Character.toString(ke.getKeyChar()),l,c);
				table.editCellAt(l, c);
			}
		});

		panel1.setLayout(borderLayout1);
		getContentPane().add(panel1);
		panel1.add(theScrollPane, BorderLayout.CENTER);
		theScrollPane.getViewport().add(table, null);
		pack();
	}


}
