/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.util.ArrayList;

public class GameList extends DataPacket {
	static final long serialVersionUID = 61362L;
	
	/** Attributes: */
	public ArrayList<GameListEntry> GamesRunning;
	
	public GameList() {
		super(DataPacket.Type.GAMELIST);

		GamesRunning = new ArrayList<GameListEntry>();
	}
	
	public void add (GameListEntry e) {
		GamesRunning.add(e);
	}

}
