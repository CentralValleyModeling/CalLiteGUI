package com.limno.calgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIWebBrowser;
import org.python.modules.types;

import com.jniwrapper.win32.ie.WebBrowser;
import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.BrowserType;
import com.teamdev.jxbrowser.events.StatusChangedEvent;
import com.teamdev.jxbrowser.events.StatusListener;
import com.teamdev.jxbrowser.events.TitleChangedEvent;
import com.teamdev.jxbrowser.events.TitleListener;
import com.teamdev.jxbrowser.mozilla.*;

public class WebData {
	public WebData(JTabbedPane jtp, final MainMenu mainMenu){
		final Browser browser = BrowserFactory.createBrowser(BrowserType.Mozilla);  
		
		if (isInternetReachable()){
			browser.navigate("http://callitewebapp.appspot.com");

//			MozillaBrowser mozillaBrowser = (MozillaBrowser) browser;
//			WebBrowser mozillaPeer = (WebBrowser) mozillaBrowser.getPeer();
//			nsIWebBrowser nsIWebBrowser = ((MozillaWebBrowser) mozillaPeer).getWebBrowser();
//			nsIDOMWindow window = nsIWebBrowser.getContentDOMWindow();
//			window.getScrollbars().setVisible(false);
//			
			
			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets = new Insets(0,0,25,75);

			browser.getComponent().setMinimumSize(new Dimension(1010,680));
			browser.getComponent().setPreferredSize(new Dimension(1010,680));

			p.add(browser.getComponent(),c);

			jtp.add("Web Map", p);

			
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
