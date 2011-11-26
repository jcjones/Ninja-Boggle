/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

public class GuessAck extends DataPacket {
	static final long serialVersionUID = 61366L;
	
	public static final int WordIsRealAndExists = 101;
	public static final int WordIsReal = 102;
	public static final int WordIsNonsense = 104;
	
	/** Attributes: */
	public int ResponseType;
	public int Score;
	public String GuessedWord;
	
	public GuessAck(String word, int response, int score) {
		super(DataPacket.Type.GUESSACK);
		GuessedWord = word;
		ResponseType = response;
		Score = score;
	}

}
