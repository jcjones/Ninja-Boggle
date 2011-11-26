/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.util.Calendar;

/**
 * class Connect
 * 
 */

public class Chatter extends DataPacket{
	static final long serialVersionUID = 61374L;

	/** Attributes: */
	public String senderName;
	public String text;
	public long timeStamp;
	
	
	public Chatter(String text) {
		super(DataPacket.Type.CHATTER);
		this.senderName = "Anonymous Coward";
		this.text = text;
	}	
	
	public String getTimestampString() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeStamp);		
		
		return cal.get(Calendar.HOUR_OF_DAY) + ":" + 
			cal.get(Calendar.MINUTE) + ":" + 
			cal.get(Calendar.SECOND);
	}

}
