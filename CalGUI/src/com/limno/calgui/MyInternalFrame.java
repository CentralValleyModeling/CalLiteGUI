package com.limno.calgui;

import javax.swing.JInternalFrame;

public class MyInternalFrame extends JInternalFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
    static final int xOffset = 1, yOffset = 10;

    public MyInternalFrame(String supstr) {
        super(supstr, 
              true, 
              true, 
              true,
              true);

        //...Create the GUI and put it in the window...

        //...Then set the window size or call pack...
        //setSize(300,300);

        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
}