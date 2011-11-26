/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Observer;

import MJBoggle.Client.ClientConnection;

public class ObserverConnection extends ClientConnection {
	public ObserverConnection(String Host, int Port) {
		super(Host, Port);
	}
}
