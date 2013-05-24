package gov.ca.water.calgui.utils;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

/**
 * 
 * ProgressFrame - simple frame to show progress passed by method setText();
 * 
 * @author tslawecki
 * 
 */
public class ProgressFrame extends JFrame {
	private final JLabel label;
	private static Logger log = Logger.getLogger(ProgressFrame.class.getName());

	public ProgressFrame(String title) {

		super();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setPreferredSize(new Dimension(400, 100));
		setMinimumSize(new Dimension(400, 100));
		setLayout(new BorderLayout());

		setTitle(title);
		label = new JLabel("", SwingConstants.CENTER);
		add(label);

		pack();
		setAlwaysOnTop(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 100) / 2);
		java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
		setVisible(true);

		try {
			Robot robot = new Robot();
			robot.mouseMove(dim.width / 2, dim.height / 2 + 40);
		} catch (AWTException e) {
			log.debug(e);

		}

		// TODO - Add close/cancel button

	}

	public void setText(String text) {
		label.setText(text);
		repaint();
	}
}
