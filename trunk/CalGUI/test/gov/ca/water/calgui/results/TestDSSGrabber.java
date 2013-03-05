package gov.ca.water.calgui.results;

import javax.swing.JList;

import junit.framework.TestCase;

import org.junit.Test;

public class TestDSSGrabber extends TestCase {

	private DSSGrabber grabber;

	public TestDSSGrabber() {

	}

	@Override
	protected void setUp() {
		JList list = new JList(new String[] { "item1", "item2" });
		grabber = new DSSGrabber(list);
	}

	@Override
	protected void tearDown() {
	}

	@Test
	public void testBaseName() {

		// Windoze
		String testString = "c:\\temp\\blah.dss";
		grabber.setBase(testString);
		String result = grabber.getBase();
		assertTrue(result.equals("blah"));

		// Rest of the world
		testString = "c:/temp/blah.dss";
		grabber.setBase(testString);
		result = grabber.getBase();
		assertTrue(result.equals("blah"));

	}

	public void testDateRangeErrorHandling() {
		grabber.setDateRange("Apr2001-MAN2011");
	}

}
