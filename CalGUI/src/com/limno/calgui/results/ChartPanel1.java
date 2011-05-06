package com.limno.calgui.results;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.util.Date;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.JPanel;

public class ChartPanel1 extends JPanel {
	/**
	 * ChartPanel1 - Creates JPanel with a single ChartPanel
	 */
	private static final long serialVersionUID = 7398804723681056388L;

	public ChartPanel1(String title, String yLabel, TimeSeriesContainer[] tscs, TimeSeriesContainer[] stscs, boolean isExceed,
			Date lower, Date upper) {

		super();

		// create datasets ...

		JFreeChart chart;
		int primaries = 0;
		if (isExceed) {
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries[] series = new XYSeries[tscs.length];
			for (int i = 0; i < tscs.length; i++) {
				series[i] = new XYSeries(tscs[i].fileName);
				for (int j = 0; j < tscs[i].numberValues; j++) {
					series[i].add((double) (100.0 * j / tscs[i].numberValues), tscs[i].values[j]);
				}
				dataset.addSeries(series[i]);
			}

			if (stscs != null) {
				XYSeries[] sseries = new XYSeries[stscs.length];
				for (int i = 0; i < stscs.length; i++) {
					sseries[i] = new XYSeries("");
					for (int j = 0; j < stscs[i].numberValues; j++) {
						series[i].add((double) (100.0 * j / tscs[i].numberValues), tscs[i].values[j]);
					}
					dataset.addSeries(sseries[i]);
				}
			}

			chart = ChartFactory.createXYLineChart(title.replace(";", "+"), // title
					"Percent", // x-axis label
					yLabel + " (" + tscs[0].units + ")", // y-axis label
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
			}

			if (stscs != null) {
				TimeSeries[] sseries = new TimeSeries[stscs.length];
				for (int i = 0; i < stscs.length; i++) {
					if (stscs[i].numberValues > 0) {
						sseries[i] = new TimeSeries(tscs[i].fileName);
						for (int j = 0; j < stscs[i].numberValues; j++) {
							ht.set(stscs[i].times[j]);
							sseries[i].add(new Month(ht.month(), ht.year()), stscs[i].values[j]);
						}
						dataset.addSeries(sseries[i]);
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
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		if (stscs != null) {
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
		else {
			DateAxis dateAxis = (DateAxis) axis;
			dateAxis.setRange(upper,lower);
		}
			
		axis = plot.getRangeAxis();
		axis.setTickMarkInsideLength(axis.getTickMarkOutsideLength());

		ChartPanel p1 = new ChartPanel(chart);
		// JButton clipButton = new JButton();
		// clipButton.setText("Copy" );
		// p1.add(clipButton);

		p1.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		this.add(p1);

	}

	private void Work(String title, TimeSeriesContainer[] tscs, TimeSeriesContainer[] stscs) {

	}
}
