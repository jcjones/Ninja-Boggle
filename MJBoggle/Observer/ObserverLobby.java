/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Observer;

import java.awt.event.ActionEvent;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import MJBoggle.Client.gameLobby;
import MJBoggle.Support.GameListEntry;
import MJBoggle.Support.Join;

public class ObserverLobby extends gameLobby {
	
	protected void initComponents() {
		super.initComponents();
		gameMenu.remove(newMenuItem);
		buttonPane.remove(newButton);
	}
	
	protected void newDojoActionPerformed(ActionEvent e) {
		throw new NotImplementedException();
	}
	
	protected void fleeActionPerformed(ActionEvent e) {
		Observer.getInstance().shutdown();
	}
	
	protected void joinDojoActionPerformed(ActionEvent e) {
		Observer client = Observer.getInstance();
		
		selectedGame = (GameListEntry)gameList.getSelectedValue();
		
		if(selectedGame != null)
		{
			// Join the selected game...
			System.out.println("Game num: "+selectedGame.GameNum);
			Join pkt = new Join(selectedGame.GameNum);
			
			//Show the game, hide this window
			client.getGameWindow().setVisible(true);
			client.getGameWindow().setGameName(selectedGame.Name);
			setVisible(false);
			
			client.getConnection().sendPacket(pkt);							
		}
	}
}
