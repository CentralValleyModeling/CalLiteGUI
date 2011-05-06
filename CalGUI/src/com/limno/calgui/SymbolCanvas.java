package com.limno.calgui;

import java.awt.*;

public class SymbolCanvas extends Canvas {
    Font font;
    int charHeight;
    int charWidth;
    int charBase;
    int charVal;

	public SymbolCanvas(Font font, int base, int val, Image img) {
		//Image image = imageicon.getImage();
		Graphics  graphics = img.getGraphics();
		graphics.setColor(Color.red);		
        FontMetrics fm = graphics.getFontMetrics();
        charHeight = fm.getHeight() + 3;
        charWidth = fm.getMaxAdvance() + 4;

        charBase = base;
        charVal = val;
        setSize(charWidth * 16 + 60, charHeight * 16 + 10);
        repaint();
    }

    public void setBase(int base) {
        charBase = base;
        repaint();
    }

    public void setFont(Font font) {
        this.font = font;
        repaint();
    }

    public void paint(Graphics g) {
        g.setFont(font);
        g.setColor(Color.red);
        char[] carray = new char[1];
        int c = charBase;
        int y = 310;
        for (int v = 0; v < 2; v++) {
            //g.drawString(Integer.toHexString(c), 10, y);
            int x = 330;
            carray[0] = ((char)(charBase+charVal));
            g.drawChars(carray, 0, 1, x, y);
            /*for (int h = 0; h < 16; h++) {
                carray[0] = (char)c++;
                g.drawChars(carray, 0, 1, x, y);
                x += charWidth;
            }
            */
            y += 70;
        }
    }
}
