package gov.ca.water.calgui.results;

import gov.ca.water.calgui.utils.Utils;
import hec.io.TimeSeriesContainer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;

import org.jfree.data.time.Month;
import org.swixml.SwingEngine;

/**
 * DisplayFrame class provides a frame for showing charts.
 * 
 * @author tslawecki
 * 
 */
public class DisplayFrame {
	public int displayCount;

	/**
	 * 
	 * displayFrame method creates a frame showing multiple charts according to parameters.
	 * 
	 * @param displayGroup
	 * @param swix
	 * @param dss_Grabber
	 * @param lstScenarios
	 * @param desktop
	 * @param displayCount
	 */
	public static void displayFrame(String displayGroup, SwingEngine swix, JList lstScenarios, JFrame desktop, int displayCount) {

		DSSGrabber dssGrabber = new DSSGrabber(lstScenarios);
		boolean doComparison = false;
		boolean doDifference = false;
		boolean doTimeSeries = false;
		boolean doBase = false;
		boolean doExceedance = false;
		boolean isCFS = false;
		boolean doMonthlyTable = false;
		boolean doSummaryTable = false;
		String exceedMonths = "";
		String summaryTags = "";
		String names = "";
		String locations = "";
		String dateRange = "";

		String[] groupParts = displayGroup.split(";");
		String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		for (int i = 0; i < groupParts.length; i++) {
			if (groupParts[i].equals("Base"))
				doBase = true;
			if (groupParts[i].equals("Comp"))
				doComparison = true;
			else if (groupParts[i].equals("Diff"))
				doDifference = true;
			else if (groupParts[i].equals("TS"))
				doTimeSeries = true;
			else if (groupParts[i].startsWith("EX-")) {
				doExceedance = true;
				exceedMonths = groupParts[i].substring(3);
			} else if (groupParts[i].equals("CFS"))
				isCFS = true;
			else if (groupParts[i].equals("TAF"))
				isCFS = false;
			else if (groupParts[i].equals("Monthly"))
				doMonthlyTable = true;
			else if (groupParts[i].startsWith("ST-")) {
				doSummaryTable = true;
				summaryTags = groupParts[i].substring(4);
			} else if (groupParts[i].startsWith("Locs-"))
				names = groupParts[i].substring(5);
			else if (groupParts[i].startsWith("Index-"))
				locations = groupParts[i].substring(6);
			else {
				// Check to see if the groupPart parses as mmmyyyy-mmmyyy
				Pattern p = Pattern.compile("\\w\\w\\w\\d\\d\\d\\d-\\w\\w\\w\\d\\d\\d\\d");
				Matcher m = p.matcher(groupParts[i]);
				if (m.find())
					dateRange = groupParts[i];
				else
					System.out.println("Unparsed display list component - " + groupParts[i]);
			}
		}

		dssGrabber.setIsCFS(isCFS);

		for (int i = 0; i < lstScenarios.getModel().getSize(); i++) {
			RBListItem item = (RBListItem) lstScenarios.getModel().getElementAt(i);
			if (item.isSelected())
				dssGrabber.setBase(item.toString());
		}

		String locationNames[] = locations.split(",");
		String namesText[] = names.split(",");

		for (int i = 0; i < locationNames.length; i++) {

			dssGrabber.setLocation(locationNames[i]);

			if (dssGrabber.getPrimaryDSSName() == null)
				JOptionPane.showMessageDialog(desktop, "No GUI table entry found for " + namesText[i] + "/" + locationNames[i]
				        + ".");
			else if (dssGrabber.getPrimaryDSSName().equals(""))
				JOptionPane.showMessageDialog(desktop, "No DSS time series specified for " + namesText[i] + "/" + locationNames[i]
				        + ".");
			else {

				dssGrabber.setDateRange(dateRange);

				TimeSeriesContainer[] primary_Results = dssGrabber.getPrimarySeries(locationNames[i]);
				TimeSeriesContainer[] secondary_Results = dssGrabber.getSecondarySeries();

				dssGrabber.calcTAFforCFS(primary_Results, secondary_Results);

				TimeSeriesContainer[] diff_Results = dssGrabber.getDifferenceSeries(primary_Results);
				TimeSeriesContainer[][] exc_Results = dssGrabber.getExceedanceSeries(primary_Results);
				TimeSeriesContainer[][] sexc_Results = dssGrabber.getExceedanceSeries(secondary_Results);
				TimeSeriesContainer[][] dexc_Results = dssGrabber.getExceedanceSeries2(primary_Results);

				JTabbedPane tabbedpane = new JTabbedPane();

				if (doSummaryTable) {
					SummaryTablePanel stp;
					if (doDifference)
						stp = new SummaryTablePanel(dssGrabber.getTitle() + " - Difference from " + primary_Results[0].fileName,
						        diff_Results, null, summaryTags, "", dssGrabber);
					else
						stp = new SummaryTablePanel(dssGrabber.getTitle(), primary_Results, secondary_Results, summaryTags,
						        dssGrabber.getSLabel(), dssGrabber, doBase);
					tabbedpane.insertTab("Summary - " + dssGrabber.getBase(), null, stp, null, 0);
				}

				if (doMonthlyTable) {
					MonthlyTablePanel mtp;
					if (doDifference) {
						mtp = new MonthlyTablePanel(dssGrabber.getTitle() + " - Difference from " + primary_Results[0].fileName,
						        diff_Results, null, dssGrabber, "");
					} else
						mtp = new MonthlyTablePanel(dssGrabber.getTitle(), primary_Results, secondary_Results, dssGrabber,
						        dssGrabber.getSLabel(), doBase);
					tabbedpane.insertTab("Monthly - " + dssGrabber.getBase(), null, mtp, null, 0);
				}

				Date lower = new Date();
				JSpinner m = (JSpinner) swix.find("spnStartMonth");
				JSpinner y = (JSpinner) swix.find("spnStartYear");
				lower.setTime((new Month(Utils.monthToInt((String) m.getValue()), (Integer) y.getValue())).getFirstMillisecond());

				Date upper = new Date();
				m = (JSpinner) swix.find("spnEndMonth");
				y = (JSpinner) swix.find("spnEndYear");
				upper.setTime((new Month(Utils.monthToInt((String) m.getValue()), (Integer) y.getValue()).getLastMillisecond()));

				ChartPanel1 cp3;
				if (doExceedance) {
					boolean plottedOne = false; // Check if any monthly plots
					                            // were
					                            // done
					for (int m1 = 0; m1 < 12; m1++)
						if (exceedMonths.contains(monthNames[m1])) {
							if (doDifference)
								cp3 = new ChartPanel1(dssGrabber.getTitle() + " - Exceedance (" + monthNames[m1] + ")"
								        + " - Difference from " + primary_Results[0].fileName, dssGrabber.getYLabel(),
								        dexc_Results[m1], null, true, upper, lower, dssGrabber.getSLabel());
							else
								cp3 = new ChartPanel1(dssGrabber.getTitle() + " - Exceedance (" + monthNames[m1] + ")",
								        dssGrabber.getYLabel(), exc_Results[m1], sexc_Results == null ? null : sexc_Results[m1],
								        true, upper, lower, dssGrabber.getSLabel(), doBase);
							plottedOne = true;
							tabbedpane.insertTab("Exceedance (" + monthNames[m1] + ")", null, cp3, null, 0);
						}
					if (exceedMonths.contains("ALL") || !plottedOne) {
						if (doDifference)
							cp3 = new ChartPanel1(dssGrabber.getTitle() + " - Exceedance (all months)" + " - Difference from "
							        + primary_Results[0].fileName, dssGrabber.getYLabel(), dexc_Results[13], null, true, upper,
							        lower, dssGrabber.getSLabel());
						else
							cp3 = new ChartPanel1(dssGrabber.getTitle() + " - Exceedance (all months)", dssGrabber.getYLabel(),
							        exc_Results[13], sexc_Results == null ? null : sexc_Results[13], true, upper, lower,
							        dssGrabber.getSLabel(), doBase);
						tabbedpane.insertTab("Exceedance (all)", null, cp3, null, 0);
					}
					if (exceedMonths.contains("Annual")) {
						if (dssGrabber.getOriginalUnits().equals("CFS")) {
							if (doDifference)
								cp3 = new ChartPanel1(dssGrabber.getTitle() + " - Exceedance (annual total)"
								        + " - Difference from " + primary_Results[0].fileName, "Annual Total Volume (TAF)",
								        dexc_Results[12], null, true, upper, lower, dssGrabber.getSLabel());
							else

								cp3 = new ChartPanel1(dssGrabber.getTitle() + " - Exceedance (Annual Total)",
								        "Annual Total Volume (TAF)", exc_Results[12], sexc_Results == null ? null
								                : sexc_Results[12], true, upper, lower, dssGrabber.getSLabel(), doBase);
							tabbedpane.insertTab("Exceedance (annual total)", null, cp3, null, 0);
						} else {
							JPanel panel = new JPanel();
							panel.add(new JLabel("No chart - annual totals are only calculated for flows."));
							tabbedpane.insertTab("Exceedance (Annual Total)", null, panel, null, 0);
						}
					}
				}

				ChartPanel1 cp1;
				ChartPanel1 cp2;

				if (doTimeSeries) {
					if (locationNames[i].contains("SchVw") && dssGrabber.getPrimaryDSSName().contains(",")) {
						cp2 = new ChartPanel1("SchVw" + dssGrabber.getTitle(), dssGrabber.getYLabel(), primary_Results,
						        secondary_Results, false, upper, lower, dssGrabber.getPrimaryDSSName(), false); // abuse
						// slabel to
						// pass
						// individual
						// dataset
						// names
						tabbedpane.insertTab("Time Series (experimental)", null, cp2, null, 0);

					} else

					if (doBase) {
						cp2 = new ChartPanel1(dssGrabber.getTitle(), dssGrabber.getYLabel(), primary_Results, secondary_Results,
						        false, upper, lower, dssGrabber.getSLabel(), doBase);
						tabbedpane.insertTab("Time Series", null, cp2, null, 0);

					} else if (primary_Results.length < 2) {
						JPanel panel = new JPanel();
						panel.add(new JLabel("No chart - need two or more time series."));
						tabbedpane.insertTab(doDifference ? "Difference" : "Comparison", null, panel, null, 0);
					} else {
						if (doDifference) {
							cp2 = new ChartPanel1(dssGrabber.getTitle() + " - Difference from " + primary_Results[0].fileName,
							        dssGrabber.getYLabel(), diff_Results, null, false, upper, lower, dssGrabber.getSLabel());
							tabbedpane.insertTab("Difference", null, cp2, null, 0);
						} else if (doComparison) {
							cp1 = new ChartPanel1(dssGrabber.getTitle() + " - Comparison ", dssGrabber.getYLabel(),
							        primary_Results, secondary_Results, false, upper, lower, dssGrabber.getSLabel());
							tabbedpane.insertTab("Comparison", null, cp1, null, 0);
						}
					}
				}

				// Show the frame
				JFrame frame = new JFrame();

				Container container = frame.getContentPane();
				container.add(tabbedpane);

				frame.pack();
				frame.setTitle("CalLite Results - " + namesText[i]);
				// CalLite icon
				java.net.URL imgURL = DisplayFrame.class.getClass().getResource("/images/CalLiteIcon.png");
				frame.setIconImage(Toolkit.getDefaultToolkit().getImage(imgURL));

				if (!(doTimeSeries || doExceedance || doMonthlyTable || doSummaryTable))
					container.add(new JLabel("Nothing to show!"));
				else
					tabbedpane.setSelectedIndex(0);

				frame.setVisible(true);
				frame.setSize(980, 700);
				frame.setLocation(displayCount * 20, displayCount * 20);
				displayCount++;

			}
		}
		return;
	}

