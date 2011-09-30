package com.limno.calgui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

class FileListModel extends DefaultListModel {

	/**
	 * FileListModel class shows file name with full path as tooltip
	 */
	private static final long serialVersionUID = 3394317320429362812L;

}

public class GetDSSFilename implements ActionListener {

	public DefaultListModel lmScenNames;
	public JFileChooser fc = new JFileChooser2();
	JList theList;
	JLabel theLabel;
	JTextField theTextField;
	String theFileExt = null;
	JRadioButton rdbopt1;
	JRadioButton rdbopt2;

	public GetDSSFilename(JList aList, JLabel aLabel) {
		theLabel = aLabel;
		theFileExt = "DSS";
		theTextField = null;
		Setup(aList);
	}

	public GetDSSFilename(JList aList, JTextField aTextField) {
		theLabel = null;
		theFileExt = "DSS";
		theTextField = aTextField;
		Setup(aList);
	}

	public GetDSSFilename(JList aList, JTextField aTextField, String aFileExt) {
		theLabel = null;
		theTextField = aTextField;
		theFileExt = aFileExt;
		Setup(aList);
	}

	public GetDSSFilename(JList aList, JLabel aLabel, JRadioButton rdb1, JRadioButton rdb2) {
		theLabel = aLabel;
		theFileExt = "DSS";
		theTextField = null;
		Setup(aList);
		rdbopt1 = rdb1;
		rdbopt2 = rdb2;
	}

	public int dialogRC;

	private void Setup(JList aList) {

		if (theFileExt.equals("DSS")) {
			fc.setFileFilter(new DSSFileFilter());
			fc.setCurrentDirectory(new File(".//Scenarios"));
		} else {
			fc.setFileFilter(new GeneralFileFilter(theFileExt));
			if (theFileExt.equals("PDF") || theFileExt.equals("CLS"))
				fc.setCurrentDirectory(new File(".//Scenarios"));
			else
				fc.setCurrentDirectory(new File(".//Config"));
		}
		if (aList != null) {

			lmScenNames = new DefaultListModel();
			lmScenNames.addListDataListener(new MyListDataListener());

			theList = aList;
			theList.setCellRenderer(new RBListRenderer());
			theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Add a mouse listener to handle changing selection

			theList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					JList list = (JList) event.getSource();

					// Get index of item clicked

					int index = list.locationToIndex(event.getPoint());

					// Toggle selected state

					for (int i = 0; i < list.getModel().getSize(); i++) {
						RBListItem item = (RBListItem) list.getModel().getElementAt(i);
						if (i == index) {
							item.setSelected(true);
							list.repaint(list.getCellBounds(i, i));
						} else {
							if (item.isSelected())
								list.repaint(list.getCellBounds(i, i));
							item.setSelected(false);
						}
					}

					// Repaint cell

					list.repaint(list.getCellBounds(index, index));
				}
			});
		}
	}

	class MyListDataListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			// System.out.println("Changed");
		}

		public void intervalAdded(ListDataEvent e) {
			// System.out.println(lmScenNames.getSize());
			if (rdbopt1 != null && rdbopt2 != null) {
				rdbopt1.setEnabled(lmScenNames.getSize() > 1);
				rdbopt2.setEnabled(lmScenNames.getSize() > 1);
			}
		}

		public void intervalRemoved(ListDataEvent e) {
			// System.out.println(lmScenNames.getSize());
			if (rdbopt1 != null && rdbopt2 != null) {
				rdbopt1.setEnabled(lmScenNames.getSize() > 1);
				rdbopt2.setEnabled(lmScenNames.getSize() > 1);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if ((((Component) obj).getName() != null) && ((Component) obj).getName().equals("btnDelScenario")) {
			if ((theList != null) && lmScenNames.getSize() > 0) {
				int todel = -1;
				for (int i = 0; i < lmScenNames.getSize(); i++)
					if (((RBListItem) lmScenNames.getElementAt(i)).isSelected())
						todel = i;
				if (todel > 0)
					((RBListItem) lmScenNames.getElementAt(todel - 1)).setSelected(true);
				else if (todel < lmScenNames.getSize() - 1)
					((RBListItem) lmScenNames.getElementAt(todel + 1)).setSelected(true);
				lmScenNames.remove(todel);
			}
		} else {
			int rc;
			if (theFileExt == null)
				rc = fc.showOpenDialog(null);
			else
				rc = fc.showDialog(null, theFileExt.equals("DSS") ? "Open" : "Save");

			dialogRC = rc;
			File file;
			if (rc == 0) {
				file = fc.getSelectedFile();
				if (theFileExt.equals("PDF") && !file.getName().toLowerCase().endsWith(".pdf")) {
					file = new File(file.getPath() + ".PDF");
				}
				if (theFileExt.equals("CLS") && !file.getName().toLowerCase().endsWith(".cls")) {
					file = new File(file.getPath() + ".CLS");
				}
				boolean match = false;
				if (theList != null) {
					for (int i = 0; i < lmScenNames.getSize(); i++) {
						RBListItem rbli = (RBListItem) lmScenNames.getElementAt(i);
						match = match | (rbli.toString().equals(file.getPath()));
					}
					if (!match)
						lmScenNames.addElement(new RBListItem(file.getPath(), file.getName()));
				}
				if (match) {
					JOptionPane.showMessageDialog(null, "Scenario \"" + file.getPath() + "\" is already in the Scenario list.","Alert",JOptionPane.ERROR_MESSAGE);
				} else {
					if (theList == null || lmScenNames.getSize() == 1) {
						if (theList != null)
							((RBListItem) lmScenNames.getElementAt(0)).setSelected(true);
						if (theLabel != null) {
							// theLabel.setText(file.getName());
							// theLabel.setToolTipText(file.getPath());
						} else if (theTextField != null) {
							theTextField.setText(file.getName());
							theTextField.setToolTipText(file.getPath());
						}
					}
					if (theList != null) {
						theList.ensureIndexIsVisible(lmScenNames.getSize() - 1);
						theList.revalidate();
						theList.validate();
						theList.getParent().invalidate();
					}

				}
			}
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

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
								String theKey = br.readLine().toLowerCase();
								String theValue = br.readLine() + "\n" + br.readLine() + "\n" + br.readLine();
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

	class GeneralFileFilter extends javax.swing.filechooser.FileFilter {
		private String extension;
		private String description;

		GeneralFileFilter(String extension) {
			this.extension = "." + extension.toLowerCase();
			this.description = extension.toUpperCase() + " File (*." + extension.toLowerCase() + ")";

		}

		public boolean accept(File file) {
			// Convert to lower case before checking extension
			return (file.getName().toLowerCase().endsWith(extension) || file.isDirectory());
		}

		public String getDescription() {
			return description;
		}
	}

	public class RBListItem {
		private String label;
		private String fullname;
		private boolean isSelected = false;

		public RBListItem(String label, String label2) {
			this.label = label2;
			this.fullname = label;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public String toString() {
			return fullname;
		}

		public String toString2() {
			return label;
		}
	}

	// Handles rendering cells in the list using a check box

	class RBListRenderer extends JRadioButton implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
			setEnabled(list.isEnabled());
			setSelected(((RBListItem) value).isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(((RBListItem) value).toString2());
			this.setToolTipText(value.toString());
			return this;
		}
	}
}
