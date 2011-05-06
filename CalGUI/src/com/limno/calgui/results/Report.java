package com.limno.calgui.results;

import gov.ca.dsm2.input.parser.InputTable;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import com.limno.calgui.ProgressFrame;
import com.limno.calgui.loadPDF;

import gov.ca.dsm2.input.parser.Parser;
import gov.ca.dsm2.input.parser.Tables;

import java.awt.Cursor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import vista.set.DataReference;
import vista.set.Group;
import vista.set.RegularTimeSeries;
import vista.time.TimeFactory;
import vista.time.TimeWindow;

/**
 * Generates a report based on the template file instructions
 * 
 * @author psandhu
 * 
 */
public class Report extends SwingWorker<Void, String> {
	/**
	 * Externalizes the format for output. This allows the flexibility of
	 * defining a writer to output the report to a PDF file vs an HTML file.
	 * 
	 * @author psandhu
	 * 
	 */
	public static interface Writer {
		static final int BOLD = 100;
		static final int NORMAL = 1;

		void startDocument(String outputFile);

		void endDocument();

		void addTableTitle(String string);

		void addTableHeader(ArrayList<String> headerRow, int[] columnSpans);

		void addTableRow(List<String> rowData, int[] columnSpans, int style, boolean centered);

		void endTable();

		void addTimeSeriesPlot(ArrayList<double[]> buildDataArray, String title, String[] seriesName,
				String xAxisLabel, String yAxisLabel);

		void addExceedancePlot(ArrayList<double[]> buildDataArray, String title, String[] seriesName,
				String xAxisLabel, String yAxisLabel);

		public void setAuthor(String author);

		void addTableSubTitle(String string);
	}

	/*
	 ********** START SwingWorker additions
	 */
	
	private InputStream inputStream;
	private JFrame desktop;
	private ProgressFrame frame;

	@Override
	protected Void doInBackground() throws Exception {

		frame = new ProgressFrame("CalLite 2.0 GUI - Generating Report");
		publish("Generating report in background thread.");
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		desktop.setVisible(false);

		logger.fine("Parsing input template");
		publish("Parsing input template.");
		parseTemplateFile(inputStream);

		publish("Processing DSS files.");
		doProcessing();
		logger.fine("Done generating report");

		return null;
	}

	protected void process(List<String> status) {

		frame.setText(status.get(status.size()-1));
		return;
	}
	
	protected void done(){

		frame.setCursor(null);
		frame.dispose();

		desktop.setVisible(true);
		return;
		
	}

	public Report(InputStream inputStream, JFrame desktop) throws IOException {

		this.desktop = desktop;
		this.inputStream = inputStream;

	}
	/*
	 ********** END SwingWorker additions
	 */

	static final Logger logger = Logger.getLogger("callite.report");
	private ArrayList<ArrayList<String>> twValues;
	private ArrayList<PathnameMap> pathnameMaps;
	private HashMap<String, String> scalars;
	private Writer writer;

	public Report(String templateFile) throws IOException {
		this(new FileInputStream(templateFile));
	}

	public Report(InputStream inputStream) throws IOException {
		generateReport(inputStream);
	}

	void generateReport(InputStream templateContentStream) throws IOException {
		logger.fine("Parsing input template");
		parseTemplateFile(templateContentStream);
		doProcessing();
		logger.fine("Done generating report");
	}

	void parseTemplateFile(InputStream templateFileStream) throws IOException {
		Parser p = new Parser();
		Tables tables = p.parseModel(templateFileStream);
		// load scalars into a map
		InputTable scalarTable = tables.getTableNamed("SCALAR");
		ArrayList<ArrayList<String>> scalarValues = scalarTable.getValues();
		int nscalars = scalarValues.size();
		scalars = new HashMap<String, String>();
		for (int i = 0; i < nscalars; i++) {
			String name = scalarTable.getValue(i, "NAME");
			String value = scalarTable.getValue(i, "VALUE");
			scalars.put(name, value);
		}
		// load pathname mapping into a map
		InputTable pathnameMappingTable = tables.getTableNamed("PATHNAME_MAPPING");
		ArrayList<ArrayList<String>> pmap_values = pathnameMappingTable.getValues();
		int nvalues = pmap_values.size();
		pathnameMaps = new ArrayList<PathnameMap>();
		for (int i = 0; i < nvalues; i++) {
			String var_name = pathnameMappingTable.getValue(i, "VARIABLE");
			var_name = var_name.replace("\"", "");
			PathnameMap path_map = new PathnameMap(var_name);
			path_map.report_type = pathnameMappingTable.getValue(i, "REPORT_TYPE");
			path_map.path1 = pathnameMappingTable.getValue(i, "PATH1");
			path_map.path2 = pathnameMappingTable.getValue(i, "PATH2");
			path_map.var_category = pathnameMappingTable.getValue(i, "VAR_CATEGORY");
			path_map.row_type = pathnameMappingTable.getValue(i, "ROW_TYPE");
			if (path_map.path2 == null || path_map.path2.length() == 0) {
				path_map.path2 = path_map.path1;
			}
			pathnameMaps.add(path_map);
		}
		InputTable timeWindowTable = tables.getTableNamed("TIME_PERIODS");
		twValues = timeWindowTable.getValues();
	}

