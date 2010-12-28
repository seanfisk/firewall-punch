package com.seanfisk.firewall_punch.server;

public class ClientPair extends Thread
{
	public ClientPair(Client c1, Client c2)
	{
		c1.setPartner(c2);
	}
}