	public static String quickState(SwingEngine swix) {

		String cAdd;
		cAdd = "";
		// Base, Comparison and Difference
		JRadioButton rdb = (JRadioButton) swix.find("rdbp000");
		if (rdb.isSelected()) {
			cAdd = cAdd + "Base";
		}

		rdb = (JRadioButton) swix.find("rdbp001");
		if (rdb.isSelected()) {
			cAdd = cAdd + "Comp";
		}

		rdb = (JRadioButton) swix.find("rdbp002");
		if (rdb.isSelected()) {
			cAdd = cAdd + "Diff";
		}
		// Units
		rdb = (JRadioButton) swix.find("rdbCFS");
		if (rdb.isSelected()) {
			cAdd = cAdd + ";CFS";
		} else {
			cAdd = cAdd + ";TAF";
		}

		// Date
		JSpinner spnSM = (JSpinner) swix.find("spnStartMonth");
		JSpinner spnEM = (JSpinner) swix.find("spnEndMonth");
		JSpinner spnSY = (JSpinner) swix.find("spnStartYear");
		JSpinner spnEY = (JSpinner) swix.find("spnEndYear");
		String cDate = spnSM.getValue().toString() + spnSY.getValue().toString();
		cDate = cDate + "-" + spnEM.getValue().toString() + spnEY.getValue().toString();
		cAdd = cAdd + ";" + cDate;

		// Time Series
		JCheckBox ckb = (JCheckBox) swix.find("RepckbTimeSeriesPlot");
		if (ckb.isSelected()) {
			cAdd = cAdd + ";TS";
		}

		// Exceedance Plot
		JPanel controls2 = (JPanel) swix.find("controls2");
		Component[] components = controls2.getComponents();
		ckb = (JCheckBox) swix.find("RepckbExceedancePlot");
		if (ckb.isSelected()) {
			String cST;
			cST = ";EX-";
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					boolean b = c.isSelected();
					if (b == true) {
						String cName = c.getText();
						// TODO Need different naming convention.
						cST = cST + "," + cName;
					}
				}
			}
			cAdd = cAdd + cST;
		}

		// Monthly Table
		ckb = (JCheckBox) swix.find("RepckbMonthlyTable");
		if (ckb.isSelected()) {
			cAdd = cAdd + ";Monthly";
		}

		// Summary Table
		JPanel controls3 = (JPanel) swix.find("controls3");
		components = controls3.getComponents();
		ckb = (JCheckBox) swix.find("RepckbSummaryTable");
		if (ckb.isSelected()) {
			String cST;
			cST = ";ST-";
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					JCheckBox c = (JCheckBox) components[i];
					boolean b = c.isSelected();
					if (b == true) {
						String cName = c.getText();
						// TODO Need different naming convention.
						cST = cST + "," + cName;
					}
				}
			}
			cAdd = cAdd + cST;
		}

		return cAdd;
	}

}
