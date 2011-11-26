/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import java.io.*;

import MJBoggle.WordTree.WordTree;
import MJBoggle.WordTree.WordTreeResult;

/**
 * class Dictionary
 * run with 'java -Xmx128M MJBoggle.Server.Dictionary'
 */

public class Dictionary {

	/** Attributes: */

	private WordTree Words;

	private static Dictionary Self_Reference;

//	private ArrayList<DictionaryCache> Cache;

	/** Public methods: */
	public static Dictionary getInstance() {
		if (Self_Reference == null) {
			Self_Reference = new Dictionary();
		}
		return Self_Reference;
	}

	public boolean isWord(String Guess) {
		/*TODO: Cache*/
		//System.out.print("Checking for Word \""+Guess+"\"...");
		WordTreeResult result = Words.lookup(sanitize(Guess));
		//System.out.println(result.result);
		return result.result;
	}

	public boolean isSeq(String Sequence) {
		/*TODO: Cache*/
		//System.out.print("Checking for Sequence \""+Sequence+"\"...");
		WordTreeResult result = Words.seqExists(sanitize(Sequence));
		//System.out.println(result.result);
		return result.result;		
	}

	/** Private methods: */
	private Dictionary() {
		Words = new WordTree();
		
		init();
		try {
			load(ClassLoader.getSystemClassLoader().getResourceAsStream("MJBoggle/Contrib/word.list"));			
		} catch (FileNotFoundException e) {
			System.err.println("Word list not found.");
		} catch (IOException e) {
			System.err.println("Error loading word list.");
		}
	}

	private void init() {
		//Cache = new ArrayList<DictionaryCache>(5);		
	}

	private void load(InputStream dictionaryData) throws IOException {
		BufferedReader fin = new BufferedReader(new InputStreamReader(dictionaryData));
		String line;
		
		int count = 0;
		while (true) {
			line = fin.readLine();
			
			if (line == null || line.length() < 1)
				break;
			
			Words.insert(line);
			count++;
						
		}
		
		System.out.println("Dictionary:: Loaded " + count + " words into the dictionary.");
		fin.close();
	}
	
	public static String sanitize(String dirty) {
		if (dirty == null) return "";
		
		StringBuffer clean = new StringBuffer("");		
		dirty = dirty.toLowerCase();
		
		for (int i=0; i<dirty.length(); i++) {
			char c = dirty.charAt(i);
			if (c < 'a' || c > 'z')
				continue;
			else
				clean.append(c);
		}
		
		return clean.toString();
	}
	
	public static void main(String[] args) {
		Dictionary d = Dictionary.getInstance();
		
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        
		d.isSeq("disenvelopin");
		d.isSeq("#%^##disenV-eloping");
		d.isWord("disenvelopin");
		d.isWord("disenveloping");
		System.out.println("Enter words you want to test, pressing enter after each line.");
		
		String line;
		while (true) {			
			try {
				line = input.readLine();
			} catch (IOException e) {
				break;				
			}
			
			if (line == null || line.length() < 1)
				break;
			
			System.out.print("Checking for Sequence \""+line+"\"...");
			System.out.println(d.isSeq(line));
			System.out.print("Checking for Word \""+line+"\"...");
			System.out.println(d.isWord(line));
		}
		
	}

}
