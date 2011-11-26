/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import MJBoggle.Support.*;

/**
 * class Game
 * 
 */

public class Game implements Runnable {

	/** Attributes: */
	private final String Name;
	private final int GameNum;

	private final ScoreBoard Scores;
	private final Board Board;
	
	private final ArrayList<Connection> Connections;
	private final ArrayBlockingQueue<DataPacket> RepeaterQueue;
	
	private long StopTime;
	private boolean isRunning;
	private long SuicideTime;
	private boolean shuttingDown;
	private long StandingsNextTime;

	/** Public methods: */
	public Game(String gameName, int gameNum) {
		Name = gameName;
		GameNum = gameNum;
	
		Connections = new ArrayList<Connection>();
		RepeaterQueue = new ArrayBlockingQueue<DataPacket>(50);

		Scores = new ScoreBoard();
		Board = new Board();

		isRunning = false;
		shuttingDown = false;
		
		// It looks like I need both of these stoptime sets.
		StopTime = Defaults.getInstance().getNewGameDelaySeconds()*1000+System.currentTimeMillis();

		new Thread(this, "GameID"+GameNum).start();
		new Thread(new GenericRepeater(), "GameID"+GameNum+"-Repeater").start();

		System.out.println(Name + "] New Game created: " + gameName);
		
		// Re-set stop time below, it seems important.
		setupNewGame();
		StopTime = Defaults.getInstance().getNewGameDelaySeconds()*1000+System.currentTimeMillis();
	}
	
	public int getGameNum() {
		return GameNum;
	}
	public int getGameNumPlayers() {
		synchronized(Connections) {
			return Connections.size();
		}
	}
	public String getGameName() {
		return Name;
	}
	public int getGameSecondsLeft() {
		return (int)(StopTime - System.currentTimeMillis())/1000;
	}
	public String[] getPlayerNameArray() {		
		synchronized(Connections) {
			String PlayerNames[] = new String[Connections.size()];
			for(int i=0; i<Connections.size(); i++) {
				PlayerNames[i] = Connections.get(i).getName();
			}
			return PlayerNames;
		}
	}
	public boolean isAlive() {
		return !shuttingDown;
	}
	
	public void playerJoins(Connection TheConnection) {
		synchronized(Connections) {
			Connections.add(TheConnection);
		}
		
		// We have a player! No more suiciding!
		SuicideTime = 0;
		
		if (TheConnection.observing()) {
			new Thread(new ObserverManager(TheConnection), "Observer-"+TheConnection).start();
		} else {
			// Alert other players
			Chatter chat = new Chatter("Player " + TheConnection.getName() + " has joined the fight.");
			chat.senderName = "Dojo";
			chat.timeStamp = System.currentTimeMillis();
		
			// Enqueue even if we get interrupted...
			for(;;) {
				try {
					RepeaterQueue.put(chat);
					break;
				} catch (InterruptedException e) {}
			}
		}

		// Only send Board data if game is running.
		DataPacket pkt = new GameStat(StopTime-System.currentTimeMillis(), getBoardData());
		TheConnection.sendPacket(pkt);
	}

	public void playerLeaves(Connection TheConnection) {
		synchronized(Connections) {
			Connections.remove(TheConnection);
			
			// Note that the last player left so let's now set our suicide time.
			if (Connections.size() == 0) {
				SuicideTime = System.currentTimeMillis() + Defaults.getInstance().getGamePersistSeconds()*1000; 
			}
		}
		
		TheConnection.setGameNum(0);		
	}
	
	public void setupNewGame() {
		Board.randomize();
		Board.beginWordSearch();
	}

	public void newGame() {
		// Resets timers and such to start a new round
		isRunning = true;
		// Resets the scores
		Scores.nextGame();		
		
		StopTime = Defaults.getInstance().getGameLengthSeconds()*1000+System.currentTimeMillis();
		StandingsNextTime = Defaults.getInstance().getStandingsDelaySeconds()*1000+System.currentTimeMillis();
		
		//Send whatever board data is available.
		DataPacket pkt = new GameStat(StopTime-System.currentTimeMillis(), getBoardData());

		// Enqueue even if we get interrupted...
		for(;;) {
			try {
				RepeaterQueue.put(pkt);
				break;
			} catch (InterruptedException e) {}
		}
	}

	public void endGame() {
		isRunning = false;
		StopTime = Defaults.getInstance().getNewGameDelaySeconds()*1000+System.currentTimeMillis();
		
		// Sends out game end packets, is run when time runs out.
		IndividualScores score;
		Standings standings = Scores.getStandings();

		synchronized(Connections) {
			for(Connection conn : Connections) {
				score = Scores.getScores(conn.getPlayer());
				DataPacket pkt = new GameDone(score, standings);
				conn.sendPacket(pkt);
			}
		}
		
		// Send blank board data.
		DataPacket pkt = new GameStat(StopTime-System.currentTimeMillis(), getBoardData());

		// Enqueue even if we get interrupted...
		for(;;) {
			try {
				RepeaterQueue.put(pkt);
				break;
			} catch (InterruptedException e) {}
		}		
		
		setupNewGame();		
	}

