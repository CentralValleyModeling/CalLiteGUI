package gov.ca.water.calgui.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
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
		setPreferredSize(new Dimension(400, 220));
		setMinimumSize(new Dimension(400, 220));
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

		JButton btnClose = new JButton("Stop");
		btnClose.setPreferredSize(new Dimension(100, 25));
		btnClose.setMinimumSize(new Dimension(100, 25));
		btnClose.addActionListener(this);
		btnClose.setActionCommand("Stop");
		add(BorderLayout.PAGE_END, btnClose);

		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 200) / 2);
		java.net.URL imgURL = getClass().getResource("/images/CalLiteIcon.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));
		setAlwaysOnTop(true);
		setModal(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("Go".equals(e.getActionCommand())) {
			this.setVisible(false);
		} else if ("Stop".equals(e.getActionCommand())) {
			Runtime rt = Runtime.getRuntime();
			Process proc;
			try {
				proc = rt.exec("taskkill /f /t /fi \"WINDOWTITLE eq CalLiteRun*\" ");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
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
