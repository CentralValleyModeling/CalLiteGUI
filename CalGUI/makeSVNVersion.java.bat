"c:\Program Files (x86)\Subversion\bin\svnversion" -n . > SVNVersion.java.fragment2
copy /b SVNVersion.java.fragment1+SVNVersion.java.fragment2+SVNVersion.java.fragment3 .\src\gov\ca\water\calgui\utils\SVNVersion.java
