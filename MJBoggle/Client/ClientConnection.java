/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.net.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import MJBoggle.Support.DataPacket;

/**
 * class ClientConnection
 * 
 */

public class ClientConnection {

	/** Attributes: */

	private String Host;
	private int Port;
	private Socket Socket;
	private ArrayBlockingQueue<DataPacket> SendQueue;
	private ArrayBlockingQueue<DataPacket> RecvQueue;
	private ObjectInputStream ObjIn;
	private ObjectOutputStream ObjOut;

	/** Public methods: */
	public ClientConnection(String Host, int Port) {
		SendQueue = new ArrayBlockingQueue<DataPacket>(10);
		RecvQueue = new ArrayBlockingQueue<DataPacket>(10);
		this.Host = Host;
		this.Port = Port;		
	}

	/* This blocks until a packet is successfully enqueued for sending. */
	public void sendPacket(DataPacket data) {
		for (;;) {
			try {
				SendQueue.put(data);
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	/* This blocks until a packet comes back. */
	public DataPacket getPacket() {
		try {
			return RecvQueue.take();
		} catch (InterruptedException e) {}
		return null;
	}
	
	public String reportOnQueues() {
		return "SendQueue: " + SendQueue.size() + " RecvQueue: " + RecvQueue.size();
	}

	public void connect() throws IOException {
		Socket = new Socket(Host, Port);
		System.out.println("Creating streams...");
		
		ObjOut = new ObjectOutputStream(Socket.getOutputStream());
		ObjIn = new ObjectInputStream(Socket.getInputStream());
		
		System.out.println("Spawning threads...");
		new Thread(new ConnectionSender(), "Recv-ClientConnection").start();
		new Thread(new ConnectionReceiver(), "Send-ClientConnection").start();
	}

	public void disconnect() throws IOException {
		ObjOut.close();
		ObjIn.close();
		Socket.close();
		System.out.println("Disconnected.");
	}

	class ConnectionSender implements Runnable {
		public void run() {
			for (;;) {
				try {
					DataPacket pkt = SendQueue.take();
					if (pkt != null) {
						try {						
							ObjOut.writeObject(pkt);						
						} catch (IOException e) {
							System.out.println("Failed to write object for "
									+ Thread.currentThread().getName() + ": " + e);
						}
						//System.out.println("Sent: " + pkt.PacketType);					
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}

	class ConnectionReceiver implements Runnable {
		public void run() {
			for (;;) {
				try {
					DataPacket pkt = (DataPacket) ObjIn.readObject();
					for (;;) {
						try {
							RecvQueue.put(pkt);
							break;
						} catch (InterruptedException e) {}
					}
					//System.out.println("Got new packet ["+pkt.PacketType.toString()+"] hash: " + pkt.hashCode());
				} catch (IOException e) {
					System.out.println("Failed to read data from "
							+ Thread.currentThread().getName());
					System.exit(1);
				} catch (ClassNotFoundException e) {
					System.out.println("Invalid data from "
							+ Thread.currentThread().getName());
				}
			}
		}
	}

}

