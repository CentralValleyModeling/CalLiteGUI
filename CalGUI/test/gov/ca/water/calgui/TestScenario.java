package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.GUIUtils;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScenario {

	private MainMenu mm = null;

	@Before
	public void setUp() {

		// Give Cal-Lite a chance to initialize fully.

		System.out.println("Initializing UI.");

		try {
			mm = new MainMenu(false);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Give Cal-Lite a chance to initialize fully.
		try {

			System.out.println("Pausing for a few seconds...");

			Thread.sleep(4000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@After
	public void cleanUp() {
		// clean up scenario files and directories here
	}

	@Test
	public void testDefault() {

		// generate (not run) the scenario here

		File file = new File(System.getProperty("user.dir") + "\\Scenarios\\default.cls");

		mm.regUserEditFlags = GUIUtils.setControlValues(file, mm.swix, mm.dTableModels, mm.gl);
		mm.regFlags = GUIUtils.setControlValues(file, mm.swix, mm.gl);

		FileAction.setupScenario("default.cls", "", mm.desktop, mm.swix, mm.regUserEditFlags, mm.dTableModels, mm.gl, mm.regFlags);

		// Give Cal-Lite a chance to initialize fully.
		try {

			System.out.println("Pausing for a few more seconds...");

			Thread.sleep(4000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// now check generated files vs baselined generated files for this scenario
		File directory1 = new File("test-resources/default_scenario/Generated/Lookup");

		// fix this to the actual directory generated
		File directory2 = new File(System.getProperty("user.dir") + "\\Scenarios\\Run_Details\\default\\Generated\\Lookup");
		FileAssert.assertDirectoryAllFilesWithSameNameHaveSameContent("Default scenario comparison", directory1, directory2);

	}
}
