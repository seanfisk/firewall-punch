package com.seanfisk.firewall_punch.client;

import java.io.IOException;

/**
 * Thread which handles reception of messages from the peer.
 * 
 * @author Sean Fisk
 * @version 1.1
 */
public class PeerReceive implements Runnable
{
	private PeerConnection peer;

	public PeerReceive(PeerConnection peer)
	{
		this.peer = peer;
	}

	public void run()
	{
		System.out
				.print("> Spawned new PeerReceive thread to receive messages from the peer.\n> ");
		try
		{
			while (true)
			{
				System.out.print("Peer says: " + peer.receiveMsg() + "\n> ");
			}
		}
		catch (IOException e)
		{
			System.out
					.println("Connection to peer closed.  Peer receive thread exiting...");
		}
	}
}
