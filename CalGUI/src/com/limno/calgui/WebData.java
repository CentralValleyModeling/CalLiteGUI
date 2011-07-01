package com.limno.calgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JTabbedPane;

import org.python.modules.types;

import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.BrowserType;
import com.teamdev.jxbrowser.events.StatusChangedEvent;
import com.teamdev.jxbrowser.events.StatusListener;
import com.teamdev.jxbrowser.events.TitleChangedEvent;
import com.teamdev.jxbrowser.events.TitleListener;

public class WebData {
	public WebData(JTabbedPane jtp, final MainMenu mainMenu){
		final Browser browser = BrowserFactory.createBrowser(BrowserType.Mozilla);  
		if (isInternetReachable()){
			browser.navigate("http://callitewebapp.appspot.com");
			jtp.add("Web Map", browser.getComponent());
			jtp.setForegroundAt(jtp.getTabCount()-1, Color.blue);
			jtp.setBackgroundAt(jtp.getTabCount()-1, Color.WHITE);
			browser.addTitleListener(new TitleListener(){
				@Override
				public void titleChanged(TitleChangedEvent arg0) {
					String title=browser.getTitle();
					if (title.contains(":")) {
						String[] subtitles=title.split(":");
						if (subtitles.length==2){
							if (!subtitles[1].startsWith("AD_") && !subtitles[1].startsWith("I_")) mainMenu.DisplayFrameWeb(mainMenu.QuickState() + ";Locs-" + subtitles[1]+ ";Index-" + subtitles[1]);
						}
					}
				}
			});
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
