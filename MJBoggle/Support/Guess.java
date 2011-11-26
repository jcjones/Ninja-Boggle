/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.io.Serializable;

/**
  * class Guess
  * 
  */

public class Guess implements Serializable
{
	static final long serialVersionUID = 61365L;
/**Attributes: */

	public String Word;
	public int Score;
	
	public Guess(String w, int s) {
		Word = w;
		Score = s;
	}


}
