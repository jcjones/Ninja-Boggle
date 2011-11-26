/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.util.*;
import MJBoggle.Support.*;

/**
  * class PlayerGuess
  * 
  */

public class PlayerGuess
{

/**Attributes: */

	private Player Player;
	private ArrayList<Guess> Guesses;

	public PlayerGuess(Player p) {
		Player = p;
		Guesses = new ArrayList<Guess>();		
	}
	
	public Player getPlayer() {
		return Player;
	}
	
	public ArrayList<Guess> getGuesses() {
		return Guesses;
	}
	
	public void addGuess(Guess g) {
		Guesses.add(g);
	}
		
}
