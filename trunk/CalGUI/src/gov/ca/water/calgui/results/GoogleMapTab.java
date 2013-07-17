package gov.ca.water.calgui.results;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.BrowserType;
import com.teamdev.jxbrowser.events.TitleChangedEvent;
import com.teamdev.jxbrowser.events.TitleListener;

public class GoogleMapTab {

	private static Logger log = Logger.getLogger(GoogleMapTab.class.getName());
	private String urlString = "http://callitewebapp.appspot.com";
	private Browser browser = BrowserFactory.createBrowser(BrowserType.Mozilla);
	private JList lstScenarios;

	public GoogleMapTab(JList lstScenarios) {

		this.lstScenarios = lstScenarios;
		this.initializeBrowser();

	}

	private void initializeBrowser() {

		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			// Object objData = urlConnect.getContent();
		} catch (UnknownHostException e) {

			this.showErrorDialog(e);
			log.debug(e.getMessage());
			return;

		} catch (Exception e) {
			this.showErrorDialog(e);
			log.debug(e.getMessage());
			return;

		}

		browser.navigate(urlString);

		browser.addTitleListener(new TitleListener() {
			@Override
			public void titleChanged(TitleChangedEvent arg0) {
				String title = browser.getTitle();

				if (title.contains(":")) {
					String[] subtitles = title.split(":");
					if (subtitles.length == 2) {
						if (!subtitles[1].startsWith("AD_") && !subtitles[1].startsWith("I_")) {

							DisplayFrame.showDisplayFrames(DisplayFrame.quickState() + ";Locs-" + subtitles[1] + ";Index-"
							        + subtitles[1], lstScenarios);
						}
					}
				}
			}
		});

	}

	public JPanel getWebTab() {

		JPanel jPanel = new JPanel();

		jPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(0, 0, 25, 75);

		browser.getComponent().setMinimumSize(new Dimension(1010, 680));
		browser.getComponent().setPreferredSize(new Dimension(1010, 680));

		jPanel.add(browser.getComponent(), c);

		return jPanel;
	}

	private void showErrorDialog(Exception e) {

		JOptionPane.showMessageDialog(null, "Couldn't find the website at " + urlString + ". " + e.getMessage(), "Warning!",
		        JOptionPane.WARNING_MESSAGE);
	}

}
