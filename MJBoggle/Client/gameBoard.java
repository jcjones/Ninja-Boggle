/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.*;

import MJBoggle.Support.*;


/**
 * @author Mikel Mazlaghani
 */
public class gameBoard  {
	
	
	//Inteface
	private JFrame gameFrame;
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenu menu2;
	private ClientBoard board;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JMenuItem menuItem3;
	private JLabel gameNameLabel;
	private JLabel guessesLabel;
	private JScrollPane guesses;
	private JTextPane guessTextArea;
	private JScrollPane guessesScrollPane;
	private JLabel standingsLabel;
	private JScrollPane standingsScrollPane;
	private JTextPane standingsTextPane;
	private JLabel guessFieldLabel;
	private JLabel timerLabel;
	private JTextField guessTextfield;
	private JButton guessButton;
	private JLabel chatLabel;
	private JTextArea chatterTextArea;
	private JScrollPane chatterScrollPane;
	private JTextField chatTextfield;
	private JButton chatSendButton;
	private Timer stopTimer;
	
	//Data	
	/*private String GameName;
	private IndividualScores Scores;
	private int HighlightLevel;
	*/
	private volatile long stopTime;
	private boolean newGame = true;
	private String guessLog = "<html>";
	private int guessScore = 0;
	
	public gameBoard() {
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
		
		initComponents();
	}
	
