package gov.ca.water.calgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.Timer;

public class SplashScreen extends JWindow {

	private static JProgressBar progressBar = new JProgressBar();
	private static SplashScreen splashScreen;
	private static int count = 1, TIMER_PAUSE = 25, PROGBAR_MAX = 100;
	private static Timer progressBarTimer;
	ActionListener al = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent evt) {
			progressBar.setValue(count);
			if (PROGBAR_MAX == count) {
				progressBarTimer.stop();// stop the timer
				splashScreen.dispose();// dispose of splashscreen

			}
			count++;

		}
	};

	public SplashScreen() {
		createSplash();
	}

	private void createSplash() {
		Container container = getContentPane();

		JPanel panel = new JPanel();
		panel.setBorder(new javax.swing.border.EtchedBorder());
		panel.setBackground(new Color(255, 255, 157));
		container.add(panel, BorderLayout.CENTER);

		// JLabel label = new JLabel("Hello World!");
		// label.setFont(new Font("Verdana", Font.BOLD, 14));
		// panel.add(label);

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("img/splash.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JLabel imgLabel = new JLabel("Cal-Lite version: ", new ImageIcon(image), JLabel.CENTER);
		imgLabel.setVerticalTextPosition(JLabel.CENTER);
		imgLabel.setHorizontalTextPosition(JLabel.CENTER);
		panel.add(imgLabel);

		// JLabel splashLabel = new JLabel("Blah");
		// panel.add(splashLabel);

		// progressBar.setMaximum(PROGBAR_MAX);
		// container.add(progressBar, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		startProgressBar();
	}

	private void startProgressBar() {
		progressBarTimer = new Timer(TIMER_PAUSE, al);
		progressBarTimer.start();
	}

}
