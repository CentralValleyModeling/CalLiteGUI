package gov.ca.water.calgui.results;

import gov.ca.water.calgui.MainMenu;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.swixml.SwingEngine;

import COM.objectspace.jgl.Container;

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
	public ControlFrame() {

		swix = MainMenu.getSwix();
		removeClose(this);

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

	private void removeClose(Component comp) {
		System.out.println(comp.toString());
		if (comp instanceof JButton) {
			String accName = ((JButton) comp).getAccessibleContext().getAccessibleName();
			System.out.println(accName);
			if (accName.equals("Close"))
				comp.getParent().remove(comp);
		}
		System.out.println(comp instanceof Container);
		if (comp instanceof Container) {
			Component[] comps = ((java.awt.Container) comp).getComponents();
			for (int x = 0, y = comps.length; x < y; x++) {
				removeClose(comps[x]);
			}
		}
	}

	/**
	 * Places frame to right of "desktop" frame if screen width permits, then makes visible
	 * 
	 */
	public void display() {

		setSize(new Dimension(460, 768));
		JFrame f = (JFrame) swix.find("desktop");
		int right = f.getWidth() + f.getLocation().x;
		if (right + getWidth() < java.awt.Toolkit.getDefaultToolkit().getScreenSize().width)
			setLocation(right, 0);
		else
			setLocation(0, 0);
		setVisible(true);
		setExtendedState(JFrame.NORMAL);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		GUIUtils.closeControlFrame();

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

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
