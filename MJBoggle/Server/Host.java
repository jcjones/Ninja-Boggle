/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MJBoggle.Support.*;

/**
 * class Host
 * 
 */

public class Host implements Runnable{

	/** Attributes: */
	private ArrayList<Connection> Connections = new ArrayList<Connection>();
	private ArrayList<Game> Games = new ArrayList<Game>();
	private ServerSocket LocalSocket;
	private int LocalPort;
	private boolean accepting;
	private int idNumber;
	private Thread gameBrowserManager;
	
	private static ArrayList<Pattern> profanityRegexList = new ArrayList<Pattern>();
	
	// Master work queue for all initialConnectionHandler objects	
	public static final ExecutorService ThreadPool = Executors.newCachedThreadPool();
	
	/** Public methods: */
	public Host(int Port) throws IOException {	
		LocalPort = Port;
		LocalSocket = new ServerSocket(LocalPort);
		
		accepting = true;
		
		idNumber = 1;
		
		loadProfanityRegexList(this.getClass().getClassLoader().
				getResourceAsStream("MJBoggle/Contrib/profanity.list"));
		
		Dictionary.getInstance();
		
		gameBrowserManager = new Thread(this, "BrowserManager");
		gameBrowserManager.start();
		
		waitForConnections();
	}

	public static void main(String[] args) throws IOException {
		int port = Defaults.getInstance().getPort();
		
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		
		System.out.println("MJBoggle Server starting on port " + port);
		System.out.println("=======================================================");
		new Host(port);
	}

	public Game newGame(String GameName) {
		Game ret = new Game(makeAcceptableGameName(GameName), idNumber++);
		synchronized(Games) {
			Games.add(ret);
		}
		return ret;
	}
	
	public Game getGame(int num) {
		synchronized(Games) {
			for(Game game : Games) {
				if (game.getGameNum() == num)
					return game;
			}
		}
		return null;
	}
	
	private void loadProfanityRegexList(InputStream data) {
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(data));
			String line;
			
			int count = 0;
			while (true) {
				line = fin.readLine();
				
				if (line == null || line.length() < 1)
					break;
				
				profanityRegexList.add(Pattern.compile(line, Pattern.CASE_INSENSITIVE));
				count++;
							
			}
			
