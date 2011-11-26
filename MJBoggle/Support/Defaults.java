/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Defaults {
	private static Defaults instance;
	
	private Properties properties;
	
	private String hostname;
	private int port;
	
	private int newGameDelaySeconds;
	private int gameLengthSeconds;
	private int gamePersistSeconds;
	private int standingsDelaySeconds;
	private boolean showSolutions;

	private Defaults() {
		properties = new Properties();
		loadProperties(this.getClass().getClassLoader().getResourceAsStream("defaults.xml"));
	}
	
	public static Defaults getInstance() {
		if (instance == null)
			instance = new Defaults();
		
		return instance;
	}	
	
	public int getGameLengthSeconds() {
		return gameLengthSeconds;
	}

	public int getGamePersistSeconds() {
		return gamePersistSeconds;
	}

	public String getHostname() {
		return hostname;
	}

	public int getNewGameDelaySeconds() {
		return newGameDelaySeconds;
	}

	public int getPort() {
		return port;
	}

	public Properties getProperties() {
		return properties;
	}

	public int getStandingsDelaySeconds() {
		return standingsDelaySeconds;
	}
	
	public boolean getShowSolutions() {
		return showSolutions;
	}

	private void loadProperties(InputStream in) {
		try {
            properties.loadFromXML(in);
            in.close();
		} catch (IOException e) {
			System.err.println("Could not read configuration file.");
		}
		
		hostname = properties.getProperty("Hostname", "localhost");
		port = Integer.parseInt(properties.getProperty("Port", "20210"));
		newGameDelaySeconds = Integer.parseInt(properties.getProperty("NewGameStartDelay", "20"));
		gameLengthSeconds = Integer.parseInt(properties.getProperty("GameLengthSeconds", "240"));
		gamePersistSeconds = Integer.parseInt(properties.getProperty("EmptyGamePersistSeconds", "60"));
		standingsDelaySeconds = Integer.parseInt(properties.getProperty("DelayBetweenStandingsUpdates","5"));
		showSolutions = Boolean.parseBoolean(properties.getProperty("ShowSolutions","false"));
		
		// Enforce range
		if ( (port < 1025) || (port > 65535) )
			throw new IllegalArgumentException("Port value in defaults.xml is out of range.");
	}
}
