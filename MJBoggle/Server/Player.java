/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

/**
 * class Player
 * 
 */

public class Player {

	/** Attributes: */	
	private String Name;
	private int InGameNum;

	/** Public methods: */
	public Player(String Name) {
		this.Name = Name;
		InGameNum = 0;
		System.out.println("New player created: " + Name);
	}

	public String getName() {
		return Name;
	}

	public int getGameNum() {
		return InGameNum;
	}

	public void setGameNum(int GameNum) {
		InGameNum = GameNum;
	}

}
