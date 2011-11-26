/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import MJBoggle.Support.*;

public class ConsoleClient {
	private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private ClientConnection connection;
    private String playerName;
    
    private ArrayList<GameListEntry> gameList = null;
	private String[][] boardData;
	private long stopTime;
	private IndividualScores individualScores;
	private Standings standings;
	
	private boolean chatterOn = true;
    
	public ConsoleClient(String ip, int port) {
		connection = new ClientConnection(ip, port);	
	}
	
	public String getConsoleLine() {
		try {
			return input.readLine();
		} catch (IOException e) {
			System.out.println("Error reading from console: " + e);
		}
		return "";
	}
	
	public void go() {
		System.out.println("Connecting...");
		
		try {
			connection.connect();
		} catch (IOException e) {
			System.out.println("Failed to connect: "+e);
			return;
		}
		
		new Thread(new ClientWorker(), "ClientWorker").start();
		
		System.out.println("Connected! What name would you like to use?");
		playerName = getConsoleLine();		
		
		DataPacket pkt = new PlayerConnect(playerName);
		connection.sendPacket(pkt);
		
		System.out.println("Report: " + connection.reportOnQueues());
		
		gameBrowser();				
	}
	
	private void gameBrowser() {
		DataPacket pkt;
		String line;		

		for(;;) {
			System.out.print("> ");
			line = getConsoleLine();
			if (line.length() < 1) continue;
			
			switch(line.charAt(0)) {
			case '?':
				System.out.println("Help:\n!: Quit\nq: Quit\nnumber: Join game\n.: Show game list again");
				break;
			case '!':
			case 'q':
				return;
			case '.':
				if (gameList == null ) break;
				System.out.println("Num GameName\t Number of Players");
				for(GameListEntry e : gameList) {
					System.out.println(e.GameNum + "] " + e.Name + "\t" + e.NumPlayers);
				}
				break;
			case 'n':
				System.out.print("New game name?: ");
				line = getConsoleLine();
				stopTime = 0;
				pkt = new NewGame(line);
				connection.sendPacket(pkt);
				playGame();
				break;
			default:
				int gameNum;
				try {
					gameNum = Integer.parseInt(line);
				} catch (IllegalArgumentException e) {
					continue;
				}
				stopTime = 0;
				pkt = new Join(gameNum);
				connection.sendPacket(pkt);
				// NOTE: If gameNum isn't valid, we enter a bad state here...
				playGame();				
			}

		}
	}
	
	private void playGame() {
		DataPacket pkt;
		String line;
						
		while(stopTime < 1) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {}
		}
		
		showBoard(boardData);
		
		for(;;) {
			int secondsLeft = (int)(stopTime - System.currentTimeMillis())/1000;
			System.out.print(secondsLeft + "> ");
			line = getConsoleLine();
			
			if (line.length() < 1) continue;
			
			switch(line.charAt(0)) {
			case '?':
				System.out.println("Help:\n!: Quit\n.: Show Board\n\' [txt]: Send chat message\nm: Mute chat\n$: Show your scores from last game\n%: Show current standings");
				break;
			case '!':
				pkt = new Leave();
				connection.sendPacket(pkt);
				return;
			case '.':
				showBoard(boardData);
				break;
			case '$':
				if (individualScores == null ) break;
				System.out.println("Your scores from last game: ");
				for (Guess g : individualScores.Guesses) {
					System.out.println(g.Word + " got you " + g.Score + " points.");
				}				
				break;
			case 'm':
				chatterOn = !chatterOn;
				break;
			case '\'':
				if (line.length() > 1) {
					pkt = new Chatter(line.substring(1).trim());
					connection.sendPacket(pkt);
				}
				// Speak
				break;
			case '%':
				if (standings == null ) break;
				System.out.println("Current standings: ");
				for (StandingEntry e : standings.Entries) {
					System.out.println(e.PlayerName + "\t" + e.Score);
				}
				break;
			default:
				// Make guess
				pkt = new MakeGuess(line);
				connection.sendPacket(pkt);
				break;
			}
			
		}
		
	}
	
	private void showBoard(String[][] boardData) {
		System.out.println("\n\nBoard:");
		for (int i=0; i<boardData.length; i++) {
			for (int j=0; j<boardData[i].length; j++) {
				System.out.print(boardData[i][j]+" ");
			}
			System.out.println();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Arguments: [ip address]:[port]");
			return;
		}
		String line[] = args[0].split(":");
		
		new ConsoleClient(line[0], Integer.parseInt(line[1])).go();
	}
	
	
	class ClientWorker implements Runnable {
		public void run() {			
			for(;;) {
				DataPacket pkt = connection.getPacket();
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMELIST)) {
					gameList = ((GameList)pkt).GamesRunning;
				} 			
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMESTAT)) {
					stopTime = System.currentTimeMillis()+((GameStat)pkt).TimeLeft;
					System.out.println(connection.reportOnQueues());

					// Update board data
					boardData = 	((GameStat)pkt).BoardData;
					
					showBoard(boardData);
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMESTANDINGS)) {
					standings = ((GameStandings)pkt).Standings;
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GUESSACK)) {
					if (((GuessAck)pkt).ResponseType == GuessAck.WordIsRealAndExists) {
						System.out.println("Guess was correct: " + ((GuessAck)pkt).Score);
					} else if (((GuessAck)pkt).ResponseType == GuessAck.WordIsReal) {
						System.out.println("Guess was incorrect");
					} else {
						System.out.println("Guess not a valid word");
					}
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.GAMEDONE)) {
					System.out.println("Game finished");
					
					individualScores = ((GameDone)pkt).IndividualScores;
					standings = ((GameDone)pkt).Standings;
				}
				
				if (pkt.PacketType.equals(DataPacket.Type.CHATTER)) {
					if (chatterOn)
						System.out.println(((Chatter)pkt).senderName + "[" + ((Chatter)pkt).getTimestampString() + "]: " + ((Chatter)pkt).text);
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				
			}			
		}
	}

}
