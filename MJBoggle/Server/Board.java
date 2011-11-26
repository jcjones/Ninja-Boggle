/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;

import MJBoggle.WordTree.WordTree;
import MJBoggle.Support.Defaults;

/**
 * class Board
 * 
 */

public class Board {
	/** Attributes: */
	private final int N, M;
	private String[][] RolledDice;
	private String[][] RawDice;
	private String[][] placeholderData;
	private WordTree WordsWithin;	
  
	private Random RandGenerator;
	
	// Master work queue for all Board objects	
	public static final ExecutorService ThreadPool = Executors.newFixedThreadPool(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());	
	
	/** Public methods: */
	public Board() {
		N = 4;
		M = 4;
		RolledDice = new String[N][M];
		RawDice = new String[N*M][6];
		
		Dictionary.getInstance(); // Force dictionary to load
		
		WordsWithin = new WordTree();
		
		RandGenerator = new Random();
		
		try {
			loadDice(this.getClass().getClassLoader().getResourceAsStream("MJBoggle/Contrib/dice.txt"));
		} catch (FileNotFoundException e) {
			System.err.println("Word list not found.");
		} catch (IOException e) {
			System.err.println("Error loading word list.");
		}
		
		// Placeholder data
		String word = "WAITING!";
		placeholderData = new String[N][M];
		
		int ptr = 0;
		
		for(int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				placeholderData[i][j] = new String(word.charAt(ptr)+"");
				ptr++;
				if (ptr >= word.length())
					ptr = 0;
			}
		}
		// Done Placeholder Data
				
		
		randomize();			
	}
	
	public void setRolledDice(String[][] a) {
		if (a.length == N && a[0].length == M)
			RolledDice = a;
		else
			throw new IllegalArgumentException("Dice array passed as argument is not "+N+"x"+M);
	}
	
	public void destroy() {
		ThreadPool.shutdown();
	}
	
	public void loadDice(InputStream data) throws IOException {
		BufferedReader fin = new BufferedReader(new InputStreamReader(data));
		
		// TODO: Make this fault-tolerant		
		for(int i=0; i<N*M; i++) {
			String[] ln = fin.readLine().split(" ");
			for (int j=0; j<6; j++) {
				RawDice[i][j] = ln[j];
			}
		}
		
		fin.close();
	}
	
	public void randomize() {
		/* Steps to randomize: 
		 * 1) Randomize order of individual dice in RawDice using inorder randomization algorithm copied from Gpremacy
		 * 2) Copy dice from RawDice into RolledDice, randomizing which face we choose
		 * */

		/* Algorithm: 
  		RandomInx : integer;
  		TempPtr   : pointer;

  		for Inx := aList.Count - 1 downto 1 do begin
	    	RandomInx := Random(Inx + 1);
    		if (RandomInx <> Inx) then begin
	      		TempPtr := aList[Inx];
	      		aList[Inx] := aList[RandomInx];
	      		aList[RandomInx] := TempPtr;
	    	end;
		*/
		
		String[] TempPtr;
		int RandomIndex;
		int Index;
		for (Index = RawDice.length-1; Index > 0; Index--) {
			RandomIndex = RandGenerator.nextInt(Index);			
			
			if (RandomIndex != Index) {
				TempPtr = RawDice[Index];
				RawDice[Index] = RawDice[RandomIndex];
				RawDice[RandomIndex] = TempPtr;				
			}
		}
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				RolledDice[i][j] = RawDice[(i*N)+j][RandGenerator.nextInt(6)];
			}
		}
		
	}
	
	private void printRolledDice() {
		System.out.println("Dice:");
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				System.out.print(RolledDice[i][j]+", ");
			}
			System.out.println();
		}
	}

	public String[][] getData() {
		return RolledDice;
	}
	
	public String[][] getPlaceholderData() {		
		return placeholderData;
	}

	public boolean isWordWithin(String word) {
		return WordsWithin.lookup(Dictionary.sanitize(word)).result;
	}

	public void beginWordSearch() {
		WordsWithin.destroy();
		ArrayList<Future<ArrayList<String>>> Futures = new ArrayList<Future<ArrayList<String>>>();
		
		long startTime = System.currentTimeMillis();
		//System.out.println("Word search begins at " + startTime);			
		
		// Queue up the letter positions for the children to fight over
		for(int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				// We can use add here since the queue is being accessed by only one thread.
				//SearchQueue.add(new Integer((i*N)+j));
				Futures.add(ThreadPool.submit(new wordSearchlet(new Integer((i*N)+j))));
			}			
		}
				
		// Our children do the work
		
		int wordsFound = 0;
		
		boolean printWordsFound = Defaults.getInstance().getShowSolutions();
		
		// Pack the word tree, etc.
		for (Future<ArrayList<String>> f : Futures) {
			try {
				ArrayList<String> results = f.get();
				for(String s : results) {
					WordsWithin.insert(Dictionary.sanitize(s));
					wordsFound++;
					if (printWordsFound)
						System.out.println(Thread.currentThread().getName() + "] Inserting into known words: " + s);
				}
			} catch (ExecutionException e) {
				System.out.println("Error in execution: "+ e);
			} catch (InterruptedException e) {}
		}
		
		long endTime = System.currentTimeMillis();
		//System.out.println("Word search ended at " + endTime);
		System.out.println("Board search time taken: " + (endTime-startTime) + " milliseconds.");
		System.out.println("Board word list is complete, found " + wordsFound + " words.");
	}

	public static void main(String[] args) {
		Board b = new Board();
		
		for (int i=0; i<5; i++) {
			b.randomize();
			b.printRolledDice();
			b.beginWordSearch();
		}
		
		b.destroy();

	}
	
	private class wordSearchlet implements Callable<ArrayList<String>> {
		Integer startingLocation;
		ArrayList<String> SearchResults;
		
		public wordSearchlet(Integer start) {
			startingLocation = start;
			SearchResults = new ArrayList<String>();
		}
		
		public ArrayList<String> call() {
			
			Stack<Integer> trace = new Stack<Integer>();
			StringBuffer wordFragment = new StringBuffer("");
			
			wordSearchRecurse(startingLocation, wordFragment, trace);
			return SearchResults;
		}
		
		private Integer makeInt(int i, int j) {
			return new Integer((i*N)+j);
		}
		
		private void wordSearchRecurse(Integer curDie, StringBuffer wordFragment, Stack<Integer> trace) {
			Dictionary dict = Dictionary.getInstance();
			
			//System.out.println(Thread.currentThread().getName() + ":: " + curDie +" -> " + wordFragment);
			
			// get the letter corresponding to this...
			int v = curDie.intValue();
			//(i*N)+j				
			int i = v/N;
			int j = v%N;
			
			// Add that letter to our current word
			wordFragment.append(RolledDice[i][j]);
			// mark that we've been here
			trace.push(curDie);
			
			// test the word and its sequences
			String fragmentString = wordFragment.toString();
			
			if (fragmentString.length() > 1 && dict.isWord(fragmentString)) {
				// This is a word! Add it to our results.
				SearchResults.add(fragmentString);
				//System.out.println(Thread.currentThread().getName() + " found word: " + fragmentString);
			}
			
			if (dict.isSeq(fragmentString)) {
				// It's part of a sequence of futher words, let's add all the neighbors onto the queue
				
				// For each neighbor, if the neighbor is not within trace already, recurse into it!
				if (i > 0 && j > 0 && trace.search(makeInt(i-1, j-1)) == -1 )
					wordSearchRecurse(makeInt(i-1, j-1), wordFragment, trace);
				if (j > 0 && trace.search(makeInt(i, j-1)) == -1 )
					wordSearchRecurse(makeInt(i, j-1), wordFragment, trace);
				if (i < N-1 && j > 0 && trace.search(makeInt(i+1, j-1)) == -1 )
					wordSearchRecurse(makeInt(i+1, j-1), wordFragment, trace);
				
				if (i > 0  && trace.search(makeInt(i-1, j)) == -1 )
					wordSearchRecurse(makeInt(i-1, j), wordFragment, trace);	
				if (i < N-1 && trace.search(makeInt(i+1, j)) == -1 )
					wordSearchRecurse(makeInt(i+1, j), wordFragment, trace);
				
				if (i > 0 && j < M-1 && trace.search(makeInt(i-1, j+1)) == -1 )
					wordSearchRecurse(makeInt(i-1, j+1), wordFragment, trace);
				if (j < M-1 && trace.search(makeInt(i, j+1)) == -1 )
					wordSearchRecurse(makeInt(i, j+1), wordFragment, trace);
				if (i < N-1 && j < M-1 && trace.search(makeInt(i+1, j+1)) == -1 )
					wordSearchRecurse(makeInt(i+1, j+1), wordFragment, trace);
			}
			
			// Leave no trace that we've been here...
			trace.pop();
			wordFragment.delete(wordFragment.length()-RolledDice[i][j].length(), wordFragment.length());
		}
		
	}

}
