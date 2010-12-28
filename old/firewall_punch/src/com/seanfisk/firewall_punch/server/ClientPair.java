package com.seanfisk.firewall_punch.server;

// Thread which handles and sets up the client pair on the server
public class ClientPair extends Thread
{
	public ClientPair(ClientConnection c1, ClientConnection c2)
	{
		System.out.println("New ClientPair thread spawned: "+this);
		c1.setPartner(c2);
		c2.setPartner(c1);
		System.out.println("ClientPair thread exiting: "+this);
	}
}
