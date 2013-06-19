package gov.ca.water.calgui.utils;

/**
 * Class to hold a 2-tuple for use in ScenarioMonitor class hashmap
 * 
 * @author tslawecki
 * 
 * @param <String>
 * @param <Date>
 */
public class Pair<String, Date> {
	public final String status;
	public final Date timestamp;

	public Pair(String status, Date timestamp) {
		this.status = status;
		this.timestamp = timestamp;
	}
}
