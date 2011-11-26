/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.awt.Container;
/*
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Component;
*/
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * class ClientBoard
 * 
 */

public class ClientBoard extends JLabel {
	static final long serialVersionUID = 9395353L;
	
	/** Attributes: */

	private Die[][] DieWidget;

	private int rows;
	private int cols;
	
	private static final int DEFAULT_SPACING = 20;
	
	/** Public methods: */
	public ClientBoard(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		
		// Initialize dice
		DieWidget = new Die[rows][cols];
		for (int i=0; i<rows; i++)
			for (int j=0; j<cols; j++)
				DieWidget[i][j]=new Die("?", i, j);		
	}
	
	public void setBoard(String[][] DiceData) {
		for(int y = 0; y < rows; y++)
		{
			for(int x=0; x < cols; x++)
			{
				// Set the label, don't create new dice.
				DieWidget[y][x].setLabel(DiceData[y][x]);				
			}
		}
	}

	public void draw() {
		repaint(1,0,0,getWidth(),getHeight());
	}

	public int getHeight() {
	    return((Die.DEFAULT_HEIGHT*rows) + ((rows+2)*DEFAULT_SPACING));
	}

	public int getWidth() {
		return((Die.DEFAULT_WIDTH*cols) + ((cols+2)*DEFAULT_SPACING));
	}	
	
	public void setHighlight(String Character, int Level) {
		//System.out.println("Searching for: "+Character);
		for(int y = 0; y < rows; y++)
		{
			for(int x=0; x < cols; x++)
			{
				if(DieWidget[y][x].getLabel().compareToIgnoreCase(Character) == 0)
				{
					//System.out.println("Found: "+Character);
					DieWidget[y][x].setHighlight(Level);
				}
			}
		}
		repaint();
	}

	public void setHighlight(int x, int y, int Level) {
		DieWidget[y][x].setHighlight(Level);
	}
	
	public void clearHighlights() {
		for(int y = 0; y < rows; y++)
		{
			for(int x=0; x < cols; x++)
			{
				DieWidget[y][x].setHighlight(0);				
			}
		}
		repaint();
	}

	public void paint(Graphics g) {
		int ySpace = 0;
		for(int yRow = 0; yRow < rows; yRow++)
		{
			int xSpace = 0;
			for(int xCol = 0; xCol < cols; xCol++)
			{
				DieWidget[yRow][xCol].paintIcon(this, g, 
					DEFAULT_SPACING + xSpace + Die.DEFAULT_WIDTH*xCol, 
					DEFAULT_SPACING + ySpace + Die.DEFAULT_HEIGHT*yRow
				);
				xSpace += DEFAULT_SPACING;
			}
			
			ySpace += DEFAULT_SPACING;
		}
	}
	


	public static void main(String args[]) {
	    JFrame frame = new JFrame("Label Icon");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    Container content = frame.getContentPane();

	    String [][] DiceData = new String[4][4];
	    
	    //1st row
	    DiceData[0][0] = "A";
	    DiceData[0][1] = "G";
	    DiceData[0][2] = "Qu";
	    DiceData[0][3] = "E";
	    
	    //2nd row
	    DiceData[1][0] = "T";
	    DiceData[1][1] = "N";
	    DiceData[1][2] = "L";
	    DiceData[1][3] = "O";
	    
	    //3rd row
	    DiceData[2][0] = "K";
	    DiceData[2][1] = "B";
	    DiceData[2][2] = "R";
	    DiceData[2][3] = "F";
	    
	    //4th row
	    DiceData[3][0] = "S";
	    DiceData[3][1] = "I";
	    DiceData[3][2] = "S";
	    DiceData[3][3] = "Z";
	    
	    ClientBoard board = new ClientBoard(4,4);
	    board.setBoard(DiceData);
	    board.setHighlight("S",1);
	    board.setHighlight(2,3,2);
	    board.setHighlight(2,2,3);
	    content.add(board);
	    
	    
	    frame.setSize(600, 600);
	    frame.setVisible(true);
	}
	
}
