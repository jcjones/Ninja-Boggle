/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.util.*;
import java.io.Serializable;


/**
  * class Standings
  * 
  */

public class Guesses implements Serializable
{
	static final long serialVersionUID = 361373L;

	/**Attributes: */
	public ArrayList<GuessEntry> Entries;
	
	public Guesses() {
		Entries = new ArrayList<GuessEntry>();
	}
	
	public void addGuessEntry(GuessEntry e) {
		Entries.add(e);
	}

}