	public void shutdown() {
		shuttingDown = true;
		
		synchronized(Connections) {
			for(Connection conn : Connections) {
				conn.setGameNum(0);
			}
		}
	}
	
	private String[][] getBoardData() {
		if (isRunning)
			return Board.getData();
		
		return Board.getPlaceholderData();
	}
	
	private void handlePacket(DataPacket pkt, Connection conn) {
		/* Game handles the following packets:
		 * 1) MakeGuess
		 * 2) Leave
		 * 3) There is no 3 */
		
		if (pkt.PacketType.equals(DataPacket.Type.MAKEGUESS)) {
			String guess = ((MakeGuess)pkt).Guess;
			
			if (!isRunning) return;
			
			// Determine results of guess
			boolean exists = Board.isWordWithin(guess);
			boolean real = Dictionary.getInstance().isWord(guess);
			
			boolean result = exists && real;
			
			// Record the overall result
			int points = Scores.recordGuess(conn.getPlayer(), guess, result);
			
			// Determine the acknowledgement type
			int ackType = 0;
			if (exists && real)
				ackType = GuessAck.WordIsRealAndExists;
			else if (!exists && real)
				ackType = GuessAck.WordIsReal;
			else
				ackType = GuessAck.WordIsNonsense;
			
			// Send the acknowledgement 
			conn.sendPacket(new GuessAck(guess, ackType, points));
			
			return;
		}
		if (pkt.PacketType.equals(DataPacket.Type.LEAVE)) {
			playerLeaves(conn);			
			return;
		}
		if (pkt.PacketType.equals(DataPacket.Type.CHATTER)) {
			// Add player's name to the chatter
			((Chatter)pkt).senderName = conn.getName();
			// Filter the profanity
			((Chatter)pkt).text = Host.makeNameWithoutProfanity(((Chatter)pkt).text);
			// Set to server's time
			((Chatter)pkt).timeStamp = System.currentTimeMillis();
			
			// Send it to everyone, fail silently if interrupted!!
			try {
				RepeaterQueue.put((Chatter)pkt);
			} catch (InterruptedException e) {}
			
			return;
		}
		
	}
	
	public void run() {
		LinkedList<Connection> connQueue = new LinkedList<Connection>();
		LinkedList<DataPacket> pktQueue = new LinkedList<DataPacket>();
		
		while(!shuttingDown) {
			/* When a player joins or leaves we have to add/remove Connections and we do that from
			 * handlePacket, so we must not be iterating through Connections at the time. So thus we
			 * have to do this in two stages: get all the packets from all the connections, then
			 * handle them.
			 */
			synchronized(Connections) {
				// Ask each connection for a new packet
				for(Connection conn : Connections) {
					DataPacket pkt = conn.getPacket();
					
					if (pkt != null) {
						connQueue.offer(conn);
						pktQueue.offer(pkt);
					}
				}
			}
			
			while (pktQueue.size() > 0) {
				DataPacket pkt = pktQueue.poll();
				Connection conn = connQueue.poll();
				handlePacket(pkt, conn);
			}
			
			assert connQueue.size() == 0 && pktQueue.size() == 0 : "Queues did not empty in Game::Run";
			
			if (System.currentTimeMillis() > StopTime) {
				if (isRunning) {
					// Time to stop!
					endGame();
				} else {
					// Time to start!
					newGame();				
				}				
			}
			
			// If our SuicideTime is set and we've exceeded it, kill ourselves.
			if (SuicideTime > 0 && System.currentTimeMillis() > SuicideTime) {
				shutdown();
			} else if (System.currentTimeMillis() > StandingsNextTime && isRunning) {
				StandingsNextTime = System.currentTimeMillis() + Defaults.getInstance().getStandingsDelaySeconds()*1000;
				try {
					RepeaterQueue.put(new GameStandings(Scores.getStandings()));
				} catch (InterruptedException e) {}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}

	}
	
	class GenericRepeater implements Runnable {		
		public void run() {
			while(!shuttingDown) {
				DataPacket pkt = RepeaterQueue.poll();
				
				if (pkt == null) 
					continue;
				synchronized(Connections) {
					for(Connection conn : Connections) {
						conn.sendPacket(pkt);
					}
				}
			}
		}
	}
	
	class ObserverManager implements Runnable {
		private Connection connection;
		
		public ObserverManager(Connection conn) {
			connection = conn;
		}
		public void run() {
			Guesses guesses;
			GameGuesses pkt;
			int i = 0;
			
			while(connection.alive() && !shuttingDown && connection.getGameNum() == GameNum) {
				if (++i > 3) {
					// Send updated long word list,
					guesses = Scores.getLongestWords();
					pkt = new GameGuesses(guesses, DataPacket.Type.LONGGUESSES);
					connection.sendPacket(pkt);
					i = 0;
				}
				
				// Send updated dupe word list
				guesses = Scores.getRecentDupes();
				pkt = new GameGuesses(guesses, DataPacket.Type.DUPEGUESSES);
				connection.sendPacket(pkt);
				
				try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { ; }
			}
		}
	}

}
