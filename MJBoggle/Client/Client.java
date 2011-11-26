/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.io.*;
//import java.util.ArrayList;

import MJBoggle.Support.Chatter;
import MJBoggle.Support.DataPacket;
import MJBoggle.Support.GameDone;
import MJBoggle.Support.GameList;
//import MJBoggle.Support.GameListEntry;
import MJBoggle.Support.GameStandings;
import MJBoggle.Support.GameStat;
import MJBoggle.Support.GuessAck;

public class Client {

	//Singleton
	static private Client instance;
	
	//Data
	//private ArrayList<GameListEntry> gameList = null;
	
	//Network
	private ClientConnection connection;
	
	//Windows
	private gameLogin LoginWindow;
	private gameLobby LobbyWindow;
	private gameBoard BoardWindow;
	
	//Constructor
	private Client() {
				
		//Instantiate Game Windows
		BoardWindow = new gameBoard();
		LoginWindow = new gameLogin();		
		LobbyWindow = new gameLobby();
		
	}

	//Singleton method
	public static Client getInstance() {
		if(Client.instance==null)
		{
			Client.instance = new Client();			
		}
		return(Client.instance);
	}
	
	public boolean makeConnection(String hostname, int port) {		
		connection = new ClientConnection(hostname, port);
		try {
			connection.connect();
		} catch (IOException e) {
			System.out.println("Failed to connect: "+e);
			return false;
		}	
		
		//Run client worker
		new Thread(new ClientWorker(), "ClientWorker").start();

		return true;
	}
	
	public ClientConnection getConnection() {
		return(connection);		
	}
	
	public gameBoard getBoardWindow() {
		return(BoardWindow);
	}
	
	public gameLobby getLobbyWindow() {
		return(LobbyWindow);
	}
	
	public gameLogin getLoginWindow() {
		return(LoginWindow);
	}

	public static void main(String[] args) {
		Client.getInstance();
		//Client client = Client.getInstance();
		//client.getLoginWindow().setVisible(true);
	}
	
	public void shutdown() {
		try {
			connection.disconnect();
		} catch (Exception e) {}
		System.exit(0);
	}
	
	class ClientWorker implements Runnable {
		public void run() {			
			for(;;) {
				DataPacket pkt = connection.getPacket();
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMELIST)) {
					getLobbyWindow().setGameList(((GameList)pkt).GamesRunning);
				} 			
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMESTAT)) {
					BoardWindow.setStopTime(((GameStat)pkt).TimeLeft);
					System.out.println(connection.reportOnQueues());

					// Update board data
					BoardWindow.setDiceBoard(((GameStat)pkt).BoardData);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GUESSACK)) {
					if (((GuessAck)pkt).ResponseType == GuessAck.WordIsRealAndExists) {
						System.out.println("Guess was correct");
						BoardWindow.addCorrectGuess(((GuessAck)pkt).GuessedWord,((GuessAck)pkt).Score);
					} else if (((GuessAck)pkt).ResponseType == GuessAck.WordIsReal) {
						System.out.println("Guess was incorrect");
						BoardWindow.addIncorrectGuess(((GuessAck)pkt).GuessedWord);
					} else {
						System.out.println("Guess not a valid word");
						BoardWindow.addInvalidGuess(((GuessAck)pkt).GuessedWord);
					}
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMEDONE)) {
					System.out.println("Game finished");
					BoardWindow.resetGame();
					BoardWindow.updateStandings(((GameDone)pkt).Standings);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMESTANDINGS)) {
					BoardWindow.updateStandings(((GameStandings)pkt).Standings);
				}
				
				
				if (pkt.PacketType.equals(DataPacket.Type.CHATTER)) {
						BoardWindow.addChatter("["+((Chatter)pkt).senderName + "] " + ((Chatter)pkt).getTimestampString() + ": " + ((Chatter)pkt).text);
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}			
		}
	}	
}