package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.DataFileTableModel;
import gov.ca.water.calgui.utils.GUILinks;
import gov.ca.water.calgui.utils.GUIUtils;
import gov.ca.water.calgui.utils.NumericTextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JRadioButton;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.swixml.SwingEngine;

public class TestWriteGUIFiles extends TestCase {

	private ArrayList<String> GUILinks;
	private SwingEngine swix;
	JFrame desktop;
	private int[] RegFlags;

	public TestWriteGUIFiles() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		FileAction.setupScenarioDirectory("\\Run");

		// Read GUI_Links2

		GUILinks = GUIUtils.getGUILinks("Config\\GUI_Links2.table");

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
			FileAction.writeScenarioTables(System.getProperty("user.dir") + "\\Run\\Lookup", GUILinks, null, swix, RegFlags);
			assert (true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("WriteGUITables eaised an exception");

		}
	}

	public void testWrite2() {

		// Test of base functionality - checks that one particular .table file is copied into the Run\Lookup directory.

		try {
			FileAction.writeScenarioTables(System.getProperty("user.dir") + "\\Run\\Lookup", GUILinks, null, swix, RegFlags);
			assert (true);
			File f1 = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\GUI_HydroClimate.table");
			File f2 = new File(System.getProperty("user.dir") + "\\Run\\Lookup\\GUI_HydroClimate.table");

			junitx.framework.FileAssert.assertEquals(f1, f2);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("WriteScenario raised an exception");

		}
	}

	public void testWrite3() {

		// Test of base functionality - verify that changing a GUI input propagates to scenario inputs

		// Change a RadioButton

		JRadioButton c = (JRadioButton) swix.find("dem_rdbUD1");
		// c.setSelected(true);

		try {

			FileAction.writeScenarioTables(System.getProperty("user.dir") + "\\Run\\Lookup", GUILinks, null, swix, RegFlags);

			assert (true);

			File f1 = new File(System.getProperty("user.dir") + "\\Default\\Lookup\\GUI_SODdemand.table");

			File f2 = new File(System.getProperty("user.dir") + "\\Run\\Lookup\\GUI_SODdemand.table");
			boolean f1DifferentFromf2 = false;

			try {
				junitx.framework.FileAssert.assertEquals(f1, f2);
			} catch (AssertionFailedError e) {
				f1DifferentFromf2 = true;
				assertTrue(true);
			}
			if (!f1DifferentFromf2)
				fail("Test fails - Default and Run GUI_SODdemand tables should NOT be equal.");
		} catch (IOException e1) {

			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("WriteScenario raised an exception");

		}

	}

	/**
	 * Tests FileAction.setupAndRun by looking for check.text in the Run directory. This file is created immediately before the run.
	 */
	public void testSetupAndRun() {

		Boolean[] RegUserEdits = null;
		DataFileTableModel[] dTableModels = null;
		GUILinks gl = null;
		int[] RegFlags = null;

		FileAction.setupScenario("default.cls", "", desktop, swix, RegUserEdits, dTableModels, gl, RegFlags);
		FileAction.setupBatchFile("default.cls", false);
		FileAction.runBatch();
		File checkFile = new File(System.getProperty("user.dir") + "\\Run" + File.separator + "check.text");
		assertTrue(checkFile.exists());
	}
}
