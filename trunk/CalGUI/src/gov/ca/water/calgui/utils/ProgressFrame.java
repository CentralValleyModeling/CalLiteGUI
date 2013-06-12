package gov.ca.water.calgui.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
public class ProgressFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -606008444073979623L;

	private static Logger log = Logger.getLogger(ProgressFrame.class.getName());

	private final JList list;

	public ProgressFrame(String title) {

		super();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(400, 200));
		setMinimumSize(new Dimension(400, 200));
		setLayout(new BorderLayout(5, 5));

		setTitle(title);

		String[] data = { "No scenarios active" };
		list = new JList(data);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(350, 150));
		listScroller.setMinimumSize(new Dimension(350, 150));
		add(BorderLayout.PAGE_START, listScroller);

		JButton btnClose = new JButton("Hide");
		btnClose.setPreferredSize(new Dimension(100, 25));
		btnClose.setMinimumSize(new Dimension(100, 25));
		btnClose.addActionListener(this);
		btnClose.setActionCommand("Go");
		add(BorderLayout.PAGE_END, btnClose);

		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 200) / 2);
		java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
		setAlwaysOnTop(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("Go".equals(e.getActionCommand()))
			setVisible(false);

	}

	public void setList(String[] listData) {
		list.setListData(listData);
		repaint();
	}

}
