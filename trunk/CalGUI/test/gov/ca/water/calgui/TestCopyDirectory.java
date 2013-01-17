package gov.ca.water.calgui;

import static org.junit.Assert.assertTrue;
import gov.ca.water.calgui.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCopyDirectory {

	private File src;
	private File dest;
	private File subFolder;
	private File testFile;

	/*
	 * JUnit 4.x class that allows efficient IO testing. Folder and subfolder auto-deleted after test. Make sure you have JUnit 4
	 * library on your build path.
	 */

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void createTestFiles() throws IOException {

		src = testFolder.newFolder("src");
		dest = testFolder.newFolder("dest");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// Test recursive copy of subfolders and files to target directory
	// Preserves directory structure
	public void testRecursiveCopy1() {

		writeContents(true);

		try {

			// Run the test
			FileUtils.copyDirectory(src, dest, true);
		}

		catch (IOException e) {

			e.printStackTrace();
		}

		String destFilePath = dest.getAbsolutePath();

		assertTrue(new File(destFilePath + "/subfolder/test.txt").exists());

	}

	@Test
	// Test recursive copy of subfolders and files to target directory
	// Only files are copied to target directory; source subfolders are not preserved
	public void testRecursiveCopy2() {

		writeContents(true);

		try {

			// Run the test
			FileUtils.copyDirectory(src, dest, false);
		}

		catch (IOException e) {

			e.printStackTrace();
		}

		String destFilePath = dest.getAbsolutePath();

		assertTrue(new File(destFilePath + "/test.txt").exists());

	}

	@Test
	public void testShallowCopy1() {

		// Copy a file from source folder to target folder; no subfolders

		writeContents(false);

		try {

			FileUtils.copyDirectory(src, dest, false);

		}

		catch (IOException e) {

			e.printStackTrace();
		}

		assertTrue(new File(dest.getAbsolutePath() + "/test.txt").exists());

	}

	@Test
	public void testShallowCopy2() {

		// Repeat test using recursion algorithm
		// Should work. If it does, refactor to exclude boolean parameter, blow away helper method.

		writeContents(false);

		try {

			FileUtils.copyDirectory(src, dest, true);

		}

		catch (IOException e) {

			e.printStackTrace();
		}

		assertTrue(new File(dest.getAbsolutePath() + "/test.txt").exists());

	}

	private void writeContents(boolean hasSubfolders) {

		if (hasSubfolders) {

			// Create src directory subfolder, then put a file in it.
			subFolder = new File(src, "subfolder");
			subFolder.mkdir();
			testFile = new File(subFolder, "test.txt");
		}

		else {
			testFile = new File(src, "test.txt");
		}

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(testFile));
			bw.write("this is a test file...");
			bw.close();

		}

		catch (IOException e) {

			e.printStackTrace();
		}

	}

}
