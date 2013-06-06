package gov.ca.water.calgui;

import java.util.ArrayList;

/**
 * Tracks and displays scenario status during save and run
 * 
 * @author tslawecki
 * 
 */
public class ScenarioMonitor {

	private final ArrayList<String> scenarioList = new ArrayList<String>();

	/**
	 * Starts monitoring thread
	 */
	public void start() {
	}

	/**
	 * Adds a scenario to be monitored
	 * 
	 * @param scenarioName
	 */
	public void add(String scenarioName) {
		// TODO: Check for duplicate and handle appropriately
		// TODO: Logging
		scenarioList.add(scenarioName);
	}
}
