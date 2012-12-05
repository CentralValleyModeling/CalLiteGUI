package gov.ca.water.calgui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.swixml.SwingEngine;

public class TestWriteGUIFiles extends TestCase {

	private ArrayList<String> GUILinks;
	private SwingEngine swix;

	public TestWriteGUIFiles() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Set up working directory

		File ft = new File(System.getProperty("user.dir") + "\\Run");
		// First delete existing Run directory.
		GUIUtils.deleteDir(ft);
		ft.mkdirs();
		File fs = new File(System.getProperty("user.dir") + "\\Default");
		try {
			GUIUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Copy lookup files.
		fs = new File(System.getProperty("user.dir") + "\\Default\\Lookup");
		ft = new File(System.getProperty("user.dir") + "\\Run\\Lookup");
		try {
			GUIUtils.copyDirectory(fs, ft, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Read GUI_Links2

		GUILinks = GUIUtils.GetGUILinks("Config\\GUI_Links2.table");

		// Create UI

		swix = new SwingEngine(this);
		swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		swix.render(new File(System.getProperty("user.dir") + "\\Config\\GUI.xml")).setVisible(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWrite1() {

		// Test to make sure we can write a working test!

		try {
			GUIUtils.WriteGUITables(GUILinks, null, swix);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
