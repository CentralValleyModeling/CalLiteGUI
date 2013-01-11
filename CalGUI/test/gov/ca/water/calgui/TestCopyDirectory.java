package gov.ca.water.calgui;
import gov.ca.water.calgui.GUIUtils;
import static org.junit.Assert.*;

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
	
	/* JUnit 4.x class that allows efficient IO testing. Folder and subfolder auto-deleted after test.
	 * Make sure you have JUnit 4 library on your build path. 
	 */
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void createTestFiles() throws IOException {
				
		src = testFolder.newFolder("src");
		dest = testFolder.newFolder("dest");

	}

	@After
	public void tearDown() throws Exception {}

	@Test
	
	// Test recursive copy of subfolders to target directory
	public void testRecursiveCopy() {
		
		// Create src directory subfolder, then put a file in it.
		subFolder = new File(src,"subfolder");
		subFolder.mkdir();
		testFile = new File(subFolder,"test.txt");				
		
		try {
		    
			// Write something to the test file
			BufferedWriter bw = new BufferedWriter(new FileWriter(testFile)); 
			bw.write("this is a test file...");
			bw.close();

			// Run the test
			GUIUtils.copyDirectory(src, dest, true);
		} 
		
		catch (IOException e) {
			
			e.printStackTrace();
		}
		
		String destFilePath = dest.getAbsolutePath(); 
		
		assertTrue(new File(destFilePath + "\\subfolder\\test.txt").exists());

	}
		

}
