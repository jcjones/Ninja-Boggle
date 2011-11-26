/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import MJBoggle.Support.*;

/**
 * class Connection
 * 
 */

public class Connection {

	/** Attributes: */
	private Player Player;
	private Socket Client;
	private ArrayBlockingQueue<DataPacket> RecvQueue;
	private ArrayBlockingQueue<DataPacket> SendQueue;
	private Thread RecvThread;
	private Thread SendThread;
	private Host Host;
	private boolean running;
	private ObjectInputStream ObjIn;
	private ObjectOutputStream ObjOut;
	
	private boolean observing;

	/** Public methods: */
	public Connection(Host h, Socket Socket, ObjectInputStream In, ObjectOutputStream Out, MJBoggle.Support.Connect pkt) {
		Host = h;
		Client = Socket;
		ObjIn = In;
		ObjOut = Out;
		Player = new Player(pkt.Name);
		RecvQueue = new ArrayBlockingQueue<DataPacket>(50);
		SendQueue = new ArrayBlockingQueue<DataPacket>(50);
		
		running = true;
		
		if (pkt instanceof ObserverConnect)
			observing = true;
		else
			observing = false;
		
		RecvThread = new Thread(new ConnectionReceiver(), "Recv-ServerConnection-" + pkt.Name);
		SendThread = new Thread(new ConnectionSender(), "Send-ServerConnection-" + pkt.Name);
		
		RecvThread.start();
		SendThread.start();
	}

	// Blocking
	public void sendPacket(DataPacket data) {
		for (;;) {
			try {
				SendQueue.put(data);
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	// Non-blocking
	public DataPacket getPacket() {
		// getPacket returns null if the queue is empty
		return RecvQueue.poll();
	}

	public Player getPlayer() {
		return Player;
	}

	public String getName() {
		return Player.getName();
	}

	public int getGameNum() {
		return Player.getGameNum();
	}

	public void setGameNum(int GameNum) {
		Player.setGameNum(GameNum);
	}
	
	public boolean alive() {
		return running;
	}
	
	public boolean observing() {
		return observing;
	}

	public void close() {
		running = false;
		
		// Depart this player from their game
		Game game = Host.getGame(getGameNum());
		if (game != null)
			game.playerLeaves(this);
		
		// Close anything running
		try {
			ObjIn.close();
			ObjOut.close();
			Client.close();
		} catch (IOException e) {
			System.out.println("Failed to close connections for "
					+ Thread.currentThread().getName());
		}
	}

	public boolean tryToHandle(DataPacket pkt) {
		/*
		 * Connection handles the following packet types: Join, NewGame
		 */
		int gameNum;
		Game gameObj;
		
		if (pkt.PacketType.equals(DataPacket.Type.JOIN)) {		
			// Crack packet to find game number, look it up, join it and mark the player appropiately
			gameNum = ((Join)pkt).GameNum;
			gameObj = Host.getGame(gameNum);
			
			if (gameObj == null) 
				return true;
			
			if (Player.getGameNum() != 0) {
				// If for some reason the player is already IN a game, force them to leave.
				// This takes care of buggy or inconsistent clients.
				Game formerGameObj = Host.getGame(Player.getGameNum());
				if (formerGameObj != null)
					formerGameObj.playerLeaves(this);
			}
			
			gameObj.playerJoins(this);
			Player.setGameNum(gameNum);
			
			return true;
		}
		
		if (pkt.PacketType.equals(DataPacket.Type.NEWGAME)) {
			// Create a new game, join it and mark the player appropiately
			String name = ((NewGame)pkt).GameName;
			gameObj = Host.newGame(name);
			gameNum = gameObj.getGameNum();
			
			gameObj.playerJoins(this);
			Player.setGameNum(gameNum);			
			return true;
		}
		
		// We can't handle this!
		return false;		
	}

	class ConnectionSender implements Runnable {
		public void run() {
			int failureCount = 0;
			
			System.out.println(Thread.currentThread().getName()+" I'm running");
			while(running) {
				try {
					DataPacket pkt = SendQueue.take();
					if (pkt != null) {
						try {
							ObjOut.writeObject(pkt);
						} catch (IOException e) {
							System.out.println("Failed to write object for "
									+ Thread.currentThread().getName() + ": "
									+ e);
							if (failureCount++ > 10) {
								running = false;
								close();
							}
						}
					}
					//System.out.println("Sent: " + pkt.PacketType);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	class ConnectionReceiver implements Runnable {
		public void run() {
			System.out.println(Thread.currentThread().getName()+" I'm running");
			while(running) {
				try {
					DataPacket pkt = (DataPacket) ObjIn.readObject();
					if (!tryToHandle(pkt)) {
						// If we couldn't handle it, add it to the queue and
						// continue...
						for (;;) {
							try {
								RecvQueue.put(pkt);
								break;
							} catch (InterruptedException e) {
							}
						}
					}
					//System.out.println("Recieved: " + pkt.PacketType);
				} catch (IOException e) {
					System.out.println("Failed to read data from "
							+ Thread.currentThread().getName());
					close();
				} catch (ClassNotFoundException e) {
					System.out.println("Invalid data from "
							+ Thread.currentThread().getName());
				}
			}
		}

	}

}
