/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;


public class GameStat extends DataPacket {
	static final long serialVersionUID = 61364L;
	
	/** Attributes: */
	public long TimeLeft;
	public String[][] BoardData;
	
	public GameStat(long time, String[][] data) {
		super(DataPacket.Type.GAMESTAT);

		TimeLeft = time;
		
		// Force construction of entirely new String array object 
		// to keep serializer from doing anything stupid.
		BoardData = new String[data.length][];		
		for(int i=0; i<data.length; i++) {
			BoardData[i] = new String[data[i].length];
			for(int j=0; j<data[i].length; j++) {
				BoardData[i][j] = new String(data[i][j]);
			}
		}
	}

}
