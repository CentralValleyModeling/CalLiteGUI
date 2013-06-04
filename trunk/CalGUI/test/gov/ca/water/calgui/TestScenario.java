package gov.ca.water.calgui;

import gov.ca.water.calgui.utils.GUIUtils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestScenario {

	MainMenu mm = null;

	@Before
	public void setUp() {

		try {
			mm = new MainMenu(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@After
	public void cleanUp() {
		// clean up scenario files and directories here
	}

	@Test
	public void runAndCompare() {

		// generate (not run) the scenario here

		File file = new File(System.getProperty("user.dir") + "\\Scenarios\\default.cls");

		mm.regUserEditFlags = GUIUtils.setControlValues(file, mm.swix, mm.dTableModels, mm.gl);
		mm.regFlags = GUIUtils.setControlValues(file, mm.swix, mm.gl);

		FileAction.setupScenario("default.cls", mm.desktop, mm.swix, mm.regUserEditFlags, mm.dTableModels, mm.gl, mm.regFlags);

		// Give Cal-Lite a chance to initialize fully.
		try {

			System.out.println("Pausing...");

			Thread.sleep(4000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// now check generated files vs baselined generated files for this scenario
		File directory1 = new File("test-resources/default_scenario/Generated/Lookup");

		// fix this to the actual directory generated
		File directory2 = new File(System.getProperty("user.dir") + "\\Scenarios\\Run_Details\\default\\Generated\\Lookup");
		assertDirectoryAllFilesWithSameNameHaveSameContent("Default scenario comparison", directory1, directory2);

	}

	public void assertFileContentIsSame(String msg, File fileExpected, File fileActual) {
		try {
			System.out.println("Testing " + msg);

			Assert.assertEquals(FileUtils.readFileToString(fileExpected).toLowerCase(), FileUtils.readFileToString(fileActual)
			        .toLowerCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(msg + ". Failure with exception : " + ex.getMessage());
		}
	}

	public void assertDirectoryAllFilesWithSameNameHaveSameContent(String msg, File directory1, File directory2) {
		Assert.assertTrue(directory1.isDirectory() && directory2.isDirectory());
		File[] files1 = directory1.listFiles();
		File[] files2 = directory2.listFiles();
		Assert.assertEquals(
		        "Directorys have different number of files:  " + directory1.getAbsolutePath() + " vs "
		                + directory2.getAbsolutePath(), files1.length, files2.length);
		for (File file1 : files1) {
			for (File file2 : files2) {
				if (file1.getName().equals(file2.getName())) {
					assertFileContentIsSame(file1.getName(), file1, file2);
				}
			}
			// Assert.fail(msg + "No matching file : " + file1.getName() + " found in " + directory2.getAbsolutePath());
		}
	}
}
