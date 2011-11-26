/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;
import java.awt.Color;
import java.awt.Component;
//import java.awt.Container;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.FontMetrics;
import java.awt.Font;

import javax.swing.Icon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;

/**
 * class Die
 * 
 */

public class Die implements Icon {

	/** Attributes: */

	private String ShownText;
	private int HighlightAmount = 0;
//	private int X;
//	private int Y;
	
	private Polygon square;
	private Polygon border;
	private Polygon borderInside;
	public static final int DEFAULT_WIDTH = 75;
	public static final int DEFAULT_HEIGHT = 75;
	
	/** Public methods: */
	public Die(String label, int X, int Y) {		
//		this.X = X;
//		this.Y = Y;
		ShownText = label;		
		
		initSquare();
		initBorder();
	}

	public void setLabel(String newText) {
		ShownText = newText;
	}
	
	public String getLabel() {
		return(ShownText);
	}
	
	public Polygon getBorder() {
		return(border);
	}
	
	public Polygon getBorderInside() {
		return(borderInside);
	}
	
	public Polygon getBackground() {
		return(square);
	}		
	
	// Do a pulsating effect where highlights are assigned 0--1234543212....
	public void setHighlight(int level) {
		if (level == 0) {
			HighlightAmount=0;
			return;
		}
		
		// This is ugly as sin, but my formula [ level-(2*(level-4))+1 ] ain't workin'. 
		switch(level%8) {
		case 0:
			HighlightAmount=1;
			break;
		case 1:
			HighlightAmount=2;
			break;
		case 2:
			HighlightAmount=3;
			break;
		case 3:
			HighlightAmount=4;
			break;
		case 4:
			HighlightAmount=5;
			break;
		case 5:
			HighlightAmount=4;
			break;
		case 6:
			HighlightAmount=3;
			break;
		case 7:
			HighlightAmount=2;
			break;			
		}
	}

	public void reset() {
		HighlightAmount=0;
	}
	
	public int getIconHeight() {
	    return(DEFAULT_HEIGHT);
	}

	public int getIconWidth() {
	    return(DEFAULT_WIDTH);
	}
	
	public void initSquare() {
		square = new Polygon();
	    
	    //======= Calculate 4 corner points ========
	    
	    square.addPoint(0, 0);
	    square.addPoint(0, DEFAULT_HEIGHT);	    
	    square.addPoint(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	    square.addPoint(DEFAULT_WIDTH, 0);
	}

	public void initBorder() {
		border = new Polygon();
		
		//======= Calculate 4 corner points ========
	    
	    border.addPoint(-7, -7);
	    border.addPoint(-7, DEFAULT_HEIGHT+7);	    
	    border.addPoint(DEFAULT_WIDTH+7,DEFAULT_HEIGHT+7);
	    border.addPoint(DEFAULT_WIDTH+7, -7);
	    //===============================================
	    
	    borderInside = new Polygon();
	    borderInside.addPoint(-2, -2);
	    borderInside.addPoint(-2, DEFAULT_HEIGHT+2);	    
	    borderInside.addPoint(DEFAULT_WIDTH+2,DEFAULT_HEIGHT+2);
	    borderInside.addPoint(DEFAULT_WIDTH+2, -2);
	}
	
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		g.translate(x,y);
		if(HighlightAmount > 0)
		{	    	
			//Border
			g.setColor(new Color(120, 0, 0));
			g.fillPolygon(border);
			    	
			//Border Inside
			g.setColor(new Color(255, 255, 255));
			g.fillPolygon(borderInside);
			    						
			//Highlight
			g.setColor(new Color(140, 0, 0, 255-(HighlightAmount*15)));
			g.fillPolygon(square);
		}else{	    	
			    	
			//Border
			//g.setColor(new Color(62, 77, 159));
			g.setColor(Color.darkGray);			
			g.fillPolygon(border);
			    	
			//Border Inside
			g.setColor(new Color(255, 255, 255));
			g.fillPolygon(borderInside);
			    	
			//Background
			//g.setColor(new Color(71, 87, 170, 255));
			g.setColor(new Color(70, 70, 70, 255));
			g.fillPolygon(square);
		}
			    
		//Text
		int totalWidth,totalHeight,posX, posY;
			    
		Font f = new Font("Dialog", Font.PLAIN, (DEFAULT_WIDTH + DEFAULT_HEIGHT)/4);
		g.setFont(f);
		g.setColor(new Color(255,255,255,255));	    
		FontMetrics fm = g.getFontMetrics();
		totalWidth = fm.charWidth(ShownText.charAt(0));
		totalHeight = fm.getDescent() - fm.getAscent();
		if(ShownText.length() > 1)
		{
		  	totalWidth += fm.charWidth(ShownText.charAt(1));
		}
			    
			    
		posX = (DEFAULT_WIDTH - totalWidth)/2;
		posY = (DEFAULT_HEIGHT - totalHeight)/2;
			   
			    
		g.setColor(new Color(255, 255, 255));
		g.drawString(ShownText,posX, posY);
			    
		g.translate(-x,-y);
	}	
}