			System.out.println("Profanity List:: Loaded " + count + " words into the list.");
			fin.close();
		} catch (IOException e) {
			System.out.println("Failed to load Profanity List. Working without one. Error: " + e);
		}		
	}
	
	// Game names should be unique, so if it's not, let's add a tag to the end of it
	private String makeUniqueGameName(String in) {
		if (in == null || in.length() < 1)
			in = new String("unnamed game");

		int id = 0;
		String myNewName = in;
		boolean hit = false;
		
		
		// In a loop, search for a "match" which, if found, prompts another run of the loop with id++.
		for (;;) {
			hit = false;
			
			if (id > 0) {
				myNewName = in + "-" + id;
			}
			
			synchronized(Games) {
				for(Game game : Games) {
					String name = game.getGameName();
					
					if (myNewName.equals(name)) {	
						// It's already taken, let's add a value and try again...
						id++;
						hit = true;
						break;
					}
				}
			}
			if (!hit)			
				return myNewName;
		}
	}
		
	// Player names should be unique also, so if they aren't, add a tag to the end.
	private String makeUniquePlayerName(String in) {
		if (in == null || in.length() < 1)
			in = new String("Anonymous Coward");

		int id = 0;
		String myNewName = in;
		boolean hit = false;
		
		// Disallow restricted names used by the game
		if (myNewName.equals("Sensei") || myNewName.equals("Dojo"))
			id++;
		
		// In a loop, search for a "match" which, if found, prompts another run of the loop with id++.		
		for (;;) {
			hit = false;
			
			if (id > 0) {
				myNewName = in + "-" + id;
			}
			
			synchronized(Connections) {
				for(Connection conn : Connections) {					
					String name = conn.getPlayer().getName();
					if (myNewName.equals(name)) {
						// It's already taken, let's add a value and try again...
						id++;
						hit = true;
						break;

					}
				}
			}
			if (!hit)			
				return myNewName;

		}
	}

	// This is where we filter profanity
	public static String makeNameWithoutProfanity(String in) {
		for(Pattern regex : profanityRegexList)
		    in = regex.matcher(in).replaceAll("pony");
		return in;
	}
	
	// Return a game name that isn't a duplicate and that doesn't have profanity.
	private String makeAcceptableGameName(String in) {
		in = makeUniqueGameName(in);
		return makeNameWithoutProfanity(in);
	}

	// Return a player name that isn't a duplicate and that doesn't have profanity.
	private String makeAcceptablePlayerName(String in) {
		in = makeUniquePlayerName(in);
		return makeNameWithoutProfanity(in);
	}
	
		
	public void run() {
		// Give all clients the gameslist 
		
		ArrayList<Connection> defunctConnections = new ArrayList<Connection>();
		ArrayList<Game> defunctGames = new ArrayList<Game>();
				
		for(;;) {
			/* Slow down execution */
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {}
						
			/* Common case: no connections are loitering outside of a game, so let's check that case first */
			boolean loiteringPlayers = false;
			synchronized(Connections) {
				for(Connection conn : Connections) {
					if (conn != null && !conn.alive()) {
						defunctConnections.add(conn);
					} else if (conn != null && conn.getGameNum() == 0) {
						loiteringPlayers = true;
						break;
					}
				}
			}
			
			//System.out.println(Thread.currentThread().getName()+" Loitering? " + loiteringPlayers);
			
			/* If no one is loitering, just go sleep again */
			if (!loiteringPlayers)
				continue;
			
			GameList list = new GameList();
			
			synchronized(Games) {
				for(Game game : Games) {
					if (game.isAlive())
						list.add(new GameListEntry(game.getGameName(), game.getGameNum(), game.getGameNumPlayers(), game.getGameSecondsLeft(), game.getPlayerNameArray()));
					else
						defunctGames.add(game);
				}
			}
			
			//System.out.println("Games listed: " + list.GamesRunning.size());
						
			/* Tell those clients about the games open */
			synchronized(Connections) {
				for(Connection conn : Connections) {
					if (conn != null && conn.getGameNum() == 0) {
						//System.out.println(Thread.currentThread().getName()+" queuing to " + conn);
						conn.sendPacket(list);
					}
				}
			}

			/* Remove defunct connections and games */
			if (defunctConnections.size() > 0) {
				synchronized(Connections) {
					for(Connection defunctConn : defunctConnections) {
						Connections.remove(defunctConn);
					}
				}
				defunctConnections.clear();
			}
			if (defunctGames.size() > 0) {
				synchronized (Games) {
					for(Game defunctGame : defunctGames) {
						Games.remove(defunctGame);
					}
				}
				defunctGames.clear();
			}
		}
	}

	public void waitForConnections() {
		while (accepting) {
			try {
				Socket inc = LocalSocket.accept();
				System.out.println("Connection accepted, starting a handler..." + ThreadPool.toString());
				Host.ThreadPool.execute(	new initialConnectionHandler(inc, this));
			} catch (IOException e) {
				
			}

		}
		
		try {
			LocalSocket.close();
		} catch (IOException e) {
			
		}
	}
	
	class initialConnectionHandler implements Runnable {
		Socket inc;
		Host host;
		
		public initialConnectionHandler(Socket in, Host ho) {
			inc = in;
			host = ho;
		}
		
		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream(inc.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(inc.getOutputStream());
				
				System.out.println("initialConnectionHandler: Reading object...");
				DataPacket pkt = (DataPacket)in.readObject();
				System.out.println("initialConnectionHandler: Read packet: "+pkt);
				
				if (pkt instanceof Connect) {
					((Connect)pkt).Name = makeAcceptablePlayerName(((Connect)pkt).Name);					
					
					synchronized(Connections) {
						Connections.add(new Connection(host, inc, in, out, (Connect)pkt));
					}
				
					System.out.println("initialConnectionHandler: Added connection from "+((Connect)pkt).Name+"@"+inc);
					return;
				} else {
					System.out.println("initialConnectionHandler: Connection attempt failed due to improper handshake: " + pkt.PacketType.hashCode() + " " + DataPacket.Type.CONNECT.hashCode());
				}
				
			} catch (ClassNotFoundException e) {
				System.out.println("initialConnectionHandler: Failed to accept connection due to improper client: " + e);
			} catch (IOException e) {
				System.out.println("initialConnectionHandler: Failed to accept connection due to IO Exception: " + e);
			} finally {	
				System.out.println("initialConnectionHandler: Connection handler exiting for " +inc);
			}
		}
	}

}