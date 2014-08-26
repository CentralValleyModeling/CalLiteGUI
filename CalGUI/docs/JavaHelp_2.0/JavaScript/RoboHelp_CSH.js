// Adobe
// Copyright© 1998-2008 Adobe Systems Incorporated. All rights reserved.
// RoboHelp_CSH.js
//============================================================================================
// The Helper function for WebHelp Context Sensitive Help
//--------------------------------------------------------------------------------------------
//     Syntax:
//     function RH_ShowHelp(hParent, a_pszHelpFile, uCommand, dwData)
//
//     hParent
//          Reserved - Use 0
//   
//     pszHelpFile
//          WebHelp: 
//               Path to help system start page ("http://www.myurl.com/help/help.htm" or "/help/help.htm")
//               For custom windows (defined in Help project), add ">" followed by the window name ("/help/help.htm>mywin")
//
//          WebHelp Enterprise: 
//               Path to RoboEngine server ("http://RoboEngine/roboapi.asp")
//               If automatic merging is turned off in RoboEngine Configuration Manager, specify the project name in the URL ("http://RoboEngine/roboapi.asp?project=myproject")
//               For custom windows (defined in Help project), add ">" followed by the window name ("http://RoboEngine/roboapi.asp>mywindow")
//
//     uCommand
//          Command to display help. One of the following:
//                    HH_HELP_CONTEXT     // Displays the topic associated with the Map ID sent in dwData
//											if 0, then default topic is displayed.				
//               The following display the default topic and the Search, Index, or TOC pane. 
//               Note: The pane displayed in WebHelp Enterprise will always be the window's default pane.
//                    HH_DISPLAY_SEARCH 
//                    HH_DISPLAY_INDEX
//                    HH_DISPLAY_TOC
//
//     dwData
//          Map ID associated with the topic to open (if using HH_HELP_CONTEXT), otherwise 0
//
//     Examples:
//     <p>Click for <A HREF='javascript:RH_ShowHelp(0, "help/help.htm", 0, 10)'>Help</A> (map number 10)</p>
//     <p>Click for <A HREF='javascript:RH_ShowHelp(0, "help/help.htm>mywindow", 0, 100)'>Help in custom window (map number 100)</A></p>
//
//==================================================================================================
// The Helper function for Multiscreen Context Sensitive Help using Map Number
//--------------------------------------------------------------------------------------------------
//		Syntax:
//		function RH_ShowMultiscreenHelpWithMapNo(szHelpURL, szWnd, dwMapNo)
//
//		Parameters:
//			szHelpURL
//					Path to help system start page ('http://www.myurl.com/help/index.htm' or 'help/index.htm')
//
//			szWnd
//					Name of custom window in which the help is to be shown.
//					Allowed values:
//								Window Name String	to specify a window	: e.g. "MultiScreenWindow"
//								Blank String to specify no window		: e.g. ''
//
//			dwMapNo
//          		Map number associated with the topic to open. 
//					Allowed values:
//								Map number for a specific topic			: e.g. 10
//								Zero value for opening the default topic
//
//		Examples:
//			<p>Click for <A HREF="javascript:RH_ShowMultiscreenHelpWithMapNo('help/index.htm', '', 10)">Help</A> (Map number 10)</p>
//			<p>Click for <A HREF="javascript:RH_ShowMultiscreenHelpWithMapNo('help/index.htm', 'MultiScreenWindow', 10)">Help</A> (Custom Window Name MultiScreenWindow)</p>
//==================================================================================================
// The Helper function for Multiscreen Context Sensitive Help using Map Id
//--------------------------------------------------------------------------------------------------
//		Syntax:
//		function RH_ShowMultiscreenHelpWithMapId(szHelpURL, szWnd, szMapId)
//
//		Parameters:
//			szHelpURL
//					Path to help system start page ('http://www.myurl.com/help/index.htm' or 'help/index.htm')
//
//			szWnd
//					Name of custom window in which the help is to be shown.
//					Allowed values:
//								Window Name String	to specify a window	: e.g. "MultiScreenWindow"
//								Blank String to specify no window		: e.g. ''
//
//			szMapId
//					Map ID associated with the topic to open.
//					Allowed values
//								Map ID for a specific topic					: e.g. "HelpTopic"
//								Blank String for opening the default topic	: e.g. ''
//
//		Examples:
//			<p>Click for <A HREF="javascript:RH_ShowMultiscreenHelpWithMapId('help/index.htm', '', 'HelpTopic')">Help</A> (Map ID HelpTopic)</p>
//			<p>Click for <A HREF="javascript:RH_ShowMultiscreenHelpWithMapId('help/index.htm', 'MultiScreenWindow', 'HelpTopic')">Help</A> (Custom Window Name MultiScreenWindow)</p>
//
//===================================================================================================
// The Helper function for Browser based Help
//--------------------------------------------------------------------------------------------------		
//     Syntax:
//     function RH_Show_BrowserBasedHelp(hParent, strHelpPath, strCategory,strWnd, uCommand, nMapId) 
//
//   
//     hParent
//          Reserved - Use 0
//   
//     strHelpPath                	Path to help system start page ("http://www.myurl.com/help/help.htm" or "/help/help.htm")
//
//     strCategory                	Content Category defined in AIR SSL Dialog
//
//     strWnd                		Name of custom window 
//
//     uCommand
//          Command to display help. One of the following:
//                    HH_HELP_CONTEXT     // Displays the topic associated with the Map ID sent in dwData
//											if 0, then default topic is displayed.				
//               The following display the default topic and the Search, Index, or TOC pane. 
//                    HH_DISPLAY_SEARCH 
//                    HH_DISPLAY_INDEX
//                    HH_DISPLAY_TOC
//
//     nMapId          Map ID associated with the topic to open (if using HH_HELP_CONTEXT), otherwise 0
//

