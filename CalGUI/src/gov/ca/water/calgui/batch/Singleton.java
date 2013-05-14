package gov.ca.water.calgui.batch;

import java.io.IOException;
import java.util.Calendar;

import org.antlr.runtime.RecognitionException;

import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2.components.ControllerBatch;
import wrimsv2.components.Error;
import wrimsv2.components.PreRunModel;
import wrimsv2.evaluator.PreEvaluator;
import wrimsv2.wreslparser.elements.StudyUtils;

public class Singleton {

	public Singleton(String[] args) {

		runStudy(args);
	}

	public void runStudy(String[] args) {

		ControllerBatch cb = new ControllerBatch();
		cb.enableProgressLog = true;

		long startTimeInMillis = Calendar.getInstance().getTimeInMillis();
		try {
			cb.processArgs(args);
			StudyDataSet sds = cb.parse();

			if (StudyUtils.total_errors == 0 && Error.getTotalError() == 0) {
				new PreEvaluator(sds);
				new PreRunModel(sds);
				cb.generateStudyFile();
				// ExecutorService es = Executors.newCachedThreadPool();
				// es.execute(new ProgressUpdate("test", ControlData.startYear, ControlData.startMonth, ControlData.endYear,
				// ControlData.endMonth));
				cb.runModel(sds);
				long endTimeInMillis = Calendar.getInstance().getTimeInMillis();
				int runPeriod = (int) (endTimeInMillis - startTimeInMillis);
				System.out.println("=================Run Time is " + runPeriod / 60000 + "min"
				        + Math.round((runPeriod / 60000.0 - runPeriod / 60000) * 60) + "sec====");

			} else {
				System.out.println("=================Run ends with errors=================");
			}

		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(0);

	}

	public static void main(String[] args) {
		new Singleton(args);
	}
}
