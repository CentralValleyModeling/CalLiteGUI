package gov.ca.water.calgui.batch;

import java.io.IOException;
import java.util.ArrayList;

public class RunScenarios {

	public RunScenarios() {

	}

	public void runParallel(ArrayList<String> scenarioList) throws IOException, InterruptedException {

		for (String sc : scenarioList) {

			String fn = "run_" + sc + ".bat";
			Runtime rt = Runtime.getRuntime();
			// Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\CalLite_w2.bat");
			Process proc = rt.exec("cmd /c start " + System.getProperty("user.dir") + "\\" + fn);
			int exitVal = proc.waitFor();
		}
	}

	public static void main(String[] args) {

		ArrayList<String> scenarioList = new ArrayList<String>();
		scenarioList.add("test1");
		scenarioList.add("test2");

		RunScenarios rs = new RunScenarios();
		try {
			rs.runParallel(scenarioList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
