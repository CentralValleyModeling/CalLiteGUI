package com.limno.calgui;

import java.awt.Dimension;

import javax.swing.JFrame;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

//import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.Container;

public class DoAChart2 {

	public void main(String title, TimeSeriesContainer[] tscs, TimeSeriesContainer[] stscs) {
		
		// create datasets ...
		
		
		HecTime ht = new HecTime();
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		TimeSeries[] series = new TimeSeries[tscs.length];
		for (int i=0; i < tscs.length; i++) {
			series[i] = new TimeSeries("");
			for (int j = 0; j < tscs[i].numberValues; j++) {
				ht.set(tscs[i].times[j]);
				series[i].add(new Month(ht.month(), ht.year()), tscs[i].values[j]);
			}
			dataset.addSeries(series[i]);
		}
		
		if (stscs != null) {	
			TimeSeries[] sseries = new TimeSeries[stscs.length];
			for (int i=0; i < stscs.length; i++) {
				sseries[i] = new TimeSeries("");
				for (int j = 0; j < stscs[i].numberValues; j++) {
					ht.set(stscs[i].times[j]);
					sseries[i].add(new Month(ht.month(), ht.year()), stscs[i].values[j]);
				}
				dataset.addSeries(sseries[i]);
			}
			
		}
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				title, // title
				"", // x-axis label
				"TAF", // y-axis label
				dataset, // data
				false
				); // create and display a frame...

		
		ChartPanel p1 = new ChartPanel(chart);
		p1.setPreferredSize(new Dimension(500,350));
		// create a chart...

		JFrame frame = new JFrame();
		Container container = frame.getContentPane();
		container.add(p1);
	
		
		
		// Show the frame
		frame.pack();
		frame.setVisible(true);	
		
		}
}