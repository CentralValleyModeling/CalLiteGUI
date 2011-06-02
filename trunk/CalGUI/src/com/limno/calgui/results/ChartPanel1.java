package com.limno.calgui.results;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Date;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChartPanel1 extends JPanel implements Printable {
	/**
	 * ChartPanel1 - Creates JPanel with a single ChartPanel
	 */
	private static final long serialVersionUID = 7398804723681056388L;

	public ChartPanel1(String title, String yLabel, TimeSeriesContainer[] tscs, TimeSeriesContainer[] stscs, boolean isExceed, Date lower,
			Date upper, String sLabel) {

		super();

		// create datasets ...

		double ymax = -1e20;
		double ymin = 1e20;

		JFreeChart chart;
		int primaries = 0;
		String sName = "";
		if (sLabel.equals("")) {
			if (stscs != null) {
				String[] sParts = stscs[0].fullName.split("/");
				if (sParts.length > 3)
					sName = sParts[2] + "/" + sParts[3];
				else
					sName = "Unassigned Secondary";
			}
		} else
			sName = sLabel;

		if (isExceed) {

			// Primary datasets

			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries[] series = new XYSeries[tscs.length];
			for (int i = 0; i < tscs.length; i++) {
				series[i] = new XYSeries(tscs[i].fileName);
				for (int j = 0; j < tscs[i].numberValues; j++) {
					series[i].add((double) (100.0 * j / (tscs[i].numberValues - 1)), tscs[i].values[j]);
				}
				dataset.addSeries(series[i]);
				if (ymin > tscs[i].minimumValue())
					ymin = tscs[i].minimumValue();
				if (ymax < tscs[i].maxmimumValue())
					ymax = tscs[i].maxmimumValue(); // typo in HEC DSS classes?
			}

			// Secondary datasets

			if (stscs != null) {
				XYSeries[] sseries = new XYSeries[stscs.length];
				for (int i = 0; i < stscs.length; i++) {
					if (stscs[i].numberValues > 0) {
						sseries[i] = new XYSeries(i == 0 ? sName : "");
						for (int j = 0; j < stscs[i].numberValues; j++) {
							sseries[i].add((double) (100.0 * j / (stscs[i].numberValues - 1)), stscs[i].values[j]);
						}
						dataset.addSeries(sseries[i]);
						if (ymin > stscs[i].minimumValue())
							ymin = stscs[i].minimumValue();
						if (ymax < stscs[i].maxmimumValue())
							ymax = stscs[i].maxmimumValue(); // typo in HEC DSS
																// classes?
					}
				}
			}

			chart = ChartFactory.createXYLineChart(title.replace(";", "+"), // title
					"Percent", // x-axis label
					yLabel + ((yLabel.endsWith("(TAF)") ? "" : "(" + tscs[0].units + ")")), // y-axis label
					dataset, // data
					true); // create and display a frame...

		} else {

			HecTime ht = new HecTime();
			TimeSeriesCollection dataset = new TimeSeriesCollection();

			TimeSeries[] series = new TimeSeries[tscs.length];
			for (int i = 0; i < tscs.length; i++) {
				series[i] = new TimeSeries(tscs[i].fileName);
				primaries++;
				for (int j = 0; j < tscs[i].numberValues; j++) {
					ht.set(tscs[i].times[j]);
					series[i].add(new Month(ht.month(), ht.year()), tscs[i].values[j]);
				}

				dataset.addSeries(series[i]);
				if (ymin > tscs[i].minimumValue())
					ymin = tscs[i].minimumValue();
				if (ymax < tscs[i].maxmimumValue())
					ymax = tscs[i].maxmimumValue(); // typo in HEC DSS classes?
			}

			if (stscs != null) {
				TimeSeries[] sseries = new TimeSeries[stscs.length];
				for (int i = 0; i < stscs.length; i++) {
					if (stscs[i].numberValues > 0) {
						sseries[i] = new TimeSeries(i == 0 ? sName : "");

						for (int j = 0; j < stscs[i].numberValues; j++) {
							ht.set(stscs[i].times[j]);
							sseries[i].add(new Month(ht.month(), ht.year()), stscs[i].values[j]);
						}
						dataset.addSeries(sseries[i]);
						if (ymin > stscs[i].minimumValue())
							ymin = stscs[i].minimumValue();
						if (ymax < stscs[i].maxmimumValue())
							ymax = stscs[i].maxmimumValue(); // typo in HEC DSS
																// classes?
					}
				}
			}

			chart = ChartFactory.createTimeSeriesChart(title.replace(";", "+"), // title
					"Time (1MON)", // x-axis label //TODO - Hard-coded to
									// monthly!
					yLabel + " (" + tscs[0].units + ")", // y-axis label
					dataset, // data
					true); // create and display a frame...

		}

		chart.setBackgroundPaint(Color.WHITE);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE); // White background
		plot.setDomainGridlinesVisible(false); // No gridlines
		plot.setRangeGridlinesVisible(false);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0)); // No axis offset

		if (tscs.length >= 4) { // Time series #4 in black

			XYItemRenderer r = plot.getRenderer();
			r.setSeriesPaint(3, ChartColor.BLACK);
		}

		if (stscs != null) { // Secondary time series as dashed lines
			XYItemRenderer r = plot.getRenderer();
			for (int t = 0; t < stscs.length; t++) {
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
		axis.setTickMarkInsideLength(axis.getTickMarkOutsideLength());
		if (isExceed)
			axis.setRange(0.0, 100.0);
		// else {
		// DateAxis dateAxis = (DateAxis) axis;
		// dateAxis.setRange(upper, lower);
		// }

		if ((ymax - ymin) < 0.01) {
			ymax += 0.05;
			ymin -= 0.05;
		}
		axis = plot.getRangeAxis();
		axis.setTickMarkInsideLength(axis.getTickMarkOutsideLength());
		axis.setRange(new Range(ymin - 0.05 * (ymax - ymin), ymax + 0.05 * (ymax - ymin)));

		ChartPanel p1 = new ChartPanel(chart);
		// JButton clipButton = new JButton();
		// clipButton.setText("Copy" );
		// p1.add(clipButton);

		p1.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		this.add(p1);

	}

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
			}
		}

	}

	private void Work(String title, TimeSeriesContainer[] tscs, TimeSeriesContainer[] stscs) {

	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}
}
