/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.WordTree;

/**
 * class TreeNode
 * 
 */

public class TreeNode {

	/** Attributes: */

	private TreeNode[] Children;
	private boolean Terminal;

	/** Public methods: */
	public TreeNode() {
		Children = new TreeNode[26];
		this.Terminal = false;
	}

	public void insert(String FullWord, String RemainingWord) {
		if (RemainingWord.length() == 0) {
			// Last letter, we're the word!
			this.Terminal = true;
			//System.out.println("WordTree:: INSERTED ...\""+FullWord+"\"");
			return;
		}
			
		int firstChar = RemainingWord.charAt(0)-'a';
		
		if (Children[firstChar] == null) {
			Children[firstChar] = new TreeNode();
		}
		
		//System.out.println("WordTree:: inserting ...\""+RemainingWord+"\"");
				
		Children[firstChar].insert(FullWord, RemainingWord.substring(1));
	}

	public WordTreeResult lookup(String FullWord, String RemainingWord) {
		if (RemainingWord.length() == 0) {
			return new WordTreeResult(this.Terminal, this);
		}
		
		int firstChar = RemainingWord.charAt(0)-'a';
		
		if (Children[firstChar] != null) {			
			return Children[firstChar].lookup(FullWord, RemainingWord.substring(1));
		}
		
		return new WordTreeResult(false, this);
	}

	public WordTreeResult seqExists(String FullSeq, String RemainingSeq) {		
		if (RemainingSeq.length() == 0)
			return new WordTreeResult(true, this);

		int firstChar = RemainingSeq.charAt(0)-'a';

		
		if (Children[firstChar] != null) {			
			return Children[firstChar].seqExists(FullSeq, RemainingSeq.substring(1));
		}
		
		return new WordTreeResult(false, this);
	}

	public void destroy() {
		for (int i=0; i<26; i++) {
			if (Children[i] != null) {
				Children[i].destroy();
				Children[i] = null;
			}
		}
	}

}
