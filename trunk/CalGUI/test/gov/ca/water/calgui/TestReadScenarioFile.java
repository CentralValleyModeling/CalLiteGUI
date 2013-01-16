package gov.ca.water.calgui;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestReadScenarioFile {

	private File testFile;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {

		testFile = testFolder.newFile("testfile.txt");

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(testFile));
			bw.write("this is a test file...\nIt has two lines of text.");
			bw.close();

		}

		catch (IOException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void test() {

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer = GUIUtils.ReadScenarioFile(testFile);

		String myString = stringBuffer.toString();

		assertTrue(myString.contains("this is a test file"));

	}

}
