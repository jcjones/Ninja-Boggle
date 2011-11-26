/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.WordTree;

/**
 * class WordTreeResult
 * 
 */

public class WordTreeResult {

	/** Attributes: */

	public boolean result;

	public TreeNode cacheNode;
	
	public WordTreeResult(boolean res, TreeNode cache) {
		result = res;
		cacheNode = cache;
	}

}
