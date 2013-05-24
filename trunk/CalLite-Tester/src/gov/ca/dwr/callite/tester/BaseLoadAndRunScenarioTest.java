package gov.ca.dwr.callite.tester;

import static org.fest.swing.launcher.ApplicationLauncher.application;
import gov.ca.water.calgui.MainMenu;

import java.io.File;
import java.util.regex.Pattern;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BaseLoadAndRunScenarioTest {
	Robot robot;
	File scenariosDirectory = new File("Scenarios");
	File scenarioFile = new File(scenariosDirectory,"DEFAULT.cls"); 
    @Before
	public void loadAndRunScenario() {
    	Assert.assertTrue(scenariosDirectory.exists() && scenariosDirectory.isDirectory());
		application(MainMenu.class).start();
		robot = BasicRobot.robotWithCurrentAwtHierarchy();
		FrameFixture frame = WindowFinder.findFrame(FrameMatcher.withTitle(Pattern.compile("CalLite.*"))).using(robot);
		JButtonFixture button = frame.button(JButtonMatcher.withText("Load Scenario..."));
		Assert.assertNotNull(button);
		button.click();
		JFileChooserFixture fileChooser = frame.fileChooser();
		Assert.assertNotNull(fileChooser);
		fileChooser.selectFile(scenarioFile);
		fileChooser.approve();
		frame.button(JButtonMatcher.withText(Pattern.compile(".*Run Scenario.*"))).click();
		//FIXME: What indicates on the GUI that a run is in progress ?? Need to wait on that condition here.
		//FIXME: This is a guess that the model run will take 8 mins, a better way is as indicated above
		try {
			Thread.sleep(8*60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@After
	public void cleanUp(){
		robot.cleanUpWithoutDisposingWindows();
	}
	
	@Test
	public void testScenarioOutput(){
		File runDetails = new File(scenariosDirectory,"Run_Details");
		Assert.assertTrue(runDetails.exists());
		Assert.assertTrue(runDetails.isDirectory());
		//TODO: run the comparisions with baseline here
	}
	
	
}
