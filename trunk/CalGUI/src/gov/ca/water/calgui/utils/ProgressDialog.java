package gov.ca.water.calgui.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * 
 * ProgressFrame - Dialog shows status of runs and of PDF report generation
 * 
 * @author tslawecki
 * 
 */
public class ProgressDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -606008444073979623L;

	private final JList list;
	private final JLabel label;
	private final JScrollPane listScroller;

	public ProgressDialog(String title) {

		super();

		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(400, 200));
		setMinimumSize(new Dimension(400, 200));
		setLayout(new BorderLayout(5, 5));

		setTitle(title);

		String[] data = { "No scenarios active" };
		list = new JList(data);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);

		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(350, 150));
		listScroller.setMinimumSize(new Dimension(350, 150));
		listScroller.setVisible(false);
		add(BorderLayout.PAGE_START, listScroller);

		label = new JLabel("");
		label.setVisible(false);
		add(label, BorderLayout.CENTER);

		// JButton btnClose = new JButton("Dispose");
		// btnClose.setPreferredSize(new Dimension(100, 25));
		// btnClose.setMinimumSize(new Dimension(100, 25));
		// btnClose.addActionListener(this);
		// btnClose.setActionCommand("Go");
		// add(BorderLayout.PAGE_END, btnClose);

		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 200) / 2);
		java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
		setAlwaysOnTop(false);
		setModal(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("Go".equals(e.getActionCommand()))
			this.setVisible(false);
	}

	public void setList(String[] listData) {
		if (!listScroller.isVisible()) {
			label.setVisible(false);
			listScroller.setVisible(true);
		}
		list.setListData(listData);
		repaint();
	}

	public void setText(String string) {
		if (!label.isVisible()) {
			label.setVisible(true);
			listScroller.setVisible(false);
		}
		label.setText(string);
		repaint();
	}

}