var gbNav6=false;
var gbIE5=false;

var gAgent=navigator.userAgent.toLowerCase();
var gbMac=(gAgent.indexOf("mac")!=-1);
var gbSunOS=(gAgent.indexOf("sunos")!=-1);
var gbOpera=(gAgent.indexOf("opera")!=-1);

var HH_DISPLAY_TOPIC = 0;
var HH_DISPLAY_TOC = 1;
var HH_DISPLAY_INDEX = 2;
var HH_DISPLAY_SEARCH = 3;
var HH_HELP_CONTEXT = 15;

var gVersion=navigator.appVersion.toLowerCase();

var gnVerMajor=parseInt(gVersion);
var gnVerMinor=parseFloat(gVersion);

if(navigator.appName.indexOf("Microsoft")!=-1)
{
	var nPos=gAgent.indexOf("msie");
	if(nPos!=-1)
	{
		var nVersion=parseFloat(gAgent.substring(nPos+5));
		if(nVersion>=5)
			gbIE5=true;
	}
}

if(gnVerMajor>=4)
{
	if(navigator.appName=="Netscape")
	{
		if(gnVerMajor>=5)
			gbNav6=true;
	}
}

function RH_ShowMultiscreenHelpWithMapNo(szHelpURL, szWnd, dwMapNo)
{
	var szParam = "?rhnewwnd=1&rhcsh=1";
	if (szHelpURL == "")
		return;
	
	if(szWnd)
		szParam += "&rhwnd=" + szWnd;
	if(dwMapNo > 0)
		szParam += "&rhmapno=" + dwMapNo.toString();
	var szURL = szHelpURL + szParam;

	if (gbIE5 || gbNav6)
		loadData(szURL);
	else
	{
		var sParam = "left="+screen.width+",top="+screen.height+",width=100,height=100";
		window.open(szURL, "__webCshStub", sParam);
	}
}
function RH_ShowMultiscreenHelpWithMapId(szHelpURL, szWnd, szMapId)
{
	var szParam = "?rhnewwnd=1&rhcsh=1";
	if (szHelpURL == "")
		return;
	
	if(szWnd)
		szParam += "&rhwnd=" + szWnd;
	if(szMapId != undefined && szMapId != "")
		szParam += "&rhmapid=" + szMapId;
	var szURL = szHelpURL + szParam;

	if (gbIE5 || gbNav6)
		loadData(szURL);
	else
	{
		var sParam = "left="+screen.width+",top="+screen.height+",width=100,height=100";
		window.open(szURL, "__webCshStub", sParam);
	}
}

function RH_Show_BrowserBasedHelp(hParent, strHelpPath, strCategory,strWnd, uCommand, nMapId) 
{
	var a_pszHelpFile = "";
	if (uCommand == HH_DISPLAY_TOPIC)
	{
		a_pszHelpFile = strHelpPath + "#<id=0";
	}
	if (uCommand == HH_HELP_CONTEXT)
	{
		a_pszHelpFile = strHelpPath + "#<id=" + nMapId;
	}
	else if (uCommand == HH_DISPLAY_INDEX)
	{
		a_pszHelpFile = strHelpPath + "#<cmd=idx";
	}
	else if (uCommand == HH_DISPLAY_SEARCH)
	{
		a_pszHelpFile = strHelpPath + "#<cmd=fts";
	}
	else if (uCommand == HH_DISPLAY_TOC)
	{
		a_pszHelpFile = strHelpPath + "#<cmd=toc";
	}
	
	if (strWnd)
		a_pszHelpFile += ">>wnd=" + strWnd;
		
	if(strCategory)
		a_pszHelpFile += ">>helpid=" +strCategory;

	if (a_pszHelpFile)
	{
		if (gbIE5 || gbNav6)
			loadDataForBrowserBased(a_pszHelpFile);
		else
		{
			var sParam = "left="+screen.width+",top="+screen.height+",width=100,height=100";
			window.open(a_pszHelpFile, "__webCshStub", sParam);
		}
	}
}

