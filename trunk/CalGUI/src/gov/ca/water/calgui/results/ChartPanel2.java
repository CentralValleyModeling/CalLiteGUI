package gov.ca.water.calgui.results;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Stroke;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import calsim.app.MultipleTimeSeries;

/**
 * 
 * Variant of ChartPanel1 specifically for plotting WRIMS GUI MUlipleTimeSeriesobjects
 * 
 * @author tslawecki
 * 
 */
public class ChartPanel2 extends JPanel implements Printable {
	/**
	 * ChartPanel1 - Creates JPanel with a single ChartPanel
	 */
	private static final long serialVersionUID = 7398804723681056388L;
	private String buffer;
	private static Logger log = Logger.getLogger(ChartPanel.class.getName());
	JButton btnScatter;

	public ChartPanel2(String title, String yLabel, TimeSeriesContainer[][] mtscs, boolean isExceed, Date lower, Date upper,
	        MultipleTimeSeries mts) {
		this(title, yLabel, mtscs, isExceed, lower, upper, false, mts);
	}

	public ChartPanel2(String title, String yLabel, TimeSeriesContainer[][] mtscs, boolean isExceed, Date lower, Date upper,
	        boolean isBase, MultipleTimeSeries mts) {

		super();

		// create datasets ...

		double ymax = -1e20;
		double ymin = 1e20;

		JFreeChart chart;

		int primaries = 0;

		TimeSeriesCollection dataset = new TimeSeriesCollection();

		for (int mtsI = 0; mtsI < mtscs.length; mtsI++) {

			TimeSeriesContainer[] tscs = mtscs[mtsI];
			TimeSeries[] series = new TimeSeries[(isBase ? 1 : mtscs.length)];
			HecTime ht = new HecTime();

			for (int i = 0; i < tscs.length; i++) {
				series[i] = new TimeSeries(mts.getVarTypeAt(mtsI).equals("DTS") ? mts.getDTSNameAt(mtsI) : mts.getBPartAt(mtsI)
				        + "/" + mts.getCPartAt(mtsI));
				primaries++;
				for (int j = 0; j < tscs[i].numberValues; j++) {
					ht.set(tscs[i].times[j]);
					series[i].addOrUpdate(new Month(ht.month(), ht.year()), tscs[i].values[j]);
					System.out.println(new Month(ht.month(), ht.year()));
				}

				dataset.addSeries(series[i]);
				if (ymin > tscs[i].minimumValue())
					ymin = tscs[i].minimumValue();
				if (ymax < tscs[i].maxmimumValue())
					ymax = tscs[i].maxmimumValue(); // typo in HEC DSS classes?

			}

		}

		chart = ChartFactory.createTimeSeriesChart(title.replace(";", "+"), // title
		        "Time (1MON)", // x-axis label //TODO - Hard-coded to
		                       // monthly!
		        yLabel + " (" + mtscs[0][0].units + ")", // y-axis label
		        dataset, // data
		        true); // create and display a frame...

		setChartOptions(chart, null, isExceed, isBase, ymax, ymin, primaries);
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axis = plot.getDomainAxis();

		final ChartPanel p1 = new ChartPanel(chart);

		// Copy title, all data series to clipboard

		JPopupMenu popupmenu = p1.getPopupMenu();
		JMenuItem item0 = popupmenu.add("Reset Axes");
		item0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// JMenuItem mi = (JMenuItem) e.getSource();
				// JPopupMenu pm = (JPopupMenu) mi.getParent();
				// ChartPanel cp = (ChartPanel) pm.getParent();
				// cp.restoreAutoBounds();
				p1.restoreAutoBounds();
			}
		});
		JMenuItem item = popupmenu.add("Copy Data");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (buffer == null)
					return;
				StringSelection clipString = new StringSelection(buffer);
				getToolkit().getSystemClipboard().setContents(clipString, clipString);
			}
		});

		// Finish up window

		p1.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(p1);

		if (plot instanceof CombinedDomainXYPlot)
			;
		else {
			// Put data in buffer for clipboard

			buffer = title + "\n";

			// Dataset titles

			XYDataset dataset1 = plot.getDataset();
			for (int i = 0; i < dataset1.getSeriesCount(); i++)
				buffer = buffer + dataset1.getSeriesKey(i).toString() + "\t\t\t";

			buffer = buffer + "\n";

			for (int i = 0; i < dataset1.getSeriesCount(); i++)
				buffer = buffer + (isExceed ? "%" : "Date") + "\t" + axis.getLabel() + "\t\t";

			buffer = buffer + "\n";

			// Data
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			for (int j = 0; j < dataset1.getItemCount(0); j++) {

				for (int i = 0; i < dataset1.getSeriesCount(); i++)
					if (j < dataset1.getItemCount(i)) {
						if (isExceed)

							buffer = buffer + dataset1.getXValue(i, j) + "\t" + dataset1.getYValue(i, j) + "\t\t";
						else {
							buffer = buffer + df.format(new Date((long) dataset1.getXValue(i, j))) + "\t"
							        + dataset1.getYValue(i, j) + "\t\t";
						}
					} else
						buffer = buffer + "\t\t\t";

				buffer = buffer + "\n";
			}
		}
	}

	/**
	 * Sets some common chart options
	 * 
	 * @param chart
	 * @param stscs
	 * @param isExceed
	 * @param isBase
	 * @param ymax
	 * @param ymin
	 * @param primaries
	 */
	private void setChartOptions(JFreeChart chart, TimeSeriesContainer[] stscs, boolean isExceed, boolean isBase, Double ymax,
	        Double ymin, Integer primaries) {

		chart.setBackgroundPaint(Color.WHITE);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE); // White background
		plot.setDomainGridlinesVisible(false); // No gridlines
		plot.setRangeGridlinesVisible(false);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0)); // No axis offset

		XYItemRenderer r = plot.getRenderer();

		if (plot instanceof CombinedDomainXYPlot)
			;
		else if (plot.getDataset(0).getSeriesCount() >= 4) // Fourth series assumed yellow, switched to black
			r.setSeriesPaint(3, ChartColor.BLACK);

		if (stscs != null) { // Secondary time series as dashed lines
			for (int t = 0; t < (isBase ? 1 : stscs.length); t++) {
				Stroke stroke = new BasicStroke(1.0f, // Width
				        BasicStroke.CAP_SQUARE, // End cap
				        BasicStroke.JOIN_MITER, // Join style
				        10.0f, // Miter limit
				        new float[] { 2.0f, 2.0f }, // Dash pattern
				        0.0f); // Dash phase
				r.setSeriesStroke(primaries + t, stroke);
			}
		}

		ValueAxis axis = plot.getDomainAxis();
		if (isExceed)
			axis.setInverted(true);

		axis.setTickMarkInsideLength(axis.getTickMarkOutsideLength());
		if (isExceed)
			axis.setRange(0.0, 100.0);

		if ((ymax - ymin) < 0.01) {
			ymax += 0.05;
			ymin -= 0.05;
		}
		if (plot instanceof CombinedDomainXYPlot)
			;
		else {
			axis = plot.getRangeAxis();
			axis.setTickMarkInsideLength(axis.getTickMarkOutsideLength());
			axis.setRange(new Range(ymin - 0.05 * (ymax - ymin), ymax + 0.05 * (ymax - ymin)));
		}
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		return;
	}

	/**
	 * Prints chart
	 */
	public void createChartPrintJob() {

		PrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
		set.add(OrientationRequested.PORTRAIT);
		set.add(new Copies(1));

		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(this);
		if (job.printDialog(set)) {
			try {
				job.print(set);
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(this, e);
				log.debug(e.getMessage());
			}
		}

	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}
}
