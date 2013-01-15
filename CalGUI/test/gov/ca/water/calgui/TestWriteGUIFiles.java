package gov.ca.water.calgui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JRadioButton;

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

		FileAction.setupScenarioDirectory("\\Run");

		// Read GUI_Links2

		GUILinks = GUIUtils.GetGUILinks("Config\\GUI_Links2.table");

		// Create UI

		swix = new SwingEngine(this);
		swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		swix.render(new File(System.getProperty("user.dir") + "\\Config\\GUI.xml")).setVisible(false);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testWrite1() {

		// Test to make sure we can write a working test!

		try {
			FileAction.writeScenarioTables(GUILinks, null, swix);
			assert (true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("WriteGUITables eaised an exception");

		}
	}

	public void testWrite2() {

		// Test of base functionality - comparing one scenario input.

		try {
			FileAction.writeScenarioTables(GUILinks, null, swix);
			assert (true);
			File f1 = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\GUI_HydroClimate.table");
			File f2 = new File(System.getProperty("user.dir") + "\\Run\\Lookup\\GUI_HydroClimate.table");
			assertEquals(f1, f2);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("WriteGUITables raised an exception");

		}
	}

	public void testWrite3() {

		// Test of base functionality - verify that changing a GUI input propagates to scenario inputs

		// Change a RadioButton

		JRadioButton c = (JRadioButton) swix.find("hyd_rdb2005");
		c.setSelected(true);

		try {
			FileAction.writeScenarioTables(GUILinks, null, swix);
			assert (true);
			File f1 = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\GUI_HydroClimate.table");
			File f2 = new File(System.getProperty("user.dir") + "\\Run\\Lookup\\GUI_HydroClimate.table");
			assertEquals(f1, f2);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("WriteGUITables raised an exception");

		}
	}
}
