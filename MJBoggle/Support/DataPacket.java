/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Support;

import java.io.Serializable;

/**
 * class DataPacket
 * 
 */

abstract public class DataPacket implements Serializable {
// enum
public static final class Type implements Serializable {
		static final long serialVersionUID = 61380L;
		
        public static final Type CONNECT = new Type("CONNECT");
        public static final Type PLAYERCONNECT = new Type("PLAYERCONNECT");
        public static final Type GAMEDONE = new Type("GAMEDONE");
        public static final Type GAMELIST = new Type("GAMELIST");
        public static final Type GAMESTAT = new Type("GAMESTAT");
        public static final Type GAMESTANDINGS = new Type("GAMESTANDINGS");
        public static final Type LONGGUESSES = new Type("LONGGUESSES");
        public static final Type DUPEGUESSES = new Type("DUPEGUESSES");
        public static final Type GUESS = new Type("GUESS");
        public static final Type MAKEGUESS = new Type("MAKEGUESS");
        public static final Type GUESSACK = new Type("GUESSACK");
        public static final Type JOIN = new Type("JOIN");
        public static final Type LEAVE = new Type("LEAVE");
        public static final Type NEWGAME = new Type("NEWGAME");
        public static final Type CHATTER = new Type("CHATTER");

        public String toString() { return name; }
        
        public boolean equals(Object arg0) {
        		return this.name.equals(((Type)arg0).name);        		
        }
        public int hashCode() {
        		return this.name.hashCode();
        }

        private String name;
        private Type(String myName) { name = myName; }
}

	/** Attributes: */
	public Type PacketType;

	/** Public methods: */
	public DataPacket(Type typeID) {
		PacketType = typeID;
	}
	
	

}
