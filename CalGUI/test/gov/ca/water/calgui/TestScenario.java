package gov.ca.water.calgui;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestScenario {

	@Before
	public void setUp() {
		// setup ability to run a scenario here
	}

	@After
	public void cleanUp() {
		// clean up scenario files and directories here
	}

	@Test
	public void runAndCompare() {
		// run the scenario here
		// now check generated files vs baselined generated files for this scenario
		File directory1 = new File("test-resources/default_scenario/Generated/Lookup");
		// fix this to the actual directory generated
		File directory2 = new File("test???");
		assertDirectoryAllFilesWithSameNameHaveSameContent("Default scenario comparison", directory1, directory2);
	}

	public void assertFileContentIsSame(String msg, File fileExpected, File fileActual) {
		try {
			Assert.assertEquals(msg + " " + fileExpected.getAbsolutePath() + " vs " + fileActual.getAbsolutePath(),
			        FileUtils.contentEquals(fileExpected, fileActual));
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
					assertFileContentIsSame(msg, file1, file2);
				}
			}
			Assert.fail(msg + "No matching file : " + file1.getName() + " found in " + directory2.getAbsolutePath());
		}
	}
}
