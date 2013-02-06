REM ----------------------------------------------------------
REM set study config
REM pause
set MainFile=D:\workspace\CalLite\Run\main.wresl
set SvarFile=D:\workspace\CalLite\Run\DSS\CL_FUTURE_BO_080911_SV.DSS
set SvarFPart=2020D09E
set InitFile=D:\workspace\CalLite\Run\DSS\CL_INIT.dss
set InitFPart=INITIAL
set DvarFile=D:\workspace\CalLite\Scenarios\DEFAULT_DV.DSS
set StartYear=1921
set StartMonth=10
set StartDay=31
set EndYear=2003
set EndMonth=9
set EndDay=30

REM ----------------------------------------------------------
REM set wrims v2 lib

set lib=Model_w2\lib
set ExternalDir=Run\External

set path=%lib%;%ExternalDir%

jre6\bin\java -Xmx1000m -Xss1024K -Djava.library.path=%path% -cp "%lib%\WRIMSv2.jar;%lib%\CalLiteV16.jar;%lib%\gurobi.jar;%lib%\heclib.jar;%lib%\jnios.jar;%lib%\jpy.jar;%lib%\misc.jar;%lib%\pd.jar;%lib%\vista.jar;%lib%\commons-io-2.1.jar;%lib%\guava-11.0.2.jar;%lib%\javatuples-1.2.jar" wrimsv2.components.ControllerSG \ %MainFile% %SvarFile% %InitFile% %DvarFile% %SvarFPart% %InitFPart% CalLite 1MON %StartYear% %StartMonth% %StartDay% %EndYear% %EndMonth% %EndDay% XA csv