	public void doProcessing() {
		// open files 1 and file 2 and loop over to plot
		Group dss_group1 = Utils.opendss(scalars.get("FILE1"));
		Group dss_group2 = Utils.opendss(scalars.get("FILE2"));
		ArrayList<TimeWindow> timewindows = new ArrayList<TimeWindow>();
		for (ArrayList<String> values : twValues) {
			String v = values.get(1).replace("\"", "");
			timewindows.add(TimeFactory.getInstance().createTimeWindow(v));
		}
		TimeWindow tw = null;
		if (timewindows.size() > 0) {
			tw = timewindows.get(0);
		}
		String output_file = scalars.get("OUTFILE");
		writer = new ReportPDFWriter();
		writer.startDocument(output_file);
		writer.setAuthor(scalars.get("MODELER"));
		if (dss_group1 == null || dss_group2 == null) {
			logger.severe("No data available in either : " + scalars.get("FILE1") + " or " + scalars.get("FILE2"));
			return;
		}

		publish("Generating summary table."); // SwingWorker

		generateSummaryTable();
		int dataIndex = 0;
		for (PathnameMap pathMap : pathnameMaps) {
			dataIndex = dataIndex + 1;
			logger.fine("Working on index: " + dataIndex);
			publish("Generating plot " + dataIndex + " of " + pathnameMaps.size() +"."); //SwingWorker
			if (pathMap.path2 == null || pathMap.path2 == "") {
				pathMap.path2 = pathMap.path1;
			}
			boolean calculate_dts = false;
			if (pathMap.var_category.equals("HEADER")) {
				logger.fine("Inserting header");
				continue;
			}
			if (pathMap.report_type.equals("Exceedance_Post")) {
				calculate_dts = true;
			}
			DataReference ref1 = Utils.getReference(dss_group1, pathMap.path1, calculate_dts, pathnameMaps, 1);
			DataReference ref2 = Utils.getReference(dss_group2, pathMap.path2, calculate_dts, pathnameMaps, 2);
			if (ref1 == null || ref2 == null) {
				continue;
			}
			String[] series_name = new String[] { scalars.get("NAME1"), scalars.get("NAME2") };
			String data_units = Utils.getUnits(ref1, ref2);
			String data_type = Utils.getType(ref1, ref2);
			if (pathMap.report_type.equals("Average")) {
				generatePlot(Utils.buildDataArray(ref1, ref2, tw), dataIndex,
						"Average " + pathMap.var_name.replace("\"", ""), series_name, data_type + "(" + data_units
								+ ")", "Time", PlotType.TIME_SERIES);
			} else if (pathMap.report_type.equals("Exceedance")) {
				generatePlot(Utils.buildExceedanceArray(ref1, ref2, pathMap.var_category == "S_SEPT", tw), dataIndex,
						Utils.getExceedancePlotTitle(pathMap), series_name, data_type + "(" + data_units + ")",
						"Percent at or above", PlotType.EXCEEDANCE);
			} else if (pathMap.report_type.equals("Avg_Excd")) {
				generatePlot(Utils.buildDataArray(ref1, ref2, tw), dataIndex,
						"Average " + pathMap.var_name.replace("\"", ""), series_name, data_type + "(" + data_units
								+ ")", "Time", PlotType.TIME_SERIES);
				generatePlot(Utils.buildExceedanceArray(ref1, ref2, pathMap.var_category == "S_SEPT", tw), dataIndex,
						Utils.getExceedancePlotTitle(pathMap), series_name, data_type + "(" + data_units + ")",
						"Percent at or above", PlotType.EXCEEDANCE);
			} else if (pathMap.report_type.equals("Timeseries")) {
				generatePlot(Utils.buildDataArray(ref1, ref2, tw), dataIndex,
						"Average " + pathMap.var_name.replace("\"", ""), series_name, data_type + "(" + data_units
								+ ")", "Time", PlotType.TIME_SERIES);
			} else if (pathMap.report_type.equals("Exceedance_Post")) {
				generatePlot(Utils.buildExceedanceArray(ref1, ref2, true, tw), dataIndex, "Exceedance "
						+ pathMap.var_name.replace("\"", ""), series_name, data_type + "(" + data_units + ")",
						"Percent at or above", PlotType.EXCEEDANCE);
			}
		}
		writer.endDocument();

		loadPDF pdf = new loadPDF();
		try {
			publish("Loading PDF file \"" + output_file + "\".");;
			loadPDF.main(output_file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getOutputFile();

	}

	private void generateSummaryTable() {
		writer.addTableTitle(String.format("System Flow Comparision: %s vs %s", scalars.get("NAME2"),
				scalars.get("NAME1")));
		writer.addTableSubTitle(scalars.get("NOTE").replace("\"", ""));
		Group dss_group1 = Utils.opendss(scalars.get("FILE1"));
		Group dss_group2 = Utils.opendss(scalars.get("FILE2"));
		ArrayList<TimeWindow> timewindows = new ArrayList<TimeWindow>();
		for (ArrayList<String> values : twValues) {
			String v = values.get(1).replace("\"", "");
			timewindows.add(TimeFactory.getInstance().createTimeWindow(v));
		}
		ArrayList<String> headerRow = new ArrayList<String>();
		headerRow.add("");
		ArrayList<String> headerRow2 = new ArrayList<String>();
		headerRow2.add("River Flows");

		for (TimeWindow tw : timewindows) {
			headerRow.add(Utils.formatTimeWindowAsWaterYear(tw));
			headerRow2.addAll(Arrays.asList(scalars.get("NAME2"), scalars.get("NAME1"), "Diff", "% Diff"));
		}
		int[] columnSpans = new int[timewindows.size() + 1];
		columnSpans[0] = 1;
		for (int i = 1; i < columnSpans.length; i++) {
			columnSpans[i] = 4;
		}
		writer.addTableHeader(headerRow, columnSpans);
		writer.addTableHeader(headerRow2, null);
		writer.addTableHeader(new ArrayList<String>(), null);
		List<String> categoryList = Arrays.asList("RF", "DI", "DO", "DE", "SWPSOD", "CVPSOD");
		int count = 0;
		for (PathnameMap pathMap : pathnameMaps) {
			count++;
			publish("Processing " + pathMap.var_name + " (" + count + " of " + pathnameMaps.size() + ").");  //Stringworker;
			if (!categoryList.contains(pathMap.var_category)) {
				continue;
			}
			ArrayList<String> rowData = new ArrayList<String>();
			rowData.add(pathMap.var_name);
			boolean calculate_dts = false;
			if (pathMap.report_type.equals("Exceedance_Post")) {
				calculate_dts = true;
			}
			DataReference ref1 = Utils.getReference(dss_group1, pathMap.path1, calculate_dts, pathnameMaps, 1);
			DataReference ref2 = Utils.getReference(dss_group2, pathMap.path2, calculate_dts, pathnameMaps, 2);
			for (TimeWindow tw : timewindows) {
				double avg1 = Utils.avg(Utils.cfs2taf((RegularTimeSeries) ref1.getData()), tw);
				double avg2 = Utils.avg(Utils.cfs2taf((RegularTimeSeries) ref2.getData()), tw);
				double diff = avg2 - avg1;
				double pctDiff = Double.NaN;
				if (avg1 != 0) {
					pctDiff = diff / avg1 * 100;
				}
				rowData.add(formatDoubleValue(avg2));
				rowData.add(formatDoubleValue(avg1));
				rowData.add(formatDoubleValue(diff));
				rowData.add(formatDoubleValue(pctDiff));
			}
			if ("B".equals(pathMap.row_type)) {
				ArrayList<String> blankRow = new ArrayList<String>();
				for (int i = 0; i < rowData.size(); i++) {
					blankRow.add(" ");
				}
				writer.addTableRow(blankRow, null, Writer.NORMAL, false);
				writer.addTableRow(rowData, null, Writer.BOLD, false);
			} else {
				writer.addTableRow(rowData, null, Writer.NORMAL, false);
			}
		}
		writer.endTable();
	}

	private String formatDoubleValue(double val) {
		return Double.isNaN(val) ? "" : String.format("%3d", Math.round(val));
	}

	public void generatePlot(ArrayList<double[]> buildDataArray, int dataIndex, String title, String[] seriesName,
			String yAxisLabel, String xAxisLabel, String plotType) {
		if (plotType.equals(PlotType.TIME_SERIES)) {
			writer.addTimeSeriesPlot(buildDataArray, title, seriesName, xAxisLabel, yAxisLabel);
		} else if (plotType.equals(PlotType.EXCEEDANCE)) {
			writer.addExceedancePlot(buildDataArray, title, seriesName, xAxisLabel, yAxisLabel);
		} else {
			logger.warning("Requested unknown plot type: " + plotType + " for title: " + title + " seriesName: "
					+ seriesName[0] + ",..");
		}
	}

	public static interface PlotType {

		String TIME_SERIES = "timeseries";
		String EXCEEDANCE = "exceedance";

	}

	public static class PathnameMap {
		String report_type;
		String path1;
		String path2;
		String var_category;
		String var_name;
		String row_type;

		public PathnameMap(String var_name) {
			this.var_name = var_name;
		}
	}

	public String getOutputFile() {
		return scalars.get("OUTFILE");
	}

}
