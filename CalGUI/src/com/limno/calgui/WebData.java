package com.limno.calgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JTabbedPane;

import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.BrowserType;

public class WebData {
	public WebData(JTabbedPane jtp){
		Browser browser = BrowserFactory.createBrowser(BrowserType.Mozilla);  
		if (isInternetReachable()){
			browser.navigate("http://callitewebapp.appspot.com");
			jtp.add("Web Map", browser.getComponent());
			jtp.setForegroundAt(jtp.getTabCount()-1, Color.blue);
			jtp.setBackgroundAt(jtp.getTabCount()-1, Color.WHITE);
		}
	}
	
    public static boolean isInternetReachable() {                 
    	try {       
    		URL url = new URL("http://callitewebapp.appspot.com");     
    		HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();                
    		Object objData = urlConnect.getContent();              
    	} catch (UnknownHostException e) {              
    			return false;                
    	} catch (IOException e) {                                                       
    			return false;                
    	}                
    	return true;          
    }
}
