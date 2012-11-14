package gov.ca.water.calgui.unused;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.text.TextAnchor;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;

public class DoAChart {

	public void main(TimeSeriesContainer tsc) {
		// create a dataset...
		HecTime ht = new HecTime();
		org.jfree.data.time.TimeSeries s1 = new org.jfree.data.time.TimeSeries("Trinity Storage");
		org.jfree.data.time.TimeSeries s2 = new org.jfree.data.time.TimeSeries("Trinity Storage Exceedance");

		// Generate time series for full dataset

		double store[] = new double[tsc.numberValues];
		for (int i = 0; i < tsc.numberValues; i++) {
			ht.set(tsc.times[i]);
			s1.add(new Month(ht.month(), ht.year()), tsc.values[i]);
			store[i] = tsc.values[i];
		}
		Arrays.sort(store);
		for (int i = 0; i < tsc.numberValues; i++) {
			ht.set(tsc.times[i]);
			s2.add(new Month(ht.month(), ht.year()), store[i]);
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("", // title
		        "", // x-axis label
		        "TAF", // y-axis label
		        dataset, // data
		        false); // create and display a frame...

		ChartPanel p1 = new ChartPanel(chart);
		p1.setPreferredSize(new Dimension(500, 350));

		XYPlot plot = chart.getXYPlot();
		ValueAxis axis = plot.getRangeAxis();
		double max = axis.getUpperBound();
		double min = axis.getLowerBound();

		// Generate monthly time series

		TimeSeries data[] = new TimeSeries[12];
		TimeSeries exceed[] = new TimeSeries[12];
		TimeSeriesCollection datasets[] = new TimeSeriesCollection[12];
		TimeSeriesCollection datasets2[] = new TimeSeriesCollection[12];
		JFreeChart charts[] = new JFreeChart[12];
		ChartPanel panels[] = new ChartPanel[12];

		for (int i = 0; i < 12; i++) {

			data[i] = new TimeSeries("");
			exceed[i] = new TimeSeries("");

			double store1[] = new double[1 + tsc.numberValues / 12];
			int k = 0;
			for (int j = 0; j < tsc.numberValues; j++) {
				ht.set(tsc.times[j]);
				if (ht.month() == i + 1) {
					data[i].add(new Year(ht.year()), tsc.values[j]);
					store1[k] = tsc.values[j];
					k++;
				}
			}

			Arrays.sort(store1);
			k = 0;
			for (int j = 0; j < tsc.numberValues; j++) {
				ht.set(tsc.times[j]);
				if (ht.month() == i + 1) {
					exceed[i].add(new Year(ht.year()), store1[k]);
					k++;
				}
			}

			datasets[i] = new TimeSeriesCollection();
			datasets[i].addSeries(data[i]);
			datasets2[i] = new TimeSeriesCollection();
			datasets2[i].addSeries(exceed[i]);

			charts[i] = ChartFactory.createTimeSeriesChart("", // title
			        "", // x-axis label
			        "", // y-axis label
			        datasets[i], // data
			        false); // create and display a frame...

			plot = charts[i].getXYPlot();
			plot.setDataset(1, datasets2[i]);

			XYAreaRenderer xyAreaRenderer = new XYAreaRenderer();
			xyAreaRenderer.setSeriesPaint(0, Color.gray);

			StandardXYItemRenderer stdXYItemRenderer = new StandardXYItemRenderer();
			stdXYItemRenderer.setSeriesPaint(0, Color.black);

			XYDotRenderer xyDotRenderer = new XYDotRenderer();
			xyDotRenderer.setSeriesPaint(0, Color.black);
			xyDotRenderer.setDotHeight(3);
			xyDotRenderer.setDotWidth(3);

			plot.setRenderer(0, xyDotRenderer);
			plot.setRenderer(1, xyAreaRenderer);

			axis = plot.getRangeAxis();
			axis.setUpperBound(max);
			axis.setLowerBound(min);
			String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

			XYTextAnnotation annotation = new XYTextAnnotation(months[i], 1930.0, max - (max - min) / 20);
			annotation.setTextAnchor(TextAnchor.TOP_LEFT);
			plot.addAnnotation(annotation);

			panels[i] = new ChartPanel(charts[i]);
			panels[i].setPreferredSize(new Dimension(250, 175));

		}

		// create a chart...

		JFrame frame = new JFrame();
		Container container = frame.getContentPane();

		// Create the layout
		GridBagLayout gbl = new GridBagLayout();

		// Set layout on container
		container.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 2;
		gbc.gridwidth = 2;
		/*
		 * gbl.setConstraints(p1, gbc); container.add(p1);
		 */

		gbc.gridheight = 1;
		gbc.gridwidth = 1;

		gbc.gridy = 0;
		for (int i = 0; i < 4; i++) {
			gbc.gridx = i;
			gbl.setConstraints(panels[i], gbc);
			container.add(panels[i]);
		}

		gbc.gridy = 1;
		for (int i = 0; i < 4; i++) {
			gbc.gridx = i;
			gbl.setConstraints(panels[i + 4], gbc);
			container.add(panels[i + 4]);
		}
		gbc.gridy = 2;
		for (int i = 0; i < 2; i++) {
			gbc.gridx = i + 2;
			gbl.setConstraints(panels[i + 8], gbc);
			container.add(panels[i + 8]);
		}
		gbc.gridy = 3;
		for (int i = 0; i < 2; i++) {
			gbc.gridx = i + 2;
			gbl.setConstraints(panels[i + 10], gbc);
			container.add(panels[i + 10]);
		}

		// Show the frame
		frame.pack();
		frame.setVisible(true);
	}
}