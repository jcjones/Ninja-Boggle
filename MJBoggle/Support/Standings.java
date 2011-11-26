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

public class Standings implements Serializable
{
	static final long serialVersionUID = 61373L;

	/**Attributes: */
	public ArrayList<StandingEntry> Entries;
	
	public Standings() {
		Entries = new ArrayList<StandingEntry>();
	}
	
	public void addStandingEntry(StandingEntry e) {
		Entries.add(e);
	}

}