function RH_ShowHelpForContext(hParent, a_pszHelpFile, a_pszContext, uCommand, dwData) 
{
    // this function only support WebHelp
    var strHelpPath = a_pszHelpFile;
    var strWnd = "";
    var nPos = a_pszHelpFile.indexOf(">");
    if (nPos != -1) {
        strHelpPath = a_pszHelpFile.substring(0, nPos);
        strWnd = a_pszHelpFile.substring(nPos + 1);
    }
    if (a_pszContext.length > 0) {    
        var slashCh = "/";
        nPos = strHelpPath.lastIndexOf("/");
        if (nPos == -1) {
            nPos = strHelpPath.lastIndexOf("\\");
            slashCh = "\\";
        }
        if (nPos != -1)
            strHelpPath = strHelpPath.substring(0, nPos + 1);
        else
            strHelpPath = "";
        strHelpPath += a_pszContext;
        strHelpPath += slashCh;
        strHelpPath += a_pszContext;
        strHelpPath += ".htm";
    }
    RH_ShowWebHelp(hParent, strHelpPath, strWnd, uCommand, dwData);
}

function RH_ShowHelp(hParent, a_pszHelpFile, uCommand, dwData)
{
	// this function only support WebHelp
	var strHelpPath = a_pszHelpFile;
	var strWnd = "";
	var nPos = a_pszHelpFile.indexOf(">");
	if (nPos != -1)
	{
		strHelpPath = a_pszHelpFile.substring(0, nPos);
		strWnd = a_pszHelpFile.substring(nPos+1); 
	}
	if (isServerBased(strHelpPath))
		RH_ShowWebHelp_Server(hParent, strHelpPath, strWnd, uCommand, dwData);
	else
		RH_ShowWebHelp(hParent, strHelpPath, strWnd, uCommand, dwData);
}

function RH_OpenHelpTopic(a_pszHelpMainPage, a_pszRelTopicUrl)
{
	// this function only support WebHelp
	var strHelpPath = a_pszHelpMainPage;
	var strWnd = "";
	var nPos = a_pszHelpMainPage.indexOf(">");
	if (nPos != -1)
	{
		strHelpPath = a_pszHelpMainPage.substring(0, nPos);
		strWnd = a_pszHelpMainPage.substring(nPos+1); 
	}

	var a_pszHelpFile = "";
	a_pszHelpFile = strHelpPath + "#<url=" + a_pszRelTopicUrl;
	if (strWnd)
		a_pszHelpFile += ">>wnd=" + strWnd;

	if (a_pszHelpFile)
	{
		if (gbIE5 || gbNav6)
			loadData(a_pszHelpFile);
		else
		{
			var sParam = "left="+screen.width+",top="+screen.height+",width=100,height=100";
			window.open(a_pszHelpFile, "__webCshStub", sParam);
		}
	}
	
}


function RH_ShowWebHelp_Server(hParent, strHelpPath, strWnd, uCommand, dwData)
{
	// hParent never used.
	ShowWebHelp_Server(strHelpPath, strWnd, uCommand, dwData);
}

function RH_ShowWebHelp(hParent, strHelpPath, strWnd, uCommand, dwData)
{
	// hParent never used.
	ShowWebHelp(strHelpPath, strWnd, uCommand, dwData);
}


function ShowWebHelp_Server(strHelpPath, strWnd, uCommand, nMapId)
{
	var a_pszHelpFile = "";
	if (uCommand == HH_HELP_CONTEXT)
	{
		if (strHelpPath.indexOf("?") == -1)
			a_pszHelpFile = strHelpPath + "?ctxid=" + nMapId;
		else
			a_pszHelpFile = strHelpPath + "&ctxid=" + nMapId;
	}
	else
	{
		if (strHelpPath.indexOf("?") == -1)
			a_pszHelpFile = strHelpPath + "?ctxid=0";
		else
			a_pszHelpFile = strHelpPath + "&ctxid=0";
	}

	if (strWnd)
		a_pszHelpFile += ">" + strWnd;

	if (gbIE5 || gbNav6)
	{
		a_pszHelpFile += "&cmd=newwnd&rtype=iefrm";
		loadData(a_pszHelpFile);
	}
	else
	{
		var sParam = "left="+screen.width+",top="+screen.height+",width=100,height=100";
		window.open(a_pszHelpFile, "__webCshStub", sParam);
	}
}


