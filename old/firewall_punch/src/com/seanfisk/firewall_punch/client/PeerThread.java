package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.DatagramPacket;

public class PeerThread extends Thread
{
	public PeerThread(PeerConnection peer)
	{
		System.out.println("New PeerThread spawned.");
		try
		{
			peer.punch();
		}
		catch(IOException e)
		{
			System.err.println("Peer firewall punch failed.");
			e.printStackTrace();
			return;
		}
		try
		{
			peer.sock.receive(new DatagramPacket(null,0));
		}
		catch(Exception e)
		{
			System.err.println("UDP receive failed.");
		}
		System.out.println("This firewall has been punched.");
	}
}