	private void initComponents() {		
				
		//Init Interface
		gameFrame = new JFrame();
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		menu2 = new JMenu();
		menuItem3 = new JMenuItem();
		gameNameLabel = new JLabel();
		board = new ClientBoard(4,4);
		guessesLabel = new JLabel();
		guesses = new JScrollPane();
		guessTextArea = new JTextPane();
		guessesScrollPane = new JScrollPane();
		standingsLabel = new JLabel();
		standingsScrollPane = new JScrollPane();
		standingsTextPane = new JTextPane();
		guessFieldLabel = new JLabel();
		timerLabel = new JLabel();
		guessTextfield = new JTextField();
		guessButton = new JButton();
		chatLabel = new JLabel();
		chatterTextArea = new JTextArea("[Sensei] You have joined the fight!");
		chatTextfield = new JTextField();
		chatSendButton = new JButton();
		chatterScrollPane = 
		    new JScrollPane(chatterTextArea,
		                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//======== gameFrame =======
		{
			gameFrame.setTitle("Ninja Boggle Dojo");
			gameFrame.setResizable(false);
			gameFrame.setBackground(Color.black);
			gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			Container frameContentPane = gameFrame.getContentPane();
			frameContentPane.setLayout(new GridBagLayout());
			frameContentPane.setBackground(Color.black);
			((GridBagLayout)frameContentPane.getLayout()).columnWidths = new int[] {10, 405, 105, 10};
			((GridBagLayout)frameContentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 190, 0, 190, 30, 0, 20, 130, 0, 0, 0};
			
			gameFrame.addWindowListener(
		            new WindowAdapter()
		            {
		                public void windowClosing(WindowEvent e)
		                {
		                		sendChatter(" is fleeing the battle.");
		                		DataPacket pkt = new Leave();
		                		Client.getInstance().getConnection().sendPacket(pkt);
		                		Client.getInstance().getLobbyWindow().setVisible(true);
		            			Client.getInstance().getBoardWindow().setVisible(false);
		            			
		                }
		           });
			
			//-------- menuBar1 --------
			{
				menuBar1.setForeground(Color.white);
				menuBar1.setBackground(Color.black);				
				//======== menu1 ========
				{
					menu1.setText("Game");
					menu1.setForeground(Color.white);
					menu1.setBackground(Color.black);
										
					//---- menuItem2 ----
					menuItem2.setText("Dojo List");
					menuItem2.setForeground(Color.white);
					menuItem2.setBackground(Color.black);					
					menuItem2.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							sendChatter(" is seeking a new dojo.");
	                		DataPacket pkt = new Leave();
	                		Client.getInstance().getConnection().sendPacket(pkt);
	                		Client.getInstance().getLobbyWindow().setVisible(true);
	            			Client.getInstance().getBoardWindow().setVisible(false);								
						}					
					});
					menu1.add(menuItem2);
					
					//---- menuItem1 ----
					menuItem1.setText("Flee [Exit]");
					menuItem1.setForeground(Color.white);
					menuItem1.setBackground(Color.black);					
					menuItem1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							sendChatter(" is fleeing the dojo.");	
							Client.getInstance().shutdown();
						}					
					});
					menu1.add(menuItem1);
					
				}
				menuBar1.add(menu1);

				// ======== menu2 ========
				{
					menu2.setText("Help");
					menu2.setForeground(Color.white);
					menu2.setBackground(Color.black);
					
					menuItem3.setText("How To Play");
					menuItem3.setForeground(Color.white);
					menuItem3.setBackground(Color.black);					
					menuItem3.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							JOptionPane.showMessageDialog(null,
								    "Boggle is played with a game board of 16 letter cubes, much like dice. These\n" +
									"are rolled into a tray such that only the top letter of each cube is visible. All\n" +
								    "players simultaneously begin the main phase of play for the duration of the game.\n\n" +
									"Each player searches for words that can be constructed from the letters of\n" +
									"sequentially adjacent cubes. Adjacent includes the horizontally, vertically,\n" +
									"and diagonally neighboring cubes. Words may include singular and plural\n" +
									"separately, but may not utilize the same letter cube more than once per word.\n\n" +
									"When you correctly guess a word (by typing it into the \"Guess\" box and clicking \n" + 
									"\"Attack\") you receive a number of points equal to the length of the word, unless\n" +
									"the word has already been found by another player. In that event only the first\n" +
									"player to successfully guess the word receives its points.",
								    "How To Play",
								    JOptionPane.PLAIN_MESSAGE,
								    new ImageIcon(getClass().getResource("images/icon_sensei_questions.gif"))
								    );
						}					
					});
					menu2.add(menuItem3);
				}
				menuBar1.add(menu2);
			}
			gameFrame.add(menuBar1, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 0), 0, 0));
			
			//---- gameNameLabel ----
			gameNameLabel.setForeground(Color.white);
			gameNameLabel.setFont(new Font("Verdana", Font.BOLD, 16));
			gameNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			gameNameLabel.setBackground(Color.black);
			frameContentPane.add(gameNameLabel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
			
			visuallyDemonstrateGuesses(0);
			
			//---- board ----
			board.setHorizontalAlignment(SwingConstants.CENTER);
			board.setVerticalAlignment(SwingConstants.CENTER);
			board.setBackground(Color.black);
			frameContentPane.add(board, new GridBagConstraints(1, 2, 1, 5, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
			
			//---- guessesLabel ----
			guessesLabel.setText("Guesses");
			guessesLabel.setForeground(Color.white);
			guessesLabel.setBackground(Color.black);
			guessesLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
			guessesLabel.setHorizontalAlignment(SwingConstants.CENTER);
			frameContentPane.add(guessesLabel, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//======== guesses =======
			{
				guesses.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				guesses.setViewportBorder(new EtchedBorder());
				
				//---- guessTextPane ----
				guessTextArea.setEditable(false);
				guessTextArea.setToolTipText("Your guesses!");
				guessTextArea.setContentType("text/html");
				guessTextArea.setBackground(Color.darkGray);
				guessTextArea.setForeground(Color.white);
				guesses.setViewportView(guessTextArea);
			}
			frameContentPane.add(guesses, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			frameContentPane.add(guessesScrollPane, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- standingsLabel ----
			standingsLabel.setText("Standings");
			standingsLabel.setForeground(Color.white);
			standingsLabel.setBackground(Color.black);
			standingsLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
			standingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
			frameContentPane.add(standingsLabel, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//========= standingsScrollPane ========
			{
				
				//---- standingsTextPane ----
				standingsTextPane.setContentType("text/html");
				standingsTextPane.setEditable(false);
				standingsTextPane.setBackground(Color.darkGray);
				standingsScrollPane.setViewportView(standingsTextPane);
			}
			frameContentPane.add(standingsScrollPane, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- guessFieldLabel ----
			guessFieldLabel.setText("Guess A Word");
			guessFieldLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			guessFieldLabel.setBackground(Color.black);
			guessFieldLabel.setForeground(Color.white);
			guessFieldLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			frameContentPane.add(guessFieldLabel, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- timerLabel ----
			timerLabel.setText(" ");
			timerLabel.setForeground(Color.white);
			timerLabel.setBackground(Color.black);
			timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
			frameContentPane.add(timerLabel, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- guessTextfield ----
			guessTextfield.setColumns(30);
			guessTextfield.setToolTipText("Make your move here!");
			guessTextfield.addCaretListener(new HighlightListener());
			guessTextfield.setBackground(Color.darkGray);
			guessTextfield.setForeground(Color.white);
			guessTextfield.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent e) {
						if(e.getKeyCode()==KeyEvent.VK_ENTER)
						{
							if(guessTextfield.getText().trim().compareTo("") != 0)
							{						
								Client client = Client.getInstance();
								MakeGuess pkt = new MakeGuess(guessTextfield.getText());
								client.getConnection().sendPacket(pkt);	
								guessTextfield.setText("");						
							}else{
								addChatter("[Sensei] You must be fast on your fingers!");
							}
						}
					}
			});
			
			frameContentPane.add(guessTextfield, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- guessButton ----
			guessButton.setText("Attack!");
			guessButton.setBackground(Color.black);
			guessButton.setForeground(Color.white);
			guessButton.setToolTipText("Give me your best move!");
			guessButton.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					if(guessTextfield.getText().trim().compareTo("") != 0)
					{						
						Client client = Client.getInstance();
						MakeGuess pkt = new MakeGuess(guessTextfield.getText());
						client.getConnection().sendPacket(pkt);	
						guessTextfield.setText("");						
					}else{
						addChatter("[Sensei] You are as silent as the wind.");
					}
					board.clearHighlights();
				}
			});
			frameContentPane.add(guessButton, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- chatLabel ----
			chatLabel.setText("Chat");
			chatLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			chatLabel.setBackground(Color.black);
			chatLabel.setForeground(Color.white);
			chatLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			frameContentPane.add(chatLabel, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- chatterTextfield ----
			chatterTextArea.setEditable(false);
			chatterTextArea.setLineWrap(true);
			chatterTextArea.setWrapStyleWord(true);
			chatterTextArea.setBackground(Color.darkGray);
			chatterTextArea.setForeground(Color.white);
			chatTextfield.setBackground(Color.darkGray);
			chatTextfield.setForeground(Color.white);
			frameContentPane.add(chatterScrollPane, new GridBagConstraints(1, 10, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			chatTextfield.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER)
					{
						if(chatTextfield.getText().trim().compareTo("") != 0)
						{
							Client client = Client.getInstance();
							Chatter pkt = new Chatter(chatTextfield.getText());
							client.getConnection().sendPacket(pkt);	
							chatTextfield.setText("");
						}else{
							addChatter("[Sensei] You are as silent as the wind.");
						}
					}
				}
			});
			frameContentPane.add(chatTextfield, new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			
			//---- chatSendButton ----
			chatSendButton.setText("Send");
			chatSendButton.setBackground(Color.black);
			chatSendButton.setForeground(Color.white);
			chatSendButton.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					if(chatTextfield.getText().trim().compareTo("") != 0)
					{
						sendChatter();
						chatTextfield.setText("");
					}else{
						addChatter("[Sensei] You are as silent as the wind.");
					}
				}
			});
			frameContentPane.add(chatSendButton, new GridBagConstraints(2, 11, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));			
		}
		
		gameFrame.pack();
	}	
	
	public void addChatter(String chatter){		
//		 Determine whether the scrollbar is currently at the very bottom position.
		JScrollBar vbar = chatterScrollPane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());

		chatterTextArea.append("\r\n"+chatter);
		
//		 now scroll if we were already at the bottom.
		if( autoScroll ) chatterTextArea.setCaretPosition( chatterTextArea.getDocument().getLength() );
	}
	
	public void sendChatter(String chatter){
		Client client = Client.getInstance();			
		Chatter pkt = new Chatter(chatter);
		client.getConnection().sendPacket(pkt);	
	}
	
	public void sendChatter() {
		sendChatter(chatTextfield.getText());
	}
	
	public void resetGame(){
		board.setBoard(getBlankBoard(4,4));
		newGame = true;
		guessLog = "<html>";
		guessScore = 0;
		standingsTextPane.setText("");
		showGuesses(guessLog);
		addChatter("[Sensei] Fight over!");		
	}
	
	public void setDiceBoard(String[][] newBoard) {
		newGame = false;
		board.setBoard(newBoard);		
		board.draw();
	}
	
	public void setStopTime(long time) {
		stopTime = time;
		stopTimer.restart();
	}
	
	public String[][] getBlankBoard(int rows, int cols) {
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
	
	public void setGameName(String name) {		
		if (name.length() > 20) 	name = name.substring(20);
		gameNameLabel.setText("Dojo: \""+name+"\"");		
	}
	
	public void updateStandings(Standings currentStandings) {
		StringBuffer text = new StringBuffer();
		
		Collections.sort(currentStandings.Entries);
		Collections.reverse(currentStandings.Entries);
		
		for(StandingEntry e : currentStandings.Entries) {
			text.append("<font color=\"#FFFFFF\"><b>"+e.PlayerName + "</b> " + e.Score + "</font><br>");
		}
		
		standingsTextPane.setText(text.toString());
	}
	
	public void addIncorrectGuess(String guess){		
		guessLog = guessLog+"<font color=\"#FFFFFF\">[0]"+guess.toUpperCase()+"</font><br>";
		showGuesses(guessLog);	
	}	
	
	public void addCorrectGuess(String guess, int score){
		
		guessLog = guessLog+"<font color=\"#FFFFFF\">["+score+"]</font><font color=\"#00FF00\"><b>"+guess.toUpperCase()+"</b></font><br>";
		showGuesses(guessLog);
		guessScore += score;
		addChatter("[Sensei] Your score is "+guessScore);
		visuallyDemonstrateGuesses(score);
	}
	
	public void addInvalidGuess(String guess) {			
		guessLog = guessLog+"<font color=\"#FFFFFF\">[0]</font><font color=\"#FF0000\">"+guess.toUpperCase()+"</font><br>";
		showGuesses(guessLog);
	}
	
	private void showGuesses(String text){
		//Determine whether the scrollbar is currently at the very bottom position.
		JScrollBar vbar = guessesScrollPane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		guessTextArea.setText(text);
		//now scroll if we were already at the bottom.
		if( autoScroll ) guessTextArea.setCaretPosition(guessTextArea.getDocument().getLength() );		
	}
	
	private void updateStopTime(long StopTime) {
		String timeLeft = null;
		long mins, secs;
		long totalSecs = StopTime/1000;
		mins = totalSecs/60;
		secs = totalSecs%60;
		
		if(!newGame)
		{		
			if(mins == 0 && secs <= 10)
			{
				timeLeft = "<html><font color=\"#FF0000\">"+mins+" mins, "+secs+" secs</font>";
			}else{
				timeLeft = mins+" mins, "+secs+" secs";
			}
		}else{
			timeLeft = "<html>New Fight In<br><font color=\"#00FF00\">"+mins+" mins, "+secs+" secs</font>";
		}
		
		if(mins == 0 && secs ==0)
		{
			newGame = !newGame;
		}
		timerLabel.setText(timeLeft);
	}
	
	public void setVisible(boolean visible){
		gameFrame.setVisible(visible);
		standingsTextPane.setText("");
	}
	
	private void visuallyDemonstrateGuesses(int score) {
	
		if(score < 3) {
			gameNameLabel.setIcon(new ImageIcon(getClass().getResource("images/icon_sensei_poor.gif")));
			addChatter("[Sensei] Is that the best you can do? Surely you will lose.");
		} else if(score > 3 && score <= 5) {
			gameNameLabel.setIcon(new ImageIcon(getClass().getResource("images/icon_sensei_good.gif")));
		} else if(score > 5) {
			gameNameLabel.setIcon(new ImageIcon(getClass().getResource("images/icon_sensei_great.gif")));
			addChatter("[Sensei] A mighty attack indeed!");			
		}
	}
	
	public static void main(String[] args){
		gameBoard board = new gameBoard();
		board.setVisible(true);
	}
	
	class HighlightListener implements CaretListener {				
		public void caretUpdate(CaretEvent e) {
			String guess = guessTextfield.getText();
			board.clearHighlights();
			int length = guess.length();
			for(int j=0;j<length;j++)
			{
				board.setHighlight(guess.substring(j,j+1),j+1);
				if (guess.length() >= j+2)
					board.setHighlight(guess.substring(j,j+2),j+1);
			}					
			gameFrame.repaint();
		}
	}
}