function ShowWebHelp(strHelpPath, strWnd, uCommand, nMapId)
{
	var a_pszHelpFile = "";
	if (uCommand == HH_DISPLAY_TOPIC)
	{
		a_pszHelpFile = strHelpPath + "#<id=0";
	}
	if (uCommand == HH_HELP_CONTEXT)
	{
		a_pszHelpFile = strHelpPath + "#<id=" + nMapId;
	}
	else if (uCommand == HH_DISPLAY_INDEX)
	{
		a_pszHelpFile = strHelpPath + "#<cmd=idx";
	}
	else if (uCommand == HH_DISPLAY_SEARCH)
	{
		a_pszHelpFile = strHelpPath + "#<cmd=fts";
	}
	else if (uCommand == HH_DISPLAY_TOC)
	{
		a_pszHelpFile = strHelpPath + "#<cmd=toc";
	}
	if (strWnd)
		a_pszHelpFile += ">>wnd=" + strWnd;

	if (a_pszHelpFile)
	{
		if (gbIE5 || gbNav6)
			loadData(a_pszHelpFile);
		else
		{
			var sParam = "left="+screen.width+",top="+screen.height+",width=100,height=100";
			window.open(a_pszHelpFile, "__webCshStub", sParam);
		}
	}
}

function isServerBased(a_pszHelpFile)
{
	if (a_pszHelpFile.length > 0)
	{
		var nPos = a_pszHelpFile.lastIndexOf('.');
		if (nPos != -1 && a_pszHelpFile.length >= nPos + 4)
		{
			var sExt = a_pszHelpFile.substring(nPos, nPos + 4);
			if (sExt.toLowerCase() == ".htm")
			{
				return false;
			}
		}
	}
	return true;
}

function getElement(sID)
{
	if(document.getElementById)
		return document.getElementById(sID);
	else if(document.all)
		return document.all(sID);
	return null;
}

function loadDataForBrowserBased(sFileName)
{
	if(!getElement("dataDiv"))
	{
		if(!insertDataDivForBrowserBased())
		{
			gsFileName=sFileName;
			return;
		}
	}
	var sHTML="";
	sHTML+="<iframe name=\"__WebHelpCshStub\" style=\"width:5;height:5\" src=\""+sFileName+"\"></iframe>";
	
	var oDivCon=getElement("dataDiv");
	if(oDivCon)
	{
		if(gbNav6)
		{
			if(oDivCon.getElementsByTagName&&oDivCon.getElementsByTagName("iFrame").length>0)
			{
				oDivCon.getElementsByTagName("iFrame")[0].src=sFileName;
			}
			else
				oDivCon.innerHTML=sHTML;
		}
		else
			oDivCon.innerHTML=sHTML;
	}
}

function loadData(sFileName)
{
	if(!getElement("dataDiv"))
	{
		if(!insertDataDiv())
		{
			gsFileName=sFileName;
			return;
		}
	}
	var sHTML="";
	if(gbMac)
		sHTML+="<iframe name=\"__WebHelpCshStub\" src=\""+sFileName+"\"></iframe>";
	else
		sHTML+="<iframe name=\"__WebHelpCshStub\" style=\"visibility:hidden;width:0;height:0\" src=\""+sFileName+"\"></iframe>";
	
	var oDivCon=getElement("dataDiv");
	if(oDivCon)
	{
		if(gbNav6)
		{
			if(oDivCon.getElementsByTagName&&oDivCon.getElementsByTagName("iFrame").length>0)
			{
				oDivCon.getElementsByTagName("iFrame")[0].src=sFileName;
			}
			else
				oDivCon.innerHTML=sHTML;
		}
		else
			oDivCon.innerHTML=sHTML;
	}
}

function insertDataDivForBrowserBased()
{
	var sHTML="";
	sHTML+="<div id=dataDiv></div>";

	var obj = document.body;
	if (gbIE5)
	{
		obj.insertAdjacentHTML("beforeEnd", sHTML);
	}
	else
	{
		var r = obj.ownerDocument.createRange();
		r.setStartBefore(obj);
		var	parsedHTML = r.createContextualFragment(sHTML);
		obj.appendChild(parsedHTML);
	}
	return true;
}

function insertDataDiv()
{
	var sHTML="";
	if(gbMac)
		sHTML+="<div id=dataDiv style=\"display:none;\"></div>";
	else
		sHTML+="<div id=dataDiv style=\"visibility:hidden\"></div>";

	var obj = document.body;
	if (gbIE5)
	{
		obj.insertAdjacentHTML("beforeEnd", sHTML);
	}
	else if (gbNav6)
	{
		var r = obj.ownerDocument.createRange();
		r.setStartBefore(obj);
		var	parsedHTML = r.createContextualFragment(sHTML);
		obj.appendChild(parsedHTML);
	}
	return true;
}
