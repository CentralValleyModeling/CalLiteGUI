package com.limno.calgui;

import java.io.File; 
public class CGRFileFilter extends javax.swing.filechooser.FileFilter
{
    public boolean accept(File file)
    {
         //Convert to lower case before checking extension
        return (file.getName().toLowerCase().endsWith(".cgr")  ||
           file.isDirectory());
   }

   public String getDescription()
   {
       return "CalLite Report File (*.cgr)";
   }
}