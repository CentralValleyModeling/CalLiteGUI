Aceeptance testing procedure for Cal-Lite User Interface

1. Run Default Scenario

  * Erase any pre-existing installations of the CalLite GUI.
  * Extract contents of zip file into folder of your choosing. Note the file name references the build date.
  * Double click run-calgui.bat. This should fire up the user interface.
  * Click the "Run Scenario" button. Ignore the Windows Firewall warning should one appear. The default scenario should run "out of the box". You will see WRIMS-generated message in the console.

2. Create and save D1485 scenario

  * Change the Run Period to end 10/23
  * Switch to Regulations page
  * Click on "D-1485" radiobutton
  * File|Save As "D1485.cls"

3. Create and save D1641 scenario

  * Click on "D-1641" radiobutton
  * File|Save As "D1485.cls"

4. Do batch run

  * Switch to Run Settings page
  * Click "Select Scenarios" button and choose D1485.cls and D1641.cls.

5. Check outputs

  * When runs are done, switch to "Quick Results"
  * Delete DEFAULT\_DV.DSS
  * Quick Results
    * Right click on "Trinity" checkbox to see plot
    * (MORE DETAIL TO COME)
  * Switch to "External PDF"
    * Select D1485\_DV.DSS and DEFAULT\_DV.DSS as result files.
    * Click "Generate Report"
  * Switch to "Map View"
    * (MORE DETAIL TO COME)


---

## _Old Notes_ ##

  1. erify post processing by checking these outputs:
> ...
> Quick Results
> Exceedence plot
> Verify Panel with month checkboxes comes alive
> Right Click Trinity or some other
> Order ascending exceedence percentage on plot

> 5. If the run fails or the UI exhibits weird behavior, please write up a bug report.




2. Test D-1485 Scenario

  1. Repeat steps (1) through (2) above, if you haven't yet done so.
> 2. Click the "Regulations" tab. The Regulations dashboard should appear.
> 3. Click the D-1485 button under the Quick Select panel
> 4. Save the scenario: File > Save As. Pick a file name for the cls file
> 5. Verify the these files appear in the Run Setting tab:
> > Scenario Name: The file name you chose.
> > DSS File Name: The same filename, concatenated with "_DV.DSS:

> 6. Click the "Run Scenario" button.
> 7. After the run finishes, verify the post-processing functionality:
> > ...

> 8. If the run fails or the UI exhibits weird behavior, please write up a bug report._

3. Test D-1641 Scenario

