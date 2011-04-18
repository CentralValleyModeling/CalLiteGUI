package com.limno.calgui;

import javax.swing.text.*;
import javax.swing.*;

import java.text.ParseException;
import java.util.regex.Pattern;
 
public class NumericTextField extends JTextField
{
	
	private float minimum;
	private float maximum;
	
	public NumericTextField() {
	}

	
    @Override
    protected Document createDefaultModel()
    {
        return new NumericDocument();
    }
    
    public void setMinVal(float minval) {
    	minimum = minval;
    }
    
    public void setMaxVal(float maxval) {
    	maximum = maxval;
    }

    public float getMinVal() {
    	return minimum;
    }
    
    public float getMaxVal() {
    	return maximum;
    }

    private class NumericDocument extends PlainDocument
    {
        // The regular expression to match input against (zero or more digits)
        private final Pattern DIGITS = Pattern.compile("(\\d*)|[0-9]{0,15}[.]{1}[0-9]{0,15}");


        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
        {
        	// Only insert the text if it matches the regular expression
        	String s = super.getText(0, super.getLength());
        	String s1 = s.substring(0, offs);
        	String s2 = s.substring(offs,super.getLength() );
        	String sfinal = s1+str+s2;

        	try
        	{
        		float f = Float.valueOf(sfinal).floatValue();
        		float min = getMinVal();
        		float max = getMaxVal();

        		if (str != null && DIGITS.matcher(str).matches() && f >= min && f <= max)
        			super.insertString(offs, str, a);

        	}
        	catch(NumberFormatException e)
        	{
        	}
        }
    }
}