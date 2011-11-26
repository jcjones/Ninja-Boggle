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

public class StandingEntry implements Serializable, Comparable<StandingEntry> {
	static final long serialVersionUID = 61372L;

	/** Attributes: */

	public String PlayerName;
	public int Score;

	/** Public methods: */
	public StandingEntry(String p, int s) {
		PlayerName = p;
		Score = s;
	}
	
	public int compareTo(StandingEntry other) {
		if (this.Score > other.Score)
			return 1;
		else if (this.Score < other.Score)
			return -1;
		else
			return this.PlayerName.compareTo(other.PlayerName);
	}

}
