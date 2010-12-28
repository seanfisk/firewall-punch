package com.seanfisk.firewall_punch.client;

public class PeerReceive implements Runnable
{
	private PeerConnection peer;
	
	public PeerReceive(PeerConnection peer)
	{
		this.peer=peer;
	}

	public void run()
	{
		System.out.println("Spawned new PeerReceive thread to receive messages from the peer.");
		while(true)
		{
			System.out.print("Peer says: "+peer.receiveMsg()+"\n> ");
		}
	}
}
