/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

//import MJBoggle.Client.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import MJBoggle.Server.Board;
import MJBoggle.Support.*;

public class LoadTestingAutoClient implements Runnable {
	LoadTestingClient parent;
	ClientConnection connection;
	String serverHost;
	int serverPort;
	String playerName;
	int intendedGame;
	
	// Gloat objects
	private final Random randomObject = new Random();
	private final String[] randomGloating = { "Oh yeah baby, I got one!", "YES! I am the master!", "Good, our first catch of the day!", "Come on baby, papa needs a new pair of galoshes... WOO!" };
	
	// Stuff from game
	GameList gameList;
	Board gameBoard;
	String[][] boardData;
	long gameEndTime;
	
	String gameName;
	
	public LoadTestingAutoClient(LoadTestingClient parent, String host, int port, String name, int intendedGame) {
		this.parent = parent;
		this.serverHost = host;
		this.serverPort = port;
		this.playerName = name;
		this.intendedGame = intendedGame;
	}
	
	public String getGameName() {
		return gameName;
	}
	public String getPlayerName() {
		return playerName;
	}
		
	public void run() {
		DataPacket pkt;
		new Thread(new LoadTestingAutoClientReceiver(parent), Thread.currentThread().getName()+"-helper").start();
		
		try {
			connection = new ClientConnection(serverHost, serverPort);
			connection.connect();
			
			pkt = new PlayerConnect(playerName);
			connection.sendPacket(pkt);

			// Wait for game info
			try {
				TimeUnit.MILLISECONDS.sleep(randomObject.nextInt(3000));
			} catch (InterruptedException e) {}
			
			
			// If we've an intended game, get from it.
			if (intendedGame >= 0 && gameList != null && gameList.GamesRunning.size() > 0) {
				boolean good = false;
				for (GameListEntry e : gameList.GamesRunning) {
					if (e.GameNum == intendedGame) {
						pkt = new Join(intendedGame);
						connection.sendPacket(pkt);
						gameName = e.Name;
						good = true;
						break;
					}
				}
				if (!good) throw new Exception("Could not find intended game!");
			} else {			
			// Pick a game or make a new game			
				for(;;) {
					boolean haveFoundAGame = false;
									
					if (randomObject.nextInt(10)==9) {
						// New Game!
						pkt = new NewGame(playerName+"'s Awesome Game");
						connection.sendPacket(pkt);
						gameName = playerName+"'s Awesome Game";
						break;
					}
					
					// If there's a game in play...
					if (gameList == null || gameList.GamesRunning.size() < 1) 
						continue;
					
					// ...then pick a game
					int pickedGame = randomObject.nextInt(gameList.GamesRunning.size());
									
					for (GameListEntry gle : gameList.GamesRunning) {
						if (gle.GameNum == pickedGame) {
							pkt = new Join(pickedGame);
							connection.sendPacket(pkt);
							gameName = gle.Name;
							haveFoundAGame = true;
							break;
						}
					}
					
					if (haveFoundAGame) 
						break;
					
					try {
						TimeUnit.MILLISECONDS.sleep(randomObject.nextInt(3000));
					} catch (InterruptedException e) {}
				}

			}
			
			// Now in game, in main loop.
			while(parent.keepRunning) {
				if (System.currentTimeMillis() < gameEndTime) {
					pkt = new MakeGuess(parent.getRandomGuess());
					connection.sendPacket(pkt);
				} else {
					
				}
				
				try {
					TimeUnit.SECONDS.sleep(1+randomObject.nextInt(3));
				} catch (InterruptedException e) {}
			}
			
		} catch (IOException e) {
			System.out.println("IOException occured in " + Thread.currentThread().getName() + " " + e);
		} catch (Exception e){
			System.out.println(e);
		} finally {
			try { connection.disconnect(); } catch (Exception e) { ; }
			System.out.println(Thread.currentThread().getName()+" Done!");
		}
	}

	class LoadTestingAutoClientReceiver implements Runnable {
		LoadTestingClient parent;
		public LoadTestingAutoClientReceiver(LoadTestingClient parent) {
			this.parent = parent;
		}
		public void run() {
			DataPacket pkt;
			
			// Sleep for a moment to ensure everything sets up properly.
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {}
			
			while(parent.keepRunning) {
				pkt = connection.getPacket();
				
				if (pkt == null) 
					continue;
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMELIST)) {
					gameList = (GameList)pkt;						
				} else if (pkt.PacketType.equals(DataPacket.Type.GAMESTAT)) {
					String[][] data = ((GameStat)pkt).BoardData;
					boardData = data;					 
					gameEndTime = System.currentTimeMillis() + ((GameStat)pkt).TimeLeft;
				} else if (pkt.PacketType.equals(DataPacket.Type.GAMEDONE)) {
					boardData = null;
				} else if (pkt.PacketType.equals(DataPacket.Type.GUESSACK)) {
					if (((GuessAck)pkt).ResponseType == GuessAck.WordIsRealAndExists) {
						// WOOHOO! WE GOT ONE! Let's brag!
						pkt = new Chatter(randomGloating[randomObject.nextInt(randomGloating.length)]);
						connection.sendPacket(pkt);
					}
					
				}
				
			}			
		}
	}

}
