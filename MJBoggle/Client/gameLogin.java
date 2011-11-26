/*
* Ninja Boggle: A multiplayer Boggle clone game written in Java
*  Copyright (C) 2006 James 'Pug' Jones and Mikel Mazlaghani
*  Licensed with the GNU General Public License v2 or later. See
*  the COPYING file distributed with this for a copy of the license.
*/
package MJBoggle.Client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import MJBoggle.Support.Defaults;
import MJBoggle.Support.PlayerConnect;
import MJBoggle.Support.DataPacket;




/**
 * @author Mikel Mazlaghani
 */
public class gameLogin extends JFrame {
	
	static final long serialVersionUID = 133352L;
	
	//Interface
	protected JPanel dialogPane;
	protected JPanel contentPanel;
	protected JLabel bogglePic;
	protected JLabel question;
	protected JLabel loginLabel;
	protected JTextField usernameTextfield;	
	protected JTextField hostname;
	protected JSpinner port;
	protected SpinnerNumberModel portModel;
	protected JPanel buttonBar;
	protected JButton okButton;
	protected JButton cancelButton;

	
	//Data
	protected String playerName;
	
	public gameLogin() {
		initComponents();
		pack();
		setVisible(true);
	}

	
	protected void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		//Boggle Icon
		bogglePic = new JLabel();
		//What name would you like to use?
		question = new JLabel();
		//User handle
		usernameTextfield = new JTextField();
		buttonBar = new JPanel();
		
		//Login data
		loginLabel = new JLabel();
		hostname = new JTextField();
		portModel = new SpinnerNumberModel(Defaults.getInstance().getPort(), 1025, 65535, 1);
		port = new JSpinner(portModel);
		
		//Connect button
		okButton = new JButton();
		
		//Exit button
		cancelButton = new JButton();

		//======== this ========
		setTitle("Ninja Boggle");
		setBackground(Color.black);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setForeground(Color.white);
		setResizable(false);
		setAlwaysOnTop(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setForeground(Color.white);
			dialogPane.setBackground(Color.black);					
			dialogPane.setLayout(new BorderLayout());
			
			//======== contentPanel ========
			{
				contentPanel.setBackground(Color.black);
				contentPanel.setForeground(Color.white);
				contentPanel.setLayout(new GridBagLayout());
				((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 255, 0, 0};
				((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {30, 20, 20, 30};
				((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
				((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
				
				
				
				//---- bogglePic ----
				bogglePic.setIcon(new ImageIcon(getClass().getResource("images/titlescreen.jpg")));
				bogglePic.setHorizontalAlignment(SwingConstants.CENTER);
				bogglePic.setForeground(Color.white);
				bogglePic.setBackground(Color.black);
				contentPanel.add(bogglePic, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				
//				---- login prompt  ----
				loginLabel.setText("Enter the name and port of your desired dojo."); //Ask user for host
				loginLabel.setForeground(Color.white);
				loginLabel.setBackground(Color.black);
				loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
				loginLabel.setVerticalAlignment(SwingConstants.BOTTOM);
				loginLabel.setFont(new Font("Verdana", Font.BOLD, 11));
				contentPanel.add(loginLabel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				
				//---- hostname ----
				hostname.setText(Defaults.getInstance().getHostname());
				hostname.setBackground(Color.darkGray);
				hostname.setForeground(Color.white);
				contentPanel.add(hostname, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				
				contentPanel.setBackground(Color.black);
				port.setBackground(Color.darkGray);
				port.setForeground(Color.white);
				contentPanel.add(port, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				
				//---- question ----
				question.setText("What secret name would you like to use?"); //Ask user for handle
				question.setForeground(Color.white);
				question.setHorizontalAlignment(SwingConstants.CENTER);
				question.setVerticalAlignment(SwingConstants.BOTTOM);
				question.setFont(new Font("Verdana", Font.BOLD, 11));
				contentPanel.add(question, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				
				//---- usernameTextfield ----
				usernameTextfield.setBackground(Color.darkGray);
				usernameTextfield.setForeground(Color.white);
				contentPanel.add(usernameTextfield, new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
			}
			dialogPane.add(contentPanel, BorderLayout.NORTH);
			
			//======== button bar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setForeground(Color.white);
				buttonBar.setBackground(Color.black);
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};
				
				//---- ok button ----
				okButton.setText("OK");
				okButton.setBackground(Color.black);
				okButton.setForeground(Color.white);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				
				//---- cancelButton ----
				cancelButton.setText("Cancel");
				cancelButton.setBackground(Color.black);
				cancelButton.setForeground(Color.white);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		System.out.println("Finished building gameLogin interface.");
	}

	protected void okButtonActionPerformed(ActionEvent e) {
		//Get player name
		playerName = usernameTextfield.getText();
		
		if (playerName.length() < 2) {
			// The user needs a longer damn name!
			JOptionPane.showMessageDialog(null,
			    "Please enter a longer name",
			    "Your name sucks",
			    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		//Get instance of singleton
		Client client = Client.getInstance();
		
		boolean result = client.makeConnection(hostname.getText(), portModel.getNumber().intValue());
		
		if (result == true) {
			// We connected! Tell the server who we are...
			DataPacket pkt = new PlayerConnect(playerName);
			
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
		Client client = Client.getInstance();
		try{
			client.getConnection().disconnect();
		}catch(Exception e2){
			System.out.println("IO Error on Disconnect");			
		}
		System.out.println("Disconnecting...");
		System.exit(0);
	}

}