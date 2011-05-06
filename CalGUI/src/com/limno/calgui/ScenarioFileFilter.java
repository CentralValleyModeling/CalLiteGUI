package com.limno.calgui;

import java.io.File; 
public class ScenarioFileFilter extends javax.swing.filechooser.FileFilter
{
    public boolean accept(File file)
    {
         //Convert to lower case before checking extension
        return (file.getName().toLowerCase().endsWith(".cls")  ||
           file.isDirectory());
   }

   public String getDescription()
   {
       return "CalLite Scenario File (*.cls)";
   }
}