/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Observer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import MJBoggle.Client.ClientBoard;
import MJBoggle.Support.DataPacket;
import MJBoggle.Support.GuessEntry;
import MJBoggle.Support.Guesses;
import MJBoggle.Support.Leave;
import MJBoggle.Support.StandingEntry;
import MJBoggle.Support.Standings;


public class ObserverGame {
	private JFrame gameFrame;
	
	private ClientBoard board;
	private JLabel gameNameLabel;
	private JLabel remainingTimeLabel;

	private JLabel longGuessesLabel;
	private JScrollPane longGuessesScrollPane;
	private JTextPane longGuessesTextPane;
	
	private JLabel recentDupesLabel;
	private JScrollPane recentDupesScrollPane;
	private JTextPane recentDupesTextPane;

	private JLabel standingsLabel;
	private JScrollPane standingsScrollPane;
	private JTextPane standingsTextPane;
		
	private Timer stopTimer;	
	private volatile long stopTime;
	private boolean newGame = true;	
	
	private Font smallFont = new Font("Tahoma", Font.BOLD, 20);
	private Font mediumFont = new Font("Arial", Font.BOLD, 22);
	private Font largeFont = new Font("Tahoma", Font.BOLD, 23);
	private Font hugeFont = new Font("Tahoma", Font.BOLD, 34);

	public ObserverGame() {
		initComponents();
		
		stopTime = 0;
		stopTimer = new Timer(1000,new ActionListener() {				
			public void actionPerformed(ActionEvent e) {			
				if(stopTime > 0)
				{
					stopTime -= 1000;
					updateStopTime(stopTime);
				}
			}
		});
		
	}
	
	protected void initComponents() {
		gameFrame = new JFrame();
		board = new ClientBoard(4,4);
		
		gameNameLabel = new JLabel();
		remainingTimeLabel = new JLabel();
		
		gameFrame.setTitle("Observing a Ninja Boggle Dojo");
		
		longGuessesLabel = new JLabel("Longest Words Found");
		recentDupesLabel = new JLabel("Recent Dupes");
		standingsLabel = new JLabel("Current Standings");
		
		longGuessesScrollPane = new JScrollPane();
		recentDupesScrollPane = new JScrollPane();
		standingsScrollPane = new JScrollPane();
		
		longGuessesTextPane = new JTextPane();
		recentDupesTextPane = new JTextPane();
		standingsTextPane = new JTextPane();
		
		Container frameContentPane = gameFrame.getContentPane();
		frameContentPane.setLayout(new GridBagLayout());
		frameContentPane.setBackground(Color.black);
		((GridBagLayout)frameContentPane.getLayout()).columnWidths = new int[] {1, 500, 500, 5};
		((GridBagLayout)frameContentPane.getLayout()).rowHeights = new int[] {20, 20, 0, 205, 10, 100, 105, 0, 205, 15};
		
		gameFrame.addWindowListener(
	            new WindowAdapter()
	            {
	                public void windowClosing(WindowEvent e)
	                {
	                		DataPacket pkt = new Leave();
	                		Observer.getInstance().getConnection().sendPacket(pkt);
	                		Observer.getInstance().getLobbyWindow().setVisible(true);
	                		Observer.getInstance().getGameWindow().setVisible(false);
	                }
	           });
		
		gameNameLabel.setForeground(Color.white);
		gameNameLabel.setFont(largeFont);
		gameNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gameNameLabel.setBackground(Color.black);
		frameContentPane.add(gameNameLabel, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));
		
		//---- Board ----
		board.setHorizontalAlignment(SwingConstants.CENTER);
		board.setVerticalAlignment(SwingConstants.CENTER);
		board.setBackground(Color.black);
		frameContentPane.add(board, new GridBagConstraints(1, 1, 1, 4, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(5, 35, 0, 0), 0, 0));
		
