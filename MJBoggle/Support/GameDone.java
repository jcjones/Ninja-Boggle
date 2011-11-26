/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.io.Serializable;

/**
 * class GameDone
 * 
 */

public class GameDone extends DataPacket implements Serializable {
	static final long serialVersionUID = 61361L;

	/** Attributes: */

	public IndividualScores IndividualScores;
	public Standings Standings;
	
	public GameDone(IndividualScores is, Standings s) {
		super(DataPacket.Type.GAMEDONE);
		
		IndividualScores = is;
		Standings = s;
	}

}
