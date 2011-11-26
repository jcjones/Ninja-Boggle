/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

public class GameGuesses extends DataPacket {
	static final long serialVersionUID = 61399L;

	/** Attributes: */
	public Guesses Guesses;
	
	public GameGuesses(Guesses guess, Type type) {
		super(type);
		this.Guesses = guess;
	}	
	
}