package gov.ca.water.calgui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuListener;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;

public class GUIUtils {

	private static Logger log;

	public static void setMouseListener(Component component, Object obj) {

		component.addMouseListener((MouseListener) obj);

		for (Component child : ((Container) component).getComponents()) {
			setMouseListener(child, obj);
		}
	}

	public static void setMenuListener(Component component, Object obj) {

		if (component instanceof JMenu) {
			JMenu m = (JMenu) component;
			m.addMenuListener((MenuListener) obj);
		}

		for (Component child : ((Container) component).getComponents()) {
			setMenuListener(child, obj);
		}
	}

	public static void setChangeListener(Component component, Object obj) {

		if (component instanceof JTabbedPane) {
			JTabbedPane tp = (JTabbedPane) component;
			tp.addChangeListener((ChangeListener) obj);
		}
		for (Component child : ((Container) component).getComponents()) {
			setChangeListener(child, obj);
		}
	}

	public static void setCheckBoxItemListener(Component component, Object obj) {

		if (component instanceof JCheckBox) {
			JCheckBox c = (JCheckBox) component;
			c.addItemListener((ItemListener) obj);
		}

		for (Component child : ((Container) component).getComponents()) {
			setCheckBoxItemListener(child, obj);
		}
	}

	public static void setRadioButtonItemListener(Component component, Object obj) {

		if (component instanceof JRadioButton) {
			JRadioButton r = (JRadioButton) component;
			r.addItemListener((ItemListener) obj);
		}

		for (Component child : ((Container) component).getComponents()) {
			setRadioButtonItemListener(child, obj);
		}
	}

	public static void setCheckBoxorRadioButtonItemListener(Component component, Object obj) {

		if (component instanceof JCheckBox || component instanceof JRadioButton) {
			((AbstractButton) component).addItemListener((ItemListener) obj);
		}

		for (Component child : ((Container) component).getComponents()) {

			setCheckBoxorRadioButtonItemListener(child, obj);
		}
	}

	public static void toggleEnComponentAndChildren(Component component, Boolean b) {

		component.setEnabled(b);

		for (Component child : ((Container) component).getComponents()) {
			toggleEnComponentAndChildren(child, b);
		}
	}

	public static void toggleEnComponentAndChildren(Component component, Boolean b, Object obj) {

		if (component.getClass() == obj) {
			component.setEnabled(b);
		}

		for (Component child : ((Container) component).getComponents()) {
			toggleEnComponentAndChildren(child, b, obj);
		}
	}

	public static void toggleVisComponentAndChildren(Component component, Boolean b) {

		component.setVisible(b);

		for (Component child : ((Container) component).getComponents()) {
			toggleVisComponentAndChildren(child, b);
		}
	}

	public static void toggleEnComponent(Component component, Boolean b) {
		component.setEnabled(b);
	}

	public static void toggleVisComponent(Component component, Boolean b) {
		component.setVisible(b);
	}

	public static void toggleVisComponents(Component[] components, Boolean b) {

		for (int i = 0; i < components.length; i++) {
			components[i].setVisible(b);
		}

	}

	public static void toggleVisComponentAndChildrenCrit(Component component, String crit, Boolean b) {
		String cName = component.getName();
		if (cName != null) {
			if (cName.startsWith(crit)) {
				component.setVisible(b);
			}
		}
		for (Component child : ((Container) component).getComponents()) {
			toggleVisComponentAndChildrenCrit(child, crit, b);
		}
	}

	public static void toggleSelComponentAndChildren(Component component, Boolean b, Object obj) {

		if (component.getClass() == obj) {
			((AbstractButton) component).setSelected(b);
		}

		for (Component child : ((Container) component).getComponents()) {
			toggleSelComponentAndChildren(child, b, obj);
		}
	}

