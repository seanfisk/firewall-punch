package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class PeerConnection
{
	private InetSocketAddress addr;
	public DatagramSocket sock;
	
	public void setAddr(InetSocketAddress addr)
	{
		this.addr=addr;
	}

	public void punch() throws IOException
	{
		System.out.println("Attempting to punch peer's firewall...");
		sock=new DatagramSocket(5000);
		sock.connect(addr);
		sock.send(new DatagramPacket(new byte[0],0,addr));
	}
	
	public String toString()
	{
		return addr.toString();
	}
}
