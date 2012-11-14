package com.limno.calgui.results;

public class loadPDF {

	  public static void main(String filename) throws Exception {
		  Process p = 
	      Runtime.getRuntime()
	        .exec("rundll32 url.dll,FileProtocolHandler " + System.getProperty("user.dir") + "\\" +filename);
	    p.waitFor();
	  }
	}
