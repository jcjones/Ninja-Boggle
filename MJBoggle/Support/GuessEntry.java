/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.io.Serializable;

/**
 * class StandingEntry
 * 
 */

public class GuessEntry implements Serializable, Comparable<GuessEntry> {
	static final long serialVersionUID = 61372L;

	/** Attributes: */

	public String Guess;
	public String PlayerName;
	public int NumberOfInstances = 0;

	/** Public methods: */
	public GuessEntry(String p, String n) {
		Guess = p;
		PlayerName = n;
	}
	
	public GuessEntry(String p, String n, int s) {
		Guess = p;
		PlayerName = n;
		NumberOfInstances = s;
	}
	
	public int compareTo(GuessEntry other) {
		if (this.Guess.length() > other.Guess.length())
			return 1;
		else if (this.Guess.length() < other.Guess.length())
			return -1;
		else if (this.NumberOfInstances > other.NumberOfInstances)
			return 1;
		else 
			return -1;
			
	}
	
	public String toString() {
		if (NumberOfInstances > 0)
			return "Entry: " + Guess + " by " + PlayerName;
		else
			return "Entry: " + Guess + " (" + NumberOfInstances + ") by " + PlayerName;
	}

}
