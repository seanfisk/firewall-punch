package com.seanfisk.firewall_punch.server;

// Thread which handles and sets up the client pair on the server
public class ClientPair implements Runnable
{
	private ClientConnection c1, c2;
	
	public ClientPair(ClientConnection c1, ClientConnection c2)
	{
		this.c1=c1;
		this.c2=c2;
	}
		
	public void run()
	{
		System.out.println("New ClientPair thread("+this+") spawned for "+c1+" and "+c2+'.');
		c1.sendMsg("Partner found: "+c2);
		c2.sendMsg("Partner found: "+c1);
		c1.setPartner(c2);
		c2.setPartner(c1);
		c1.close();
		c2.close();
		System.out.println("ClientPair thread exiting: "+this);
	}
}
