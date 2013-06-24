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

		String buildDate = properties.getProperty("build.date");
		String versionId = "v. " + properties.getProperty("version.id");

		Graphics2D g = splash.createGraphics();
		g.setColor(Color.BLACK);
		g.drawString(buildDate, 80, 100);
		// Further progress
		g.drawString(versionId, 80, 120);
		splash.update();

	}

}
