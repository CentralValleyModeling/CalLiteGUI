package com.limno.calgui;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;

import com.limno.calgui.GetDSSFilename.CheckListItem;

import java.util.Iterator;

import hec.heclib.dss.*;
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
	private String units;

	private int scenarios;

	public DSS_Grabber(JList list) {

		lstScenarios = list;
	}

	public void setBase(String string) {

		baseName = string;
	}

	public void setLocation(String string) {
		String lookups[][] = {
				{ "102", "S_TRNTY/STORAGE", "", "TAF" },
				{ "103", "S_SHSTA/STORAGE;SHSTAE/STORAGE", "", "TAF" },
				{ "104", "S_FOLSM/STORAGE", "", "TAF" },
				{ "105", "S_TRNTY/STORAGE;S_SHSTA/STORAGE;S_FOLSM/STORAGE", "",
						"TAF" },
				{ "106", "S_SLCVP/STORAGE", "", "TAF" },
				{ "107", "S_OROVL/STORAGE", "", "TAF" },
				{ "108", "S_SLSWP/STORAGE", "", "TAF" },
				{ "111", "S_TRNTY/STORAGE", "", "TAF" },
				{ "112", "S_SHSTA/STORAGE;SHSTAE/STORAGE", "", "TAF" },
				{ "113", "S_FOLSM/STORAGE", "", "TAF" },
				{ "114", "S_SLCVP/STORAGE", "CPRULECV/RULECURVE", "TAF" },
				{ "115", "S_OROVL/STORAGE", "", "TAF" },
				{ "116", "S_SLSWP/STORAGE", "SWPRULECV/RULECURVE", "TAF" },
				{ "202", "C_LWSTN/FLOW-CHANNEL",
						"MIF_C_LEWISTONDV/FLOW-MIN-REQUIRED", "CFS" },
				{ "203", "D_CLEARTU/FLOW-TUNNEL", "TRINITY_IMPORTDV/ALIAS",
						"CFS" },
				{ "204", "C_WKYTN_M/FLOW", "C_WKYTN_MIF/FLOW-MIN-INSTREAM",
						"CFS" },
				{ "205", "D_SPRING/FLOW-TUNNEL", "", "CFS" },
				{ "206", "C_KSWCK/FLOW-CHANNEL",
						"EFFKESWICKMIN_DV/FLOW-MIN-REQUIRED", "" },
				{ "207", "C_REDBLF/FLOW-CHANNEL",
						"REDBLUFFMIN_DV/FLOW-MIN-REQUIRED", "" },
				{ "208", "C_WILNKS/FLOW-CHANNEL",
						"MINFLOW_C_WILKNS/FLOW-MIN-REQUIRED", "" },
				{ "209", "C_THERM/FLOW-CHANNEL",
						"THERMOLITOMIN_DV/FLOW-MIN-REQUIRED", "" },
				{ "210", "C_YUBFEA/FLOW-CHANNEL",
						"MINFLOWFEAMOUTH/FLOW-MIN-REQUIRED", "" },
				{ "211", "C_NIMBUS/FLOW-CHANNEL",
						"NIMBUS_HIST_STDV/FLOW-MIN-REQUIRED", "" },
				{ "212", "D_FREWEIR/FLOW-DELIVERY", "", "" },
				{ "213", "D_SACWEIR/FLOW-DELIVERY", "", "" },
				{ "214", "C_HOOD/FLOW-CHANNEL", "D_HOOD/FLOW-CHANNEL", "" },
				{ "215", "C_YOLOBP/FLOW-CHANNEL", "", "" },
				{ "216", "C_DXC/FLOW-CHANNEL", "", "" },
				{ "217", "C_SACRV/FLOW-CHANNEL",
						"C_SACRV_MIF/FLOW-MIN-INSTREAM", "" },
				{ "218", "C_OMR/FLOW_CHANNEL; C_INDNSL/FLOW_CHANNEL", "", "" },
				{ "219", "C_SJRVI/FLOW-CHANNEL", "", "" },
				{ "302", "INFLOW/INFLOW-DELTA", "", "" },
				{ "303", "C_SACSJR/FLOW_CHANNEL", "", "" },
				{
						"304",
						"D_DELTAREQD/FLOW_DELIVERY; DO_REQ_X2ROE_OUT/FLOW-REQ-X2ROE; DO_REQ_X2CHS_OUT/FLOW-REQ-X2CHS; DO_REQ_X2CNF_OUT/FLOW-REQ-X2CNF; DO_REQ_FLOW_OUT/FLOW-REQ-NDOI",
						"", "" },
				{
						"305",
						"EXPRATIO_/EI-RATIO-STD; D_JONES/FLOW_DELIVERY; D_BANKS/FLOW_DELIVERY; INFLOW/INFLOW-DELTA",
						"", "" },
				{ "306", "X2_PRV/X2-POSITION-PREV", "", "" },
				{ "307", "", "", "" },
				{ "310", "D_JONES/FLOW_DELIVERY", "", "" },
				{ "311", "D_BANKS/FLOW_DELIVERY", "", "" },
				{ "312", "EXPORTACTUAL/EXPORT-PRJ", "", "" },
				{ "313", "C_NBA/FLOW_CHANNEL", "", "" },
				{ "314", "D_CCWDINTK/FLOW_DELIVERY", "", "" },
				{ "315", "D_CCWDVCOR/FLOW_DELIVERY", "", "" },
				{ "316", "D_CCWDINTK/FLOW_DELIVERY; D_CCWDVCOR/FLOW_DELIVERY",
						"", "" },
				{ "402", "DEL_CVP_TOTAL_N/DELIVERY-CVP", "", "" },
				{ "403", "DEL_CVP_TOTAL_S/DELIVERY-CVP", "", "" },
				{ "404", "DEL_SWP_TOT_N/DELIVERY-SWP", "", "" },
				{ "405", "DEL_SWP_TOT_S/DELIVERY-SWP", "", "" },
				{ "406", "DEL_SWP_PIN/DELIVERY-SWP", "", "" },
				{ "409", "JP_EC_STD(-1)/SALINITY", "JP_EC_MONTH/SALINITY", "" },
				{ "410", "RS_EC_STD(-1)/SALINITY", "RS_EC_MONTH/SALINITY", "" },
				{ "411", "EM_EC_STD(-1)/SALINITY", "EM_EC_MONTH/SALINITY", "" },
				{ "412", "CO_EC_STD(-1)/SALINITY", "CO_EC_MONTH/SALINITY", "" } };

		locationName = string;
		primaryDSSName = null;
		secondaryDSSName = null;
		for (int i = 0; i < lookups.length; i++) {
			if (string.endsWith(lookups[i][0])) {
				primaryDSSName = lookups[i][1];
				secondaryDSSName = lookups[i][2];
				units = lookups[i][3];
			}
		}
	}

	private TimeSeriesContainer getOneSeries(String dssFilename, String dssName) {

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
			result = (TimeSeriesContainer) hD.get("/CALSIM/" + dssNames[0]
					+ "/01JAN1930/1MON/" + hecFPart, true);

			if (result == null) {

				JOptionPane.showMessageDialog(null, "Could not find "
						+ dssNames[0] + " in " + dssFilename, "Error",
						JOptionPane.ERROR_MESSAGE);

			} else {

				for (int i = 1; i < dssNames.length; i++) {
					TimeSeriesContainer result2 = (TimeSeriesContainer) hD.get(
							"/CALSIM/" + dssNames[i] + "/01JAN2020/1MON/"
									+ hecFPart, true);
					if (result2 == null) {
						JOptionPane.showMessageDialog(null, "Could not find "
								+ dssNames[0] + " in " + dssFilename, "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						for (int j = 0; j < result2.numberValues; j++)

							result.values[j] = result.values[j]
									+ result2.values[j];
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				String scenarioName = (String) lstScenarios.getModel()
						.getElementAt(i);
				if (!baseName.equals(scenarioName)) {
					j = j + 1;
					results[j] = getOneSeries(scenarioName, primaryDSSName);
				}
			}
			return results;
		}
	}

	public TimeSeriesContainer[] getDifferenceSeries(
			TimeSeriesContainer[] primaryResults) {
		TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios - 1];
		for (int i = 0; i < scenarios - 1; i++) {
			results[i] = (TimeSeriesContainer) primaryResults[i + 1].clone();
			for (int j = 0; j < results[i].numberValues; j++)
				results[i].values[j] = results[i].values[j]
						- primaryResults[0].values[j];
		}
		return results;
	}

	public TimeSeriesContainer[] getExceedanceSeries(
			TimeSeriesContainer[] primaryResults) {

		TimeSeriesContainer[] results = new TimeSeriesContainer[scenarios];

		for (int i = 0; i < scenarios; i++) {

			results[i] = (TimeSeriesContainer) primaryResults[i].clone();
			Double[] sortArray = new Double[results[i].numberValues];

			for (int j = 0; j < results[i].numberValues; j++) {
				sortArray[j] = results[i].values[j];
			}

			Arrays.sort(sortArray);

			for (int j = 0; j < results[i].numberValues; j++) {
				results[i].values[j] = sortArray[j];
			}
		}
		return results;
	}
}
