package gov.ca.water.calgui.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

/**
 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The clipboard data format used by the adapter is compatible
 * with the clipboard format used by Excel. This provides for clipboard interoperability between enabled JTables and Excel.
 */
public class ExcelAdapter implements ActionListener {
	private String rowstring, value;
	private Clipboard system;
	private StringSelection stsel;
	private JTable jTable1;
	private static Logger log = Logger.getLogger(ExcelAdapter.class.getName());

	/**
	 * The Excel Adapter is constructed with a JTable on which it enables Copy-Paste and acts as a Clipboard listener.
	 */
	public ExcelAdapter(JTable myJTable) {
		jTable1 = myJTable;
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		// Identifying the Paste KeyStroke user can modify this
		// to copy on some other Key combination.
		jTable1.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Public Accessor methods for the Table on which this adapter acts.
	 */
	public JTable getJTable() {
		return jTable1;
	}

	public void setJTable(JTable jTable1) {
		this.jTable1 = jTable1;
	}

	/**
	 * This method is activated on the Keystrokes we are listening to in this implementation. Here it listens for Copy and Paste
	 * ActionCommands. Selections comprising non-adjacent cells result in invalid selection and then copy action cannot be
	 * performed. Paste is done by aligning the upper left corner of the selection with the 1st element in the current selection of
	 * the JTable.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0) {
			GUIUtils.copyTableValues(jTable1);
		}
		if (e.getActionCommand().compareTo("Paste") == 0) {
			GUIUtils.pasteTableValues(jTable1);
		}
	}
}