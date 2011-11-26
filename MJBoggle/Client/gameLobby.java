/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

//import MJBoggle.Client.Client.ClientWorker;
//import MJBoggle.Support.Connect;
//import MJBoggle.Support.DataPacket;
//import MJBoggle.Support.GameList;
import MJBoggle.Support.GameListEntry;
import MJBoggle.Support.Join;
import MJBoggle.Support.NewGame;

/**
 * @author Mikel Mazlaghani
 */
public class gameLobby  {

	//Interface
	protected JFrame frame;
	protected JMenuBar menuBar;
	protected JMenu gameMenu;
	protected JMenuItem newMenuItem;
	protected JMenuItem exitMenuItem;
	protected JScrollPane gameListScrollPane;
	protected JList gameList;
	protected JLabel gameListLabel;
	protected JPanel buttonPane;
	
	protected JButton joinButton;
	protected JButton newButton;

	protected GameListEntry selectedGame = null;
	protected int selectedIndex = 0;
	
	public gameLobby() {
		initComponents();		
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
		//frame.setSize(450, 400);
	}
	
	public void setGameList(ArrayList<GameListEntry> updatedGameList){	
		/*
		for(int x=0; x < updatedGameList.size();x++)
		{
			GameListEntry game = updatedGameList[x];
			if(game.NumPlayers <= 0)
			{
								
			}
		}*/
		selectedGame = (GameListEntry)gameList.getSelectedValue();
		selectedIndex = gameList.getSelectedIndex();	
		
		if(updatedGameList.size() > 0)
		{
			gameList.setModel(new JListGameList(updatedGameList));
//			System.out.println("Receiving game list update of size "+updatedGameList.size());
//			System.out.println(updatedGameList);
		}else{
			ArrayList<GameListEntry> alice = new ArrayList<GameListEntry>();
			gameList.setModel(new JListGameList(alice));			
		}
		
		gameList.setSelectedIndex(selectedIndex);
		gameList.setSelectedValue(selectedGame, true);
	}
	
