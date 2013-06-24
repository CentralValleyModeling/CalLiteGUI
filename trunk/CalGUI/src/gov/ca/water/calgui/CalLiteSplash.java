package gov.ca.water.calgui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class CalLiteSplash {

	// Place revision/release info on splash screen (to match About screen?)

	private static final SplashScreen splash = SplashScreen.getSplashScreen();
	private static Properties properties = new Properties();
	private static Logger log = Logger.getLogger(CalLiteSplash.class.getName());

	public static void getSplash() {

		try {

			properties.load(CalLiteSplash.class.getClassLoader().getResourceAsStream("callite-gui.properties"));

		} catch (IOException e) {
			log.error("Problem in main method:", e);
		}

		String buildDate = "Build date: " + properties.getProperty("build.date");
		String versionId = "v. " + properties.getProperty("version.id");

		Graphics2D g = splash.createGraphics();
		g.setColor(new Color(255, 255, 204));
		g.drawString(versionId, 25, 110);
		g.drawString(buildDate, 25, 130);
		splash.update();

	}

}
