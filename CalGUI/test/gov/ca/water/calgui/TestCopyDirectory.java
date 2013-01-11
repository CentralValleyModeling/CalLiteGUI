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
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	
	@Before
	public void createTestFiles() throws IOException {
		
		
		src = testFolder.newFolder("src");
		dest = testFolder.newFolder("dest");
		subFolder = new File(src,"subfolder");
		subFolder.mkdir();
		testFile = new File(subFolder,"test.txt");
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(testFile)); 
		bw.write("this is a test file...");
		bw.close();
	}

	@After
	public void tearDown() throws Exception {
		
	
	}

	@Test
	public void testRecursiveCopy() {
		
	
		try {
		    
			GUIUtils.copyDirectory(src, dest, true);
		} 
		
		catch (IOException e) {
			
			e.printStackTrace();
		}
		
		String destFilePath = dest.getAbsolutePath(); 
		
		assertTrue(new File(destFilePath + "\\subfolder\\test.txt").exists());

	}

}
