package gov.ca.water.calgui;
import org.apache.log4j.Logger;

import java.util.HashMap;
import org.apache.log4j.Logger;

public class UnitsUtils {
	
	private static HashMap<String, Integer> monthMap;
	static Logger log = Logger.getLogger(UnitsUtils.class.getName());
	
	public UnitsUtils()	{} // Default Constructor
	
	public static int monthToInt(String month)	{
		
		monthMap = new HashMap<String,Integer>();
		
		monthMap.put("jan",1);
		monthMap.put("feb", 2);
		monthMap.put("mar", 3);
		monthMap.put("apr", 4);
		monthMap.put("may", 5);
		monthMap.put("jun", 6);
		monthMap.put("jul", 7);
		monthMap.put("aug", 8);
		monthMap.put("sep", 9); 
		monthMap.put("oct", 10);
		monthMap.put("nov", 11);
		monthMap.put("dec", 12);
		
		month = month.toLowerCase();
		Integer monthCode = null;
		
		try {
			
			monthCode = monthMap.get(month);
			
		}
		
		catch (Exception e)	{
			
			log.debug(e.getMessage());
			
		}
		
		if (monthCode == null)    {
			
			log.debug("Invalid Key at UnitsUtls.monthToInt");
			return -1;
			
		}
		
		return monthCode.intValue();
			
	}
	
	

}
