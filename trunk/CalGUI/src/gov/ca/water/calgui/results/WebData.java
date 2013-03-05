package gov.ca.water.calgui.results;

import gov.ca.water.calgui.MainMenu;
import gov.ca.water.calgui.utils.UnitsUtils;
import hec.io.TimeSeriesContainer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;

import org.jfree.data.time.Month;
import org.swixml.SwingEngine;

import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.BrowserType;
import com.teamdev.jxbrowser.events.TitleChangedEvent;
import com.teamdev.jxbrowser.events.TitleListener;

public class WebData {
	public WebData(JTabbedPane jtp, final MainMenu mainMenu, final SwingEngine swix, final DSSGrabber dss_Grabber,
	        final JList lstScenarios, final JFrame desktop, final int displayCount) {
		final Browser browser = BrowserFactory.createBrowser(BrowserType.Mozilla);

		if (isInternetReachable()) {
			browser.navigate("http://callitewebapp.appspot.com");

			// MozillaBrowser mozillaBrowser = (MozillaBrowser) browser;
			// WebBrowser mozillaPeer = (WebBrowser) mozillaBrowser.getPeer();
			// nsIWebBrowser nsIWebBrowser = ((MozillaWebBrowser) mozillaPeer).getWebBrowser();
			// nsIDOMWindow window = nsIWebBrowser.getContentDOMWindow();
			// window.getScrollbars().setVisible(false);
			//

			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets = new Insets(0, 0, 25, 75);

			browser.getComponent().setMinimumSize(new Dimension(1010, 680));
			browser.getComponent().setPreferredSize(new Dimension(1010, 680));

			p.add(browser.getComponent(), c);

			jtp.add("Web Map", p);

			jtp.setForegroundAt(jtp.getTabCount() - 1, Color.blue);
			jtp.setBackgroundAt(jtp.getTabCount() - 1, Color.WHITE);
			browser.addTitleListener(new TitleListener() {
				@Override
				public void titleChanged(TitleChangedEvent arg0) {
					String title = browser.getTitle();
					if (title.contains(":")) {
						String[] subtitles = title.split(":");
						if (subtitles.length == 2) {
							if (!subtitles[1].startsWith("AD_") && !subtitles[1].startsWith("I_"))
								displayFrameWeb(DisplayFrame.QuickState(swix) + ";Locs-" + subtitles[1] + ";Index-" + subtitles[1],
								        swix, dss_Grabber, lstScenarios, desktop, displayCount);
						}
					}
				}
			});
		}
	}

