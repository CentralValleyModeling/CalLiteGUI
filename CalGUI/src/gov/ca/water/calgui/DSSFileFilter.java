package gov.ca.water.calgui;

import java.io.File;


public class DSSFileFilter extends javax.swing.filechooser.FileFilter
{
     public boolean accept(File file)
     {
          //Convert to lower case before checking extension
         return (file.getName().toLowerCase().endsWith(".dss")  ||
            file.isDirectory());
    }

    public String getDescription()
    {
        return "DSS File (*.dss)";
    }
}
