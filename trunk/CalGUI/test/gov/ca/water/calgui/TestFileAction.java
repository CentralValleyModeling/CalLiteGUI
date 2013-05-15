package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import junit.framework.TestCase;

import org.swixml.SwingEngine;

public class TestFileAction extends TestCase {

	public void testActionRun() {
		// everything compiles but data is not set correctly so I expect this to fail
		JFrame desktop = new JFrame("TestFileAction");
		SwingEngine swix = new SwingEngine();
		Boolean[] regUserEdits = new Boolean[] { Boolean.TRUE };
		int[] RegFlags = new int[] { 1 };
		String dataFilePath = "\\dataFileDir";
		int tID = 77777;
		DataFileTableModel[] dataTableModels = new DataFileTableModel[] { new DataFileTableModel(dataFilePath, tID) };
		GUILinks guiLinks = new GUILinks();
		int actionWSIDI = 0;
		FileAction action = new FileAction(desktop, swix, regUserEdits, dataTableModels, guiLinks, actionWSIDI, RegFlags);
		int id = 99999;
		action.actionPerformed(new ActionEvent(desktop, id, "AC_RUN"));
		assertTrue(true); // test passes if we get to here without an exception
	}
}
