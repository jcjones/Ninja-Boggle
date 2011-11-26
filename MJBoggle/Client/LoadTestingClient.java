/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class LoadTestingClient {
	private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	private final ArrayList<LoadTestingAutoClient> clients;
	
	private String serverHostname;
	private int serverPort;
	private int intendedGame = -1;
	
	private final Random randomObject = new Random();
	private final int numberOfWordsToLoad = 400000;
	private int numberOfWordsLoaded = 0;
	private String[] wordList;
		
	public boolean keepRunning = true;

	public LoadTestingClient(String host, int port) {
		serverHostname = host;
		serverPort = port;
		clients = new ArrayList<LoadTestingAutoClient>();
		loadDictionary("MJBoggle/Contrib/word.list");
	}
	
	private String getConsoleLine() {
		try {
			return input.readLine();
		} catch (IOException e) {
			System.out.println("Error reading from console: " + e);
		}
		return "";
	}
	
	public String getRandomGuess() {
		return wordList[randomObject.nextInt(numberOfWordsLoaded)];
	}
	
	public void execute() {
		System.out.println("Welcome to the MJBoggle Load Testing Bot Console.");
		System.out.println("You can type '?' for options.");
		for(;;) {			
			System.out.print(clients.size() + " clients running> ");
			String line = getConsoleLine();
			if (line.length() < 1) continue;
			
			switch(line.charAt(0)) {
			case 'n':
				keepRunning = true;
				int num = Integer.parseInt(line.split(" ")[1]);
				for (int i=0; i<num; i++) {
					LoadTestingAutoClient client = new LoadTestingAutoClient(this, serverHostname, serverPort, "Bot-"+i, intendedGame);					
					clients.add(client);
					new Thread(client, "Bot-"+i).start();
				}
				// Make new
				break;
			case 'g':
				intendedGame = Integer.parseInt(line.split(" ")[1]);
				break;
			case 'l':
				for(LoadTestingAutoClient client : clients) {
					System.out.println(client.getPlayerName() + " in " + client.getGameName());
				}
				break;
			case '!':
			case 'q':
				if (!keepRunning) {
					return;
				} else {
					keepRunning = false;
					clients.clear();
				}
				// Quit			
				break;
			case '?':
			case 'h':
				System.out.println("Commands:\nn [num]\tStart num new clients\ni [num]\tSet intended game\nl\tList clients\n!\tQuit\n?\tThis menu");
				break;
			}
		}
	}		
	
	private void loadDictionary(String filename) {
		Random r = new Random();
		wordList = new String[numberOfWordsToLoad];
		numberOfWordsLoaded = 0;
		
		try {
			/*TODO: Do stuff*/
			BufferedReader fin = new BufferedReader(new FileReader(filename));
			
			String line;
			
			while(numberOfWordsLoaded < numberOfWordsToLoad) {
				line = fin.readLine();
				
				if (line == null || line.length() < 1)
					break;
				
				if ( r.nextInt(10) > 3 )
					wordList[numberOfWordsLoaded++] = line;
			}
			
			fin.close();
		} catch (IOException e) {
			
		}
		
		System.out.println("Loaded " + numberOfWordsLoaded + " random words from " + wordList[0] + " to " + wordList[numberOfWordsLoaded-1]);
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Arguments: [ip address]:[port]");
			return;
		}
		String line[] = args[0].split(":");
		
		new LoadTestingClient(line[0], Integer.parseInt(line[1])).execute();
		
	}

}
