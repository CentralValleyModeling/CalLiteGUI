package gov.ca.water.calgui.dashboards;

import gov.ca.water.calgui.CalLiteHelp;
import gov.ca.water.calgui.MainMenu;
import gov.ca.water.calgui.results.ControlFrame;
import gov.ca.water.calgui.utils.GUIUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.swixml.SwingEngine;

import calsim.app.Project;
import calsim.gui.DtsTreeModel;
import calsim.gui.DtsTreePanel;
import calsim.gui.GuiUtils;

public class CustomResultsAction implements ActionListener {
	private final SwingEngine SWIX;

	public CustomResultsAction(SwingEngine swix) {
		this.SWIX = swix;

	}

	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getActionCommand().equals("AC_Help")) {

			JTabbedPane jtp = (JTabbedPane) SWIX.find("tabbedPane1");
			String label = jtp.getTitleAt(jtp.getSelectedIndex());
			CalLiteHelp calLiteHelp = new CalLiteHelp();
			calLiteHelp.showHelp(label);

		} else if (actionEvent.getActionCommand().equals("AC_Controls")) {

			ControlFrame cf = GUIUtils.getControlFrame();
			cf.display();
			if (cf.getExtendedState() == JFrame.ICONIFIED)
				cf.setExtendedState(JFrame.NORMAL);

		} else if (actionEvent.getActionCommand().equals("CR_LoadList")) {

			GUIUtils.readCGR();

		} else if (actionEvent.getActionCommand().equals("CR_SaveList")) {

			GUIUtils.writeCGR();

		} else if (actionEvent.getActionCommand().equals("CR_ClearTree")) {

			Project p = MainMenu.getProject();
			p.clearMTSList();
			p.clearDTSList();

			DtsTreePanel dtp = GuiUtils.getCLGPanel().getDtsTreePanel();
			DtsTreeModel dtm = dtp.getCurrentModel();
			dtm.clearVectors();
			dtm.createTreeFromPrj(null, null, "");

			GuiUtils.getCLGPanel().repaint();
		}

	}

}
