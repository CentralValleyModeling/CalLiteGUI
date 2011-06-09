package com.limno.calgui.results;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;

import com.limno.calgui.GetDSSFilename.RBListItem;
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

	static double cfs2TAFday = 0.001983471;

	private JList lstScenarios;
	private String baseName;
	private String locationName;
	public String primaryDSSName;
	private String secondaryDSSName;

	private String title; // Chart title
	private String yLabel; // Y-axis label
	private String sLabel; // Label for secondary time series

	private boolean isCFS; // Indicates whether "CFS" button was selected
	public String originalUnits; // Copy of original units

	private int startTime; // Start and end time from dashboard
	private int endTime;
	private int startWY;
	private int endWY; // Number of water years in time period

	private int scenarios;
	private double[][] annualTAFs;

	public DSS_Grabber(JList list) {

		lstScenarios = list;
	}

	public void setIsCFS(boolean isCFS) {
		this.isCFS = isCFS;
	}

	public void setDateRange(String dateRange) {

		// Assumes daterange specified as mmmyyyy-mmmyyyy

		HecTime ht = new HecTime();

		int m = monthToInt(dateRange.substring(0, 3));
		int y = new Integer(dateRange.substring(3, 7));
		ht.setYearMonthDay(m == 12 ? y + 1 : y, m == 12 ? 1 : m + 1, 1, 0);
		startTime = ht.value();
		startWY = (m < 10) ? y : y + 1; // Water year of startTime

		m = monthToInt(dateRange.substring(8, 11));
		y = new Integer(dateRange.substring(11, 15));
		ht.setYearMonthDay(m == 12 ? y + 1 : y, m == 12 ? 1 : m + 1, 1, 0);
		endTime = ht.value();
		endWY = ((m < 10) ? y : y + 1);
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

	public String getSLabel() {

		return sLabel;

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
				sLabel = com.limno.calgui.MainMenu.getLookups(i, 5);
			}
		}
	}

	public double getAnnualTAF(int i, int wy) {

		return annualTAFs[i][wy - startWY];
	}

	public void calcTAFforCFS(TimeSeriesContainer[] TimeSeriesResults, TimeSeriesContainer[] secondaryResults) {

		/*
		 * Procedure calculates annual volume in TAF for any CFS dataset, and replaces monthly values if TAF flag is
		 * checked.
		 * 
		 * Should only be called after getPrimarySeries and getSecondarySeries have been invoked.
		 */

		// Allocate and zero out

		int datasets = TimeSeriesResults.length;
		if (secondaryResults != null)
			datasets = datasets + secondaryResults.length;

		annualTAFs = new double[datasets][endWY - startWY + 2];

		for (int i = 0; i < datasets; i++)
			for (int j = 0; j < endWY - startWY + 1; j++)
				annualTAFs[i][j] = 0.0;

		// Calculate

		if (originalUnits.equals("CFS")) {

			HecTime ht = new HecTime();
			Calendar calendar = Calendar.getInstance();

			// Primary series

			for (int i = 0; i < TimeSeriesResults.length; i++) {
				for (int j = 0; j < TimeSeriesResults[i].numberValues; j++) {

					ht.set(TimeSeriesResults[i].times[j]);
					calendar.set(ht.year(), ht.month() - 1, 1);
					double monthlyTAF = TimeSeriesResults[i].values[j] * calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * cfs2TAFday;
					int wy = ((ht.month() < 10) ? ht.year() : ht.year() + 1) - startWY;
					if (wy >= 0)
						annualTAFs[i][wy] += monthlyTAF;
					if (!isCFS)
						TimeSeriesResults[i].values[j] = monthlyTAF;
				}
				if (!isCFS)
					TimeSeriesResults[i].units = "TAF";
			}

			if (secondaryResults != null) {

				// Secondary series

				for (int i = 0; i < secondaryResults.length; i++) {
					for (int j = 0; j < secondaryResults[i].numberValues; j++) {

						ht.set(secondaryResults[i].times[j]);
						calendar.set(ht.year(), ht.month() - 1, 1);
						double monthlyTAF = secondaryResults[i].values[j] * calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * cfs2TAFday;
						int wy = ((ht.month() < 10) ? ht.year() : ht.year() + 1) - startWY;
						annualTAFs[i + TimeSeriesResults.length][wy] += monthlyTAF;
						if (!isCFS)
							secondaryResults[i].values[j] = monthlyTAF;

					}
					if (!isCFS)
						secondaryResults[i].units = "TAF";
				}
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

			String delims = "[+]";
			String[] dssNames = dssName.split(delims);

			// Check for time shift (-1 at end of name)

			boolean doTimeShift = false;
			if (dssNames[0].endsWith("(-1)")) {
				doTimeShift = true;
				dssNames[0] = dssNames[0].substring(0, dssNames[0].length() - 4);
			}

			result = (TimeSeriesContainer) hD.get("/CALSIM/" + dssNames[0] + "/01JAN1930/1MON/" + hecFPart, true);

			if ((result == null) || (result.numberValues < 1)) {

				JOptionPane.showMessageDialog(null, "Could not find " + dssNames[0] + " in " + dssFilename, "Error", JOptionPane.ERROR_MESSAGE);

			} else {
				for (int i = 1; i < dssNames.length; i++) {
					TimeSeriesContainer result2 = (TimeSeriesContainer) hD.get("/CALSIM/" + dssNames[i] + "/01JAN2020/1MON/" + hecFPart, true);
					if (result2 == null) {
						JOptionPane.showMessageDialog(null, "Could not find " + dssNames[0] + " in " + dssFilename, "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						for (int j = 0; j < result2.numberValues; j++)

							result.values[j] = result.values[j] + result2.values[j];
					}

				}
			}

			// Trim to date range

			int first = 0;
			for (int i = 0; (i < result.numberValues) && (result.times[i] < startTime); i++)
				first = i;

			int last = result.numberValues - 1;
			for (int i = result.numberValues - 1; (i >= 0) && (result.times[i] > endTime); i--)
				last = i;

			System.out.println(result.times[first]+ " " + result.startTime);
			System.out.println(result.times[last] + " " + result.endTime);

			if (first != 0)
				for (int i = 0; i < (last - first); i++) {
					result.times[i] = result.times[i + first];
					result.values[i] = result.values[i + first];
				}
			result.numberValues = last - first + 1;

			// Do time shift were indicated
			if (doTimeShift) {
				for (int i = result.numberValues; i < result.numberValues - 1; i++)
					result.times[i] = result.times[i + 1];
				result.numberValues = result.numberValues - 1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String shortFileName = new File(dssFilename).getName();
		result.fileName = shortFileName;
		return result;
	}

	public TimeSeriesContainer[] getPrimarySeries() {

		// Store number of scenarios

		scenarios = lstScenarios.getModel().getSize();
		TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios];

		// Base first

		results[0] = getOneSeries(baseName, primaryDSSName);
		originalUnits = results[0].units;

		// Then scenarios

		int j = 0;
		for (int i = 0; i < scenarios; i++) {
			String scenarioName = ((RBListItem) lstScenarios.getModel().getElementAt(i)).toString();
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
					results[j] = getOneSeries(scenarioName, secondaryDSSName);
				}
			}
			return results;
		}
	}

	public TimeSeriesContainer[] getDifferenceSeries(TimeSeriesContainer[] TimeSeriesResults) {
		TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios - 1];
		for (int i = 0; i < scenarios - 1; i++) {

			results[i] = (TimeSeriesContainer) TimeSeriesResults[i + 1].clone();
			for (int j = 0; j < results[i].numberValues; j++)
				results[i].values[j] = results[i].values[j] - TimeSeriesResults[0].values[j];
		}
		return results;
	}

	public TimeSeriesContainer[][] getExceedanceSeries(TimeSeriesContainer[] TimeSeriesResults) {

		/*
		 * Generates exceedance time series for all values [index=0], for each month's values [1..12], and [TBI] for
		 * annual totals [Index=13]
		 */

		TimeSeriesContainer[][] results;
		if (TimeSeriesResults == null)
			results = null;
		else {
			results = new TimeSeriesContainer[14][scenarios];

			for (int month = 0; month < 14; month++) {

				HecTime ht = new HecTime();
				for (int i = 0; i < scenarios; i++) {

					if (month == 13) {
						results[month][i] = (TimeSeriesContainer) TimeSeriesResults[i].clone();
					} else {

						int n;
						int times2[];
						double values2[];

						results[month][i] = new TimeSeriesContainer();

						if (month == 12) {

							// Annual totals - grab from annualTAFs
							n = annualTAFs[i].length;
							times2 = new int[n];
							values2 = new double[n];
							for (int j = 0; j < n; j++) {
								ht.setYearMonthDay(j + startWY, 11, 1, 0);
								times2[j] = ht.value();
								values2[j] = annualTAFs[i][j];
							}

						} else {

							int[] times = TimeSeriesResults[i].times;
							double[] values = TimeSeriesResults[i].values;
							n = 0;
							for (int j = 0; j < times.length; j++) {
								ht.set(times[j]);
								if (ht.month() == month + 1)
									n = n + 1;
							}
							times2 = new int[n];
							values2 = new double[n];
							n = 0;
							for (int j = 0; j < times.length; j++) {
								ht.set(times[j]);
								if (ht.month() == month + 1) {
									times2[n] = times[j];
									values2[n] = values[j];
									n = n + 1;
								}
							}
						}
						results[month][i].times = times2;
						results[month][i].values = values2;
						results[month][i].numberValues = n;
						results[month][i].units = TimeSeriesResults[i].units;
						results[month][i].fullName = TimeSeriesResults[i].fullName;
						results[month][i].fileName = TimeSeriesResults[i].fileName;
					}
					if (results[month][i].values != null) {
						double[] sortArray = results[month][i].values;
						Arrays.sort(sortArray);
						results[month][i].values = sortArray;
					}
				}
			}
		}
		return results;
	}

	public int monthToInt(String EndMon) {
		int iEMon = 0;

		if (EndMon.equals("Apr")) {
			iEMon = 4;
		} else if (EndMon.equals("Jun")) {
			iEMon = 6;
		} else if (EndMon.equals("Sep")) {
			iEMon = 9;
		} else if (EndMon.equals("Nov")) {
			iEMon = 11;
		} else if (EndMon.equals("Feb")) {
			iEMon = 2;
		} else if (EndMon.equals("Jan")) {
			iEMon = 1;
		} else if (EndMon.equals("Mar")) {
			iEMon = 3;
		} else if (EndMon.equals("May")) {
			iEMon = 5;
		} else if (EndMon.equals("Jul")) {
			iEMon = 7;
		} else if (EndMon.equals("Aug")) {
			iEMon = 8;
		} else if (EndMon.equals("Oct")) {
			iEMon = 10;
		} else if (EndMon.equals("Dec")) {
			iEMon = 12;
		}
		return iEMon;
	}

}
