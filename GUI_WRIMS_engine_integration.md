# Introduction #

GUI receives scenario options and data and has to pass this information to the WRIMS engine

# Details #

  * GUI write to scenario directory
  * WRIMS engine copies over to Run directory ?

## NECESSARY STEPS ##

  * CALGUI code modified to write generated files to RunFiles subdir

  * CALGUI to generate RunFiles on save operation ( what if the cls file to be loaded has the same name as one cls file in the .\Scenarios folder? )

  * CALGUI or WRIMS2 to create new Run subdir and copy generated files over from RunFiles

  * CALGUI to write config file

  * CALGUI to save results from new Run subdir (config file)

## OUTCOME ##

> - Generated/custom files are easy to find and inspect in Runfiles subdir

> - All output available for inspection in Run subdir



## Preceding discussion: ##

1. CALGUI will write scenario-specific files to a scenario-specific directory

> - could be .\Scenarios\scenname\RunFiles

> - Replicates Run directory structure - e.g. .\Scenarios\scenname\RunFiles\Lookup, etc.

> - Generated automatically when a scenario is saved


2. CALGUI will then create a new Run directory

> - .\Scenarios\scenname\Run


3. CALGUI copies (all of?) .\Default to .\Scenarios\scenname\Run



4. CALGUI then copies all contents of .\Scenarios\scenname\RunFiles over .\Scenarios\scenname\Run

> - This overwrites default files with all necessary custom files


OUTCOME

> - Generated/custom files are easy to find and inspect in Runfiles subdir

> - All output available for inspection in Run subdir






  * WRIMS updates progress to GUI - **NOT YET RESOLVED**