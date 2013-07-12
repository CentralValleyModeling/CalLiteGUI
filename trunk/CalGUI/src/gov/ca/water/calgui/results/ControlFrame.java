package gov.ca.water.calgui.results;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.swixml.SwingEngine;

/**
 * Floating frame to display Quick Result scenario and display panels
 * 
 * @author tslawecki
 * 
 */
public class ControlFrame extends JFrame implements WindowListener {

	private static final long serialVersionUID = 6984886958106730868L;
	private final SwingEngine swix;

	/**
	 * Constructor moves controls from Quick Results to new frame
	 * 
	 * @param swix_in
	 *            Reference to SwingEngine containing basic GUI
	 */
	public ControlFrame(SwingEngine swix_in) {

		swix = swix_in;
		JPanel p = new JPanel(new GridBagLayout());
		add(p);

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		p.add(swix.find("ss"), c);

		c.gridy = 1;
		p.add(swix.find("Display"), c);

		p.setSize(new Dimension(450, 748));
		setSize(new Dimension(460, 768));
		setTitle("Result Display Controls");

		addWindowListener(this);
	}

	/**
	 * Places frame to right of "desktop" frame if screen width permits, then makes visible
	 * 
	 */
	public void display() {

		JFrame f = (JFrame) swix.find("desktop");
		int right = f.getWidth() + f.getLocation().x;
		if (right + getWidth() < java.awt.Toolkit.getDefaultToolkit().getScreenSize().width)
			setLocation(right, 0);

		setVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

		// Move scenario management and display panels back into GUI

		JPanel p = (JPanel) swix.find("controls");
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		p.add(swix.find("ss"), c);

		c.gridy = 1;
		p.add(swix.find("Display"), c);

		((JPanel) swix.find("Reporting")).invalidate();

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

}
