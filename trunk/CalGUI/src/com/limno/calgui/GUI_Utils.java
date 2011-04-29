package com.limno.calgui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuListener;
import javax.swing.text.JTextComponent;

import org.swixml.SwingEngine;
import org.swixml.XScrollPane;

public class GUI_Utils {

    // If targetLocation does not exist, it will be created.
    public static void copyDirectory(File sourceLocation , File targetLocation)
    throws IOException {
        
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
	
    public static void ReplaceLineInFile(String filename, Integer LineNum, String newText ) {
    	final String NL = System.getProperty("line.separator"); 
        File f=new File(filename);
        
        Integer LineCt;

        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();

        String textinLine;

        try {
             fs = new FileInputStream(f);
             in = new InputStreamReader(fs);
             br = new BufferedReader(in);

            LineCt=0;
            while(true)
            {
                LineCt=LineCt+1;
                textinLine=br.readLine();
                if (LineCt == LineNum) {
                	sb.append(newText+ NL);
                } else {;
	                if(textinLine==null)
	                    break;
	                sb.append(textinLine+ NL);
                }
            }

              fs.close();
              in.close();
              br.close();

            } catch (FileNotFoundException e) {
              e.printStackTrace();
            } catch (IOException e) {
              e.printStackTrace();
            }

            try{
                FileWriter fstream = new FileWriter(f);
                BufferedWriter outobj = new BufferedWriter(fstream);
                outobj.write(sb.toString());
                outobj.close();

            }catch (Exception e){
              System.err.println("Error: " + e.getMessage());
            }
    }
    
    public static void ReplaceLinesInFile(String filename, Integer[] LineNum, String[] newText ) {
    	final String NL = System.getProperty("line.separator"); 
    	
        File f=new File(filename);
        
        Integer LineCt;

        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();

        String textinLine;
        Integer n=0;

        try {
             fs = new FileInputStream(f);
             in = new InputStreamReader(fs);
             br = new BufferedReader(in);

            LineCt=0;
            while(true)
            {
                LineCt=LineCt+1;
                textinLine=br.readLine();
                if (LineCt == LineNum[n]) {
                	sb.append(newText[n] + NL);
                	n=n+1;
                } else {;
	                if(textinLine==null)
	                    break;
	                sb.append(textinLine+ NL);
                }
            }

              fs.close();
              in.close();
              br.close();

            } catch (FileNotFoundException e) {
              e.printStackTrace();
            } catch (IOException e) {
              e.printStackTrace();
            }

            try{
                FileWriter fstream = new FileWriter(f);
                BufferedWriter outobj = new BufferedWriter(fstream);
                outobj.write(sb.toString());
                outobj.close();

            }catch (Exception e){
              System.err.println("Error: " + e.getMessage());
            }
    }
    
    public  static void  ReplaceTextInFile(String filename, String textToReplace, String newText ) {
    	final String NL = System.getProperty("line.separator"); 
    	File f=new File(filename);

    	FileInputStream fs = null;
    	InputStreamReader in = null;
    	BufferedReader br = null;

    	StringBuffer sb = new StringBuffer();

    	String textinLine;

    	try {
    		fs = new FileInputStream(f);
    		in = new InputStreamReader(fs);
    		br = new BufferedReader(in);

    		while(true)
    		{
    			textinLine=br.readLine();
    			if(textinLine==null)
    				break;
    			sb.append(textinLine + NL);
    		}
    		int cnt1 = sb.indexOf(textToReplace);
    		sb.replace(cnt1,cnt1+textToReplace.length(),newText);

    		fs.close();
    		in.close();
    		br.close();

    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	try{
    		FileWriter fstream = new FileWriter(f);
    		BufferedWriter outobj = new BufferedWriter(fstream);
    		outobj.write(sb.toString());
    		outobj.close();

    	}catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
    
    public static void CreateNewFile(String filename ) {
        File f;
        f=new File(filename);
        if(!f.exists()){
        try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
      }
	
    public static void WriteNewLinesInFile(String filename, String[] newText ) {

    	File f=new File(filename);
    	final String NL = System.getProperty("line.separator"); 
    	StringBuffer sb = new StringBuffer();

    	for (int i = 0; i < newText.length; i++) {
    		sb.append(newText[i] + NL);
    	}

    	try{
    		FileWriter fstream = new FileWriter(f);
    		BufferedWriter outobj = new BufferedWriter(fstream);
    		outobj.write(sb.toString());
    		outobj.close();

    	}catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
    } 
    
    
    public static void WriteGUITables(ArrayList arr, SwingEngine swix) throws IOException {
    	String filename="";
    	File f=null;
    	BufferedWriter outobj =null;
		String line="", outstring="";
		String cName="", tableName="", descr="", value="", option="";
		Boolean val;
		int index=0;
		
    	final String NL = System.getProperty("line.separator"); 
    	
    	for (int i = 0; i<arr.size();i++) {
    		line = arr.get(i).toString();
    		String[] parts = line.split("[\t]+");
    		cName = parts[0].trim();
    		tableName=parts[1].trim();
    		if(tableName.equals("n/a")){

    		} else{
    			index=Integer.parseInt(parts[2].trim());

    			option=parts[3].trim();
    			descr= "!"+ parts[4].trim();

    			if (tableName.equals(filename)) {

    			} else{
    				if (outobj!=null) {outobj.close();}	//close existing file

    				filename=tableName;
    				f=new File(System.getProperty("user.dir") + "\\Run\\Lookup\\" + filename);
    				GUI_Utils.deleteDir(f);
    				FileWriter fstream = new FileWriter(f);
    				outobj = new BufferedWriter(fstream);

    				outstring =filename.substring(0,filename.length()-6) + NL;
    				outobj.write(outstring);
    				outstring ="Index" + "\t" + "Option" + NL;
    				outobj.write(outstring);
    			}

    			Component c = (Component) swix.find(cName);

    			if (c instanceof JTextField || c instanceof NumericTextField || c instanceof JTextArea) {
    				value=((JTextComponent) c).getText();
    				option=value;
    				outstring = (index + "\t" + option + "\t" + descr + NL); 
    				outobj.write(outstring);
    			}else if (c instanceof JCheckBox) {
    				val=((AbstractButton) c).isSelected();
    				value=val.toString();
    				if (value.startsWith("true")) {
    					option="1";
    					outstring = (index + "\t" + option + "\t" + descr + NL); 
    					outobj.write(outstring);
    				} else {
    					option="0";
    					outstring = (index + "\t" + option + "\t" + descr + NL); 
    					outobj.write(outstring);
    				}
    			}else if (c instanceof JRadioButton) {
    				val=((AbstractButton) c).isSelected();
    				value=val.toString();

    				if (value.startsWith("true")) {
    					outstring = (index + "\t" + option + "\t" + descr + NL); 
    					outobj.write(outstring);
    				}
    			}else if (c == null) {      //control not found    			
    				outstring = (index + "\t" + option + "\t" + descr + NL); 
    				outobj.write(outstring);
    			}    

    		}
    	}
    	outobj.close();   	

    }
    
    public static void SetMouseListener(Component component, Object obj) {
		
    	component.addMouseListener((MouseListener) obj);

        for (Component child : ((Container) component).getComponents()) {
        	SetMouseListener(child,obj);
        }
    }
    
    public static void SetMenuListener(Component component, Object obj) {
		
    	 if (component instanceof JMenu) {
    		 JMenu m = (JMenu) component;
 			m.addMenuListener((MenuListener) obj);
 		}    	

        for (Component child : ((Container) component).getComponents()) {
        	SetMenuListener(child,obj);
        }
    }
    
    public static void SetChangeListener(Component component, Object obj) {
		
    	if (component instanceof JTabbedPane) {
    		JTabbedPane tp = (JTabbedPane) component;
    		tp.addChangeListener((ChangeListener) obj);
    	}
        for (Component child : ((Container) component).getComponents()) {
        	SetChangeListener(child,obj);
        }
    }
        
    
    public static void SetCheckBoxItemListener(Component component, Object obj) {
		
        if (component instanceof JCheckBox) {
        	JCheckBox c = (JCheckBox) component;
			c.addItemListener((ItemListener) obj);
		}    	

        for (Component child : ((Container) component).getComponents()) {
        	SetCheckBoxItemListener(child,obj);
        }
    }
    
    public static void SetRadioButtonItemListener(Component component, Object obj) {
		
        if (component instanceof JRadioButton) {
        	JRadioButton r = (JRadioButton) component;
			r.addItemListener((ItemListener) obj);
		}    	

        for (Component child : ((Container) component).getComponents()) {
        	SetRadioButtonItemListener(child,obj);
        }
    }
    
    public static void SetCheckBoxorRadioButtonItemListener(Component component, Object obj) {
		
        if (component instanceof JCheckBox ||component instanceof JRadioButton) {
        	((AbstractButton) component).addItemListener((ItemListener) obj);
		}    	

        for (Component child : ((Container) component).getComponents()) {
        	
        	SetCheckBoxorRadioButtonItemListener(child,obj);
        }
    }
  
    public static void ToggleEnComponentAndChildren(Component component, Boolean b) {

        component.setEnabled(b);

        for (Component child : ((Container) component).getComponents()) {
        	ToggleEnComponentAndChildren(child,b);
        }
    }
 
    public static void ToggleEnComponentAndChildren(Component component, Boolean b, Object obj) {

    	if (component.getClass()==obj) {
    		component.setEnabled(b);
    		}    

        for (Component child : ((Container) component).getComponents()) {
        	ToggleEnComponentAndChildren(child,b, obj);
        }
    }
    
    public static void ToggleVisComponentAndChildren(Component component, Boolean b) {

        component.setVisible(b);

        for (Component child : ((Container) component).getComponents()) {
        	ToggleVisComponentAndChildren(child,b);
        }
    }
    
    public static void ToggleEnComponent(Component component, Boolean b) {
        component.setEnabled(b);
    }
    
    public static void ToggleVisComponent(Component component, Boolean b) {
        component.setVisible(b);
    }
    
    public static void ToggleVisComponents(Component[] components, Boolean b) {
    	
		for (int i = 0; i < components.length; i++) {
			components[i].setVisible(b);
		}

    }
    
    public static void ToggleVisComponentAndChildrenCrit(Component component, String crit, Boolean b) {
    	String cName = component.getName();
    	if (cName != null) {
    		if (cName.startsWith(crit)) {component.setVisible(b);}    	
    	}
        for (Component child : ((Container) component).getComponents()) {
        	ToggleVisComponentAndChildrenCrit(child,crit,b);
        }  
    }
    
    public static void ToggleSelComponentAndChildren(Component component, Boolean b, Object obj) {

    	if (component.getClass()==obj) {
    		((AbstractButton) component).setSelected(b);
    		}    

        for (Component child : ((Container) component).getComponents()) {
        	ToggleSelComponentAndChildren(child,b, obj);
        }
    }
    
    public static int CountSelectedButtons(Component component, Object obj, int ct) {
    	
    	if (component.getClass()==obj) {
    		if (((AbstractButton) component).isSelected()) {
    		++ct;
    		}   		
    	}

        for (Component child : ((Container) component).getComponents()) {
        	ct=CountSelectedButtons(child, obj, ct);
        }
        
        return ct;
    }
    
    public static StringBuffer GetControlValues(Component component, StringBuffer sb) {
        //System.out.println(component.getName());
		String comp="";
		String value="";
		Boolean val;

		final String NL = System.getProperty("line.separator"); 
		
        if (component instanceof JTextField || component instanceof NumericTextField || component instanceof JTextArea) {
			comp=component.getName();
			value=((JTextComponent) component).getText();
			if (comp != null) {sb.append(comp + "|" + value + NL);}
		} else if (component instanceof JSpinner) {
				comp=component.getName();				
				value= ((JSpinner) component).getValue().toString();
				if (comp != null) {sb.append(comp + "|" + value + NL);}
		}else if (component instanceof JCheckBox || component instanceof JRadioButton) {
			comp=component.getName();
			val=((AbstractButton) component).isSelected();
			value=val.toString();
			if (comp != null) {sb.append(comp + "|" + value + NL);}
		}    	
        
        if (component instanceof JSpinner) {
        
        } else {
	        for (Component child : ((Container) component).getComponents()) {
	        	GetControlValues(child, sb);
	        }
        }
		return sb;
    }
      
    public static StringBuffer GetTableModelData(DataFileTableModel[] dTableModels, GUILinks gl, StringBuffer sb) {
    	final String NL = System.getProperty("line.separator"); 
    	
    	if (dTableModels == null) {
			System.out.println("Tables not initialized");
		} else {
			for (int switchIdx = 1; switchIdx <= 14; switchIdx++) {
				String switchID = Integer.toString(switchIdx);
				String cID = "1";//gl.ctrlForSwitch(switchID);

				int tID = Integer.parseInt(cID);
				if (dTableModels[tID] == null) {
					System.out.println("Table not initialized");
				} else {
					Object[][] dataArr;
					dataArr=dTableModels[tID].getTableData();
					String dataStr="";
					for (int row=0; row < dataArr.length; row++) {
					    for (int col=0; col < dataArr[row].length; col++){
					    	if (col==0) {
					    		dataStr = dataStr + dataArr[row][col].toString();
					    	} else {
					    		dataStr = dataStr + "," + dataArr[row][col].toString();
					    	}
					    }
				    	if (row!=dataArr.length) {
				    		dataStr = dataStr + ";";
				    	} else {
				    		dataStr = dataStr + ";";
				    	}
					}
					sb.append(tID + "|" + dataStr + NL);
				}
			}
		}
		return sb;
    }
	
	
	public  static void  SetControlValues(File f, SwingEngine swix, DataFileTableModel[] dTableModels, GUILinks gl ) {


		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;

		String textinLine;
		String comp;
		String value;
		Integer val1;
		Boolean val=false;
		String delims = "[|]";

    	try {
    		fs = new FileInputStream(f);
    		in = new InputStreamReader(fs);
    		br = new BufferedReader(in);

    		while(true)
    		{
				textinLine=br.readLine();
    			if(textinLine==null|textinLine.equals("DATATABLEMODELS"))
    				break;
				String[] tokens = textinLine.split(delims);
				
				
				
				comp=tokens[0];
				value=tokens[1];
				JComponent component = (JComponent) swix.find(comp);
				
				//System.out.println(component.getName());
				
				if (component instanceof JCheckBox || component instanceof JRadioButton) {
					if (value.toLowerCase().equals("true"))  {
						val=true;
					} else if (value.toLowerCase().equals("false")) {
						val=false;
					}
					((AbstractButton) component).setSelected(val);				
				} else if (component instanceof JSpinner) {
					JSpinner spn = (JSpinner) component;
					if (value.matches("((-|\\+)?[0-9])+")) { 
						val1=Integer.parseInt( value );
						spn.setValue(val1);
			        } else {  
			        	spn.setValue(value); 
			        }  

				}else {
					((JTextComponent) component).setText(value);
					//System.out.println(comp +": " + value);
				}

    		}
    		
    		
    		//Read in tablemodel data
    		while(true)
    		{
				textinLine=br.readLine();
    			if(textinLine==null|textinLine.equals("END DATATABLEMODELS"))
    				break;    			
    			
    			String[] tokens = textinLine.split(delims);
    			String cID=tokens[0];
    			
    			String fileName = gl.tableNameForCtrl(cID);   			
    			if (fileName != null) {

    				String[] files = fileName.split("[|]");
    				int size = files.length;

    				if (size == 1) {
    					// CASE 1: 1 file specified
    					fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" + fileName + ".table";
    					File fn = new File(fileName);
    					Boolean exists = fn.exists();
    				} else if (size == 2) {
    					// CASE 2: 2 files specified
    					fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[0]+ ".table";
    					File fn = new File(fileName);
    					Boolean  exists = fn.exists();
    					if (exists) {
    						fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[1]+ ".table";
    						fn = new File(fileName);
    						exists = fn.exists();
    						fileName=System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[0]+ ".table" + "|" + System.getProperty("user.dir") + "\\Default\\Lookup\\" +  files[1]+ ".table";
    					}
    				}

    			}

    			
    			int tID = Integer.parseInt(cID);

    			if (dTableModels == null) {
    				dTableModels = new DataFileTableModel[20];
    			}
    			if (dTableModels[tID] == null) {
    				dTableModels[tID] = new DataFileTableModel(fileName, tID);
    			}

        		dTableModels[tID].setVectors(tokens[1]);
        		
    			//JTable table = (JTable) swix.find("tblRegValues");
    			//table.setModel(dTableModels[tID]);
    			//table.createDefaultColumnsFromModel();

    		}


    		//fs.close();
    		//in.close();
    		//br.close();

    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

	}
	
    public static ArrayList GetGUILinks (String filename) {
    	ArrayList GUILinks = new ArrayList();

		Scanner input;
		try 
		{
			input = new Scanner(new FileReader(filename));
		}
		catch (FileNotFoundException e) {
			System.out.println("Cannot open input file " + filename);
			return null;
		}
		
	    int lineCount = 0;
	    int rowid=0;
	    int colid=0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lineCount++;
			if (lineCount > 1) {
				StringTokenizer st1 = new StringTokenizer(line, "\t| ");
				if (st1.countTokens()>0) {
					GUILinks.add(line);
				}			
			}
		}

		input.close();

		return GUILinks;
    }
	
    public static String GetControls(Component component, String str) {
        //System.out.println(component.getName());
		String comp="";
		String type="";
		String value="";
		Boolean val;

		final String NL = System.getProperty("line.separator"); 
		
        if (component instanceof JTextField || component instanceof NumericTextField || component instanceof JTextArea) {
			comp=component.getName();
			type="Text";
			value=((JTextComponent) component).getText();
		} else if (component instanceof JSpinner) {
				comp=component.getName();	
				type="Spinner";
				value= ((JSpinner) component).getValue().toString();
		}else if (component instanceof JCheckBox) {
			comp=component.getName();
			type="Checkbox";
			val=((AbstractButton) component).isSelected();
			value=val.toString();
		}else if (component instanceof JRadioButton) {
			comp=component.getName();
			type="Radiobutton";
			val=((AbstractButton) component).isSelected();
			value=val.toString();
		}    
        if (comp != "") {
        	str = str + (comp + "|" + type + "|" + value + NL);
        }
        
        if (component instanceof JSpinner) {
        
        } else {
	        for (Component child : ((Container) component).getComponents()) {
	        	GetControls(child, str);
	        }
        }
		return str;
    }
    
    public static ArrayList GetGUITables(ArrayList arr, String board)  {
		String cName="";
		String line="";
		String switchID="", TID="", datatable="";
		Boolean val;
		String board1="";
		int index;
		
		ArrayList arr1 = new ArrayList();
    	
		for (int i = 0; i<arr.size();i++) {
			line = arr.get(i).toString();
			String[] parts = line.split("[\t]+");
			
			
			if (parts.length > 6) {
				cName = parts[0].trim();
				datatable=parts[6].trim();
				switchID=parts[7].trim();
				TID=parts[8].trim();
				
				board1 = parts[5].trim();
				if (board1.equals(board)) {
					arr1.add(cName + "|" + datatable + "|" + switchID + "|" + TID);
				}
		    }

		}
		return arr1;
 	

    }
    	
}
