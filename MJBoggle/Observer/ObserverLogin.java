/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones 
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Observer;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import MJBoggle.Client.gameLogin;
import MJBoggle.Support.DataPacket;
import MJBoggle.Support.ObserverConnect;

public class ObserverLogin extends gameLogin {

	static final long serialVersionUID = 233352L;
	
	protected void okButtonActionPerformed(ActionEvent e) {		
		//Get instance of singleton
		Observer client = Observer.getInstance();
		
		boolean result = client.makeConnection(hostname.getText(), portModel.getNumber().intValue());
		
		if (result == true) {
			// We connected!
			DataPacket pkt = new ObserverConnect();
			
			//Send packet
			client.getConnection().sendPacket(pkt);
			
			//Show the lobby, hide this window
			client.getLobbyWindow().setVisible(true);
			
			setVisible(false);
		} else {
			// We failed to connect, alert the user.
			JOptionPane.showMessageDialog(null,
			    "I couldn't connect to the server you specified. Maybe it's not up?",
			    "Connection failed",
			    JOptionPane.ERROR_MESSAGE);			
			
		}
	}

	protected void cancelButtonActionPerformed(ActionEvent e) {
		Observer client = Observer.getInstance();
		try{
			client.getConnection().disconnect();
		}catch(Exception e2){
			System.out.println("IO Error on Disconnect");			
		}
		System.out.println("Disconnecting...");
		System.exit(0);
	}
	
	protected void initComponents() {
		super.initComponents();
		
		setTitle("Ninja Boggle Observer");
		question.setText("Logging In As An Observer");
		usernameTextfield.setEnabled(false);
		contentPanel.remove(usernameTextfield);
	}

}
