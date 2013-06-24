package gov.ca.water.calgui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class CalLiteSplash {

	private static final SplashScreen splash = SplashScreen.getSplashScreen();
	private static Properties properties = new Properties();
	private static Logger log = Logger.getLogger(CalLiteSplash.class.getName());

	public static void getSplash() {

		String buildDate = null;
		String versionId = null;

		try {

			properties.load(CalLiteSplash.class.getClassLoader().getResourceAsStream("callite-gui.properties"));
			buildDate = "Build date: " + properties.getProperty("build.date");
			versionId = "v. " + properties.getProperty("version.id");

		} catch (IOException e) {
			log.error("Problem getting splash screen metadata from properties file: ", e);
		}

		Graphics2D g = splash.createGraphics();
		g.setColor(new Color(255, 255, 204));
		g.drawString(versionId, 25, 110);
		g.drawString(buildDate, 25, 130);
		splash.update();

	}

}
