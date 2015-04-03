Using Ant to compile and deploy Cal-Lite GUI

# Introduction #

We now have a tool that will build the UI from the source code, jar-up the classes, and deploy the resources required to run the UI.

To use:

In Eclipse:

Window > Show View > Other > Ant

Open build.xml in the Ant panel. Execute the target called Main.

The script will create a deploy directory in your project then will deploy the contents into a time-stamped zip file at the root of your project. To test, move the zip file to another location, unzip, then double click the .bat file.

**Note**
To compile from source, your dev machine must have a system variable called JAVA\_HOME. JAVA\_HOME needs to point to a jdk, not a jre. Ant uses the jdk to compile the source code into class files outside of the Eclipse environment.

**Make sure you use the most current jdk update for java 6**
I am using

jdk1.6.0\_39

You can find the 32-bit version from Oracle's website:

http://www.oracle.com/technetwork/java/javase/downloads/jre6-downloads-1637595.html

Create or edit the system variable JAVA\_HOME using the procedures for windows OS.
