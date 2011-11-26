/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.WordTree;

/**
 * class WordTree
 * 
 * all Word values must be 
 * 	1) all lowercase
 *  2) only alphabetic, no punctuation, spaces or numbers
 */

public class WordTree {

	/** Attributes: */

	private TreeNode[] Nodes;

	public WordTree() {
		Nodes = new TreeNode[26];
	}

	/** Public methods: */
	public WordTreeResult lookup(String Word) {
		if (Word == null || Word.length() < 1)
			return new WordTreeResult(false, null);
		
		int firstChar = Word.charAt(0)-'a';
		
		if (Nodes[firstChar] != null) {
			return Nodes[firstChar].lookup(Word, Word.substring(1));
		}
		
		return new WordTreeResult(false, null);
	}
	
	public WordTreeResult seqExists(String Seq) {
		if (Seq == null || Seq.length() < 1)
			return new WordTreeResult(false, null);
		
		int firstChar = Seq.charAt(0)-'a';
		
		if (Nodes[firstChar] != null) {
			return Nodes[firstChar].seqExists(Seq, Seq.substring(1));
		}
		
		return new WordTreeResult(false, null);
	}	

	public void insert(String Word) {
		if (Word == null || Word.length() < 1)
			return;
		
		int firstChar = Word.charAt(0)-'a';
		
		if (Nodes[firstChar] == null) {
			Nodes[firstChar] = new TreeNode();
		}
				
		Nodes[firstChar].insert(Word, Word.substring(1));
	}

	public void destroy() {
		for (int i=0; i<26; i++) {
			if (Nodes[i] != null) {
				Nodes[i].destroy();
				Nodes[i] = null;
			}
		}
	}

}
