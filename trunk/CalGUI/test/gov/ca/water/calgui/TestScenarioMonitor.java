package gov.ca.water.calgui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScenarioMonitor {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {

		ScenarioMonitor.start();
		Thread.sleep(2000);
		ScenarioMonitor.add("Test");
		Thread.sleep(2000);
		System.out.println("end");
	}

}
