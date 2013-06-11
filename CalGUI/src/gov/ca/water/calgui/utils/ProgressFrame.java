package gov.ca.water.calgui.utils;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

/**
 * 
 * ProgressFrame - simple frame to show progress passed by method setText();
 * 
 * @author tslawecki
 * 
 */
public class ProgressFrame extends JFrame {

	private static Logger log = Logger.getLogger(ProgressFrame.class.getName());

	private final JList list;

	public ProgressFrame(String title) {

		super();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setPreferredSize(new Dimension(400, 200));
		setMinimumSize(new Dimension(400, 200));
		setLayout(new BorderLayout());

		setTitle(title);

		String[] data = { "No scenarios" };
		list = new JList(data); // data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(350, 150));
		listScroller.setAlignmentX(CENTER_ALIGNMENT);
		listScroller.setAlignmentY(TOP_ALIGNMENT);
		add(listScroller);

		pack();

		setAlwaysOnTop(true);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 200) / 2);
		java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

		try {
			Robot robot = new Robot();
			robot.mouseMove(dim.width / 2, dim.height / 2 + 40);
		} catch (AWTException e) {
			log.debug(e);

		}

		// TODO - Add close/cancel button

	}

	public void setList(String[] listData) {
		list.setListData(listData);
		repaint();
	}

}
