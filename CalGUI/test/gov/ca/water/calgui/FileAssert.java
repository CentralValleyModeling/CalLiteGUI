package gov.ca.water.calgui;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

public class FileAssert {

	public static void assertDirectoryAllFilesWithSameNameHaveSameContent(String msg, File directory1, File directory2) {
		Assert.assertTrue(directory1.isDirectory() && directory2.isDirectory());
		File[] files1 = directory1.listFiles();
		File[] files2 = directory2.listFiles();
		Assert.assertEquals(
		        "Directorys have different number of files:  " + directory1.getAbsolutePath() + " vs "
		                + directory2.getAbsolutePath(), files1.length, files2.length);
		outer: for (File file1 : files1) {
			for (File file2 : files2) {
				if (file2.getName().equals(file1.getName())) {
					if (file1.isDirectory()) {
						Assert.assertTrue(file2.isDirectory());
						assertDirectoryAllFilesWithSameNameHaveSameContent(msg + "/" + file1.getName(), file1, file2);
						continue outer;
					} else {
						Assert.assertFalse(file2.isDirectory());
						assertFileContentIsSame(msg, file1, file2);
						continue outer;
					}
				}
			}
			Assert.fail(msg + "No matching file : " + file1.getName() + " found in " + directory2.getAbsolutePath());
		}
	}

	public static void assertFileContentIsSame(String msg, File fileExpected, File fileActual) {
		try {
			Assert.assertTrue(msg + " " + fileExpected.getAbsolutePath() + " vs " + fileActual.getAbsolutePath(),
			        FileUtils.contentEquals(fileExpected, fileActual));
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(msg + ". Failure with exception : " + ex.getMessage());
		}
	}

}
