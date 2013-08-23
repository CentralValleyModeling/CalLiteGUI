package gov.ca.water.calgui.results;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

public class BoxPlotChartPanel extends JPanel implements Printable {
	/**
	 * ChartPanel1 - Creates JPanel with a single ChartPanel
	 */
	private static final long serialVersionUID = 7398804723681056388L;
	private String buffer;
	private static Logger log = Logger.getLogger(ChartPanel.class.getName());
	JButton btnScatter;

	public BoxPlotChartPanel(String title, String yLabel, TimeSeriesContainer[] tscs, TimeSeriesContainer[] stscs, Date lower,
	        Date upper, String sLabel, boolean isBase) {

		super();

		double ymin = 1e9;
		double ymax = -1e9;
		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		int seriesCount = isBase ? 1 : tscs.length;
		int categoryCount = 14; // 12 months, all, annual
		for (int i = 0; i < seriesCount; i++) {
			for (int j = 0; j < categoryCount; j++) {
				final List<Double> list = new ArrayList<Double>();

				if (j == 0) {
					// All data
					for (int k = 0; k < tscs[i].numberValues; k++) {
						list.add(tscs[i].values[k]);
						ymin = Math.min(ymin, tscs[i].values[k]);
						ymax = Math.max(ymax, tscs[i].values[k]);
					}
				} else if (j <= 13) {
					// Monthly
					for (int k = j - 1; k < tscs[i].numberValues; k += 12) {
						list.add(tscs[i].values[k]);
					}
				}

				else {
					// Annual
					for (int k = 0; k < tscs[i].numberValues; k += 12) {
						double sum = 0;
						for (int l = 0; l < 12; l++) {
							sum = sum + tscs[i].values[k + l];
						}
						list.add(sum / 12);
					}

				}
				dataset.add(list, tscs[i].fileName,
				        j == 0 ? "All" : j == 13 ? "Annual" : "OctNovDecJanFebMarAprMayJunJulAugSep".substring(3 * j - 3, 3 * j));
				list.clear();
			}

			final CategoryAxis xAxis = new CategoryAxis("Period");
			final NumberAxis yAxis = new NumberAxis("Value");
			yAxis.setAutoRangeIncludesZero(false);
			yAxis.setRange(ymin * 0.95, ymax * 1.05);
			final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
			renderer.setFillBox(false);

			renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
			final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

			plot.setBackgroundPaint(Color.WHITE); // White background
			plot.setDomainGridlinesVisible(false); // No gridlines
			plot.setRangeGridlinesVisible(false);
			plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0)); // No axis offset

			JFreeChart chart = new JFreeChart(plot);
			chart.setTitle(title);

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