	protected void initComponents() {

		//Interface init
		frame = new JFrame();
		menuBar = new JMenuBar();
		gameMenu = new JMenu();
		newMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		gameListScrollPane = new JScrollPane();
		gameList = new JList();
		gameListLabel = new JLabel();
		joinButton = new JButton();
		newButton = new JButton();

		
		
		//======== frame ========
		{
			frame.setTitle("Ninja Boggle Dojos");
			//frame.setSize(450, 400);
			//frame.setResizable(false);
			frame.setAlwaysOnTop(false);
			frame.setForeground(Color.white);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			Container frameContentPane = frame.getContentPane();
			frameContentPane.setBackground(Color.black);
						
			frame.addWindowListener(
		            new WindowAdapter()
		            {
		                public void windowClosing(WindowEvent e)
		                {
		            			fleeActionPerformed(null);
		                }
		           });
			
			//======== menuBar ========
			{
				menuBar.setBackground(Color.black);
				menuBar.setForeground(Color.white);
				
				//======== gameMenu ========
				{
					gameMenu.setText("Game");
					gameMenu.setForeground(Color.white);
					gameMenu.setBackground(Color.black);
					
					//---- newMenuItem ----
					newMenuItem.setText("New Dojo");
					newMenuItem.setSelected(true);
					newMenuItem.setBackground(Color.black);
					newMenuItem.setForeground(Color.white);
					newMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							newDojoActionPerformed(e);
						}
					});
					gameMenu.add(newMenuItem);
					
					//---- exitMenuItem ----
					exitMenuItem.setText("Flee [Exit]");
					exitMenuItem.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							fleeActionPerformed(e);
						}					
					});
					gameMenu.add(exitMenuItem);
				}
				menuBar.add(gameMenu);
			}
			frame.setJMenuBar(menuBar);
			
			//======== gameListScrollPane ========
			{
				gameListLabel.setBackground(Color.black);
				gameListLabel.setForeground(Color.white);
				gameListLabel.setText("");
				gameListLabel.setIcon(new ImageIcon(getClass().getResource("images/icon_dojo_list.jpg")));
				
				gameListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				gameListScrollPane.setViewportBorder(new EmptyBorder(2, 2, 2, 2));
				
				//---- gameList ----
				gameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				gameList.setVisibleRowCount(10);
				gameList.setForeground(Color.white);
				gameList.setBackground(Color.black);
				gameListScrollPane.setViewportView(gameList);
				
				gameList.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						String players = "";
						if((GameListEntry)gameList.getSelectedValue() != null)
						{
							String[] playerList = ((GameListEntry)gameList.getSelectedValue()).PlayerNames;
							players = "Players in " + ((GameListEntry)gameList.getSelectedValue()).Name + ": ";
							for(int x= 0;x<playerList.length;x++)
							{
								players = players+playerList[x];
								if(x < (playerList.length-1))
								{
									players = players+", ";
								}
							}
							gameList.setToolTipText(players);	
						}
					}
				});
			}
						
			//---- joinButton ----
			joinButton.setText("Join Dojo");
			joinButton.setBackground(Color.black);
			joinButton.setForeground(Color.white);
			joinButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					joinDojoActionPerformed(e);
				}
			});
			
			
			//---- newButton ----
			newButton.setText("New Dojo");	
			newButton.setBackground(Color.black);
			newButton.setForeground(Color.white);
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newDojoActionPerformed(e);
				}
			});
			
			// Add to the layout			

			JPanel listPane = new JPanel();
			listPane.setBackground(Color.black);
			listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
			
			buttonPane = new JPanel();
			
			buttonPane.setBackground(Color.black);
			buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			
			gameListScrollPane.setMinimumSize(new Dimension(200, 300));

			JLabel list = new JLabel("Player list is a tool tip.");
			list.setForeground(Color.white);
			list.setHorizontalAlignment(JLabel.CENTER);
			list.setVerticalAlignment(JLabel.TOP);
			
			listPane.add(gameListLabel);
			listPane.add(Box.createRigidArea(new Dimension(200,5)));
			listPane.add(gameListScrollPane);
			buttonPane.add(joinButton);
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(list);
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(newButton);

			frameContentPane.add(listPane, BorderLayout.CENTER);			
			frameContentPane.add(buttonPane, BorderLayout.PAGE_END);
		}
		
		frame.pack(); // Make the frame be the right size

		System.out.println("Finished building gameLobby interface.");
	}

	protected void newDojoActionPerformed(ActionEvent e) {
		Client client = Client.getInstance();
		
		// Ask the player for the game name
		String gameName = JOptionPane.showInputDialog(null,
			    "What name would you like for your new dojo?",
			    "New Dojo Name",
			    JOptionPane.QUESTION_MESSAGE);		
		
		if(gameName != null && gameName.trim().compareTo("") != 0)
		{
			NewGame pkt = new NewGame(gameName);
			client.getConnection().sendPacket(pkt);
			client.getBoardWindow().setGameName(gameName);					
			client.getLobbyWindow().setVisible(false);
			client.getBoardWindow().setVisible(true);
			client.getBoardWindow().resetGame();
		}
	}
	
	protected void fleeActionPerformed(ActionEvent e) {
		Client.getInstance().shutdown();
	}
	
	protected void joinDojoActionPerformed(ActionEvent e) {
		Client client = Client.getInstance();
		
		selectedGame = (GameListEntry)gameList.getSelectedValue();
		
		if(selectedGame != null)
		{
			// Join the selected game...
			System.out.println("Game num: "+selectedGame.GameNum);
			Join pkt = new Join(selectedGame.GameNum);
			
			//Show the game, hide this window
			client.getBoardWindow().setVisible(true);
			client.getBoardWindow().setGameName(selectedGame.Name);
			setVisible(false);
			
			client.getConnection().sendPacket(pkt);							
		}
	}
	
	class JListGameList implements ListModel {
		
		protected ArrayList<GameListEntry> gameActiveList;
		
		public JListGameList(ArrayList<GameListEntry> list) {
			gameActiveList = list;
		}
		
		public GameListEntry getElementAt(int index){			
			return(gameActiveList.get(index));
		}
		
		public int getSize(){
			return(gameActiveList.size());
		}
		//Abstract methods
		public void addListDataListener(ListDataListener l){}
		public void removeListDataListener(ListDataListener l){}
	}
}
