package com.limno.calgui.unused;

import javax.swing.*;

import com.limno.calgui.DataFileTableModel;
import com.limno.calgui.ExcelAdapter;

import java.awt.event.*;
import java.awt.*;

public class DataFileTable extends JPanel {
  
	public DataFileTable(String dataFilePath, int tID) {
    JTable table;
    DataFileTableModel model;
    Font f;
    

    f = new Font("SanSerif",Font.PLAIN,24);
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

 public Dimension getPreferredSize(){
    return new Dimension(2,2);
    }
    
 public static void main(String dataFilePath, int tID) {
    JFrame frame = new JFrame("Data File Table");
    DataFileTable panel;
        
    panel = new DataFileTable(dataFilePath, tID);

    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setForeground(Color.black);
    frame.setBackground(Color.lightGray);
    frame.getContentPane().add(panel,"Center");
            
    frame.setSize(panel.getPreferredSize());
    frame.setResizable(true);
    frame.setVisible(true);
    frame.addWindowListener(new WindowCloser());
    
    
    }
 }

class WindowCloser extends WindowAdapter {
 public void windowClosing(WindowEvent e) {
   Window win = e.getWindow();
   win.setVisible(false);
   System.exit(0);
    }
}