		remainingTimeLabel.setForeground(Color.white);
		remainingTimeLabel.setBackground(Color.black);
		remainingTimeLabel.setFont(hugeFont);
		remainingTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		remainingTimeLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		
		remainingTimeLabel.setIcon(new ImageIcon(getClass().getResource("images/large_ninja.jpg")));
		remainingTimeLabel.setVerticalTextPosition(SwingConstants.TOP);
		remainingTimeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		
		frameContentPane.add(remainingTimeLabel, new GridBagConstraints(1, 6, 1, 3, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		
		//---- Long Guesses ----
		longGuessesLabel.setForeground(Color.white);
		longGuessesLabel.setBackground(Color.black);
		longGuessesLabel.setFont(mediumFont);
		longGuessesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frameContentPane.add(longGuessesLabel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		
		longGuessesTextPane.setEditable(false);
		longGuessesTextPane.setForeground(Color.white);
		longGuessesTextPane.setBackground(Color.darkGray);
		longGuessesTextPane.setFont(smallFont);
		longGuessesScrollPane.setViewportView(longGuessesTextPane);
		longGuessesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		longGuessesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		frameContentPane.add(longGuessesScrollPane, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		

		//---- Recent Dupes ----
		recentDupesLabel.setForeground(Color.white);
		recentDupesLabel.setBackground(Color.black);
		recentDupesLabel.setFont(mediumFont);
		recentDupesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frameContentPane.add(recentDupesLabel, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		recentDupesTextPane.setEditable(false);
		recentDupesTextPane.setForeground(Color.white);		
		recentDupesTextPane.setBackground(Color.darkGray);
		recentDupesTextPane.setFont(smallFont);
		recentDupesScrollPane.setViewportView(recentDupesTextPane);
		recentDupesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		recentDupesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);		
		frameContentPane.add(recentDupesScrollPane, new GridBagConstraints(2, 5, 1, 2, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		
		//---- Standings ----
		
		standingsLabel.setForeground(Color.white);
		standingsLabel.setBackground(Color.black);
		standingsLabel.setFont(mediumFont);
		standingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frameContentPane.add(standingsLabel, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		standingsTextPane.setEditable(false);
		standingsTextPane.setForeground(Color.white);
		standingsTextPane.setBackground(Color.darkGray);
		standingsTextPane.setFont(smallFont);
		standingsScrollPane.setViewportView(standingsTextPane);
		standingsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		frameContentPane.add(standingsScrollPane, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
					
		gameFrame.pack();
		
	}
	
	public void resetGame() {
		board.setBoard(getBlankBoard(4,4));
		newGame = true;
	}
	
	public void setVisible(boolean visible) {
		System.out.println("Visible: " + visible);
		gameFrame.setVisible(visible);
		standingsTextPane.setText("");
		recentDupesTextPane.setText("");
		longGuessesTextPane.setText("");
	}
	
	public void setGameName(String name) {
		if (name.length() > 20) 	name = name.substring(20);
		gameNameLabel.setText("Dojo: \""+name+"\"");		
	}
	
	public void setStopTime(long time) {
		stopTime = time;
		stopTimer.restart();
	}
	
	public void setDiceBoard(String[][] newBoard) {
		newGame = false;
		board.setBoard(newBoard);		
		board.draw();
	}	
	
	protected String[][] getBlankBoard(int rows, int cols) {
		String[][] blankBoard = new String[rows][cols];
	    for(int i = 0; i < rows; i++)
	    {
	    	for(int j = 0;j < cols; j++)
	    	{
	    		blankBoard[i][j] = " ";
	    	}	
	    }
	    return(blankBoard);
	}
	
	public void updateStandings(Standings currentStandings) {
		StringBuffer text = new StringBuffer();
		
		Collections.sort(currentStandings.Entries);
		Collections.reverse(currentStandings.Entries);
		
		for(StandingEntry e : currentStandings.Entries) {
			//text.append("<b>"+e.PlayerName + "</b> " + e.Score + "<br>");
			text.append(e.PlayerName + "\t[" + e.Score + "]\n");
		}
		
		standingsTextPane.setText(text.toString());
	}	
	
	public void updateLongGuesses(Guesses guesses) {
		StringBuffer text = new StringBuffer();
		
		Collections.sort(guesses.Entries);
		Collections.reverse(guesses.Entries);
		
		for(GuessEntry e : guesses.Entries) {
			//text.append("<font color=\"#FFFFFF\"><b>" + e.Guess + "</b> (" + e.Guess.length() + " letters)</font><br>");
			text.append(e.Guess + " [" + e.Guess.length() + "] - " + e.PlayerName + "\n");
		}
		
		if (text.length() > 1) text.deleteCharAt(text.length()-1);
		
		longGuessesTextPane.setText(text.toString());		
	}
	
	public void updateDupesGuesses(Guesses guesses) {
		StringBuffer text = new StringBuffer();
		
		Collections.sort(guesses.Entries);
		Collections.reverse(guesses.Entries);
		
		for(GuessEntry e : guesses.Entries) {
			text.append(e.Guess + " - " + e.PlayerName + "\n");
		}
		
		if (text.length() > 1) text.deleteCharAt(text.length()-1);
		
		recentDupesTextPane.setText(text.toString());		
	}
	
	protected void updateStopTime(long StopTime) {
		String timeLeft = null;
		long mins, secs;
		long totalSecs = StopTime/1000;
		mins = totalSecs/60;
		secs = totalSecs%60;
		
		if(!newGame)
		{		
			if(mins == 0 && secs <= 10)
			{
				timeLeft = "<html>Time Remaining<br><font color=\"#FF0000\">"+mins+" mins, "+secs+" secs</font>";
			}else{
				timeLeft = "<html>Time Remaining<br>" + mins + " mins, " + secs + " secs";
			}
		}else{
			timeLeft = "<html>New Fight In<br><font color=\"#00FF00\">"+mins+" mins, "+secs+" secs</font>";
		}
		
		if(mins == 0 && secs ==0)
		{
			newGame = !newGame;
		}
		remainingTimeLabel.setText(timeLeft);
	}
	
}
