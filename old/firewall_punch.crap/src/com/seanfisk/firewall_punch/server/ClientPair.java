package com.seanfisk.firewall_punch.server;

// Thread which handles and sets up the client pair on the server
public class ClientPair extends Thread
{
	public ClientPair(Client c1, Client c2)
	{
		c1.setPartner(c2);
	}
}