	public static int countSelectedButtons(Component component, Object obj, int ct) {

		if (component.getClass() == obj) {
			if (((AbstractButton) component).isSelected()) {
				++ct;
			}
		}

		for (Component child : ((Container) component).getComponents()) {
			ct = countSelectedButtons(child, obj, ct);
		}

		return ct;
	}

	public static StringBuffer setControlValues(Component component, StringBuffer sb) {
		// System.out.println(component.getName());
		String comp = "";
		String value = "";
		Boolean val;

		final String NL = System.getProperty("line.separator");

		if (component instanceof JTextField || component instanceof NumericTextField || component instanceof JTextArea) {
			comp = component.getName();
			value = ((JTextComponent) component).getText();
			if (comp != null) {
				sb.append(comp + "|" + value + NL);
			}
		} else if (component instanceof JSpinner) {
			comp = component.getName();
			value = ((JSpinner) component).getValue().toString();
			if (comp != null) {
				sb.append(comp + "|" + value + NL);
			}
		} else if (component instanceof JCheckBox || component instanceof JRadioButton) {
			comp = component.getName();
			val = ((AbstractButton) component).isSelected();
			value = val.toString();
			if (comp != null) {
				sb.append(comp + "|" + value + NL);
			}
		}

		if (component instanceof JSpinner) {

		} else {
			for (Component child : ((Container) component).getComponents()) {
				setControlValues(child, sb);
			}
		}
		return sb;
	}

	public static StringBuffer getTableModelData(DataFileTableModel[] dTableModels, ArrayList GUITables, GUILinks gl,
	        StringBuffer sb, SwingEngine swix) {
		final String NL = System.getProperty("line.separator");

		if (dTableModels == null) {
			System.out.println("Tables not initialized");
		} else {
			// for (int switchIdx = 1; switchIdx <= 14; switchIdx++) {
			for (int i = 0; i < GUITables.size(); i++) {
				String line = GUITables.get(i).toString();
				String[] parts = line.split("[|]");
				String cName = parts[0].trim();
				String tableName = gl.tableNameForCtrl(cName);
				String switchID = gl.switchIDForCtrl(cName);
				int tID;
				if (switchID.equals("n/a")) {
					tID = 0;
				} else {
					tID = Integer.parseInt(gl.tableIDForCtrl(cName));
				}
				AbstractButton ckb = (AbstractButton) swix.find(cName);

				// int tID = Integer.parseInt(cID);
				if (dTableModels[tID] == null) {
					System.out.println("Table not initialized");
				} else if (!ckb.isSelected() && !cName.startsWith("op_btn")) { // option checked off
					System.out.println("Table not selected");
				} else {
					Object[][] dataArr;
					dataArr = dTableModels[tID].getTableData();
					String dataStr = "";
					for (int row = 0; row < dataArr.length; row++) {
						for (int col = 0; col < dataArr[row].length; col++) {
							if (col == 0) {
								dataStr = dataStr + dataArr[row][col].toString();
							} else {
								dataStr = dataStr + "," + dataArr[row][col].toString();
							}
						}
						if (row != dataArr.length) {
							dataStr = dataStr + ";";
						} else {
							dataStr = dataStr + ";";
						}
					}
					sb.append(tID + "|" + dataStr + NL);
				}
			}
		}
		return sb;
	}

