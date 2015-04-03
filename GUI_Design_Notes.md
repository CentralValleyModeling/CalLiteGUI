# Introduction #

Add your content here.


# GUI specification #

  1. The CalLite GUI is implemented as a desktop Java application.
  1. The Swing UI layout is mostly defined in the file [Config/GUI.xml](https://code.google.com/p/callitegui/source/browse/trunk/CalGUI/Config/GUI.xml).
  1. The layout in GUI.xml is realized as a Swing GUI using the [SwiXml](http://www.swixml.org) library.
  1. Low-level GUI behavior is encapsulated in SwiXml.
  1. GUI.xml allows Swing Actions to be associated with components whose ActionListeners can be defined in the Java code.
  1. Other Listeners can be defined in the code.
  1. Basic code access to GUI components is through SwiXml's SwingEngine object. The main instance is defined in the GUI app's main class, which is [MainMenu](https://code.google.com/p/callitegui/source/browse/trunk/CalGUI/src/gov/ca/water/calgui/MainMenu.java).
  1. The main SwingEngine instance - currently named "swix" - can be used to access components defined in GUI.xml. For example, the textfield "run\_txfoDSS" can be accessed as follows:
`(JTextField) swix.find("run_txfoDSS"))`

# Linking GUI components to CalLite model inputs #

The values of textfield, checkbox, and radiobutton components in the GUI can be linked directly to CalLite model inputs specified in GUI-generated .table files. The linkage is specified in the file [GUI\_Links2.table](https://code.google.com/p/callitegui/source/browse/trunk/CalGUI/Config/GUI_Links2.table).

For a textfield, the fields GUIid, Table name, Index, Option and description are needed. The Option should be -1 to indicate a textfield; the value to be written is retrieved from the component named "GUIid" and then placed in the GUI-generated .table file named "Table name" in data row "Index".

For a checkbox, the same fields are used except that Option should be set to a value of -2. A value of "1" will be plin the SwiXml-defined GUI

## Scenario management ##