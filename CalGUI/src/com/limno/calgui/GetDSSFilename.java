package com.limno.calgui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import sun.security.util.Debug;

class FileListModel extends DefaultListModel {

	/**
	 * FileListModel class shows file name with full path as tooltip
	 */
	private static final long serialVersionUID = 3394317320429362812L;

}

public class GetDSSFilename implements ActionListener {

	public DefaultListModel lmScenNames;
	JFileChooser fc = new JFileChooser2();
	JList theList;
	JLabel theLabel;
	JTextField theTextField;
	String theFileExt = null;

	public GetDSSFilename(JList aList, JLabel aLabel) {
		lmScenNames = new DefaultListModel();
		fc.setFileFilter(new DSSFileFilter());
		fc.setCurrentDirectory(new File(".//Scenarios"));
		theList = aList;
		theLabel = aLabel;
		theTextField = null;
	}

	public GetDSSFilename(JList aList, JTextField aTextField) {
		lmScenNames = new DefaultListModel();
		fc.setFileFilter(new DSSFileFilter());
		fc.setCurrentDirectory(new File(".//Scenarios"));
		theList = aList;
		theLabel = null;
		theTextField = aTextField;
	}

	public GetDSSFilename(JList aList, JTextField aTextField, String aFileExt) {
		lmScenNames = new DefaultListModel();
		fc.setFileFilter(new SimpleFileFilter(aFileExt));
		fc.setCurrentDirectory(new File(".//Scenarios"));
		theList = aList;
		theLabel = null;
		theTextField = aTextField;
		theFileExt = aFileExt;
	}

	public void actionPerformed(ActionEvent e) {
		int rc;
		if (theFileExt == null)
			rc = fc.showOpenDialog(null);
		else
			rc = fc.showDialog(null, "Save");

		File file;
		if (rc == 0) {
			file = fc.getSelectedFile();
			lmScenNames.addElement(file.getPath());
			if (theList == null || lmScenNames.getSize() == 1) {
				if (theLabel != null) {
					theLabel.setText(file.getName());
					theLabel.setToolTipText(file.getPath());
				} else {
					theTextField.setText(file.getName());
					theTextField.setToolTipText(file.getPath());
				}
			}
			if (theList != null)
				theList.repaint();
		}
		return;
	}

	class FileNameRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3040003845509293885L;

		private JFileChooser2 theOwner;
		private Map<String, String> theToolTips = new HashMap<String, String>();

		public FileNameRenderer(JFileChooser2 jFileChooser) {
			// TODO Auto-generated constructor stub
			theOwner = jFileChooser;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!theOwner.ToolTipFlag) {
				theToolTips.clear();
				File folder = new File(System.getProperty("user.dir") + "\\Scenarios"); // change
																						// to
																						// read
																						// current
																						// directory
				File[] listOfFiles = folder.listFiles();

				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						if (listOfFiles[i].getName().toLowerCase().endsWith(".txt")) {
							try {
								FileInputStream fin = new FileInputStream(listOfFiles[i]);
								BufferedReader br = new BufferedReader(new InputStreamReader(fin));
								String theKey = br.readLine();
								String theValue = br.readLine();
								theToolTips.put(theKey.toLowerCase(), theValue);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					}
					theOwner.ToolTipFlag = true;// need to flag when dierctory
												// changes
				}
			}
			File file = new File(String.valueOf(value));

			if (theToolTips.containsKey(file.getName().toLowerCase())) {
				lbl.setToolTipText(theToolTips.get(file.getName().toLowerCase()));
			} else {
				lbl.setToolTipText("No scenario information for this file");
			}
			lbl.setText(file.getName());
			return lbl;
		}
	}

	class JFileChooser2 extends javax.swing.JFileChooser {
		/**
		 * 
		 */
		private static final long serialVersionUID = -150877374751505363L;

		public boolean ToolTipFlag = false;

		private Component findJList(Component comp) {

			if (comp instanceof JList)
				return comp;
			if (comp instanceof Container) {
				Component[] components = ((Container) comp).getComponents();
				for (int i = 0; i < components.length; i++) {
					// System.out.println(((Component) components[i]).getName()
					// +
					// " " + Integer.toString(i) + " "
					// + ((Component) components[i]).toString());
					Component child = findJList(components[i]);
					if (child != null)
						return child;
				}
			}
			return null;
		}

		public int showOpenDialog(Component c) {
			JList myList = (JList) findJList(this);
			myList.setCellRenderer(new FileNameRenderer(this));
			ToolTipFlag = false;
			return super.showOpenDialog(c);
		}
	}

	class DSSFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			// Convert to lower case before checking extension
			return (file.getName().toLowerCase().endsWith(".dss") || file.isDirectory());
		}

		public String getDescription() {
			return "DSS File (*.dss)";
		}
	}

}