	public static Boolean[] setControlValues(File f, SwingEngine swix, DataFileTableModel[] dTableModels, GUILinks gl) {

		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;

		String textinLine;
		String comp;
		String value;
		Integer val1;
		Boolean val = false;
		String delims = "[|]";
		final Boolean[] RegUserEdits = new Boolean[20];

		try {
			fs = new FileInputStream(f);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			while (true) {
				textinLine = br.readLine();
				if (textinLine == null)
					break;
				if (textinLine.equals("DATATABLEMODELS"))
					break;
				String[] tokens = textinLine.split(delims);

				// System.out.println(textinLine);
				comp = tokens[0];
				value = tokens[1];
				JComponent component = (JComponent) swix.find(comp);

				// System.out.println(component.getName());

				if (component instanceof JCheckBox || component instanceof JRadioButton) {
					if (value.toLowerCase().equals("true")) {
						val = true;
					} else if (value.toLowerCase().equals("false")) {
						val = false;
					}
					((AbstractButton) component).setSelected(val);
				} else if (component instanceof JSpinner) {
					JSpinner spn = (JSpinner) component;
					if (value.matches("((-|\\+)?[0-9])+")) {
						val1 = Integer.parseInt(value);
						spn.setValue(val1);
					} else {
						spn.setValue(value);
					}

				} else {
					if (component != null) {
						((JTextComponent) component).setText(value);
						// System.out.println(comp +": " + value);
					}
				}

			}

			// Read in tablemodel data
			while (true) {
				textinLine = br.readLine();

				if (textinLine == null)
					break;
				if (textinLine.equals("END DATATABLEMODELS"))
					break;

				String[] tokens = textinLine.split(delims);
				String cID = tokens[0];
				String cName = gl.CtrlFortableID(cID);
				String fileName = gl.tableNameForCtrl(cName);
				if (fileName != null) {

					String[] files = fileName.split("[|]");
					int size = files.length;

					if (size == 1) {
						// CASE 1: 1 file specified
						fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + fileName + ".table";
						File fn = new File(fileName);
						Boolean exists = fn.exists();
					} else if (size == 2) {
						// CASE 2: 2 files specified
						fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[0] + ".table";
						File fn = new File(fileName);
						Boolean exists = fn.exists();
						if (exists) {
							fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[1] + ".table";
							fn = new File(fileName);
							exists = fn.exists();
							fileName = System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[0] + ".table" + "|"
							        + System.getProperty("user.dir") + "\\Default\\Lookup\\" + files[1] + ".table";
						}
					}

				}

				int tID = Integer.parseInt(cID);

				if (dTableModels == null) {
					dTableModels = new DataFileTableModel[20];
				}
				if (dTableModels[tID] == null) {
					dTableModels[tID] = new DataFileTableModel(fileName, tID);
				}

				dTableModels[tID].setVectors(tokens[1]);

				// JTable table = (JTable) swix.find("tblRegValues");
				// table.setModel(dTableModels[tID]);
				// table.createDefaultColumnsFromModel();

			}

			// Read in user defined flags data
			textinLine = br.readLine();

			while (true) {
				textinLine = br.readLine();
				if (textinLine == null)
					break;
				if (textinLine.equals("END USERDEFINEDFLAGS"))
					break;

				String[] tokens = textinLine.split(delims);
				int tID = Integer.parseInt(tokens[0]);
				value = tokens[1];
				if (value.startsWith("true")) {
					RegUserEdits[tID] = true;
				} else {
					RegUserEdits[tID] = false;
				}
			}

			// fs.close();
			// in.close();
			// br.close();

		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		return RegUserEdits;

	}

	/**
	 * Returns ArrayList containing lines from a text file such as GUI_Links2.table
	 * 
	 * @param filename
	 *            Name of file to read
	 * @return ArrayList of strings - one per line
	 */
	public static ArrayList<String> getGUILinks(String filename) {
		ArrayList<String> GUILinks = new ArrayList<String>();

		Scanner input;
		try {
			input = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
			return null;
		}

		int lineCount = 0;
		// int rowid = 0;
		// int colid = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lineCount++;
			if (lineCount > 1) {
				StringTokenizer st1 = new StringTokenizer(line, "\t| ");
				if (st1.countTokens() > 0) {
					GUILinks.add(line);
				}
			}
		}

		input.close();

		return GUILinks;
	}

