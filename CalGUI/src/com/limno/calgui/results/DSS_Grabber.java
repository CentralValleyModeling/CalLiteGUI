package com.limno.calgui.results;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;

import com.limno.calgui.GetDSSFilename.CheckListItem;
import java.util.Iterator;

import hec.heclib.dss.*;
import hec.heclib.util.HecTime;
import hec.io.*;

public class DSS_Grabber {
	/*
	 * Grabs DSS time series for a particular set of scenarios
	 * 
	 * Scenarios are passed in a JList
	 * 
	 * Base scenario is specified
	 * 
	 * Location is specified
	 * 
	 * Retrieval includes:
	 * 
	 * - Main time series (result, including necessary math) for each scenario
	 * 
	 * - Secondary series (control) where indicated for each scenario
	 * 
	 * - Difference for main time series for each scenario
	 * 
	 * - Exceedance for main time series for each scenario
	 */

	private JList lstScenarios;
	private String baseName;
	private String locationName;
	public String primaryDSSName;
	private String secondaryDSSName;
	private String title;
	private String yLabel;
	private boolean isCFS;

	private int scenarios;

	public DSS_Grabber(JList list) {

		lstScenarios = list;
	}

	public void setIsCFS(boolean isCFS) {
		this.isCFS = isCFS;
	}

	public void setBase(String string) {

		baseName = string;
	}

	public String getBase() {

		File file = new File(baseName);
		int dot = file.getName().lastIndexOf(".");
		return file.getName().substring(0, dot);
	}

	public String getYLabel() {

		return yLabel;

	}

	public String getTitle() {
		if (title != "")
			return title;
		else {
			return primaryDSSName;
		}
	}

	public void setLocation(String string) {

		locationName = string;
		primaryDSSName = null;
		secondaryDSSName = null;
		for (int i = 0; i < com.limno.calgui.MainMenu.getLookupsLength(); i++) {
			if (string.endsWith(com.limno.calgui.MainMenu.getLookups(i, 0))) {
				primaryDSSName = com.limno.calgui.MainMenu.getLookups(i, 1);
				secondaryDSSName = com.limno.calgui.MainMenu.getLookups(i, 2);
				yLabel = com.limno.calgui.MainMenu.getLookups(i, 3);
				title = com.limno.calgui.MainMenu.getLookups(i, 4);
			}
		}
	}

