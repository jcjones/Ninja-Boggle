/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

public class ObserverConnect extends Connect {
	static final long serialVersionUID = 261360L;
	
	public ObserverConnect() {
		super(DataPacket.Type.CONNECT);
		Name = "Observer";
	}
}
