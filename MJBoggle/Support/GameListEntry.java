/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.io.Serializable;

/**
 * class GameListEntry
 * 
 */

public class GameListEntry implements Serializable {
	static final long serialVersionUID = 61363L;

	/** Attributes: */
	public String Name;
	public int GameNum;
	public int NumPlayers;
	public int TimeRemainingSeconds;
	public String[] PlayerNames;
	
	public GameListEntry(String n, int gn, int np, int trs, String[] pn) {
		Name = n;
		GameNum = gn;
		NumPlayers = np;
		TimeRemainingSeconds = trs;
		PlayerNames = pn;		
	}
	
	public String toString() {

		String playerString;
		if(NumPlayers == 1)		
			playerString = NumPlayers +" player";
		else if (NumPlayers == 0)
			playerString = "empty";
		else
			playerString = NumPlayers +" players";
		
		String TimeRemaining = new String((TimeRemainingSeconds/60)+" mins, "+(TimeRemainingSeconds%60)+" secs");
		
		return("<html><table cellpadding=\"5\" width=\"300\"><tr><td>["+playerString+"]</td><td align=\"left\">"+Name+"</td><td>"+TimeRemaining+"</td></tr></table></html>");

		//return(Name+" \t"+NumPlayers+" players");

	}

}
