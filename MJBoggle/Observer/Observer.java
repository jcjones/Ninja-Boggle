/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Observer;

import java.io.IOException;

import MJBoggle.Support.DataPacket;
import MJBoggle.Support.GameDone;
import MJBoggle.Support.GameGuesses;
import MJBoggle.Support.GameList;
import MJBoggle.Support.GameStandings;
import MJBoggle.Support.GameStat;

public class Observer {

	//Singleton
	static private Observer instance;
	
	//Data
	//private ArrayList<GameListEntry> gameList = null;
	
	//Network
	private ObserverConnection connection;
	
	//Windows
	private ObserverLogin LoginWindow;
	private ObserverLobby LobbyWindow;
	private ObserverGame GameWindow;
	
	//Constructor
	private Observer() {
				
		//Instantiate Game Windows
		GameWindow = new ObserverGame();
		LoginWindow = new ObserverLogin();		
		LobbyWindow = new ObserverLobby();
		
	}

	//Singleton method
	public static Observer getInstance() {
		if(Observer.instance==null)
		{
			Observer.instance = new Observer();			
		}
		return(Observer.instance);
	}
	
	public boolean makeConnection(String hostname, int port) {		
		connection = new ObserverConnection(hostname, port);
		try {
			connection.connect();
		} catch (IOException e) {
			System.out.println("Failed to connect: "+e);
			return false;
		}	
		
		//Run client worker
		new Thread(new ObserverWorker(), "ObserverWorker").start();

		return true;
	}
	
	public ObserverConnection getConnection() {
		return(connection);		
	}
	
	public ObserverGame getGameWindow() {
		return(GameWindow);
	}
	
	public ObserverLobby getLobbyWindow() {
		return(LobbyWindow);
	}
	
	public ObserverLogin getLoginWindow() {
		return(LoginWindow);
	}

	public static void main(String[] args) {
		Observer.getInstance();
	}
	
	public void shutdown() {
		try {
			connection.disconnect();
		} catch (Exception e) {}
		System.exit(0);
	}
	
	class ObserverWorker implements Runnable {
		public void run() {			
			for(;;) {
				DataPacket pkt = connection.getPacket();
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMELIST)) {
					getLobbyWindow().setGameList(((GameList)pkt).GamesRunning);
				} 			
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMESTAT)) {
					GameWindow.setStopTime(((GameStat)pkt).TimeLeft);
					System.out.println(connection.reportOnQueues());

					// Update board data
					GameWindow.setDiceBoard(((GameStat)pkt).BoardData);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMEDONE)) {
					System.out.println("Game finished");
					GameWindow.resetGame();
					GameWindow.updateStandings(((GameDone)pkt).Standings);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMESTANDINGS)) {
					GameWindow.updateStandings(((GameStandings)pkt).Standings);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.LONGGUESSES)) {
					GameWindow.updateLongGuesses(((GameGuesses)pkt).Guesses);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.DUPEGUESSES)) {
					GameWindow.updateDupesGuesses(((GameGuesses)pkt).Guesses);
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}			
		}
	}	
}