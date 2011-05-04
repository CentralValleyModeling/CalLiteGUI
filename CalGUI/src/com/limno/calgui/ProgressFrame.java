package com.limno.calgui;

/*
 * ProgressFrame - simple frame to show progress passed by method setText();
 */

import java.awt.*;
import javax.swing.*;

public class ProgressFrame extends JFrame {
	private JLabel label;

	ProgressFrame(String title) {

		super();

		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setPreferredSize(new Dimension(400, 100));
		setMinimumSize(new Dimension(400, 100));
		setLayout(new BorderLayout());

		setTitle(title);
		label = new JLabel();
		label.setText("");
		label.setHorizontalTextPosition(JLabel.CENTER);
		add(label,BorderLayout.CENTER);

		pack();
		setAlwaysOnTop(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 100) / 2);
		setVisible(true);

		try {
			Robot robot = new Robot();
			robot.mouseMove(dim.width / 2, dim.height / 2);
		} catch (AWTException e) {

		}

		// TODO - Add close/cancel button

	}

	public void setText(String text) {
		label.setText(text);
		repaint();
	}
}
