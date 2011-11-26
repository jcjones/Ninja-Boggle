/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

public class MakeGuess extends DataPacket {
	static final long serialVersionUID = 61370L;
	
	/** Attributes: */
	public String Guess;
	
	public MakeGuess(String guess) {
		super(DataPacket.Type.MAKEGUESS);

		Guess = guess;
	}

}