	public TimeSeriesContainer getOneSeries(String dssFilename, String dssName) {

		HecDss hD;
		TimeSeriesContainer result = null;
		try {
			hD = HecDss.open(dssFilename);

			Vector<String> aList = hD.getPathnameList();
			Iterator<String> it = aList.iterator();
			String hecFPart = (String) it.next();
			hecFPart = hecFPart.substring(hecFPart.length() - 9);
			System.out.println(hecFPart);

			String[] dssNames = dssName.split(";");

			boolean doTimeShift = false;
			if (dssNames[0].endsWith("(-1)")) {
				doTimeShift = true;
				dssNames[0] = dssNames[0].substring(0, dssNames[0].length() - 4);
			}
			result = (TimeSeriesContainer) hD.get("/CALSIM/" + dssNames[0] + "/01JAN1930/1MON/" + hecFPart, true);

			// Do time shift were indicated
			if (doTimeShift) {
				for (int i = result.numberValues; i < result.numberValues - 1; i++)
					result.times[i] = result.times[i + 1];
				result.numberValues = result.numberValues - 1;
			}
			if ((result == null) || (result.numberValues < 1)) {

				JOptionPane.showMessageDialog(null, "Could not find " + dssNames[0] + " in " + dssFilename, "Error",
						JOptionPane.ERROR_MESSAGE);

			} else {
				for (int i = 1; i < dssNames.length; i++) {
					TimeSeriesContainer result2 = (TimeSeriesContainer) hD.get("/CALSIM/" + dssNames[i]
							+ "/01JAN2020/1MON/" + hecFPart, true);
					if (result2 == null) {
						JOptionPane.showMessageDialog(null, "Could not find " + dssNames[0] + " in " + dssFilename,
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						for (int j = 0; j < result2.numberValues; j++)

							result.values[j] = result.values[j] + result2.values[j];
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Convert CFS to TAFY
		if ((result.units.equals("CFS")) && !isCFS) {
			for (int j = 0; j < result.numberValues; j++)
				result.values[j] = result.values[j] * 0.723966942;
			result.units = "TAFY";
		}

		String shortFileName = new File(dssFilename).getName();
		result.fileName = shortFileName;
		return result;
	}

	public TimeSeriesContainer[] getPrimarySeries() {

		// Number of scenarios

		scenarios = lstScenarios.getModel().getSize();

		TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios];

		// Base first

		results[0] = getOneSeries(baseName, primaryDSSName);
		int j = 0;
		for (int i = 0; i < scenarios; i++) {
			String scenarioName = ((CheckListItem) lstScenarios.getModel().getElementAt(i)).toString();
			// if (scenarioName.contains("\\"))
			// scenarioName = new File(scenarioName).getName();
			if (!baseName.equals(scenarioName)) {
				j = j + 1;
				results[j] = getOneSeries(scenarioName, primaryDSSName);
			}
		}
		return results;
	}

	public TimeSeriesContainer[] getSecondarySeries() {
		if (secondaryDSSName.equals("")) {
			return null;
		} else {
			scenarios = lstScenarios.getModel().getSize();

			TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios];

			// Base first

			results[0] = getOneSeries(baseName, secondaryDSSName);
			int j = 0;
			for (int i = 0; i < scenarios; i++) {
				String scenarioName = lstScenarios.getModel().getElementAt(i).toString();
				if (!baseName.equals(scenarioName)) {
					j = j + 1;
					results[j] = getOneSeries(scenarioName, primaryDSSName);
				}
			}
			return results;
		}
	}

	public TimeSeriesContainer[] getDifferenceSeries(TimeSeriesContainer[] primaryResults) {
		TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios - 1];
		for (int i = 0; i < scenarios - 1; i++) {
			results[i] = (TimeSeriesContainer) primaryResults[i + 1].clone();
			for (int j = 0; j < results[i].numberValues; j++)
				results[i].values[j] = results[i].values[j] - primaryResults[0].values[j];
		}
		return results;
	}

	public TimeSeriesContainer[][] getExceedanceSeries(TimeSeriesContainer[] primaryResults) {

		TimeSeriesContainer[][] results = new TimeSeriesContainer[13][scenarios];

		for (int month = 0; month < 13; month++) {

			HecTime ht = new HecTime();
			for (int i = 0; i < scenarios; i++) {

				if (month == 12) {
					results[month][i] = (TimeSeriesContainer) primaryResults[i].clone();
				} else {
					int[] times = primaryResults[i].times;
					double[] values = primaryResults[i].values;
					int n = 0;
					for (int j = 0; j < times.length; j++) {
						ht.set(times[j]);
						if (ht.month() == month)
							n = n + 1;
					}
					int times2[] = new int[n];
					double values2[] = new double[n];
					n = 0;
					for (int j = 0; j < times.length; j++) {
						ht.set(times[j]);
						if (ht.month() == month) {
							times2[n] = times[j];
							values2[n] = values[j];
							n = n + 1;
						}
					}
					results[month][i] = new TimeSeriesContainer();
					results[month][i].times = times2;
					results[month][i].values = values2;
					results[month][i].numberValues = n;
					results[month][i].units = primaryResults[i].units;
					results[month][i].fileName = primaryResults[i].fileName;

				}
				if (results[month][i].values != null) {
					double[] sortArray = results[month][i].values;
					Arrays.sort(sortArray);
					results[month][i].values = sortArray;
				}
			}
		}
		return results;
	}
}
