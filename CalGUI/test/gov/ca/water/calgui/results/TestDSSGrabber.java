package gov.ca.water.calgui.results;

import hec.io.TimeSeriesContainer;

import javax.swing.JList;

import junit.framework.TestCase;

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

	public void testDateRangeErrorHandling() {
		grabber.setDateRange("Apr2001-MAN2011");
	}

	public void testOrderOfCalls() {
		// grabber.setBase("base");
		TimeSeriesContainer[] primarySeries = grabber.getPrimarySeries();
		assertNotNull(primarySeries);
		assertEquals("title", grabber.getTitle());
	}
}
