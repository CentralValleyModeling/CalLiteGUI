#     Name: StudyTab.py
#   Author: Ben Tustison
#   E-mail: tustison@mbkengineers.com
#    Phone: 916.456.4400
# Last Rev: 06.25.2010
#  Purpose: Mimics StudyTab class from CALSIM

# python modules
import shutil

#CalsimWsiDi class imports
from StudyTab import *

# java class imports - standard
from java.awt import *
from java.awt.event import *
from java.io import *
from java.util import *
from javax.swing import *
from java.lang import *

# Main class

class Main:

   # constructor: initialize class parameters
   def main():
        studyDvName=r"D:\CS3_Studies\CS3_BO_version134testANN_wsi\calsim30_bo\conv\DSS\Version134testANN_060812_wsiDV.dss";
      
        # WSI-DI curve labels and DSS pathnames
	crvName = ['SWP','CVP_SYS'] 
	crvWsiVar = ['WSI_ACTUAL_SWP','WSI_ACT_CVP_SYS'] 
        crvDiVar = ['DI_ACTUAL_SWP','DI_ACT_CVP_SYS']
        
        # maximum value(TAF) for WSI-DI curve; same value used for WSI and DO components
        crvMax = [20000,20000]
      
       	s=StudyTabCl()
       	s.runForWsi(studyDvName,crvName,crvWsiVar,crvDiVar,crvMax)

  
   if __name__ == "__main__":
       main()

