/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.util.*;
import MJBoggle.Support.Guess;
import MJBoggle.Support.GuessEntry;
import MJBoggle.Support.Guesses;
import MJBoggle.Support.Standings;
import MJBoggle.Support.StandingEntry;
import MJBoggle.Support.IndividualScores;

/**
 * class ScoreBoard
 * 1) If the word has already been submitted, the score is zero.
 * 2) If the word has not already been submitted, the score is the length of the word.
 */

public class ScoreBoard {

	/** Attributes: */
	private ArrayList<PlayerGuess> PlayerGuesses;
	private Hashtable<String, Player> WordsGuessed;
	private CircularBuffer RecentDupes;
	private LengthFilter LongWordStorage;

	/** Public methods: */
	public ScoreBoard() {
		PlayerGuesses = new ArrayList<PlayerGuess>();
		WordsGuessed = new Hashtable<String, Player>(100);
		RecentDupes = new CircularBuffer(10);
		LongWordStorage = new LengthFilter(10);
	}
	
	public int recordGuess(Player Player, String Guess, boolean Result) {
		int Points = 0;
		String sanitizedGuess = Dictionary.sanitize(Guess);
		if (Result == true) {
			if (WordsGuessed.containsKey(sanitizedGuess)) {
				// No points
				RecentDupes.put(sanitizedGuess, Player.getName());
				LongWordStorage.putDupe(sanitizedGuess);
			} else {
				// Player gets points, they're the first to get this word
				// First trim the word to real letters so ".,.,.,.,.,.,.it" doesn't get tons of points.				
				Points = sanitizedGuess.length();
				WordsGuessed.put(sanitizedGuess, Player);
				LongWordStorage.put(sanitizedGuess, Player.getName());
			}
		}
		
		// Record it
		boolean found = false;
		// Perform linear search for the player (there are few players)
		for (PlayerGuess pg : PlayerGuesses) {
			if (pg.getPlayer() == Player) {
				found = true;
				pg.addGuess(new Guess(Guess, Points));
				break;
			}
		}
		if (!found) {
			// Player isn't noted in the PlayerGuesses list yet, add them.
			PlayerGuess pg = new PlayerGuess(Player);
			PlayerGuesses.add(pg);
			pg.addGuess(new Guess(Guess, Points));
		}
		
		return Points;
	}
	
	public void nextGame() {
		PlayerGuesses.clear();
		WordsGuessed.clear();
		LongWordStorage.clear();
		RecentDupes.clear();
	}

	public IndividualScores getScores(Player target) {
		// Perform linear search for the player (there are few players)
		for (PlayerGuess pg : PlayerGuesses) {
			if (pg.getPlayer() == target) {
				return new IndividualScores(pg.getGuesses());
			}
		}			
		
		return null;
	}

	public Standings getStandings() {
		Standings finalStandings = new Standings();
		
		for (PlayerGuess pg : PlayerGuesses) {
			int totalScore = 0;
			for (Guess g : pg.getGuesses()) {
				totalScore += g.Score;
			}
			
			finalStandings.addStandingEntry(new StandingEntry(pg.getPlayer().getName(), totalScore));			
		}
		
		return finalStandings;
	}
	
	public Guesses getRecentDupes() {
		Guesses dupes = new Guesses();
		
		dupes.Entries.addAll(RecentDupes.getContents());
		
		return dupes;
	}
	
	public Guesses getLongestWords() {
		Guesses longest = new Guesses();
		
		longest.Entries.addAll(LongWordStorage.getContents());
		
		return longest;
	}
	
	private class CircularBuffer {
		int capacity;
		Queue<GuessEntry> data;

		public CircularBuffer(int cap) {
			capacity = cap;
			data = new LinkedList<GuessEntry>();
		}
		
		public void put(String word, String person) {
			GuessEntry ge = new GuessEntry(word, person);
			
			if (data.size() >= capacity)
				data.poll();
			data.offer(ge);
		}
		
		public Collection<GuessEntry> getContents() {
			return data;
		}
		
		public void clear() {
			data.clear();
		}
		
	}
	
	private class LengthFilter {
		PriorityQueue<GuessEntry> data;
		int capacity;
		int minLength;
		
		public LengthFilter(int cap) {
			data = new PriorityQueue<GuessEntry>();
			minLength = 1;
			capacity = cap;
		}
		
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("Capacity: " + capacity + " Min Length: " + minLength + "\n");
			for (GuessEntry ge : data) {
				buf.append(ge).append("\n");
			}
			return buf.toString();
		}
		
		public void put(String word, String name) {
			// If it's longer than the shortest word in our list so far, replace the
			// short word with this one.
			
			if (word.length() < minLength)
				return;
			
			GuessEntry ge = new GuessEntry(word, name, 1);			
			data.offer(ge);
			
			if (data.size() > capacity) {
				data.poll();		
				// Update the minlength now to the length of the min element
				minLength = data.peek().Guess.length();
			}
		}
		
		public void putDupe(String word) {
			// If it's in our list, increment the count

			if (word.length() < minLength)
				return;
			
			for (GuessEntry ge : data) {
				if (ge.Guess.equals(word)) {
					ge.NumberOfInstances++;
					return;
				}
			}
		}
		
		public void clear() {
			data.clear();
			minLength = 1;
		}
		
		public Collection<GuessEntry> getContents() {
			return data;
		}
	}

}
