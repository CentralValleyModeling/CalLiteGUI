package gov.ca.water.calgui;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FileAssertTest {

	@Test
	public void testFileIsSame() {
		File file1 = new File("test/gov/ca/water/calgui/FileAssert.java");
		File file2 = new File("test/gov/ca/water/calgui/FileAssert.java");
		FileAssert.assertFileContentIsSame("same file compare", file1, file2);
	}

	@Test
	public void testFileIsNotSame() {
		File file1 = new File("test/gov/ca/water/calgui/FileAssert.java");
		File file2 = new File("test/gov/ca/water/calgui/FileAssertTest.java");
		try {
			FileAssert.assertFileContentIsSame("same file compare", file1, file2);
			Assert.fail("those files don't have the same content");
		} catch (AssertionError ex) {

		}
	}

	@Test
	public void testDirIsSame() {
		File file1 = new File("test");
		File file2 = new File("test");
		FileAssert.assertDirectoryAllFilesWithSameNameHaveSameContent("same dir compare", file1, file2);
	}
}