	public static boolean isInternetReachable() {
		try {
			URL url = new URL("http://callitewebapp.appspot.com");
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			Object objData = urlConnect.getContent();
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void displayFrameWeb(String displayGroup, SwingEngine swix, DSSGrabber dss_Grabber, JList lstScenarios, JFrame desktop,
	        int displayCount) {

		// if (lstScenarios.getModel().getSize() < 1) {
		// JOptionPane.showMessageDialog(desktop,
		// "Please specify scenario(s) of interest on Quick Results tab.");
		// return;
		// }
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

		dss_Grabber.setIsCFS(isCFS);

		for (int i = 0; i < lstScenarios.getModel().getSize(); i++) {
			RBListItem item = (RBListItem) lstScenarios.getModel().getElementAt(i);
			if (item.isSelected())
				dss_Grabber.setBase(item.toString());
		}

		String namesText[] = names.split(",");
		String locationNames[] = locations.split(",");

		// String locationNames[]; // Differentiate between
		// schematicandnon-schematic view
		// boolean isSchematicView = locations.startsWith("SchVw"); // TODO -
		// Look at using currently visible panel to
		// do this check
		// if (isSchematicView)
		// locationNames = locations.substring(5).split(",");
		// else
		// locationNames = locations.split(",");
		//

		for (int i = 0; i < locationNames.length; i++) {

			dss_Grabber.setLocationWeb(locationNames[i]);
			dss_Grabber.setDateRange(dateRange);

			TimeSeriesContainer[] primary_Results = dss_Grabber.getPrimarySeries(locationNames[i]);
			TimeSeriesContainer[] secondary_Results = dss_Grabber.getSecondarySeries();

			dss_Grabber.calcTAFforCFS(primary_Results, secondary_Results);

			TimeSeriesContainer[] diff_Results = dss_Grabber.getDifferenceSeries(primary_Results);
			TimeSeriesContainer[][] exc_Results = dss_Grabber.getExceedanceSeries(primary_Results);
			TimeSeriesContainer[][] sexc_Results = dss_Grabber.getExceedanceSeries(secondary_Results);
			TimeSeriesContainer[][] dexc_Results = dss_Grabber.getExceedanceSeries2(primary_Results);

			JTabbedPane tabbedpane = new JTabbedPane();

			if (doSummaryTable) {
				SummaryTablePanel stp;
				if (doDifference)
					stp = new SummaryTablePanel(dss_Grabber.getTitle() + " - Difference from " + primary_Results[0].fileName,
					        diff_Results, null, summaryTags, "", dss_Grabber);
				else
					stp = new SummaryTablePanel(dss_Grabber.getTitle(), primary_Results, secondary_Results, summaryTags,
					        dss_Grabber.getSLabel(), dss_Grabber);
				tabbedpane.insertTab("Summary - " + dss_Grabber.getBase(), null, stp, null, 0);
			}

			if (doMonthlyTable) {
				MonthlyTablePanel mtp;
				if (doDifference) {
					mtp = new MonthlyTablePanel(dss_Grabber.getTitle() + " - Difference from " + primary_Results[0].fileName,
					        diff_Results, null, dss_Grabber, "");
				} else
					mtp = new MonthlyTablePanel(dss_Grabber.getTitle(), primary_Results, secondary_Results, dss_Grabber,
					        dss_Grabber.getSLabel());
				tabbedpane.insertTab("Monthly - " + dss_Grabber.getBase(), null, mtp, null, 0);
			}

			Date lower = new Date();
			JSpinner m = (JSpinner) swix.find("spnStartMonth");
			JSpinner y = (JSpinner) swix.find("spnStartYear");
			lower.setTime((new Month(UnitsUtils.monthToInt((String) m.getValue()), (Integer) y.getValue())).getFirstMillisecond());

			Date upper = new Date();
			m = (JSpinner) swix.find("spnEndMonth");
			y = (JSpinner) swix.find("spnEndYear");
			upper.setTime((new Month(UnitsUtils.monthToInt((String) m.getValue()), (Integer) y.getValue()).getLastMillisecond()));

			ChartPanel1 cp3;
			if (doExceedance) {
				boolean plottedOne = false; // Check if any monthly plots were
				                            // done
				for (int m1 = 0; m1 < 12; m1++)
					if (exceedMonths.contains(monthNames[m1])) {
						if (doDifference)
							cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (" + monthNames[m1] + ")"
							        + " - Difference from " + primary_Results[0].fileName, dss_Grabber.getYLabel(),
							        dexc_Results[m1], null, true, upper, lower, dss_Grabber.getSLabel());
						else
							cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (" + monthNames[m1] + ")",
							        dss_Grabber.getYLabel(), exc_Results[m1], sexc_Results == null ? null : sexc_Results[m1], true,
							        upper, lower, dss_Grabber.getSLabel());
						plottedOne = true;
						tabbedpane.insertTab("Exceedance (" + monthNames[m1] + ")", null, cp3, null, 0);
					}
				if (exceedMonths.contains("ALL") || !plottedOne) {
					if (doDifference)
						cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (all months)" + " - Difference from "
						        + primary_Results[0].fileName, dss_Grabber.getYLabel(), dexc_Results[13], null, true, upper, lower,
						        dss_Grabber.getSLabel());
					else
						cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (all months)", dss_Grabber.getYLabel(),
						        exc_Results[13], sexc_Results == null ? null : sexc_Results[13], true, upper, lower,
						        dss_Grabber.getSLabel());
					tabbedpane.insertTab("Exceedance (all)", null, cp3, null, 0);
				}
				if (exceedMonths.contains("Annual")) {
					if (dss_Grabber.getOriginalUnits().equals("CFS")) {
						if (doDifference)
							cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (annual total)" + " - Difference from "
							        + primary_Results[0].fileName, "Annual Total Volume (TAF)", dexc_Results[12], null, true,
							        upper, lower, dss_Grabber.getSLabel());
						else

							cp3 = new ChartPanel1(dss_Grabber.getTitle() + " - Exceedance (Annual Total)",
							        "Annual Total Volume (TAF)", exc_Results[12], sexc_Results == null ? null : sexc_Results[12],
							        true, upper, lower, dss_Grabber.getSLabel());
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
				if (doBase) {
					cp2 = new ChartPanel1(dss_Grabber.getTitle(), dss_Grabber.getYLabel(), primary_Results, secondary_Results,
					        false, upper, lower, dss_Grabber.getSLabel());
					tabbedpane.insertTab("Time Series", null, cp2, null, 0);

				} else if (primary_Results.length < 2) {
					JPanel panel = new JPanel();
					panel.add(new JLabel("No chart - need two or more time series."));
					tabbedpane.insertTab(doDifference ? "Difference" : "Comparison", null, panel, null, 0);
				} else {
					if (doDifference) {
						cp2 = new ChartPanel1(dss_Grabber.getTitle() + " - Difference from " + primary_Results[0].fileName,
						        dss_Grabber.getYLabel(), diff_Results, null, false, upper, lower, dss_Grabber.getSLabel());
						tabbedpane.insertTab("Difference", null, cp2, null, 0);
					} else if (doComparison) {
						cp1 = new ChartPanel1(dss_Grabber.getTitle() + " - Comparison ", dss_Grabber.getYLabel(), primary_Results,
						        secondary_Results, false, upper, lower, dss_Grabber.getSLabel());
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
			if (!(doTimeSeries || doExceedance || doMonthlyTable || doSummaryTable))
				container.add(new JLabel("Nothing to show!"));
			else
				tabbedpane.setSelectedIndex(0);
			frame.setVisible(true);
			frame.setSize(980, 700);
			frame.setLocation(displayCount * 20, displayCount * 20);
			displayCount++;

		}

		return;
	}

}