	public static String getControls(Component component, String str) {
		// System.out.println(component.getName());
		String comp = "";
		String type = "";
		String value = "";
		Boolean val;

		final String NL = System.getProperty("line.separator");

		if (component instanceof JTextField || component instanceof NumericTextField || component instanceof JTextArea) {
			comp = component.getName();
			type = "Text";
			value = ((JTextComponent) component).getText();
		} else if (component instanceof JSpinner) {
			comp = component.getName();
			type = "Spinner";
			value = ((JSpinner) component).getValue().toString();
		} else if (component instanceof JCheckBox) {
			comp = component.getName();
			type = "Checkbox";
			val = ((AbstractButton) component).isSelected();
			value = val.toString();
		} else if (component instanceof JRadioButton) {
			comp = component.getName();
			type = "Radiobutton";
			val = ((AbstractButton) component).isSelected();
			value = val.toString();
		}
		if (comp != "") {
			str = str + (comp + "|" + type + "|" + value + NL);
		}

		if (component instanceof JSpinner) {

		} else {
			for (Component child : ((Container) component).getComponents()) {
				getControls(child, str);
			}
		}
		return str;
	}

	public static ArrayList getGUITables(ArrayList arr, String board) {
		String cName = "";
		String line = "";
		String switchID = "", TID = "", datatable = "";
		Boolean val;
		String board1 = "";
		int index;

		ArrayList arr1 = new ArrayList();

		for (int i = 0; i < arr.size(); i++) {
			line = arr.get(i).toString();
			String[] parts = line.split("[\t]+");

			// if (parts.length > 6) {
			if (parts[6].trim() != "n/a") {
				cName = parts[0].trim();
				datatable = parts[6].trim();
				switchID = parts[7].trim();
				TID = parts[8].trim();

				board1 = parts[5].trim();
				if (board1.equals(board)) {
					arr1.add(cName + "|" + datatable + "|" + switchID + "|" + TID);
				}
			}

		}
		return arr1;

	}

	public static StringBuffer getControlParents(Component component, StringBuffer sb) {
		// System.out.println(component.getName());
		Container c = (Container) component;
		String desc = "";
		String text = "";

		while (c.getParent() != null) {
			c = c.getParent();
			JComponent jc = (JComponent) c;
			Object oc = c;

			String cName = jc.getName();
			if (cName != null) {
				if (jc.getName().toString().startsWith("tabbedPane1")) {
					break;
				}
				if (jc instanceof JPanel) {
					JPanel jp = (JPanel) jc;
					TitledBorder tb = (TitledBorder) jp.getBorder();
					if (tb != null) {
						desc = tb.getTitle();
						if (desc.equals("")) {
							sb.append(cName + "|");
						} else {
							sb.append(desc + "|");
						}

					} else {
						sb.append(cName + "|");
					}
				}
			} else {
				if (jc instanceof JPanel) {
					JPanel jp = (JPanel) jc;
					TitledBorder tb = (TitledBorder) jp.getBorder();
					if (tb != null) {
						desc = tb.getTitle();
						sb.append(desc + "|");
					}

				}
			}

		}

		sb = sb.deleteCharAt(sb.length() - 1);
		return sb;

	}

	/**
	 * Calculates value for lookup to use with default wsi-di tables. Value is generally stored in the GUI textfield named
	 * hyd_DSS_Index, which is hidden from user view.
	 * 
	 * @param swix
	 *            - link to GUI/model
	 * @return - Calculated value for hyd_DSS_Index in GUI
	 */
	public static String makeHydDSSIndex(SwingEngine swix) {
		String lookup = ((JRadioButton) swix.find("run_rdbD1641")).isSelected() ? "1" : "2";
		lookup = lookup + (((JRadioButton) swix.find("hyd_rdb2005")).isSelected() ? "1" : "2");
		if (((JRadioButton) swix.find("hyd_rdbHis")).isSelected())
			lookup = lookup + "10";
		else {
			lookup = lookup + (((JRadioButton) swix.find("hyd_rdbMid")).isSelected() ? "2" : "3");
		}
		return lookup;
	}
}
