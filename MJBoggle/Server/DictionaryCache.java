/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Server;

import MJBoggle.WordTree.TreeNode;


/**
  * class DictionaryCache
  * 
  */

public class DictionaryCache
{

/**Attributes: */

	public String Sequence;
	public TreeNode Node;

	public DictionaryCache(String seq, TreeNode node) {
		Sequence = seq;
		Node = node;
	}

}
