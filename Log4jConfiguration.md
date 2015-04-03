#Instructions for setting up log4j config file

Edit this line in log4j.properties

log4j.appender.rollingFile.File=/the/path/to/your-workspace-and-checkout/CalGUI/logs/callite-debug.log

Note unix slashes work for windows. An example:

c:/users/john/workspace/project-name/logs/myLog.log

You will also need to add the log4j jar to your build path.

You can test to see if DSSGrabber exceptions are being logged by running the unit test in TestDSSGrabber.

Note you may need to add the junit jar to your build path as well.