package gov.ca.water.calgui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScenarioMonitor {

	ScenarioMonitor sm;

	@Before
	public void setUp() throws Exception {

		sm = new ScenarioMonitor();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {

		sm.start();
		Thread.sleep(2000);
		sm.add("Test");
		Thread.sleep(2000);
		System.out.println("end");
	}

}
