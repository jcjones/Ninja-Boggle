/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

/**
 * class Connect
 * 
 */

public class GameStandings extends DataPacket{
	static final long serialVersionUID = 61375L;

	/** Attributes: */
	public Standings Standings;
	
	public GameStandings(Standings stand) {
		super(DataPacket.Type.GAMESTANDINGS);
		this.Standings = stand;
	}	
	
}
