package gov.ca.water.calgui.unused;

import gov.ca.water.calgui.ProgressFrame;
import gov.ca.water.calgui.results.Report;

import java.awt.Cursor;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

public class ReportWrapper {

	private InputStream is;
	Report report = null;
	public ProgressFrame frame;
	private JFrame desktop;

	public ReportWrapper(InputStream is, final JFrame desktop) {
		this.is = is;
		this.desktop = desktop;
		desktop.setVisible(false);
		frame = new ProgressFrame("CalLite 2.0 GUI - Generating Report");
		frame.setText("Starting background thread for report generation.");
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
			@Override
			public Void doInBackground() {
				publish("Generating report.");
				report = doReport(this);
				if (report != null) {
					publish("Loading output file.");
					report.getOutputFile();
				}
				frame.setCursor(null);
				frame.dispose();
				desktop.setVisible(true);
				return null;
			}

			protected Void process(String status) {

				frame.setText(status);
				return null;
			}
		};
		worker.execute();

	}

	private Report doReport(SwingWorker<Void, String> worker) {
		try {
			report = new Report(is);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return report;
	}
}
