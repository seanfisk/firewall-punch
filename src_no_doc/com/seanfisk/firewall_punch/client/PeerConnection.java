package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class PeerConnection
{
	private InetSocketAddress addr;
	private DatagramSocket sock;
	
	public void bind()
	{
		try
		{
			sock=new DatagramSocket();
		}
		catch(SocketException e)
		{
			System.err.println("Couldn't bind UDP socket to localhost.");
			e.printStackTrace();
		}
	}
	
	public void setAddr(InetSocketAddress addr)
	{
		this.addr=addr;
	}
	
	public int getLocalPort()
	{
		return sock.getLocalPort();
	}

	public void punch()
	{
		System.out.println("Attempting to punch peer's firewall...");
		sendMsg("[This is the firewall punch packet.  If you get it, your firewall did not need to be punched.]");
	}
	
	public void sendMsg(String msg)
	{
		byte[] buf=msg.getBytes();
		try
		{
			sock.send(new DatagramPacket(buf,buf.length,addr));
		}
		catch(IOException e)
		{
			System.err.println("Message sending to "+this+" failed.");
			e.printStackTrace();
		}
	}
	
	public String receiveMsg() throws IOException
	{
		byte[] buf=new byte[256];
		DatagramPacket packet=new DatagramPacket(buf,buf.length);
		sock.receive(packet);
		return new String(packet.getData(),0,packet.getLength());
	}
	
	public void close()
	{
		sock.close();
	}
	
	public String toString()
	{
		return addr.toString();
	}
}
